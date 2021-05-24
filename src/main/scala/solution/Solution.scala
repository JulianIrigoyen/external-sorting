package solution

import java.io.{BufferedWriter, FileOutputStream, OutputStreamWriter}

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
}
