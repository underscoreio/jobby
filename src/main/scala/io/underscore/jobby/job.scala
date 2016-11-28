package io.underscore.jobby

import java.time.Instant

/*
 * Encoding of a job posting.
 * I've encoded columns we need to reason about with ADTs.
 * For columns we don't process and just output, I've left as Strings.
 *
 * The citizenship field was added to the job form later, so not all
 * jobs will have that field. That's why it's an Option.
 */

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
  companyName       : String,
  citizenship       : Option[String] = None
)

