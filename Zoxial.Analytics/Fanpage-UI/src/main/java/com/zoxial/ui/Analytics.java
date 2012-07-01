package com.zoxial.ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;
import org.json.JSONArray;

import com.zoxial.application.ConfigResource;
import com.zoxial.domain.Engagement;

public class Analytics extends WebPage {
	private static final long serialVersionUID = 1L;

	private static BasicDataSource datasource;

	public static Map SITEMAP = new HashMap();

	/** The default date pattern that must be used when we show dates. */
	private static String DATE_PATTERN = "dd/MM/yyyy";

	static {
		BasicDataSource ds = new BasicDataSource();

		ds.setDriverClassName("com.mysql.jdbc.Driver");

		Configuration configResource = ConfigResource.INSTANCE;
		String host = configResource.getString("mysql.host");
		String user = configResource.getString("mysql.user");
		String pass = configResource.getString("mysql.pass");
		String database = configResource.getString("mysql.database");

		String url = "jdbc:mysql://" + host + "/" + database;
		ds.setUrl(url);
		ds.setUsername(user);
		ds.setPassword(pass);
		datasource = (BasicDataSource) ds;
	}

	public Analytics(final PageParameters parameters) {
		int indexedCount = parameters.getIndexedCount();

		String chartId = "";
		DateTime toDate = null;
		DateTime fromDate = null;
		if (indexedCount == 0) {
			chartId = "celularesArgentina";
			toDate = new DateTime();
			fromDate = toDate.minusWeeks(1);
		} else if (indexedCount == 1) {
			chartId = parameters.get(0).toString();
			toDate = new DateTime();
			fromDate = toDate.minusWeeks(1);
		} else if (indexedCount == 3) {
			chartId = parameters.get(0).toString();
			String from = parameters.get(1).toString();
			String to = parameters.get(2).toString();
			toDate = new DateTime(Long.valueOf(to));
			fromDate = new DateTime(Long.valueOf(from));
		} else {
			throw new RuntimeException("Error in parameters");
		}

		Connection conn = null;
		PreparedStatement engagementWinnerQuery = null;
		ResultSet executeQueryForWinner = null;
		Statement statementForQueryChart = null;
		ResultSet executeQueryForCharts = null;
		ResultSet postsIterator = null;
		this.buildChartSelection(chartId);
		this.createNavigatorLink(chartId, toDate, fromDate);

		String currentDate = fromDate.toString(DATE_PATTERN) + " - "
				+ toDate.toString(DATE_PATTERN);
		this.add(new Label("currentdate", currentDate));
		try {
			conn = datasource.getConnection();
			String sql = "select page_name, sum(engagement) AS sumEngagement, "
					+ "(SELECT charts.page_name FROM charts where `facebook_page_id` = posts.page_name) AS fbname "
					+ "from posts where from_id IN ( SELECT facebook_page_id from charts where chart_id = ? ) "
					+ "AND created_time BETWEEN ? AND ? group by page_name order by sumEngagement DESC";
			engagementWinnerQuery = conn.prepareStatement(sql);
			engagementWinnerQuery.setString(1, chartId);
			engagementWinnerQuery.setTimestamp(2, new Timestamp(fromDate
					.toDate().getTime()));
			engagementWinnerQuery.setTimestamp(3, new Timestamp(toDate.toDate()
					.getTime()));
			executeQueryForWinner = engagementWinnerQuery.executeQuery();

			ArrayList<Engagement> engagements = new ArrayList<Engagement>();

			while (executeQueryForWinner.next()) {
				String pageName = executeQueryForWinner.getString("fbname");
				long engagement = executeQueryForWinner
						.getLong("sumEngagement");
				Engagement theEngagement = new Engagement(pageName, engagement);
				engagements.add(theEngagement);
			}

			builBarChart(engagements);

			this.add(new ListView<Engagement>("viewer", engagements) {

				@Override
				protected void populateItem(ListItem<Engagement> item) {
					Engagement model = item.getModelObject();
					item.add(new Label("pagename", model.getPageName()));
					item.add(new Label("engagement", String.valueOf(model
							.getEngagement())));

				}
			});

			statementForQueryChart = conn.createStatement();

			executeQueryForCharts = statementForQueryChart
					.executeQuery("SELECT facebook_page_id,page_name, description FROM charts WHERE chart_id = '"
							+ chartId + "'");
			final HashMap chartItem = new HashMap();
			String description = "";
			while (executeQueryForCharts.next()) {
				String facebookId = executeQueryForCharts
						.getString("facebook_page_id");
				description = executeQueryForCharts.getString("description");
				String pageName = executeQueryForCharts.getString("page_name");

				ArrayList<Properties> postsForChartItem = new ArrayList<Properties>();

				String askMostInfluentialPosts = "select id,created_time,shares,likes,comments,engagement,message from posts where page_name = "
						+ "? AND created_time BETWEEN ? AND ? order by engagement DESC LIMIT 5";
				PreparedStatement askMostInfl = conn
						.prepareStatement(askMostInfluentialPosts);
				askMostInfl.setString(1, facebookId);
				askMostInfl.setTimestamp(2, new Timestamp(fromDate.toDate()
						.getTime()));
				askMostInfl.setTimestamp(3, new Timestamp(toDate.toDate()
						.getTime()));

				postsIterator = askMostInfl.executeQuery();
				while (postsIterator.next()) {
					Properties item = new Properties();
					item.put("id", postsIterator.getString("id"));
					item.put("created_time",
							postsIterator.getDate("created_time"));
					item.put("shares", postsIterator.getString("shares"));
					item.put("likes", postsIterator.getString("likes"));
					item.put("comments", postsIterator.getString("comments"));
					item.put("engagement",
							postsIterator.getString("engagement"));
					item.put("message", postsIterator.getString("message"));
					postsForChartItem.add(item);
				}
				chartItem.put(pageName, postsForChartItem);
			}

			this.add(new Label("description", description));
			this.add(new Label("title", description));

			this.add(new ListView<String>("chartitems", new ArrayList(chartItem
					.keySet())) {

				@Override
				protected void populateItem(ListItem<String> item) {
					String itemName = item.getModelObject();
					item.add(new Label("itemName", itemName));

					List<Properties> l = (List<Properties>) chartItem
							.get(itemName);
					item.add(new ListView<Properties>("items", l) {
						private int i = 1;

						@Override
						protected void populateItem(ListItem<Properties> item) {
							Properties p = item.getModelObject();
							item.add(new Label("engagement", p
									.getProperty("engagement")));
							String id = p.getProperty("id");
							String[] split = id.split("_");
							String link = "<a href=\"https://www.facebook.com/"
									+ split[0] + "/posts/" + split[1]
									+ "\">link</a>";
							Label linkLabel = new Label("link", link);

							linkLabel.setEscapeModelStrings(false);
							item.add(linkLabel);

							item.add(new Label("message", p
									.getProperty("message")));

							Date createdTime = (Date) p.get("created_time");
							SimpleDateFormat sdf = new SimpleDateFormat(
									DATE_PATTERN);
							String createdTimeStr = sdf.format(createdTime);
							item.add(new Label("date", createdTimeStr));
							item.add(new Label("index", "" + i));
							i++;
						}
					});

				}
			});

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(executeQueryForWinner);
			DbUtils.closeQuietly(engagementWinnerQuery);
			DbUtils.closeQuietly(statementForQueryChart);
			DbUtils.closeQuietly(executeQueryForCharts);
			DbUtils.closeQuietly(postsIterator);
			DbUtils.closeQuietly(conn);
		}

		String fullUrl = "http://zoxial.com/Analytics/" + chartId + "/"
				+ fromDate.getMillis() + "/" + toDate.getMillis();
		Label label = new Label("text", fullUrl);
		ExternalLink shareLink = new ExternalLink("sharelink", fullUrl);
		shareLink.add(label);
		this.add(shareLink);
	}

