import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class IndexerApp {

    private static String indexPath = "index";
    private static DocumentIndexer documentIndexer;
    private static String filePath = "data/cacm/cacm.all";
    private DocumentModel docModel;


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


        IndexerApp indexerApp = new IndexerApp("1");

        try {


            indexerApp.indexDocumentsFromFile(filePath);

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
