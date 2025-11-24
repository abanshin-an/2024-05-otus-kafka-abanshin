package alpakka

import akka.Done
import akka.actor.ActorSystem
import scala.util.{Failure, Success}
import akka.kafka.ProducerSettings
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory
import org.apache.kafka.clients.producer.ProducerRecord
import akka.kafka.scaladsl.Producer
import org.apache.kafka.common.serialization.StringSerializer

import scala.concurrent.Future

object  ProducerApp extends App {
  implicit val system: ActorSystem = ActorSystem("producer")
  implicit val mat: Materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val config = ConfigFactory.load()
  val producerConf = config.getConfig("akka.kafka.producer")

  val producerSettings = ProducerSettings(producerConf, new StringSerializer, new StringSerializer)

  val producer: Future[Done] =
    Source(1 to 5)
      .map(value => new ProducerRecord[String, String]("test", value.toString))
      .runWith(Producer.plainSink(producerSettings))

  producer onComplete {
    case Success(_) => println("Done"); sys.exit(0)
    case Failure(err) => println(err.toString); sys.exit(0)
  }


}
