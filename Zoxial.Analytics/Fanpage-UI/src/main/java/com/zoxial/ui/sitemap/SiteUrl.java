package com.zoxial.ui.sitemap;

public class SiteUrl {
	
	private String loc;
	private String lastmod;
	private String changefreq;
	private String priority;
	
	
	public SiteUrl(String url, String lastmod, String changeFrecuency, String prio) {
		loc = url;
		this.lastmod = lastmod;
		changefreq = changeFrecuency;
		priority = prio;
	}
	
	public void setLastmod(String lastmod) {
		this.lastmod = lastmod;
	}
	public String getLastmod() {
		return lastmod;
	}
	public void setLoc(String loc) {
		this.loc = loc;
	}
	public String getLoc() {
		return loc;
	}
	public void setChangefreq(String changefreq) {
		this.changefreq = changefreq;
	}
	public String getChangefreq() {
		return changefreq;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getPriority() {
		return priority;
	}
	
	
	


}
