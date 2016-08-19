package io.underscore.jobby

import java.time.Instant

sealed trait RemoteWork
final case object Remote        extends RemoteWork
final case object PartialRemote extends RemoteWork
final case object OnSite        extends RemoteWork

sealed trait ApplicationRoute
final case class ApplicationEmail(value: String) extends ApplicationRoute
final case class ApplicationURL(value: String)   extends ApplicationRoute

case class Job(
  timestamp         : Instant,
  position          : String,
  location          : String,
  work              : RemoteWork,
  level             : String,
  application       : ApplicationRoute,
  summary           : String,
  description       : String,
  adminEmailAddress : String,
  newsletter        : String,
  companyName       : String
)

