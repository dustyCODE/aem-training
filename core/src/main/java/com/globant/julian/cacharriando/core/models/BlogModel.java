package com.globant.julian.cacharriando.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.sightly.WCMUse;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.globant.julian.cacharriando.core.models.utils.PageBlog;

/**
 * Este modelo se encarga de leer las propiedades(title, content) de cada una de
 * las páginas hijas creadas
 *
 * @author julian.herrera
 */
public class BlogModel extends WCMUse {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlogModel.class);

    private List<PageBlog> posts;
    private PageBlog post;

    //TODO
	/*
         Crear un Sling Servlet, que no se envie como parametro la pagina actual
		 Hacer un GET asociado al nuevo componente, configurar servlet asociado a un resourceType
		 El servlet devuelve un JSON coom respuesta de un request AJAX

		 Obtener la URL, puede ser de un data-attr en la vista para hacer la peticion.
	 */


    //currentNode.getPath() - obtener la ruta

    @Override
    public void activate() {

        Page page = getResourcePage();
        Iterator<Page> allPages = page.listChildren();
        posts = new ArrayList<>();
        while (allPages.hasNext()) {
            try {
                post = PageBlog.getInstance();
                Page childPage = allPages.next();
                Node contentNode = childPage.getContentResource().adaptTo(Node.class);
                post.setTitle(contentNode.getProperty("title").getValue().toString());
                post.setContent(contentNode.getProperty("content").getValue().toString());
                post.setUrl(contentNode.getProperty("url").getValue().toString());
                if (contentNode.hasProperty("imageUrl")) {
                    post.setImageUrl(contentNode.getProperty("imageUrl").getValue().toString());
                }
                posts.add(post);
            } catch (Exception ex) {
                LOGGER.error("Exception {}", ex);
            }
        }
    }

    public List<PageBlog> getPosts() {
        return this.posts;
    }

}
