package com.elasticpath.sfweb.filters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.elasticpath.service.catalogview.impl.ThreadLocalStorageImpl;
import com.elasticpath.sfweb.util.StoreResolver;

/**
 * Tests that StoreSelectionFilter works properly.
 */
public class StoreSelectionFilterTest {

	private static final String UNEXPECTED_SERVLET_EXCEPTION = "Unexpected servlet exception: ";
	private static final String UNEXPECTED_IO_ERROR = "Unexpected I/O error: ";
	private StoreSelectionFilter filter;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private MockFilterChain filterChain;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	private ThreadLocalStorageImpl storeConfig;
	private StoreResolver storeResolver;

	/**
	 * Set up objects required for all tests.
	 * 
	 * @throws java.lang.Exception in case of an error setting up the objects.
	 */
	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		filterChain = new MockFilterChain();

		storeConfig = context.mock(ThreadLocalStorageImpl.class);
		storeResolver = context.mock(StoreResolver.class);
		
		filter = new StoreSelectionFilter();
		filter.setStoreConfig(storeConfig);
		filter.setStoreResolver(storeResolver);
	}
	
	/**
	 * Test that the filter with no selection candidates returns an error in the response.
	 */
	@Test
	public void testFilterWithNoCandidates() {
		try {
			filter.doFilter(request, response, filterChain);
		} catch (IOException e) {
			fail(UNEXPECTED_IO_ERROR + e);
		} catch (ServletException e) {
			fail(UNEXPECTED_SERVLET_EXCEPTION + e);
		}
		assertEquals("Reponse should return 200 OK", HttpServletResponse.SC_OK, response.getStatus());
		assertEquals("Reponse should be empty", 0, response.getContentLength());
	}
	
	/**
	 * Tests that the filter returns an error when the selection candidate doesn't resolve. 
	 */
	@Test
	public void testFilterWithNonResolvingCandidate() {
		List<String> candidateList = new ArrayList<String>();
		candidateList.add("DomainHeader=HOST");
		filter.setSelectionCandidates(candidateList);
		context.checking(new Expectations() {
			{
				oneOf(storeResolver).resolveDomainHeader(request, "HOST");
				will(returnValue(null));
			}
		});
		try {
			filter.doFilter(request, response, filterChain);
		} catch (IOException e) {
			fail(UNEXPECTED_IO_ERROR + e);
		} catch (ServletException e) {
			fail(UNEXPECTED_SERVLET_EXCEPTION + e);
		}
		assertEquals("Reponse should return 200 OK", HttpServletResponse.SC_OK, response.getStatus());
		assertEquals("Reponse should be empty", 0, response.getContentLength());
	}
	
	/**
	 * Test that a domain header candidate causes the correct resolver method to be called.
	 */
	@Test
	public void testFilterWithDomainHeaderCandidate() {
		List<String> candidateList = new ArrayList<String>();
		candidateList.add("DomainHeader=HOST");
		filter.setSelectionCandidates(candidateList);
		context.checking(new Expectations() {
			{
				oneOf(storeResolver).resolveDomainHeader(request, "HOST");
				will(returnValue("store1"));
				
				oneOf(storeConfig).setStoreCode("store1");
			}
		});
		try {
			filter.doFilter(request, response, filterChain);
		} catch (IOException e) {
			fail(UNEXPECTED_IO_ERROR + e);
		} catch (ServletException e) {
			fail(UNEXPECTED_SERVLET_EXCEPTION + e);
		}
	}

	/**
	 * Test that a domain param candidate causes the correct resolver method to be called.
	 */
	@Test
	public void testFilterWithDomainParamCandidate() {
		List<String> candidateList = new ArrayList<String>();
		candidateList.add("DomainParam=domain");
		filter.setSelectionCandidates(candidateList);
		context.checking(new Expectations() {
			{
				oneOf(storeResolver).resolveDomainParam(request, "domain");
				will(returnValue("store2"));
				
				oneOf(storeConfig).setStoreCode("store2");
			}
		});
		try {
			filter.doFilter(request, response, filterChain);
		} catch (IOException e) {
			fail(UNEXPECTED_IO_ERROR + e);
		} catch (ServletException e) {
			fail(UNEXPECTED_SERVLET_EXCEPTION + e);
		}
	}
	
	/**
	 * Test that a store code header candidate causes the correct resolver method to be called.
	 */
	@Test
	public void testFilterWithStoreCodeHeaderCandidate() {
		List<String> candidateList = new ArrayList<String>();
		candidateList.add("StoreCodeHeader=STORECODE");
		filter.setSelectionCandidates(candidateList);
		context.checking(new Expectations() {
			{
				oneOf(storeResolver).resolveStoreCodeHeader(request, "STORECODE");
				will(returnValue("store3"));
				
				oneOf(storeConfig).setStoreCode("store3");
			}
		});
		try {
			filter.doFilter(request, response, filterChain);
		} catch (IOException e) {
			fail(UNEXPECTED_IO_ERROR + e);
		} catch (ServletException e) {
			fail(UNEXPECTED_SERVLET_EXCEPTION + e);
		}
	}

	/**
	 * Test that a domain header candidate causes the correct resolver method to be called.
	 */
	@Test
	public void testFilterWithStoreCodeParamCandidate() {
		List<String> candidateList = new ArrayList<String>();
		candidateList.add("StoreCodeParam=storeCode");
		filter.setSelectionCandidates(candidateList);
		context.checking(new Expectations() {
			{
				oneOf(storeResolver).resolveStoreCodeParam(request, "storeCode");
				will(returnValue("store4"));
				
				oneOf(storeConfig).setStoreCode("store4");
			}
		});
		try {
			filter.doFilter(request, response, filterChain);
		} catch (IOException e) {
			fail(UNEXPECTED_IO_ERROR + e);
		} catch (ServletException e) {
			fail(UNEXPECTED_SERVLET_EXCEPTION + e);
		}
	}
	
	/**
	 * Test that the first valid candidate of multiple candidates is used by the filter.
	 */
	@Test
	public void testFilterWithMultipleCandidates() {
		List<String> candidateList = new ArrayList<String>();
		candidateList.add("DomainHeader=DOMAIN");
		candidateList.add("StoreCodeParam=storeCode");
		candidateList.add("DomainParam=host");
		filter.setSelectionCandidates(candidateList);
		final Sequence expectedSequence = context.sequence("expected-sequence");
		context.checking(new Expectations() {
			{
				oneOf(storeResolver).resolveDomainHeader(request, "DOMAIN");
				will(returnValue(null));
				inSequence(expectedSequence);
				
				oneOf(storeResolver).resolveStoreCodeParam(request, "storeCode");
				will(returnValue("store5"));
				inSequence(expectedSequence);
				
				oneOf(storeConfig).setStoreCode("store5");
				inSequence(expectedSequence);
				
				never(storeResolver).resolveDomainParam(request, "host");
			}
		});
		try {
			filter.doFilter(request, response, filterChain);
		} catch (IOException e) {
			fail(UNEXPECTED_IO_ERROR + e);
		} catch (ServletException e) {
			fail(UNEXPECTED_SERVLET_EXCEPTION + e);
		}
	}

	/**
	 * Test that an invalid candidate will resolve to null store code.
	 */
	@Test
	public void testResolveCandidateWithInvalidCandidate() {
		assertNull("null candidate should resolve to null", filter.resolveCandidate(request, null));
		assertNull("empty candidate should resolve to null", filter.resolveCandidate(request, ""));
		assertNull("candidate that is not delimited by = should resolve to null", filter.resolveCandidate(request, "DomainHeaderHOST"));
	}
	
	/**
	 * Test resolve candidate for domain header calls resolveDomainHeader.
	 */
	@Test
	public void testResolveDomainHeaderCandidate() {
		context.checking(new Expectations() {
			{
				oneOf(storeResolver).resolveDomainHeader(request, "HOST");
				will(returnValue("store6"));
			}
		});
		assertEquals("domain header candidate should call expected store resolver", "store6", filter.resolveCandidate(request, "DomainHeader=HOST"));
	}

	/**
	 * Test resolve candidate for domain header calls resolveDomainHeader.
	 */
	@Test
	public void testResolveDomainParamCandidate() {
		context.checking(new Expectations() {
			{
				oneOf(storeResolver).resolveDomainParam(request, "host");
				will(returnValue("store7"));
			}
		});
		assertEquals("domain param candidate should call expected store resolver", "store7", filter.resolveCandidate(request, "DomainParam=host"));
	}
	/**
	 * Test resolve candidate for domain header calls resolveDomainHeader.
	 */
	@Test
	public void testResolveStoreCodeHeaderCandidate() {
		context.checking(new Expectations() {
			{
				oneOf(storeResolver).resolveStoreCodeHeader(request, "STORECODE");
				will(returnValue("store8"));
			}
		});
		assertEquals("store code header candidate should call expected store resolver", "store8", 
				filter.resolveCandidate(request, "StoreCodeHeader=STORECODE"));
	}
	/**
	 * Test resolve candidate for domain header calls resolveDomainHeader.
	 */
	@Test
	public void testResolveStoreCodeParamCandidate() {
		context.checking(new Expectations() {
			{
				oneOf(storeResolver).resolveStoreCodeParam(request, "storeCode");
				will(returnValue("store9"));
			}
		});
		assertEquals("store code param candidate should call expected store resolver", "store9", 
				filter.resolveCandidate(request, "StoreCodeParam=storeCode"));
	}
}
