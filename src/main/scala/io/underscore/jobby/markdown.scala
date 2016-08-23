package io.underscore.jobby

object Markdown {

  def instructionsText(job: Job): String =
    job.application match {
      case ApplicationEmail(_) => 
        """Use the button below to send us an email including your CV, the position you're applying for, and anything else you might want to say."""
      case ApplicationURL(_)   => 
        """Apply online. Click "Apply Now" below to get started."""
    }

  def howToApplyMeta(job: Job): String =
    job.application match {
      case ApplicationEmail(email) => s"email: $email"
      case ApplicationURL(url)     => s"application_url: $url"
    }

  def remoteText(job: Job): String =
    job.work match {
      case Remote        => "Yes"
      case PartialRemote => "Partial"
      case OnSite        => "No"
    }

  def juniorMeta(job: Job): String = 
    if (job.level contains "Junior") "junior: true" else ""

  def apply(job: Job): String =
      s"""
      |---
      |layout: job
      |title: ${job.position}
      |company: |
      |  ${job.companyName}
      |location: ${job.location}
      |level: ${job.level}
      |remote: ${remoteText(job)}
      |summary: |
      |  ${job.summary}
      |admin: ${job.adminEmailAddress}
      |${howToApplyMeta(job)}
      |instructions: |
      |  ${instructionsText(job)}
      |${juniorMeta(job)}
      |---
      |
      |<!-- break -->
      |
      |${job.description}
      """.stripMargin.trim
}
