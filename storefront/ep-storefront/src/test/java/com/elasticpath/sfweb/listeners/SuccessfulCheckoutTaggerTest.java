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

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.facade.impl.HttpServletFacadeFactoryImpl;
import com.elasticpath.tags.TagSet;

/**
*	Tests {@link SuccessfulCheckoutTagger} class.
*/
public class SuccessfulCheckoutTaggerTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final String FIRST_TIME_BUYER = "FIRST_TIME_BUYER";
	
	private TagSet tagSet;
	private MockHttpServletRequest request;
	private HttpServletRequestFacade requestFacade;
	private CustomerSession session;
	
	private SuccessfulCheckoutTagger successfulCheckoutTagger;
	
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
		successfulCheckoutTagger = new SuccessfulCheckoutTagger();
	}
	
	/**
	 * Tests that shopping start time is set correctly.
	 */
	@Test
	public void testShoppingStartTime() {
		context.checking(new Expectations() { {
			allowing(session).getCustomerTagSet(); will(returnValue(tagSet));
		} });
		
		assertNull("FIRST_TIME_BUYER was not null", tagSet.getTagValue(FIRST_TIME_BUYER));

		successfulCheckoutTagger.execute(session, requestFacade);
		
		assertNotNull("FIRST_TIME_BUYER was null", tagSet.getTagValue(FIRST_TIME_BUYER));
		
		assertEquals("SHOPPING_START_TIME value setted in tag set is incorrect", false, 
				tagSet.getTagValue(FIRST_TIME_BUYER).getValue());
		

	}	
	
	

}
