package solution

import java.io.File
import scala.io.Source

case class SolutionTest() extends UnitSpec {

  protected val randomIntUpperBound = 9999
  protected val newDatasetPath = "dataset.txt"

  "Solution" when {
    "creating a data set" should {
      "create a file" in {
        val n = 1
        Solution.createDataset(n, randomIntUpperBound)
        val file = new File(newDatasetPath)
        file.check.get.isFile should be(true)
      }

      "create n1 records" in {
        val n1 = 100
        Solution.createDataset(n1, randomIntUpperBound)
        val source = Source.fromFile(newDatasetPath)
        source.getLines().toArray.length == n1 should be(true)
      }

      "create n2 records in binary form" in {
        val n2 = 50
        Solution.createDataset(n2, randomIntUpperBound)
        val source = Source.fromFile(newDatasetPath)
        val records = source.getLines().toArray

        records.length == n2 should be(true)
        records.foreach { record =>
          for (x <- 2 to 9) record.contains(x) should be(false)
        }
      }

      "create n3 records" in {
        val n3 = 41235
        Solution.createDataset(n3, randomIntUpperBound)
        val source = Source.fromFile(newDatasetPath)
        source.getLines().toArray.length == n3 should be(true)
      }
    }
  }
}
