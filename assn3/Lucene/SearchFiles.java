/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// package org.apache.lucene.demo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;


/** simple command-line based search demo */
public class SearchFiles {

  private SearchFiles() {}

  /** simple command-line based search demo */
public static void main(String[] args) throws Exception{
    String usage =
      "Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir]";
    if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
      System.out.println(usage);
      System.exit(0);
    }

    String defaultField = "title";
    String index = "index";
    boolean raw = false;
    String queryString = null;
    int hitsPerPage = 10;
    
    for(int i = 0; i < args.length; i++) {
      if ("-index".equals(args[i])) {
        index = args[i+1];
        i++;
      } else if ("-raw".equals(args[i])) {
        raw = true;
      } else if ("-paging".equals(args[i])) {
        hitsPerPage = Integer.parseInt(args[i+1]);
        if (hitsPerPage <= 0) {
          System.err.println("There must be at least 1 hit per page.");
          System.exit(1);
        }
        i++;
      }
    }
    
    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
    IndexSearcher searcher = new IndexSearcher(reader);
    Analyzer analyzer = new StandardAnalyzer();
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
    
    
    while (true) {
    	
      queryString = getQuery();
      //System.out.println(queryString);
      String field = defaultField;
      
      System.out.println("Search in a specific field? Otherwise will use default field " + field + " (y,n)");
      String opt = in.readLine();
      if(opt.toLowerCase().equals("y"))
      {
        System.out.println("Please enter a specific field: ");
      	field = in.readLine().toLowerCase();
      }
      System.out.println(queryString);
      QueryParser parser = new QueryParser(field, analyzer);
      Query query = parseQuery(queryString, parser);
      
      System.out.println("Searching for: " + query.toString(field));
            
      doPagingSearch(in, searcher, query, hitsPerPage, raw, true);
      
      System.out.println("\nPerform another query? (y/n)");
      opt = in.readLine();
      if(opt.toLowerCase().equals("n"))
      {
        break;
      }
    }
    
