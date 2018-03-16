import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class SearchApp {

    private static Map<String, String> allQuery;

    private SearchApp() throws IOException {
    }


    public static void main(String[] args) throws Exception {


        String index = "index";
        String field = "all";
        String queryFiles = "data/cacm/query.text";
        String queryString;
        String queryNo;
        int hitsPerPage = 1000;
        StringBuilder result = new StringBuilder();


        CacmQueryParser queryParser = new CacmQueryParser();
        allQuery = queryParser.QueryParse(queryFiles);


        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity());
        Analyzer analyzer = CustomAnalyzer.builder()
                .withTokenizer("standard")
                .addTokenFilter("lowercase")
                .addTokenFilter("kstem")
                .build();


        for (Map.Entry<String, String> entry : allQuery.entrySet()) {

            queryString = entry.getValue();
            queryNo = entry.getKey();


            QueryParser parser = new QueryParser(field, analyzer);
            parser.setDefaultOperator(QueryParser.Operator.OR);


            Query query = parser.parse(QueryParser.escape(queryString));
            System.out.println("Searching for: " + query.toString(field));

            TopDocs results = searcher.search(query, hitsPerPage);
            ScoreDoc[] hits = results.scoreDocs;

            for (int i = 0; i < hits.length; i++) {


                Document doc = searcher.doc(hits[i].doc);
                //   System.out.println(queryNo + "\tQ0\t"+hits[i].doc+"\t"+hits[i].score+"\tporter");
                //System.out.println(queryNo + "\tQ0\t" + doc.get("id") + "\t" + hits[i].score + "\tporter");
                result.append(queryNo).append("\tQ").append(i).append("\t").append(doc.get("id")).append("\t").append(i).append("\t").append(hits[i].score).append("\tporter\n");


            }


        }


        reader.close();

        Files.write(Paths.get("result/bm25.res"), result.toString().getBytes());
    }


}
