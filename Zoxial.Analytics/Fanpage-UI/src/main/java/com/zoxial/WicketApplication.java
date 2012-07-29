package com.zoxial;

import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.MountedMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zoxial.application.ConfigResource;
import com.zoxial.ui.AddPageToRetrivePosts;
import com.zoxial.ui.Analytics;
import com.zoxial.ui.sitemap.SiteMap;

public class WicketApplication extends WebApplication {

	private static final Logger log = LoggerFactory
			.getLogger(WicketApplication.class);

	public Class getHomePage() {
		return Moodle.class;
	}

	@Override
	protected void init() {

		log.info("STARTING ZOXIAL.COM.....");

		// this.mountPage(
		// "/",
		// Moodle.class);

		this.mountPage("/sitemap.xml", SiteMap.class);

		MountedMapper indexedParamUrlCodingStrategy = new MountedMapper("/"
				+ Analytics.class.getSimpleName(), Analytics.class);
		this.mount(indexedParamUrlCodingStrategy);

		MountedMapper addPage = new MountedMapper("/Analytics/AddPage",
				AddPageToRetrivePosts.class);
		this.mount(addPage);
	}

	@Override
	public RuntimeConfigurationType getConfigurationType() {
		boolean isDeployMode = ConfigResource.INSTANCE
				.getBoolean("application.mode.deploy");
		if (isDeployMode) {

			return RuntimeConfigurationType.DEPLOYMENT;
		} else {
			return RuntimeConfigurationType.DEVELOPMENT;

		}
	}

}