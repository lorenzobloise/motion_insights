import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Milliseconds, StreamingContext}

object Main {

  implicit var isStreaming: Boolean = false
  implicit var consumer: Consumer = null
  implicit var producer: Producer = null
  val spark = SparkSession
    .builder
    .master("local[*]")
    .appName("motion_insights")
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")
  var ssc = Option.empty[StreamingContext]
  var generalHistory = new GeneralHistory(spark)
  generalHistory.load_history_data()
  println("READY FOR STREAMING")

  def main(args: Array[String]): Unit = {
    val httpServer = new HTTPServer()
  }

  def startSession(subject: Int): Unit = {
    ssc = Option(new StreamingContext(spark.sparkContext, Milliseconds(Utils.batch_interval)))
    ssc.get.sparkContext.setLogLevel("ERROR")
    consumer = new Consumer(subject, spark, ssc.get)
    producer = new Producer(subject, spark)
    consumer.start()
    producer.start()
  }

  def finishSession(): Unit = {
    producer.interrupt()
    consumer.updateSessionHistory()
    println("Storico sessione")
    consumer.sessionHistory.show(Integer.MAX_VALUE, false)
    generalHistory.df = generalHistory.df.union(consumer.sessionHistory)
    generalHistory.updateStatistics()
    ssc.get.stop(false, false)
    ssc.get.awaitTermination()
    ssc = Option.empty[StreamingContext]
    consumer.interrupt()
    isStreaming = false
    println("General history")
    generalHistory.df.show(Integer.MAX_VALUE, false)
  }

}
