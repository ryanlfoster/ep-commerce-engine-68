/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.tax.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumns;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.TaxCategoryLocalizedPropertyValueImpl;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCategoryTypeEnum;
import com.elasticpath.domain.tax.TaxRegion;

/**
 * The default implementation of <code>TaxCategory</code>.
 */
@Entity
@Table(name = TaxCategoryImpl.TABLE_NAME)
@DataCache(enabled = true)
public class TaxCategoryImpl extends AbstractLegacyEntityImpl implements TaxCategory {
	private static final int BASE = 17;

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TTAXCATEGORY";

	private String name;

	private Map<String, LocalizedPropertyValue> localizedPropertiesMap = new HashMap<String, LocalizedPropertyValue>();

	private LocalizedProperties localizedProperties;

	private TaxCategoryTypeEnum fieldMatchType;

	private Set<TaxRegion> taxRegionSet = new HashSet<TaxRegion>();

	private long uidPk;

	private String guid;

	/**
	 * The constructor.
	 */
	public TaxCategoryImpl() {
		// default constructor.
	}

	/**
	 * The copy constructor. Does shallow copy of existing tax category.
	 * 
	 * @param taxCategory taxCategory to be shallow cloned
	 */
	public TaxCategoryImpl(final TaxCategory taxCategory) {
		this.name = taxCategory.getName();
		this.localizedProperties = taxCategory.getLocalizedProperties();
		this.fieldMatchType = taxCategory.getFieldMatchType();
	}

	/**
	 * Get the tax category name.
	 * 
	 * @return the parameter name
	 */
	@Basic
	@Column(name = "NAME", nullable = false)
	public String getName() {
		return this.name;
	}

	/**
	 * Set the tax category name.
	 * 
	 * @param name the parameter name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Returns the display name of the <code>TaxCategory</code> with the given locale.
	 * 
	 * @param locale the locale
	 * @return the display name of the taxCategory displayName
	 */
	public String getDisplayName(final Locale locale) {
		String displayName = null;
		if (getLocalizedProperties() != null) {
			displayName = getLocalizedProperties().getValue(TaxCategory.LOCALIZED_PROPERTY_DISPLAY_NAME, locale);
		}
		if (displayName == null) {
			displayName = getName();
		}
		return displayName;
	}

	/**
	 * Returns the <code>LocalizedProperties</code>, i.e. <code>TaxCategory</code> name.
	 * 
	 * @return the <code>LocalizedProperties</code>
	 */
	@Transient
	public LocalizedProperties getLocalizedProperties() {
		if (localizedProperties == null) {
			localizedProperties = getBean(ContextIdNames.LOCALIZED_PROPERTIES);
			localizedProperties.setLocalizedPropertiesMap(getLocalizedPropertiesMap(), ContextIdNames.TAX_CATEGORY_LOCALIZED_PROPERTY_VALUE);
		}
		return localizedProperties;
	}

	/**
	 * Set the <code>LocalizedProperties</code>, i.e. <code>TaxCategory</code> name.
	 * 
	 * @param localizedProperties - the <code>LocalizedProperties</code>
	 */
	public void setLocalizedProperties(final LocalizedProperties localizedProperties) {
		this.localizedProperties = localizedProperties;
		this.setLocalizedPropertiesMap(localizedProperties.getLocalizedPropertiesMap());
	}

	/**
	 * Get the localized properties map.
	 * 
	 * @return the map
	 */
	@OneToMany(targetEntity = TaxCategoryLocalizedPropertyValueImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@MapKey(name = "localizedPropertyKey")
	@ElementJoinColumns({
		@ElementJoinColumn(name = "OBJECT_UID", referencedColumnName = "UIDPK", nullable = false),
		@ElementJoinColumn(name = "TLOCALIZEDPROPERTIES.TYPE", referencedColumnName = "'TaxCategory'")
	})	
	@ElementDependent
	public Map<String, LocalizedPropertyValue> getLocalizedPropertiesMap() {
		return localizedPropertiesMap;
	}

	/**
	 * Set the property map.
	 * 
	 * @param localizedPropertiesMap the map to set
	 */
	public void setLocalizedPropertiesMap(final Map<String, LocalizedPropertyValue> localizedPropertiesMap) {
		this.localizedPropertiesMap = localizedPropertiesMap;
	}

	/**
	 * Retrieve the field match type for this <code>TaxCategory</code>.
	 * 
	 * @return the field match type for this <code>TaxCategory</code>.
	 */
	@Persistent(optional = false)
	@Externalizer("getIntValue")
	@Factory("TaxCategoryTypeEnum.getInstance")
	@Column(name = "FIELD_MATCH_TYPE")
	public TaxCategoryTypeEnum getFieldMatchType() {
		return this.fieldMatchType;
	}

	/**
	 * Set the field match type for this <code>TaxJurisdiction</code>.
	 * 
	 * @param fieldMatchType the field match type.
	 */
	public void setFieldMatchType(final TaxCategoryTypeEnum fieldMatchType) {
		this.fieldMatchType = fieldMatchType;
	}

	@Override
	@OneToMany(targetEntity = TaxRegionImpl.class, cascade = { CascadeType.ALL },
			fetch = FetchType.EAGER)
	@ElementJoinColumn(name = "TAX_CATEGORY_UID")
	@ElementForeignKey
	@ElementDependent
	public Set<TaxRegion> getTaxRegionSet() {
		return taxRegionSet;
	}

	@Override
	public void setTaxRegionSet(final Set<TaxRegion> taxRegionSet) {
		this.taxRegionSet = taxRegionSet;
	}

	@Override
	public void addTaxRegion(final TaxRegion taxRegion) {
		if (getTaxRegionSet().contains(taxRegion.getRegionName())) {
			throw new EpDomainException("The region of the same name " + taxRegion.getRegionName() + " already exist.");
		}

		getTaxRegionSet().add(taxRegion);
	}

	@Override
	public TaxRegion getTaxRegion(final String taxRegionName) {
		for (TaxRegion taxRegion : getTaxRegionSet()) {
			if (taxRegion.getRegionName().equals(taxRegionName)) {
				return taxRegion;
			}
		}
		return null;
	}

	@Override
	public TaxRegion removeTaxRegion(final String taxRegionName) {
		for (TaxRegion taxRegion : getTaxRegionSet()) {
			if (taxRegion.getRegionName().equals(taxRegionName)) {
				getTaxRegionSet().remove(taxRegion);
				return taxRegion;
			}
		}
		return null;
	}

	@Override
	public TaxRegion removeTaxRegion(final TaxRegion taxRegion) {
		return removeTaxRegion(taxRegion.getRegionName());
	}

	/**
	 * Return the guid.
	 * 
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID", nullable = false, length = GUID_LENGTH)
	public String getGuid() {
		return guid;
	}

	/**
	 * Set the guid.
	 * 
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 * 
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Returns the String representation of a TaxCategory object.
	 * 
	 * @return String
	 */
	@Override
	public String toString() {
		return "Name: " + getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = BASE;
		
		if (getName() == null) {
			result = prime * result;
		} else {
			result = prime * result + getName().hashCode();
		}
		
		if (getLocalizedProperties() != null) {
			result = prime * result + getLocalizedProperties().hashCode();
		}
		
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		TaxCategory other = (TaxCategory) obj;
		return new EqualsBuilder().append(getName(), other.getName())
			.append(getLocalizedProperties(), other.getLocalizedProperties()).isEquals();
	}

}
