This project is my implementation of other’s paper “Bed-tree: an all-purpose index structure for string similarity search based on edit distance". 
This paper presents a new indexing scheme using a standard B+ tree to efficiently support string similarity selection query and join query. 
In my implementation, first I implement a standard B+ tree. 
Then I implement the “string dictionary order” and “q-gram counting order” transform functions proposed in that paper. 
Last, I use tens of thousands of paper titles and author names from the DBLP to evaluate performance of string similarity selection query of “bed-tree”. (Java)

hdblp.xml are all data from dblp site, it’s 2014 version.
terces is library used for parsing xml file
You can run Test.java, Test1.java, Test2.java, Test3.java
Detailed explanation of each class’s functions and performance test results are presented in my report.