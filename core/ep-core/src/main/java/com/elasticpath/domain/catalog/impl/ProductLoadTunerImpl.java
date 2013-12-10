package com.elasticpath.domain.catalog.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSkuLoadTuner;
import com.elasticpath.domain.catalog.ProductTypeLoadTuner;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Represents a tuner to control product load. A product load tuner can be used in some services to fine control what data to be loaded for a
 * product. The main purpose is to achieve maximum performance for some specific performance-critical pages.
 */
public class ProductLoadTunerImpl extends AbstractEpDomainImpl implements ProductLoadTuner {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private boolean loadingSkus;

	private boolean loadingProductType;

	private boolean loadingAttributeValue;

	private boolean loadingCategories;

	private boolean loadingDefaultSku;

	private ProductSkuLoadTuner productSkuLoadTuner;

	private ProductTypeLoadTuner productTypeLoadTuner;

	private CategoryLoadTuner categoryLoadTuner;

	private boolean loadingDefaultCategory;

	/**
	 * Default constructor.
	 */
	public ProductLoadTunerImpl() {
		// do nothing
	}

	/**
	 * Return <code>true</code> if sku is requested.
	 *
	 * @return <code>true</code> if sku is requested.
	 */
	public boolean isLoadingSkus() {
		return loadingSkus;
	}

	/**
	 * Return <code>true</code> if default category is requested.
	 *
	 * @return <code>true</code> if default category is requested.
	 */
	public boolean isLoadingDefaultCategory() {
		return loadingDefaultCategory;
	}

	/**
	 * Return <code>true</code> if sku product type requested.
	 *
	 * @return <code>true</code> if sku product type requested.
	 */
	public boolean isLoadingProductType() {
		return loadingProductType;
	}

	/**
	 * Return <code>true</code> if attribute value is requested.
	 *
	 * @return <code>true</code> if attribute value is requested.
	 */
	public boolean isLoadingAttributeValue() {
		return loadingAttributeValue;
	}

	/**
	 * Return <code>true</code> if category is requested.
	 *
	 * @return <code>true</code> if category is requested.
	 */
	public boolean isLoadingCategories() {
		return loadingCategories;
	}

	/**
	 * Return <code>true</code> if default sku is requested.
	 *
	 * @return <code>true</code> if default sku is requested.
	 */
	public boolean isLoadingDefaultSku() {
		return loadingDefaultSku;
	}

	/**
	 * Sets the flag of loading skus.
	 *
	 * @param flag sets it to <code>true</code> to request loading skus.
	 */
	public void setLoadingSkus(final boolean flag) {
		loadingSkus = flag;
	}

	/**
	 * Sets the flag of loading product type.
	 *
	 * @param flag sets it to <code>true</code> to request loading product type.
	 */
	public void setLoadingProductType(final boolean flag) {
		loadingProductType = flag;
	}

	/**
	 * Sets the flag of loading attribute values.
	 *
	 * @param flag sets it to <code>true</code> to request loading attribute values.
	 */
	public void setLoadingAttributeValue(final boolean flag) {
		loadingAttributeValue = flag;
	}

	/**
	 * Sets the flag of loading categories.
	 *
	 * @param flag sets it to <code>true</code> to request loading categories.
	 */
	public void setLoadingCategories(final boolean flag) {
		loadingCategories = flag;
	}

	/**
	 * Sets the flag of loading default sku.
	 *
	 * @param flag sets it to <code>true</code> to request loading default sku.
	 */
	public void setLoadingDefaultSku(final boolean flag) {
		loadingDefaultSku = flag;
	}

	/**
	 * Sets the <code>ProductSkuLoadTuner</code>.
	 *
	 * @param tuner the <code>ProductSkuLoadTuner</code>
	 */
	public void setProductSkuLoadTuner(final ProductSkuLoadTuner tuner) {
		productSkuLoadTuner = tuner;
	}

	/**
	 * Returns the <code>ProductSkuLoadTuner</code>.
	 *
	 * @return the <code>ProductSkuLoadTuner</code>
	 */
	public ProductSkuLoadTuner getProductSkuLoadTuner() {
		return productSkuLoadTuner;
	}

