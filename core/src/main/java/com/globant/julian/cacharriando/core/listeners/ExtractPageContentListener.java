package com.globant.julian.cacharriando.core.listeners;

import com.globant.julian.cacharriando.core.models.utils.PageBlog;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.jcr.api.SlingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.ObservationManager;
import java.util.Iterator;
import java.util.Map;

@Component(immediate = true, metatype = true, label = "Extract Posts Listener")
public class ExtractPageContentListener implements EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractPageContentListener.class);

    @Reference
    private SlingRepository slingRepository;

    private Session session;
    private ObservationManager observationManager;

    private StringBuilder allText;

    private ResourceResolver resourceResolver;

    private static final int CHARS_CONTENT_MAX = 250;

    @org.apache.felix.scr.annotations.Property(label = "Path to listen posts", description = "Choose the path e.g /content/cacharriando/articulos")
    private static final String PATH = "PATH_BLOG_PAGE";
    private String PATH_BLOG_PAGE;

    private final String DEFAULT_PATH_BLOG_PAGE = "/content/cacharriando/articulos";

    @SuppressWarnings("deprecation")
    @Activate
    protected void activate(final Map<String, Object> config) throws Exception {
        session = slingRepository.loginAdministrative(null);
        //resourceResolver = resourceResolverFactory.getResourceResolver(null);
        this.PATH_BLOG_PAGE = PropertiesUtil.toString(config.get(PATH), DEFAULT_PATH_BLOG_PAGE);
        observationManager = this.getSession().getWorkspace().getObservationManager();
        final String[] types = {"cq:Page", "nt:unstructured"};
        observationManager.addEventListener(this,
                Event.NODE_ADDED | Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED,
                this.PATH_BLOG_PAGE, true, null, types, true);
        LOGGER.info("New event listener created...listening the path: {}", this.PATH_BLOG_PAGE);
    }

    @Override
    public void onEvent(EventIterator events) {
        LOGGER.debug("Launching the onEvent method()...");
        try {
            processEvents(events);
        } catch (RepositoryException ex) {
            LOGGER.error("Repository Exception {}", ex);
        }
    }

    /**
     * Este metodo se encarga de procesar todos los eventos atendidos por el
     * listener
     *
     * @param events
     */
    private void processEvents(EventIterator events) throws RepositoryException {
        while (events.hasNext()) {
            Event event = events.nextEvent();
            if (event.getType() == Event.NODE_ADDED) {
                Node eventNode = this.getSession().getNode(event.getPath());
                if ((eventNode != null) && eventNode.getPrimaryNodeType().isNodeType("cq:PageContent")) {
                    saveNodeProperties(eventNode);
                }
            } else if (event.getType() == Event.PROPERTY_CHANGED) {
                Property changedProperty = this.getSession().getProperty(event.getPath());
                String propertyName = changedProperty.getName();
                if (propertyName.equalsIgnoreCase("jcr:title") || propertyName.equalsIgnoreCase("text")) {
                    setPropertiesValues(changedProperty, true);
                }
            } else if (event.getType() == Event.PROPERTY_ADDED) {

                Property changedProperty = this.getSession().getProperty(event.getPath());
                if (changedProperty.getName().equalsIgnoreCase("text")) {
                    setPropertiesValues(changedProperty, true);
                } else if (changedProperty.getName().equalsIgnoreCase("fileReference")
                        && changedProperty.getParent().getProperty("sling:resourceType").getValue().toString()
                        .equalsIgnoreCase("cacharriando/components/content/image")) {
                    setPropertiesValues(changedProperty, true);
                }
            } else if (event.getType() == Event.PROPERTY_REMOVED) {
                String propertyPath = event.getPath();
                onRemoveProperty(propertyPath);
            }

        }
    }

    /**
     * Este metodo se ejecuta cuando se elimina una propiedad
     *
     * @param propertyPath
     * @throws RepositoryException
     */
    private void onRemoveProperty(String propertyPath) throws RepositoryException {

        if (propertyPath.endsWith("fileReference")) {
            int max = propertyPath.indexOf("jcr:content");
            String contentNodePath = propertyPath.substring(0, (max - 1));
            contentNodePath = contentNodePath + "/jcr:content";
            Node contentNode = this.getSession().getNode(contentNodePath);
            if (contentNode.hasProperty("imageUrl")) {
                contentNode.getProperty("imageUrl").remove();
            }
            this.getSession().save();
        } else if (propertyPath.endsWith("text")) {
            int max = propertyPath.indexOf("jcr:content");
            String contentNodePath = propertyPath.substring(0, (max - 1));
            contentNodePath = contentNodePath + "/jcr:content";
            Node contentNode = this.getSession().getNode(contentNodePath);
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
            this.getSession().save();
        }
    }

    private void saveNodeProperties(Node contentNode) throws RepositoryException {

        String title;
        String content;
        String url;

        Node titleNode = contentNode.hasNode("title") ? contentNode.getNode("title") : null;
        title = titleNode != null && titleNode.hasProperty("jcr:title") ? titleNode.getProperty("jcr:title").getValue().toString() : contentNode.getParent().getName();
        try {
            Property textContent = contentNode.getNode("par/text").getProperty("text");
            content = extractPostContent(textContent.getValue().toString());
        } catch (RepositoryException ex) {
            LOGGER.error("The content of the new page was not found...establishing a default content for the site: {}", contentNode.getParent().getName());
            // pageBlog.setContent("<p>Empty content</p>");
            content = "<p>Empty content</p>";
        }

        url = contentNode.getParent().getPath();

        setContentProperties(contentNode, "title", title);
        setContentProperties(contentNode, "content", content);
        setContentProperties(contentNode, "url", url);

    }

    private void setContentProperties(Node contentTargetNode, String key, String value) {

        try {
            contentTargetNode.setProperty(key, value, PropertyType.STRING);
            //TODO - siempre que se cambie o se cree la propiedad se esta enviando a guardar la session
            this.getSession().save();
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

    public Session getSession() {
        return session;
    }

    @Deactivate
    protected void deactivate() {
        try {
            if (observationManager != null) {
                observationManager.removeEventListener(this);
                LOGGER.info("**** Event Listener removed!");
            }
            if (resourceResolver != null) {
                resourceResolver.close();
            }
        } catch (RepositoryException ex) {
            LOGGER.error("RepositoryException: ", ex);
        } finally {
            if (this.getSession() != null) {
                this.session.logout();
                this.session = null;
            }
        }

    }
}
