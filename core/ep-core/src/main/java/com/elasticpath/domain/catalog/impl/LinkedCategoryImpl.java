/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalog.impl;

import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.Nonpolymorphic;
import org.apache.openjpa.persistence.jdbc.NonpolymorphicType;

import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.TopSeller;
import com.elasticpath.domain.localization.LocaleFallbackPolicy;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * The implementation of <code>Category</code> that represents a linked, virtual <code>Category</code> object.
 * <br/>
 * 
 * NOTE that the presence of the {@code DatabaseLastModifiedDate} means that whenever this object is saved or updated to the database
 * the lastModifiedDate will be set by the {@code LastModifiedPersistenceEngineImpl} if that class in configured in Spring. 
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@Entity
@Table(name = LinkedCategoryImpl.TABLE_NAME)
@PrimaryKeyJoinColumn(name = "UIDPK", referencedColumnName = "UIDPK")
@DiscriminatorValue("linkedCategory")
@FetchGroups({
		@FetchGroup(name = FetchGroupConstants.CATEGORY_INDEX, attributes = { 
				@FetchAttribute(name = "masterCategory"),
				@FetchAttribute(name = "included") }, postLoad = true),
  		@FetchGroup(name = FetchGroupConstants.CATEGORY_AVAILABILITY, attributes = { 
				@FetchAttribute(name = "masterCategory"),
				@FetchAttribute(name = "included") }),
		@FetchGroup(name = FetchGroupConstants.CATEGORY_BASIC, 
				fetchGroups = { FetchGroupConstants.CATEGORY_HASH_MINIMAL }, attributes = { 
				@FetchAttribute(name = "included") }),
		@FetchGroup(name = FetchGroupConstants.CATEGORY_HASH_MINIMAL, attributes = { 
				@FetchAttribute(name = "masterCategory") }),
		@FetchGroup(name = FetchGroupConstants.LINK_PRODUCT_CATEGORY, 
				fetchGroups = { FetchGroupConstants.CATEGORY_HASH_MINIMAL }),
		@FetchGroup(name = FetchGroupConstants.INFINITE_CHILD_CATEGORY_DEPTH, attributes = {
				@FetchAttribute(name = "masterCategory") }),
		@FetchGroup(name = FetchGroupConstants.INIFINITE_PARENT_CATEGORY_DEPTH, attributes = {
				@FetchAttribute(name = "masterCategory") })
}) 

public class LinkedCategoryImpl extends AbstractCategoryImpl implements Category {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TLINKEDCATEGORY";

	private Category masterCategory;

	private boolean include;

	/**
	 * Default constructor.
	 */
	public LinkedCategoryImpl() {
		super();
	}

	/**
	 * Get the start date that this category will become available to customers.
	 * 
	 * @return the start date
	 */
	@Transient
	public Date getStartDate() {
		return this.getMasterCategory().getStartDate();
	}

	/**
	 * Set the start date that this category will become valid.
	 * 
	 * @param startDate the start date
	 */
	public void setStartDate(final Date startDate) {
		throw new EpUnsupportedOperationException("Cannot set start date on LinkedCategory."); //$NON-NLS-1$
	}

	/**
	 * Get the end date. After the end date, the category will become unavailable to customers.
	 * 
	 * @return the end date
	 */
	@Transient
	public Date getEndDate() {
		return this.getMasterCategory().getEndDate();
	}

	/**
	 * Set the end date.
	 * 
	 * @param endDate the end date
	 */
	public void setEndDate(final Date endDate) {
		throw new EpUnsupportedOperationException("Cannot set end date on LinkedCategory."); //$NON-NLS-1$
	}

	/**
	 * Gets the display template of this category.
	 * 
	 * @return the template
	 */
	@Transient
	public String getTemplate() {
		return this.getMasterCategory().getTemplate();
	}

	/**
	 * Get the display template name of this category.
	 * 
	 * @param defaultTemplate the default template name
	 * @return the template name if it is defined for this category, otherwise the given default template name
	 */
	public String getTemplateWithFallBack(final String defaultTemplate) {
		return this.getMasterCategory().getTemplateWithFallBack(defaultTemplate);
	}

	/**
	 * Returns <code>true</code> if the category is available to be displayed.
	 * 
	 * @return <code>true</code> if the category is available.
	 */
	@Transient
	public boolean isAvailable() {
		return this.getMasterCategory().isAvailable() && isIncluded();
	}

