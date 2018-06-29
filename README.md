# LuceneCacmIndexer
This project was made for indexing and searching of the Cacm, Cranfield, Medline and Cisi Collection in Information retrieval.

In ~/LuceneCacmIndexer/data, there are a number of folders contain different data sets.

In LuceneCacmIndexer/src/main/java/, there are a currently apps, an Indexing Application (IndexerApp.java), a Retrieval Application (SearchApp.java) .

The code is based on examples developed by https://github.com/lucene4ir/lucene4ir

Run Configuration Setups

## IndexerApp

You must give the file path of the data set to be indexed as the program argument.




    -docs data/cacm/cacm.all



## SearchApp

You must give the file path of the index,query,result and specify the index field,paging number adn similarity model as the program argument.



     -index index -field all -queries data/cacm/query.text -paging 1000 -result result/cacm/lmd.res -sim LMD



## JAR creation.
`run mvn clean package` from the shell, executable jar will be in target/LuceneCacmIndexer-1.0-SNAPSHOT.jar

## Indexing

```shell
java -jar target/LuceneCacmIndexer-1.0-SNAPSHOT.jar -docs data/cacm/cacm.all
```

## Searching

```shell
java -cp target/LuceneCacmIndexer-1.0-SNAPSHOT.jar -index index -field all -queries data/cacm/query.text -paging 1000 -result result/cacm/lmd.res -sim LMD
```


## Try out the Apps

First, run the IndexerApp, which give params, will index the CACM collection. 

Then, you can run the SearchApp, which will take a list of queries, and run them against the index using LMD, and save the results to a result file (result/cacm/lmd.res).

To evaluate the output you will need to download and install the trec_eval from NIST, http://trec.nist.gov/trec_eval/

In data/cacm the list of documents relevant to each query is in the file, cacm.qrels, using trec_eval, we can measure the precision, recall, etc:

trec_eval ~/LuceneCacmIndexer/data/cacm/cacm.qrels ~/LuceneCacmIndexer/data/cacm/lmd.res