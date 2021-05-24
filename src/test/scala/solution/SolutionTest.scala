package solution

import org.scalatest.BeforeAndAfterEach

import java.io.File
import scala.io.Source

case class SolutionTest() extends UnitSpec with BeforeAndAfterEach {

  import solution.Solution._

  protected val randomIntUpperBound = 9999
  protected val newDatasetPath = "dataset.txt"
  protected val K = 3
  protected val chunkFilePrefix = "sorted-chunk-"
  protected val chunkFilesuffix = ".txt"

  override def afterEach(): Unit = cleanUp(K)

  "Solution" when {
    "creating a data set" should {
      "create a file" in {
        val n = 1
        Solution.createDataset(n, randomIntUpperBound)
        val file = new File(newDatasetPath)
        file.check.get.isFile shouldBe true
      }

      "create n1 records" in {
        val n1 = 100
        createDataset(n1, randomIntUpperBound)
        val source = Source.fromFile(newDatasetPath)
        source.getLines().toArray.length == n1 shouldBe true
      }

      "create n2 records in binary form" in {
        val n2 = 50
        Solution.createDataset(n2, randomIntUpperBound)
        val source = Source.fromFile(newDatasetPath)
        val records = source.getLines().toArray

        records.length == n2 shouldBe true
        records.foreach { record =>
          for (x <- 2 to 9) record.contains(x) shouldBe false
        }
      }

      "create n3 records" in {
        val n3 = 432
        Solution.createDataset(n3, randomIntUpperBound)
        val source = Source.fromFile(newDatasetPath)
        source.getLines().toArray.length == n3 shouldBe true
      }
    }

    "splitting and sorting files" should {
      "split the dataset into K chunks" in {
        val n1 = 100
        createDataset(n1, randomIntUpperBound)
        splitSortFiles(K)
        new File(chunkFilePrefix + (K + 1) + chunkFilesuffix).check.isEmpty shouldBe true
        for (x <- 0 to K) {
          new File(chunkFilePrefix + x + chunkFilesuffix).check.get.isFile shouldBe true
        }
      }

      "be faster when splitting and sorting concurrently" in {
        val n1 = 10000
        createDataset(n1, randomIntUpperBound)

        val tSingleThread = System.nanoTime
        splitSortFiles(K)
        val singleThreadDuration = (System.nanoTime - tSingleThread) / 1e9d
        println(s"Duration of splitting file and sorting chunks: $singleThreadDuration")

        val tMultiThread = System.nanoTime
        concurrentSplitSortFiles(K)
        val multithreadedDuration = (System.nanoTime - tMultiThread) / 1e9d
        println(s"Duration of splitting file and sorting chunks concurrently: $multithreadedDuration")

        multithreadedDuration < singleThreadDuration shouldBe true
      }
    }
  }

  private def cleanUp(K: Int) = {
    for {
      data <- new File(newDatasetPath).check
      deleted <- data.remove
    } yield deleted
    for (x <- 0 to K) {
      val path = s"sorted-chunk-$x.txt"
      for {
        foundFile <- new File(path).check
        deletedFile <- foundFile.remove
      } yield deletedFile
    }
  }
}
