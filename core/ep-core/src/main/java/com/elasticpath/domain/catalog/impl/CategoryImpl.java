/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.ElementType;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.attribute.AttributeValueGroupFactory;
import com.elasticpath.domain.attribute.impl.AttributeValueGroupFactoryImpl;
import com.elasticpath.domain.attribute.impl.CategoryAttributeValueFactoryImpl;
import com.elasticpath.domain.attribute.impl.CategoryAttributeValueImpl;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.TopSeller;
import com.elasticpath.domain.localization.LocaleFallbackPolicy;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * The default implementation of <code>Category</code>. Represents categories that are not linked.
 * <br/>
 * 
 * NOTE that the presence of the {@code DatabaseLastModifiedDate} means that whenever this object is saved or updated to the database
 * the lastModifiedDate will be set by the {@code LastModifiedPersistenceEngineImpl} if that class in configured in Spring. 
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@Entity
@Table(name = CategoryImpl.TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = "CODE"))
@PrimaryKeyJoinColumn(name = "UIDPK", referencedColumnName = "UIDPK")
@DiscriminatorValue("masterCategory")
@FetchGroups({
		@FetchGroup(name = FetchGroupConstants.CATEGORY_INDEX, attributes = { 
				@FetchAttribute(name = "code"),
				@FetchAttribute(name = "startDate"),
				@FetchAttribute(name = "endDate"), 
				@FetchAttribute(name = "hidden"),
				@FetchAttribute(name = "localeDependantFieldsMap") }),
		@FetchGroup(name = FetchGroupConstants.INFINITE_CHILD_CATEGORY_DEPTH, attributes = {
				@FetchAttribute(name = "code") }),
		@FetchGroup(name = FetchGroupConstants.CATEGORY_AVAILABILITY, attributes = {
				@FetchAttribute(name = "code"),
				@FetchAttribute(name = "startDate"),
				@FetchAttribute(name = "endDate"), 
				@FetchAttribute(name = "hidden") }),
		@FetchGroup(name = FetchGroupConstants.CATEGORY_BASIC, 
				fetchGroups = { FetchGroupConstants.CATEGORY_AVAILABILITY }, attributes = { 
				@FetchAttribute(name = "code"), 
				@FetchAttribute(name = "localeDependantFieldsMap"),
				@FetchAttribute(name = "virtual"), 
				@FetchAttribute(name = "categoryType") }),
		@FetchGroup(name = FetchGroupConstants.CATEGORY_HASH_MINIMAL, attributes = { 
				@FetchAttribute(name = "code") }),
		@FetchGroup(name = FetchGroupConstants.LINK_PRODUCT_CATEGORY, fetchGroups = { 
				FetchGroupConstants.CATEGORY_HASH_MINIMAL }, attributes = { 
				@FetchAttribute(name = "code") }),
		@FetchGroup(name = FetchGroupConstants.CATEGORY_ATTRIBUTES, attributes = {
				@FetchAttribute(name = "code"),
				@FetchAttribute(name = "attributeValueMap") })
})

public class CategoryImpl extends AbstractCategoryImpl implements Category {
	private static final Logger LOG = Logger.getLogger(CategoryImpl.class);

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TMASTERCATEGORY";

	private Date startDate;

	private Date endDate;

	private CategoryType categoryType;

	private Map<Locale, LocaleDependantFields> localeDependantFieldsMap = new HashMap<Locale, LocaleDependantFields>();

	private boolean hidden;

	private AttributeValueGroup attributeValueGroup;

	private Set<TopSeller> topSellers;

	private String code;

	private Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();

	private boolean virtual;
	

	/**
	 * Default constructor.
	 */
	public CategoryImpl() {
		super();
	}

	/**
	 * Get the start date that this category will become available to customers.
	 *
	 * @return the start date
	 */
	@Basic
	@Column(name = "START_DATE")
	public Date getStartDate() {
		return this.startDate;
	}

	/**
	 * Set the start date that this category will become valid.
	 *
	 * @param startDate the start date
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Get the end date. After the end date, the category will become unavailable to customers.
	 *
	 * @return the end date
	 */
	@Basic
	@Column(name = "END_DATE")
	public Date getEndDate() {
		return this.endDate;
	}

	/**
	 * Set the end date.
	 *
	 * @param endDate the end date
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * Gets the display template of this category.
	 *
	 * @return the template
	 */
	@Transient
	public String getTemplate() {
		return this.getCategoryType().getTemplate();
	}

