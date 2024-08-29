import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.StreamingContext

class Consumer(subject: Int, spark: SparkSession, ssc: StreamingContext) extends Thread {

  val kafkaParams = Map[String, Object](
    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
    ConsumerConfig.GROUP_ID_CONFIG -> "test",
    ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
    ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest",
    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> (true: java.lang.Boolean),
    ConsumerConfig.MAX_POLL_RECORDS_CONFIG -> (120: java.lang.Integer)
  )
  val topic = "motion_insights"

  val activityClassifier = new ActivityClassifier(Utils.path_training_data+"/subject"+subject+"/dataset.csv",spark)
  println("Activity classifier created for subject"+subject)
  var df: DataFrame = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], Utils.data_schema)
  val emptyRow = Row.fromSeq(Seq.fill(Utils.statistics_schema.length - 1)(null) :+ subject)
  var statisticsDf: DataFrame = spark.createDataFrame(spark.sparkContext.parallelize(Seq(emptyRow)), Utils.statistics_schema)
  statisticsDf.show()
  var sessionHistory: DataFrame = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], Utils.session_history_schema)

  override def run(): Unit = {

    var firstBatch = true

    val kafkaStream = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Seq(topic), kafkaParams)
    )

    kafkaStream.foreachRDD { rdd =>
      val newRdd = rdd.map(line => Utils.createRow(line.value.split(","), Utils.data_schema))
      val newDf = spark.createDataFrame(newRdd, Utils.data_schema)
      if(!newDf.isEmpty){ // Data is being received
        // Take the last tuple
        val last_tuple = newDf.tail(1)(0).toString().replaceAll("[\\[\\]]", "")
        // If it's empty, then the activity is finished, so add the statistics to the session history,
        // empty the dataframes and prepare to receive new data
        if(last_tuple.equals(Utils.endOfActivity)){
          updateSessionHistory()
          println("Session history")
          sessionHistory.show(Integer.MAX_VALUE,false)
          df = spark.createDataFrame(spark.sparkContext.emptyRDD[Row],Utils.data_schema)
          statisticsDf = spark.createDataFrame(spark.sparkContext.parallelize(Seq(emptyRow)),Utils.statistics_schema)
        }
        else{
          if(firstBatch){
            Main.isStreaming = true
            firstBatch = false
          }
          else{
            df = df.union(newDf)
            // Take ECG column from last batch and transform it into a string whose values are separated by a comma
            val last_ecg_column = Statistics.generic_query(newDf,"ecg_1")
            // Take the activity
            val activity = newDf.first().getDouble(23)
            // Predictions
            val pred_activity = activityClassifier.predict(newDf)
            val predictions = activityClassifier.predictions(newDf)
            val activity_description = Utils.activity_description.apply(activity.toInt)
            // Statistics
            val percAbnormalPeaks = Statistics.abnormalPeaks(df)
            val cardiacFrequency = Statistics.cardiacFrequency(df, subject, activity.toInt)
            val accelerations = Statistics.batch_accelerations(df)
            // Create a string with the statistics separated by a semicolon
            val statistics = percAbnormalPeaks + ";" + cardiacFrequency + ";" + accelerations + ";" + activity + ";" + pred_activity + ";" + predictions + ";" + activity_description + ";" + last_tuple + ";" + last_ecg_column + ";" + subject
            val newRowStatistics = Utils.createRow(statistics.split(";"),Utils.statistics_schema)
            val newStatisticsDf = spark.createDataFrame(spark.sparkContext.parallelize(Seq(newRowStatistics)),Utils.statistics_schema)
            newStatisticsDf.show(Integer.MAX_VALUE,false)
            // Add statistics to the statistics dataframe
            statisticsDf = statisticsDf.union(newStatisticsDf)
          }
        }
      }
    }
    ssc.start()
    ssc.awaitTermination()

  }

  def updateSessionHistory(): Unit = {
    if(!statisticsDf.isEmpty){
      val record_session_history = Statistics.createSessionHistoryRecord(statisticsDf)
      val row_record_session_history = Utils.createRow(record_session_history.split(","), Utils.session_history_schema)
      val df_record_session_history = spark.createDataFrame(spark.sparkContext.parallelize(Seq(row_record_session_history)),Utils.session_history_schema)
      sessionHistory = sessionHistory.union(df_record_session_history)
    }
  }

}
