/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.skuconfiguration.impl;

import java.util.Collection;
import java.util.HashMap;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumns;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.SkuOptionLocalizedPropertyValueImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * Represents a SKU option that can be configured.
 */
@Entity
@Table(name = SkuOptionImpl.TABLE_NAME)
@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = { @FetchAttribute(name = "optionKey"), 
		@FetchAttribute(name = "catalog") })
@SuppressWarnings("PMD.UselessOverridingMethod")
public class SkuOptionImpl extends AbstractLegacyEntityImpl implements SkuOption, Cloneable {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSKUOPTION";

	/**
	 * The name of localized property key -- display name.
	 */
	protected static final String LOCALIZED_PROPERTY_DISPLAY_NAME = "skuOptionDisplayName";

	private String optionKey;

	private Map<String, SkuOptionValue> optionValueMap = new HashMap<String, SkuOptionValue>();

	private SkuOptionValue defaultOptionValue;

	private LocalizedProperties localizedProperties;
	
	private Map<String, LocalizedPropertyValue> localizedPropertiesMap = new HashMap<String, LocalizedPropertyValue>();

	private Catalog catalog;

	private long uidPk;

	/**
	 * Get the key of this SKU option (e.g. Color).
	 *
	 * @return the SKU option key.
	 */
	@Basic
	@Column(name = "OPTION_KEY", nullable = false, unique = true)
	public String getOptionKey() {
		return this.optionKey;
	}

	/**
	 * Set the key of this SKU option.
	 *
	 * @param optionKey the key of the option (e.g. Color).
	 */
	public void setOptionKey(final String optionKey) {
		this.optionKey = optionKey;
	}

	/**
	 * Get the available values for this SKU option.
	 *
	 * @return a set of <code>SkuValue</code>s
	 */
	@Transient
	public Collection<SkuOptionValue> getOptionValues() {
		return getOptionValueMap().values();
	}

	/**
	 * Sets the available values for this SKU option.
	 *
	 * @param optionValues a set of <code>SkuOptionValue</code>s
	 */
	public void setOptionValues(final Set<SkuOptionValue> optionValues) {
		final Map<String, SkuOptionValue> newOptionValueMap = new HashMap<String, SkuOptionValue>();
		for (SkuOptionValue skuOptionValue : optionValues) {
			newOptionValueMap.put(skuOptionValue.getOptionValueKey(), skuOptionValue);
		}

		setOptionValueMap(newOptionValueMap);
	}

	/**
	 * Add an option value to the set of available values.
	 *
	 * @param optionValue an <code>OptionValue</code>
	 */
	public void addOptionValue(final SkuOptionValue optionValue) {
		if (optionValue.getOptionValueKey() == null) {
			throw new EpDomainException("Option value code is not set.");
		}
		if (!this.equals(optionValue.getSkuOption())) {
			optionValue.setSkuOption(this);
		}
		getOptionValueMap().put(optionValue.getOptionValueKey(), optionValue);
	}

	/**
	 * Set the option value that is to appear by default if no option has yet been selected. Note that the default option value is computed based on
	 * the default SKU and is not persisted
	 *
	 * @param defaultOptionValue the default <code>SkuOptionValue</code>
	 */
	public void setDefaultOptionValue(final SkuOptionValue defaultOptionValue) {
		this.defaultOptionValue = defaultOptionValue;
	}

	/**
	 * Get the option value that has been designated the default value if no option has yet been selected. Note that the default option value is
	 * computed based on the default SKU and is not persisted
	 *
	 * @return the default <code>SkuOptionValue</code>
	 */
	@Transient
	public SkuOptionValue getDefaultOptionValue() {
		return this.defaultOptionValue;
	}

