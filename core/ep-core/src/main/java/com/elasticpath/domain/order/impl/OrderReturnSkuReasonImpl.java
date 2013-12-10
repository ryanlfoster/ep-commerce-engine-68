package com.elasticpath.domain.order.impl;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.misc.impl.AbstractPropertyBasedImpl;
import com.elasticpath.domain.order.OrderReturnSkuReason;

/**
 * A <code>OrderReturnSkuReason</code> represents a reason associated with a <code>OrderReturnSku</code>.
 */
public class OrderReturnSkuReasonImpl extends AbstractPropertyBasedImpl implements OrderReturnSkuReason {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Returns a map of available return sku reasons.
	 * 
	 * @return the map of available reasons
	 */
	public Map<String, String> getReasonMap() {
		return super.getPropertyMap(getPropertyName());
	}

	/**
	 * Returns a map of available sku reasons based on locale.
	 * 
	 * @param locale the selected locale
	 * @return a map of available reasons based on locale
	 */
	public Map<String, String> getReasonMap(final Locale locale) {
		return super.getPropertyMap(getPropertyName(), locale);
	}

}
