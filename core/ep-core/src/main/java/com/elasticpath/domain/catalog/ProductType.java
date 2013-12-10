/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import java.util.List;
import java.util.Set;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.persistence.api.Entity;

/**
 * Represents the type of a <code>Product</code>, which determines the set of attributes that it has. An example of a product type is "Shoe." Note
 * that this differs from a product category, which might also be called "Shoes" because this describes the characteristics of the product rather
 * than how they are displayed and organized in the store.
 */
public interface ProductType extends Entity, CatalogObject {
	/**
	 * Returns <code>true</code> if the product type may have multiple skus.
	 *
	 * @return <code>true</code> if the product type has multiple skus
	 */
	boolean isWithMultipleSkus();

	/**
	 * Sets multiple sku flag.
	 *
	 * @param multipleSkuFlag sets it to <code>true</code> if the product type may have multiple skus.
	 */
	void setWithMultipleSkus(boolean multipleSkuFlag);

	/**
	 * Gets the available options for configuring SKUs of this product.
	 *
	 * @return the set of options for configuring SKUs of this product or null if there are no configurable options
	 */
	Set<SkuOption> getSkuOptions();

	/**
	 * Gets the available options for configuring SKUs of this product.
	 * Updates the default SKU option values based on the values specified in a given default SKU.
	 *
	 * @param defaultSku the SKU whose option values are to be the defaults for the given options
	 * @return the set of options for configuring SKUs of this product or null if there are no configurable options
	 */
	Set<SkuOption> getSkuOptions(final ProductSku defaultSku);

	/**
	 * Sets the available options for configuring SKUs of this product.
	 *
	 * @param skuOptions the set of available options for configuring SKUs of this product
	 */
	void setSkuOptions(Set<SkuOption> skuOptions);

	/**
	 * Get the product type name.
	 *
	 * @return the product type name
	 *
	 * @domainmodel.property
	 */
	String getName();

	/**
	 * Set the product type name.
	 *
	 * @param name the product type name
	 */
	void setName(String name);

	/**
	 * Get the product type description.
	 *
	 * @return the product type description
	 *
	 * @domainmodel.property
	 */
	String getDescription();

	/**
	 * Set the product type description.
	 *
	 * @param description the product type description
	 */
	void setDescription(String description);

	/**
	 * Get the display template of this category.
	 *
	 * @return the template
	 *
	 * @domainmodel.property
	 */
	String getTemplate();

	/**
	 * Set the template.
	 *
	 * @param template the template
	 */
	void setTemplate(String template);

	/**
	 * Sets the product attribute group.
	 *
	 * @param productAttributeGroup the product attribute group.
	 */
	void setProductAttributeGroup(AttributeGroup productAttributeGroup);

	/**
	 * Returns the product attribute group.
	 *
	 * @return the product attribute group
	 */
	AttributeGroup getProductAttributeGroup();

	/**
	 * Sets the product sku attribute group.
	 *
	 * @param skuAttributeGroup the product attribute group.
	 */
	void setSkuAttributeGroup(AttributeGroup skuAttributeGroup);

	/**
	 * Returns the product sku attribute group.
	 *
	 * @return the product sku attribute group
	 */
	AttributeGroup getSkuAttributeGroup();

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
	 * Get the set of product attribute group attributes.
	 *
	 * @return the attributes
	 */
	Set<AttributeGroupAttribute> getProductAttributeGroupAttributes();

	/**
	 * Set the product attribute group attributes.
	 *
	 * @param productAttributeGroupAttributes the set of attributes
	 */
	void setProductAttributeGroupAttributes(final Set<AttributeGroupAttribute> productAttributeGroupAttributes);

	/**
	 * Adds or updates sku option.
	 *
	 * @param skuOption the sku option to update
	 */
	void addOrUpdateSkuOption(SkuOption skuOption);

	/**
	 * Convenience method to get the sorted SKU options based on SKU option display name on catalog default locale.
	 *
	 * @param defaultSku the SKU whose option values are to be the defaults for the given options
	 * @return the sorted list of options for configuring SKUs of this product or null if there are no configurable options
	 */
	List<SkuOption> getSortedSkuOptionList(final ProductSku defaultSku);

	/**
	 * Convenience method to get the sorted SKU options based on Frequency and SKU option display name on catalog default locale.<br>
	 * The frequency sku option is always the last one in the sorting.<br>
	 * All remaining items are sorted by display name.<br>
	 *
	 * @param defaultSku the SKU whose option values are to be the defaults for the given options
	 * @return the sorted list of options for configuring SKUs of this product or null if there are no configurable options
	 */
	List<SkuOption> getSortedSkuOptionListForRecurringItems(final ProductSku defaultSku);

	/**
	 * Returns marker if product of this type should be excluded from discount calculation.
	 *
	 * @return <code>true</code> if item should be excluded from discount calculation, false otherwise
	 */
	boolean isExcludedFromDiscount();

	/**
	 * Sets marker if product of this type should be excluded from discount calculation.
	 *
	 * @param excludedFromDiscount  <code>true</code> if item should be excluded from discount calculation, false otherwise
	 */
	void setExcludedFromDiscount(final boolean excludedFromDiscount);

	/**
	 * Convenience method to remove a productType skuOption by the skuOption guid.
	 *
	 * @param skuOptionGuid the skuOption guid of the skuOption to remove
	 */
	void removeSkuOptionByGuid(final String skuOptionGuid);

	/**
	 * Convenience method to find a productType skuOption by the skuOption guid.
	 *
	 * @param skuOptionGuid the sku option guid to find
	 * @return the skuOption matching the guid, otherwise null if not found
	 */
	SkuOption findSkuOptionByGuid(final String skuOptionGuid);

	/**
	 * Convenience method to remove a productType sku attribute by the sku attribute's key.
	 *
	 * @param skuAttributeKey the sku attribute key to find
	 */
	void removeSkuAttributeByKey(final String skuAttributeKey);

	/**
	 * Convenience method to find a productType sku attribute by the sku attribute's key.
	 *
	 * @param skuAttributeKey the sku attribute key to search on
	 * @return the matching sku Attribute, otherwise null if not found
	 */
	Attribute findSkuAttributeByKey(final String skuAttributeKey);
}
