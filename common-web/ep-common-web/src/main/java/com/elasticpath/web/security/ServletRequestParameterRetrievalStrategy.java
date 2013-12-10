package com.elasticpath.web.security;

import javax.servlet.ServletRequest;

/**
 * Represents a strategy for retrieving parameter values from a {@link ServletRequest}.
 */
public interface ServletRequestParameterRetrievalStrategy {
	/**
	 * Retrieves the values for the given {@link ServletRequest} for parameters which have the given name.
	 * 
	 * @param request {@link ServletRequest} to retrieve parameters from
	 * @param parameterName parameter name to get values for
	 * @param config {@link ParameterConfiguration} that should be used for retrieving a value
	 * @return array of all values for that parameter
	 * @see ServletRequest#getParameterValues(String)
	 */
	String[] getParameterValues(ServletRequest request, String parameterName, ParameterConfiguration config);
}
