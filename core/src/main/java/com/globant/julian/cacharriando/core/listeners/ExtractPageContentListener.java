package com.globant.julian.cacharriando.core.listeners;

import java.util.Iterator;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import javax.jcr.version.VersionException;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.globant.julian.cacharriando.core.models.utils.PageBlog;

@Component(immediate = true, metatype = true, label = "Extract Posts Listener")
public class ExtractPageContentListener implements EventListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExtractPageContentListener.class);

	@Reference
	private SlingRepository slingRepository;

	private Session session;
	private ObservationManager observationManager;

	private PageBlog pageBlog;

	private StringBuilder allText;

	private static final int CHARS_CONTENT_MAX = 250;

	@org.apache.felix.scr.annotations.Property(label = "Path to listen posts", description = "Choose the path e.g /content/cacharriando/articulos")
	private static final String PATH = "PATH_BLOG_PAGE";
	private String PATH_BLOG_PAGE;

	private final String DEFAULT_PATH_BLOG_PAGE = "/content/cacharriando/articulos";

	@SuppressWarnings("deprecation")
	@Activate
	protected void activate(final Map<String, Object> config) throws Exception {
		session = slingRepository.loginAdministrative(null);
		this.PATH_BLOG_PAGE = PropertiesUtil.toString(config.get(PATH), DEFAULT_PATH_BLOG_PAGE);
		observationManager = session.getWorkspace().getObservationManager();
		final String[] types = { "cq:Page", "nt:unstructured" };
		observationManager.addEventListener(this,
				Event.NODE_ADDED | Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED,
				this.PATH_BLOG_PAGE, true, null, types, true);
		LOGGER.info("New event listener created...listening the path: {}", this.PATH_BLOG_PAGE);
	}

	@Override
	public void onEvent(EventIterator events) {
		LOGGER.debug("Launching the onEvent method()...");

		processEvents(events);

		processEvents(events);

	}

	/**
	 * Este metodo se encarga de procesar todos los eventos atendidos por el
	 * listener
	 * 
	 * @param events
	 */
	private void processEvents(EventIterator events) {
		while (events.hasNext()) {
			Event event = events.nextEvent();
			if (event.getType() == Event.NODE_ADDED) {
				try {
					Node eventNode = session.getNode(event.getPath());
					if ((eventNode != null) && eventNode.getPrimaryNodeType().isNodeType("cq:PageContent")) {
						PageBlog pageCreated = getPage(eventNode);
						saveNodeProperties(eventNode, pageCreated);
					} else {
						LOGGER.info("Skip processing node: " + event.getPath());
					}
				} catch (RepositoryException ex) {
					LOGGER.error("Exception during the event impl: " + ex);
				}

			} else if (event.getType() == Event.PROPERTY_CHANGED) {
				try {
					Property changedProperty = session.getProperty(event.getPath());
					String propertyName = changedProperty.getName();
					if (propertyName.equalsIgnoreCase("jcr:title") || propertyName.equalsIgnoreCase("text")) {
						setPropertiesValues(changedProperty, true);
					} else {
						LOGGER.debug("Skip processing property: " + event.getPath());
					}
				} catch (RepositoryException e) {
					LOGGER.error("Exception setting the new properties values: {}", e);
					e.printStackTrace();
				}
			} else if (event.getType() == Event.PROPERTY_ADDED) {

				try {
					Property changedProperty = session.getProperty(event.getPath());
					if (changedProperty.getName().equalsIgnoreCase("text")) {
						setPropertiesValues(changedProperty, true);
					} else if (changedProperty.getName().equalsIgnoreCase("fileReference")
							&& changedProperty.getParent().getProperty("sling:resourceType").getValue().toString()
									.equalsIgnoreCase("cacharriando/components/content/image")) {
						setPropertiesValues(changedProperty, true);
					}

				} catch (RepositoryException ex) {
					LOGGER.debug("The property doesn't exist! {}", ex);
				}
			} else if (event.getType() == Event.PROPERTY_REMOVED) {
				try {
					String propertyPath = event.getPath();
					onRemoveProperty(propertyPath);
				} catch (RepositoryException ex) {
					LOGGER.debug("The property doesn't exist! {}", ex);
				}
			}

		}
	}

	/**Este metodo se ejecuta cuando se elimina una propiedad
	 * @param propertyPath
	 * @throws RepositoryException
	 */
	private void onRemoveProperty(String propertyPath) throws RepositoryException {

		if (propertyPath.endsWith("fileReference")) {
			int max = propertyPath.indexOf("jcr:content");
			String contentNodePath = propertyPath.substring(0, (max - 1));
			contentNodePath = contentNodePath + "/jcr:content";
			Node contentNode = session.getNode(contentNodePath);
			if (contentNode.hasProperty("imageUrl")) {
				contentNode.getProperty("imageUrl").remove();
			}
			session.save();
		} else if (propertyPath.endsWith("text")) {
			int max = propertyPath.indexOf("jcr:content");
			String contentNodePath = propertyPath.substring(0, (max - 1));
			contentNodePath = contentNodePath + "/jcr:content";
			Node contentNode = session.getNode(contentNodePath);
			Node aParNode = contentNode.getNode("par");
			NodeIterator it = aParNode.getNodes("*text*");
			StringBuilder texts = new StringBuilder();
			while (it.hasNext()) {
				Node textNode = it.nextNode();
				if (textNode.hasProperty("text")) {
					texts.append(textNode.getProperty("text").getValue().toString());
				}
			}

			contentNode.setProperty("content", texts.toString());
			session.save();
		}
	}

	private PageBlog getPage(Node pageContentNode) {

		pageBlog = PageBlog.getInstance();

		try {

			Node titleNode = pageContentNode.hasNode("title") ? pageContentNode.getNode("title") : null;
			String title = titleNode != null && titleNode.hasProperty("jcr:title")
					? titleNode.getProperty("jcr:title").getValue().toString() : pageContentNode.getParent().getName();
			pageBlog.setTitle(title);

		} catch (RepositoryException ex) {
			LOGGER.error("The title of the new page was not found");
			pageBlog.setTitle("New post");
		}

		try {

			Property textContent = pageContentNode.getNode("par/text").getProperty("text");
			String allContent = textContent.getValue().toString();
			allContent = extractPostContent(allContent);

			pageBlog.setContent(allContent);
		} catch (RepositoryException ex) {
			LOGGER.error("The content of the new page was not found");
			pageBlog.setContent("<p>Empty content</p>");
		}

		try {
			pageBlog.setUrl(pageContentNode.getParent().getPath());
		} catch (RepositoryException ex) {
			LOGGER.error("Path of page not found", ex);
		}

		return pageBlog;
	}

	private void saveNodeProperties(Node contentNode, PageBlog pageBlog) throws RepositoryException {

		setContentProperties(contentNode, "title", pageBlog.getTitle());
		setContentProperties(contentNode, "content", pageBlog.getContent());
		setContentProperties(contentNode, "url", pageBlog.getUrl());

	}

	private void setContentProperties(Node contentTargetNode, String key, String value) {

		try {
			contentTargetNode.setProperty(key, value, PropertyType.STRING);
			session.save();
		} catch (RepositoryException ex) {
			LOGGER.error("Error while trying to change the properties ", ex);
		}
	}

	private void setPropertiesValues(Property changedProperty, boolean isAddedOrUpdated) throws RepositoryException {

		// obtenemos el jcr:content de la pagina de manera recursiva.
		Node aNode = changedProperty.getParent();
		while (!aNode.getPrimaryNodeType().isNodeType("cq:PageContent")) {
			aNode = aNode.getParent();
		}

		String key;
		String allContent;
		String propertyName = changedProperty.getName();

		if (changedProperty.getType() == PropertyType.STRING && propertyName.equalsIgnoreCase("text")) {
			key = "content";
			Node par = changedProperty.getParent();
			while (!par.getName().equals("par")) {
				par = par.getParent();
			}

			Iterable<Node> its = JcrUtils.getChildNodes(par, "*text*");
			Iterator<Node> iterator = its.iterator();

			allText = new StringBuilder();
			while (iterator.hasNext()) {
				Node textNode = iterator.next();
				if (textNode.hasProperty("text")) {
					allText.append(textNode.getProperty("text").getValue().toString());
				}
			}

			allContent = allText.toString();
			allContent = extractPostContent(allContent);
			setContentProperties(aNode, key, allContent);
		} else if (changedProperty.getType() == PropertyType.STRING && propertyName.equalsIgnoreCase("jcr:title")) {
			key = "title";

			if (isAddedOrUpdated) {
				setContentProperties(aNode, key, changedProperty.getValue().toString());
			} else {
				// clean the property
				if (aNode.hasProperty("title")) {
					aNode.getProperty("title").remove();
				}
				// setContentProperties(aNode, key, "");
			}
		} else if (propertyName.equalsIgnoreCase("fileReference")) {
			// se agrego o cambio la propiedad para la imagen
			key = "imageUrl";
			if (isAddedOrUpdated) {
				setContentProperties(aNode, key, changedProperty.getValue().toString());
			} else {
				if (aNode.hasProperty("imageUrl")) {
					aNode.getProperty("imageUrl").remove();
				}
			}

		}
	}

	private String extractPostContent(String fullContent) {

		if (fullContent.length() >= CHARS_CONTENT_MAX) {
			fullContent = fullContent.substring(0, CHARS_CONTENT_MAX) + "....";
		}

		return fullContent;

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

}
