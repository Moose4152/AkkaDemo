package message


case class ReadFile(filePath:String)
case class FileLineCount(filePath:String,lineCount:Int)
case class AllFileCounts(fileCounts:List[FileLineCount])
case class StartReading(files:Seq[String])
case object StopReading
case object CheckQueue