	/**
	 * Get the display template name of this category.
	 *
	 * @param defaultTemplate the default template name
	 * @return the template name if it is defined for this category, otherwise the given default template name
	 */
	public String getTemplateWithFallBack(final String defaultTemplate) {
		String templateName;
		if (this.getCategoryType() == null || this.getCategoryType().getTemplate() == null) {
			templateName = defaultTemplate;
		} else {
			templateName = this.getCategoryType().getTemplate();
		}

		return templateName;
	}

	/**
	 * Returns <code>true</code> if the category is available to be displayed.
	 *
	 * @return <code>true</code> if the category is available.
	 */
	@Transient
	public boolean isAvailable() {
		final Date currentDate = new Date();
		if (this.getStartDate() == null || currentDate.getTime() < this.getStartDate().getTime()) {
			return false;
		} else if (this.getEndDate() != null && currentDate.getTime() > this.getEndDate().getTime()) {
			return false;
		} else if (isHidden()) {
			return false;
		}
		return true;
	}

	/**
	 * Set default values for those fields that need default values.
	 */
	@Override
	public void initialize() {
		super.initialize();

		if (this.getStartDate() == null) {
			this.setStartDate(new Date());
		}

		if (this.getAttributeValueGroup() == null) {
			initializeAttributeValueGroup();
		}
	}

	/**
	 * Set the <code>CategoryType</code>.
	 *
	 * @param categoryType the <code>CategoryType</code>
	 */
	public void setCategoryType(final CategoryType categoryType) {
		this.categoryType = categoryType;
	}

	/**
	 * Returns the category type.
	 *
	 * @return the category type
	 */
	@ManyToOne(targetEntity = CategoryTypeImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "CATEGORY_TYPE_UID")
	public CategoryType getCategoryType() {
		return this.categoryType;
	}

	/**
	 * Gets the all <code>LocaleDependantFields</code> as a map.
	 *
	 * @return the <code>LocaleDependantFields</code> map
	 */
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@ElementType(value = CategoryLocaleDependantFieldsImpl.class)
	@MapKey(name = "locale")
	@ElementJoinColumn(name = "CATEGORY_UID", nullable = false)
	@ElementDependent
	@ElementForeignKey(name = "TCATEGORYLDF_IBFK_1")
	protected Map<Locale, LocaleDependantFields> getLocaleDependantFieldsMap() {
		return this.localeDependantFieldsMap;
	}

	/**
	 * Sets all <code>LocaleDependantFields</code> with the given map.
	 *
	 * @param localeDependantFieldsMap the <code>LocaleDependantFields</code> map to set
	 */
	public void setLocaleDependantFieldsMap(final Map<Locale, LocaleDependantFields> localeDependantFieldsMap) {
		this.localeDependantFieldsMap = localeDependantFieldsMap;
	}

	/**
	 * Returns the {@link LocaleDependantFields} of the given locale (cannot be <code>null</code>).
	 * Falls back to the language and country (if variant present) then to the language (if
	 * country is present). If no field is defined for the given locale, optionally falls
	 * back to this Product's Master Catalog's default locale, for which the product is
	 * guaranteed to have values for its locale-dependent fields.
	 *
	 * @param locale the locale
	 * @param fallback whether to fall back to the Master Catalog's default locale if
	 * there is no LocaleDependantFields object for the given locale
	 * @return the {@link LocaleDependantFields} of the given non-null locale if it exists, or
	 * 	for the fallback locale if requested, an empty LDF in the given locale if neither can be found.
	 */
	public LocaleDependantFields getLocaleDependantFields(final Locale locale, final boolean fallback) {
		CatalogLocaleFallbackPolicyFactory factory = getBean(ContextIdNames.LOCALE_FALLBACK_POLICY_FACTORY);
		return getLocaleDependantFields(factory.createCategoryLocaleFallbackPolicy(locale, fallback, this));
	}

	/**
	 * Creates a LocaleDependantFields value object for this category. Override this method if you need to specify an alternative implementation
	 * class for the LocaleDependantFields implementor.
	 * 
	 * @param locale The Locale associated with this LocaleDependantFields object
	 * @return the LocaleDependantFields object.
	 */
	protected LocaleDependantFields createLocaleDependantFields(final Locale locale) {
		final LocaleDependantFields ldf = new CategoryLocaleDependantFieldsImpl();
		ldf.setLocale(locale);

		return ldf;
	}

