import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.sql.SparkSession
import java.util.Properties
import scala.util.Random
import scala.io

class Producer(subject: Int, spark: SparkSession) extends Thread {

  val properties: Properties = new Properties()
  properties.put("bootstrap.servers", "localhost:9092")
  properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  properties.put("acks", "all")

  val producer = new KafkaProducer[String, String](properties)
  val topic = "motion_insights"
  producer.flush()

  override def run(): Unit = {
    try{
      while(true){
        val activities = scala.util.Random.shuffle(Vector(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
        Thread.sleep(5000)
        for (i <- activities){
          val bufferedSource = io.Source.fromFile(Utils.path_realtime_data + "/subject" + subject + "/activity" + i + ".txt")
          val it = bufferedSource.getLines()
          var lineNumber = 0
          while (it.hasNext) {
            val line = it.next()
            val record = new ProducerRecord[String, String](topic, lineNumber + "", line)
            val metadata = producer.send(record)
            lineNumber = lineNumber + 1
            Thread.sleep(20) // A record is sent every 20 milliseconds
          }
          producer.flush()
          Thread.sleep(5000) // Sleeps 5 seconds before sending the last line
          val endRecord = new ProducerRecord[String, String](topic, lineNumber + "", Utils.endOfActivity)
          val endMetadata = producer.send(endRecord)
          Thread.sleep(5000) // Sleep 5 second at the end of the file
        }
      }
    } catch {
      case e: InterruptedException => {}
      case e: Exception => e.printStackTrace()
    } finally {
      producer.flush()
      producer.close()
    }
  }

}
