package io.underscore.jobby

import org.scalatest._
import scala.util.{ Success, Failure }
import java.time.Instant

class ParsingSpec extends FlatSpec with Matchers {

  import purecsv.safe._
  import Converters._

  "Parser" should "parse valid CSV row" in {

    val row = """
      |4/16/2015 14:17:34
      |Head o Stuff
      |Anywhere!
      |Yes
      |"Junior, Senior"
      |jobs@example.org
      |Here at Stuff Co we're passionate about Stuff.
      |Your role will include "doing stuff".
      |boss@example.org
      |Please subscribe me to the Underscore Newsletter
      |Stuff Co""".stripMargin.trim.replaceAll("\n",",")

   CSVReader[Job].readCSVFromString(row) should be(
     List(
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
     )
  }

  it should "fail on invalid CSV rows" in {
    CSVReader[Job].readCSVFromString("wibble").head shouldBe a [Failure[_]]
  }

}
