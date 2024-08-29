import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import java.io.{BufferedReader, FileReader}

class GeneralHistory(spark: SparkSession) {

  var df: DataFrame = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], Utils.session_history_schema)

  var infoSubjects: DataFrame = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], Utils.info_subjects_schema)

  var infoActivities: DataFrame = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], Utils.info_activities_schema)

  var max_ecg: Array[Array[Double]] = Array.ofDim[Double](Utils.numSubjects, Utils.numActivities)

  def load_history_data(): Unit = {
    df = spark.read
      .format("csv")
      .option("inferSchema", "false")
      .schema(Utils.session_history_schema)
      .option("header", "true")
      .load(Utils.path_general_data_history)
    infoSubjects = spark.read
      .format("csv")
      .option("inferSchema", "false")
      .schema(Utils.info_subjects_schema)
      .option("header", "true")
      .load(Utils.path_info_subjects)
      .orderBy("subject")
    infoActivities = spark.read
      .format("csv")
      .option("inferSchema", "false")
      .schema(Utils.info_activities_schema)
      .option("header", "true")
      .load(Utils.path_info_activities)
      .orderBy("activity")
    val reader = new BufferedReader(new FileReader(Utils.path_max_ecg))
    val lines: List[String] = Iterator.continually(reader.readLine()).takeWhile(_ != null).toList
    reader.close()
    lines.zipWithIndex.foreach { case (line, rowIndex) =>
      val rowValues: Array[Double] = line.split(",").map(_.toDouble)
      max_ecg(rowIndex) = rowValues
    }
    println("Loaded history data")
    df.show(Integer.MAX_VALUE, false)
    infoSubjects.show(Integer.MAX_VALUE, false)
    infoActivities.show(Integer.MAX_VALUE, false)
    max_ecg.foreach(row => println(row.mkString(", ")))
  }

  def updateStatistics(): Unit = {
    updateInfoSubjects()
    updateInfoActivities()
  }

  def updateInfoSubjects(): Unit = {
    infoSubjects = df.groupBy("subject").agg(Map(
      "activity" -> "count",
      "max_acceleration_chest" -> "max",
      "percAbnormalPeaks" -> "max"
    )).toDF(Utils.info_subjects_schema.fieldNames: _*)
    println("infoSubjects")
    infoSubjects.show()
  }

  def updateInfoActivities(): Unit = {
    infoActivities = df.groupBy("activity").agg(Map(
      "subject" -> "count",
      "ratio_correct_pred_activity" -> "min"
    )).toDF(Utils.info_activities_schema.fieldNames: _*)
    println("infoActivities")
    infoActivities.show()
  }

}
