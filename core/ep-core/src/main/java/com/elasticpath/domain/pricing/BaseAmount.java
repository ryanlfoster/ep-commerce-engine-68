package com.elasticpath.domain.pricing;

import java.math.BigDecimal;

import com.elasticpath.persistence.api.Persistable;

/**
 * Domain representation for the quantifier portion of a BasePrice.
 * List and Sale values when combined with a PriceListDescriptor becomes a BasePrice for 
 * the given quantity of a Product or SKU.
 * 
 * BaseAmounts are uniquely defined either by their GUID, or by the combination key of 
 * ObjectGuid, ObjectType, and Quantity.
 */
public interface BaseAmount extends Persistable {
    
    /** Maximum quantity. */
    int MAX_QTY = 10000;
    
	/**
	 * @return the object GUID of the Product/SKU for this base amount.
	 */
	String getObjectGuid();
	
	/**
	 * Get the object type for this base amount.
	 *  
	 * @return Type Product or SKU
	 */
	String getObjectType();

	/**
	 * @return The quantity threshold at which this particular List Price and Sale Price take 
	 * affect for a particular Product or SKU when in a Price List.
	 */
	BigDecimal getQuantity();
	
	/**
	 * @return the list value.
	 */
	BigDecimal getListValue();

	/**
	 * @return the sale value.
	 */
	BigDecimal getSaleValue();

	/**
	 * @param list the list value.
	 */
	void setListValue(BigDecimal list);

	/**
	 * @param sale the sale value.
	 */
	void setSaleValue(BigDecimal sale);	
	
	/**
	 * @return the associated {@link com.elasticpath.domain.pricing.PriceListDescriptor PriceListDescriptor} GUID
	 */
	String getPriceListDescriptorGuid();
	
	/**
	 * GUIDs are needed on the BaseAmount to simplify identification 
	 * for future extensions and indexing, rather than referencing all of the unique key fields.
	 * 
	 * @return the GUID of this BaseAmount
	 */
	String getGuid();

	/**
	 * Quantity should be integer. But it is represented by BigDecimal.
	 * In order to process quantity correctly its scale should be set to zero after 
	 * the object has been created and verified.
	 * 
	 * TODO: Correct implementation should verify validity of input data 
	 * before the object has been created. Even better would be to have it Integer.
	 */
	void setQuantityScaleToInteger();
	
}
