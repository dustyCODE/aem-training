package com.globant.julian.cacharriando.core.models;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import static org.junit.Assert.*;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;

public class TestVideoEmbedModel {

	private ValueMap properties;

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
		properties = new ValueMapDecorator( new HashMap<String, Object>());
		properties.put("type-video", "Youtube");
		properties.put("key-video", "9QS6bDH6anw");
		when(embedVideo.getProperties()).thenReturn(properties);
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
		
		properties = new ValueMapDecorator(new HashMap<String, Object>());
		properties.put("type-video", typeTest);
		properties.put("key-video", "9QS6bDH6anw");

		when(embedVideo.getProperties()).thenReturn(properties);

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

		properties = new ValueMapDecorator(new HashMap<String, Object>());
		properties.put("type-video", typeTest);
		properties.put("key-video", "1248955");

		when(embedVideo.getProperties()).thenReturn(properties);

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
