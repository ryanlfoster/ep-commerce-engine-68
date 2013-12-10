package com.elasticpath.web.security;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.owasp.esapi.filters.SecurityWrapperRequest;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 * Test case for {@link EsapiServletUtils}.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class EsapiServletUtilsTest {

	/**
	 * If a request has the given parameter, {@link EsapiServletUtils#hasParameter(ServletRequest, String)} should
	 * return {@code true}.
	 */
	@Test
	public void testHasParameterParamExists() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("exists", "value");
		request.setParameter("another", "zvalue");
		request.setParameter("morethan", "onemore");

		assertTrue("Parameter exists in the request", EsapiServletUtils.hasParameter(request, "exists"));
	}

	/**
	 * If a request doesn't have the parameter, {@link EsapiServletUtils#hasParameter(ServletRequest, String)} should
	 * return {@code false}.
	 */
	@Test
	public void testHasParameterParamsDNE() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setParameter("some", "value");
		request.setParameter("another", "invalid value");
		request.setParameter("one", "more");

		assertFalse("Parameter doesn't exist in the request", EsapiServletUtils.hasParameter(request, "missing"));
	}

	/** When a {@link SecurityWrapperRequest} is the only one and is on top, it should be removed directly. */
	@Test
	public void testUnwrapRequestDirect() {
		HttpServletRequest parentRequest = new DummyServletRequestWrapper(new MockHttpServletRequest());
		ServletRequest request = new SecurityWrapperRequest(parentRequest);

		assertEquals("Should only remove the top-level wrapper", parentRequest, EsapiServletUtils.findUnsafeRequest(request));
	}

	/**
	 * When there are multiple {@link SecurityWrapperRequest}s and various other requests mixed inbetween them,
	 * everything above the lowest {@link SecurityWrapperRequest} including the security wrapper should be removed.
	 */
	@Test
	public void testUnwrapRequestMultiple() {
		HttpServletRequest parentRequest = new DummyServletRequestWrapper(new MockHttpServletRequest());
		HttpServletRequest securityRequest = new SecurityWrapperRequest(parentRequest);
		HttpServletRequest requestAboveSecurity = new DummyServletRequestWrapper(securityRequest);
		HttpServletRequest toplevelRequest = new SecurityWrapperRequest(requestAboveSecurity);

		assertFalse("Did not unwrap multiple security requests",
				requestAboveSecurity.equals(EsapiServletUtils.findUnsafeRequest(toplevelRequest)));
		assertEquals("Should remove all wrappers above the bottom-most security wrapper", parentRequest,
				EsapiServletUtils.findUnsafeRequest(toplevelRequest));
	}

	/** Ensure that requests are wrapped in a {@link SecurityWrapperRequest}. */
	@Test
	public void testWrapRequest() {
		Matcher<HttpServletRequest> isASecurityWrapperMatcher = instanceOf(SecurityWrapperRequest.class);
		assertThat("Request should be wrapped", EsapiServletUtils.secureHttpRequest(new MockHttpServletRequest()),
				isASecurityWrapperMatcher);
	}

	/** If a request is already wrapped by {@link SecurityWrapperRequest}, it should not be wrapped again. */
	@Test
	public void testWrapRequestAlreadyWrapped() {
		HttpServletRequest securityRequest = new SecurityWrapperRequest(new MockHttpServletRequest());
		HttpServletRequest toplevelRequset = new DummyServletRequestWrapper(securityRequest);

		assertEquals("Wrappers above the security request should not be lost", toplevelRequset,
				EsapiServletUtils.secureHttpRequest(toplevelRequset));
		assertEquals("Request should not be wrapped twice", securityRequest, EsapiServletUtils.secureHttpRequest(securityRequest));
	}

	/** Dummy wrapper class for tests. */
	private static class DummyServletRequestWrapper extends HttpServletRequestWrapper {
		public DummyServletRequestWrapper(final HttpServletRequest request) {
			super(request);
		}
	}
}
