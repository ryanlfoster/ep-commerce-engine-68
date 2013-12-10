/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.elasticpath.commons.exception.EpInvalidValueBindException;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.persistence.api.Entity;

/**
 * <code>ProductSku</code> represents a variation of a merchandise product in
 * Elastic Path. A <code>ProductSku</code> corresponds to a single
 * <code>Product</code>
 */
public interface ProductSku extends Entity {

	/**
	 * True if this SKU is digital. It can has  optional asset associated.
	 *
	 * @see ProductSku#isDownloadable()
	 * @return true if this SKU is digital
	 */
	boolean isDigital();

	/**
	 * Sets if this SKU is digital. Digital product can
	 * have asset associated. Some digital products, like:
	 * registration, gift sertificates, access codes can live
	 * without associated asset.
	 *
	 * @param isDigital the digital flag for the SKU
	 */
	void setDigital(boolean isDigital);

	/**
	 *
	 * True if digital SKU has associated digital asset,
	 * that can be downloaded.
	 *
	 * @return true if digital SKU has digital asset.
	 */
	boolean isDownloadable();


	/**
	 * Get the start date that this productSku will become available to
	 * customers.
	 *
	 * @return the start date
	 */
	Date getStartDate();

	/**
	 * Set the start date that this productSku will become valid.
	 *
	 * @param startDate
	 *            the start date
	 */
	void setStartDate(Date startDate);

	/**
	 * Get the end date. After the end date, the productSku will change to
	 * unavailable to customers.
	 *
	 * @return the end date
	 */
	Date getEndDate();

	/**
	 * Set the end date.
	 *
	 * @param endDate
	 *            the end date
	 */
	void setEndDate(Date endDate);

	/**
	 * Get the productSku SKU.
	 *
	 * @return the productSku system name
	 */
	String getSkuCode();

	/**
	 * Set the SKU for this productSku.
	 *
	 * @param skuCode
	 *            the SKU
	 */
	void setSkuCode(final String skuCode);

	/**
	 * Get the attribute value group.
	 *
	 * @return the domain model's <code>AttributeValueGroup</code>
	 */
	AttributeValueGroup getAttributeValueGroup();

	/**
	 * Set the attribute value group.
	 *
	 * @param attributeValueGroup
	 *            the <code>AttributeValueGroup</code>
	 */
	void setAttributeValueGroup(AttributeValueGroup attributeValueGroup);

	/**
	 * Get the parent product corresponding to this SKU. Note that this column
	 * matches the key defined on the TPRODUCTSKU table by Product
	 *
	 * @return the parent <code>Product</code>
	 */
	Product getProduct();

	/**
	 * Set the parent product of this SKU. This method maintains the
	 * bidirectional relationships between the given product and the sku. So it
	 * will so add this sku to the given product's sku collectin.
	 *
	 * @param product
	 *            the parent product
	 */
	void setProduct(final Product product);

	/**
	 * Get the available values for this SKU option.
	 * (e.g. the values for blue, 40 inches)
	 *
	 * @return a set of <code>SkuOptionValue</code>s
	 */
	Collection<SkuOptionValue> getOptionValues();

	/**
	 * Get the option value codes for this SKU (e.g. "Color", "Size", etc).
	 *
	 * @return a set of strings of the option value codes
	 */
	Set<String> getOptionValueCodes();

	/**
	 * Get the collection of SkuOptionValue keys for this ProductSku's SkuOptionValues.
	 * (e.g. if the SkuOptionValues for this ProductSku are for Color=Red,
	 * Size=Large then the keys might be "RED" and "L").
	 *
	 * @return the collection of option value keys for this ProductSku's SkuOptionValues.
	 */
	Collection<String> getOptionValueKeys();

	/**
	 * Gets the available configuration option values for this SKU,
	 * mapped by the option key (e.g. "Color" to a SkuOptionValue for "Red")
	 *
	 * @return a map of <code>SkuOptionValue</code>s
	 */
	Map<String, SkuOptionValue> getOptionValueMap();

	/**
	 * Sets the available configuration option values for this SKU.
	 *
	 * @param optionValueMap
	 *            the map of <code>SkuOptionValue</code>s.
	 */
	void setOptionValueMap(final Map<String, SkuOptionValue> optionValueMap);

	/**
	 * Sets the sku option value to the one corresponding given value code.
	 *
	 * @param skuOption
	 *            the sku option
	 * @param valueCode
	 *            the sku option value code
	 * @throws EpInvalidValueBindException
	 *             in case the given value code is not defined in the given
	 *             <code>SkuOption</code>
	 */
	void setSkuOptionValue(final SkuOption skuOption, final String valueCode)
			throws EpInvalidValueBindException;

