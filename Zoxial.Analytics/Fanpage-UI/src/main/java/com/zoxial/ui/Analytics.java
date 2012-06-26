package com.zoxial.ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;
import org.joda.time.DateTime;
import org.json.JSONArray;

import com.restfb.util.DateUtils;

public class Analytics extends WebPage {
	private static final long serialVersionUID = 1L;

	private static BasicDataSource datasource;
	
	public static Map SITEMAP = new HashMap();

	static {
		BasicDataSource ds = new BasicDataSource();

		ds.setDriverClassName("com.mysql.jdbc.Driver");

		String host = "127.0.0.1";

		String url = "jdbc:mysql://" + host + "/fanpagespider";
		ds.setUrl(url);
		String user = "root";
		ds.setUsername(user);
		ds.setPassword("280884");
		datasource = (BasicDataSource) ds;
	}

	public Analytics(final PageParameters parameters) {
		int indexedCount = parameters.getIndexedCount();
		
		String chartId = "";
		DateTime now = null;
		DateTime lastWeek = null;
		if(indexedCount == 0){
			chartId = "celularesArgentina";
		    now = new DateTime();
			lastWeek = now.minusWeeks(1);			
		} else if(indexedCount == 1){
			chartId = parameters.get(0).toString();
			now = new DateTime();
			lastWeek = now.minusWeeks(1);
		} else if (indexedCount == 3){
			chartId = parameters.get(0).toString();
			String from = parameters.get(1).toString();
			String to = parameters.get(2).toString();
			now = new DateTime(Long.valueOf(to));
			lastWeek = new DateTime(Long.valueOf(from));
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
		try {
			conn = datasource.getConnection();
			String sql = "select page_name, sum(engagement) AS sumEngagement, " +
					"(SELECT charts.page_name FROM charts where `facebook_page_id` = posts.page_name) AS fbname " +
					"from posts where from_id IN ( SELECT facebook_page_id from charts where chart_id = ? ) " +
							"AND created_time BETWEEN ? AND ? group by page_name order by sumEngagement DESC";
			engagementWinnerQuery = conn.prepareStatement(sql);
			engagementWinnerQuery.setString(1, chartId);
			engagementWinnerQuery.setTimestamp(2, new Timestamp(lastWeek.toDate().getTime()));
			engagementWinnerQuery.setTimestamp(3, new Timestamp(now.toDate().getTime()));
			executeQueryForWinner = engagementWinnerQuery.executeQuery();
			
			ArrayList<Properties> arrayList = new ArrayList<Properties>();

			JSONArray namesChartJS = new JSONArray();
			namesChartJS.put("");
			JSONArray valuesChartJS = new JSONArray();
			valuesChartJS.put("");
			while (executeQueryForWinner.next()) {
				Properties jsonObject = new Properties();
				String pageName = executeQueryForWinner.getString("fbname");
				long engagement = executeQueryForWinner
						.getLong("sumEngagement");
				jsonObject.put("page_name", pageName);
				namesChartJS.put(pageName);
				jsonObject.put("engagement", "" + engagement);
				valuesChartJS.put(engagement);
				arrayList.add(jsonObject);
			}
			JSONArray compose = new JSONArray();
			compose.put(namesChartJS);
			compose.put(valuesChartJS);
			Label label = new Label(
					"za",
					"var arr = "
							+ compose.toString()
							+ "; var chartName = 'La barra mas alta muestra que pagina tuvo mejor performance los ultimos 7 dias';");
			label.setEscapeModelStrings(false);
			this.add(label);

			this.add(new ListView<Properties>("viewer", arrayList) {

				@Override
				protected void populateItem(ListItem<Properties> item) {
					Properties model = item.getModelObject();
					item.add(new Label("pagename", model
							.getProperty("page_name")));
					item.add(new Label("engagement", model
							.getProperty("engagement")));

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
				description = executeQueryForCharts
						.getString("description");
				String pageName = executeQueryForCharts.getString("page_name");

				ArrayList<Properties> postsForChartItem = new ArrayList<Properties>();
				
				String askMostInfluentialPosts = "select id,created_time,shares,likes,comments,engagement,message from posts where page_name = "
						+ "? AND created_time BETWEEN ? AND ? order by engagement DESC LIMIT 5";
				PreparedStatement askMostInfl = conn.prepareStatement(askMostInfluentialPosts);
				askMostInfl.setString(1, facebookId);
				askMostInfl.setTimestamp(2, new Timestamp(lastWeek.toDate().getTime()));
				askMostInfl.setTimestamp(3, new Timestamp(now.toDate().getTime()));

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
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							String createdTimeStr = sdf.format(createdTime);
							item.add(new Label("date",createdTimeStr));
							item.add(new Label("index",""+i));
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

	
		String fullUrl = "http://zoxial.com/Analytics/" + chartId + "/" + lastWeek.getMillis() + "/" + now.getMillis();
		Label label = new Label("text", fullUrl);
		ExternalLink shareLink = new ExternalLink("sharelink", fullUrl);
		shareLink.add(label);
		this.add(shareLink);
	}


	private void buildChartSelection(final String chartIdSelected) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet executeQuery = null;
		final HashMap<String, String> map = new HashMap<String, String>();
		try {
			conn = datasource.getConnection();
			stmt = conn.createStatement();
			executeQuery = stmt.executeQuery("SELECT DISTINCT `chart_id`, `chart_name` from charts;");
			while(executeQuery.next()){
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
		
		this.add(new ListView<String>("chartselector",new ArrayList(map.keySet())) {

			@Override
			protected void populateItem(ListItem<String> item) {
				String id = item.getDefaultModelObjectAsString();
				
				if(chartIdSelected.equals(id)){
					item.add(new AttributeAppender("class", new Model("active")));
				}
				
				String renderFullUrl = RequestCycle.get().getUrlRenderer().renderFullUrl(
						   Url.parse(urlFor(Analytics.class,null).toString()));
				
				String chartName = map.get(id);
				Label label = new Label("chartname", chartName);
				ExternalLink bookmarkablePageLink = new ExternalLink("link", renderFullUrl + "/" + id);
				bookmarkablePageLink.add(label);
				item.add(bookmarkablePageLink);
			}
		});
		
		SITEMAP = new HashMap(map);
	}

}
