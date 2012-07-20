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

		Configuration configResource = ConfigResource.INSTANCE;
		String token = configResource.getString("facebook.token");
		int postFetcherPeriod = configResource
				.getInt("fetcher.facebook.post.period");
		
		// Metadata persistence actor
		Connection conn = null;
		ResultSet query = null;
		try {
			conn = FacebookPostFetcher.datasource.getConnection();
			query = conn.createStatement().executeQuery(
					"SELECT id FROM facebook_pages;");
			while (query.next()) {
				String facebook_page_id = query.getString("id");
				Properties p = new Properties();
				p.put("pageToFetch", facebook_page_id);
				p.put("token", token);
				system.scheduler().schedule(Duration.create(0, TimeUnit.HOURS),
						Duration.create(postFetcherPeriod, TimeUnit.HOURS),
						resourceFetcher, new Fetch(p));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(query);
			DbUtils.closeQuietly(conn);
		}

	}

}
