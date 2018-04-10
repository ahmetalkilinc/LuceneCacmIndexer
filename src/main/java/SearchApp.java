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

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchApp {

    private static Map<String, String> allQuery;

    private SearchApp() throws IOException {
    }


    public static void main(String[] args) throws Exception {


        String usage = "Usage:\t [-index dir] [-field f] [-queries file] [-paging hitsPerPage] [-result resultPath]\n\n ";

        if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
            System.out.println(usage);
            System.exit(0);
        }


        String index = "index";
        String field = "all";
        String queryFiles = null;
        String queryString;
        String queryNo;
        int hitsPerPage = 1000;
        String result = "";
        String resultPath = null;
        List<String> lines = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            if ("-index".equals(args[i])) {
                index = args[i + 1];
                i++;
            } else if ("-field".equals(args[i])) {
                field = args[i + 1];
                i++;
            } else if ("-queries".equals(args[i])) {
                queryFiles = args[i + 1];
                i++;
            } else if ("-result".equals(args[i])) {
                resultPath = args[i + 1];
                i++;
            } else if ("-paging".equals(args[i])) {
                hitsPerPage = Integer.parseInt(args[i + 1]);
                if (hitsPerPage <= 0) {
                    System.err.println("There must be at least 1 hit per page.");
                    System.exit(1);
                }
                i++;
            }
        }


        if (queryFiles == null) {
            System.err.println("Usage: " + usage);
            System.err.println("There must be queryFiles.");
            System.exit(1);
        }
        if (resultPath == null) {
            System.err.println("Usage: " + usage);
            System.err.println("There must be resultPath.");
            System.exit(1);
        }


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

                result = queryNo + " Q0 " + doc.get("id") + " " + i + " " + hits[i].score + " porter";
                lines.add(result);

            }


        }


        reader.close();


       Files.write(Paths.get(resultPath),lines);

    }


}
