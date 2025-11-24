package alpakka

import akka.NotUsed
import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Sink, Source, ZipN}
import akka.stream.{ActorMaterializer, ClosedShape, Graph, Materializer}
import com.typesafe.config.ConfigFactory
import akka.kafka.scaladsl.Consumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.{Level, Logger}

import scala.collection.Seq
import scala.concurrent.{ExecutionContextExecutor, Future}

object ConsumerApp {
  implicit val system = ActorSystem("fusion")
  implicit val materializer = ActorMaterializer()

  implicit val logger = LoggerFactory.getLogger(getClass)


  val graph =
    GraphDSL.create(){ implicit builder: GraphDSL.Builder[NotUsed] =>
      import GraphDSL.Implicits._

      val input = builder.add(KafkaSource.input)

      val multiplier10 = builder.add(Flow[Int].map(x=>x*10))
      val multiplier2 = builder.add(Flow[Int].map(x=>x*2))
      val multiplier3 = builder.add(Flow[Int].map(x=>x*3))

      val output = builder.add(Sink.foreach(println))

      val broadcast = builder.add(Broadcast[Int](3))

      val zip = builder.add(ZipN[Int](3))

      val appender = builder.add(Flow[Seq[Int]].map(x => x.sum))


      //3
      input ~> broadcast

      broadcast.out(0) ~> multiplier10 ~> zip.in(0)
      broadcast.out(1) ~> multiplier2 ~> zip.in(1)
      broadcast.out(2) ~> multiplier3 ~> zip.in(2)

      zip ~> appender.in

      appender.out ~> output

      //4
      ClosedShape
    }


  def main(args: Array[String]) : Unit ={
    println("Start ConsumerApp")
    RunnableGraph.fromGraph(graph).run()
  }
}

object KafkaSource  {
      implicit val system: ActorSystem = ActorSystem("consumer-sys")
      implicit val mat: Materializer = ActorMaterializer()
      implicit val ec: ExecutionContextExecutor = system.dispatcher
      LoggerFactory
        .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)
        .asInstanceOf[Logger]
        .setLevel(Level.ERROR)

      val config = ConfigFactory.load()
      val consumerConfig = config.getConfig("akka.kafka.consumer")
      val consumerSettings = ConsumerSettings(consumerConfig, new StringDeserializer, new StringDeserializer)


      val input = Consumer
        .plainSource(consumerSettings, Subscriptions.topics("test"))
        .map(consumerRecord => consumerRecord.value().toInt)
}
