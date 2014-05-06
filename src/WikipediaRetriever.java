import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.lucene.queryparser.classic.ParseException;
import org.xml.sax.SAXException;

import wikipedia.*;

/**
 * @author elvin
 * 
 */
public class WikipediaRetriever {

	/**
	 * @param args
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParseException
	 * @throws java.text.ParseException
	 */
	public static void main(String[] args) throws IOException, SAXException,
			ParseException, java.text.ParseException {

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in,
				"UTF-8"));
		String queryString;
		String dataFilePath;

		System.out.println("1) Index Wikipedia pages from xml data file");
		System.out.println("2) Search in indexed Wikpedia pages");
		System.out.print("Enter you choice(1 or 2) to start: ");

		// perform selected action
		if ("1".equals(in.readLine())) {
			System.out.print("Enter the path to XML data file: ");
			dataFilePath = in.readLine();
			File dataFile = new File(dataFilePath);
			while (!dataFile.exists()) {
				System.out.println("No such file exists: " + dataFilePath);
				System.out.print("Enter the path to XML data file: ");
				dataFilePath = in.readLine();
				dataFile = new File(dataFilePath);
			}
			// set index directory path
			Indexer.indexPath = dataFile.getName().replace(".xml",
					"_lucene_index");

			// page settings
			Page.upBoundaryForVeryOld = new SimpleDateFormat("yyyy-MM-dd",
					Locale.ENGLISH).parse("2006-01-01");
			Page.upBoundaryForOld = new SimpleDateFormat("yyyy-MM-dd",
					Locale.ENGLISH).parse("2009-01-01");

			System.out.println("Indexing...");
			// parse the data file and index pages
			Date start = new Date();
			Indexer.parseAndIndex(dataFilePath);
			Date end = new Date();

			System.out.println("Indexing is done in "
					+ (end.getTime() - start.getTime())
					+ " milliseconds. The index path is: "
					+ System.getProperty("user.dir")
					+ System.getProperty("file.separator") + Indexer.indexPath);
		} else {
			System.out.print("Enter the index path: ");
			Indexer.indexPath = in.readLine();

			while (true) {
				System.out.println("To search for phrase use double "
						+ "quotes and tilde operator. For example: \"information retrieval\"~6");
				System.out
						.print("Enter the query to search(type \"\\q\" to exit): ");
				queryString = in.readLine();
				if (queryString.equals("\\q")) {
					System.out.println("No searching is performed.");
					break;
				} else if (queryString.length() > 0) {
					Searcher.showResultsFor(queryString);
				}
			}
		}
	}

}
