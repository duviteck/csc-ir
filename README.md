csc-ir
======

Practical programming for Information Retrieval course in Computer Science Center, Autumn 2014.


# Generating JARs
For generating executable JARs, run `mvn package -DskipTests`. <br> 
Two JARs, *indexer.jar* and *searcher.jar* will be created.


# Indexer
**Usage**: `java -jar indexer.jar directory_to_index output_index_file_path` <br>
As soon as index will be generated and saved into file, process will stop.


# Searcher
**Usage**: `java -jar searcher.jar inverted_index_file_path` <br>
Searcher will ask for new queries eternally, until you pass an empty line or kill the process. <br>
Searcher handles queries like <br> 
**term1 /N term2 /+M term3 /-K term4** <br>
and don't handles queries like <br>
**term1 AND term2** or **term1 OR term2**
