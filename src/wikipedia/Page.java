package wikipedia;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author elvin
 *
 * defines a Wikipedia page
 */
public class Page {
	public static Date upBoundaryForVeryOld = null;
	public static Date upBoundaryForOld = null;
	private String title;
	public List<Revision> revisions = new ArrayList<Revision>();
	
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return this.title;
	}

	// add revision object to the list
	public void addRevision(Revision revision) {
		this.revisions.add(revision);
	}
	
	/*
	 *  rank the revision according to its timestamp
	 *  if timestamp of the revision is newer then it
	 *  will have bigger score/boost 
	 *  1893452400000 is a fixed date in the future
	 */
	public float scoreRevision(Revision revision) {
		float score = revision.getTimestamp().getTime() / 1893452400000f;
		
		if (revision.getTimestamp().before(Page.upBoundaryForVeryOld)) {
			score *= 1f; // very old revision
		} else if (revision.getTimestamp().before(Page.upBoundaryForOld)) {
			score *= 5f; // old revision
		} else {
			score *= 25f; // recent revision
		}
		
		return  score;
	}
}
