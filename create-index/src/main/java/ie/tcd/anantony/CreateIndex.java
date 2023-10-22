package ie.tcd.anantony;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class CreateIndex {

    private static final String FILE_PATH = "../cran/cran.all.1400";

    private static final String FILE_PATH_INDEX = "../index";
    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new EnglishAnalyzer();

        Directory directory = FSDirectory.open(Paths.get(FILE_PATH_INDEX));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter iwriter = new IndexWriter(directory, config);

        List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));
        Document doc = new Document();
        StringBuilder contentBuilder = new StringBuilder();
        
        for (String line : lines) {
            if (line.startsWith(".I")) {
                if (contentBuilder.length() > 0) {
                    doc.add(new TextField("content", contentBuilder.toString(), Field.Store.YES));
                    iwriter.addDocument(doc);
                    doc = new Document();
                    contentBuilder = new StringBuilder();
                }
            } else if (line.startsWith(".T") && line.length() > 3) {
                contentBuilder.append(line.substring(3).trim()).append(" ");
            } else if (line.startsWith(".A") && line.length() > 3) {
                doc.add(new StringField("author", line.substring(3).trim(), Field.Store.YES));
            } else if (line.startsWith(".B") && line.length() > 3) {
                // You can add this field similarly if you want to index the bibliography
            } else if (line.startsWith(".W") && line.length() > 3) {
                contentBuilder.append(line.substring(3).trim()).append(" ");
            } else {
                contentBuilder.append(line).append(" ");
            }
        }
        
        if (contentBuilder.length() > 0) {
            doc.add(new TextField("content", contentBuilder.toString(), Field.Store.YES));
            iwriter.addDocument(doc);
        }
        
        iwriter.close();
        directory.close();
    }
}
