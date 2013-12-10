/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.persistence.api.Entity;

/**
 * <code>Product</code> represents a merchandise product in Elastic Path. A product is likely to
 * have one or more <code>ProductSku</code> belongs to it.
 */
public interface Product extends Comparable<Product>, Entity, ObjectWithLocaleDependantFields {

	/**
	 * Gets the unique identifier for this domain model object. Returns an int so that it can be accessed by Drools. Will likely remove this if
	 * rules don't use the UID.
	 *
	 * @return the unique identifier.
	 */
	int getUidPkInt();

	/**
	 * Get the start date that this product will become available to customers.
	 *
	 * @return the start date
	 */
	Date getStartDate();

	/**
	 * Set the start date that this product will become valid.
	 *
	 * @param startDate the start date
	 */
	void setStartDate(Date startDate);

	/**
	 * Get the end date. After the end date, the product will change to unavailable to customers.
	 *
	 * @return the end date
	 */
	Date getEndDate();

	/**
	 * Set the end date.
	 *
	 * @param endDate the end date
	 */
	void setEndDate(Date endDate);
	
	/**
	 * Returns the date when the product was last modified.
	 *
	 * @return the date when the product was last modified
	 */
	Date getLastModifiedDate();

	/**
	 * Set the date when the product was last modified.
	 *
	 * @param lastModifiedDate the date when the product was last modified
	 */
	void setLastModifiedDate(Date lastModifiedDate);

	/**
	 * Get the display template.
	 *
	 * @return the template
	 */
	String getTemplate();

	/**
	 * Get the display template name of this product.
	 *
	 * @param defaultTemplate the default template name
	 * @return the template name if it is defined for this product, otherwise the given default template name
	 */
	String getTemplateWithFallBack(String defaultTemplate);

	/**
	 * Get the product SKUs (variations) associated with this product.
	 *
	 * @return a map of the product's <code>ProductSku</code> as value and the SKU GUID as key
	 */
	Map<String, ProductSku> getProductSkus();

	/**
	 * Retrieves a product's SKU by its guid.
	 *
	 * @param guid the guid of the SKU to retrieve
	 * @return the corresponding SKU, or null if not found
	 */
	ProductSku getSkuByGuid(final String guid);
	
	/**
	 * Retrieve a SKU by its code.
	 *
	 * @param code the code of the SKU to retrieve
	 * @return the corresponding SKU, or null if not found
	 */
	ProductSku getSkuByCode(final String code);

	/**
	 * Set the variations of this product.
	 *
	 * @param productSkus the map of <code>ProductSku</code>s
	 */
	void setProductSkus(final Map<String, ProductSku> productSkus);

	/**
	 * Get the product type.
	 *
	 * @return the product type
	 */
	ProductType getProductType();

	/**
	 * Set the <code>ProductType</code>.
	 *
	 * @param productType the <code>ProductType</code>
	 */
	void setProductType(ProductType productType);

	/**
	 * Get the attribute value group.
	 *
	 * @return the domain model's <code>AttributeValueGroup</code>
	 */
	AttributeValueGroup getAttributeValueGroup();

	/**
	 * Set the attribute value group.
	 *
	 * @param attributeValueGroup the <code>AttributeValueGroup</code>
	 */
	void setAttributeValueGroup(AttributeValueGroup attributeValueGroup);
	
	/**
	 * Returns true if the product should not be displayed (e.g. in its category or as a search result).
	 *
	 * @return true if the product should not be displayed
	 */
	boolean isHidden();

	/**
	 * Set to true if the product should not be displayed.
	 *
	 * @param hidden true if the product should not be displayed
	 */
	void setHidden(boolean hidden);

	/**
	 * Returns true if the product equals this product.
	 *
	 * @param otherProduct the other product to be compared
	 * @return true if the product equals this product
	 */
	boolean equals(final Object otherProduct);

	/**
	 * Add this product to the specified category.
	 *
	 * @param category the category
	 */
	void addCategory(Category category);

	/**
	 * Remove this product from the specified category.
	 *
	 * @param category the category
	 */
	void removeCategory(Category category);
	
	/**
	 * Sets the preferred index of this product when it's in a collection of
	 * featured products in the given category.
	 * @param category the category in which the product may be featured. <b>This must
	 * be a Category that contains this Product</b>
	 * @param rank the preferred rank of this product in a collection of
	 * featured products.
	 */
	void setFeaturedRank(Category category, int rank);

	/**
	 * Gets the preferred rank of this product when it's in a collection of
	 * featured products in the given category.
	 * @param category the category in which the product may be featured. <b>This must
	 * be a Category that contains this Product</b>
	 * @return the preferred rank of this product when featured in the given category
	 */
	int getFeaturedRank(Category category);
	
	/**
	 * Removes this product from all categories except its default category in its master catalog.
	 */
	void removeAllCategories();
	
