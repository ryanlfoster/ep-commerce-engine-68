package com.elasticpath.domain.catalog.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.domain.catalog.ProductTypeLoadTuner;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Represents a tuner to control product type load. A product type load tuner can be used in some services to fine control what data to be loaded for
 * a product type. The main purpose is to achieve maximum performance for some specific performance-critical pages.
 */
public class ProductTypeLoadTunerImpl extends AbstractEpDomainImpl implements ProductTypeLoadTuner {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private boolean loadingAttributes;

	private boolean loadingSkuOptions;

	/**
	 * Return <code>true</code> if attributes is requested.
	 *
	 * @return <code>true</code> if attributes is requested.
	 */
	public boolean isLoadingAttributes() {
		return loadingAttributes;
	}

	/**
	 * Return <code>true</code> if sku options is requested.
	 *
	 * @return <code>true</code> if sku options is requested.
	 */
	public boolean isLoadingSkuOptions() {
		return loadingSkuOptions;
	}

	/**
	 * Sets the flag of loading attributes.
	 *
	 * @param flag sets it to <code>true</code> to request loading attributes.
	 */
	public void setLoadingAttributes(final boolean flag) {
		loadingAttributes = flag;
	}

	/**
	 * Sets the flag of loading sku options.
	 *
	 * @param flag sets it to <code>true</code> to request loading sku options.
	 */
	public void setLoadingSkuOptions(final boolean flag) {
		loadingSkuOptions = flag;
	}

	/**
	 * Returns <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>.
	 *
	 * @param productTypeLoadTuner the product type load tuner
	 * @return <code>true</code> if this load tuner is super set of the given load tuner, otherwise, <code>false</code>
	 */
	public boolean contains(final ProductTypeLoadTuner productTypeLoadTuner) {
		// same load tuner
		if (this == productTypeLoadTuner) {
			return true;
		}

		if (productTypeLoadTuner == null) {
			return true;
		}

		if (!loadingAttributes && productTypeLoadTuner.isLoadingAttributes()) {
			return false;
		}

		if (!loadingSkuOptions && productTypeLoadTuner.isLoadingSkuOptions()) {
			return false;
		}

		return true;
	}

	/**
	 * Merges the given product type load tuner with this one and returns the merged load tuner.
	 *
	 * @param productTypeLoadTuner the product type load tuner
	 * @return the merged load tuner
	 */
	public ProductTypeLoadTuner merge(final ProductTypeLoadTuner productTypeLoadTuner) {
		if (productTypeLoadTuner == null) {
			return this;
		}
		final ProductTypeLoadTunerImpl mergedProductTypeLoadTuner = new ProductTypeLoadTunerImpl();
		mergedProductTypeLoadTuner.loadingAttributes = loadingAttributes || productTypeLoadTuner.isLoadingAttributes();
		mergedProductTypeLoadTuner.loadingSkuOptions = loadingSkuOptions || productTypeLoadTuner.isLoadingSkuOptions();
		return mergedProductTypeLoadTuner;
	}

	/**
	 * Hash code.
	 *
	 * @return the hash code.
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		return builder.append(loadingAttributes)
			.append(loadingSkuOptions)
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

		final ProductTypeLoadTunerImpl other = (ProductTypeLoadTunerImpl) obj;
		EqualsBuilder builder = new EqualsBuilder();
		return builder.append(loadingAttributes, other.loadingAttributes)
			.append(loadingSkuOptions, other.loadingSkuOptions)
			.isEquals();
	}

	@Override
	public boolean contains(final LoadTuner loadTuner) {
		if (!(loadTuner instanceof ProductTypeLoadTuner)) {
			return false;
		}
		return contains((ProductTypeLoadTuner) loadTuner);
	}

	@Override
	public LoadTuner merge(final LoadTuner loadTuner) {
		if (!(loadTuner instanceof ProductTypeLoadTuner)) {
			return this;
		}
		return merge((ProductTypeLoadTuner) loadTuner);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("loadingAttributes", isLoadingAttributes())
			.append("loadingSkuOptions", isLoadingSkuOptions())
			.toString();
	}
}
