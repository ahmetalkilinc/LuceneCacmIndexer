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
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.LMSimilarity.CollectionModel;

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


    protected enum SimModel {
        BM25, LGD, LMD, DFI, DFR
    }

    protected static SimModel sim;
    protected static Similarity simfn;
    protected static CollectionModel colModel;

    private static void setSim(String val) {
        try {
            sim = SimModel.valueOf(val);
        } catch (Exception e) {
            System.out.println("Similarity Function Not Recognized - Setting to Default");
            System.out.println("Possible Similarity Functions are:");
            for (SimModel value : SimModel.values()) {
                System.out.println("<model>" + value.name() + "</model>");
            }

        }
    }

    public static void selectSimilarityFunction(SimModel sim) {
        colModel = null;
        switch (sim) {

            case BM25:
                System.out.println("BM25 Similarity Function");
                simfn = new BM25Similarity();
                break;

            case LGD:
                System.out.println("LGD Similarity Function");
                Distribution distruibution = new DistributionLL();
                Lambda lambda = new LambdaDF();
                Normalization norm = new NormalizationH2();
                simfn = new IBSimilarity(distruibution, lambda, norm);
                break;

            case LMD:
                System.out.println("LM Dirichlet Similarity Function");
                colModel = new LMSimilarity.DefaultCollectionModel();
                simfn = new LMDirichletSimilarity(colModel);
                break;

            case DFI:
                System.out.println("DFI Similarity Function");
                simfn = new DFISimilarity(new IndependenceChiSquared());
                break;


            case DFR:
                System.out.println("DFR Similarity Function with no after effect (?)");
                BasicModelP bmd = new BasicModelP();
                AfterEffect aen = new AfterEffectL();
                Normalization nh2 = new NormalizationH2();
                simfn = new DFRSimilarity(bmd, aen, nh2);
                break;


        }
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
        String similarity = "";
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
            } else if ("-sim".equals(args[i])) {
                similarity = args[i + 1];
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

        setSim(similarity);

        CacmQueryParser queryParser = new CacmQueryParser();
        allQuery = queryParser.QueryParse(queryFiles);


        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);

        selectSimilarityFunction(sim);
        searcher.setSimilarity(simfn);

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



        Files.write(Paths.get(resultPath), lines);
        System.out.println("Searching is finished.");
    }


}
