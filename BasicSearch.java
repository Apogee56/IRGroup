/*
  This program illustrates term and Boolean queries

  Requires the path to an existing Lucene index 
  through a command line argument
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class BasicSearch {

  static final int MAX_DOCS = 10;

  public static void main(String[] args){
    // did the user provide correct number of command line arguments?
    // if not, print message and exit
    if (args.length != 1){
      System.err.println("Number of command line arguments must be 1");
      System.err.println("You have provided " + args.length + " command line arguments");
      System.err.println("Incorrect usage. Program terminated");
      System.err.println("Correct usage: java BasicSearch <path-to-lucene-index>");
      System.exit(1);
    }

    // extract directory name where the Lucene index is stored
    String indexDirName = args[0];
    System.out.println("Lucene index directory: " + indexDirName);

    // Term query
	private void UseTermQuery(String contents, String theSearch)throws IOException, ParseException 
	{
    	try 
		{
     		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirName)));
      		IndexSearcher searcher = new IndexSearcher(reader);
      		Analyzer analyzer = new StandardAnalyzer();

			//Do search
      		Term term = new Term(contents, theSearch);
      		Query query = new TermQuery(term);
      		TopDocs topDocs = searcher.search(query, MAX_DOCS);
      		System.out.println("\nTotal matches: " + topDocs.totalHits);
      		ScoreDoc[] resultSet = topDocs.scoreDocs;

			//Print results
      		int resultSetSize = Math.min(MAX_DOCS, Math.toIntExact(topDocs.totalHits));
      		System.out.println("\nResult set size (Term query): " + resultSetSize);

      		for (int i = 0; i < resultSetSize; i++)
			{
       	 		System.out.println("Document = " + resultSet[i].doc + "\t" + " Score=" + resultSet[i].score);
      		}	
      		reader.close();
    	}
    	catch(IOException e) 
		{
     		e.printStackTrace();
   	 	}
	}


    // Boolean query
	private void UseBooleanQuery(String theSearch1, String theSearch2)throws IOException, ParseException 
	{
    	try 
		{
      		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirName)));
      		IndexSearcher searcher = new IndexSearcher(reader);
      		Analyzer analyzer = new StandardAnalyzer();

      		String field = "contents";
      		QueryParser qParser = new QueryParser(field, analyzer);

      		// parse() method requires exception catch
      		try
			{
        		// alice AND rabbit, but NOT contemporary
        		Query query = qParser.parse("+alice +rabbit -contemporary");
        		TopDocs topDocs = searcher.search(query, MAX_DOCS);
        		System.out.println("\nTotal matches: " + topDocs.totalHits);
        		ScoreDoc[] resultSet = topDocs.scoreDocs;

        		int resultSetSize = Math.min(MAX_DOCS, Math.toIntExact(topDocs.totalHits));
        		System.out.println("\nResult set size (Boolean query - v1): " + resultSetSize);

        		for (int i = 0; i < resultSetSize; i++)
				{
          			System.out.println("Document = " + resultSet[i].doc + "\t" + " Score = " + resultSet[i].score);
        		}

        		// alice OR wonderland
        		query = qParser.parse("+alice OR wonderland");
        		topDocs = searcher.search(query, 10);
        		System.out.println("\nTotal matches: " + topDocs.totalHits);
        		resultSet = topDocs.scoreDocs;


        		resultSetSize = Math.min(MAX_DOCS, Math.toIntExact(topDocs.totalHits));
        		System.out.println("\nResult set size (Boolean query - v2): " + resultSetSize);

        		for (int i = 0; i < resultSetSize; i++)
				{
          			System.out.println("Document = " + resultSet[i].doc + "\t" + " Score=" + resultSet[i].score);
        		}
			}
      		catch (Exception e)
			{
        		e.printStackTrace();
      		}
    	}
    	catch(IOException e) 
		{
      		e.printStackTrace();
    	}
	}

	// WildCard query
	private void UseWildCardQuery(String contents, String theSearch)throws IOException, ParseException
	{ 
		try 
		{
    		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirName)));
    		IndexSearcher searcher = new IndexSearcher(reader);
	      	Analyzer analyzer = new StandardAnalyzer();

			Query query = new WildcardQuery(new Term(contents, "exampl*"));
			TopDocs topDocs = searcher.search(query, 10, Sort.INDEXORDER);
      	
      		System.out.println("Total matches: " + topDocs.totalHits);
      		//System.out.println("\nTotal matches: " + topDocs.totalHits);
	      	ScoreDoc[] resultSet = topDocs.scoreDocs;

    	  	int resultSetSize = Math.min(MAX_DOCS, Math.toIntExact(topDocs.totalHits));
      		System.out.println("\nResult set size (WildCard query): " + resultSetSize);

	      	for (int i = 0; i < resultSetSize; i++)
			{
    	  	  System.out.println("Document = " + resultSet[i].doc + "\t" + " Score=" + resultSet[i].score);
	      	}
			reader.close();
    	}
    	catch(IOException e) 
		{
      		e.printStackTrace();
    	}	
	}
	// Phrase query
	private void UsePhraseQuery(String contents, String theSearch)throws IOException, ParseException
	{
		try 
		{
      		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirName)));
      		IndexSearcher searcher = new IndexSearcher(reader);
      		Analyzer analyzer = new StandardAnalyzer();

			//Do search
    	  	PhraseQuery query = new PhraseQuery();
 			String[] words = theSearch.split(" ");
        	for (String word : words) 
			{
            	query.add(new Term(contents, word));
        	}

			//Print Results
	      	TopDocs topDocs = searcher.search(query, MAX_DOCS);
      		System.out.println("\nTotal matches: " + topDocs.totalHits);
	      	ScoreDoc[] resultSet = topDocs.scoreDocs;

    	  	int resultSetSize = Math.min(MAX_DOCS, Math.toIntExact(topDocs.totalHits));
      		System.out.println("\nResult set size (Phrase query): " + resultSetSize);

	      	for (int i = 0; i < resultSetSize; i++)
			{
        		System.out.println("Document = " + resultSet[i].doc + "\t" + " Score=" + resultSet[i].score);
	      	}
		   	reader.close();
		}
	    catch(IOException e) 
		{
      		e.printStackTrace();
	    }
	}

	
	// Prefix Query
	private void UseMultiPhraseQuery(String contents, String theSearch)throws IOException, ParseException
	{
		try 
		{
      		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirName)));
      		IndexSearcher searcher = new IndexSearcher(reader);
      		Analyzer analyzer = new StandardAnalyzer();


      		Term term = new Term(contents, searchString);
      		Query query = new TermQuery(term);
      		TopDocs topDocs = searcher.search(query, MAX_DOCS);
      		System.out.println("\nTotal matches: " + topDocs.totalHits);
      		ScoreDoc[] resultSet = topDocs.scoreDocs;

      		int resultSetSize = Math.min(MAX_DOCS, Math.toIntExact(topDocs.totalHits));
      		System.out.println("\nResult set size (Prefix query): " + resultSetSize);

      		for (int i = 0; i < resultSetSize; i++)
			{
        		System.out.println("Document = " + resultSet[i].doc + "\t" + " Score=" + resultSet[i].score);
      		}
			reader.close();
    	}
    	catch(IOException e) 
		{
      		e.printStackTrace();
    	}
	}

	// MultiPhrase query
	private void UseMultiPhraseQuery(Terms[] terms)throws IOException, ParseException
	{
		//Do search
		MultiPhraseQuery.Builder queryB = new MultiPhraseQuery.Builder();
		for(i = 0; i < terms.length; i++)
		{	
			queryB.add(terms[i])
		}
		TopDocs corpus = queryB.build();
		ScoreDoc[] resultSet = topDocs.scoreDocs;

		//print results
		int resultSetSize = Math.min(MAX_DOCS, Math.toIntExact(topDocs.totalHits));
      	System.out.println("\nResult set size (Phrase query): " + resultSetSize);

	    for (int i = 0; i < resultSetSize; i++)
		{
        	System.out.println("Document = " + resultSet[i].doc + "\t" + " Score=" + resultSet[i].score);
	    }
		reader.close();
   	}


	// MatchAllDocsQuery
	private void UseMatchAllDocsQuery(String theSearch)throws IOException, ParseException
	{
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDirName)));
      	IndexSearcher searcher = new IndexSearcher(reader);
      	Analyzer analyzer = new StandardAnalyzer();

		//Searching
		Query query = new MatchAllDocsQuery(theSearch);
		TopDocs corpus = searcher.search(query);
		ScoreDoc[] resultSet = topDocs.scoreDocs;

		//print results
    	int resultSetSize = Math.min(MAX_DOCS, Math.toIntExact(topDocs.totalHits));
      	System.out.println("\nResult set size (Phrase query): " + resultSetSize);

	    for (int i = 0; i < resultSetSize; i++)
		{
        	System.out.println("Document = " + resultSet[i].doc + "\t" + " Score=" + resultSet[i].score);
	    }
		reader.close();
	}


}