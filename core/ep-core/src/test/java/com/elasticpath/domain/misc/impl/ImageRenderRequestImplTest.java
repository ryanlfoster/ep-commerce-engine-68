package com.elasticpath.domain.misc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.misc.ImageRenderRequest;

/**
 * Test <code>ImageRenderRequestImpl</code>.
 */
public class ImageRenderRequestImplTest {


	private ImageRenderRequest imageRenderRequest;

	@Before
	public void setUp() throws Exception {
		this.imageRenderRequest = new ImageRenderRequestImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.ImageRenderRequestImpl.getImageName()'.
	 */
	@Test
	public void testGetImageName() {
		assertNull(this.imageRenderRequest.getImageName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.ImageRenderRequestImpl.setImageName(String)'.
	 */
	@Test
	public void testSetImageName() {
		final String name = "test";
		this.imageRenderRequest.setImageName(name);
		assertSame(name, this.imageRenderRequest.getImageName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.ImageRenderRequestImpl.getImageFilePath()'.
	 */
	@Test
	public void testGetImageFilePath() {
		assertNull(this.imageRenderRequest.getImageFilePath());
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.ImageRenderRequestImpl.setImageFielPath(String)'.
	 */
	@Test
	public void testSetImageFielPath() {
		final String path = "test";
		this.imageRenderRequest.setImageFielPath(path);
		assertSame(path, this.imageRenderRequest.getImageFilePath());
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.ImageRenderRequestImpl.getRequiredWidth()'.
	 */
	@Test
	public void testGetRequiredWidth() {
		assertEquals(0, this.imageRenderRequest.getRequiredWidth());
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.ImageRenderRequestImpl.setRequiredWidth(int)'.
	 */
	@Test
	public void testSetRequiredWidth() {
		this.imageRenderRequest.setRequiredWidth(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, this.imageRenderRequest.getRequiredWidth());

		this.imageRenderRequest.setRequiredWidth(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, this.imageRenderRequest.getRequiredWidth());
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.ImageRenderRequestImpl.getRequiredHeight()'.
	 */
	@Test
	public void testGetRequiredHeight() {
		assertEquals(0, this.imageRenderRequest.getRequiredHeight());
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.ImageRenderRequestImpl.setRequiredHeight(int)'.
	 */
	@Test
	public void testSetRequiredHeight() {
		this.imageRenderRequest.setRequiredHeight(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, this.imageRenderRequest.getRequiredHeight());

		this.imageRenderRequest.setRequiredHeight(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, this.imageRenderRequest.getRequiredHeight());
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.ImageRenderRequestImpl.getPadding()'.
	 */
	@Test
	public void testGetPadding() {
		assertEquals(0, this.imageRenderRequest.getPadding());
	}

	/**
	 * Test method for 'com.elasticpath.domain.misc.impl.ImageRenderRequestImpl.setPadding(int)'.
	 */
	@Test
	public void testSetPadding() {
		this.imageRenderRequest.setPadding(Integer.MIN_VALUE);
		assertEquals(Integer.MIN_VALUE, this.imageRenderRequest.getPadding());

		this.imageRenderRequest.setPadding(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, this.imageRenderRequest.getPadding());
	}

}
