package com.alison;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class Searcher {
    IndexSearcher indexSearcher;
    QueryParser queryParser;
    Query query;

    public Searcher() {
    }

    public void initial(String indexDirectoryPath) throws IOException {
        Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath));
        indexSearcher = new IndexSearcher(indexDirectory);
        queryParser = new QueryParser(Version.LUCENE_36, LuceneDemoyibai.LuceneConstants.CONTENTS,
                new StandardAnalyzer(Version.LUCENE_36));
    }

    public TopDocs search(String searchQuery) throws IOException, ParseException {
        query = queryParser.parse(searchQuery);
        return indexSearcher.search(query, LuceneDemoyibai.LuceneConstants.MAX_SEARCH);
    }

    public TopDocs search(Query query) throws Exception {
        return indexSearcher.search(query, LuceneDemoyibai.LuceneConstants.MAX_SEARCH);
    }

    public Document getDocument(ScoreDoc scoreDoc) throws Exception {
        return indexSearcher.doc(scoreDoc.doc);
    }

    public void close() throws IOException {
        indexSearcher.close();
    }


    String indexDir = "E:\\lucene\\index";
    String dataDir = "E:\\lucene\\data";
    Searcher searcher;

    @Test
    public void test01() {
        Searcher searcher = new Searcher();
        try {
            long startTime = System.currentTimeMillis();
            searcher.initial(indexDir);
            TopDocs hits = searcher.search("alison");
            long endTime = System.currentTimeMillis();
            System.out.println(hits.totalHits + "  documents found Time : " + (endTime - startTime) + " ms");
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document document = searcher.getDocument(scoreDoc);
                System.out.println("File : " + document.get(LuceneDemoyibai.LuceneConstants.FILE_PATH));
            }
            searcher.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test02() {
        try {
            String searchQueryMin = "alison";
            String searchQueryMax = "tom";
            Searcher searcher = new Searcher();
            searcher.initial(indexDir);
            long startTime = System.currentTimeMillis();
            // create the term query object
            Query query = new TermRangeQuery(LuceneDemoyibai.LuceneConstants.FILE_NAME, searchQueryMin, searchQueryMax, true, false);
            // do the search
            TopDocs hits = searcher.search(query);
            long endTime = System.currentTimeMillis();
            System.out.println(hits.totalHits + " document found . Time " + (endTime - startTime) + " ms");
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.getDocument(scoreDoc);
                System.out.println("File: " + doc.get(LuceneDemoyibai.LuceneConstants.FILE_PATH));
            }
            searcher.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
