package com.globant.julian.cacharriando.core.models;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.day.cq.wcm.api.designer.Cell;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Style;

import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import static org.junit.Assert.*;

import org.apache.batik.css.engine.StyleDeclaration;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

public class TestVideoEmbedModel {

	private ValueMap editProperties;
	private Style designProperties;
	private ValueMap designPropertiesMap;

	@Mock
	VideoEmbedModel embedVideo;

	@Before
	public void setup() throws Exception {

		MockitoAnnotations.initMocks(this);
		embedVideo = mock(VideoEmbedModel.class);
		when(embedVideo.getUrl()).thenCallRealMethod();
		when(embedVideo.getType()).thenCallRealMethod();
		doCallRealMethod().when(embedVideo).activate();
	}

	// Probamos que sea posible obtener una URL cualquiera.
	@Test
	public void testGetURL() throws Exception {

//		properties = new ValueMapDecorator(new HashMap<String, Object>());
		editProperties = new ValueMapDecorator( new HashMap<String, Object>());
		editProperties.put("type-video", "Youtube");
		editProperties.put("key-video", "9QS6bDH6anw");
		designPropertiesMap = new ValueMapDecorator(new HashMap<String, Object>());
		designPropertiesMap.put("element-title", "h1");
		when(embedVideo.getProperties()).thenReturn(editProperties);
//		when(embedVideo.getCurrentStyle()).thenReturn();
		embedVideo.activate();
		String url = embedVideo.getUrl();
		assertNotNull(url);
		assertTrue(embedVideo.getUrl().length() > 0);
	}

	/**
	 * Metodo que prueba segun el tipo indicado que la URL embebida sea la
	 * correspondiente...
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTypeYoutube() throws Exception {

		final String typeTest = "Youtube";
		
		editProperties = new ValueMapDecorator(new HashMap<String, Object>());
		editProperties.put("type-video", typeTest);
		editProperties.put("key-video", "9QS6bDH6anw");

		designProperties.put("element-title", "h1");
		when(embedVideo.getProperties()).thenReturn(editProperties);
		when(embedVideo.getCurrentStyle()).thenReturn(designProperties);

		embedVideo.activate();
		String type = embedVideo.getType();
		String URL = embedVideo.getUrl();

		System.out.println("Type of the video for testing : " + type);

		assertNotNull(type);
		if (type.equals(typeTest)) {
			assertTrue(URL.contains(typeTest.toLowerCase()));
		} else {
			throw new AssertionError();
		}
	}

	@Test
	public void testTypeVimeo() throws Exception {

		final String typeTest = "Vimeo";

		editProperties = new ValueMapDecorator(new HashMap<String, Object>());
		editProperties.put("type-video", typeTest);
		editProperties.put("key-video", "1248955");

		designProperties.put("element-title", "h1");
		when(embedVideo.getProperties()).thenReturn(editProperties);
		when(embedVideo.getCurrentStyle()).thenReturn(designProperties);

		embedVideo.activate();
		String type = embedVideo.getType();
		String URL = embedVideo.getUrl();
		
		System.out.println("Type of the video for testing : " + type);
		assertNotNull(type);

		if (type.equals(typeTest)) {
			assertTrue(URL.contains(typeTest.toLowerCase()));
		} else {
			throw new AssertionError();
		}
	}
}
