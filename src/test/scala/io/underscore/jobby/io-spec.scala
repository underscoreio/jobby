package io.underscore.jobby

import org.junit.Test
import org.junit.Assert._

class IOSpec {

  import java.time.Instant
  /*
  import org.scalacheck.Arbitrary
  import org.scalacheck.Arbitrary.arbitrary

  implicit lazy val arbInst: Arbitrary[Instant] = Arbitrary(
    arbitrary[Long].map(Instant.ofEpochMilli)
  )

  def jobWithCompany(co: String): Option[Job] =
    arbitrary[Job].sample.map(job => job.copy(companyName = co))
  */

  def jobWithCompany(co: String): Option[Job] = Some(
    Job(
      Instant.now(),
      "Position",
      "Location",
      RemoteWork.OnSite,
      "Level",
      ApplicationRoute.ApplicationURL("http://example.org"),
      "Summary",
      "Description",
      "adminEmailAddress",
      "Newsletter",
      co,
    )
  )

  @Test def `Slug should trim whitespace from company name` =
    assertEquals(Some("foo-co"), jobWithCompany("Foo co ").map(IO.slug))

}
