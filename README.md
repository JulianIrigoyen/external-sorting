# External Sorting

## The problem

Write an external sort program, with some unit tests. The objective of external sorting is to sort a dataset that does not fit in memory. 

**To manage this problem effectively in a reasonable period of time, this restriction will need to be artificial.**

Therefore  : 

* A. Write a program which will write N random 4-byte integers to a file (in binary form). Choose N small at first to debug your problem, but it should scale to billions.

* B. Write a program which uses an external merge sort to write a sorted version of the file, using no more than M integers of in-memory capacity to do sorting.
      Start with a small M -- say three -- and make sure it works up to millions. (When M is larger than N, then the sort is no longer external) 
      
* C. Write a program which reads the sorted file and confirms that it is sorted correctly. 
You may use any library functions your language provides for doing the in-memory sort of the M integers, but you must write the external merge sort algorithm yourself. 
Many merge sort algorithms allow large numbers of files to be merged during the merge phase. An ideal algorithm does allow an arbitrary number, but it is acceptable for you to allow only 2 if you like. 
Can you adjust your program in the following ways, or, if you do not have time, discuss your strategy for addressing? 

Please include in your response the URLs of any references you consulted in choosing your external sorting algorithm. 


##  Approach
An external sorting algorithm manages the trade offs of rapid rapid random access of internal memory with the relatively fast sequential access of secondary disks by sorting in two phases:

* A run creation phase, where the original data is chunked and usually sorted using a sorting algorithm of O(n lg n) complexity. 
* A merge phase, where the in memory capacity is limited. Among the possibilities, using the min heap data structure is particularly efficient, as it allows to perform the sorting in  O(n * k * lg k)

In a min heap, the min heap property is that for every node i other than the root:
A[Parent(i)] <= A[i]

The smallest element in the heap is the root. 

The min heap data structure will be exploited to perform a K Way Merge Sort, where K will represent the memory units that will be used to perform the sorting through the heap. 

By this, after performing the initial splitting and sorting of the original dataset, the first element of each chunk will be loaded in to the heap, where parallel comparison will be performed to extract the minimum value and write it to the output file. 
Because each of the min heap nodes holds the index of the chunk where it came from, we will know where to get the next element to fill in the heap.

## Discussion 

1. This program is likely to be I/O bound. Is there any way to take advantage of this? 
   1. We can leverage the min heap data structure to make data access more efficient through the merging phase. 
  

2. Are there parts of the program that can be parallelized across multiple cores in the same machine? 
      1. Splitting and sorting the initial unsorted dataset can be parallelized. A demonstration and test of this is included in the soruce code. 
      2. The merge sort phase of the internal sorting step can also be made concurrent to make the program more efficient (Cormen et al, p. 803)

3. Across multiple disks in the same machine? 
      1.  Splitting and sorting the initial unsorted dataset could also be done along multiple disks in the same machine. 
      
4. Across multiple machines? 
      1. Splitting and sorting the initial unsorted dataset could also be done along multiple disks in the same machine. 
      2. In a more realistic context, the splitted and sorted files could be fed to the merge step from different machines.

5. How would one choose M for different N, cores, spindles, and machines?
      1. For optimum efficiency, M (which will define the number of passes of our algorithm), M needs to be obtained by dividing N (records) with the amount of units of in memory capacity (B). 
      Hence optimal M = (N/B)


## Resources

In order to find a valid approach and write a solution to this problem, I consulted the following resources: 

 * Cormen, Thomas H. Et Al. Introduction to Algorithms. Cambridge, MA: MIT Press, 2009.
 * Atallah, Mikhail J., and Marina Blanton. "Algorithms and Theory of Computation Handbook, Volume 1." 2009. doi:10.1201/9781584888239.
 * Bell, Ana, Eric Grimson, and John Guttag. MIT OpenCourseWare, Massachusetts Institute of Technology. Accessed May 24, 2021. [Link to video](https://ocw.mit.edu/courses/electrical-engineering-and-computer-science/6-0001-introduction-to-computer-science-and-programming-in-python-fall-2016/lecture-videos/lecture-12-searching-and-sorting/)
 
 The implementations of merge(), mergeSort(), mergeKSortedArrays() and the MinHeap class were inspired by content in the material listed above and enhanced by consulting the following online resources: 
 
 * Piterman, Sergey. "How to Merge K Sorted Arrays." Medium. February 12, 2021. Accessed May 24, 2021. [Link to article](https://medium.com/outco/how-to-merge-k-sorted-arrays-c35d87aa298e).
 * "Merge K Sorted Arrays: Set 1." GeeksforGeeks. May 18, 2021. Accessed May 24, 2021 [Link to article](https://www.geeksforgeeks.org/merge-k-sorted-arrays/).
 * "K-way Merge Algorithm." Wikipedia. April 09, 2021. Accessed May 24, 2021. [Link to article](https://en.wikipedia.org/wiki/K-way_merge_algorithm).
 
 Several parts of the code where improved thanks to / with answers found in StackOverflow. 
  

  
  
  
