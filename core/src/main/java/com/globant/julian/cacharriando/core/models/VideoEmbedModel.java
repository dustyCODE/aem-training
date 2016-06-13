package com.globant.julian.cacharriando.core.models;

import org.apache.sling.api.resource.ValueMap;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import com.adobe.cq.sightly.WCMUse;

public class VideoEmbedModel extends WCMUse {

	private String type;
	private String key;

	private final String TYPE_NAME_PROP = "type-video";
	private final String KEY_NAME_PROP = "key-video";
	private final String PREFIX_URL_YOUTUBE = "https://www.youtube.com/embed/";
	private final String PREFIX_URL_VIMEO = "https://player.vimeo.com/video/";

	private String url;
	private ValueMap properties;

	@Override
	public void activate() throws Exception {

		this.properties = getProperties();
		this.type = this.properties.get(this.TYPE_NAME_PROP, "Youtube");
		this.key = this.properties.get(this.KEY_NAME_PROP, "");

		switch (type) {
		case "Youtube":
			this.url = PREFIX_URL_YOUTUBE + key;
			break;
		case "Vimeo":
			this.url = PREFIX_URL_VIMEO + key;
			break;
		default:
			this.url = PREFIX_URL_YOUTUBE + "wSnx4V_RewE";
			break;
		}
	}

	public String getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public String getUrl() {
		return url;
	}

}
