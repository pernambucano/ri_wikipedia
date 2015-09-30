package util;

import java.awt.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class Searcher {
	static String queryStr; 
	int version;
	public Searcher(String query, int version){this.queryStr = query; this.version = version;}
	
	public ArrayList<String>  SearchFiles() throws IOException, ParseException {
		String index = "index" + version;
		String field = "contents";
		String queries = null;
		int repeat = 0;
		boolean raw = false;
		String queryString =null;
		int hitsPerPage  = 10;

		
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer();
		
		BufferedReader in = null;
		
		if (queries != null ){
			in = Files.newBufferedReader(Paths.get(queries), StandardCharsets.UTF_8);
		} else {
			in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		}
		
		QueryParser parser = new QueryParser(field, analyzer);
		
		Query query = parser.parse(queryStr);
		System.out.println("Searching for: " + query.toString(field));
	
		searcher.search(query, 200);
		
//		printListSearch(searcher, query);
		
		TopDocs results = searcher.search(query, 10);
		ScoreDoc[] hits = results.scoreDocs;
		ArrayList<String> s_hits = new ArrayList<String>();
		for (int i = 0; i < hits.length; i++) {
			Document doc = searcher.doc(hits[i].doc);
			String path = doc.get("path");
			if(path != null) {
				s_hits.add(path);
			}
		}
		
		
		
		reader.close();
		
		return s_hits;
	}
	
	public static void printListSearch (IndexSearcher searcher, Query query) throws IOException {
		TopDocs results = searcher.search(query,10);
		ScoreDoc[] hits = results.scoreDocs;
		
		for (int i = 0; i < hits.length; i++) {
				
			Document doc =searcher.doc(hits[i].doc);
			String path = doc.get("path");
			if (path != null) {
				System.out.println((i+1) + "." + path);
				String title = doc.get("title");
				if (title != null ) {
					System.out.println("   Title: " + doc.get("title"));
				}
			} else {
				System.out.println((i+1) + "." + " No Path for this document");
			}
		}
		
	}
	
	
}
