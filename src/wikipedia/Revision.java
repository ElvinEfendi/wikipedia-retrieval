package wikipedia;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author elvin
 *
 */
public class Revision {
	private int id;
	private Date timestamp;
	private String text;
	
	public int getId() {
		return this.id;
	}
	public void setId(String value) {
		this.id = Integer.parseInt(value);
	}
	
	public Date getTimestamp() {
		return this.timestamp;
	}
	public void setTimestamp(String value) throws ParseException {
		this.timestamp = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'", Locale.ENGLISH).parse(value);
	}
	
	public String getText() {
		return this.text;
	}
	public void setText(String value) {
		this.text = value;
	}
	
	
}
