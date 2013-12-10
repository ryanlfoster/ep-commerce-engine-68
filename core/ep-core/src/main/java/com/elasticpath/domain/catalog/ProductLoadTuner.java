package com.elasticpath.domain.catalog;

import com.elasticpath.persistence.api.LoadTuner;

/**
 * Represents a tuner to control product load. A product load tuner can be used in some services to fine control what data to be loaded for a
 * product. The main purpose is to achieve maximum performance for some specific performance-critical pages.
 */
public interface ProductLoadTuner extends LoadTuner {

	/**
	 * Return <code>true</code> if sku is requested.
	 *
	 * @return <code>true</code> if sku is requested.
	 */
	boolean isLoadingSkus();

	/**
	 * Return <code>true</code> if sku product type requested.
	 *
	 * @return <code>true</code> if sku product type requested.
	 */
	boolean isLoadingProductType();

	/**
	 * Return <code>true</code> if attribute value is requested.
	 *
	 * @return <code>true</code> if attribute value is requested.
	 */
	boolean isLoadingAttributeValue();

	/**
	 * Return <code>true</code> if category is requested.
	 *
	 * @return <code>true</code> if category is requested.
	 */
	boolean isLoadingCategories();

	/**
	 * Return <code>true</code> if default sku is requested.
	 *
	 * @return <code>true</code> if default sku is requested.
	 */
	boolean isLoadingDefaultSku();

	/**
	 * Sets the flag of loading skus.
	 *
	 * @param flag sets it to <code>true</code> to request loading skus.
	 */
	void setLoadingSkus(boolean flag);

	/**
	 * Return <code>true</code> if default category is requested.
	 *
	 * @return  <code>true</code> if default category is requested.
	 */
	boolean isLoadingDefaultCategory();

	/**
	 * Sets the flag of loading default category.
	 *
	 * @param flag sets it to <code>true</code> to request loading default category.
	 */
	void setLoadingDefaultCategory(boolean flag);

	/**
	 * Sets the flag of loading product type.
	 *
	 * @param flag sets it to <code>true</code> to request loading product type.
	 */
	void setLoadingProductType(boolean flag);

	/**
	 * Sets the flag of loading attribute values.
	 *
	 * @param flag sets it to <code>true</code> to request loading attribute values.
	 */
	void setLoadingAttributeValue(boolean flag);

	/**
	 * Sets the flag of loading categories.
	 *
	 * @param flag sets it to <code>true</code> to request loading categories.
	 */
	void setLoadingCategories(boolean flag);

	/**
	 * Sets the flag of loading default sku.
	 *
	 * @param flag sets it to <code>true</code> to request loading default sku.
	 */
	void setLoadingDefaultSku(boolean flag);

	/**
	 * Sets the <code>ProductSkuLoadTuner</code>.
	 *
	 * @param tuner the <code>ProductSkuLoadTuner</code>
	 */
	void setProductSkuLoadTuner(ProductSkuLoadTuner tuner);

	/**
	 * Returns the <code>ProductSkuLoadTuner</code>.
	 *
	 * @return the <code>ProductSkuLoadTuner</code>
	 */
	ProductSkuLoadTuner getProductSkuLoadTuner();

	/**
	 * Sets the {@link CategoryLoadTuner} instance to use.
	 *
	 * @param categoryLoadTuner the {@link CategoryLoadTuner} instance to use
	 */
	void setCategoryLoadTuner(CategoryLoadTuner categoryLoadTuner);

	/**
	 * Gets the {@link CategoryLoadTuner}.
	 *
	 * @return the {@link CategoryLoadTuner}
	 */
	CategoryLoadTuner getCategoryLoadTuner();

	/**
	 * Sets the <code>ProductTypeLoadTuner</code>.
	 *
	 * @param tuner the <code>ProductTypeLoadTuner</code>
	 */
	void setProductTypeLoadTuner(ProductTypeLoadTuner tuner);

	/**
	 * Returns the <code>ProductTypeLoadTuner</code>.
	 *
	 * @return the <code>ProductTypeLoadTuner</code>
	 */
	ProductTypeLoadTuner getProductTypeLoadTuner();

	/**
	 * Returns <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>.
	 *
	 * @param productLoadTuner the product load tuner
	 * @return <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>
	 */
	boolean contains(ProductLoadTuner productLoadTuner);

	/**
	 * Merges the given product load tuner with this one and returns the merged product load tuner.
	 *
	 * @param productLoadTuner the product load tuner
	 * @return the merged product load tuner
	 */
	ProductLoadTuner merge(ProductLoadTuner productLoadTuner);
}
