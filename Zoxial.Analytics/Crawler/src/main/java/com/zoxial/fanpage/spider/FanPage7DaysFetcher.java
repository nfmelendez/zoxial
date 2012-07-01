package com.zoxial.fanpage.spider;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import ar.com.blog.melendez.asyncrestfb.actor.FacebookFetchActor;

import com.restfb.Connection;
import com.restfb.FacebookClient;
import com.restfb.json.JsonObject;
import com.restfb.util.DateUtils;

public class FanPage7DaysFetcher extends FacebookFetchActor {

	private static Logger log = Logger.getLogger(FanPage7DaysFetcher.class);

	public static BasicDataSource datasource;

	static {
		BasicDataSource ds = new BasicDataSource();

		ds.setDriverClassName("com.mysql.jdbc.Driver");

		String host = ConfigResource.INSTANCE.getString("mysql.host");
		String user = ConfigResource.INSTANCE.getString("mysql.user");
		String pass = ConfigResource.INSTANCE.getString("mysql.pass");
		String database = ConfigResource.INSTANCE.getString("mysql.database");

		String url = "jdbc:mysql://" + host + "/" + database;
		ds.setUrl(url);
		ds.setUsername(user);
		ds.setPassword(pass);
		datasource = (BasicDataSource) ds;
	}

	public FanPage7DaysFetcher() {
	}

	@Override
	public void fetch(FacebookClient facebookClient, Properties p) {
		String pageToFetch = p.getProperty("pageToFetch");
		log.info("Start Crawling page id: " + pageToFetch);
		Connection<JsonObject> myFeed = facebookClient.fetchConnection(
				pageToFetch + "/feed", JsonObject.class);

		for (List<JsonObject> list : myFeed) {
			for (JsonObject post : list) {

				JsonObject obFrom = post.optJsonObject("from");
				String fromName = "";
				String fromId = "";
				if (null != obFrom) {
					fromName = obFrom.getString("name");
					fromId = obFrom.getString("id");
				}

				JsonObject obLikes = post.optJsonObject("likes");
				long likes = 0;
				if (null != obLikes) {
					likes = obLikes.optLong("count", 0);
				}

				JsonObject obcomments = post.optJsonObject("comments");
				long comments = 0;
				if (null != obcomments) {
					comments = obcomments.optLong("count", 0);
				}

				JsonObject obshares = post.optJsonObject("shares");
				long shares = 0;
				if (null != obshares) {
					shares = obshares.optLong("count", 0);
				}

				String message = post.optString("message", "No-Message");

				java.sql.Connection conn = null;
				try {
					conn = datasource.getConnection();

					String sql = "INSERT INTO posts (id,comments,likes,shares,from_id,from_name,page_name,created_time,message,engagement,raw_post)"
							+ " VALUES (?,?,?,?,?,?,?,?,?,(likes + shares + comments),?) ON DUPLICATE KEY UPDATE comments = ?, likes = ?, shares = ?, " +
							"message = ?,raw_post = ?, engagement = likes + shares + comments";

					PreparedStatement createStatement = conn
							.prepareStatement(sql);
					createStatement.setString(1, post.getString("id"));
					createStatement.setLong(2, comments);
					createStatement.setLong(3, likes);
					createStatement.setLong(4, shares);
					createStatement.setString(5, fromId);
					createStatement.setString(6, fromName);
					createStatement.setString(7, pageToFetch);

					Date dateFromLongFormat = DateUtils
							.toDateFromLongFormat(post
									.getString("created_time"));
					DateTime postTime = new DateTime(
							dateFromLongFormat.getTime());

					DateTime limit = new DateTime().plusDays(-7);

					if (postTime.isBefore(limit)) {
						log.info("Cut execution for 7 days limit for page: http://www.facebook.com/"
								+ pageToFetch);
						return;
					}

					createStatement.setTimestamp(8, new Timestamp(
							dateFromLongFormat.getTime()));
					createStatement.setString(9, message);
					createStatement.setString(10, post.toString());

					// on dulicated key update
					createStatement.setLong(11, comments);
					createStatement.setLong(12, likes);
					createStatement.setLong(13, shares);
					createStatement.setString(14, message);
					createStatement.setString(15, post.toString());

					createStatement.execute();

				} catch (SQLException e) {
					log.error(e);
				} finally {
					DbUtils.closeQuietly(conn);
				}

			}
		}

		log.info("Finish Crawling page id: " + pageToFetch);
	}

}
