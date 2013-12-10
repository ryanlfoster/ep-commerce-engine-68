package com.elasticpath.domain.pricing;

import java.math.BigDecimal;

import com.elasticpath.persistence.api.Persistable;

/**
 * Price adjustment for bundle constituent.
 */
public interface PriceAdjustment extends Persistable {

	/**
	 * @return the adjustment
	 */
	BigDecimal getAdjustmentAmount();

	/**
	 * @return the associated {@link com.elasticpath.domain.pricing.PriceListDescriptor PriceListDescriptor} GUID
	 */
	String getPriceListGuid();
	
	/**
	 * GUID for identification.
	 * 
	 * @return the GUID of this PriceAdjustment
	 */
	String getGuid();
	
	/**
	 * @param guid the GUID of this PriceAdjustment
	 */
	void setGuid(String guid);
	
	/**
	 * @param guid the Price List guid.
	 */
	void setPriceListGuid(final String guid);
		
	/**
	 * @param amount the adjustment amount.
	 */
	void setAdjustmentAmount(final BigDecimal amount);
}
