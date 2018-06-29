
import org.apache.lucene.index.*;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;


public class StatisticApp {

    public String indexName;
    public IndexReader reader;

    public StatisticApp() {
        indexName = "index";
        reader = null;
    }


    public void openReader() {
        try {
            reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexName)));

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }
    }

    public void docStats() {

        long numDocs = reader.numDocs();
        long maxDocs = reader.maxDoc();
        System.out.println("Number of docs: " + numDocs + ", Max Docs = " + maxDocs);

    }


    public void reportCollectionStatistics() throws IOException {

        IndexSearcher searcher = new IndexSearcher(reader);

        CollectionStatistics collectionStats = searcher.collectionStatistics("all");
        long token_count = collectionStats.sumTotalTermFreq();
        long doc_count = collectionStats.docCount();
        long sum_doc_count = collectionStats.sumDocFreq();
        long avg_doc_length = token_count / doc_count;

        System.out.println("ALL: Token count: " + token_count + " Doc Count: " + doc_count + " sum doc: " + sum_doc_count + " avg doc len: " + avg_doc_length);


    }


    public static void main(String[] args) throws IOException {


        StatisticApp statsApp = new StatisticApp();


        statsApp.openReader();
        statsApp.docStats();
        statsApp.reportCollectionStatistics();


    }

}

