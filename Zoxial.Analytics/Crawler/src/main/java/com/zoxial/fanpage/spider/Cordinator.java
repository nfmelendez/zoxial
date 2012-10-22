package com.zoxial.fanpage.spider;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import ar.com.blog.melendez.asyncrestfb.messages.Fetch;

public class Cordinator extends UntypedActor {

	private static Logger log = Logger.getLogger(Cordinator.class);

	private final ActorRef resourceFetcher;

	public Cordinator(ActorRef resourceFetcher) {
		this.resourceFetcher = resourceFetcher;
	}

	@Override
	public void onReceive(Object arg0) throws Exception {

		if (arg0 instanceof StartFetch) {

			Configuration configResource = ConfigResource.INSTANCE;
			String token = configResource.getString("facebook.token");
			String firstTimePeriod = configResource
					.getString("fetcher.facebook.first.fetch.period");
			String fetchPeriod = configResource
					.getString("fetcher.facebook.period");

			// Metadata persistence actor
			Connection conn = null;
			ResultSet query = null;
			try {
				conn = FacebookPostFetcher.datasource.getConnection();
				query = conn
						.createStatement()
						.executeQuery(
								"SELECT f.id , (select count(*) from posts p where p.page_name = f.id)  as post_count  FROM facebook_pages f;");
				while (query.next()) {
					String facebook_page_id = query.getString("id");
					int postCount = query.getInt("post_count");
					Properties p = new Properties();
					p.put("pageToFetch", facebook_page_id);
					p.put("token", token);

					p.put("fetchLimit", fetchPeriod);

					resourceFetcher.tell(new Fetch(p));
					log.info(p);

				}

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				DbUtils.closeQuietly(query);
				DbUtils.closeQuietly(conn);
			}
		}

	}

	public boolean isFirstTimeFetchPost(int postCount) {
		return postCount == 0;
	}

}
