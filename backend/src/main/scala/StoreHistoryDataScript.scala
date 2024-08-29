import org.apache.spark.sql.functions.{col, max}
import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession}
import java.io.PrintWriter
import scala.util.Random

object StoreHistoryDataScript extends App {

  val spark = SparkSession
    .builder
    .master("local[*]")
    .appName("store_history_data_script")
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  var df: DataFrame = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], Utils.session_history_schema)

  var infoSubjects: DataFrame = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], Utils.info_subjects_schema)

  var infoActivities: DataFrame = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], Utils.info_activities_schema)

  var max_ecg: Array[Array[Double]] = store_max_ecg() // Store the maximum ECG values of the training data

  var activities_per_subject: Array[Set[Int]] = randomize_activities()

  for(i <- 1 to Utils.numSubjects){
    val activityClassifier = new ActivityClassifier(Utils.path_realtime_data + "/subject" + i + "/dataset.csv", spark)
    for(j <- 0 until activities_per_subject(i-1).size){
      val dfSubjectActivity = spark.read
        .option("inferSchema", "false")
        .schema(Utils.data_schema)
        .option("header", "false")
        .option("delimiter", ",")
        .csv(Utils.path_training_data + "/subject" + i + "/activity" + activities_per_subject(i - 1).toList(j) + ".txt")
      // Statistics of the sessions
      val subject = i
      val activity = activities_per_subject(i - 1).toList(j)
      val dynamic = Utils.dynamic_activities.contains(activity)
      val ratio_correct_pred_activity = Statistics.ratioCorrectPredActivity(dfSubjectActivity, activityClassifier)
      val percAbnormalPeaks = Statistics.abnormalPeaks(dfSubjectActivity)
      val avgCardiacFrequency = cardiacFrequency(dfSubjectActivity, subject, activity)
      val maxAccelerationChest = Statistics.maxAccelerationChest(dfSubjectActivity)
      val line = subject + "," + activity + "," + dynamic + "," + ratio_correct_pred_activity + "," + percAbnormalPeaks + "," + avgCardiacFrequency + "," + maxAccelerationChest
      val row = Utils.createRow(line.split(","), Utils.session_history_schema)
      val newDf = spark.createDataFrame(spark.sparkContext.parallelize(Seq(row)), Utils.session_history_schema)
      df = df.union(newDf)
    }
    println(i * 20 + "%")
  }
  println("History data loading complete")
  df.show(Integer.MAX_VALUE, false)
  populateInfoSubjects()
  populateInfoActivities()

  df.coalesce(1).write
    .mode(SaveMode.Overwrite)
    .format("csv")
    .option("header", "true")
    .save(Utils.path_general_data_history)

  infoSubjects.coalesce(1).write
    .mode(SaveMode.Overwrite)
    .format("csv")
    .option("header", "true")
    .save(Utils.path_info_subjects)

  infoActivities.coalesce(1).write
    .mode(SaveMode.Overwrite)
    .format("csv")
    .option("header", "true")
    .save(Utils.path_info_activities)

  val writer = new PrintWriter(Utils.path_max_ecg)
  max_ecg.foreach(row => writer.println(row.mkString(",")))
  writer.close()

  def randomize_num_activities(): Array[Int] = {
    var v = new Array[Int](Utils.numSubjects)
    for (i <- 1 to Utils.numSubjects)
      v(i - 1) = Random.nextInt(6) + 3 // Random number of activities between 3 and 8
    return v
  }

  def store_max_ecg(): Array[Array[Double]] = {
    val ret: Array[Array[Double]] = Array.ofDim[Double](Utils.numSubjects, Utils.numActivities)
    for (i <- 1 to Utils.numSubjects) {
      for (j <- 1 to Utils.numActivities) {
        val dfECG: DataFrame = spark.read
          .option("inferSchema", "false")
          .schema(Utils.data_schema)
          .option("header", "false")
          .option("delimiter", ",")
          .csv(Utils.path_training_data + "/subject" + i + "/activity" + j + ".txt")
        ret(i - 1)(j - 1) = dfECG.agg(max("ecg_1")).first().getDouble(0)
      }
    }
    return ret
  }

  def randomize_activities(): Array[Set[Int]] = {
    var v = new Array[Set[Int]](Utils.numSubjects)
    val n_activities_per_subject = randomize_num_activities()
    for (i <- 1 to Utils.numSubjects) {
      val n_activities = n_activities_per_subject(i - 1) // Number of activities the subject does
      var set_activities = Set(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11) // Set of activity IDs
      for (j <- 0 until n_activities) {
        val random_activity_to_delete = Random.nextInt(11) + 1
        set_activities = set_activities.filter(x => x != random_activity_to_delete)
      }
      v(i - 1) = set_activities
    }
    return v
  }

  def cardiacFrequency(df: DataFrame, subject: Int, activity: Int): Double = {
    // Take the maximum ECG of this activity
    val min_ecg_activity = max_ecg(subject - 1)(activity - 1)
    // Count the number of R waves and divide it for the sampling time
    val numOndeR = df.filter(col("ecg_1") > Utils.bpm_threshold * min_ecg_activity).count()
    val durataCampionamento = df.count() * Utils.sampling_frequency // Milliseconds
    val ondeRperMs = numOndeR.toDouble / durataCampionamento.toDouble
    // Multiply by 60 seconds
    val frequenzaCardiaca = ondeRperMs * 60000
    return Math.round(frequenzaCardiaca)
  }

  def populateInfoSubjects(): Unit = {
    infoSubjects = df.groupBy("subject").agg(Map(
      "activity" -> "count",
      "max_acceleration_chest" -> "max",
      "percAbnormalPeaks" -> "max"
    )).toDF(Utils.info_subjects_schema.fieldNames: _*)
    println("infoSubjects")
    infoSubjects.show()
  }

  def populateInfoActivities(): Unit = {
    infoActivities = df.groupBy("activity").agg(Map(
      "subject" -> "count",
      "ratio_correct_pred_activity" -> "min"
    )).toDF(Utils.info_activities_schema.fieldNames: _*)
    println("infoActivities")
    infoActivities.show()
  }

}