	/**
	 * Sets the {@link CategoryLoadTuner} instance to use.
	 *
	 * @param categoryLoadTuner the {@link CategoryLoadTuner} instance to use
	 */
	public void setCategoryLoadTuner(final CategoryLoadTuner categoryLoadTuner) {
		this.categoryLoadTuner = categoryLoadTuner;
	}

	/**
	 * Gets the {@link CategoryLoadTuner}.
	 *
	 * @return the {@link CategoryLoadTuner}
	 */
	public CategoryLoadTuner getCategoryLoadTuner() {
		return categoryLoadTuner;
	}

	/**
	 * Sets the flag of loading default category.
	 *
	 * @param flag sets it to <code>true</code> to request loading default category.
	 */
	public void setLoadingDefaultCategory(final boolean flag) {
		loadingDefaultCategory = flag;
	}

	/**
	 * Sets the <code>ProductTypeLoadTuner</code>.
	 *
	 * @param tuner the <code>ProductTypeLoadTuner</code>
	 */
	public void setProductTypeLoadTuner(final ProductTypeLoadTuner tuner) {
		productTypeLoadTuner = tuner;
	}

	/**
	 * Returns the <code>ProductTypeLoadTuner</code>.
	 *
	 * @return the <code>ProductTypeLoadTuner</code>
	 */
	public ProductTypeLoadTuner getProductTypeLoadTuner() {
		return productTypeLoadTuner;
	}

	/**
	 * Returns <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>.
	 *
	 * @param productLoadTuner the product load tuner
	 * @return <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean contains(final ProductLoadTuner productLoadTuner) {
		// same load tuner
		if (this == productLoadTuner) {
			return true;
		}

		// Any load tuner contains an empty one
		if (productLoadTuner == null) {
			return true;
		}

		if (!loadingAttributeValue && productLoadTuner.isLoadingAttributeValue()) {
			return false;
		}

		if (!loadingCategories && productLoadTuner.isLoadingCategories()) {
			return false;
		}

		if (!loadingDefaultCategory && productLoadTuner.isLoadingDefaultCategory()) {
			return false;
		}

		if (!loadingDefaultSku && productLoadTuner.isLoadingDefaultSku()) {
			return false;
		}

		if (!loadingProductType && productLoadTuner.isLoadingProductType()) {
			return false;
		}

		if (!loadingSkus && productLoadTuner.isLoadingSkus()) {
			return false;
		}

		if (productSkuLoadTuner == null) {
			if (productLoadTuner.getProductSkuLoadTuner() != null) {
				return false;
			}
		} else {
			if (!productSkuLoadTuner.contains(productLoadTuner.getProductSkuLoadTuner())) {
				return false;
			}
		}

		if (categoryLoadTuner == null) {
			if (productLoadTuner.getCategoryLoadTuner() != null) {
				return false;
			}
		} else {
			if (!categoryLoadTuner.contains(productLoadTuner.getCategoryLoadTuner())) {
				return false;
			}
		}

		if (productTypeLoadTuner == null) {
			if (productLoadTuner.getProductTypeLoadTuner() != null) {
				return false;
			}
		} else {
			if (!productTypeLoadTuner.contains(productLoadTuner.getProductTypeLoadTuner())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Merges the given product load tuner with this one and returns the merged product load tuner.
	 *
	 * @param productLoadTuner the product load tuner
	 * @return the merged product load tuner
	 */
	public ProductLoadTuner merge(final ProductLoadTuner productLoadTuner) {
		if (productLoadTuner == null) {
			return this;
		}

		// Do not need to create a new load tuner if the given one contains this.
		if (productLoadTuner.contains(this)) {
			return productLoadTuner;
		}

		final ProductLoadTunerImpl mergedProductLoadTuner = new ProductLoadTunerImpl();
		mergedProductLoadTuner.loadingAttributeValue = loadingAttributeValue || productLoadTuner.isLoadingAttributeValue();
		mergedProductLoadTuner.loadingCategories = loadingCategories || productLoadTuner.isLoadingCategories();
		mergedProductLoadTuner.loadingDefaultCategory = loadingDefaultCategory || productLoadTuner.isLoadingDefaultCategory();
		mergedProductLoadTuner.loadingDefaultSku = loadingDefaultSku || productLoadTuner.isLoadingDefaultSku();
		mergedProductLoadTuner.loadingProductType = loadingProductType || productLoadTuner.isLoadingProductType();
		mergedProductLoadTuner.loadingSkus = loadingSkus || productLoadTuner.isLoadingSkus();

		if (productSkuLoadTuner == null) {
			productSkuLoadTuner = getBean(ContextIdNames.PRODUCT_SKU_LOAD_TUNER);
		}
		mergedProductLoadTuner.productSkuLoadTuner = productSkuLoadTuner.merge(productLoadTuner.getProductSkuLoadTuner());

		if (categoryLoadTuner == null) {
			categoryLoadTuner = getBean(ContextIdNames.CATEGORY_LOAD_TUNER);
		}
		mergedProductLoadTuner.categoryLoadTuner = categoryLoadTuner.merge(productLoadTuner.getCategoryLoadTuner());

		if (productTypeLoadTuner == null) {
			productTypeLoadTuner = getBean(ContextIdNames.PRODUCT_TYPE_LOAD_TUNER);
		}
		mergedProductLoadTuner.productTypeLoadTuner = productTypeLoadTuner.merge(productLoadTuner.getProductTypeLoadTuner());

		return mergedProductLoadTuner;
	}

