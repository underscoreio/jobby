package io.underscore.jobby

import scala.collection.JavaConverters._
import scala.util.{Try,Success,Failure}

case class Talk(
  number         : Int,
  timestamp      : String,
  title          : String,
  talkType       : String,
  summary        : String,
  audienceLevel  : String,
  classification : String,
  author         : String,
)

object Config {
  val sheetId = "1pI-BYpRlT1UiUMukyUytwdAv7uPFZHR147jLtN8hGDs"
  val sheetRange = "CFP!A2:H"
  val outDir = "/Users/richard/tmp/cfp/content/post"
}

/*
  Extracts Scala eXchange CFP submissions from a Google Sheet and writes 
  them as markdown to the file system.
 
  Best used with a static site generator, such as hugo.
 
  The `Talk` class above defines the columns of interest in the sheet.
  The `sheetId` and `sheetRange` must correspond to the CFP
  sheet and the range of columns to match against the `Talk` case class.
 
  To use wth hugo:
 
 
  1, Install Hugo
  
  ` brew install hugo` (or similar)
 
  2. Create a site and give it a theme
  ```
  cd tmp
  hugo new site cfp
  cd cfp
  mkdir content/post
  git init 
  git submodule add https://github.com/budparr/gohugo-theme-ananke.git themes/ananke
  echo 'theme = "ananke"' >> config.toml
  ``

  2. Serve content
  - hugo server
 */
object ScalaxMain {

  import Read._

  def main(args: Array[String]): Unit =
    fetch(Config.sheetId, Config.sheetRange) match {
      case Failure(err)   => err.printStackTrace()
      case Success(range) =>
        val talks: List[Try[Talk]] = asScala(range).map(Read.as[Talk])
        talks.map(_.flatMap(write)).foreach( x => println(x))
    }

  import com.google.api.services.sheets.v4.model._

  def fetch(spreadsheetId: String, range: String): Try[ValueRange] =
    GoogleAuth().authorize.flatMap(Service.sheets).flatMap { service =>
      Try { service.spreadsheets().values().get(spreadsheetId, range).execute() }
    }

  def asScala(range: ValueRange): List[List[String]] =
    range.getValues().asScala.toList.map(row => row.asScala.toList.map(_.toString))

  import java.nio.file.{FileSystems, Path, Files}
  import java.nio.charset.Charset

  def write(talk: Talk): Try[Path] = Try {
    val name = s"${talk.number}.md"
    val path = FileSystems.getDefault().getPath(Config.outDir, name)
    val content = s"""
      |+++
      |title = "${talk.number}. ${talk.title.trim}"
      |draft = false
      |+++
      |
      |- ${talk.talkType}
      |- ${talk.classification}
      |- ${talk.audienceLevel}
      |
      |${talk.summary}
      |
      |[Next>](../${talk.number + 1})
      |---
      |
      |${talk.author}
      |
      |""".stripMargin.trim

      val w = Files.newBufferedWriter(path, Charset.forName("UTF-8"))
      try w.write(content) finally w.close
      path
  }

}
