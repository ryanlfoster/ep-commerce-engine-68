/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

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

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumns;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.BrandLocalizedPropertyValueImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * The default implementation of <code>Brand</code>.
 */
@Entity
@Table(name = BrandImpl.TABLE_NAME)
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = {
			@FetchAttribute(name = "code") }),
	@FetchGroup(name = FetchGroupConstants.PRODUCT_SKU_INDEX, attributes = {
			@FetchAttribute(name = "code"),
			@FetchAttribute(name = "catalog"),
			@FetchAttribute(name = "localizedPropertiesMap") })
})
public class BrandImpl extends AbstractLegacyEntityImpl implements Brand {

	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TBRAND";

	private String imageUrl;

	private LocalizedProperties localizedProperties;

	private String code;

	private Map<String, LocalizedPropertyValue> localizedPropertiesMap = new HashMap<String, LocalizedPropertyValue>();

	private Catalog catalog;

	private long uidPk;

	@Override
	@Basic
	@Column(name = "IMAGE_URL")
	public String getImageUrl() {
		return imageUrl;
	}

	@Override
	public void setImageUrl(final String imageUrl) {
		this.imageUrl = imageUrl;
	}

	@Override
	@Transient
	public LocalizedProperties getLocalizedProperties() {
		if (localizedProperties == null) {
			localizedProperties = getBean(ContextIdNames.LOCALIZED_PROPERTIES);
			localizedProperties.setLocalizedPropertiesMap(getLocalizedPropertiesMap(), ContextIdNames.BRAND_LOCALIZED_PROPERTY_VALUE);
		}
		return localizedProperties;
	}

	@Override
	public void setLocalizedProperties(final LocalizedProperties localizedProperties) {
		this.localizedProperties = localizedProperties;
		if (localizedProperties != null) {
			setLocalizedPropertiesMap(localizedProperties.getLocalizedPropertiesMap());
		}
	}

	@Override
	@Basic
	@Column(name = "CODE")
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(final String code) {
		this.code = code;
	}

	@Override
	@Transient
	public String getGuid() {
		return getCode();
	}

	@Override
	public void setGuid(final String guid) {
		setCode(guid);
	}

	@Override
	@Transient
	public String getDisplayName(final Locale locale) {
		return getDisplayName(locale, true);
	}

	@Override
	@Transient
	public String getDisplayName(final Locale locale, final boolean fallback) {
		String displayName = getLocalizedProperties().getValueWithoutFallBack(LOCALIZED_PROPERTY_DISPLAY_NAME, locale);
		if ((displayName == null || displayName.length() == 0) && fallback) {
			displayName = getLocalizedProperties().getValue(LOCALIZED_PROPERTY_DISPLAY_NAME, getCatalogDefaultLocale());
		}
		return displayName;
	}

	/**
	 * Gets the containing Catalog's default locale.
	 *
	 * @return the containing catalog's default locale
	 */
	@Transient
	protected Locale getCatalogDefaultLocale() {
		return getCatalog().getDefaultLocale();
	}

	@Override
	@OneToMany(targetEntity = BrandLocalizedPropertyValueImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@MapKey(name = "localizedPropertyKey")
	@ElementJoinColumns({
			@ElementJoinColumn(name = "OBJECT_UID", referencedColumnName = "UIDPK", nullable = false),
			@ElementJoinColumn(name = "TLOCALIZEDPROPERTIES.TYPE", referencedColumnName = "'Brand'") })
	@ElementDependent
	public Map<String, LocalizedPropertyValue> getLocalizedPropertiesMap() {
		return localizedPropertiesMap;
	}

	@Override
	public void setLocalizedPropertiesMap(final Map<String, LocalizedPropertyValue> localizedPropertiesMap) {
		this.localizedPropertiesMap = localizedPropertiesMap;
	}

	@Override
	@ManyToOne(optional = false, targetEntity = CatalogImpl.class)
	@JoinColumn(name = "CATALOG_UID", nullable = false)
	public Catalog getCatalog() {
		return catalog;
	}

	@Override
	public void setCatalog(final Catalog catalog) {
		this.catalog = catalog;
	}

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}

		if (!(other instanceof BrandImpl)) {
			return false;
		}

		BrandImpl brand = (BrandImpl) other;
		return ObjectUtils.equals(code, brand.code);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(code);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("code", getCode())
				.append("imageUrl", getImageUrl())
				.append("localizedProperties", getLocalizedProperties())
				.toString();
	}
}
