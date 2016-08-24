package io.underscore.jobby

import java.util.Arrays
import java.util.{List => JList}

import scala.collection.JavaConverters._
import scala.util.{Try,Success,Failure}

case class Talk(
  number         : Int,
  timestamp      : java.time.Instant,
  title          : String,
  summary        : String,
  talkType       : String,
  audienceLevel  : String,
  author         : String,
  email          : String,
  twitter        : String,
  plus           : String,
  phone          : String,
  organization   : String,
  location       : String,
  bio            : String,
  assitance      : String,
  notes          : String,
  classification : String
)


object ScalaxMain {

  import java.time.format.DateTimeFormatter
  import java.time.ZoneId
  import java.time.Instant
  import Read._

  private val ukDate = DateTimeFormatter
    .ofPattern("d/M/yyyy H:mm:ss")
    .withZone(ZoneId of "UTC")

  implicit val instantReader = new StringReader[Instant] {
    def read(str: String): Try[Instant] = Try(Instant.from(ukDate parse str))
  }

  def main(args: Array[String]): Unit =
    fetch("1pI-BYpRlT1UiUMukyUytwdAv7uPFZHR147jLtN8hGDs", "CFPs!A2:Q") match {
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
    val path = FileSystems.getDefault().getPath("/Users/richard/talks-92/scalax/content/post", name)
    val content = s"""
      |+++
      |date = "${talk.timestamp.toString}"
      |title = "${talk.number}. ${talk.title}"
      |draft = false
      |+++
      |
      |- ${talk.talkType}
      |- ${talk.classification}
      |- ${talk.audienceLevel}
      |
      |${talk.summary}
      |
      |---
      |
      |${talk.author}
      |
      |${talk.bio}
      |
      |""".stripMargin.trim

      val w = Files.newBufferedWriter(path, Charset.forName("UTF-8"))
      try w.write(content) finally w.close
      path
  }

}
