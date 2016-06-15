package com.globant.julian.cacharriando.core.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.julian.cacharriando.core.models.utils.PageBlog;

@Component
public class ExtractPageContentListener implements EventListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExtractPageContentListener.class);

	private List<String> posts;

	@Reference
	private SlingRepository slingRepository;

	private Session session;
	private ObservationManager observationManager;
	private Map<String, Object> infoEvent;

	private PageBlog pageBlog;

	@Activate
	protected void activate(ComponentContext componentContext) throws Exception {
		session = slingRepository.loginAdministrative(null);
		posts = new ArrayList<>();
		observationManager = session.getWorkspace().getObservationManager();
		final String[] types = { "cq:Page" };
		final String path = "/content/cacharriando/en"; // define the path
		// Create the Observation Listener...
		// The target path will be the path configure in the component for
		// articles...
		observationManager.addEventListener(this, Event.NODE_ADDED | Event.PROPERTY_CHANGED, path, true, null, types, true);
		LOGGER.info("New event listener created");
	}

	@Deactivate
	protected void deactivate() {
		try {
			if (observationManager != null) {
				observationManager.removeEventListener(this);
				LOGGER.info("**** Event Listener removed!");
			}
		} catch (RepositoryException ex) {
			LOGGER.error("RepositoryException: ", ex);
		} finally {
			if (session != null) {
				session.logout();
				session = null;
			}
		}

	}

	@Override
	public void onEvent(EventIterator events) {
		LOGGER.debug("Launching the onEvent method()...");
		while (events.hasNext()) {
			// capture the event
			Event event = events.nextEvent();
			//Impl to a new page created
			if (event.getType() == Event.NODE_ADDED) {
				try {
					LOGGER.debug("New event: {}", event.getPath());
					infoEvent = event.getInfo();
					LOGGER.debug("INFO EVENT {}", infoEvent.toString());
					Node pageContentNode = session.getNode(event.getPath());
					if ((pageContentNode != null)
							&& pageContentNode.getPrimaryNodeType().isNodeType("cq:PageContent")) {
						PageBlog currentPage = getNodeProperties(pageContentNode);
						saveNodeProperties(currentPage);
					} else {
						LOGGER.debug("Skip processing node: " + event.getPath());
					}

				} catch (Exception ex) {
					LOGGER.error("Exception during the event impl: " + ex);
				}
			
				//Impl when a property change...	
			}else if (event.getType() == Event.PROPERTY_CHANGED){
				
				try {
					LOGGER.debug("la propiedad que cambio es: {}", event.getPath());
					Property property = session.getProperty(event.getPath());
//					property.getName().equals("title")
					
					
					
				} catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				
				
				
				
			}
			
		}

	}

	private PageBlog getNodeProperties(Node pageContentNode) throws Exception {

		Node parNode = pageContentNode.getNode("par/text");
		Property textContent = parNode.getProperty("text");
		String text = textContent != null ? textContent.getValue().toString() : null;
		Property titleProp = pageContentNode.getNode("title").getProperty("jcr:title");
		String title = titleProp != null ? titleProp.getValue().toString() : null;

		pageBlog = new PageBlog(title, text);
		return pageBlog;

	}

	private void saveNodeProperties(PageBlog pageBlog) {

		try {
			posts.add(pageBlog.toString());
			Node componentNode = session.getNode("/content/cacharriando/en/jcr:content/par/blog");
			ValueFactory valueFactory = session.getValueFactory();
			Value[] values = new Value[posts.size()];
			for (int i = 0; i < values.length; i++) {
				values[i] = valueFactory.createValue(posts.get(i));
			}
			componentNode.setProperty("posts", values);
			session.save();
		} catch (RepositoryException e) {
			LOGGER.error("Error: " + e);
			e.printStackTrace();
		}
	}

}
