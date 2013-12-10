package com.elasticpath.domain.misc.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.awt.image.RenderedImage;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.misc.ImageRenderResponse;

/**
 * Test <code>ImageRenderResponseImpl</code>.
 */
public class ImageRenderResponseImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ImageRenderResponse imageRenderResponse;

	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		this.imageRenderResponse = new ImageRenderResponseImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.ImageRenderResponseImpl.getImageType()'.
	 */
	@Test
	public void testGetImageType() {
		assertNull(imageRenderResponse.getImageType());
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.ImageRenderResponseImpl.setImageType(String)'.
	 */
	@Test
	public void testSetImageType() {
		final String type = ".gif";
		this.imageRenderResponse.setImageType(type);
		assertSame(type, this.imageRenderResponse.getImageType());
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.ImageRenderResponseImpl.getScaledImage()'.
	 */
	@Test
	public void testGetScaledImage() {
		assertNull(imageRenderResponse.getScaledImage());
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.ImageRenderResponseImpl.setScaledImage(PlanarImage)'.
	 */
	@Test
	public void testSetScaledImage() {
		final RenderedImage mockRenderedImage = context.mock(RenderedImage.class);
		final RenderedImage image = mockRenderedImage;

		this.imageRenderResponse.setScaledImage(image);
		assertSame(image, this.imageRenderResponse.getScaledImage());
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.MimeRenderResponseImpl.getMimeType()'.
	 */
	@Test
	public void testGetMimeType() {
		assertNull(imageRenderResponse.getMimeType());
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.MimeRenderResponseImpl.setMimeType(String)'.
	 */
	@Test
	public void testSetMimeType() {
		final String type = "image/gif";
		this.imageRenderResponse.setMimeType(type);
		assertSame(type, this.imageRenderResponse.getMimeType());
	}
}
