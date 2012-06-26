package com.zoxial;

import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.codec.binary.Base64;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Facebook extends WebPage{
	
	private static final Logger log = LoggerFactory.getLogger(WicketApplication.class);

	
	public Facebook() {
		
//		Map<String, String[]> parameters = this.getRequest().getParameterMap();
//		String sigreq = parameters.get("signed_request")[0];
//		log.info("signed_request: " + sigreq);			
//		
//		int idx = sigreq.indexOf(".");
//		byte[] sig = new Base64(true).decode(sigreq.substring(0, idx).getBytes());
//		String rawpayload = sigreq.substring(idx+1);
//		String payload = new String(new Base64(true).decode(rawpayload.getBytes()));
//		log.info("payload: " + payload);			
//		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON( payload );
//		log.info("page: " + jsonObject.getJSONObject("page"));


	}

}
