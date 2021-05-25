package solution

import org.scalatest.BeforeAndAfterEach

import java.io.File
import java.util.concurrent.TimeUnit
import scala.io.Source

case class SolutionTest() extends UnitSpec with BeforeAndAfterEach {

  import solution.Solution._

  val K = 3

  protected val randomIntUpperBound = 9999
  protected val newDatasetPath = "dataset.txt"
  protected val chunkFilePrefix = "sorted-chunk-"
  protected val chunkFilesuffix = ".txt"

  override def beforeEach(): Unit = cleanUp(K)
  override def afterEach() : Unit = cleanUp(K)

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
      "be faster when splitting and sorting concurrently" in {
        val n1 = 10000
        createDataset(n1, randomIntUpperBound)

        val tSingleThread = System.nanoTime
        splitSortFiles(K)
        val singleThreadDuration = (System.nanoTime - tSingleThread) / 1e9d
        println(s"Duration of splitting file and sorting chunks: $singleThreadDuration")

        val tMultiThread = System.nanoTime
        multithreadedSplitSort(K)
        val multithreadedDuration = (System.nanoTime - tMultiThread) / 1e9d
        println(s"Duration of splitting file and sorting chunks concurrently: $multithreadedDuration")

        multithreadedDuration < singleThreadDuration shouldBe true
      }
    }

    "k-way merging using the minheap" should {

      "sort properly I (n = 100 k = 3)" in {

        val n = 100
        val upperIntBound = 999
        val k = K

        println("<<<<<<<<<<<< Start >>>>>>>>>>>>>>")
        println("\n")
        createDataset(n, upperIntBound)
        multithreadedSplitSort(k)

        val t0 = System.nanoTime
        MinHeap.mergeKSortedArrays(loadDataset(k).toArray.filter(_.nonEmpty).map(_.toArray), k)
        val elapsedTime = System.nanoTime - t0

        validateSolution

        println("\n")
        println(s"Sorted $n records using $k units of in-memory capacity to do sorting in ${TimeUnit.NANOSECONDS.toMillis(elapsedTime)} millis.")
      }

      "sort properly II (n = 1000 k = 10)" in {

        val n = 1000
        val upperIntBound = 99999
        val k = 10

        println("<<<<<<<<<<<< Start >>>>>>>>>>>>>>")
        println("\n")
        createDataset(n, upperIntBound)
        multithreadedSplitSort(k)

        val t0 = System.nanoTime
        MinHeap.mergeKSortedArrays(loadDataset(k).toArray.filter(_.nonEmpty).map(_.toArray), k)
        val elapsedTime = System.nanoTime - t0

        validateSolution

        println("\n")
        println(s"Sorted $n records using $k units of in-memory capacity to do sorting in ${TimeUnit.NANOSECONDS.toMillis(elapsedTime)} millis.")
      }

      "sort properly III (n = 10000 k = 100)" in {

        val n = 10000
        val upperIntBound = 900000
        val k = 100

        println("<<<<<<<<<<<< Start >>>>>>>>>>>>>>")
        println("\n")
        createDataset(n, upperIntBound)
        multithreadedSplitSort(k)

        val t0 = System.nanoTime
        MinHeap.mergeKSortedArrays(loadDataset(k).toArray.filter(_.nonEmpty).map(_.toArray), k)
        val elapsedTime = System.nanoTime - t0

        validateSolution

        println("\n")
        println(s"Sorted $n records using $k units of in-memory capacity to do sorting in ${TimeUnit.NANOSECONDS.toMillis(elapsedTime)} millis.")
      }

      "sort properly IV (n = 100000 k = 1000)" in {

        val n =  100000
        val upperIntBound = 10000000
        val k = 1000

        println("<<<<<<<<<<<< Start >>>>>>>>>>>>>>")
        println("\n")
        createDataset(n, upperIntBound)
        multithreadedSplitSort(k)

        val t0 = System.nanoTime
        MinHeap.mergeKSortedArrays(loadDataset(k).toArray.filter(_.nonEmpty).map(_.toArray), k)
        val elapsedTime = System.nanoTime - t0

        validateSolution

        println("\n")
        println(s"Sorted $n records using $k units of in-memory capacity to do sorting in ${TimeUnit.NANOSECONDS.toMillis(elapsedTime)} millis.")
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
