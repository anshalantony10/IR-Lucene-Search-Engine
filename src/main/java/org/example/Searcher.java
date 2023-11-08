package org.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.example.model.QueryModel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Searcher {
    private static String INDEX_DIRECTORY = "index";
    private static int MAX_RESULTS = 1000;

    public static void search(List<QueryModel> queryModelList, String outputDirName, String outputFileName, Analyzer analyzer, Similarity similarity) throws IOException {
        System.out.println("Searching Queries...");
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        DirectoryReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        indexSearcher.setSimilarity(similarity);
        List<String> resFileContent = new ArrayList<>();
        HashMap<String, Float> boostedScores = new HashMap<String, Float>();
        boostedScores.put("Title", 0.25f);
        boostedScores.put("Author", 0.05f);
        boostedScores.put("Bibliography", 0.15f);
        boostedScores.put("Words", 0.55f);
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                new String[] {"Title", "Author", "Bibliography", "Words"},
                analyzer
            ,boostedScores
                );
        queryModelList.forEach(qm -> {
            try {
                parse(indexSearcher, queryParser, qm, resFileContent);
            } catch (ParseException | IOException e) {
                e.printStackTrace();
            }
        });
        File outputDir = new File(outputDirName);
        if (!outputDir.exists()) outputDir.mkdir();

        Files.write(Paths.get(outputDirName + "/"+ outputFileName), resFileContent, StandardCharsets.UTF_8);
        System.out.println("Results written to " + outputDirName + "/"+ outputFileName + "to be used in TREC Eval.");
        indexReader.close();
        directory.close();
    }
    private static void parse(IndexSearcher isearcher, QueryParser queryParser, QueryModel queryModel, List<String> resFileContent) throws ParseException, IOException {
        Query parse = queryParser.parse(QueryParser.escape(queryModel.getQueryString().trim()));
        ScoreDoc[] hits = isearcher.search(parse, MAX_RESULTS).scoreDocs;
        for (int i = 0; i < hits.length; i++) {
            Document hitDoc = isearcher.doc(hits[i].doc);
            String path = hitDoc.get("Id");
            if (path != null) {
                resFileContent.add(queryModel.getId() + " 0 " + hitDoc.get("Id") + " 0 " + hits[i].score + " STANDARD");
            }
        }
    }
}
