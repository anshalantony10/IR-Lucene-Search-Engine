package org.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.example.model.CranFieldDoc;
import org.example.model.QueryModel;
import org.example.parser.CranFieldDocParser;
import org.example.parser.CranQueryParser;

import java.io.IOException;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            System.out.println("Please Enter CMD Argument");
            return;
        }
        String indexPath = "index";
        String cranQueryDocPath = "cran/cran.qry";
        String cranDocPath = "cran/cran.all.1400";
        String outputDirName="output";
        String outputFileName="results.txt";
        Analyzer analyzer = new StandardAnalyzer();
        Similarity similarity = new BM25Similarity();
        boolean runIndex = false;
        boolean runQuery = false;

        for(int i=0;i<args.length;i++) {
            if ("-indexpath".equals(args[i])) {
                indexPath = args[i+1];
                runIndex = true;
                i++;
            } else if ("-docspath".equals(args[i])) {
                cranDocPath = args[i+1];
                i++;
            }
            else if ("-querypath".equals(args[i])) {
                cranQueryDocPath = args[i+1];
                runQuery = true;
                i++;
            }
            else if ("-outputdir".equals(args[i])) {
                outputDirName = args[i+1];
                i++;
            }
            else if ("-outputfilename".equals(args[i])) {
                outputFileName = args[i+1];
                i++;
            } else if("-analyzer".equals(args[i])) {
                analyzer = getAnalyzer(args[i+1]);
            }
            else if("-similarity".equals(args[i])) {
                similarity = getSimilarity(args[i+1]);
            }
        }
        if (runIndex) {
            List<CranFieldDoc> cranFieldDocs = CranFieldDocParser.readCranFieldDocs(cranDocPath);
            List<Document> documentList = DocumentCreator.createDocs(cranFieldDocs);
            Indexer.index(documentList, indexPath, false, analyzer);
        }
        if(runQuery) {
            List<QueryModel> queryModels = CranQueryParser.parseQueries(cranQueryDocPath);
            Searcher.search(queryModels, outputDirName, outputFileName, analyzer,similarity);
        }
    }

    private static Analyzer getAnalyzer(String arg) {
        switch (arg) {
            case "standard":  {
                System.out.println("Using Standard Analyzer...");
                return new StandardAnalyzer();
            }
            case "simple": {
                System.out.println("Using Simple Analyzer...");
                return new SimpleAnalyzer();
            }
            case "whitespace": {
                System.out.println("Using Whitespace Analyzer...");
                return new WhitespaceAnalyzer();
            }
            case "english": {
                System.out.println("Using English Analyzer...");
                return new EnglishAnalyzer();
            }
            case "stop": {
                System.out.println("Using Stop Analyzer...");
                return new StopAnalyzer();
            }
        }
        return new StandardAnalyzer+();
    }
    private static Similarity getSimilarity(String arg) {
        switch (arg) {
            case "classic": {
                System.out.println("Using Classic Similarity...");
                return new ClassicSimilarity();
            }
            case "bm25": {
                System.out.println("Using BM25 Similarity...");
                return new BM25Similarity();
            }
        }
        return new BM25Similarity();
    }
}
