package com.globant.julian.cacharriando.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.globant.julian.cacharriando.core.models.utils.FooterSite;
import com.adobe.acs.commons.widgets.MultiFieldPanelFunctions;
import com.adobe.cq.sightly.WCMUse;

public class FooterWCMUseModel extends WCMUse {

	private static final Logger LOGGER = LoggerFactory.getLogger(FooterWCMUseModel.class);
	private List<FooterSite> linksList;

	private boolean checkbox;

	private boolean checkboxDesign;

	private String text;

	private static final String LINKS_PROP_NAME = "links";

	// activate() impl and not @PostConstruct
	@Override
	public void activate() throws Exception {
		LOGGER.info("Reading...");

		try {
			// getProperties() obtiene propiedades del dialogo de edicion. Configurado solo para esa instancia.
			// getCurrentStyle() obtiene propiedades del dialogo de design. Configurado para todas las paginas
			checkbox = getProperties().get("checkbox", false);
			ValueMap valueMap = checkbox ? getProperties() : getCurrentStyle();
			text = valueMap.get("text", "No vino nada");
			checkboxDesign = valueMap.get("checkbox", false);

			// getResource(): hace referencia al resource del componente
			// getCurrentDesign(): hace referencia

			// TODO - aem : Mirar otra forma de acceder al resource del design
			// dialog
			Resource resource = checkbox ? getResource()
					: getResourceResolver()
							.getResource(getCurrentDesign().getContentResource().getPath() + "/page/multifield");

			List<Map<String, String>> links = MultiFieldPanelFunctions.getMultiFieldPanelValues(resource,
					LINKS_PROP_NAME);

			FooterSite site = null;
			this.linksList = new ArrayList<FooterSite>();
			for (Iterator<Map<String, String>> iterator = links.iterator(); iterator.hasNext();) {
				Map<String, String> map = (Map<String, String>) iterator.next();
				site = new FooterSite();
				site.setLabel(map.get("label") != null ? map.get("label").toString() + " wcmUse" : "empty");
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

	public boolean isCheckboxDesign() {
		return checkboxDesign;
	}

	public String getText() {
		return text;
	}

}