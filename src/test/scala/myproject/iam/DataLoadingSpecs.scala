package myproject.iam

import myproject.common.Done
import myproject.common.FutureImplicits._
import myproject.database.{DefaultDataLoader, H2ApplicationDatabase}
import org.scalatest.DoNotDiscover
import test.UnitSpec

@DoNotDiscover
class DataLoadingSpecs extends UnitSpec {
  it should "load the demo data" in {
    val dl = new DefaultDataLoader
    val h2 = new H2ApplicationDatabase
    h2.migrate()
    dl.load(h2).futureValue shouldBe Done
    h2.close()
  }
}
