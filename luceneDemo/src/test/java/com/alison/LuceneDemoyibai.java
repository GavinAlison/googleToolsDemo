package com.alison;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;

public class LuceneDemoyibai {
    public class LuceneConstants {
        public static final String CONTENTS = "contents";
        public static final String FILE_NAME = "file_name";
        public static final String FILE_PATH = "filepath";
        public static final int MAX_SEARCH = 10;
    }

    public class TextFileFilter implements FileFilter {
        public boolean accept(File pathname) {
            return pathname.getName().toLowerCase().endsWith(".txt");
        }
    }

    public LuceneDemoyibai() {
    }

    private IndexWriter writer;

    public void initial(String indexDirectoryPath) throws IOException {
        //this directory will contain the indexes
        Directory indexDirectory =
                FSDirectory.open(new File(indexDirectoryPath));

        //create the indexer
        writer = new IndexWriter(indexDirectory,
                new StandardAnalyzer(Version.LUCENE_36), true,
                IndexWriter.MaxFieldLength.UNLIMITED);
    }

    public void close() throws CorruptIndexException, IOException {
        writer.close();
    }

    private Document getDocument(File file) throws IOException {
        Document document = new Document();

        //index file contents
        Field contentField = new Field(LuceneConstants.CONTENTS,
                new FileReader(file));
        //index file name
        Field fileNameField = new Field(LuceneConstants.FILE_NAME,
                file.getName(),
                Field.Store.YES, Field.Index.NOT_ANALYZED);
        //index file path
        Field filePathField = new Field(LuceneConstants.FILE_PATH,
                file.getCanonicalPath(),
                Field.Store.YES, Field.Index.NOT_ANALYZED);

        document.add(contentField);
        document.add(fileNameField);
        document.add(filePathField);

        return document;
    }

    private void indexFile(File file) throws IOException {
        System.out.println("Indexing " + file.getCanonicalPath());
        Document document = getDocument(file);
        // update document
//        writer.updateDocument(new Term(LuceneConstants.FILE_NAME, file.getName()), document);
        // add document
        writer.addDocument(document);
        // delete document
//        writer.deleteDocuments(new Term(LuceneConstants.FILE_NAME, file.getName()));
//        writer.commit();
    }

    public int createIndex(String dataDirPath, FileFilter filter)
            throws IOException {
        //get all files in the data directory
        File[] files = new File(dataDirPath).listFiles();

        for (File file : files) {
            if (!file.isDirectory()
                    && !file.isHidden()
                    && file.exists()
                    && file.canRead()
                    && filter.accept(file)
                    ) {
                indexFile(file);
            }
        }
        return writer.numDocs();
    }


    @Test
    public void test02() {
        String indexDir = "E:\\lucene\\index";
        String dataDir = "E:\\lucene\\data";
        LuceneDemoyibai indexer;
        try {
            indexer = new LuceneDemoyibai();
            indexer.initial(indexDir);
            int numIndexed;
            long startTime = System.currentTimeMillis();
            numIndexed = indexer.createIndex(dataDir, new TextFileFilter());
            long endTime = System.currentTimeMillis();
            indexer.close();
            System.out.println(numIndexed + " File indexed, time taken: "
                    + (endTime - startTime) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test01() {
        try {
            // create document
            File file = new File("");
            Document document = new Document();
            // index file contents
            Field contentField = new Field(LuceneConstants.CONTENTS, new FileReader(file));
            // index file name
            Field fileNameFiled = new Field(LuceneConstants.FILE_NAME, file.getName(), Field.Store.YES,
                    Field.Index.NOT_ANALYZED);
            // index file path
            Field filePathField = new Field(LuceneConstants.FILE_PATH, file.getCanonicalPath(), Field.Store.YES,
                    Field.Index.NOT_ANALYZED);
            document.add(contentField);
            document.add(fileNameFiled);
            document.add(filePathField);

            //-------------------------------
            // create indexWriter
            // this directory wile contain the indexes
            Directory indexDirectory = FSDirectory.open(new File(""));
            // create the indexer
            IndexWriter indexWriter = new IndexWriter(indexDirectory, new StandardAnalyzer(Version.LUCENE_36),
                    true, IndexWriter.MaxFieldLength.UNLIMITED);

            // index file
            System.out.println("Indexing ....");
            indexWriter.addDocument(document);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