	/**
	 * Set the given category to be a default (primary) category of this product.
	 * This method will ensure that the given category is the primary category for
	 * a product in the category's catalog, since a category only has one catalog.
	 * 
	 * @param category the category to be set as default category in its catalog
	 */
	void setCategoryAsDefault(Category category);

	/**
	 * Return the default <code>Category</code> of this product in the given <code>Catalog</code>.
	 *
	 * @param catalog the catalog for which you want the default category
	 * @return the default <code>Category</code>
	 */
	Category getDefaultCategory(Catalog catalog);
	
	/**
	 * Returns the default SKU corresponding to this product.
	 *
	 * @return the <code>ProductSku</code>
	 */
	ProductSku getDefaultSku();

	/**
	 * Sets the default SKU for this product.
	 *
	 * @param defaultSku the default SKU
	 */
	void setDefaultSku(final ProductSku defaultSku);

	/**
	 * Returns true if this product is in the category with the specified category uid or a child of that category.
	 *
	 * @param compoundCategoryGuid the compound category guid that contains catalog code and category code.
	 * @return true if the product is in the category
	 */
	boolean isInCategory(final String compoundCategoryGuid);

	/**
	 * Adds or updates the given SKU to the product.
	 *
	 * @param productSku the SKU to add or update
	 */
	void addOrUpdateSku(ProductSku productSku);

	/**
	 * Remove the given SKU from the product.
	 *
	 * @param productSku the SKU to remove
	 */
	void removeSku(ProductSku productSku);

	/**
	 * Returns the brand/manufacturer of the product.
	 *
	 * @return the brand/manufacturer of the product
	 */
	Brand getBrand();

	/**
	 * Sets the brand/manufacturer of the product.
	 *
	 * @param brand the brand/manufacturer of the product
	 */
	void setBrand(Brand brand);

	/**
	 * Returns true if the product has multiple SKUs.
	 *
	 * @return true if the product has multiple SKUs.
	 */
	boolean hasMultipleSkus();

	/**
	 * Get the product default image.
	 *
	 * @return the product default image
	 */
	String getImage();

	/**
	 * Set the product default image.
	 *
	 * @param image the product default image
	 */
	void setImage(String image);
	
	/**
	 * Returns the total sales count of the product.
	 *
	 * @return the total sales count of the product
	 */
	int getSalesCount();

	/**
	 * Sets the total sales count of the product.
	 *
	 * @param salesCount the total sales count of the product
	 */
	void setSalesCount(final int salesCount);

	/**
	 * Checks if product is active on the given date.
	 * @param date the date to check
	 *
	 * @return is active.
	 */
	boolean isWithinDateRange(Date date);

	/**
	 * Returns the set of <code>Category</code>s containing this product.
	 *
	 * @return a set of <code>Category</code> objects (Not <code>ProductCategory</code>
	 */
	Set<Category> getCategories();

	/**
	 * Returns the set of <code>Category</code>s from the given Catalog that contain
	 * this product.
	 * @param catalog the catalog in which the category should exist
	 * @return the set of Categories
	 */
	Set<Category> getCategories(Catalog catalog);
	
	/**
	 * Sets the product's categories.
	 * Will add and remove from product categories according to new categories.
	 * Will throw an EpDomainException if the collection of new categories doesn't contain
	 * the default category.
	 * @param newCategories new set of categories
	 */
	void setCategories(final Set<Category> newCategories);

	/**
	 * Returns the product code.
	 *
	 * @return the product code
	 */
	String getCode();

	/**
	 * Sets the product code.
	 *
	 * @param code the product code
	 */
	void setCode(String code);

	/**
	 * Returns a list of <code>AttributeValue</code>s with the given locale for all attributes of the product type which this product belonging
	 * to. If an attribute has a value, the value will be returned. Otherwise, a <code>null</code> value will be returned.
	 *
	 * @param locale the locale
	 * @return a list of <code>AttributeValue</code>s
	 * @see com.elasticpath.domain.attribute.AttributeValueGroup#getFullAttributeValues(com.elasticpath.domain.attribute.AttributeGroup, Locale)
	 */
	List<AttributeValue> getFullAttributeValues(Locale locale);

	/**
	 * Returns a list of <code>AttributeValue</code>s with the given locale for those attributes have values.
	 *
	 * @param locale the locale
	 * @return a list of <code>AttributeValue</code>s
	 * @see com.elasticpath.domain.attribute.AttributeValueGroup#getAttributeValues(com.elasticpath.domain.attribute.AttributeGroup, Locale)
	 */
	List<AttributeValue> getAttributeValues(Locale locale);

	/**
	 * Returns the <code>TaxCode</code> associated with this <code>ProductType</code>.
	 * @return the <code>TaxCode</code>
	 */
	TaxCode getTaxCode();

