package io.underscore.jobby

import java.time.Instant

/*
 * Encoding of a job posting.
 * I've encoded columns we need to reason about with ADTs.
 * For columns we don't process and just output, I've left as Strings.
 *
 * The citizenship, visa, perm/contract fields were  added to the job form later, so not all
 * jobs will have that field. That's why they are `Option`al.
 *
 * NB: the fields you see will depend on the command line argument you pass
 * to Jobby. It'll likely be: `"Form Responses 1!A2:M"` (so you'll see fields up to column M).
 *
 * The custom case classes have String => T methods defined in `converters.scala`.
 */

enum RemoteWork {
  case Remote, PartialRemote, OnSite
}

enum Contract {
  case Permanent, Contractor, ConsiderBoth
}

enum ApplicationRoute {
  case ApplicationEmail(value: String)
  case ApplicationURL(value: String)
}

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
  citizenship       : Option[String] = None,
  contract          : Option[Contract] = None,
) derives Read

