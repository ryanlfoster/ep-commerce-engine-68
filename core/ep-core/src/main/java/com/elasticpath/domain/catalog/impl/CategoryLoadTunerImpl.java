package com.elasticpath.domain.catalog.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.CategoryTypeLoadTuner;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.persistence.api.LoadTuner;

/**
 * Represents a tuner to control category load. A category load tuner can be used in some services
 * to fine control what data to be loaded for a category. The main purpose is to achieve maximum
 * performance for some specific performance-critical pages.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity" })
public class CategoryLoadTunerImpl extends AbstractEpDomainImpl implements CategoryLoadTuner {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private boolean loadingChildren;

	private boolean loadingCategoryType;

	private boolean loadingAttributeValue;

	private CategoryTypeLoadTuner categoryTypeLoadTuner;

	private boolean loadingParent;

	private boolean loadingMaster;

	private boolean loadingLocaleDependantFields;

	private int childCategoryLevel;

	private int parentCategoryLevel;

	/**
	 * Return <code>true</code> if children is requested.
	 *
	 * @return <code>true</code> if children is requested.
	 */
	public boolean isLoadingChildren() {
		return loadingChildren;
	}

	/**
	 * Return <code>true</code> if parent is requested.
	 *
	 * @return <code>true</code> if parent is requested.
	 */
	public boolean isLoadingParent() {
		return loadingParent;
	}

	/**
	 * Return <code>true</code> if master is requested.
	 *
	 * @return <code>true</code> if master is requested.
	 */
	public boolean isLoadingMaster() {
		return loadingMaster;
	}

	/**
	 * Return <code>true</code> if sku category type requested.
	 *
	 * @return <code>true</code> if sku category type requested.
	 */
	public boolean isLoadingCategoryType() {
		return loadingCategoryType;
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
	 * Sets the flag of loading children.
	 *
	 * @param flag sets it to <code>true</code> to request loading children.
	 */
	public void setLoadingChildren(final boolean flag) {
		loadingChildren = flag;
	}

	/**
	 * Sets the flag of loading parent.
	 *
	 * @param flag sets it to <code>true</code> to request loading parent.
	 */
	public void setLoadingParent(final boolean flag) {
		loadingParent = flag;
	}

	/**
	 * Sets the flag of loading master.
	 *
	 * @param flag sets it to <code>true</code> to request loading master.
	 */
	public void setLoadingMaster(final boolean flag) {
		loadingMaster = flag;
	}

	/**
	 * Sets the flag of loading category type.
	 *
	 * @param flag sets it to <code>true</code> to request loading category type.
	 */
	public void setLoadingCategoryType(final boolean flag) {
		loadingCategoryType = flag;
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
	 * Sets the <code>CategoryTypeLoadTuner</code>.
	 *
	 * @param tuner the <code>CategoryTypeLoadTuner</code>
	 */
	public void setCategoryTypeLoadTuner(final CategoryTypeLoadTuner tuner) {
		categoryTypeLoadTuner = tuner;
	}

	/**
	 * Returns the <code>CategoryTypeLoadTuner</code>.
	 *
	 * @return the <code>CategoryTypeLoadTuner</code>
	 */
	public CategoryTypeLoadTuner getCategoryTypeLoadTuner() {
		return categoryTypeLoadTuner;
	}

	/**
	 * Gets whether we are loading locale dependant fields.
	 *
	 * @return whether we are loading locale dependant fields
	 */
	public boolean isLoadingLocaleDependantFields() {
		return loadingLocaleDependantFields;
	}

	/**
	 * Sets whether we are loading locale dependant fields.
	 *
	 * @param loadingLocaleDependantFields whether we are loading locale dependant fields
	 */
	public void setLoadingLocaleDependantFields(final boolean loadingLocaleDependantFields) {
		this.loadingLocaleDependantFields = loadingLocaleDependantFields;
	}

	/**
	 * Returns <code>true</code> if this load tuner is super set of the given load tuner,
	 * otherwise, <code>false</code>.
	 *
	 * @param categoryLoadTuner the category load tuner
	 * @return <code>true</code> if this load tuner is super set of the given load tuner,
	 *         otherwise, <code>false</code>
	 */
	public boolean contains(final CategoryLoadTuner categoryLoadTuner) {
		// same load tuner
		if (this == categoryLoadTuner) {
			return true;
		}

		// Any load tuner contains an empty one
		if (categoryLoadTuner == null) {
			return true;
		}
		if (!loadingChildren && categoryLoadTuner.isLoadingChildren()) {
			return false;
		}
		if (!loadingCategoryType && categoryLoadTuner.isLoadingCategoryType()) {
			return false;
		}
		if (!loadingAttributeValue && categoryLoadTuner.isLoadingAttributeValue()) {
			return false;
		}
		if (!loadingParent && categoryLoadTuner.isLoadingParent()) {
			return false;
		}
		if (!loadingMaster && categoryLoadTuner.isLoadingMaster()) {
			return false;
		}
		if (!loadingLocaleDependantFields && categoryLoadTuner.isLoadingLocaleDependantFields()) {
			return false;
		}
		if (categoryTypeLoadTuner == null) {
			if (categoryLoadTuner.getCategoryTypeLoadTuner() != null) {
				return false;
			}
		} else if (!categoryTypeLoadTuner.contains(categoryLoadTuner.getCategoryTypeLoadTuner())) {
			return false;
		}

		return true;
	}

	/**
	 * Merges the given load tuner with this one and returns the merged load tuner.
	 *
	 * @param categoryLoadTuner the category load tuner
	 * @return the merged load tuner
	 */
	public CategoryLoadTuner merge(final CategoryLoadTuner categoryLoadTuner) {
		if (categoryLoadTuner == null) {
			return this;
		}

		// Do not need to create a new load tuner if the given one contains this.
		if (categoryLoadTuner.contains(this)) {
			return categoryLoadTuner;
		}

		final CategoryLoadTunerImpl mergedLoadTuner = new CategoryLoadTunerImpl();
		mergedLoadTuner.loadingChildren = loadingChildren || categoryLoadTuner.isLoadingChildren();
		mergedLoadTuner.loadingCategoryType = loadingCategoryType || categoryLoadTuner.isLoadingCategoryType();
		mergedLoadTuner.loadingAttributeValue = loadingAttributeValue || categoryLoadTuner.isLoadingAttributeValue();
		mergedLoadTuner.loadingParent = loadingParent || categoryLoadTuner.isLoadingParent();
		mergedLoadTuner.loadingMaster = loadingMaster || categoryLoadTuner.isLoadingMaster();
		mergedLoadTuner.loadingLocaleDependantFields = loadingLocaleDependantFields || categoryLoadTuner.isLoadingLocaleDependantFields();

		if (categoryTypeLoadTuner == null) {
			categoryTypeLoadTuner = getBean(ContextIdNames.CATEGORY_TYPE_LOAD_TUNER);
		}
		mergedLoadTuner.categoryTypeLoadTuner = categoryTypeLoadTuner.merge(categoryLoadTuner.getCategoryTypeLoadTuner());
		return mergedLoadTuner;
	}

	@Override
	public void setChildCategoryLevel(final int childCatagoryLevel) {
		childCategoryLevel = childCatagoryLevel;

	}

	@Override
	public int getChildCategoryLevel() {
		return childCategoryLevel;

	}

	@Override
	public int getParentCategoryLevel() {
		return parentCategoryLevel;
	}

	@Override
	public void setParentCategoryLevel(final int parentCategoryLevel) {
		this.parentCategoryLevel = parentCategoryLevel;
	}

	@Override
	public boolean contains(final LoadTuner loadTuner) {
		if (!(loadTuner instanceof CategoryLoadTuner)) {
			return false;
		}
		return contains((CategoryLoadTuner) loadTuner);
	}

	@Override
	public LoadTuner merge(final LoadTuner loadTuner) {
		if (!(loadTuner instanceof CategoryLoadTuner)) {
			return this;
		}
		return merge((CategoryLoadTuner) loadTuner);
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		return builder.append(loadingChildren)
			.append(loadingCategoryType)
			.append(loadingAttributeValue)
			.append(loadingParent)
			.append(loadingMaster)
			.append(loadingLocaleDependantFields)
			.append(categoryTypeLoadTuner)
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

		CategoryLoadTunerImpl other = (CategoryLoadTunerImpl) obj;
		EqualsBuilder builder = new EqualsBuilder();
		return builder.append(loadingChildren, other.loadingChildren)
			.append(loadingCategoryType, other.loadingCategoryType)
			.append(loadingAttributeValue, other.loadingAttributeValue)
			.append(loadingParent, other.loadingParent)
			.append(loadingMaster, other.loadingMaster)
			.append(loadingLocaleDependantFields, other.loadingLocaleDependantFields)
			.append(categoryTypeLoadTuner, other.categoryTypeLoadTuner)
			.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("loadingChildren", isLoadingChildren())
			.append("loadingCategoryType", isLoadingCategoryType())
			.append("loadingAttributeValue", isLoadingAttributeValue())
			.append("categoryTypeLoadTuner", getCategoryTypeLoadTuner())
			.append("loadingParent", isLoadingParent())
			.append("loadingMaster", isLoadingMaster())
			.append("loadingLocaleDependantFields", isLoadingLocaleDependantFields())
			.append("childCategoryLevel", getChildCategoryLevel())
			.append("parentCategoryLevel", getParentCategoryLevel())
			.toString();
	}
}