	/**
	 * @return the default locale of the containing Catalog
	 */
	@Transient
	protected Locale getDefaultLocale() {
		return this.getCatalog().getDefaultLocale();
	}
	
	/**
	 * Returns the {@link LocaleDependantFields} of the given locale (cannot be <code>null</code>).
	 * Falls back to the language and country (if variant present) then to the language (if
	 * country is present). If no field is defined for the given locale, returns a fallback.
	 * This implementation will check that the fields returned have a Category's Description
	 * filled in, and if not will attempt to get the Description from the containing Category.
	 * {@link LocaleDependantFields}.
	 *
	 * @param locale the locale
	 * @return the {@link LocaleDependantFields} of the given non-null locale if it exists,
	 *         otherwise an empty {@link LocaleDependantFields}
	 */
	public LocaleDependantFields getLocaleDependantFields(final Locale locale) {
		return this.getLocaleDependantFields(locale, true);
	}

	/**
	 * Returns the <code>LocaleDependantFields</code> of the given locale without a fallback.
	 *
	 * @param locale the locale
	 * @return the <code>LocaleDependantFields</code> of the given locale
	 */
	public LocaleDependantFields getLocaleDependantFieldsWithoutFallBack(final Locale locale) {
		return this.getLocaleDependantFields(locale, false);
	}

