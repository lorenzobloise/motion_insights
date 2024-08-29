import org.apache.spark.sql.SparkSession
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.apache.spark.ml.classification.LogisticRegressionSummary

object Benchmark extends App {

  private val spark = SparkSession
    .builder
    .master("local[*]")
    .appName("benchmark")
    .getOrCreate()

  spark.sparkContext.setLogLevel("ERROR")

  val currentDateTime = LocalDateTime.now()
  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  val formattedDateTime = currentDateTime.format(formatter)
  println("BENCHMARK " + formattedDateTime)
  println("Activity classifiers trained with 'training_data', split: 70-30")
  println("Testing done with 'realtime_data'")
  println("Classification algorithm: Logistic Regression")
  println("-----------------------------")

  var activityClassifiers = new Array[ActivityClassifier](Utils.numSubjects)
  for (i <- 1 until Utils.numSubjects + 1) {
    activityClassifiers(i - 1) = new ActivityClassifier(Utils.path_training_data + "/subject" + i + "/dataset.csv", spark)
    println("Created activity classifier for subject" + i)
  }
  println("Done creating activity classifiers")

  val evaluationSummaryValidation = Array.ofDim[LogisticRegressionSummary](Utils.numSubjects)
  var accuraciesValidation = Array.ofDim[Double](Utils.numSubjects)
  var precisionsValidation = Array.ofDim[Double](Utils.numSubjects)
  var recallsValidation = Array.ofDim[Double](Utils.numSubjects)
  var f1scoresValidation = Array.ofDim[Double](Utils.numSubjects)
  val evaluationSummaryBenchmark = Array.ofDim[LogisticRegressionSummary](Utils.numSubjects)
  var accuraciesBenchmark = Array.ofDim[Double](Utils.numSubjects)
  var precisionsBenchmark = Array.ofDim[Double](Utils.numSubjects)
  var recallsBenchmark = Array.ofDim[Double](Utils.numSubjects)
  var f1scoresBenchmark = Array.ofDim[Double](Utils.numSubjects)

  for(subject <- 0 until Utils.numSubjects){
    evaluationSummaryValidation(subject) = activityClassifiers(subject).evaluationSummaryClassification
    val benchmark_data = activityClassifiers(subject).assemble(spark.read
      .option("inferSchema", "false")
      .schema(Utils.data_schema)
      .option("header", "false")
      .csv(Utils.path_realtime_data + "/subject" + (subject + 1) + "/dataset.csv"))
    evaluationSummaryBenchmark(subject) = activityClassifiers(subject).classifier.evaluate(benchmark_data)
    accuraciesValidation(subject) = evaluationSummaryValidation(subject).accuracy
    precisionsValidation(subject) = evaluationSummaryValidation(subject).weightedPrecision
    recallsValidation(subject) = evaluationSummaryValidation(subject).weightedPrecision
    f1scoresValidation(subject) = evaluationSummaryValidation(subject).weightedFMeasure(1)
    printf("Subject" + (subject + 1) + " accuracy (VALIDATION): %.4f\n", accuraciesValidation(subject))
    printf("Subject" + (subject + 1) + " precision (VALIDATION): %.4f\n", precisionsValidation(subject))
    printf("Subject" + (subject + 1) + " recall (VALIDATION): %.4f\n", recallsValidation(subject))
    printf("Subject" + (subject + 1) + " F1 score (VALIDATION): %.4f\n", f1scoresValidation(subject))
    accuraciesBenchmark(subject) = evaluationSummaryBenchmark(subject).accuracy
    precisionsBenchmark(subject) = evaluationSummaryBenchmark(subject).weightedPrecision
    recallsBenchmark(subject) = evaluationSummaryBenchmark(subject).weightedRecall
    f1scoresBenchmark(subject) = evaluationSummaryBenchmark(subject).weightedFMeasure(1)
    printf("Subject" + (subject + 1) + " accuracy (BENCHMARK): %.4f\n", accuraciesBenchmark(subject))
    printf("Subject" + (subject + 1) + " precision (BENCHMARK): %.4f\n", precisionsBenchmark(subject))
    printf("Subject" + (subject + 1) + " recall (BENCHMARK): %.4f\n", recallsBenchmark(subject))
    printf("Subject" + (subject + 1) + " F1 score (BENCHMARK): %.4f\n", f1scoresBenchmark(subject))
  }

  println()
  println("SUMMARY")
  println()

  val (min_accuracy_test, iMin_accuracy_test) = min_and_index(accuraciesValidation)
  printf("Minimum accuracy (VALIDATION): %.4f had with subject%d\n", min_accuracy_test, iMin_accuracy_test)
  printf("Average accuracy (VALIDATION): %.4f\n", average(accuraciesValidation))
  val (min_precision_test, iMin_precision_test) = min_and_index(precisionsValidation)
  printf("Minimum precision (VALIDATION): %.4f had with subject%d\n", min_precision_test, iMin_precision_test)
  printf("Average precision (VALIDATION): %.4f\n", average(precisionsValidation))
  val (min_recall_test, iMin_recall_test) = min_and_index(recallsValidation)
  printf("Minimum recall (VALIDATION): %.4f had with subject%d\n", min_recall_test, iMin_recall_test)
  printf("Average recall (VALIDATION): %.4f\n", average(recallsValidation))
  val (min_f1score_test, iMin_f1score_test) = min_and_index(f1scoresValidation)
  printf("Minimum F1 score (VALIDATION): %.4f had with subject%d\n", min_f1score_test, iMin_f1score_test)
  printf("Average F1 score (VALIDATION): %.4f\n", average(f1scoresValidation))
  val (min_accuracy_benchmark, iMin_accuracy_benchmark) = min_and_index(accuraciesBenchmark)
  printf("Minimum accuracy (BENCHMARK): %.4f had with subject%d\n", min_accuracy_benchmark, iMin_accuracy_benchmark)
  printf("Average accuracy (BENCHMARK): %.4f\n", average(accuraciesBenchmark))
  val (min_precision_benchmark, iMin_precision_benchmark) = min_and_index(precisionsBenchmark)
  printf("Minimum precision (BENCHMARK): %.4f had with subject%d\n", min_precision_benchmark, iMin_precision_benchmark)
  printf("Average precision (BENCHMARK): %.4f\n", average(precisionsBenchmark))
  val (min_recall_benchmark, iMin_recall_benchmark) = min_and_index(recallsBenchmark)
  printf("Minimum recall (BENCHMARK): %.4f had with subject%d\n", min_recall_benchmark, iMin_recall_benchmark)
  printf("Average recall (BENCHMARK): %.4f\n", average(recallsBenchmark))
  val (min_f1score_benchmark, iMin_f1score_benchmark) = min_and_index(f1scoresBenchmark)
  printf("Minimum F1 score (BENCHMARK): %.4f had with subject%d\n", min_f1score_benchmark, iMin_f1score_benchmark)
  printf("Average F1 score (BENCHMARK): %.4f\n", average(f1scoresBenchmark))

  def average(v: Array[Double]): Double = {
    var sum: Double = 0
    for (x <- v)
      sum += x
    return sum / v.length
  }

  def min_and_index(v: Array[Double]): (Double, Int) = {
    var iMin = 0
    var min = v(0)
    for (i <- 0 until v.length)
      if (v(i) < min) {
        min = v(i)
        iMin = i
      }
    return (min, iMin)
  }

}
