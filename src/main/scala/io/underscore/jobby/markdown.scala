package io.underscore.jobby

import java.time.Period

/*
 * Jobs are represented in text as a markdown file with a YAML header.
 * This class will format a job. It does a bad job of producing YAML.
 */
object Markdown {

  private def instructionsText(job: Job): String =
    job.application match {
      case ApplicationEmail(_) =>
        """Use the button below to send us an email including your CV, the position you're applying for, and anything else you might want to say."""
      case ApplicationURL(_)   =>
        """Apply online. Click "Apply Now" below to get started."""
    }

  private def howToApplyMeta(job: Job): String =
    job.application match {
      case ApplicationEmail(email) => s"email: $email"
      case ApplicationURL(url)     => s"application_url: $url"
    }

  private def remoteText(job: Job): String =
    job.work match {
      case Remote        => "Yes"
      case PartialRemote => "Partial"
      case OnSite        => "No"
    }

  private def juniorMeta(job: Job): String =
    if (job.level contains "Junior") "junior: true" else ""

  private def contract(job: Job): String =
    job.contract match {
      case None    => "Permanent" // default assumption for historic jobs before we asked the question
      case Some(c) => c match {
        case Contractor   => "Contractor only"
        case Permanent    => "Permanent employment"
        case ConsiderBoth => "Will consider permanent role and contractor"
      }
    }

  private def citizenMeta(job: Job): String = job.citizenship match {
    case Some(c) => s"citizenship: |\n  $c"
    case None    => ""
  }

  private def expireMeta(job: Job): String = {
    val maxage = Period.ofDays(31)
    IO.dateFormat.format(job.timestamp plus maxage)
  }

  def apply(job: Job): String =
      s"""
      |---
      |layout: job
      |expire: ${expireMeta(job)}
      |title: ${job.position}
      |company: |
      |  ${job.companyName}
      |location: ${job.location}
      |level: ${job.level}
      |remote: ${remoteText(job)}
      |contract: ${contract(job)}
      |summary: |
      |  ${job.summary}
      |admin: ${job.adminEmailAddress}
      |${howToApplyMeta(job)}
      |instructions: |
      |  ${instructionsText(job)}
      |${juniorMeta(job)}
      |${citizenMeta(job)}
      |---
      |
      |<!-- break -->
      |
      |${job.description}
      """.stripMargin.trim
}
