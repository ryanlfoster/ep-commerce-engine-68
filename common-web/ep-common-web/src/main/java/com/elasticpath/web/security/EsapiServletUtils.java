package com.elasticpath.web.security;

import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.esapi.filters.SecurityWrapperRequest;
import org.owasp.esapi.filters.SecurityWrapperResponse;
import org.springframework.web.util.WebUtils;

/**
 * Provides utility methods for servlet resources which may require a specific strategy due to interaction with ESAPI.
 */
public final class EsapiServletUtils {

	private EsapiServletUtils() {
		// static class
	}

	/**
	 * Determines if the given {@code parameterName} exists in {@link ServletRequest}. The parameter exists
	 * if it is defined for the request regardless of the value it has.
	 * <p>
	 * This method spares
	 *
	 * @param request {@link ServletRequest} to check
	 * @param parameterName parameter expected to check
	 * @return whether the parameter exists
	 */
	public static boolean hasParameter(final ServletRequest request, final String parameterName) {
		for (Enumeration<?> iterator = request.getParameterNames(); iterator.hasMoreElements();) {
			if (iterator.nextElement().equals(parameterName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes all {@link SecurityWrapperRequest}s on the given {@link ServletRequest}. If there are any wrappers
	 * on top of the {@link SecurityWrapperRequest}, they are removed as well.
	 *
	 * @param request {@link ServletRequest} to unwrap
	 * @return unwrapped {@link ServletRequest}
	 */
	// no one other this package should be calling this method
	/* package */ static ServletRequest findUnsafeRequest(final ServletRequest request) {
		SecurityWrapperRequest secureRequest = WebUtils.getNativeRequest(request, SecurityWrapperRequest.class);
		if (secureRequest != null) {
			ServletRequest parentRequest = secureRequest.getRequest();
			SecurityWrapperRequest secureParent = WebUtils.getNativeRequest(parentRequest, SecurityWrapperRequest.class);
			if (secureParent != null) {
				// security request wrapped in a security request
				return findUnsafeRequest(parentRequest);
			}
			// single security wrapped request
			return parentRequest;
		}
		// not wrapped at all
		return request;
	}

	/**
	 * Secures the given {@link HttpServletRequest} by wrapping it with {@link SecurityWrapperRequest}. This method does
	 * nothing if the given request is already wrapped in {@link SecurityWrapperRequest}.
	 *
	 * @param request {@link HttpServletRequest} to wrap
	 * @return request wrapped in {@link SecurityWrapperRequest}
	 */
	public static HttpServletRequest secureHttpRequest(final HttpServletRequest request) {
		if (WebUtils.getNativeRequest(request, SecurityWrapperRequest.class) == null) {
			return new SecurityWrapperRequest(request);
		}
		return request; // don't return secureRequest; we want the wrappers on top of it
	}
	
	/**
	 * Secures the given {@link HttpServletResponse} by wrapping it with {@link SecurityWrapperResponse}. This method does
	 * nothing if the given response is already wrapped in {@link SecurityWrapperResponse}.
	 *
	 * @param response the {@link HttpServletResponse} to wrap
	 * @return response wrapped in {@link SecurityWrapperResponse}
	 */
	public static HttpServletResponse secureHttpResponse(final HttpServletResponse response) {
		if (WebUtils.getNativeResponse(response, SecurityWrapperResponse.class) == null) {
			return new SecurityWrapperResponse(response);
		}
		return response;
	}
}
