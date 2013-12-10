package com.elasticpath.domain.order;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.misc.PropertyBased;

/**
 * Represents received state for SKU.
 */
public interface OrderReturnReceivedState extends PropertyBased {
	
	/**
	 * Returns a map of available return received states.
	 * 
	 * @return the map of available states
	 */
	Map<String, String> getStateMap();
	
	
	/**
	 * Returns a map of available return received states based on locale. 
	 *
	 * @param locale the selected locale
	 * @return a map of available states based on locale
	 */
	Map<String, String> getStateMap(final Locale locale);
}
