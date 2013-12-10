/**
 * 
 */
package com.elasticpath.sfweb.tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.sfweb.servlet.facade.HttpServletFacadeFactory;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestFacade;
import com.elasticpath.sfweb.servlet.facade.impl.HttpServletFacadeFactoryImpl;
import com.elasticpath.sfweb.servlet.listeners.BrowsingBehaviorEventListener;
import com.elasticpath.sfweb.tags.impl.TagProcessorsHolderImpl;

/**
 * TagProcessorsHolderTest test for TagProcessorsHolder.
 *
 */
public class TagProcessorsHolderTest {

	private final HttpServletRequest httpServletRequest = new MockHttpServletRequest();
	private HttpServletRequestFacade requestFacade;
	private TagProcessorsHolder tagProcessorHolder;
	private CustomerSession customerSession;
	private List<BrowsingBehaviorEventListener> listeners;
	private int counter;
	
	/**
	 * Test setup.
	 */
	@Before
	public void setUp() {
		final HttpServletFacadeFactory httpServletFacadeFactory = new HttpServletFacadeFactoryImpl(null, null, null);
		requestFacade = httpServletFacadeFactory.createRequestFacade(httpServletRequest);
	    
		tagProcessorHolder = new TagProcessorsHolderImpl();
		customerSession = new CustomerSessionImpl();
		listeners = new ArrayList<BrowsingBehaviorEventListener>();
		counter = 0;
	}
	
	/**
	 * Empty tagger list must not crash. 
	 */
	@Test
	public void testEmpty() {
		this.tagProcessorHolder.fireExecute(customerSession, requestFacade);
		assertEquals(counter, 0);
	}
	
	/**
	 * Test that tagger in list executed on fireExecute method.
	 */
	@Test
	public void testListener() {
		this.listeners.add(new BrowsingBehaviorEventListener() {
			public void execute(final CustomerSession session, final HttpServletRequestFacade request) {
				counter++;
				assertNotNull(session);
				assertNotNull(request);
			}
			}
		);
		this.listeners.add(new BrowsingBehaviorEventListener() {
			public void execute(final CustomerSession session, final HttpServletRequestFacade request) {
				counter++;
				assertNotNull(session);
				assertNotNull(request);
			}
			}
		);
		this.tagProcessorHolder.setListTagsProcessors(listeners);
		
		this.tagProcessorHolder.fireExecute(customerSession, requestFacade);
		assertEquals(counter, 2);
	}
	
}
