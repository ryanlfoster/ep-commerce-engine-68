/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.controller.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.SocketException;
import java.net.URL;

import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.elasticpath.sfweb.util.AssetResourceRetrievalStrategy;

/**
 * Test for AssetResourceControllerImpl that allows streaming out
 * resources.
 */
@SuppressWarnings({ "PMD.AvoidThrowingRawExceptionTypes" })
public class AssetResourceControllerImplTest {

	private AssetResourceControllerImpl controller;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * setup the objects for testing.
	 */
	@Before
	public void setupTest() {
		controller = new AssetResourceControllerImpl();
	}
	
	/**
	 * Test that SocketException check upon a given exception (Exception)
	 * evaluates to false.
	 */
	@Test
	public void testIsSocketExceptionOnException() {
		final int threeLevelsDeep = 3;
		assertFalse(controller.isSocketException(new Exception(), threeLevelsDeep));
	}
	
	/**
	 * Test that SocketException check upon a given exception (SocketException)
	 * evaluates to true.
	 */
	@Test
	public void testIsSocketExceptionOnSocketException() {
		final int threeLevelsDeep = 3;
		assertTrue(controller.isSocketException(new SocketException(), threeLevelsDeep));
		
	}
	
	/**
	 * Test that SocketException check upon a given exception (SocketException
	 * nested within Exception) evaluates to true with a 1 level deep scan.
	 */
	@Test
	public void testIsSocketExceptionOn1LevelDeepSocketExceptionWithLevel1Scan() {
		final int threeLevelsDeep = 1;
		Exception nestedOnce = new Exception(new SocketException());
		assertTrue(controller.isSocketException(nestedOnce, threeLevelsDeep));
	}
	
	/**
	 * Test that SocketException check upon a given exception (SocketException
	 * nested within Exception 2 levels deep) evaluates to false with a 1 
	 * level deep scan.
	 */
	@Test
	public void testIsSocketExceptionOn2LevelDeepSocketExceptionWithLevel1Scan() {
		final int threeLevelsDeep = 1;
		Exception nestedOnce = new Exception(new Exception(new SocketException()));
		assertFalse(controller.isSocketException(nestedOnce, threeLevelsDeep));
	}
	
	/**
	 * Test that SocketException check is null safe.
	 */
	@Test
	public void testIsSocketExceptionNullSafe() {
		final int threeLevelsDeep = 2;
		assertFalse(controller.isSocketException(null, threeLevelsDeep));
	}
	
	/**
	 * Tests that the content-disposition header only contains inline with no filename.
	 * 
	 * @throws Exception if any exception occurs.
	 */
	@Test
	public void testFileNameNotInContentDispositionHeader() throws Exception {
		
		AssetResourceControllerImpl controller = new AssetResourceControllerImpl() {
			@Override
			void copyResourceIntoResponse(final HttpServletResponse response,
				final URL requestedResourceUrl) throws IOException {
				// no-op
			}
		};
		
		
		final AssetResourceRetrievalStrategy retrievalStrategy = context.mock(AssetResourceRetrievalStrategy.class);
		controller.setResourceRetrievalStrategy(retrievalStrategy);
		
		final URL fileUrl = new URL("file:///path/to/test.vm");
		
		context.checking(new Expectations() { {
			allowing(retrievalStrategy).resolveResource(with(aNonNull(String.class))); will(returnValue(fileUrl));
		} });
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		controller.handleRequestInternal(request, response);
		
		assertEquals(
				"Filename should not be set on content-disposition header",
				"inline; filename=test.vm", response
						.getHeader("Content-disposition"));
		
	}
}
