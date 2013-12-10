package com.elasticpath.domain.catalog.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.domain.catalog.ProductSkuLoadTuner;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Represents a tuner to control productsku load. A product load tuner can be used in some services to fine control what data to be loaded for a
 * productsku. The main purpose is to achieve maximum performance for some specific performance-critical pages.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity" })
public class ProductSkuLoadTunerImpl extends AbstractEpDomainImpl implements ProductSkuLoadTuner {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private boolean loadingAttributeValue;

	private boolean loadingOptionValue;

	private boolean loadingProduct;

	private boolean loadingDigitalAsset;

	/**
	 * Return <code>true</code> if attribute value is requested.
	 *
	 * @return <code>true</code> if attribute value is requested.
	 */
	public boolean isLoadingAttributeValue() {
		return loadingAttributeValue;
	}

	/**
	 * Return <code>true</code> if option value is requested.
	 *
	 * @return <code>true</code> if option value is requested.
	 */
	public boolean isLoadingOptionValue() {
		return loadingOptionValue;
	}

	/**
	 * Return <code>true</code> if product is requested.
	 *
	 * @return <code>true</code> if product is requested.
	 */
	public boolean isLoadingProduct() {
		return loadingProduct;
	}

	/**
	 * Return <code>true</code> if digital asset is requested.
	 *
	 * @return <code>true</code> if digital asset is requested.
	 */
	public boolean isLoadingDigitalAsset() {
		return loadingDigitalAsset;
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
	 * Sets the flag of loading option values.
	 *
	 * @param flag sets it to <code>true</code> to request loading option values.
	 */
	public void setLoadingOptionValue(final boolean flag) {
		loadingOptionValue = flag;
	}

	/**
	 * Sets the flag of loading product.
	 *
	 * @param flag sets it to <code>true</code> to request loading product.
	 */
	public void setLoadingProduct(final boolean flag) {
		loadingProduct = flag;
	}

	/**
	 * Sets the flag of loading digital asset.
	 *
	 * @param flag sets it to <code>true</code> to request digital asset.
	 */
	public void setLoadingDigitalAsset(final boolean flag) {
		loadingDigitalAsset = flag;
	}

	/**
	 * Returns <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>.
	 *
	 * @param productSkuLoadTuner the sku load tuner
	 * @return <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>
	 */
	public boolean contains(final ProductSkuLoadTuner productSkuLoadTuner) {
		// same load tuner
		if (this == productSkuLoadTuner) {
			return true;
		}

		// Any load tuner contains an empty one
		if (productSkuLoadTuner == null) {
			return true;
		}

		if (!loadingAttributeValue && productSkuLoadTuner.isLoadingAttributeValue()) {
			return false;
		}

		if (!loadingDigitalAsset && productSkuLoadTuner.isLoadingDigitalAsset()) {
			return false;
		}

		if (!loadingOptionValue && productSkuLoadTuner.isLoadingOptionValue()) {
			return false;
		}

		if (!loadingProduct && productSkuLoadTuner.isLoadingProduct()) {
			return false;
		}

		return true;
	}

	/**
	 * Merges the given load tuner with this one and returns the merged load tuner.
	 *
	 * @param productSkuLoadTuner the product sku load tuner
	 * @return the merged load tuner
	 */
	public ProductSkuLoadTuner merge(final ProductSkuLoadTuner productSkuLoadTuner) {

		if (productSkuLoadTuner == null) {
			return this;
		}

		final ProductSkuLoadTunerImpl mergedProductSkuLoadTuner = new ProductSkuLoadTunerImpl();

		mergedProductSkuLoadTuner.loadingAttributeValue = loadingAttributeValue || productSkuLoadTuner.isLoadingAttributeValue();

		mergedProductSkuLoadTuner.loadingDigitalAsset = loadingDigitalAsset || productSkuLoadTuner.isLoadingDigitalAsset();

		mergedProductSkuLoadTuner.loadingOptionValue = loadingOptionValue || productSkuLoadTuner.isLoadingOptionValue();

		mergedProductSkuLoadTuner.loadingProduct = loadingProduct || productSkuLoadTuner.isLoadingProduct();

		return mergedProductSkuLoadTuner;
	}

	/**
	 * Hash code.
	 *
	 * @return the hash code.
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		return builder.append(loadingAttributeValue)
			.append(loadingDigitalAsset)
			.append(loadingOptionValue)
			.append(loadingProduct)
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

		final ProductSkuLoadTunerImpl other = (ProductSkuLoadTunerImpl) obj;
		EqualsBuilder builder = new EqualsBuilder();
		return builder.append(loadingAttributeValue, other.loadingAttributeValue)
				.append(loadingDigitalAsset, other.loadingDigitalAsset)
				.append(loadingOptionValue, other.loadingOptionValue)
				.append(loadingProduct, other.loadingProduct)
				.isEquals();
	}

	@Override
	public boolean contains(final LoadTuner loadTuner) {
		if (!(loadTuner instanceof ProductSkuLoadTuner)) {
			return false;
		}
		return contains((ProductSkuLoadTuner) loadTuner);
	}

	@Override
	public LoadTuner merge(final LoadTuner loadTuner) {
		if (!(loadTuner instanceof ProductSkuLoadTuner)) {
			return this;
		}
		return merge((ProductSkuLoadTuner) loadTuner);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("loadingAttributeValue", isLoadingAttributeValue())
			.append("loadingOptionValue", isLoadingOptionValue())
			.append("loadingProduct", isLoadingProduct())
			.append("loadingDigitalAsset", isLoadingDigitalAsset())
			.toString();
	}
}
