package solution

/**
 * MinHeap implementation
 *
 * @param heapNodes current nodes
 * @param heapSize  current size
 */
class MinHeap(var heapNodes: Array[MinHeapNode], var heapSize: Int) {

  var i: Int = (heapSize - 1) / 2
  while (i >= 0) {
    minHeapify(i)
    i -= 1
  }

  def left(i: Int) : Int = 2 * i + 1

  def right(i: Int): Int = 2 * i + 2

  def min = {
    if (heapSize <= 0) {
      println("Heap underflow")
      null
    }
    heapNodes(0)
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

/**
 * Min Heap Node
 *
 * @param element     The element to be stored
 * @param arrIndex    index of the array from which the element is taken
 * @param nextElement index of the next element to be picked from array
 */
class MinHeapNode(
                   var element: Int,
                   var arrIndex: Int,
                   var nextElement: Int
                 )
