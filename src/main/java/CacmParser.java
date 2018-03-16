import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CacmParser extends DocumentIndexer {

    private static final String FIELD_ID = "id";
    private static final String FIELD_CONTENT = "content";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_AUTHOR = "author";
    private static final String FIELD_PUBDATE = "pubdate";
    private static final String FIELD_ALL = "all";

    private Field docidField;
    private Field titleField;
    private Field contentField;
    private Field authorField;
    private Field pubdateField;
    private Field allField;
    
    private Document doc;

    public CacmParser(String indexPath) throws IOException {
        super(indexPath);


        doc = new Document();

        initFields();
        initCacmDoc();
    }


    private void initFields() {
        System.out.println("InitFields");
        docidField = new StringField(FIELD_ID, "", Field.Store.YES);
        contentField = new TextField(FIELD_CONTENT, "", Field.Store.YES);
        titleField = new TextField(FIELD_TITLE, "", Field.Store.YES);
        authorField = new TextField(FIELD_AUTHOR, "", Field.Store.YES);
        pubdateField = new StringField(FIELD_PUBDATE, "", Field.Store.YES);
        allField = new TextField(FIELD_ALL, "", Field.Store.YES);

    }

    private void initCacmDoc() {
        doc.add(docidField);
        doc.add(contentField);
        doc.add(titleField);
        doc.add(authorField);
        doc.add(pubdateField);
        doc.add(allField);
    }

    public Document createCacmDocument(String docid, String title, String author, String content, String pubdate) {

        docidField.setStringValue(docid);
        contentField.setStringValue(title + " " +content);
        titleField.setStringValue(title);
        authorField.setStringValue(author);
        pubdateField.setStringValue(pubdate);
        allField.setStringValue(title + " " + author + " " + content);

        doc.add(docidField);
        doc.add(contentField);
        doc.add(titleField);
        doc.add(authorField);
        doc.add(pubdateField);
        doc.add(allField);

        return doc;
    }

    public void indexDocumentsFromFile(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            try {

                String[] fields = new String[5];
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = "";
                }
                // 0 - docid, 1 - title, 2-authors, 3-content, 4-pubdate
                int fieldno = 0;


                String line = br.readLine();
                while (line != null) {

                    if (line.startsWith(".I")) {
                        // if there is an existing document, create doc, and add to index
                        if (!fields[0].equals("")) {
                            doc.clear();
                            doc = createCacmDocument(fields[0], fields[1], fields[2], fields[3], fields[4]);
                            addDocumentToIndex(doc);
                        }

                        // reset fields
                        for (int i = 0; i < fields.length; i++) {
                            fields[i] = "";
                        }
                        String[] parts = line.split(" ");
                        // set field 0 to docid
                        fields[0] = parts[1];
                        System.out.println("Indexing document: " + parts[1]);
                        fieldno = 0;
                    }

                    if (line.startsWith(".T")) {
                        // set field to title, capture title text
                        fieldno = 1;
                    }

                    if (line.startsWith(".A")) {
                        // set field to author
                        fieldno = 2;
                    }

                    if (line.startsWith(".W")) {
                        // set field to content
                        fieldno = 3;
                    }

                    if (line.startsWith(".B")) {
                        // set field to pub date
                        fieldno = 4;
                    }

                    if ((line.startsWith(".X")) || (line.startsWith(".N"))) {
                        // set field to title, capture title text
                        fieldno = 6;
                    }

                    if ((fieldno > 0) && (fieldno < 5)) {
                        if (line.length() > 2) {
                            fields[fieldno] += " " + line;
                        }
                    }
                    line = br.readLine();
                }
                if (!fields[0].equals("")) {
                    doc = createCacmDocument(fields[0], fields[1], fields[2], fields[3], fields[4]);
                    addDocumentToIndex(doc);
                }

            } finally {
                br.close();
            }
        } catch (Exception e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }


    }


}
