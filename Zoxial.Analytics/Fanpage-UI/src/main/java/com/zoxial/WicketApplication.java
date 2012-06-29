package com.zoxial;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.MountedMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zoxial.ui.Analytics;
import com.zoxial.ui.sitemap.SiteMap;

public class WicketApplication extends WebApplication {

	private static final Logger log = LoggerFactory.getLogger(WicketApplication.class);

	public Class getHomePage() {
		return Index.class;
	}

	@Override
	protected void init() {

		log.info("STARTING ZOXIAL.COM.....");
		
		this.mountPage("/" + Analytics.class.getSimpleName()/* + "/${chartid}"*/, Analytics.class);
		
		this.mountPage("/sitemap.xml", SiteMap.class);

		
		MountedMapper indexedParamUrlCodingStrategy = new MountedMapper("/" + Analytics.class.getSimpleName(), Analytics.class);
		this.mount(indexedParamUrlCodingStrategy);
	}
	
	@Override
	public RuntimeConfigurationType getConfigurationType() {
		return RuntimeConfigurationType.DEVELOPMENT;
	}

}