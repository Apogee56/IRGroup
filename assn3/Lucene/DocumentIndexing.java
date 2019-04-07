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
import org.apache.lucene.document.IntPoint;
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
import java.io.*;

public class DocumentIndexing {

  // private constructor
  private DocumentIndexing() {}

  // max number of documents to retrieve
  static final int MAX_DOCS = 10;

  public static void main(String[] args){

    System.out.println("Correct usage: java DocumentIndexing [-index <path-to-lucene-index>] [-update]");

    //Parses the bibliography file location from the commandline
    File file = new File(args[args.length - 1]);
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

      
      BufferedReader br = new BufferedReader(new FileReader(file));
      // code here for processing one Bibliography record at a time
      Boolean flag = true, exitdoc = false;
      String current = "";
      while(flag)
      {
    	  //breaks the loop when nothing remains
    	  current = br.readLine();
    	  
    	  if (current == null || current.equals(""))
    	  {
    		  flag = false; continue;
    	  }
    	  //Getting into this if statement implies there is a whole valid bibtex bibliography to parse.
    	  if (current.charAt(0) == '@')
    	  {
    		  Document doc = new Document();
    		  String doctype = current.substring(1,current.indexOf("{"));
    		  String bibkey = current.substring(current.indexOf("{") + 1, current.lastIndexOf(","));
    		  doc.add(new StringField("bibkey", bibkey, Field.Store.YES));
    		  doc.add(new StringField("doctype", doctype, Field.Store.YES));
    		  
    		  //Here, the parsing of the nonkey fields is done by checking the curly braces on each line.
    		  while(!exitdoc)
    		  {		
    			  current = br.readLine();
    			  /*
    			  while(current != null)
    			  {
    				  System.out.println(current);
    				  current = br.readLine();
    			  }
    			  */
				  //System.out.println(current);
    			  // CASE 1 ==========  A normal field is detected (all on one line, start and end delimiters in proper places.
    			  if(current.indexOf("{") < current.indexOf("}") && current.indexOf("{") > -1)
    			  {
    				  System.out.println(current + "" + current.indexOf("{") + "" + current.indexOf("}"));
    				  String field = current.substring(1, current.indexOf(" = "));
    				  String contents = current.substring(current.indexOf("{") + 1, current.indexOf("}"));
    				  
    				  //A special case is needed to parse the author field, since that area must be parsed into multiple identical field entries.
    				  if(field.equalsIgnoreCase("author"))
    				  {
    					  String [] authors = contents.split(" and ");
    					  for(int i = 0; i < authors.length; i++)
    					  {
    						  String temp = authors[i];
    						  doc.add(new TextField("author", temp, Field.Store.YES));
    					  }
    				  }
    				  //A special case is given to parse the pages field, since that area can be parsed into two int entries.
    				  else if(field.equalsIgnoreCase("pages"))
    				  {
    					  String [] pages = contents.split("--");
    					  
    					  doc.add(new IntPoint("pages", Integer.parseInt(pages[0]), Integer.parseInt(pages[1])));
    					  
    				  }
    				  //A special case is needed to parse the keywords field, since that area must be parsed into multiple identical field entries.
    				  else if(field.equalsIgnoreCase("keywords"))
    				  {
    					  String [] keywords = contents.split(" ");
    					  for(int i = 0; i < keywords.length; i++)
    					  {
    						  String temp = keywords[i];
    						  doc.add(new TextField("keywords", temp, Field.Store.YES));
    					  }
    				  }
    				  //A general case that takes a field and stores it as text verbatim.
    				  else
    				  {
    					  doc.add(new TextField(field, contents, Field.Store.YES));
    				  }
    				  
    			  }
    			  // CASE 2 ==========  An abnormal field which ultimately has proper delimiters but is not stored entirely on one line.
    			  else if (current.indexOf("{") > -1 && current.indexOf("}") == -1)
    			  {
    				  String field = current.substring(1, current.indexOf(" = "));
    				  String holder = current.substring(current.indexOf("{") + 1);
    				  boolean endfield = false;
    				  
    				  //Collects the scattered contents of the field.
    				  while(!endfield)
    				  {
    					  current = br.readLine();
    					  //If the current line has an end bracket in it (in this if path), then we are ending the contents
    					  if(current.indexOf("}") > -1)
    					  {
    						  current = current.substring(0, current.indexOf("}"));
    						  holder = holder + current;
    						  endfield = true;
    					  }
    					  //If it does not contain the end bracket, then we must add the current line to our holder for the field.
    					  else
    					  {
    						  holder = holder + current;
    					  }
    				  }
    				  
    				  //Escaping the prior loop implies we now have both a field and a content of that field to add, however mangled it may be.
    				  //Adding it as a TextField
    				  
    				  doc.add(new TextField(field, holder, Field.Store.YES));
    				    
    			  }
    			  // CASE 3 ==========  A lone ending bracket is found on the line at the 0th index. This implies the end of a document.
    			  // No more fields to add, just writing this document to the index and exiting the document.
    			  // It's not an else statement because "There is more in Heaven and Earth, Horatio, than exists in your philosophy."
    			  else if (current.indexOf("}") == 0)
    			  {
    				  exitdoc = true;
    				  writer.addDocument(doc);
    			  }
    		  }
    		  
    	  }
    	  
      }

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
