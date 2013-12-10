package com.elasticpath.domain.store;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.misc.PropertyBased;

/**
 * Represents adjustment quantity on hand reasons.
 */
public interface AdjustmentQuantityOnHandReason extends PropertyBased {

	/**
	 * Returns a map of adjustment quantity on hand reasons.
	 * 
	 * @return the map of available reasons
	 */
	Map<String, String> getReasonMap();

	/**
	 * Returns a map of adjustment quantity on hand reasons based on locale.
	 * 
	 * @param locale the selected locale
	 * @return a map of available reasons based on locale
	 */
	Map<String, String> getReasonMap(final Locale locale);

}
