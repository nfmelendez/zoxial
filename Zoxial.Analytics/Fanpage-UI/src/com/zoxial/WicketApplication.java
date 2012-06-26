package com.zoxial;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WebApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WicketApplication extends WebApplication {

	private static final Logger log = LoggerFactory.getLogger(WicketApplication.class);

	public Class getHomePage() {
		return Index.class;
	}

	@Override
	protected void init() {

		log.info("STARTING ZOXIAL.COM.....");
		
		this.mountBookmarkablePage("/facebook", Facebook.class);
	}

	@Override
	public String getConfigurationType() {
		return Application.DEPLOYMENT;
	}

}