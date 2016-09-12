package io.underscore.jobby

import org.scalatest._
import scala.util.{ Try, Success, Failure }
import java.time.Instant
import shapeless._

class ParsingSpec extends FlatSpec with Matchers {

  import Read._, USDateReader._

  "Parser" should "parse valid CSV row" in {

    val row: List[String] = """
      |4/16/2015 14:17:34
      |Head o Stuff
      |Anywhere!
      |Yes
      |Junior, Senior
      |jobs@example.org
      |Here at Stuff Co we're passionate about Stuff.
      |Your role will include "doing stuff".
      |boss@example.org
      |Please subscribe me to the Underscore Newsletter
      |Stuff Co""".stripMargin.trim.split("\n").toList

   Read.as[Job](row) should be(
     Success(
       Job(
         Instant.parse("2015-04-16T14:17:34.00Z"),
         "Head o Stuff",
         "Anywhere!",
         Remote,
         "Junior, Senior",
         ApplicationEmail("jobs@example.org"),
         "Here at Stuff Co we're passionate about Stuff.",
         "Your role will include \"doing stuff\".",
         "boss@example.org",
         "Please subscribe me to the Underscore Newsletter",
         "Stuff Co")
         )
       )
  }

  it should "fail on invalid CSV rows" in {
    Read.as[Job](List("wibble")) shouldBe a [Failure[_]]
  }

}
