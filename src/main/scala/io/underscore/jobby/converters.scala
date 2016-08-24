package io.underscore.jobby

import scala.util.{ Success, Failure, Try }
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.Instant

import shapeless._

/*

We need to be able to read single String cells from the spreadsheet
as particular types, such as Instant.
This is represented as the StringReader which is String => Try[T]

We also want to be able to convert a List of String cell values
from the spreadhseet into case classes.  This is the role
of the Converter which is type List[String] => List[Try[T]].

The result of a converter is flattened out into a Try[P] (for some case
class, P) by Read.as.

*/

trait StringReader[T] {
  def read(s: String): Try[T]
}

object USDateReader {
  private val usDate = DateTimeFormatter
    .ofPattern("M/d/yyyy H:mm:ss")
    .withZone(ZoneId of "UTC")

  implicit val instantReader = new StringReader[Instant] {
    def read(str: String): Try[Instant] = Try(Instant.from(usDate parse str))
  }
}

trait Readers {

  implicit val stringReader = new StringReader[String] {
    def read(str: String): Try[String] = Success(str)
  }

  implicit val intReader = new StringReader[Int] {
    def read(str: String): Try[Int] = Try(Integer parseInt str)
  }

  implicit val workReader = new StringReader[RemoteWork] {
    def read(str: String): Try[RemoteWork] = str match {
      case "Yes"     => Success(Remote)
      case "Partial" => Success(PartialRemote)
      case "No"      => Success(OnSite)
      case x         => Failure(new Exception(s"$x is not valid RemoteWork"))
    }
  }

  implicit val routeReader = new StringReader[ApplicationRoute] {
    def read(s: String): Try[ApplicationRoute] = Try(
      if (s contains "@") ApplicationEmail(s) else ApplicationURL(s)
    )
  }
}

trait Converter[T <: HList] {
  def convert(values: List[String]): Try[T]
}

trait Converters {

  implicit val c0 = new Converter[HNil] {
    def convert(values: List[String]) = values match {
      case Nil => Success(HNil)
      case xs  => Failure(new Exception(s"Too many runtime values at: ${xs.head}"))
    }
  }

  implicit def c1[H, T <: HList](implicit reader: StringReader[H], converter: Converter[T]) =
    new Converter[H :: T] {
      def convert(values: List[String]) =
        if (values.isEmpty) Failure(new Exception(s"Not enough runtime values"))
        else for {
          h <- reader.read(values.head)
          t <- converter.convert(values.tail)
          } yield h :: t
    }

  trait ProductConverter[P] {
    def read(row: List[String]): Try[P]
  }

  implicit def deriveAs[P <: Product, H <: HList]
    (implicit gen: Generic.Aux[P,H], converter: Converter[H]) = new ProductConverter[P] {
      def read(row: List[String]) = converter.convert(row).map(gen.from)
  }
}

object Read extends Converters with Readers {

  def as[P](row: List[String])(implicit converter: ProductConverter[P]): Try[P] =
    converter.read(row)

}

