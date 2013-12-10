package com.elasticpath.domain.catalog.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.domain.catalog.CategoryTypeLoadTuner;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Represents a tuner to control category type load. A category type load tuner can be used in some services to fine control what data to be loaded
 * for a category type. The main purpose is to achieve maximum performance for some specific performance-critical pages.
 */
public class CategoryTypeLoadTunerImpl extends AbstractEpDomainImpl implements CategoryTypeLoadTuner {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private boolean loadingAttributes;

	/**
	 * Return <code>true</code> if attributes is requested.
	 *
	 * @return <code>true</code> if attributes is requested.
	 */
	public boolean isLoadingAttributes() {
		return loadingAttributes;
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
	 * Returns <code>true</code> if this load tuner is super set of the given load tuner,
	 * otherwise, <code>false</code>.
	 *
	 * @param categoryTypeLoadTuner the category type load tuner
	 * @return <code>true</code> if this load tuner is super set of the given load tuner,
	 *         otherwise, <code>false</code>
	 */
	public boolean contains(final CategoryTypeLoadTuner categoryTypeLoadTuner) {
		// same load tuner
		if (this == categoryTypeLoadTuner) {
			return true;
		}

		// Any load tuner contains an empty one
		if (categoryTypeLoadTuner == null) {
			return true;
		}
		if (!loadingAttributes && categoryTypeLoadTuner.isLoadingAttributes()) {
			return false;
		}
		return true;
	}

	/**
	 * Merges the given load tuner with this one and returns the merged load tuner.
	 *
	 * @param categoryTypeLoadTuner the category type load tuner
	 * @return the merged load tuner
	 */
	public CategoryTypeLoadTuner merge(final CategoryTypeLoadTuner categoryTypeLoadTuner) {
		if (categoryTypeLoadTuner == null) {
			return this;
		}

		final CategoryTypeLoadTunerImpl mergedLoadTuner = new CategoryTypeLoadTunerImpl();
		mergedLoadTuner.loadingAttributes = loadingAttributes || categoryTypeLoadTuner.isLoadingAttributes();
		return mergedLoadTuner;
	}

	@Override
	public boolean contains(final LoadTuner loadTuner) {
		if (!(loadTuner instanceof CategoryTypeLoadTuner)) {
			return false;
		}
		return contains((CategoryTypeLoadTuner) loadTuner);
	}

	@Override
	public LoadTuner merge(final LoadTuner loadTuner) {
		if (!(loadTuner instanceof CategoryTypeLoadTuner)) {
			return this;
		}
		return merge((CategoryTypeLoadTuner) loadTuner);
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		return builder.append(loadingAttributes)
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

		CategoryTypeLoadTunerImpl other = (CategoryTypeLoadTunerImpl) obj;
		EqualsBuilder builder = new EqualsBuilder();
		return builder.append(loadingAttributes, other.loadingAttributes)
			.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
		.append("loadingAttributes", isLoadingAttributes())
		.toString();
	}
}
