package wikipedia;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.xml.sax.SAXException;

/**
 * @author elvin
 * 
 */
public class Indexer {
	public static String indexPath;
	IndexWriter writer;

	// we do not need any object of this class from outside
	private Indexer() throws IOException {
		Directory dir = FSDirectory.open(new File(Indexer.indexPath));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_45);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_45,
				analyzer);

		iwc.setOpenMode(OpenMode.CREATE);

		// Optional: for better indexing performance, if you
		// are indexing many documents, increase the RAM
		// buffer. But if you do this, increase the max heap
		// size to the JVM (eg add -Xmx512m or -Xmx1g):
		//
		// iwc.setRAMBufferSizeMB(256.0);

		this.writer = new IndexWriter(dir, iwc);
	}

	// parse and index the data in given XML file
	public static void parseAndIndex(String dataFile)
			throws IOException, SAXException {
		Indexer indexer = new Indexer();
		Parser parser = new Parser(dataFile, indexer);
		parser.parse();

		// close index writer to finish indexing
		indexer.writer.close();
	}

	/*
	 * add/update page as a document for indexing if page has no revision then
	 * just index page title otherwise index {page title, revision id, revision
	 * text} for each revision
	 */
	public void add(Page page) throws IOException {
		Document doc = null;
		// initialize fields
		TextField contentField = new TextField("content", "", Store.YES);
		IntField idField = new IntField("revision_id", 0, Store.YES);
		TextField titleField = new TextField("title", "", Store.YES);

		float score = 0f;

		if (page.revisions.size() == 0) {
			// if there is no revision then index only title
			doc = new Document();

			titleField.setStringValue(page.getTitle());
			/* 
			 * title is more expressive than content
			 * and we give 50 because content is always below 50
			 */
			titleField.setBoost(50f);
			doc.add(titleField);

			// index the document
			writer.addDocument(doc);
		} else {
			// if there are revisions then add all of them one by one as a page
			// document
			// with additional fields
			for (Revision revision : page.revisions) {
				// initialize new document
				doc = new Document();

				titleField.setStringValue(page.getTitle());
				/* 
				 * title is more expressive than content
				 * and we give 50 because content is always below 50
				 */
				titleField.setBoost(50f);
				doc.add(titleField);

				idField.setIntValue(revision.getId());
				doc.add(idField);

				contentField.setStringValue(revision.getText());
				score = page.scoreRevision(revision);
				contentField.setBoost(score); // scoring
				doc.add(contentField);

				// index the document
				writer.addDocument(doc);
			} // end for
		}
	} // end method add
}
