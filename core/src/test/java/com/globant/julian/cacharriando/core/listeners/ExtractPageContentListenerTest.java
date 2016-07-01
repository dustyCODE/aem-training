package com.globant.julian.cacharriando.core.listeners;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import javax.jcr.*;
import javax.jcr.nodetype.NodeType;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;

/**
 * Created by julian.herrera on 6/24/2016.
 */

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({ExtractPageContentListener.class})
public class ExtractPageContentListenerTest {

    @Mock
    private Logger logger;

    @Mock
    private Session session;

    @Mock
    private EventIterator eventIterator;

    @Mock
    private Event event;

    @Mock
    private Node node;

    @Mock
    private Node titleNode;

    @Mock
    private NodeType nodeType;

    @Mock
    private Property titleProperty;

    @Mock
    private Property textProperty;

    @Mock
    private Value titleValue;

    @Mock
    private Value textValue;

    @Mock
    private Value urlValue;

    @Mock
    private Node textNode;

    @Mock
    private Node parentNode;

    @Mock
    private ExtractPageContentListener extractPageContentListener;

    private final String EVENT_PATH_ADDED_NODE = "/content/cacharriando/articulos/en/jcr:content";


    private String title = "Cacharriando site";
    private String content = "Este es un texto de prueba";
    private String url = "/content/cacharriando/en.html";

    @Before
    public void setup() {

        //cuando se tenga un matcher, no se puede combinar tipos de objetos.
        MockitoAnnotations.initMocks(this);
        System.out.println("Setup test");
        Mockito.when(extractPageContentListener.getSession()).thenReturn(session);
        Mockito.doCallRealMethod().when(extractPageContentListener).onEvent(eventIterator);
    }


    @Test
    public void testAddNodePage() throws RepositoryException {


        //Given
        Mockito.when(eventIterator.hasNext()).thenReturn(true, false);
        Mockito.when(eventIterator.nextEvent()).thenReturn(event);

        Mockito.when(event.getType()).thenReturn(Event.NODE_ADDED);
        Mockito.when(event.getPath()).thenReturn(EVENT_PATH_ADDED_NODE);

        Mockito.when(session.getNode(event.getPath())).thenReturn(node);
        Mockito.when(node.getPrimaryNodeType()).thenReturn(nodeType);
        Mockito.when(node.getPrimaryNodeType().isNodeType("cq:PageContent")).thenReturn(true);

        Mockito.when(node.getNode("title")).thenReturn(titleNode);

        Mockito.when(node.hasNode("title")).thenReturn(true);

        Mockito.when(titleNode.hasProperty("jcr:title")).thenReturn(true);
        Mockito.when(titleNode.getProperty("jcr:title")).thenReturn(titleProperty);
        Mockito.when(titleProperty.getValue()).thenReturn(titleValue);
        Mockito.when(titleValue.toString()).thenReturn(title);
        Mockito.when(node.getNode("par/text")).thenReturn(textNode);
        Mockito.when(node.getNode("par/text").getProperty("text")).thenReturn(textProperty);
        Mockito.when(textProperty.getValue()).thenReturn(textValue);
        Mockito.when(textValue.toString()).thenReturn(content);
        Mockito.when(node.getParent()).thenReturn(parentNode);
        Mockito.when(node.getParent().getPath()).thenReturn(url);

        //When
        extractPageContentListener.onEvent(eventIterator);


        //Then
        //1 llamada del metodo session.getNode()
        Mockito.verify(session, Mockito.times(1)).getNode(event.getPath());

        Mockito.verify(node, Mockito.times(1)).setProperty("title", title, PropertyType.STRING);
        Mockito.verify(node, Mockito.times(1)).setProperty("content", content, PropertyType.STRING);
        Mockito.verify(node, Mockito.times(1)).setProperty("url", url, PropertyType.STRING);

    }

}
