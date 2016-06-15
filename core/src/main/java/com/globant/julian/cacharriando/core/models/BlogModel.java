package com.globant.julian.cacharriando.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUse;
import com.globant.julian.cacharriando.core.models.utils.PageBlog;

public class BlogModel extends WCMUse {

	private static final Logger LOGGER = LoggerFactory.getLogger(BlogModel.class);

	private List<PageBlog> posts;

	@Override
	public void activate() throws Exception {

		ValueMap valueMap = getProperties();
		String[] values = valueMap.get("posts", new String[0]);
		List<Map<String, String>> postsMap = getMap(values);
		this.posts = new ArrayList<>();
		PageBlog page;
		for (Iterator<Map<String, String>> iterator = postsMap.iterator(); iterator.hasNext();) {
			Map<String, String> map = (Map<String, String>) iterator.next();
			page = new PageBlog();
			page.setTitle(map.get("title"));
			page.setContent(map.get("content"));
			this.posts.add(page);
		}

	}

	private List<Map<String, String>> getMap(String[] values) {
		List<Map<String, String>> results = new ArrayList<>();
		for (String value : values) {
			try {
				JSONObject parsed = new JSONObject(value);
				Map<String, String> columnMap = new HashMap<String, String>();
				for (Iterator<String> iter = parsed.keys(); iter.hasNext();) {
					String key = iter.next();
					String innerValue = parsed.getString(key);
					columnMap.put(key, innerValue);
				}
				results.add(columnMap);
			} catch (JSONException e) {
				LOGGER.error("Unable to parse JSON property");
			}
		}

		return results;
	}

	public List<PageBlog> getPosts() {
		return this.posts;
	}

}
