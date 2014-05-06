package wikipedia;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;
import java.io.File;
import java.io.IOException;

/**
 * @author elvin
 *
 */
public class Parser {
	private String dataFile;
	private Indexer indexer;

	public Parser(String dataFile, Indexer indexer) {
		this.dataFile = dataFile;
		this.indexer = indexer;
	}
	
	public void parse() throws IOException, SAXException {
		Digester digester = new Digester();

		// add current instance of Parse to
		// digester for handling
		digester.push(this);
		
		// instantiate Page class each time page tag seen
		digester.addObjectCreate("mediawiki/page", Page.class );
		// instantiate Revision class each time page tag seen
		digester.addObjectCreate("mediawiki/page/revision", Revision.class );
		
		// set different properties of Page instance using specified methods
        digester.addCallMethod("mediawiki/page/title", "setTitle", 0);
		// set different properties of Revision instance using specified methods
        digester.addCallMethod("mediawiki/page/revision/id", "setId", 0);
        digester.addCallMethod("mediawiki/page/revision/timestamp", "setTimestamp", 0);
        digester.addCallMethod("mediawiki/page/revision/text", "setText", 0);
        
        // add revisions of page
        digester.addSetNext("mediawiki/page/revision", "addRevision");
        // call 'addPage' method when the next 'mediawiki/page' pattern is seen
        digester.addSetNext("mediawiki/page", "addPage" );
        
        digester.parse(new File(dataFile));
	}
	
	/*
	 * preprocess the page
	 * and send for indexing
	 */
	public void addPage(Page page) throws IOException {
		indexer.add(page);
	}
}
