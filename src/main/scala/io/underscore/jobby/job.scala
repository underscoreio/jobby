package io.underscore.jobby

import java.time.Instant

sealed trait RemoteWork
case object Remote        extends RemoteWork
case object PartialRemote extends RemoteWork
case object OnSite        extends RemoteWork

sealed trait ApplicationRoute
case class ApplicationEmail(value: String) extends ApplicationRoute
case class ApplicationURL(value: String)   extends ApplicationRoute


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