	/**
	 * Builds the bar chart using the engagements retrived from the repository.
	 * It sort the list by name, to have always the chart in the same position
	 * with the same color.
	 * 
	 * @param engagements
	 *            The engagement retrived from repository, cannot be null.
	 */
	public void builBarChart(ArrayList<Engagement> engagements) {
		List<Engagement> engagemntsForChart = new ArrayList<Engagement>(
				engagements);

		JSONArray namesChartJS = new JSONArray();
		namesChartJS.put("");
		JSONArray valuesChartJS = new JSONArray();
		valuesChartJS.put("");
		Collections.sort(engagemntsForChart, new Comparator<Engagement>() {

			public int compare(Engagement o1, Engagement o2) {
				return o1.getPageName().compareTo(o2.getPageName());
			}
		});

		for (Engagement e : engagemntsForChart) {
			namesChartJS.put(e.getPageName());
			valuesChartJS.put(e.getEngagement());
		}

		JSONArray compose = new JSONArray();
		compose.put(namesChartJS);
		compose.put(valuesChartJS);
		Label label = new Label(
				"za",
				"var arr = "
						+ compose.toString()
						+ "; var chartName = 'The highest bar shows the facebook page that is having the best performance.';");
		label.setEscapeModelStrings(false);
		this.add(label);
	}

