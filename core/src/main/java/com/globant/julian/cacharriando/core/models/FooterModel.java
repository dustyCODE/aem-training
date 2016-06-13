package com.globant.julian.cacharriando.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globant.julian.cacharriando.core.models.utils.FooterSite;
import com.adobe.acs.commons.widgets.MultiFieldPanelFunctions;

@Model(adaptables = Resource.class)
public class FooterModel {

	private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldModel.class);
	private List<FooterSite> linksList;

	@Inject
	@Optional
	private boolean checkbox;

	@Inject
	private Resource resource;
	private static final String LINKS_PROP_NAME = "links";

	@PostConstruct
	protected void init() {
		LOGGER.info("Reading...");

		try {

			List<Map<String, String>> links = MultiFieldPanelFunctions.getMultiFieldPanelValues(resource,
					LINKS_PROP_NAME);

			FooterSite site = null;
			this.linksList = new ArrayList<FooterSite>();
			for (Iterator<Map<String, String>> iterator = links.iterator(); iterator.hasNext();) {
				Map<String, String> map = (Map<String, String>) iterator.next();
				site = new FooterSite();
				site.setLabel(map.get("label") != null ? map.get("label").toString() : "empty");
				site.setUrl(map.get("url") != null ? map.get("url").toString() : "empty");
				site.setNoFollow(map.get("noFollow").toString().equals("true") ? true : false);
				this.linksList.add(site);
			}
			LOGGER.info("Finish...");
		} catch (Exception ex) {
			LOGGER.error("Exception: " + ex.getMessage());
		}
	}

	public List<FooterSite> getLinksList() {
		return linksList;
	}

	public boolean isCheckbox() {
		return checkbox;
	}

}