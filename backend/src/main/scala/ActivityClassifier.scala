import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{col, desc, format_string}

/**
 *
 * @param path: path of the entire training data (.csv)
 */
class ActivityClassifier(path: String,spark: SparkSession) {

  private var data = spark.read
    .option("inferSchema", "false")
    .schema(Utils.data_schema)
    .option("header", "false")
    .csv(path)

  private val featureColumns = data.columns.slice(0, 23)

  private val assembler = new VectorAssembler()
    .setInputCols(featureColumns)
    .setOutputCol("features")

  private val transformedData = assembler.transform(data)

  private val Array(training, test) = transformedData.randomSplit(Array(0.7, 0.3), seed = 11L)

  private val logisticRegressionClassifier = new LogisticRegression()
    .setFeaturesCol("features")
    .setLabelCol("activity")

  val classifier = logisticRegressionClassifier.fit(training)

  val evaluationSummaryClassification = classifier.evaluate(test)

  def assemble(df: DataFrame): DataFrame = {
    val featureColumns = df.columns.slice(0, 23)
    val assembler = new VectorAssembler()
      .setInputCols(featureColumns)
      .setOutputCol("features")
    return assembler.transform(df)
  }

  def predict(df: DataFrame): Double = {
    val df2 = assemble(df)
    val pred_activity = classifier.transform(df2)
    val counts = pred_activity.groupBy("prediction").count().orderBy("count")
    val most_frequent_pred_activity = counts.tail(1)(0).getDouble(0)
    return most_frequent_pred_activity
  }

  def predictions(df: DataFrame): String = {
    val df2 = assemble(df)
    val pred_activity = classifier.transform(df2)
    val counts = pred_activity.groupBy("prediction").count().orderBy("count")
    val percs = counts.withColumn("perc", (col("count") / df.count().toDouble) * 100)
    val predictions = percs.withColumn("predictions", format_string("%.0f: %.2f", col("prediction"), col("perc"))).orderBy(desc("perc"))
    return predictions.select("predictions").collect().map(_.getString(0)).mkString("$")
  }

}
