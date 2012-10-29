package com.zoxial.fanpage.spider;

import org.slf4j.LoggerFactory;

import akka.actor.UntypedActor;

public class ReportActor extends UntypedActor {

	private int newCounter = 0;
	private int modifiedCounter = 0;

	private static final org.slf4j.Logger log = LoggerFactory
			.getLogger(ReportActor.class);

	@Override
	public void onReceive(Object message) throws Exception {

		if ("new".equals(message)) {
			newCounter++;
		} else if ("modified".equals(message)) {
			modifiedCounter++;
		} else if ("report".equals(message)) {
			String r = "FACEBOOK FETCHER REPORT \n \n";
			r += "Number of new posts: " + newCounter + "\n";
			r += "Number of modified posts: " + modifiedCounter + "\n\n";

			newCounter = 0;
			modifiedCounter = 0;

			log.info("SENDING REPORT");
			CrawlerService.MAIL_ACTOR.tell(r);

		}

	}
}
