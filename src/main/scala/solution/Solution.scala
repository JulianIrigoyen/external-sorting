package solution

import solution.Solution.mergeSort

import java.io.{BufferedWriter, FileOutputStream, OutputStreamWriter}
import scala.io.Source

object Solution extends App {

  /**
   * A. Write a program which will write N random 4-byte integers to a file (in binary form).
   * Choose N small at first to debug your problem, but it should scale to billions.
   *
   * @param n             Integers in the dataset
   * @param upperIntBound maximum possible value for the dataset
   */
  def createDataset(n: Int, upperIntBound: Int): Unit = {
    val r = scala.util.Random
    val data = List.fill(n)(r.nextInt(upperIntBound))
    val file = "dataset.txt"
    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))
    for (x <- data) writer.write(x.toBinaryString + "\n")
    writer.close()
  }

  /**
   * B.1
   * Loads the written data and splits them into multiple chunks sorted using merge sort
   */
  def splitSortFiles(k: Int) = {
    val source = Source.fromFile("dataset.txt")
    val data = source.getLines().toList.map(Integer.valueOf(_, 2)).map(_.toInt)
    val partitions = data.grouped(data.size / k).toList
    for (partition <- partitions.zipWithIndex) {
      val file = s"sorted-chunk-${partition._2}.txt"
      val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))
      for (x <- mergeSort(partition._1)) writer.write(x + "\n")
      writer.close()
    }
  }

  /** Q2 Are there parts of the program that can be parallelized across multiple cores in the same machine? */
  def concurrentSplitSortFiles(k: Int) = {
    val source = Source.fromFile("dataset.txt")
    val data = source.getLines().toList.map(Integer.valueOf(_, 2)).map(_.toInt)
    val chunks = data.grouped(data.size / k).toList
    val t1 = System.nanoTime
    synchronized {
      for (chunk <- chunks.zipWithIndex) {
        var th = new Thread(new ChunkSorter(chunk))
        th.setName(s"Chunk-n-${chunk._2}")
        th.start()
      }
    }
  }

  /** Canonical Recursive 2 Way Merge Sort algorithm to be used for internal sorting */
  def mergeSort(list: List[Int]): List[Int] = list match {
    case ::(head, Nil) => List(head)
    case list =>
      val middle = list.length / 2
      val left = mergeSort(list.splitAt(middle)._1)
      val right = mergeSort(list.splitAt(middle)._2)
      merge(left, right)
  }

  def merge(left: List[Int], right: List[Int]) = {
    var result = List[Int]()
    var i, j = 0
    while (i < left.length && j < right.length) {
      if (left(i) < right(j)) {
        result = result :+ left(i)
        i += 1
      } else {
        result = result :+ right(j)
        j += 1
      }
    }
    while (i < left.length) {
      result = result :+ left(i)
      i += 1
    }
    while (j < right.length) {
      result = result :+ right(j)
      j += 1
    }
    result
  }
}

/**
 * Thread worker that will receive a portion of the written data
 * sort it and write it to a temp file.
 *
 * @param indexedChunk A chunk of data zipped with its index in
 */
class ChunkSorter(indexedChunk: (List[Int], Int)) extends Thread {
  override def run(): Unit = {
    val file = s"sorted-chunk-${indexedChunk._2}.txt"
    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))
    for (x <- mergeSort(indexedChunk._1)) writer.write(x + "\n")
    writer.close()
  }
}