import actors.MainActor
import akka.actor.{ActorRef, ActorSystem, Props}
import message.StartReading

object FileReadingApp extends App {
  // Create the ActorSystem
  val system: ActorSystem = ActorSystem("FileReaderSystem")

  // Create the MainActor
  val mainActor: ActorRef = system.actorOf(Props[MainActor], "mainActor")

  // Start the file reading process
  mainActor ! StartReading

  // The system will terminate once all files are read and counts are printed

}
