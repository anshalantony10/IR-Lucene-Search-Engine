package org.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class Indexer {

    public static boolean index(List<Document> documentList, String indexPath, boolean update, Analyzer analyzer) throws IOException {
        Directory dir = getDirectory(indexPath);
        IndexWriter writer = getIndexWriter(update, dir, analyzer);
        indexDocs(documentList, writer);
        writer.close();
        dir.close();
        return true;
    }

    private static void indexDocs(List<Document> documentList, IndexWriter writer) {
        if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
            System.out.println("Indexing Documents");
            documentList.forEach(doc -> {
                try {
                    writer.addDocument(doc);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            System.out.println("Done Indexing...");
        } else {
            System.out.println("updating...");
            documentList.forEach(doc -> {
                try {
                    writer.addDocument(doc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static Directory getDirectory(String indexPath) throws IOException {
        System.out.println("Indexing to directory '" + indexPath + "'...");
        return FSDirectory.open(Paths.get(indexPath));
    }

    private static IndexWriter getIndexWriter(boolean update, Directory dir, Analyzer analyzer) throws IOException {
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(update ? IndexWriterConfig.OpenMode.CREATE_OR_APPEND : IndexWriterConfig.OpenMode.CREATE);
        return new IndexWriter(dir, indexWriterConfig);
    }
}