	/**
	 * Set default values for those fields that need default values.
	 */
	@Override
	public void initialize() {
		super.initialize();

		this.setIncluded(true);

		if (this.getChildren() == null) {
			this.setChildren(new TreeSet<Category>());
		}
	}

	/**
	 * Set the <code>CategoryType</code>.
	 * 
	 * @param categoryType the <code>CategoryType</code>
	 */
	public void setCategoryType(final CategoryType categoryType) {
		throw new EpUnsupportedOperationException("Cannot set category type on LinkedCategory."); //$NON-NLS-1$
	}

	/**
	 * Returns the category type.
	 * 
	 * @return the category type
	 */
	@Transient
	public CategoryType getCategoryType() {
		return this.getMasterCategory().getCategoryType();
	}

	/**
	 * Gets the all <code>LocaleDependantFields</code> as a map.
	 * 
	 * @return the <code>LocaleDependantFields</code> map
	 */

	/**
	 * Sets all <code>LocaleDependantFields</code> with the given map.
	 * 
	 * @param localeDependantFieldsMap the <code>LocaleDependantFields</code> map to set
	 */
	public void setLocaleDependantFieldsMap(final Map<Locale, LocaleDependantFields> localeDependantFieldsMap) {
		throw new EpUnsupportedOperationException("Cannot set locale dependant fields map on LinkedCategory."); //$NON-NLS-1$
	}

	/**
	 * Returns the <code>LocaleDependantFields</code> of the given locale.
	 * 
	 * @param locale the locale
	 * @return the <code>LocaleDependantFields</code> of the given locale if it exists, otherwise an empty <code>LocaleDependantFields</code>.
	 */
	public LocaleDependantFields getLocaleDependantFields(final Locale locale) {
		return this.getMasterCategory().getLocaleDependantFields(locale);
	}

	/**
	 * Returns the <code>LocaleDependantFields</code> of the given locale without a fallback.
	 * 
	 * @param locale the locale
	 * @return the <code>LocaleDependantFields</code> of the given locale
	 */
	public LocaleDependantFields getLocaleDependantFieldsWithoutFallBack(final Locale locale) {
		return this.getMasterCategory().getLocaleDependantFieldsWithoutFallBack(locale);
	}

	/**
	 * Sets the <code>LocaleDependantFields</code>.
	 * 
	 * @param ldf the <code>LocaleDependantFields</code> instance to set.
	 */
	public void addOrUpdateLocaleDependantFields(final LocaleDependantFields ldf) {
		throw new EpUnsupportedOperationException("Cannot add or update locale dependant fields on LinkedCategory."); //$NON-NLS-1$
	}

	/**
	 * Returns the display name of the given locale. This method provides a faster way to just get display name. The result is the same with
	 * <code>getLocaleDependantFields(Locale).getDisplayName()</code>.
	 * 
	 * @param locale the locale
	 * @return the display name
	 */
	public String getDisplayName(final Locale locale) {
		return this.getMasterCategory().getDisplayName(locale);
	}

	/**
	 * Returns true if the product should not be displayed (e.g. in its category or as a search result).
	 * 
	 * @return true if the product should not be displayed
	 */
	@Transient
	public boolean isHidden() {
		return this.getMasterCategory().isHidden();
	}

	/**
	 * Set to true if the product should not be displayed.
	 * 
	 * @param hidden true if the product should not be displayed
	 */
	public void setHidden(final boolean hidden) {
		throw new EpUnsupportedOperationException("Cannot set hidden on LinkedCategory."); //$NON-NLS-1$
	}

	/**
	 * Get the attribute value group.
	 * 
	 * @return the domain model's <code>AttributeValueGroup</code>
	 */
	@Transient
	public AttributeValueGroup getAttributeValueGroup() {
		return this.getMasterCategory().getAttributeValueGroup();
	}

	/**
	 * Set the attribute value group.
	 * 
	 * @param attributeValueGroup the <code>AttributeValueGroup</code>
	 */
	public void setAttributeValueGroup(final AttributeValueGroup attributeValueGroup) {
		throw new EpUnsupportedOperationException("Cannot set attribute value group on LinkedCategory."); //$NON-NLS-1$
	}

	/**
	 * Returns the top sellers for this category.
	 * 
	 * @return top sellers for this category
	 */
	@Transient
	public Set<TopSeller> getTopSellers() {
		return this.getMasterCategory().getTopSellers();
	}

