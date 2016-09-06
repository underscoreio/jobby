package io.underscore.jobby

import java.util.Arrays
import java.util.{List => JList}

import scala.collection.JavaConverters._
import scala.util.{Try,Success,Failure}

case class Talk(
  status         : String,
  number         : Int,
  title          : String,
  summary        : String,
  talkType       : String,
  audienceLevel  : String,
  author         : String,
  classification : String,
  location       : String,
  email          : String,
  twitter        : String,
  plus           : String,
  phone          : String,
  organization   : String,
  locationAgain  : String,
  bio            : String
)


object ScalaxMain {

  import Read._

  def main(args: Array[String]): Unit =
    fetch("1pI-BYpRlT1UiUMukyUytwdAv7uPFZHR147jLtN8hGDs", "CFPs!A2:P") match {
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
      |title = "${talk.number}. ${talk.status} ${talk.title}"
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
      |${talk.organization}
      |
      |${talk.bio}
      |
      |""".stripMargin.trim

      val w = Files.newBufferedWriter(path, Charset.forName("UTF-8"))
      try w.write(content) finally w.close
      path
  }

}
