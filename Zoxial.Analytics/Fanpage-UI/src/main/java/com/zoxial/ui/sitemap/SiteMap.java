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

		// DataServiceProxy dataservice = DataServiceProxyImpl.instance();
		//
		//
		//
		// Configuration config = ClasificadConfig.get();
		// String site = config.getString("site");
		// String mainUrl = "http://www." + site;
		// final String domain = mainUrl + "/";
		//
		// siteUrls.add(new SiteUrl(domain + "clasificadoNuevo",
		// simpleDateFormat.format(now), "hourly", "0.8"));
		//
		// Iterable<Classified> allClasiffieds;
		// try {
		// allClasiffieds = dataservice.getAllClasiffieds();
		//
		// for (Classified classified : allClasiffieds) {
		//
		// String url = domain + "_/" + URLEncoder.encode(classified.getId(),
		// "UTF-8");
		// siteUrls.add(new SiteUrl(url, simpleDateFormat.format(now), "hourly",
		// "0.5"));
		// }
		// } catch (DataServiceProxyException e) {
		// log.error("Could not get all classifieds for generate sitemap.xml");
		// } catch (UnsupportedEncodingException e) {
		// log.error("Could not generate a url enconded in UTF-8");
		// }
		//
		// ArrayList<String> orders = new ArrayList<String>();
		// orders.add("date");
		// orders.add("visit");
		// orders.add("contact");
		// orders.add("price");
		// orders.add("-price");
		//
		// for (String order : orders) {
		// Iterable<Category> allCategories;
		// try {
		// allCategories = dataservice.getAllCategories();
		// for (Category category : allCategories) {
		// String url = domain + "sc/" +
		// SeoUtils.encodeUrlSpaces(category.getName()) + "/0/" + order;
		// siteUrls.add(new SiteUrl(url, simpleDateFormat.format(now), "hourly",
		// "0.7"));
		// }
		// } catch (DataServiceProxyException e) {
		// log.error("Could not get all classifieds for generate sitemap.xml");
		// }
		//
		// }
		//
		// for (String order : orders) {
		//
		// Iterable<SubCategory> allSubCategories;
		// try {
		// allSubCategories = dataservice.getAllSubCategories();
		// for (SubCategory subCategory : allSubCategories) {
		// String url = domain + "ssc/" +
		// SeoUtils.encodeUrlSpaces(subCategory.getName()) + "/0/" + order;
		// siteUrls.add(new SiteUrl(url, simpleDateFormat.format(now), "hourly",
		// "0.6"));
		// }
		// } catch (DataServiceProxyException e) {
		// log.error("Could not get all classifieds for generate sitemap.xml");
		// }
		// }
		//
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

		// log.info("End Creating sitemap.xml");
	}

	@Override
	public MarkupType getMarkupType() {
		return new MarkupType("xml", MarkupType.XML_MIME);
	}

}
