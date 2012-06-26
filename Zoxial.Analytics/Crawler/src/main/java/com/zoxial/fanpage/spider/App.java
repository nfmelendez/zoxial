package com.zoxial.fanpage.spider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) {
		ActorSystem system = Main.init();
		// Metadata persistence actor
		Connection conn = null;
		ResultSet query = null;
		try {
			conn = FanPage7DaysFetcher.datasource.getConnection();
			ResultSet count = conn.createStatement().executeQuery("SELECT count(*) AS total FROM charts;"); 
			count.next();
			int totalRouter = count.getInt("total");
		final ActorRef resourceFetcher = system.actorOf(new Props(
				new UntypedActorFactory() {
					public UntypedActor create() {
						return new FanPage7DaysFetcher();
					}
				}).withRouter(new RoundRobinRouter(totalRouter)), FanPage7DaysFetcher.class
				.getName());

		String token = "AAAFdWOPfKvEBADiIi1ZB7bLnxpnKBjKGlYn1wVIw1gBHlYYXbZBzp2p0jPy9FrWITiaIuezb5KHkoRFZB0fCMcb5862QrhKs9jE4AvvGgZDZD";
		
			query = conn.createStatement().executeQuery("SELECT facebook_page_id FROM charts;");
			while(query.next()){
				String facebook_page_id = query.getString("facebook_page_id");
				Properties p = new Properties();
				p.put("pageToFetch", facebook_page_id);
				p.put("token", token);
				system.scheduler().schedule(Duration.create(0, TimeUnit.MINUTES),
						Duration.create(1, TimeUnit.MINUTES), resourceFetcher, new Fetch(p));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(query);
			DbUtils.closeQuietly(conn);
		}
				
	}
}
