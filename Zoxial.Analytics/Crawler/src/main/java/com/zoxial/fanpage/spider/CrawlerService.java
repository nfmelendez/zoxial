package com.zoxial.fanpage.spider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.RoundRobinRouter;
import akka.util.Duration;
import ar.com.blog.melendez.asyncrestfb.Main;
import ar.com.blog.melendez.asyncrestfb.messages.Fetch;

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

		Configuration configResource = ConfigResource.INSTANCE;
		int postFetcherPeriod = configResource
				.getInt("fetcher.facebook.post.period");

		system.scheduler().schedule(Duration.create(0, TimeUnit.HOURS),
				Duration.create(7, TimeUnit.SECONDS), cordinator,
				new StartFetch());

	}

}
