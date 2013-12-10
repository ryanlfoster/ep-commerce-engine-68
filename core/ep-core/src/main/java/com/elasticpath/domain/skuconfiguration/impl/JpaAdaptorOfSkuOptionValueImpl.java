/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.skuconfiguration.impl;

import java.util.Locale;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * Wraps a SkuOptionValue. This class is meant to represent the database
 * table TPRODUCTSKUOPTIONVALUE, as a mapping class between a ProductSku
 * and a SkuOptionValue. Since a ProductSku is partially defined by a collection
 * of SkuOption keys (e.g. Color) and SkuOptionValues (e.g. Red, Blue), this
 * class is a convenience class to help associate a ProductSku with its SkuOptionValues
 * via SkuOption keys.
 * TODO: Eliminate the JpaAdaptorOfSkuOptionValueImpl class and the TPRODUCTSKUOPTIONVALUE
 * database table, linking a ProductSku and its SkuOptionValues in a more comprehensible
 * manner. [MSC-7150]
 */
@Entity
@Table(name = JpaAdaptorOfSkuOptionValueImpl.TABLE_NAME)
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = {
		@FetchAttribute(name = "optionKey"),
		@FetchAttribute(name = "skuOptionValue")
		})
})
public class JpaAdaptorOfSkuOptionValueImpl extends AbstractEntityImpl implements SkuOptionValue {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TPRODUCTSKUOPTIONVALUE";

	private SkuOptionValue skuOptionValue;

	private String optionKey;

	private long uidPk;

	/**
	 * Get the sku option value.
	 *
	 * @return the sku option value
	 */
	@ManyToOne(targetEntity = SkuOptionValueImpl.class)
	@JoinColumn (name = "OPTION_VALUE_UID", nullable = false)
	@ForeignKey
	public SkuOptionValue getSkuOptionValue() {
		return skuOptionValue;
	}

	/**
	 * Set the sku option value.
	 *
	 * @param skuOptionValue the value
	 */
	public void setSkuOptionValue(final SkuOptionValue skuOptionValue) {
		this.skuOptionValue = skuOptionValue;
	}

	/**
	 * Get the option key.
	 *
	 * @return the option key
	 */
	@Column(name = "OPTION_KEY")
	public String getOptionKey() {
		return optionKey;
	}

	/**
	 * Set the option key.
	 *
	 * @param optionKey the key
	 */
	public void setOptionKey(final String optionKey) {
		this.optionKey = optionKey;
	}

	/**
	 * Get the image.
	 *
	 * @return the image
	 */
	@Transient
	public String getImage() {
		return getSkuOptionValue().getImage();
	}

	/**
	 * Get the localized properties.
	 *
	 * @return the localized properties object
	 */
	@Transient
	public LocalizedProperties getLocalizedProperties() {
		return getSkuOptionValue().getLocalizedProperties();
	}

	/**
	 * Set the localized properties.
	 *
	 * @param properties the localized properties object
	 */
	public void setLocalizedProperties(final LocalizedProperties properties) {
		if (getSkuOptionValue() != null) {
			getSkuOptionValue().setLocalizedProperties(properties);
		}
	}

	/**
	 * Get the localized properties.
	 *
	 * @return the localized properties object
	 */
	@Transient
	public Map<String, LocalizedPropertyValue> getLocalizedPropertiesMap() {
		return getSkuOptionValue().getLocalizedPropertiesMap();
	}

	/**
	 * Get the option value key.
	 *
	 * @return the key
	 */
	@Transient
	public String getOptionValueKey() {
		SkuOptionValue skuOptionValue = getSkuOptionValue();
		if (skuOptionValue == null) {
			return null;
		}
		return skuOptionValue.getOptionValueKey();
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
	 * Get the ordering.
	 *
	 * @return the ordering
	 */
	@Transient
	public int getOrdering() {
		return getSkuOptionValue().getOrdering();
	}

	/**
	 * Set the image.
	 *
	 * @param image the image
	 */
	public void setImage(final String image) {
		if (getSkuOptionValue() != null) {
			getSkuOptionValue().setImage(image);
		}
	}

	/**
	 * Set the option value key.
	 *
	 * @param optionValueKey the key
	 */
	public void setOptionValueKey(final String optionValueKey) {
		if (getSkuOptionValue() != null) {
			getSkuOptionValue().setOptionValueKey(optionValueKey);
		}
	}

	/**
	 * Set the ordering.
	 *
	 * @param order the order
	 */
	public void setOrdering(final int order) {
		if (getSkuOptionValue() != null) {
			getSkuOptionValue().setOrdering(order);
		}
	}

	/**
	 * Gets a string representation of the <code>SkuOptionValue</code>.
	 *
	 * @return the string representation
	 */
	@Override
	public String toString() {
		return getSkuOptionValue().getOptionValueKey();
	}

	/**
	 * Set default values for the object.
	 *
	 */
	@Override
	public void initialize() {
		if (getSkuOptionValue() != null) {
			getSkuOptionValue().initialize();
		}
	}

	/**
	 * Gets the display name.
	 *
	 * @param locale the locale
	 * @return display name
	 */
	@Transient
	public String getDisplayName(final Locale locale) {
		if (getSkuOptionValue() != null) {
			return getSkuOptionValue().getDisplayName(locale);
		}
		return null;
	}

	@Override
	public void setDisplayName(final Locale locale, final String displayName) {
		if (getSkuOptionValue() != null) {
			getSkuOptionValue().setDisplayName(locale, displayName);
		}
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

	/**
	 * Get the localized display name of the underlying SkuOptionValue in the given locale,
	 * falling back to the display name for the locale in the associated SkuOption's Master
	 * Catalog's default locale if requested.
	 *
	 * @param locale the locale in which to return the display name
	 * @param fallback if true, will fallback to the display name in the associated
	 * SkuOption's Master Catalog's default locale if required.
	 * @return the display name, or null if not found.
	 */
	@Transient
	public String getDisplayName(final Locale locale, final boolean fallback) {
		if (getSkuOptionValue() != null) {
			return getSkuOptionValue().getDisplayName(locale, fallback);
		}
		return null;
	}

	/**
	 * Get the sku option.
	 * @return the SkuOption
	 */
	@Transient
	public SkuOption getSkuOption() {
		return getSkuOptionValue().getSkuOption();
	}

	/**
	 * Set the sku option.
	 * @param skuOption the SkuOption
	 */
	public void setSkuOption(final SkuOption skuOption) {
		getSkuOptionValue().setSkuOption(skuOption);
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof JpaAdaptorOfSkuOptionValueImpl)) {
			return false;
		}
		final JpaAdaptorOfSkuOptionValueImpl other = (JpaAdaptorOfSkuOptionValueImpl) obj;

		if (ObjectUtils.equals(skuOptionValue, other.getSkuOptionValue())
				&& StringUtils.equals(optionKey, other.getOptionKey())) {
			return true;
		}

		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ObjectUtils.hashCode(skuOptionValue);
		result = prime * result + ObjectUtils.hashCode(optionKey);

		return result;
	}

}
