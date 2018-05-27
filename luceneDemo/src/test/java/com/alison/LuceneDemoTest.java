//package com.alison;
//
//import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.document.Field;
//import org.apache.lucene.document.TextField;
//import org.apache.lucene.index.DirectoryReader;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.index.IndexWriterConfig;
//import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
//import org.apache.lucene.queryparser.classic.QueryParser;
//import org.apache.lucene.search.*;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.util.Version;
//import org.junit.Test;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 测试lucene
// * 环境：
// * lucene 7.3.1
// * junit 4.12
// */
//public class LuceneDemoTest {
//
//    /**
//     * 建立索引
//     * 测试结果： 存储索引成功
//     *
//     * @throws Exception
//     */
//    @Test
//    public void createIndex() throws Exception {
//        LuceneDemoTest singleton = new LuceneDemoTest();
//        Analyzer analyzer = new Analyzer() {
//            @Override
//            protected TokenStreamComponents createComponents(String s) {
//                return null;
//            }
//        };
//
//        List<Item> items = new ArrayList<Item>();
//        items.add(new Item("1", "title", "hahaha"));
//        items.add(new Item("2", "revicer", "san.com"));
//        items.add(new Item("3", "fromer", "posbao"));
//        items.add(new Item("4", "content", "liuliu jia"));
//        items.add(new Item("5", "save", "我是中国人"));
//        // 索引存到内存中的目录
//        // Directory directory = new RAMDirectory();
//        // 索引存储到硬盘
//        File file = new File("E:\\lucene");
//        Directory directory = FSDirectory.open(file.toPath());
//        singleton.buildIndexer(analyzer, directory, items);
//    }
//
//    /**
//     * 搜索索引不成功，提示NullPointerExcetion
//     * 出错地址： searchIndex
//     *
//     * @throws Exception
//     */
//    @Test
//    public void search() throws Exception {
//        LuceneDemoTest singleton2 = new LuceneDemoTest();
//        Analyzer analyzer = new Analyzer() {
//            @Override
//            protected TokenStreamComponents createComponents(String s) {
//                return null;
//            }
//        };
//        File file = new File("E:\\lucene");
//        Directory directory = FSDirectory.open(file.toPath());
//        List<Item> results = singleton2.searchIndex(analyzer, directory, "中国");
//        for (Item item : results) {
//            System.out.printf("-----------" + item.toString());
//        }
//    }
//
//    /**
//     * 测试存储索引与读取keyword
//     * 结果：为报错，但是取不出数据
//     *
//     * @throws Exception
//     */
//    @Test
//    public void createAndSearch() throws Exception {
//        LuceneDemoTest singleton = new LuceneDemoTest();
//        Analyzer analyzer = new StandardAnalyzer();
//        List<Item> items = new ArrayList<Item>();
//        items.add(new Item("1", "first", "This is the text to be greatly indexed."));
//        items.add(new Item("2", "second", "This is great"));
//        items.add(new Item("3", "third", "I love apple and pear. "));
//        items.add(new Item("4", "four", "我是中国人"));
//        items.add(new Item("5", "five", "我叫何瑞"));
//
//        // 索引存到内存中的目录
//        //Directory directory = new RAMDirectory();
//        // 索引存储到硬盘
//        File file = new File("E:/lucene");
//        Directory directory = FSDirectory.open(file.toPath());
//        singleton.buildIndexer(analyzer, directory, items);
//        List<Item> result = singleton.searchIndex(analyzer, directory, "中国");
//
//        System.out.printf("----------------");
//        for (Item item : result) {
//            System.out.println(item.toString());
//        }
//    }
//
//    /**
//     * 首字母转大写
//     *
//     * @param str
//     * @return
//     */
//    public static String toFirstLetterUpperCase(String str) {
//        if (str == null || str.length() < 2) {
//            return str;
//        }
//        return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
//    }
//
//    /**
//     * 根据内容，构建索引
//     *
//     * @param analyzer
//     * @param directory
//     * @param items
//     * @return
//     */
//    private boolean buildIndexer(Analyzer analyzer, Directory directory, List<Item> items) {
//        IndexWriter indexWriter = null;
//        try {
//            // 配置索引
//            indexWriter = new IndexWriter(directory, new IndexWriterConfig(analyzer));
//            // 删除所有document
//            indexWriter.deleteAll();
//            // 将文档信息存入索引
//            Document[] document = new Document[items.size()];
//            for (int i = 0; i > items.size(); i++) {
//                document[i] = new Document();
//                Item item = items.get(i);
//                // 利用发射检索字段名
//                java.lang.reflect.Field[] fields = item.getClass().getDeclaredFields();
//                for (java.lang.reflect.Field field : fields) {
//                    String fieldName = field.getName();
//                    String getMethodName = "get" + toFirstLetterUpperCase(fieldName);
//                    Object obj = item.getClass().getMethod(getMethodName).invoke(item);
//                    document[i].add(new Field(fieldName, (String) obj, TextField.TYPE_STORED));
//                }
//                indexWriter.addDocument(document[i]);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        } finally {
//            try {
//                indexWriter.close();
//            } catch (IOException e) {
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 根据keyword搜索索引
//     *
//     * @param analyzer
//     * @param directory
//     * @param keyword
//     * @return
//     */
////    public List<Item> searchIndex(Analyzer analyzer, Directory directory, String keyword) {
////        DirectoryReader directoryReader = null;
////        List<Item> result = new ArrayList<Item>();
////        try {
////            // 设定搜索目录
////            directoryReader = DirectoryReader.open(directory);
////            IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
////
////            // 对多field进行搜索
////            // 取出所有的field的name
////            java.lang.reflect.Field[] fields = Item.class.getDeclaredFields();
////            String[] multiFields = new String[fields.length];
////            for (int i = 0; i < fields.length; i++) {
////                multiFields[i] = fields[i].getName();
////            }
////            MultiFieldQueryParser parser = new MultiFieldQueryParser(multiFields, analyzer);
////
////            // 设定具体的搜索词
////            Query query = parser.parse(keyword);
////            ScoreDoc[] hits = indexSearcher.search(query, 10, Sort.INDEXORDER).scoreDocs;
////
////            for (int i = 0; i < hits.length; i++) {
////                Document hitDoc = indexSearcher.doc(hits[i].doc);
////                Item item = new Item();
////                for (String field : multiFields) {
////                    String setMethodName = "set" + toFirstLetterUpperCase(field);
////                    item.getClass().getMethod(setMethodName, String.class).invoke(item, hitDoc.get(field));
////                }
////                result.add(item);
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////            return null;
////        } finally {
////            try {
////                directoryReader.close();
////            } catch (IOException e) {
////            }
////            return result;
////        }
////    }
//
//    /**
//     * javaBean
//     *
//     * @throws Exception
//     */
////    @Test
////    public void add() throws Exception {
////        Item item = new Item();
////        item.setId("1");
////        item.setTitle("Lucene全文检索");
////        item.setContent("Lucene是apache软件基金会4 jakarta项目组的一个子项目，是一个开放源代码");
////        final Path path = Paths.get("E:\\lucene\\1\\");
////        Directory directory = FSDirectory.open(path);
////        Analyzer analyzer = new StandardAnalyzer();
////        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
////        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
////        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
////        Document document = new Document();
////        document.add(new TextField("id", item.getId(), Field.Store.YES));
////        document.add(new TextField("title", item.getTitle(), Field.Store.YES));
////        document.add(new TextField("content", item.getContent(), Field.Store.YES));
////        indexWriter.addDocument(document);
////        indexWriter.close();
////    }
//
//    /**
//     * 添加文件
//     *
//     * @throws Exception
//     */
////    @Test
////    public void addFile() throws Exception {
////        final Path path = Paths.get("E:/lucene/2");
////
////        Directory directory = FSDirectory.open(path);
////        Analyzer analyzer = new StandardAnalyzer();
////
////        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
////        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
////
////        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
////        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File("E:/lucene/2018.log")));
////        String content = "";
////        while ((content = bufferedReader.readLine()) != null) {
////            System.out.println(content);
////            Document document = new Document();
////            document.add(new TextField("logs", content, Field.Store.YES));
////            indexWriter.addDocument(document);
////        }
////        indexWriter.close();
////    }
//
//
//    /**
//     * search查询
//     */
//    @Test
//    public void searchFiles() throws Exception {
//        String queryString = "lucene";
//        //多条件
////        Query q = MultiFieldQueryParser.parse(new String[]{},new String[]{},new StandardAnalyzer());
//
//        final Path path = Paths.get("E:/lucene/1");
//        Directory directory = FSDirectory.open(path);
//        Analyzer analyzer = new StandardAnalyzer();
//
//        IndexReader indexReader = DirectoryReader.open(directory);
//        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//
//        //单条件
//        QueryParser queryParser = new QueryParser("lucene", analyzer);
////        QueryParser queryParser = new QueryParser("logs", analyzer);
//        Query query = queryParser.parse(queryString);
//
//        TopDocs topDocs = indexSearcher.search(query, 10);
//
//        long conut = topDocs.totalHits;
//        System.out.println("检索总条数：" + conut);
//        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
//        for (ScoreDoc scoreDoc : scoreDocs) {
//            Document document = indexSearcher.doc(scoreDoc.doc);
//            System.out.println("相关度：" + scoreDoc.score + "-----time:" + document.get("time"));
//            System.out.println(document.get("lucene"));
//        }
//    }
//
//    @Test
//    public void createIndexDB() throws Exception {
//        //把数据填充到JavaBean对象中
//        User user = new User("1", "钟福成", "未来的程序员");
//        //创建Document对象【导入的是Lucene包下的Document对象】
//        Document document = new Document();
//        //将JavaBean对象所有的属性值，均放到Document对象中去，属性名可以和JavaBean相同或不同
//        /**
//         * 向Document对象加入一个字段
//         * 参数一：字段的关键字
//         * 参数二：字符的值
//         * 参数三：是否要存储到原始记录表中
//         *      YES表示是
//         *      NO表示否
//         * 参数四：是否需要将存储的数据拆分到词汇表中
//         *      ANALYZED表示拆分
//         *      NOT_ANALYZED表示不拆分
//         *
//         * */
//        document.add(new Field("id", user.getId(), Field.Store.YES, Field.Index.ANALYZED));
//        document.add(new Field("userName", user.getUserName(), Field.Store.YES, Field.Index.ANALYZED));
//        document.add(new Field("sal", user.getSal(), Field.Store.YES, Field.Index.ANALYZED));
//
//        //创建IndexWriter对象
//        //目录指定为E:/createIndexDB
//        Directory directory = FSDirectory.open(new File("E:/createIndexDB"));
//
//        //使用标准的分词算法对原始记录表进行拆分
//        Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
//
//        //LIMITED默认是1W个
//        IndexWriter.MaxFieldLength maxFieldLength = IndexWriter.MaxFieldLength.LIMITED;
//        /**
//         * IndexWriter将我们的document对象写到硬盘中
//         *
//         * 参数一：Directory d,写到硬盘中的目录路径是什么
//         * 参数二：Analyzer a, 以何种算法来对document中的原始记录表数据进行拆分成词汇表
//         * 参数三：MaxFieldLength mfl 最多将文本拆分出多少个词汇
//         *
//         * */
//        IndexWriter indexWriter = new IndexWriter(directory, analyzer, maxFieldLength);
//
//        //将Document对象通过IndexWriter对象写入索引库中
//        indexWriter.addDocument(document);
//
//        //关闭IndexWriter对象
//        indexWriter.close();
//
//    }
//
//
//}
