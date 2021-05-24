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

  override def beforeEach(): Unit = {
    for {
      data <- new File(newDatasetPath).check
      deleted <- data.remove
    } yield deleted
    cleanUp(K)
  }

  override def afterEach(): Unit = cleanUp(K)

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
        createDataset(n1, randomIntUpperBound)
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

    "splitting and sorting files" should {
      "split the dataset into K chunks" in {
        val n1 = 100
        createDataset(n1, randomIntUpperBound)
        singleThreadedSplitSortStep(K)
        new File(chunkFilePrefix + (K + 1) + chunkFilesuffix).check.isEmpty should be(true)
        for (x <- 0 to K) {
          new File(chunkFilePrefix + x + chunkFilesuffix).check.get.isFile should be(true)
        }
      }

      "be faster when splitting and sorting concurrently" in {
        val n1 = 10000
        createDataset(n1, randomIntUpperBound)

        val tSingleThread = System.nanoTime
        singleThreadedSplitSortStep(K)
        val singleThreadDuration = (System.nanoTime - tSingleThread) / 1e9d
        println(s"Single threaded duration of splitting file and sorting chunks: $singleThreadDuration")

        val tMultiThread = System.nanoTime
        multithreadedSplitSort(K)
        val multithreadedDuration = (System.nanoTime - tMultiThread) / 1e9d
        println(s"Multithreaded duration of splitting file and sorting chunks: $multithreadedDuration")

        multithreadedDuration < singleThreadDuration shouldBe (true)
      }
    }
  }

  private def cleanUp(K: Int) = {
    for (x <- 0 to K) {
      val path = s"sorted-chunk-$x.txt"
      for {
        foundFile <- new File(path).check
        deletedFile <- foundFile.remove
      } yield deletedFile
    }
  }
}
