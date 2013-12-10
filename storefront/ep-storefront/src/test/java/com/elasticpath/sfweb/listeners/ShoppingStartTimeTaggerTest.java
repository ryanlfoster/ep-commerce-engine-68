package com.elasticpath.sfweb.listeners;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
 *	Tests {@link StartShoppingTimeTagger} class.
 */
public class ShoppingStartTimeTaggerTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private TagSet tagSet;
	private MockHttpServletRequest request;
	private HttpServletRequestFacade requestFacade;
	private CustomerSession session;
	private StartShoppingTimeTagger listener;
	
	private static final String SHOPPING_START_TIME = "SHOPPING_START_TIME";
	
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
		listener = new StartShoppingTimeTagger();
	}
	
	/**
	 * Tests that shopping start time is set correctly.
	 */
	@Test
	public void testShoppingStartTime() {
		context.checking(new Expectations() { {
			allowing(session).getCustomerTagSet(); will(returnValue(tagSet));
		} });

		long beforeTagerTime = System.currentTimeMillis() - 1;
		
		listener.execute(session, requestFacade);
		
		long afterTagerTime = System.currentTimeMillis() + 1;
		
		assertNotNull("SHOPPING_START_TIME was null", tagSet.getTagValue(SHOPPING_START_TIME));
		
		assertTrue("SHOPPING_START_TIME value setted in tag set is incorrect", 
				afterTagerTime > Long.valueOf(tagSet.getTagValue(SHOPPING_START_TIME).getValue().toString()));
		
		assertTrue("SHOPPING_START_TIME value setted in tag set is incorrect", 
				beforeTagerTime < Long.valueOf(tagSet.getTagValue(SHOPPING_START_TIME).getValue().toString()));

	}
	
}
