package com.zoxial.fanpage.spider;

import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.RoundRobinRouter;
import akka.util.Duration;
import ar.com.blog.melendez.asyncrestfb.Main;


/**
 * Service for Crawling Facebook. It configs the akka Routers and Actors for
 * fetching information. And also configures the period of time the data from
 * facebook is retrived. Finally, this is the entry point of the crawler and
 * this class is configured in the pom.xml for execution, so the mvn exec:java
 * calls the main method of this class.
 * 
 * @author @nfmelendez - nfmelendez@gmail.com
 * 
 */
public class CrawlerService {

	public static ActorRef REPORT_ACTOR;
	public static ActorRef MAIL_ACTOR;

	/**
	 * The method is called from the mvn exec:java and that is configured is the
	 * pom.xml of this project. This is the entry point of the application.
	 * 
	 * @param args
	 *            - Currently it hasn't any argument.
	 */
	public static void main(String[] args) {
		ActorSystem system = Main.init();

		final ActorRef resourceFetcher = system.actorOf(new Props(
				new UntypedActorFactory() {
					public UntypedActor create() {
						return new FacebookPostFetcher();
					}
				}).withRouter(new RoundRobinRouter(5)),
				FacebookPostFetcher.class.getName());

		final ActorRef cordinator = system.actorOf(new Props(
				new UntypedActorFactory() {
					public UntypedActor create() {
						return new Cordinator(resourceFetcher);
					}
				}).withRouter(new RoundRobinRouter(1)), Cordinator.class
				.getName());

		REPORT_ACTOR = system.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new ReportActor();
			}
		}).withRouter(new RoundRobinRouter(1)),
				ReportActor.class.getSimpleName());

		MAIL_ACTOR = system
				.actorOf(new Props(new UntypedActorFactory() {
					public UntypedActor create() {
						return new MailActor();
					}
				}).withRouter(new RoundRobinRouter(1)),
						MailActor.class.getSimpleName());

		Configuration configResource = ConfigResource.INSTANCE;
		int postFetcherPeriod = configResource
				.getInt("fetcher.facebook.post.period");

		system.scheduler().schedule(Duration.create(0, TimeUnit.HOURS),
				Duration.create(postFetcherPeriod, TimeUnit.HOURS), cordinator,
				new StartFetch());

		system.scheduler().schedule(Duration.create(1, TimeUnit.MINUTES),
				Duration.create(4, TimeUnit.HOURS), REPORT_ACTOR, "report");

	}

}
