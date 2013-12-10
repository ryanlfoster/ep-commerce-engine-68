package com.elasticpath.domain.store.impl;

import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.misc.impl.AbstractPropertyBasedImpl;
import com.elasticpath.domain.store.AdjustmentQuantityOnHandReason;

/**
 * Represents adjustment quantity on hand reasons.
 */
public class AdjustmentQuantityOnHandReasonImpl extends AbstractPropertyBasedImpl implements AdjustmentQuantityOnHandReason {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * Returns a map of adjustment quantity on hand reasons.
	 * 
	 * @return the map of available reasons
	 */
	public Map<String, String> getReasonMap() {
		return super.getPropertyMap(getPropertyName());
	}

	/**
	 * Returns a map of adjustment quantity on hand reasons based on locale.
	 * 
	 * @param locale the selected locale
	 * @return a map of available reasons based on locale
	 */
	public Map<String, String> getReasonMap(final Locale locale) {
		return super.getPropertyMap(getPropertyName(), locale);
	}

}
