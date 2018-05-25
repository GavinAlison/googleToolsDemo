package com.alison;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 测试lucene
 * 环境：
 * lucene 7.3.1
 * junit 4.12
 */
public class LuceneDemoTest {

    /**
     * 建立索引
     *
     * @throws Exception
     */
    @Test
    public void createIndex() throws Exception {
        LuceneDemoTest singleton = new LuceneDemoTest();
        Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String s) {
                return null;
            }
        };

        List<Item> items = new ArrayList<Item>();
        items.add(new Item("1", "title", "hahaha"));
        items.add(new Item("2", "revicer", "san.com"));
        items.add(new Item("3", "fromer", "posbao"));
        items.add(new Item("4", "content", "liuliu jia"));
        items.add(new Item("5", "save", "我是中国人"));
        // 索引存到内存中的目录
        // Directory directory = new RAMDirectory();
        // 索引存储到硬盘
        File file = new File("E:\\lucene");
        Directory directory = FSDirectory.open(file.toPath());
        singleton.buildIndexer(analyzer, directory, items);
    }

    @Test
    public void search() throws Exception {
        LuceneDemoTest singleton2 = new LuceneDemoTest();
        Analyzer analyzer = new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String s) {
                return null;
            }
        };
        File file = new File("E:\\lucene");
        Directory directory = FSDirectory.open(file.toPath());
        List<Item> results = singleton2.searchIndex(analyzer, directory, "中国");
        for (Item item : results) {
            System.out.printf("-----------" + item.toString());
        }
    }

    /**
     * 首字母转大写
     *
     * @param str
     * @return
     */
    public static String toFirstLetterUpperCase(String str) {
        if (str == null || str.length() < 2) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
    }

    /**
     * 根据内容，构建索引
     *
     * @param analyzer
     * @param directory
     * @param items
     * @return
     */
    private boolean buildIndexer(Analyzer analyzer, Directory directory, List<Item> items) {
        IndexWriter indexWriter = null;
        try {
            // 配置索引
            indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));
            // 删除所有document
            indexWriter.deleteAll();
            // 将文档信息存入索引
            Document[] document = new Document[items.size()];
            for (int i = 0; i > items.size(); i++) {
                document[i] = new Document();
                Item item = items.get(i);
                // 利用发射检索字段名
                java.lang.reflect.Field[] fields = item.getClass().getDeclaredFields();
                for (java.lang.reflect.Field field : fields) {
                    String fieldName = field.getName();
                    String getMethodName = "get" + toFirstLetterUpperCase(fieldName);
                    Object obj = item.getClass().getMethod(getMethodName).invoke(item);
                    document[i].add(new Field(fieldName, (String) obj, TextField.TYPE_STORED));
                }
                indexWriter.addDocument(document[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                indexWriter.close();
            } catch (IOException e) {
            }
        }
        return true;
    }

    /**
     * 根据keyword搜索索引
     *
     * @param analyzer
     * @param directory
     * @param keyword
     * @return
     */
    public List<Item> searchIndex(Analyzer analyzer, Directory directory, String keyword) {
        DirectoryReader directoryReader = null;
        List<Item> result = new ArrayList<Item>();
        try {
            // 设定搜索目录
            directoryReader = DirectoryReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(directoryReader);

            // 对多field进行搜索
            // 取出所有的field的name
            java.lang.reflect.Field[] fields = Item.class.getDeclaredFields();
            String[] multiFields = new String[fields.length];
            for (int i = 0; i < fields.length; i++) {
                multiFields[i] = fields[i].getName();
            }
            MultiFieldQueryParser parser = new MultiFieldQueryParser(multiFields, analyzer);

            // 设定具体的搜索词
            Query query = parser.parse(keyword);
            ScoreDoc[] hits = indexSearcher.search(query, 10, Sort.INDEXORDER).scoreDocs;

            for (int i = 0; i < hits.length; i++) {
                Document hitDoc = indexSearcher.doc(hits[i].doc);
                Item item = new Item();
                for (String field : multiFields) {
                    String setMethodName = "set" + toFirstLetterUpperCase(field);
                    item.getClass().getMethod(setMethodName, String.class).invoke(item, hitDoc.get(field));
                }
                result.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                directoryReader.close();
            } catch (IOException e) {
            }
            return result;
        }
    }

}
