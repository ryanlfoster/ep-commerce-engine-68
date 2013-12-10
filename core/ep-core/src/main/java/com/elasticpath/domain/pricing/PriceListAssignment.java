package com.elasticpath.domain.pricing;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.persistence.api.Entity;

/**
 * Price List Assignments are used to assign Price Lists to Catalogs 
 * and are similar in concept to Dynamic Content Delivery. 
 */
public interface PriceListAssignment extends Entity {

	/**
	 * @return name
	 */
	String getName();

	/**
	 * Sets the name.
	 * 
	 * @param name name to set
	 */
	void setName(String name);

	/**
	 * @return description
	 */
	String getDescription();

	/**
	 * Sets the description.
	 * 
	 * @param description description to set.
	 */
	void setDescription(String description);

	/**
	 * @return the priority.
	 */
	int getPriority();

	/**
	 * Sets the priority.
	 * 
	 * @param priority priority to set.
	 */
	void setPriority(int priority);

	/**
	 * @return catalog
	 */
	Catalog getCatalog();

	/**
	 * Sets the catalog.
	 * 
	 * @param catalog catalog to set.
	 */
	void setCatalog(Catalog catalog);

	/**
	 * @return price list descriptor
	 */
	PriceListDescriptor getPriceListDescriptor();

	/**
	 * Sets the price list descriptor.
	 * 
	 * @param priceListDescriptor price list descriptor to set.
	 */
	void setPriceListDescriptor(PriceListDescriptor priceListDescriptor);

	/**
	 * @return Selling context
	 */
	SellingContext getSellingContext();

	/**
	 * Sets the selling context.
	 * 
	 * @param sellingContext selling context to set
	 */
	void setSellingContext(SellingContext sellingContext);

	/**
	 * Determines whether or not this price list assignment is hidden.
	 * @return true if the PLA is hidden, false otherwise.
	 */
	boolean isHidden();
	
	/**
	 * Sets whether or not the assignment is hidden.
	 * @param hidden the boolean to set
	 */
	void setHidden(boolean hidden);
	
}