    reader.close();
  
}
	
  /*
   * This function allows the user to provide a query type and returns the query they chose.
   */
	
	public static Query parseQuery(String queryString, QueryParser defParse) 
	{
		Query res = null;
		String query = "", contents = "";
		
		if(queryString.indexOf("{") == -1)
		{
			//System.out.println(queryString);
			query = queryString.substring(0, queryString.indexOf(";"));
			contents = queryString.substring(queryString.indexOf(";") + 1, queryString.indexOf(";;"));
		}
		else 
		{
			query = queryString.substring(0, queryString.indexOf("{"));
			contents = queryString.substring(queryString.indexOf("{") + 1, queryString.lastIndexOf("}"));
		}
		try
		{
			switch (query)
			{
			  	case "TermQuery":
			  	case "BooleanQuery":
			  	case "WildcardQuery":
			  	case "PhraseQuery":
			  	case "PrefixQuery":
			  	case "FuzzyQuery":
			  		res = defParse.parse(contents);
			  		return res;
			  	case "MultiPhraseQuery":
			  		res = null;
			  		return res;
			  	case "RegexpQuery":
			  		res = new RegexpQuery(new Term(field, contents));
			  		return res;
			  	case "TermRangeQuery":
			  		String [] terms2 = contents.toLowerCase().split(" ");
			  		res = TermRangeQuery.newStringRange(field, terms2[0] ,terms2[1] ,true,false);
			  		return res;
			  	case "DisjunctionMaxQuery":
			  		res = null;
			  		return res;
			  	case "MatchAllDocsQuery":
			  		res = null;
			  		return res;
			  	default:
			  		System.out.println("Error: Unable to parse query " + query);
			}
		}
		catch(Exception e)
		{
			System.out.println("Error: Unable to parse query " + query);
		}
		//This return only activates when a query cannot be parsed.
		return res;

	}

  /*
   * This function allows the user to provide a query type and returns the query they chose.
   */

  public static String getQuery() {
	  
	  String res = "";
	  int opt = -1;
	  Scanner kbd = new Scanner(System.in);
	  System.out.println("Please enter the integer for the type of query you want to run.");
	  System.out.println("Options are: 1. TermQuery, 2. BooleanQuery, 3. WildcardQuery, 4. PhraseQuery, 5. PrefixQuery, 6. MultiPhraseQuery, 7. FuzzyQuery, 8. RegexpQuery, 9. TermRangeQuery, 10. DisjunctionMaxQuery, 11. MatchAllDocsQuery.");
	  opt = kbd.nextInt();
	  
	  while(opt >= 1 || opt <= 11)
	  {
		  //Clears the scanner
		  kbd.nextLine();
		  String temp = "";
		  switch (opt)
		  {
		  	case 1:
		  		res = "TermQuery;";
		  		System.out.println("Please enter a single search term: ");
		  		res = res + kbd.next() + ";;";
		  		return res;
		  	case 2:
		  		res = "BooleanQuery;";
		  		System.out.println("Please enter a Boolean Query (Example: \"+data +computational -research: ):");
		  		res = res + kbd.nextLine() + ";;";
		  		return res;
		  	case 3:
		  		res = "WildcardQuery;";
		  		System.out.println("Please enter a Wildcard Query Term (Examples: Rep*, *ment, co*er): ");
		  		res = res + kbd.nextLine() + ";;";
		  		return res;
		  	case 4:
		  		res = "PhraseQuery;";
		  		System.out.println("Please enter a Phrase Query in quotes (Example: \"computational complexity\"): ");
		  		res = res + kbd.nextLine() + ";;";
		  		return res;
		  	case 5:
		  		res = "PrefixQuery;";
		  		System.out.println("Please enter a Prefix Query Term (Example: Rep*): ");
		  		res = res + kbd.nextLine() + ";;";
		  		return res;
		  	case 6:
		  		res = "MultiPhraseQuery;";
		  		System.out.println("Please enter a MultiPhrase Query in quotes (Example: Rep*): ");
		  		res = res + kbd.nextLine() + ";;";
		  		return res;
		  	case 7:
		  		res = "FuzzyQuery;";
		  		System.out.println("Please enter a Fuzzy Query (Example: Nature~): ");
		  		res = res + kbd.nextLine() + ";;";
		  		return res;
		  	case 8:
		  		res = "RegexpQuery;";
		  		System.out.println("Please enter a regular expression: ");
		  		res = res + kbd.nextLine() + ";;";
		  		return res;
		  	case 9:
		  		res = "TermRangeQuery;";
		  		System.out.println("Please enter two terms for a range (Example: nature nurture): ");
		  		res = res + kbd.nextLine() + ";;";
		  		return res;
		  	case 10:
		  		res = "DisjunctionMaxQuery{";
		  		System.out.println("Please enter the number of queries you want to perform:");
		  		int numQ = kbd.nextInt();
		  		for(int i = 0; i < numQ; i++)
		  		{
		  			System.out.println("Queries left: " + (numQ-i));
		  			res = res + getQuery();
		  		}
		  		res = res + "}";
		  		return res;
		  	case 11:
		  		res = "MatchAllDocsQuery;";
		  		System.out.println("Please enter a single search term: ");
		  		res = res + kbd.next() + ";;";
		  		return res;
		  	default:
		  		System.out.println("Error: Please choose a number representing one of the above query types.");
		  }
	  }
	  
	  
	  //Only returns here if severe error occurs.
	  return res;
  }
  
  /**
   * This demonstrates a typical paging search scenario, where the search engine presents 
   * pages of size n to the user. The user can then go to the next page if interested in
   * the next hits.
   * 
   * When the query is executed for the first time, then only enough results are collected
   * to fill 5 result pages. If the user wants to page beyond this limit, then the query
   * is executed another time and all hits are collected.
   * 
   */
  public static void doPagingSearch(BufferedReader in, IndexSearcher searcher, Query query, 
                                     int hitsPerPage, boolean raw, boolean interactive) throws IOException {
 
    // Collect enough docs to show 5 pages
    TopDocs results = searcher.search(query, 5 * hitsPerPage);
    ScoreDoc[] hits = results.scoreDocs;
    
    int numTotalHits = Math.toIntExact(results.totalHits);
    System.out.println(numTotalHits + " total matching documents");

    int start = 0;
    int end = Math.min(numTotalHits, hitsPerPage);
        
    while (true) {
      if (end > hits.length) {
        System.out.println("Only results 1 - " + hits.length +" of " + numTotalHits + " total matching documents collected.");
        System.out.println("Collect more (y/n) ?");
        String line = in.readLine();
        if (line.length() == 0 || line.charAt(0) == 'n') {
          break;
        }

        hits = searcher.search(query, numTotalHits).scoreDocs;
      }
      
      end = Math.min(hits.length, start + hitsPerPage);
      
      for (int i = start; i < end; i++) {
        if (raw) {                              // output raw format
          System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
          continue;
        }

        Document doc = searcher.doc(hits[i].doc);
        String bibkey = doc.get("bibkey");
        System.out.println((i+1) + ". " + bibkey);
                  
      }

      if (!interactive || end == 0) {
        break;
      }

      if (numTotalHits >= end) {
        boolean quit = false;
        while (true) {
          System.out.print("Press ");
          if (start - hitsPerPage >= 0) {
            System.out.print("(p)revious page, ");  
          }
          if (start + hitsPerPage < numTotalHits) {
            System.out.print("(n)ext page, ");
          }
          System.out.println("(q)uit or enter number to jump to a page.");
          
          String line = in.readLine();
          if (line.length() == 0 || line.charAt(0)=='q') {
            quit = true;
            break;
          }
          if (line.charAt(0) == 'p') {
            start = Math.max(0, start - hitsPerPage);
            break;
          } else if (line.charAt(0) == 'n') {
            if (start + hitsPerPage < numTotalHits) {
              start+=hitsPerPage;
            }
            break;
          } else {
            int page = Integer.parseInt(line);
            if ((page - 1) * hitsPerPage < numTotalHits) {
              start = (page - 1) * hitsPerPage;
              break;
            } else {
              System.out.println("No such page");
            }
          }
        }
        if (quit) break;
        end = Math.min(numTotalHits, start + hitsPerPage);
      }
    }
  }
}