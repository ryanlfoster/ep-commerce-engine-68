package com.elasticpath.service.contentspace;

import java.util.List;
import java.util.Map;

import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.ParameterValue;

/**
 * Responsible for resolving values out of parameters and their parameter values.  
 */
public interface ParameterValueResolver {

	/**
	 * Resolves localized values for parameters using the parameter values supplied.
	 *  
	 * @param contentWrapper for get parameter definitions.
	 * @param parameterValues the parameter values list
	 * @param language the language or null if none specified
	 * @param context dynamic context map
	 * @throws ParameterResolvingException  if not all the parameters could be resolved.
	 * @return a map of parameter name - parameter value pairs or empty map if nothing was resolved
	 */
	Map<String, String> resolveValues(
			ContentWrapper contentWrapper,
			List<ParameterValue> parameterValues,
			Map<String, Object> context,
			String language) throws ParameterResolvingException;	
}
