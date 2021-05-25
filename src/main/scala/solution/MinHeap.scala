package solution

import java.io.{BufferedWriter, FileOutputStream, OutputStreamWriter}

class MinHeapNode(
                   var element: Int,
                   var arrIndex: Int,
                   var nextElement: Int
                 )

object MinHeap {

  def mergeKSortedArrays(kSortedArrays: Array[Array[Int]], k: Int) = {
    val file = "solution.txt"
    val writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)))


    //define an array to be represented as a heap
    val heapNodes = new Array[MinHeapNode](k)
    var outputLength = 0

    //get the first (ie smallest) element from each array and assign it as a node of the min heap
    for (i <- kSortedArrays.indices) {
      val node = new MinHeapNode(kSortedArrays(i)(0), i, 1)
      heapNodes(i) = node
      outputLength += kSortedArrays(i).length //compute output length now for later use
    }

    // Create a min heap with the previously set nodes.
    val minHeap = new MinHeap(heapNodes, k)

    for (i <- 0 until outputLength) {
      // write root to output
      val root = minHeap.min
      writer.write(root.element + "\n")

      // If root was the last element of its array, force a value down the heap
      if (root.nextElement >= kSortedArrays(root.arrIndex).length) {
        root.element = Integer.MAX_VALUE
      } else { //find next element to put in heap
        root.element = kSortedArrays(root.arrIndex)({
          root.nextElement += 1
          root.nextElement - 1
        })
      }

      // Replace root with next element of array
      minHeap.replaceRoot(root)
    }
    writer.close()
  }
}

/**
 *
 * @param heapNodes Array of elements in heap
 * @param heapSize Current number of elements in min heap
 */
class MinHeap(var heapNodes: Array[MinHeapNode], var heapSize: Int) {

  // Constructor
  var i: Int = (heapSize - 1) / 2
  while (i >= 0) {
    minHeapify(i)
    i -= 1
  }

  def minHeapify(i: Int): Unit = {
    val l: Int = left(i)
    val r: Int = right(i)
    var smallest: Int = i
    if (l < heapSize && heapNodes(l).element < heapNodes(i).element) {
      smallest = l
    }
    if (r < heapSize && heapNodes(r).element < heapNodes(smallest).element) {
      smallest = r
    }
    if (smallest != i) {
      swap(heapNodes, i, smallest)
      minHeapify(smallest)
    }
  }

  def left(i: Int): Int = 2 * i + 1

  def right(i: Int): Int = 2 * i + 2

  def min: MinHeapNode = {
    if (heapSize <= 0) {
      println("Heap underflow")
      return null
    }
    heapNodes(0)
  }

  def replaceRoot(root: MinHeapNode): Unit = {
    heapNodes(0) = root
    minHeapify(0)
  }

  def swap(arr: Array[MinHeapNode], i: Int, j: Int): Unit = {
    val temp: MinHeapNode = arr(i)
    arr(i) = arr(j)
    arr(j) = temp
  }
}

