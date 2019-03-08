package io.underscore.jobby

import org.junit.Test
import org.junit.Assert._
import scala.util.Success

class ConverterSpec {

  import implied Read._

  @Test def `lookup StringReader for an Int` = {
    val reader = the[StringReader[Int]]
    assertEquals(Success(42), reader.read("42"))
  }

  case class Foo(a: Int) derives Read

  @Test def `lookup Reader for Foo(Int)` = {
    val reader = the[Read[Foo]]
    val input = List("42")
    val expected = Foo(42)

  import scala.reflect.Generic
    val ev = the[Generic[Foo]]
    println("\n\n-----\n")
    println(ev.Shape)
    println("\n\n")


    assertEquals(Success(expected), reader.read(input))
  }


  /*
  import java.time.Instant
  def `Converter should exist for (Instant,String)` = {
    val converter = implicitly[Converter[Instant :: String :: HNil]]
    converter.convert(List("12/31/2016 6:00:00", "Hello")).success.value should be(
      Instant.parse("2016-12-31T06:00:00.00Z") :: "Hello" :: HNil
    )
  }

  it should "Fail if there are too many values at runtime" in {
    val converter = implicitly[Converter[String :: String :: HNil]]
    converter.convert(List("x", "y", "z")).failure.exception.getMessage should be(
     "Too many runtime values at: z"
    )
  }

  it should "Fail if there are not enough values at runtime" in {
    val converter = implicitly[Converter[String :: String :: HNil]]
    converter.convert(List("x")).failure.exception.getMessage should be(
      "Not enough runtime values"
    )
  }

  it should "Succeed if there are not enough values at runtime but the remaining fields are optional" in {
    val converter = implicitly[Converter[String :: Option[String] :: HNil]]
    converter.convert(List("x")).success.value should be(
      "x" :: (None:Option[String]) :: HNil
    )
  } */

}
