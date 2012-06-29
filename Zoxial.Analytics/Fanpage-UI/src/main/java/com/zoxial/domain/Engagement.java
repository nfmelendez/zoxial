package com.zoxial.domain;

/**
 * Represents an engagement for a page. Now engagement is comments + likes +
 * shares.
 * 
 * @author @nfmelendez
 * 
 */
public class Engagement {

	/** The pagename, cannot be null. */
	private String pageName;
	/** The engagement can't be less than 0 */
	private long engagement;

	/**
	 * Constructor for engagement.
	 * 
	 * @param thePageName
	 *            The page name which is the engagement from. Can't be less than
	 *            0.
	 * @param theEngagement
	 *            The engagement for the page, cannot be null.
	 */
	public Engagement(String thePageName, long theEngagement) {
		this.pageName = thePageName;
		this.engagement = theEngagement;
	}

	public String getPageName() {
		return pageName;
	}

	public long getEngagement() {
		return engagement;
	}

}
