/*
  This program illustrates how to create a Lucene document
  and index it
*/

// import java.io.BufferedReader;
import java.io.IOException;
// import java.io.InputStream;
// import java.io.InputStreamReader;
// import java.nio.charset.StandardCharsets;
// import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
// import java.nio.file.SimpleFileVisitor;
// import java.nio.file.attribute.BasicFileAttributes;
// import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
// import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class DocumentIndexing {

  // private constructor
  private DocumentIndexing() {}

  // max number of documents to retrieve
  static final int MAX_DOCS = 10;

  public static void main(String[] args){

    System.out.println("Correct usage: java DocumentIndexing [-index <path-to-lucene-index>] [-update]");

    // default index path, if the user does not provide one
    String indexPath = "indexDir";

    // a new index will be created, unless user specified -update flag
    boolean create = true;

    for(int i = 0; i < args.length; i++) {
      if ("-index".equals(args[i])) {
        // user-specified path for index creation
        indexPath = args[i+1];
        i++;
      } else if ("-update".equals(args[i])) {
        // do not create a new index, instead update an existing one
        create = false;
      }
    }

    System.out.println("Lucene index directory: " + indexPath);
    if (!create)
      System.out.println("Existing Lucene index will be updated");
    else
      System.out.println("A new Lucene index will be created");

    // create a standard analyzer
    Analyzer analyzer = new StandardAnalyzer();

    // set IndexWriter configuration 
    IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

    if (create) {
        // Create a new index in the directory, removing any
        // previously indexed documents:
        iwc.setOpenMode(OpenMode.CREATE);
    } else {
        // Add new documents to an existing index:
        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
    }

    try{

      // directory for writing Lucene index
      Directory dir = FSDirectory.open(Paths.get(indexPath));

      IndexWriter writer = new IndexWriter(dir, iwc); 

      // code here for processing one Bibliography record at a time
      // using a loop 

      // assuming that values for various fields have been extracted  

      // create a Lucene document
      Document doc = new Document();  

      // add various fields
      // A TextField can be analyzed, whereas a StringField is not analyzed 

      doc.add(new StringField("bibkey", "Gudivada-2016-Renaissance-in-Database-Management-Navigating-the-Landscape-of-Candidate-Systems", Field.Store.YES));  

      // add authors
      doc.add(new TextField("author", "V. Gudivada", Field.Store.YES));
      doc.add(new TextField("author", "D. Rao", Field.Store.YES));
      doc.add(new TextField("author", "V. Raghavan", Field.Store.YES));

      // add title
      doc.add(new TextField("title", "Renaissance in Database Management: Navigating the Landscape of Candidate Systems", Field.Store.YES));

      // add journal
      doc.add(new TextField("journal", "IEEE Computer", Field.Store.YES));

      // add doi
      doc.add(new TextField("doi", "10.1109/MC.2016.115", Field.Store.YES));

      // add year
      doc.add(new TextField("year", "2016", Field.Store.YES));

      // add month
      doc.add(new TextField("month", "apr", Field.Store.YES));

      // add pages
      doc.add(new TextField("pages", "31 - 42", Field.Store.YES));

      // add volume
      doc.add(new TextField("volume", "49", Field.Store.YES));

      // add number
      doc.add(new TextField("volume", "4", Field.Store.YES));

      // add keyword
      doc.add(new TextField("keyword", "Data Management", Field.Store.YES));

      // add keyword
      doc.add(new TextField("keyword", "NoSQL", Field.Store.YES));

      // add keyword
      doc.add(new TextField("keyword", "Scalability", Field.Store.YES));

      // add abstract
      doc.add(new TextField("abstract", "The recent emergence of a new class of systems for data management has challenged the well-entrenched relational databases. These systems provide several choices for data management under the umbrella term NoSQL. Making a right choice is critical to building applications that meet business needs. Performance, scalability and cost are the principal business drivers for these new systems. By design, they do not provide all of the relational database features such as transactions, data integrity enforcement, and declarative query language. Instead, they primarily focus on providing near real-time reads and writes in the order of billions and millions, respectively. This paper provides a unified perspective, strengths and limitations, and use case scenarios for the new data management systems.", Field.Store.YES));


      // index the document
      writer.addDocument(doc);  

      writer.close();
    }
    catch(IOException e){
      System.out.println(" Caught a " + e.getClass() + "\n with message: " + e.getMessage());
    }


    // now, test the index
    // a term query
    try {
      IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
      IndexSearcher searcher = new IndexSearcher(reader);

      String field = "abstract";
      Term term = new Term(field, "databases");

      Query query = new TermQuery(term);

      TopDocs topDocs = searcher.search(query, MAX_DOCS);
      System.out.println("\nTotal matches: " + topDocs.totalHits);
      ScoreDoc[] resultSet = topDocs.scoreDocs;

      int resultSetSize = Math.min(MAX_DOCS, Math.toIntExact(topDocs.totalHits));
      System.out.println("\nResult set size (Term query): " + resultSetSize);

      for (int i = 0; i < resultSetSize; i++){
        System.out.println("Document = " + resultSet[i].doc + "\t" + " Score=" + resultSet[i].score);
      }

      reader.close();
    }
    catch(IOException e) {
      e.printStackTrace();
    }


  } // public static void main
} // public class DocumentIndexing
