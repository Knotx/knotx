import java.util.*

object Sonatype {
  val releasesSnapshot = "https://oss.sonatype.org/content/repositories/snapshots"
  val releasesStaging = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
}

object Utils {
  fun timestamp(): Long {
    return Date().time
  }
}
