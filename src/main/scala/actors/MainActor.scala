package actors

import akka.actor.{Actor, ActorLogging, Props}
import message.{AllFileCounts, FileLineCount, StartReading, StopReading}

class MainActor extends Actor with ActorLogging{
  def receive: Receive = {
    case StartReading =>
      val files = List(
        "/Users/mayanksinghrana/Downloads/Compliance/ManagedOrderGuideItemTypes.csv",
        "/Users/mayanksinghrana/Downloads/Compliance/ManagedOrderGuides.csv",
        "/Users/mayanksinghrana/Downloads/Compliance/PreferredManufacturers.csv",
        "/Users/mayanksinghrana/Downloads/Compliance/ProductLists.csv",
        "/Users/mayanksinghrana/Downloads/Compliance/ManagedOrderGuideTypes.csv",
        "/Users/mayanksinghrana/Downloads/Compliance/PreferredDistributors.csv",
        "/Users/mayanksinghrana/Downloads/Compliance/ProductListMfrItems.csv"
      )
      val supervisor = context.actorOf(Props(new SupervisorActor(self)), "supervisor")
      supervisor ! StartReading(files)

    case AllFileCounts(fileCounts) =>
      fileCounts.foreach { case FileLineCount(filePath, count) =>
        log.info(s"File: $filePath has $count lines.")
      }
      context.system.terminate()

    case StopReading =>
      context.system.terminate()
  }
}
