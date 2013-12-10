/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import java.util.Date;

import com.elasticpath.persistence.api.Entity;

/**
 * Represents a link between two products for the purpose of displaying information about related products when viewing a particular product.
 * <p>
 * Terminology:
 * <p>
 * Source Product - The product that the user is viewing when additional products are to be displayed, e.g. for upselling to a more expensive
 * product.
 * <p>
 * Target Product - The other product that is to be displayed when viewing the source product.
 */
public interface ProductAssociation extends Entity, Comparable<ProductAssociation> {

	/** Cross-sell association. */
	int CROSS_SELL = 1;

	/** Up-sell association. */
	int UP_SELL = 2;

	/** Warranty item. */
	int WARRANTY = 3;

	/** Accessory product association. */
	int ACCESSORY = 4;

	/** Replacement product association. */
	int REPLACEMENT = 5;

	/** Computed product recommendation association. */
	int RECOMMENDATION = 6;

	/**
	 * All possible association types.
	 */
	int[] ALL_ASSOCIATION_TYPES = new int[] { CROSS_SELL, UP_SELL, WARRANTY, ACCESSORY, REPLACEMENT, RECOMMENDATION };

	/**
	 * All possible association type string names. Used in CM UI only.
	 */
	String[] ALL_ASSOCIATION_TYPE_NAMES = new String[] { "Cross Sell", "Up Sell", "Warranty", "Accessory", "Replacement", "Recommendation" };

	/**
	 * Get the type of this <code>ProductAssociation</code>.
	 * 
	 * @return the association type
	 */
	int getAssociationType();

	/**
	 * Set the type of this <code>ProductAssociation</code>.
	 * 
	 * @param associationType one of the association type constants defined in this interface
	 */
	void setAssociationType(final int associationType);

	/**
	 * Get the source product by this association.
	 * 
	 * @return the sourceProduct
	 */
	Product getSourceProduct();

	/**
	 * Gets the source product.
	 * 
	 * @param sourceProduct the sourceProduct to set
	 */
	void setSourceProduct(Product sourceProduct);

	/**
	 * Get the product targeted by this association. This is the product that is to be displayed when viewing the source product.
	 * 
	 * @return the target product
	 */
	Product getTargetProduct();

	/**
	 * Set the target product.
	 * 
	 * @param targetProduct the target product
	 */
	void setTargetProduct(final Product targetProduct);
	
	/**
	 * Set the catalog in which this association applies.
	 * 
	 * @param catalog the catalog in which this association applies
	 */
	void setCatalog(final Catalog catalog);
	
	/**
	 * Get the catalog in which this association applies.
	 * 
	 * @return the catalog in which this association applies
	 */
	Catalog getCatalog();

	/**
	 * Get the starting date on which this <code>MerchandiseAssociation</code> is valid for display.
	 * 
	 * @return the start date
	 */
	Date getStartDate();

	/**
	 * Set the starting date on which this <code>MerchandiseAssociation</code> is valid for display.
	 * 
	 * @param startDate the start date
	 */
	void setStartDate(final Date startDate);

	/**
	 * Get the end date after which this <code>MerchandiseAssociation</code> is no longer valid for display.
	 * 
	 * @return the end date
	 */
	Date getEndDate();

	/**
	 * Set the end date after which this <code>MerchandiseAssociation</code> is no longer valid for display.
	 * 
	 * @param endDate the end date
	 */
	void setEndDate(final Date endDate);

	/**
	 * Get the default quantity of the product targeted by this <code>MerchandiseAssociation</code>. If no default quantity has been set, the
	 * default defaultQuantity is 1. If the target product is added to the cart automatically, it should be added in this default quantity.
	 * 
	 * @return the default quantity of the target product
	 */
	int getDefaultQuantity();

	/**
	 * Set the default quantity of the product targeted by this <code>MerchandiseAssociation</code>.
	 * 
	 * @param defaultQuantity the default quantity
	 */
	void setDefaultQuantity(final int defaultQuantity);

	/**
	 * Get the order in which this product should appear on the page relative to other products having the same source product.
	 * 
	 * @return the ordering
	 */
	int getOrdering();

	/**
	 * Set the order in which this product should appear on the page relative to other products having the same source product.
	 * 
	 * @param ordering the ordering
	 */
	void setOrdering(final int ordering);

	/**
	 * Returns true if the product targeted by this <code>MerchandiseAssociation</code> depends on the source product such that it should be
	 * removed from the cart if the source product is removed.
	 * 
	 * @return true if the target product depends on the source product
	 */
	boolean isSourceProductDependent();

	/**
	 * Set to true if the product targeted by this <code>MerchandiseAssociation</code> depends on the source product such that it should be removed
	 * from the cart if the source product is removed.
	 * 
	 * @param sourceProductDependent sets whether the target product depends on the source product
	 */
	void setSourceProductDependent(final boolean sourceProductDependent);

	/**
	 * Returns true if this association is valid because the current date is within the start and end dates.
	 * 
	 * @return true if the product association is available at today's date.
	 */
	boolean isValid();

	/**
	 * Checks that this ProductAssociation's target product is not hidden, and that
	 * the current time is within the association's valid date range.
	 * @return true if it meets the criteria, false if not
	 */
	boolean isValidProductAssociation();
	
	/**
	 * Returns <code>true</code> if the given association type is valid.
	 * 
	 * @param associationType the association type
	 * @return <code>true</code> if the given association type is valid
	 */
	boolean isValidAssociationType(int associationType);
	
	/**
	 * Create a deep copy of this <code>ProductAssociation</code>.
	 * Not implementing clone here because the intention is not to create a fresh usable object
	 * to create a stale copy of the ProductAssociation which can be checked to see if changes have been made. Does
	 * not deep copy mutable fields, so if you modify a mutable field you will change the copy and the original. 
	 * @return a deep copy
	 */
	ProductAssociation deepCopy();

	/**
	 * Method which checks all fields to see if any are different. This is different to equals
	 * in that it compares all fields including mutable ones which shouldn't be used in equals and
	 * hashCode.   
	 * @param otherAssociation the association to compare with. 
	 * @return true if the values of both objects are equal for all fields
	 */
	boolean isSameAs(final ProductAssociation otherAssociation);
}
