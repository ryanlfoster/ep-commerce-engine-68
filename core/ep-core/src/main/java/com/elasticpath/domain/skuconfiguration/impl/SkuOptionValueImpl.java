/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.skuconfiguration.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumns;
import org.apache.openjpa.persistence.jdbc.ForeignKey;
import org.apache.openjpa.persistence.jdbc.VersionStrategy;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.SkuOptionValueLocalizedPropertyValueImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * Represents an available option value for a SKU option. Example option values include red, green, small, large, etc.
 */
@Entity
@VersionStrategy("state-comparison")
@Table(name = SkuOptionValueImpl.TABLE_NAME)
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = {
		@FetchAttribute(name = "skuOptionInternal"),
		@FetchAttribute(name = "optionValueKey"),
		@FetchAttribute(name = "localizedPropertiesMap")
		})
})
public class SkuOptionValueImpl extends AbstractLegacyEntityImpl implements SkuOptionValue {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of localized property -- display name.
	 */
	protected static final String LOCALIZED_PROPERTY_DISPLAY_NAME = "skuOptionValueDisplayName";

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSKUOPTIONVALUE";

	private String optionValueKey;

	private String image;

	private int ordering;

	private LocalizedProperties localizedProperties;

	private Map<String, LocalizedPropertyValue> localizedPropertiesMap = new HashMap<String, LocalizedPropertyValue>();

	private long uidPk;

	private SkuOption skuOption;

	/**
	 * Gets the associated SkuOption.
	 * The SkuOption is only used by other methods in this class in order
	 * to obtain the SkuOption's default Catalog, for localization purposes.
	 * @return the SkuOption containing this SkuOptionValue
	 */
	@ManyToOne(optional = false, targetEntity = SkuOptionImpl.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "SKU_OPTION_UID", nullable = false)
	@ForeignKey
	protected SkuOption getSkuOptionInternal() {
		return skuOption;
	}

	/**
	 * Sets the associated SkuOption.
	 * This method exists only so that JPA can load the associated
	 * SkuOption to get its Catalog, and hence its default locale when required.
	 *
	 * @param skuOption the SkuOption
	 */
	protected void setSkuOptionInternal(final SkuOption skuOption) {
		this.skuOption = skuOption;
	}

	/**
	 * Gets the associated SkuOption.
	 * @return the SkuOption containing this SkuOptionValue
	 */
	@Transient
	public SkuOption getSkuOption() {
		return getSkuOptionInternal();
	}

	/**
	 * Sets the associated SkuOption. This should only be used to maintain the bidirectional relationship.
	 * @param skuOption the SkuOption
	 */
	public void setSkuOption(final SkuOption skuOption) {
		setSkuOptionInternal(skuOption);
		if (skuOption.getOptionValue(getOptionValueKey()) == null) {
			skuOption.addOptionValue(this);
		}
	}

	/**
	 * Get the option value key corresponding to this option value.
	 *
	 * @return the option value key for this option
	 */
	@Basic
	@Column(name = "OPTION_VALUE_KEY", unique = true, nullable = false)
	public String getOptionValueKey() {
		return optionValueKey;
	}

	/**
	 * Set the option value key corresponding to this option value.
	 *
	 * @param optionValueKey the option value key for this option
	 */
	public void setOptionValueKey(final String optionValueKey) {
		this.optionValueKey = optionValueKey;
	}

	/**
	 * Return the guid.
	 *
	 * @return the guid.
	 */
	@Override
	@Transient
	public String getGuid() {
		return getOptionValueKey();
	}

	/**
	 * Set the guid.
	 *
	 * @param guid the guid to set.
	 */
	@Override
	@Transient
	public void setGuid(final String guid) {
		setOptionValueKey(guid);
	}

	/**
	 * Get the path to the image corresponding to this option value.
	 *
	 * @return the path to the image
	 */
	@Basic
	@Column(name = "IMAGE")
	public String getImage() {
		return image;
	}

	/**
	 * Set the path to the image corresponding to this option value.
	 *
	 * @param image the path to the image corresponding to this option value
	 */
	public void setImage(final String image) {
		this.image = image;
	}

