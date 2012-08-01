package com.zoxial.ui.sitemap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.apache.wicket.markup.MarkupType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;

import com.zoxial.ui.Analytics;

public class SiteMap extends WebPage {

	// private static final Logger log = LoggerFactory.getLogger(SiteMap.class);

	public SiteMap() {
		// log.info("Start Creating sitemap.xml");
		Map map = Analytics.SITEMAP;

		final ArrayList<SiteUrl> siteUrls = new ArrayList<SiteUrl>();
		ArrayList<String> l = new ArrayList(map.keySet());
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd");
		final Date now = new Date();
		String renderFullUrl = RequestCycle
				.get()
				.getUrlRenderer()
				.renderFullUrl(
						Url.parse(urlFor(Analytics.class, null).toString()));

		for (String id : l) {
			siteUrls.add(new SiteUrl(renderFullUrl + "/" + id, simpleDateFormat
					.format(now), "hourly", "0.9"));
		}

		this.add(new ListView<SiteUrl>("urlList", siteUrls) {

			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(ListItem<SiteUrl> item) {
				SiteUrl siteUrl = item.getModelObject();
				item.add(new Label("locNode", siteUrl.getLoc()));
				item.add(new Label("lastmodNode", siteUrl.getLastmod()));
				item.add(new Label("changefreqNode", siteUrl.getChangefreq()));
				item.add(new Label("priorityNode", siteUrl.getPriority()));
			}
		});
	}

	@Override
	public MarkupType getMarkupType() {
		return new MarkupType("xml", MarkupType.XML_MIME);
	}

}
