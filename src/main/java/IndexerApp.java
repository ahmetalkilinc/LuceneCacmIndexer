import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class IndexerApp {


    private static String indexPath = "index";
    private static DocumentIndexer documentIndexer;
    private static String docsPath = null;
    private static DocumentModel docModel;

    public IndexerApp() {
        System.out.println("Indexer");
    }

    public IndexerApp(String indexParamFile) throws IOException {
        System.out.println("Indexer App");
        selectDocumentParser(DocumentModel.CACM);
    }


    /**
     * Index all text files under a directory.
     */
    public static void main(String[] args) throws IOException {

        String usage = "[-docs DOCS_PATH] \n\n"
                + "This indexes the documents in DOCS_PATH, creating a Lucene index"
                + "in INDEX_PATH that can be searched with SearchFiles";


        for (int i = 0; i < args.length; i++) {
            if ("-docs".equals(args[i])) {
                docsPath = args[i + 1];
                i++;
            }
        }

        if (docsPath == null) {
            System.err.println("Usage: " + usage);
            System.exit(1);
        }


        IndexerApp indexerApp = new IndexerApp("1");

        try {


            indexerApp.indexDocumentsFromFile(docsPath);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        indexerApp.finished();
        System.out.println("Done building Index");


        System.out.println("Done building Index");


    }


    public void selectDocumentParser(DocumentModel dm) throws IOException {
        docModel = dm;
        documentIndexer = null;
        switch (dm) {
            case CACM:
                System.out.println("CACM Document Parser");
                documentIndexer = new CacmParser(indexPath);
                break;


            default:
                System.out.println("Default Document Parser");

                break;
        }
    }

    public void indexDocumentsFromFile(String filename) {
        documentIndexer.indexDocumentsFromFile(filename);
    }

    public void finished() {
        documentIndexer.finished();

        try {
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("index")));
            long numDocs = reader.numDocs();
            System.out.println("Number of docs indexed: " + numDocs);


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }


    }


    private enum DocumentModel {
        CACM
    }
}