	/**
	 * Set the <code>TaxCode</code> associated with this <code>ProductType</code>.
	 * @param taxCode - the  tax code for this product type, i.e. "BOOKS".
	 */
	void setTaxCode(final TaxCode taxCode);

	/**
	 * Get the max featured product order from the productCateogiers of this product.
	 *
	 * @return the maximum featured product order.
	 */
	int getMaxFeaturedProductOrder();
	
	/**
	 * Get the minimum order quantity of the product.
	 * 
	 * @return the minimum order quantity of the product.
	 */
	int getMinOrderQty();
		
	/**
	 * Set the <code>MinOrderQty</code> associated with this <code>Product</code>.
	 *
	 * @param minOrderQty - the minimum order quantity of the product.
	 */
	void setMinOrderQty(final int minOrderQty);

	/**
	 * Gets the availability criteria.
	 * 
	 * @return <code>AvailabilityCriteria</code>
	 */
	AvailabilityCriteria getAvailabilityCriteria();

	/**
	 * Returns the pre or back order limit.
	 * 
	 * @return order limit
	 */
	int getPreOrBackOrderLimit();

	
	/**
	 * Sets the pre or back order limit.
	 * 
	 * @param orderLimit the order limit
	 */
	void setPreOrBackOrderLimit(int orderLimit);

	/**
	 * Sets the availability criteria.
	 * 
	 * @param availabilityCriteria <code>AvailabilityCriteria</code>
	 */
	void setAvailabilityCriteria(AvailabilityCriteria availabilityCriteria);
	
	/**
	 * Sets the expected release date for a product available on pre order.
	 * 
	 * @param releaseDate the release date
	 */
	void setExpectedReleaseDate(Date releaseDate);
	
	/**
	 * Returns the expected release date.
	 * 
	 * @return <code>Date</code>
	 */
	Date getExpectedReleaseDate();

	/**
	 * @return true if product can not be sold separately (outside of bundle)
	 */
	boolean isNotSoldSeparately();
	
	/**
	 * Set the attribute value map.
	 *
	 * @param attributeValueMap the map
	 */
	void setAttributeValueMap(final Map<String, AttributeValue> attributeValueMap);
	
	/**
	 * Get the attribute value map.
	 *
	 * @return the map
	 */
	Map<String, AttributeValue> getAttributeValueMap();
	
	/**
	 * Returns true if this product is belong to (right under) this category.
	 *
	 * @param categoryUid the category uid to check if this product is right under it.
	 * @return true if the product is belong to the category
	 */
	boolean isBelongToCategory(final long categoryUid);

	/**
	 * Get the master <code>Catalog</code> for this product.
	 * 
	 * @return the master Catalog
	 */
	Catalog getMasterCatalog();
	
	/**
	 * Convenience method for checking whether the product is within the given {@link Catalog}.
	 * Does not check if Categories are linked and included.
	 * 
	 * @param catalog the {@link Catalog}
	 * @return whether the product is within the given {@link Catalog}
	 */
	boolean isInCatalog(final Catalog catalog);
	
	/**
	 * Convenience method for checking whether the product is within the given {@link Catalog}.
	 *
	 * @param catalog The {@link Catalog}.
	 * @param checkForLinkedCategories If true then also check if the Categories are linked and included.
	 * @return Whether the product is within the given {@link Catalog}.
	 */
	boolean isInCatalog(final Catalog catalog, final boolean checkForLinkedCategories);
	
	/**
	 * Get the catalog set of the product.
	 * @return the set of catalog
	 */
	Set<Catalog> getCatalogs();
	
	/**
	 * Find the ProductSku within this Product that is defined by the given set of
	 * OptionValueKeys. e.g. "SMALL, GREEN". If no such Sku is found, returns null.
	 * @param optionValueKeysToFind the set of option value codes to search for
	 * @return the productSku that was found, or null if one was not found
	 */
	ProductSku findSkuWithOptionValueCodes(Collection<String> optionValueKeysToFind);

	/**
	 * Sets the 'Not Sold Separately' attribute in the product.
	 * 
	 * @param notSoldSeparately true if product is not to be sold outside of bundles
	 */
	void setNotSoldSeparately(boolean notSoldSeparately);
	
	/**
	 * Check if the product has any skus whose start-end date range contains
	 * the given date. This will always return true for single-sku products
	 * 
	 * @param currentDate the date to check against
	 * @return true if the product has at least 1 sku with the date in range
	 * @since 6.2.2
	 */
	boolean hasSkuWithinDateRange(final Date currentDate);

	/**
	 * Check if the required attributes have values for the specified locale.
	 *
	 * @param allLocales - the locale for which to check values
	 * throws AttributeValueIsRequiredException if the value for required attributes are missing
	 */
	void validateRequiredAttributes(final Set<Locale> allLocales);

}
