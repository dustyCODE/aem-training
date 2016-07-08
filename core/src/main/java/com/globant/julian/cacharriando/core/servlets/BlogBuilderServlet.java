package com.globant.julian.cacharriando.core.servlets;

import com.day.cq.wcm.api.Page;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.commons.json.io.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

@SlingServlet(resourceTypes = "cacharriando/components/content/blog", methods = "GET", extensions = "json")
public class BlogBuilderServlet extends SlingSafeMethodsServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlogBuilderServlet.class);

    private final String resourcePath = "/content/cacharriando/articulos";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        JSONObject result = new JSONObject();
        Writer out = response.getWriter();
        Resource resource = request.getResource();
        Node targetNode = resource.adaptTo(Node.class);
        String pagePath;
        try {
            Node pageNode = getTargetPage(targetNode);
            pagePath = pageNode != null ? pageNode.getPath() : resourcePath;
        } catch (RepositoryException ex) {
            LOGGER.error("Exception :", ex);
            pagePath = resourcePath;
        }

        Page currentPage = request.getResourceResolver().getResource(pagePath).adaptTo(Page.class);
        Iterator<Page> allPages = currentPage.listChildren();
        JSONArray jsonArray = new JSONArray();
        JSONObject object;
        while (allPages.hasNext()) {
            try {
                Page childPage = allPages.next();
                ValueMap contentNode = childPage.getContentResource().adaptTo(ValueMap.class);
                object = new JSONObject();
                object.put("title", contentNode.get("title", null));
                object.put("content", contentNode.get("content", null));
                object.put("url", contentNode.get("url", null));
                object.put("imageUrl", contentNode.get("imageUrl", null));
                jsonArray.put(object);
            } catch (Exception ex) {
                LOGGER.error("Exception {}", ex);
                response.setStatus(SlingHttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

        out.write(jsonArray.toString());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(SlingHttpServletResponse.SC_OK);

    }


    private Node getTargetPage(Node aNode) throws RepositoryException {

        while (!aNode.getPrimaryNodeType().isNodeType("cq:Page")) {
            aNode = aNode.getParent();
        }
        return aNode;
    }

}
