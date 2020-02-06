package io.underscore.jobby

import java.time.format.DateTimeFormatter
import java.time.ZoneId
import java.nio.file.{FileSystems, Path, Files}
import java.nio.charset.Charset
import scala.util.Try

/*
 The purpose of this file is to deal with I/O related tasks,
 such as generating a filename and writing a Job to the filesystem.
 */
object IO {

  val dateFormat = DateTimeFormatter
    .ofPattern("yyyy-MM-dd")
    .withZone(ZoneId of "UTC")

  def filename(job: Job, path: String = "target"): Path = {
    val name = s"${dateFormat.format(job.timestamp)}-${slug(job)}.md"
    FileSystems.getDefault().getPath(path, name)
  }

  def slug(job: Job): String =
    job.companyName.toLowerCase.trim
      .replaceAll("\\W", "-")
      .replaceAll("-[-]+", "-")

  def write(job: Job): Try[Path] = Try {
    val path = filename(job)
    val content = Markdown(job)
    val w = Files.newBufferedWriter(path, Charset.forName("UTF-8"))
    try w.write(content)
    finally w.close
    path
  }

}
