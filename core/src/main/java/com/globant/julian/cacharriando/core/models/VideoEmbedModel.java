package com.globant.julian.cacharriando.core.models;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.cq.sightly.WCMUse;
import com.globant.julian.cacharriando.core.enums.VideoEmbedProperties;

public class VideoEmbedModel extends WCMUse {

	private String type;
	private String key;

	private final String PREFIX_URL_YOUTUBE = "https://www.youtube.com/embed/";
	private final String PREFIX_URL_VIMEO = "https://player.vimeo.com/video/";
	private final String kEY_DEFAULT = "wSnx4V_RewE";

	private String url;
	private ValueMap properties;

	private String title;
	private String elementTitle;

	@Override
	public void activate() throws Exception {

		this.properties = getProperties();
		
		this.title = this.properties.get(VideoEmbedProperties.TTILE_PROP.getName(), "Intro Video");
		this.type = this.properties.get(VideoEmbedProperties.TYPE_PROP.getName(), "");
		this.key = this.properties.get(VideoEmbedProperties.KEY_PROP.getName(), "");
//		this.elementTitle = getCurrentStyle().get("element-title", "h1");
		if (StringUtils.isNotBlank(this.key)) {
			switch (type) {
			case "Youtube":
				this.url = PREFIX_URL_YOUTUBE + key;
				break;
			case "Vimeo":
				this.url = PREFIX_URL_VIMEO + key;
				break;
			default:
				this.url = PREFIX_URL_YOUTUBE + kEY_DEFAULT;

				break;
			}
		} else {
			this.url = PREFIX_URL_YOUTUBE + kEY_DEFAULT;
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

	public String getTitle() {
		return title;
	}

	public String getElementTitle() {
		return elementTitle;
	}

}