	/**
	 * Create a deep copy of this <code>SkuOption</code>.
	 *
	 * @return a deep copy
	 * @throws CloneNotSupportedException if the object cannot be cloned
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 * Returns the <code>LocalizedProperties</code>.
	 *
	 * @return the <code>LocalizedProperties</code>
	 */
	@Transient
	public LocalizedProperties getLocalizedProperties() {
		if (localizedProperties == null) {
			localizedProperties = getBean(ContextIdNames.LOCALIZED_PROPERTIES);
			localizedProperties.setLocalizedPropertiesMap(getLocalizedPropertiesMap(), ContextIdNames.SKU_OPTION_LOCALIZED_PROPERTY_VALUE);
		}
		return localizedProperties;
	}

	/**
	 * Sets the <code>LocalizedProperties</code>.
	 *
	 * @param localizedProperties the <code>LocalizedProperties</code>
	 */
	protected void setLocalizedProperties(final LocalizedProperties localizedProperties) {
		this.localizedProperties = localizedProperties;
		if (localizedProperties == null) {
			setLocalizedPropertiesMap(null);
		} else {
			setLocalizedPropertiesMap(localizedProperties.getLocalizedPropertiesMap());
		}
	}

	/**
	 * Get the available values for this SKU option.
	 *
	 * @return a map of <code>SkuValue</code>s
	 */
	@OneToMany(targetEntity = SkuOptionValueImpl.class, mappedBy = "skuOptionInternal", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@MapKey(name = "optionValueKey")
	@ElementDependent
	protected Map<String, SkuOptionValue> getOptionValueMap() {
		return this.optionValueMap;
	}

	/**
	 * Set the available values for this SKU option.
	 *
	 * @param optionValueMap the map of <code>SkuValue</code>s.
	 */
	protected void setOptionValueMap(final Map<String, SkuOptionValue> optionValueMap) {
		this.optionValueMap = optionValueMap;
	}

	/**
	 * Returns <code>true</code> if this <code>SkuOption</code> contains the given value code.
	 *
	 * @param valueCode the sku option value code
	 * @return <code>true</code> if this <code>SkuOption</code> contains the given value code
	 */
	public boolean contains(final String valueCode) {
		return getOptionValueMap().containsKey(valueCode);
	}

	/**
	 * Returns the corresponding <code>SkuOptionValue</code> of the given value code.
	 *
	 * @param valueCode the sku option value code
	 * @return the corresponding <code>SkuOptionValue</code> of the given value code
	 */
	public SkuOptionValue getOptionValue(final String valueCode) {
		return getOptionValueMap().get(valueCode);
	}

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	@Transient
	public String getGuid() {
		return this.getOptionKey();
	}

	/**
	 * Set the guid.
	 *
	 * @param guid the guid to set.
	 */
	@Override
	@Transient
	public void setGuid(final String guid) {
		this.setOptionKey(guid);
	}

	/**
	 * Get the localized properties map.
	 * @return the map
	 */
	@OneToMany(targetEntity = SkuOptionLocalizedPropertyValueImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@MapKey(name = "localizedPropertyKey")
	@ElementJoinColumns({
		@ElementJoinColumn(name = "OBJECT_UID", referencedColumnName = "UIDPK", nullable = false),
		@ElementJoinColumn(name = "TLOCALIZEDPROPERTIES.TYPE", referencedColumnName = "'SkuOption'")
	})
	@ElementDependent
	protected Map<String, LocalizedPropertyValue> getLocalizedPropertiesMap() {
		return localizedPropertiesMap;
	}

	/**
	 * Set localized properties map.
	 * @param localizedPropertiesMap the map
	 */
	protected void setLocalizedPropertiesMap(final Map<String, LocalizedPropertyValue> localizedPropertiesMap) {
		this.localizedPropertiesMap = localizedPropertiesMap;
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
	 * Get the catalog that this sku option belongs to.
	 * @return the catalog
	 */
	@ManyToOne(optional = false, targetEntity = CatalogImpl.class, cascade = { CascadeType.REFRESH, CascadeType.MERGE })
	@JoinColumn(name = "CATALOG_UID", nullable = false)
	@ForeignKey
	public Catalog getCatalog() {
		return catalog;
	}

	/**
	 * Set the catalog that this sku option belongs to.
	 * @param catalog the catalog to set
	 */
	public void setCatalog(final Catalog catalog) {
		this.catalog = catalog;
	}
	
	
	/**
	 * Get the max ordering of the optionValues.
	 * @return the max ordering
	 */
	@Transient
	public int getMaxOrdering() {
		int maxOrdering = 0;
		for (SkuOptionValue skuOptionValue : getOptionValues()) {
			if (skuOptionValue.getOrdering() > maxOrdering) {
				maxOrdering = skuOptionValue.getOrdering();
			}
		}
		return maxOrdering;
	}

	/**
	 * Get the minimal ordering of the optionValues.
	 * @return the minimal ordering value
	 */
	@Transient
	public int getMinOrdering() {
		int minOrdering = getMaxOrdering();
		for (SkuOptionValue skuOptionValue : getOptionValues()) {
			if (skuOptionValue.getOrdering() < minOrdering) {
				minOrdering = skuOptionValue.getOrdering();
			}
		}
		return minOrdering;
	}

	/**
	 * Check if the optionValueKey exists.
	 * @param optionValueKey the option value key to be checked
	 * @return true if exist, otherwise false
	 */
	public boolean isValueKeyExist(final String optionValueKey) {
		boolean blValueKeyExist = false;
		for (SkuOptionValue skuOptionValue : getOptionValues()) {
			if (skuOptionValue.getOptionValueKey().equalsIgnoreCase(optionValueKey)) {
				blValueKeyExist = true;
			}
		}
		return blValueKeyExist;
	}	

	/**
	 * Sets the display name.
	 * 
	 * @param name the name
	 * @param locale the locale
	 */
	public void setDisplayName(final String name, final Locale locale) {
		getLocalizedProperties().setValue(LOCALIZED_PROPERTY_DISPLAY_NAME, locale, name);
	}
	
	/**
	 * Gets the display name of this SkuOption for the given locale,
	 * falling back to the SkuOption's Catalog's default locale
	 * if both requested and required, otherwise returns null
	 * if the display name doesn't exist for the given locale.
	 * @param locale the locale for which the display name should be returned
	 * @param fallback true if the SkuOption's Catalog's default locale should
	 * be used as a fallback, false if there is no fallback
	 * @return the display name in the given locale, or in the containing Catalog's
	 * default locale if fallback is requested and required, or null if no 
	 * display name is found
	 */
	@Transient
	public String getDisplayName(final Locale locale, final boolean fallback) {
		String displayName = getLocalizedProperties().getValue(LOCALIZED_PROPERTY_DISPLAY_NAME, locale);
		if (displayName == null && fallback) {
			displayName = getLocalizedProperties().getValue(LOCALIZED_PROPERTY_DISPLAY_NAME, this.getDefaultLocale());
		}
		return displayName;
	}
	
	/**
	 * Get the containing catalog's default locale.
	 * @return the containing catalog's default locale
	 */
	@Transient
	protected Locale getDefaultLocale() {
		return this.getCatalog().getDefaultLocale();
	}
	
	
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof SkuOptionImpl)) {
			return false;
		}
		SkuOptionImpl other = (SkuOptionImpl) obj;
		
		if (StringUtils.equals(this.optionKey, other.optionKey)
				&& ObjectUtils.equals(this.catalog, other.catalog)) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ObjectUtils.hashCode(this.optionKey);
		result = prime * result + ObjectUtils.hashCode(this.catalog);
		
		return result;
	}
	
	/**
	 * Removes a SKU option value from the list.
	 * 
	 * @param valueCode the SKU option value code
	 */
	public void removeOptionValue(final String valueCode) {
		getOptionValueMap().remove(valueCode);
	}

}
