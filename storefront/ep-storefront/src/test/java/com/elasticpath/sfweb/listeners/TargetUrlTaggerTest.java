/**
 * Copyright (c) 2009. ElasticPath Software Inc. All rights reserved.
 */
package com.elasticpath.sfweb.listeners;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.facade.impl.HttpServletFacadeFactoryImpl;
import com.elasticpath.tags.TagSet;

/**
 * Tests for {@link TargetUrlTagger}.
 */
public class TargetUrlTaggerTest {
	private TargetUrlTagger targetUrlTagger;
	private CustomerSession session;
	private TagSet tagSet;
	private static final String QUESTION_MARK = "?";
	
	/**
	 * Sets up the initial steps.
	 */
	@Before
	public void initialize() {
		targetUrlTagger = new TargetUrlTagger();
		tagSet = new TagSet();
		
		session = new CustomerSessionImpl() {
			private static final long serialVersionUID = 7596323801624805900L;

			@Override
			public final TagSet getCustomerTagSet() {
				return tagSet;
			}
		};
	}
	
    private HttpServletRequestFacade setupRequestFacade(final HttpServletRequest request) {
		final HttpServletFacadeFactory httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(null, null, null);
		return httpServletFacadeFactory.createRequestFacade(request);
    }
	
	/**
	 * Tests if the execute method puts the TARGET_URL into the tag set.
	 */
	@Test
	public void testExecutePutsTheTargetURLIntoTagSet() {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/index.ep");
		HttpServletRequestFacade requestFacade = setupRequestFacade(request);
		request.setQueryString("coupon=go");
		request.setLocalAddr("demo.elasticpath.com");
		request.setLocalPort(Integer.parseInt("8080"));
		
		targetUrlTagger.execute(session, requestFacade);
		
		StringBuffer requestURL = request.getRequestURL();
		requestURL.append(QUESTION_MARK);
		requestURL.append(request.getQueryString());

		assertEquals(1, tagSet.getTags().size());
		assertEquals(tagSet.getTagValue("TARGET_URL").getValue(), requestURL.toString());
	}
	
	/**
	 * Tests if the execute method puts the TARGET_URL into the tag set.
	 */
	@Test
	public void testBlankQueryInRequest() {
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/index.ep");
		HttpServletRequestFacade requestFacade = setupRequestFacade(request);
		request.setLocalAddr("demo.elasticpath.com");
		request.setLocalPort(Integer.parseInt("8080"));
		
		targetUrlTagger.execute(session, requestFacade);
		
		StringBuffer requestURL = request.getRequestURL();

		assertEquals(1, tagSet.getTags().size());
		assertEquals(tagSet.getTagValue("TARGET_URL").getValue(), requestURL.toString());
	}
}
