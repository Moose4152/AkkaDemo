package actors


import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import message.{AllFileCounts, CheckQueue, FileLineCount, ReadFile, StartReading}

import scala.collection.mutable
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

class SupervisorActor(mainActor:ActorRef)  extends Actor with ActorLogging{
  implicit val timeout: Timeout = Timeout(5.seconds)
  import context.dispatcher

  private val maxReaders = 5
  private var activeReaders = 0
  private val fileQueue: mutable.Queue[String] = mutable.Queue.empty[String]
  private var fileCounts: List[FileLineCount] = List.empty

  def receive: Receive = {
    case StartReading(files) =>
      files.foreach(x => fileQueue.append(x))
      self ! CheckQueue

    case CheckQueue =>
      while (activeReaders < maxReaders && fileQueue.nonEmpty) {
        val filePath = fileQueue.dequeue()
        val reader = context.actorOf(Props[ReaderActor], s"reader-${filePath.hashCode}")
        activeReaders += 1

        (reader ? ReadFile(filePath)).mapTo[FileLineCount].onComplete {
          case Success(fileCount) => self ! fileCount
          case Failure(exception) => log.error(s"Failed to read file: ${exception.getMessage}")
            self ! CheckQueue
        }
      }

    case FileLineCount(filePath, lineCount) =>
      activeReaders -= 1
      fileCounts ::= FileLineCount(filePath, lineCount)

      // Check if more files need to be read
      if (fileQueue.nonEmpty || activeReaders > 0) {
        self ! CheckQueue
      } else {
        mainActor ! AllFileCounts(fileCounts)
      }
  }

}
