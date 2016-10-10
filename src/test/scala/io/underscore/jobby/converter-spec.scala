package io.underscore.jobby

import org.scalatest._
import scala.util.{ Success, Failure }
import java.time.Instant
import shapeless.{HNil, ::}

class ConverterSpec extends FlatSpec with Matchers with TryValues {

  import Read._, USDateReader._

  "Converter" should "exist for (Instant,String)" in {
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
  }

}
