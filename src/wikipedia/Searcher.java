package wikipedia;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * @author elvin
 *
 */
public class Searcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void showResultsFor(String queryString) throws IOException {
		IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(
				Indexer.indexPath)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);

		
		HashMap<String, Float> boosts = new HashMap<String, Float>();
		boosts.put("title", 40f);
		MultiFieldQueryParser parser = 
				new MultiFieldQueryParser(Version.LUCENE_45, new String[] {"title", "content"}, analyzer);
		Query query = null;
		try {
			query = parser.parse(queryString);
		} catch (ParseException e) {
			System.out.println("Can not parse entered query: " + queryString);
			return;
		}
		System.out.println("Searching for: " + query.toString(queryString));
		
		Date start = new Date();
		ScoreDoc[] hits = searcher.search(query, 100).scoreDocs;
		Date end = new Date();
		
		if (hits.length == 0) {
			System.out.println("No result found.");
		} else {
			System.out.println("Found Results: ");
			System.out.format("%90s%20s%n", "Page title", "Revision#");
			for (int i = 0; i < hits.length; i++) {
				Document doc = searcher.doc(hits[i].doc);
				System.out.format("%90s%20s%n", 
						doc.get("title"), 
						doc.get("revision_id"));
				System.out.println("Page url: http://en.wikipedia.org/wiki/" + doc.get("title").replace(" ", "_"));
				System.out.println();
			}
		}
		System.out.println(end.getTime() - start.getTime()
				+ " total milliseconds spent for searching.");
		System.out.println("=============================================================");
		System.out.println();
		
		reader.close();
	}
}
