import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import ch.megard.akka.http.cors.scaladsl.CorsDirectives.cors

class HTTPServer {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val route = {
    path("start" / Segment) { subject =>
      cors() {
        get {
          val s = subject.toInt
          Main.startSession(s)
          complete("Start session with subject:" + subject)
        }
      }
    } ~
      path("stop") {
        cors() {
          get {
            Main.finishSession()
            complete("Stop session")
          }
        }
      } ~
      path("isStreaming") {
        cors() {
          get {
            complete(Main.isStreaming.toString)
          }
        }
      } ~
      // General history statistics
      path("num_activities_per_subject") {
        cors() {
          get {
            complete(Statistics.generic_query(Main.generalHistory.infoSubjects, "num_activities"))
          }
        }
      } ~
      path("num_performed_activities") {
        cors() {
          get {
            complete(Statistics.generic_query(Main.generalHistory.infoActivities, "num_performed"))
          }
        }
      } ~
      path("max_acceleration_chest") {
        cors() {
          get {
            complete(Statistics.generic_query(Main.generalHistory.infoSubjects, "max_acceleration_chest"))
          }
        }
      } ~
      path("max_perc_abnormal_peaks") {
        cors() {
          get {
            complete(Statistics.generic_query(Main.generalHistory.infoSubjects, "max_perc_abnormal_peaks"))
          }
        }
      } ~
      // Session history statistics
      path("num_dynamic_and_static_activities") {
        cors() {
          get {
            if (Main.consumer == null)
              complete("")
            else
              complete(Statistics.num_dynamic_and_static_activities(Main.consumer.sessionHistory, -1))
          }
        }
      } ~
      path("avg_bpm_per_activity") {
        cors() {
          get {
            if (Main.consumer == null)
              complete("")
            else
              complete(Statistics.generic_query(Main.consumer.sessionHistory, "avg_cardiacFrequency"))
          }
        }
      } ~
      path("total_num_dynamic_and_static_activities" / Segment) { soggetto =>
        cors() {
          get {
            val subject = soggetto.toInt
            complete(Statistics.num_dynamic_and_static_activities(Main.generalHistory.df, subject))
          }
        }
      } ~
      path("stddev_avg_bpm") {
        cors() {
          get {
            if (Main.consumer == null)
              complete("")
            else
              complete(Statistics.stddev_avg_bpm(Main.consumer.sessionHistory))
          }
        }
      } ~
      // Real time statistics
      path("statistics") {
        cors() {
          get {
            if (Main.consumer == null || Main.consumer.statisticsDf.isEmpty)
              complete("")
            else
              complete(Main.consumer.statisticsDf.tail(1)(0).mkString(";").replaceAll("[\\[\\]]", ""))
          }
        }
      }
  }

  var bindingFuture = Http().bindAndHandle(route, "localhost", 8081)

}