	private void createNavigatorLink(String chartId, DateTime toDate,
			DateTime fromDate) {
		DateTime date = toDate;
		DateTime endPreviousWeek = date.minusDays(date.getDayOfWeek());
		DateTime startOfPreviousWeek = endPreviousWeek.minusDays(6);

		String lastWeekUrl = calculateCurrentHostUrl() + "/" + chartId + "/"
				+ startOfPreviousWeek.getMillis() + "/"
				+ endPreviousWeek.getMillis();
		this.add(new ExternalLink("lastweek", lastWeekUrl));

		DateTime startOfNextWeek = startOfPreviousWeek.plusWeeks(2);
		DateTime endOfNextWeek = endPreviousWeek.plusWeeks(2);
		String nextWeekUrl = calculateCurrentHostUrl() + "/" + chartId + "/"
				+ startOfNextWeek.getMillis() + "/" + endOfNextWeek.getMillis();
		this.add(new ExternalLink("nextweek", nextWeekUrl));
		String lastDays = calculateCurrentHostUrl() + "/" + chartId;
		this.add(new ExternalLink("lastdays", lastDays));

	}

	private void buildChartSelection(final String chartIdSelected) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet executeQuery = null;
		final HashMap<String, String> map = new HashMap<String, String>();
		try {
			conn = datasource.getConnection();
			stmt = conn.createStatement();
			executeQuery = stmt
					.executeQuery("SELECT DISTINCT `chart_id`, `chart_name` from charts;");
			while (executeQuery.next()) {
				String chartId = executeQuery.getString("chart_id");
				String descrption = executeQuery.getString("chart_name");
				map.put(chartId, descrption);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtils.closeQuietly(executeQuery);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(conn);
		}

		this.add(new ListView<String>("chartselector", new ArrayList(map
				.keySet())) {

			@Override
			protected void populateItem(ListItem<String> item) {
				String id = item.getDefaultModelObjectAsString();

				if (chartIdSelected.equals(id)) {
					item.add(new AttributeAppender("class", new Model("active")));
				}

				String renderFullUrl = calculateCurrentHostUrl();

				String chartName = map.get(id);
				Label label = new Label("chartname", chartName);
				ExternalLink bookmarkablePageLink = new ExternalLink("link",
						renderFullUrl + "/" + id);
				bookmarkablePageLink.add(label);
				item.add(bookmarkablePageLink);
			}

		});

		SITEMAP = new HashMap(map);
	}

	public String calculateCurrentHostUrl() {
		String urlHost = RequestCycle
				.get()
				.getUrlRenderer()
				.renderFullUrl(
						Url.parse(urlFor(Analytics.class, null).toString()));
		return urlHost;
	}

}
