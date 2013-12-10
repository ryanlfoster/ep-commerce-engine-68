package com.elasticpath.sfweb.listeners;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.facade.impl.HttpServletFacadeFactoryImpl;
import com.elasticpath.tags.TagSet;

/**
 * Test for InStoreSearchEngineQueryTagger.
 */
public class InStoreSearchEngineQueryTaggerTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private TagSet tagSet;
	private MockHttpServletRequest request;
	private HttpServletRequestFacade requestFacade;
	private CustomerSession session;
	private InStoreSearchEngineQueryTagger listener;
	
	private static final String INSTORE_SEARCH_TERMS = "INSTORE_SEARCH_TERMS";
	private static final String EXAMPLE_KEYWORD = "EXAMPLE_KEYWORD";
	
	/**
	 * Setting up instances.
	 */
	@Before
	public void setUp() {
		tagSet = new TagSet();
		request = new MockHttpServletRequest();
		final HttpServletFacadeFactory httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(null, null, null);
		requestFacade = httpServletFacadeFactory.createRequestFacade(request);
		session = context.mock(CustomerSession.class);
		listener = new InStoreSearchEngineQueryTagger();
	}

	/**
	 * Test that {@link InStoreSearchEngineQueryTagger} adds a value to tag set,
	 * basing on the keywords that were sent to the internal search engine.
	 */
	@Test
	public void testAddSubtotalToTagSet() {
		context.checking(new Expectations() { {
			allowing(session).getCustomerTagSet(); will(returnValue(tagSet));			
		} });

		request.setParameter(WebConstants.REQUEST_KEYWORDS, EXAMPLE_KEYWORD);
		
		listener.execute(session, requestFacade);
		
		assertNotNull("In-store search tag value was null", tagSet.getTagValue(INSTORE_SEARCH_TERMS));
		assertEquals("Failed to get a correct keyword tag value from the tagset", 
				EXAMPLE_KEYWORD, tagSet.getTagValue(INSTORE_SEARCH_TERMS).getValue());
	}

 	/**
 	 * Test that {@link InStoreSearchEngineQueryTagger} behaves politely when no keywords are bound to the request param.
 	 */
 	@Test
 	public void testNoSearchTermsAddedToTagSet() {
 		context.checking(new Expectations() { {
 			allowing(session).getCustomerTagSet(); will(returnValue(tagSet));
 		} });
 
 		listener.execute(session, requestFacade);
  
 		assertNull("In-store search tag value should be null", tagSet.getTagValue(INSTORE_SEARCH_TERMS));
 	}
	
}