	/**
	 * Sets the <code>LocaleDependantFields</code>.
	 *
	 * @param ldf the <code>LocaleDependantFields</code> instance to set.
	 */
	public void addOrUpdateLocaleDependantFields(final LocaleDependantFields ldf) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("addOrUpdateLDF for ldf = " + ldf + " (locale = " + ldf.getLocale().toString());
		}
		if (ldf.getLocale() == null) {
			throw new EpDomainException("Locale is not set.");
		}
		getLocaleDependantFieldsMap().put(ldf.getLocale(), ldf);
	}

	/**
	 * Returns the display name of the given locale, falling back to the
	 * display name in the catalog's default locale if required.
	 * 
	 * This method is a convenience method that calls getLocaleDependantFields(locale, true).
	 *
	 * @param locale the locale
	 * @return the display name
	 */
	public String getDisplayName(final Locale locale) {
		String displayName = getLocaleDependantFields(locale, true).getDisplayName();
		if (displayName == null) {
			displayName = getLocaleDependantFields(super.getCatalog().getDefaultLocale(), true).getDisplayName();
		}
		return displayName;
	}

	/**
	 * Returns true if the product should not be displayed (e.g. in its category or as a search result).
	 *
	 * @return true if the product should not be displayed
	 */
	@Basic
	@Column(name = "HIDDEN")
	public boolean isHidden() {
		return this.hidden;
	}

	/**
	 * Set to true if the product should not be displayed.
	 *
	 * @param hidden true if the product should not be displayed
	 */
	public void setHidden(final boolean hidden) {
		this.hidden = hidden;
	}

	/**
	 * Get the attribute value group.
	 *
	 * @return the domain model's <code>AttributeValueGroup</code>
	 */
	@Transient
	public AttributeValueGroup getAttributeValueGroup() {
		if (this.attributeValueGroup == null) {
			this.initializeAttributeValueGroup();
		}
		return this.attributeValueGroup;
	}

	private void initializeAttributeValueGroup() {
		this.attributeValueGroup = getAttributeValueGroupFactory().createAttributeValueGroup(getAttributeValueMap());
	}

	/**
	 * Creates the factory used to create AttributeValueGroups (and AttributeValues).  Override this method
	 * in an extension class if you need to specify a custom implementation for AttributeValueGroupImpl or
	 * ProductAttributeValueImpl.
	 *
	 * @return the factory
	 */
	@Transient
	protected AttributeValueGroupFactory getAttributeValueGroupFactory() {
		return new AttributeValueGroupFactoryImpl(new CategoryAttributeValueFactoryImpl());
	}

	/**
	 * Set the attribute value group.
	 *
	 * @param attributeValueGroup the <code>AttributeValueGroup</code>
	 */
	public void setAttributeValueGroup(final AttributeValueGroup attributeValueGroup) {
		this.attributeValueGroup = attributeValueGroup;
		if (attributeValueGroup != null) {
			this.setAttributeValueMap(attributeValueGroup.getAttributeValueMap());
		}
	}

	/**
	 * Sets the top sellers for this category.
	 *
	 * @param topSellers the top sellers for this category
	 */
	public void setTopSellers(final Set<TopSeller> topSellers) {
		this.topSellers = topSellers;
	}

	/**
	 * Returns the top sellers for this category.
	 *
	 * @return top sellers for this category
	 */
	@Transient
	public Set<TopSeller> getTopSellers() {
		return this.topSellers;
	}

	/**
	 * Returns the category code.
	 *
	 * @return the category code
	 */
	@Basic(optional = false)
	@Column(name = "CODE", length = AbstractEntityImpl.GUID_LENGTH, nullable = false, unique = true)
	public String getCode() {
		return this.code;
	}

	/**
	 * Sets the category code.
	 *
	 * @param code the category code
	 */
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	@Transient
	public String getGuid() {
		return this.getCode();
	}

	/**
	 * Set the guid.
	 *
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.setCode(guid);
	}

	/**
	 * Get the attribute value map.
	 *
	 * @return the map
	 */
	@OneToMany(targetEntity = CategoryAttributeValueImpl.class, cascade = CascadeType.ALL)
	@MapKey(name = "localizedAttributeKey")
	@ElementJoinColumn(name = "CATEGORY_UID", nullable = false)
	@ElementDependent
	@ElementForeignKey(name = "TCATEGORYATTRIBUTEVALUE_IBFK_2")
	public Map<String, AttributeValue> getAttributeValueMap() {
		return this.attributeValueMap;
	}

	/**
	 * Set the attribute value map.
	 *
	 * @param attributeValueMap the map
	 */
	public void setAttributeValueMap(final Map<String, AttributeValue> attributeValueMap) {
		this.attributeValueMap = attributeValueMap;
	}

	/**
	 * Get the indicator of whether this is a virtual category.
	 *
	 * @return true if this is a virtual category
	 */
	@Basic
	@Column(name = "VIRTUAL", nullable = false)
	public boolean isVirtual() {
		return this.virtual;
	}

	/**
	 * Set the indicator of whether this is a virtual category.
	 *
	 * @param virtual true if the category is virtual
	 */
	public void setVirtual(final boolean virtual) {
		this.virtual = virtual;
	}

	/**
	 * Returns true if this category is linked (i.e. derived from a master category); false if it is a master category.
	 *
	 * @return true if this category is linked (i.e. derived from a master category); false if it is a master category
	 */
	@Transient
	public boolean isLinked() {
		return false;
	}

	/**
	 * Get the master category this virtual category is derived from (null if this category is a master).
	 *
	 * @return the master category.
	 */
	@Transient
	public Category getMasterCategory() {
		return null;
	}

	/**
	 * Set the master category this virtual category is derived from (null if this a master category).
	 *
	 * @param masterCategory the master category
	 */
	public void setMasterCategory(final Category masterCategory) {
		throw new EpUnsupportedOperationException("Cannot set master category on CategoryImpl."); //$NON-NLS-1$
	}

	/**
	 * Get the indicator of whether or not this category has been included.
	 *
	 * @return true if this category has been included
	 */
	@Transient
	public boolean isIncluded() {
		throw new EpUnsupportedOperationException("Cannot get include on CategoryImpl."); //$NON-NLS-1$
	}

	/**
	 * Set the indicator of whether or not this category has been included.
	 *
	 * @param include true if this category has been included
	 */
	public void setIncluded(final boolean include) {
		throw new EpUnsupportedOperationException("Cannot set include on CategoryImpl."); //$NON-NLS-1$
	}

	/**
	 * Sets the category display name.
	 *
	 * @param name the name
	 * @param locale the locale it is valid for
	 */
	public void setDisplayName(final String name, final Locale locale) {
		LocaleDependantFields ldf = getLocaleDependantFieldsWithoutFallBack(locale);
		ldf.setDisplayName(name);
		addOrUpdateLocaleDependantFields(ldf);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(code);
	}
	
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof CategoryImpl)) {
			return false;
		}
		
		CategoryImpl category = (CategoryImpl) other;
		return ObjectUtils.equals(code, category.code);
	}

	@Override
	public LocaleDependantFields getLocaleDependantFields(final LocaleFallbackPolicy policy) {
		LocaleDependantFields ldf = null;
		for (Locale locale : policy.getLocales()) {
			ldf = getLocaleDependantFieldsMap().get(locale);
			if (ldf != null) {
				break;
			}
		}
		// create a new LDF
		if (ldf == null) {
			ldf = createLocaleDependantFields(policy.getPrimaryLocale());
		}
		return ldf;		
	}

}