	/**
	 * Hash code. Need I say more?
	 *
	 * @return the hash code.
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		return builder.append(loadingAttributeValue)
				.append(loadingCategories)
				.append(loadingDefaultCategory)
				.append(loadingDefaultSku)
				.append(loadingProductType)
				.append(loadingSkus)
				.append(productSkuLoadTuner)
				.append(categoryLoadTuner)
				.append(productTypeLoadTuner)
				.toHashCode();
	}


	/**
	 * Implements equals semantics.<br>
	 * Because load tuners are concerned with field states within the class, it acts as a value type. In this case, content is crucial in the equals
	 * comparison. Using getClass() within the equals method ensures strict comparison between content state in this class where symmetry is
	 * maintained. If instanceof was used in the comparison this could potentially cause symmetry violations when extending this class.
	 *
	 * @param obj the other object to compare
	 * @return true if equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		final ProductLoadTunerImpl other = (ProductLoadTunerImpl) obj;
		EqualsBuilder builder = new EqualsBuilder();
		return builder.append(loadingAttributeValue, other.loadingAttributeValue)
				.append(loadingCategories, other.loadingCategories)
				.append(loadingDefaultCategory, other.loadingDefaultCategory)
				.append(loadingDefaultSku, other.loadingDefaultSku)
				.append(loadingProductType, other.loadingProductType)
				.append(loadingSkus, other.loadingSkus)
				.append(productSkuLoadTuner, other.productSkuLoadTuner)
				.append(categoryLoadTuner, other.categoryLoadTuner)
				.append(productTypeLoadTuner, other.productTypeLoadTuner).isEquals();
	}

	@Override
	public boolean contains(final LoadTuner loadTuner) {
		if (!(loadTuner instanceof ProductLoadTuner)) {
			return false;
		}
		return contains((ProductLoadTuner) loadTuner);
	}

	@Override
	public LoadTuner merge(final LoadTuner loadTuner) {
		if (!(loadTuner instanceof ProductLoadTuner)) {
			return this;
		}
		return merge((ProductLoadTuner) loadTuner);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("loadingAttributeValue", isLoadingAttributeValue())
			.append("loadingCategories", isLoadingCategories())
			.append("loadingDefaultCategory", isLoadingDefaultCategory())
			.append("loadingDefaultSku", isLoadingDefaultSku())
			.append("loadingProductType", isLoadingProductType())
			.append("loadingSkus", isLoadingSkus())
			.append("productSkuLoadTuner", getProductSkuLoadTuner())
			.append("categoryLoadTuner", getCategoryLoadTuner())
			.append("productTypeLoadTuner", getProductTypeLoadTuner())
			.toString();
	}
}
