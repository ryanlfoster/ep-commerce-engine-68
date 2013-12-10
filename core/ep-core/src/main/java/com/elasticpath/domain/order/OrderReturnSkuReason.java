package com.elasticpath.domain.order;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.misc.PropertyBased;

/**
 * A <code>OrderReturnSkuReason</code> represents a reason associated with a <code>OrderReturnSku</code>.
 */
public interface OrderReturnSkuReason extends PropertyBased {

	/**
	 * Returns a map of available return sku reasons.
	 * 
	 * @return the map of available reasons
	 */
	Map<String, String> getReasonMap();

	/**
	 * Returns a map of available sku reasons based on locale.
	 * 
	 * @param locale the selected locale
	 * @return a map of available reasons based on locale
	 */
	Map<String, String> getReasonMap(final Locale locale);

}