	/**
	 * Returns the value of the given <code>SkuOption</code>. Returns
	 * <code>null</code> if the value is not defined.
	 *
	 * @param skuOption
	 *            the sku option
	 * @return the value of the given <code>SkuOption</code>.
	 *         <code>null</code> if the value is not defined.
	 */
	SkuOptionValue getSkuOptionValue(SkuOption skuOption);

	/**
	 * Get the sku default image. Returns the product default image if no sku
	 * image exists
	 *
	 * @return the sku default image
	 */
	String getImage();

	/**
	 * Set the sku default image.
	 *
	 * @param image
	 *            the sku default image
	 */
	void setImage(String image);

	/**
	 * Gets the digital asset belong to this product SKU.
	 *
	 * @return the digital asset belong to this product SKU
	 */
	DigitalAsset getDigitalAsset();

	/**
	 * Sets the digital asset.
	 *
	 * @param digitalAsset
	 *            the digital asset
	 */
	void setDigitalAsset(final DigitalAsset digitalAsset);

	/**
	 * True if this SKU is shippable (i.e. is a physical good which requires shipping).
	 *
	 * @return true if this SKU is shippable
	 */
	boolean isShippable();

	/**
	 * Sets if this SKU is shippable (i.e. is a physical good which requires shipping).
	 *
	 * @param shippable the shippable flag for the SKU
	 */
	void setShippable(final boolean shippable);

	/**
	 * Returns the height.
	 *
	 * @return the height.
	 */
	BigDecimal getHeight();

	/**
	 * Sets the height.
	 *
	 * @param height the height to set.
	 */
	void setHeight(final BigDecimal height);

	/**
	 * Returns the width.
	 *
	 * @return the width.
	 */
	BigDecimal getWidth();

	/**
	 * Sets the width.
	 *
	 * @param width the width to set.
	 */
	void setWidth(final BigDecimal width);

	/**
	 * Returns the length.
	 *
	 * @return the length.
	 */
	BigDecimal getLength();

	/**
	 * Sets the length.
	 *
	 * @param length the length to set.
	 */
	void setLength(final BigDecimal length);

	/**
	 * Returns the weight.
	 *
	 * @return the weight.
	 */
	BigDecimal getWeight();

	/**
	 * Sets the weight.
	 *
	 * @param weight the weight to set.
	 */
	void setWeight(final BigDecimal weight);

	/**
	 * Returns the display name.
	 *
	 * @param locale the Locale
	 * @return String
	 */
	String getDisplayName(Locale locale);

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
	 * Returns true if the current date is within the start and end dates for this product SKU.
	 *
	 * @return true if the current date is within the start and end dates for this product SKU.
	 */
	boolean isWithinDateRange();

	/**
	 * Adds or updates a sku option.
	 *
	 * @param skuOption the sku option to update
	 */
	void addOrUpdateSkuOption(SkuOption skuOption);

	/**
	 * Gets the ordered quantity on pre/back order for this product SKU.
	 *
	 * @return the amount of product SKUs sold on pre/back order
	 */
	int getPreOrBackOrderedQuantity();

	/**
	 * Sets the quantity that was sold on pre/back order.
	 *
	 * @param orderedQuantity integer
	 */
	void setPreOrBackOrderedQuantity(int orderedQuantity);

	/**
	 * Get the date that this was last modified on.
	 *
	 * @return the last modified date
	 * @since 6.2.2
	 */
	Date getLastModifiedDate();

	/**
	 * Get the effective start date. For a single-sku product this will
	 * always be the product's start date. For a multi-sku product it will
	 * be the greater of the two start dates.
	 *
	 * @return the effective start date
	 * @since 6.2.2
	 */
	Date getEffectiveStartDate();

	/**
	 * Get the effective end date. For a single-sku product this will
	 * always be the product's end date. For a multi-sku product it will
	 * be the lesser of the two end dates. If this would have resulted in
	 * an effective end date earlier than the effective start date the
	 * result will be equal to the effective start date.
	 *
	 * @return the effective end date
	 * @since 6.2.2
	 */
	Date getEffectiveEndDate();

	/**
	 * Returns true if the given date is within the start and end dates for this product SKU.
	 *
	 * @param currentDate the date to compare with
	 * @return true if the given date is within the start and end dates for this product SKU.
	 * @since 6.2.2
	 */
	boolean isWithinDateRange(final Date currentDate);

}