	/**
	 * Sets the top sellers for this category.
	 * 
	 * @param topSellers the top sellers for this category
	 */
	public void setTopSellers(final Set<TopSeller> topSellers) {
		throw new EpUnsupportedOperationException("Cannot set top sellers on LinkedCategory."); //$NON-NLS-1$
	}

	/**
	 * Returns the category code.
	 * 
	 * @return the category code
	 */
	@Transient
	public String getCode() {
		if (getMasterCategory() == null) {
			return null;
		}
		return getMasterCategory().getCode();
	}

	/**
	 * Sets the category code.
	 * 
	 * @param code the category code
	 */
	public void setCode(final String code) {
		throw new EpUnsupportedOperationException("Code is automatically generated. It does not support setting it explicitly.");
	}

	/**
	 * Return the guid.
	 * 
	 * @return the guid.
	 */
	@Override
	@Transient
	public String getGuid() {
		return getCode();
	}

	/**
	 * Set the guid.
	 * 
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		// no-op: do not call setCode(guid);
	}

	/**
	 * Get the attribute value map.
	 * 
	 * @return the map
	 */
	@Transient
	public Map<String, AttributeValue> getAttributeValueMap() {
		return this.getMasterCategory().getAttributeValueMap();
	}

	/**
	 * Set the attribute value map.
	 * 
	 * @param attributeValueMap the map
	 */
	public void setAttributeValueMap(final Map<String, AttributeValue> attributeValueMap) {
		throw new EpUnsupportedOperationException("Cannot set attribute value map on LinkedCategory."); //$NON-NLS-1$
	}

	/**
	 * Get the indicator of whether this is a virtual category.
	 * 
	 * @return true if this is a virtual category
	 */
	@Transient
	public boolean isVirtual() {
		return true;
	}

	/**
	 * Set the indicator of whether this is a virtual category.
	 * 
	 * @param virtual true if the category is virtual
	 */
	public void setVirtual(final boolean virtual) {
		throw new EpUnsupportedOperationException("Cannot set virtual on LinkedCategory."); //$NON-NLS-1$
	}

	/**
	 * Get the master category this virtual category is derived from (null if this category is a master).
	 * 
	 * @return the master category.
	 */
	@ManyToOne(targetEntity = CategoryImpl.class, optional = false, fetch = FetchType.EAGER, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "MASTER_CATEGORY_UID")
	@Nonpolymorphic(NonpolymorphicType.EXACT)
	public Category getMasterCategory() {
		return this.masterCategory;
	}

	/**
	 * Set the master category this virtual category is derived from (null if this a master category).
	 * 
	 * @param masterCategory the master category
	 */
	public void setMasterCategory(final Category masterCategory) {
		this.masterCategory = masterCategory;
	}

	/**
	 * Get the indicator of whether or not this category has been included.
	 * 
	 * @return true if this category has been included
	 */
	@Basic
	@Column(name = "INCLUDE", nullable = false)
	public boolean isIncluded() {
		return this.include;
	}

	/**
	 * Set the indicator of whether or not this category has been included.
	 * 
	 * @param include true if this category has been included
	 */
	public void setIncluded(final boolean include) {
		this.include = include;
	}

	/**
	 * Returns true if this category is linked (i.e. derived from a master category); false if it is a master category.
	 * 
	 * @return true if this category is linked (i.e. derived from a master category); false if it is a master category
	 */
	@Transient
	public boolean isLinked() {
		return true;
	}

	/**
	 * Sets the display name of this linked category.
	 * 
	 * @param name the string name
	 * @param locale the locale the name is valid for
	 */
	public void setDisplayName(final String name, final Locale locale) {
		getLocaleDependantFields(locale).setDisplayName(name);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		if (this.masterCategory != this  && this.masterCategory instanceof CategoryImpl) {
			result = prime * result + ObjectUtils.hashCode(this.masterCategory);
		} 
		result = prime * result + super.hashCode();

		return result;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof LinkedCategoryImpl)) {
			return false;
		}
		final LinkedCategoryImpl category = (LinkedCategoryImpl) other;
		
		boolean result = true;
		result &= ObjectUtils.equals(this.masterCategory, category.masterCategory);
		result &= super.equals(other);
		return result;
	}

	@Override
	public LocaleDependantFields getLocaleDependantFields(
			final LocaleFallbackPolicy policy) {
		return this.getMasterCategory().getLocaleDependantFields(policy);
	}
}
