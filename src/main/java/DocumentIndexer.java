/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Index all text files under a directory.
 * <p>
 * This is a command-line application demonstrating simple Lucene indexing.
 * Run it with no command-line arguments for usage information.
 */
public class DocumentIndexer {


    private IndexWriter writer;
    private Analyzer analyzer;


    public DocumentIndexer() {
    }


    public DocumentIndexer(String indexPath) throws IOException {
        writer = null;
        analyzer = CustomAnalyzer.builder()
                .withTokenizer("standard")
                .addTokenFilter("lowercase")
                .addTokenFilter("kstem")
                .build();

        createWriter(indexPath);
    }


    public void createWriter(String indexPath) {
        try {

            Directory dir = FSDirectory.open(Paths.get(indexPath));
            System.out.println("Indexing to directory '" + indexPath + "'...");

            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            //iwc.setSimilarity(new BM25Similarity());
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            writer = new IndexWriter(dir, iwc);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    public void addDocumentToIndex(Document doc) {
        try {
            writer.addDocument(doc);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void indexDocumentsFromFile(String filename) {
        /* to be implemented in sub classess*/
    }

    public void finished() {
        try {
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}