	/**
	 * Get the ordering in which this SKU option value should appear.
	 *
	 * @return the ordering number
	 */
	@Basic
	@Column(name = "ORDERING")
	public int getOrdering() {
		return ordering;
	}

	/**
	 * Set the ordering in which this SKU option value should appear.
	 *
	 * @param ordering the ordering number
	 */
	public void setOrdering(final int ordering) {
		this.ordering = ordering;
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
			localizedProperties.setLocalizedPropertiesMap(getLocalizedPropertiesMap(), ContextIdNames.SKU_OPTION_VALUE_LOCALIZED_PROPERTY_VALUE);
		}
		return localizedProperties;
	}

	/**
	 * Sets the <code>LocalizedProperties</code>.
	 *
	 * @param localizedProperties the <code>LocalizedProperties</code>
	 */
	public void setLocalizedProperties(final LocalizedProperties localizedProperties) {
		this.localizedProperties = localizedProperties;
		if (localizedProperties == null) {
			setLocalizedPropertiesMap(null);
		} else {
			setLocalizedPropertiesMap(localizedProperties.getLocalizedPropertiesMap());
		}
	}

	/**
	 * Gets a string representation of the <code>SkuOptionValue</code>.
	 *
	 * @return the string representation
	 */
	@Override
	public String toString() {
		return getOptionValueKey();
	}

	/**
	 * Get the localized properties map.
	 *
	 * @return the map
	 */
	@OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL },
			targetEntity = SkuOptionValueLocalizedPropertyValueImpl.class)
	@MapKey(name = "localizedPropertyKey")
	@ElementJoinColumns({
		@ElementJoinColumn(name = "OBJECT_UID", referencedColumnName = "UIDPK", nullable = false),
		@ElementJoinColumn(name = "TLOCALIZEDPROPERTIES.TYPE", referencedColumnName = "'SkuOptionValue'")
	})
	@ElementDependent
	public Map<String, LocalizedPropertyValue> getLocalizedPropertiesMap() {
		return localizedPropertiesMap;
	}

	/**
	 * Set the localized properties map.
	 *
	 * @param localizedPropertiesMap the map
	 */
	protected void setLocalizedPropertiesMap(final Map<String, LocalizedPropertyValue> localizedPropertiesMap) {
		this.localizedPropertiesMap = localizedPropertiesMap;
	}

	/**
	 * Gets the display name in the given locale, falling back
	 * to the default locale if required.
	 *
	 * This implementation calls getDisplayName(locale, boolean).
	 *
	 * @param locale the locale
	 * @return the display name
	 * @deprecated use getDisplayName(Locale, boolean) instead
	 */
	@Deprecated
	public String getDisplayName(final Locale locale) {
		return this.getDisplayName(locale, true);
	}

	/**
	 * Get the localized display name in the given locale,
	 * falling back to the display name for the locale in the associated Master
	 * Catalog's default locale if requested.
	 *
	 * @param locale the locale in which to return the display name
	 * @param fallback if true, will fallback to the display name in the associated Master
	 * Catalog's default locale if required.
	 * @return the display name, or null if not found.
	 */
	public String getDisplayName(final Locale locale, final boolean fallback) {
		String displayName = getLocalizedProperties().getValue(LOCALIZED_PROPERTY_DISPLAY_NAME, locale);
		if (displayName == null && fallback) {
			final Locale masterCatalogLocale = getMasterCatalogLocale();
			displayName = getLocalizedProperties().getValue(LOCALIZED_PROPERTY_DISPLAY_NAME, masterCatalogLocale);
		}
		return displayName;
	}

	/**
	 * Returns the locale for the containing ProductSku's Master Catalogue.
	 * @return the locale for the containing ProductSku's Master Catalogue
	 */
	@Transient
	protected Locale getMasterCatalogLocale() {
		return getSkuOptionInternal().getCatalog().getDefaultLocale();
	}

	@Override
	public void setDisplayName(final Locale locale, final String displayName) {
		getLocalizedProperties().setValue(LOCALIZED_PROPERTY_DISPLAY_NAME, locale, displayName);
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
		return uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	@SuppressWarnings({ "PMD.UselessOverridingMethod" })
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!super.equals(obj)) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		return true;
	}

}
