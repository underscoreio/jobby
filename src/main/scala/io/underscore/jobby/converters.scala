package io.underscore.jobby

import scala.util.{ Success, Failure, Try }
import purecsv.safe.converter.StringConverter
import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.time.Instant

object Converters {

  private val usDate = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm:ss").withZone(ZoneId of "UTC")

  implicit val instantConverter = new StringConverter[Instant] {
    override def tryFrom(str: String): Try[Instant] = Try(Instant.from(usDate parse str))
    override def to(inst: Instant): String = usDate.format(inst)
  }

  implicit val workConverter = new StringConverter[RemoteWork] {
    override def tryFrom(str: String): Try[RemoteWork] = str match {
      case "Yes"     => Success(Remote)
      case "Partial" => Success(PartialRemote)
      case "No"      => Success(OnSite)
      case x         => Failure(new Exception(s"$x is not valid RemoteWork"))
    }

    override def to(r: RemoteWork): String = r match {
      case Remote        => "Yes"
      case PartialRemote => "Partial"
      case OnSite        => "No"
    }
  }

  implicit val routeConverter = new StringConverter[ApplicationRoute] {
    override def tryFrom(s: String): Try[ApplicationRoute] = Try(
      if (s contains "@") ApplicationEmail(s) else ApplicationURL(s)
    )

    override def to(r: ApplicationRoute): String = r match {
      case ApplicationEmail(v) => v
      case ApplicationURL(v)   => v
    }
  }
}

