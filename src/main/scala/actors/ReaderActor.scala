package actors

import akka.actor.{Actor, ActorLogging, PoisonPill}
import message.{FileLineCount, ReadFile}

import scala.io.Source

class ReaderActor extends Actor with ActorLogging {
  override def receive: Receive = {
    case ReadFile(filePath) =>
      log.info(s"Reading file: $filePath")
      val lineCount = Source.fromFile(filePath).getLines().size
      sender() ! FileLineCount(filePath, lineCount)
      self ! PoisonPill
  }
}
