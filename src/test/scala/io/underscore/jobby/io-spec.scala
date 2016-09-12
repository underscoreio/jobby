package io.underscore.jobby

import org.scalatest._
import org.scalacheck.Shapeless._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary

class IOSpec extends FlatSpec with Matchers {

  import java.time.Instant
  implicit lazy val arbInst: Arbitrary[Instant] = Arbitrary(
    arbitrary[Long].map(Instant.ofEpochMilli)
  )

  def jobWithCompany(co: String): Option[Job] =
    arbitrary[Job].sample.map(job => job.copy(companyName = co))
    

  "IO.slug" should "trim whitespace from company name" in {
    jobWithCompany("Foo co ").map(IO.slug) shouldBe Some("foo-co")
  }


}
