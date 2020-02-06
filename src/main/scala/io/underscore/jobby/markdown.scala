package io.underscore.jobby

import java.time.Period

/*
 * Jobs are represented in text as a markdown file with a YAML header.
 * This class will format a job.
 */
object Markdown {

  private def instructionsText(job: Job): String =
    job.application match {
      case ApplicationEmail(_) =>
        """Use the button below to send us an email including your CV, the position you're applying for, and anything else you might want to say."""
      case ApplicationURL(_) =>
        """Apply online. Click "Apply Now" below to get started."""
    }

  private def remoteText(job: Job): String =
    job.work match {
      case Remote        => "Yes"
      case PartialRemote => "Partial"
      case OnSite        => "No"
    }

  private def contract(job: Job): String =
    job.contract match {
      case None =>
        "Permanent" // default assumption for historic jobs before we asked the question
      case Some(c) =>
        c match {
          case Contractor   => "Contractor only"
          case Permanent    => "Permanent employment"
          case ConsiderBoth => "Will consider permanent role and contractor"
        }
    }

  private def expireMeta(job: Job): String = {
    val maxage = Period.ofDays(31)
    IO.dateFormat.format(job.timestamp plus maxage)
  }

  private def citizenMeta(job: Job): Option[(String, String)] =
    job.citizenship.map(c => "citizenship" -> c)

  private def howToApplyMeta(job: Job): (String, String) =
    job.application match {
      case ApplicationEmail(email) => "email" -> email
      case ApplicationURL(url)     => "application_url" -> url
    }

  private def juniorMeta(job: Job): Option[(String, String)] =
    Some(job.level).filter(_ contains "Junior").map(_ => "junior" -> "true")

  def apply(job: Job): String = {

    val fixedData = Map(
      "layout" -> "job",
      "expire" -> expireMeta(job),
      "title" -> job.position,
      "company" -> job.companyName,
      "location" -> job.location,
      "level" -> job.level,
      "remote" -> remoteText(job),
      "contract" -> contract(job),
      "summary" -> job.summary,
      "admin" -> job.adminEmailAddress,
      "instructions" -> instructionsText(job)
    ) + howToApplyMeta(job)

    val optionalData = juniorMeta(job).toMap ++ citizenMeta(job).toMap

    val data = fixedData ++ optionalData

    import scala.jdk.CollectionConverters._
    import org.yaml.snakeyaml.Yaml
    import org.yaml.snakeyaml.DumperOptions
    val options = new DumperOptions()
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
    val meta = new Yaml(options).dump(data.asJava)

    s"""
      |---
      |${meta}
      |---
      |
      |<!-- break -->
      |
      |${job.description}
      """.stripMargin.trim
  }
}
