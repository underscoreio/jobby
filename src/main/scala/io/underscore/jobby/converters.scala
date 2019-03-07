package io.underscore.jobby

import scala.util.{ Success, Failure, Try }
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.Instant

/*

We need to be able to read single String cells from the spreadsheet
as particular types, such as Instant.
This is represented as the StringReader which is String => Try[T]

We also want to be able to convert a List of String cell values
from the spreadsheet into case classes.  This is the role
of the Converter which is type List[String] => List[Try[T]].

The result of a converter is flattened out into a Try[P] (for some case
class, P) by Read.as.

## Optional cells

As the spreadsheet evolves by adding rows, we have a situation where...

1. older rows will not have the new column.
That is, we have rows returned will different lengths.

2. for optional answers added to the spreadhsheet,
if the answer is not given there is no cell returned by Google.
Not even a blank value.
Again, we have rows of different lengths.

To handle these cases we support Option[T] values in the case class.
If we "run out of values" when reading a row, but the cell is Optional,
we treat that as None and carry on.

*/

trait StringReader[T] {
  def read(s: String): Try[T]
}

trait Readers {

  implied USDateReader for StringReader[Instant] {
    val usDate = DateTimeFormatter
      .ofPattern("M/d/yyyy H:mm:ss")
      .withZone(ZoneId of "UTC")

    def read(str: String): Try[Instant] = 
      Try(Instant.from(usDate parse str))
  }

  implied stringReader for StringReader[String] {
    def read(str: String): Try[String] = Success(str)
  }

  implied intReader for StringReader[Int] {
    def read(str: String): Try[Int] = Try(Integer parseInt str)
  }

  implied workReader for StringReader[RemoteWork] {
    def read(str: String): Try[RemoteWork] = str match {
      case "Yes"     => Success(RemoteWork.Remote)
      case "Partial" => Success(RemoteWork.PartialRemote)
      case "No"      => Success(RemoteWork.OnSite)
      case x         => Failure(new Exception(s"$x is not valid RemoteWork"))
    }
  }

  implied contractReader for StringReader[Contract] {
    def read(str: String): Try[Contract] = str match {
      case "Contract"                            => Success(Contract.Contractor)
      case "Permanent"                           => Success(Contract.Permanent)
      case "Will consider permanent or contract" => Success(Contract.ConsiderBoth)
      case x                                     => Failure(new Exception(s"$x is not a valid Contract"))
    }
  }

  implied routeReader for StringReader[ApplicationRoute] {
    import ApplicationRoute._
    def read(s: String): Try[ApplicationRoute] = Try(
      if (s contains "@") ApplicationEmail(s) else ApplicationURL(s)
    )
  }
}

/*
trait Converter[T <: HList] {
  def convert(values: List[String]): Try[T]
}
*/

trait Converters {
/*
  implicit val emptyListConverter = new Converter[HNil] {
    def convert(values: List[String]) = values match {
      case Nil => Success(HNil)
      case xs  => Failure(new Exception(s"Too many runtime values at: ${xs.head}"))
    }
  }

  implicit def optionalValueConverter[H, T <: HList](implicit reader: StringReader[H], converter: Converter[T]) =
    new Converter[Option[H] :: T] {
      def convert(values: List[String]) =
        if (values.isEmpty) converter.convert(Nil).map(tail => None :: tail)
        else for {
          h <- reader.read(values.head)
          t <- converter.convert(values.tail)
          } yield Some(h) :: t
    }

  implicit def listConverter[H, T <: HList](implicit reader: StringReader[H], converter: Converter[T]) =
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
*/
}

trait Read[P] {
  def read(row: List[String]): Try[P]
}

object Read extends Converters with Readers {

  import scala.reflect.Generic
  import scala.compiletime.erasedValue
  import scala.compiletime.Shape._

  inline def derived[P] given (ev: Generic[P]) = new Read[P] {
    def read(row: List[String]): Try[P] = {
        Failure(new Exception("recurse"))
      /*
      inline erasedValue[ev.Shape] match {
        case _: Case[_, elems] =>  Failure(new Exception("recurse"))
      }*/
    }
  }

  // Or instead of `...derives Read` on `Job`, we could:
  // implied [P] given Generic[P] for Read[P] = Read.derived

}

