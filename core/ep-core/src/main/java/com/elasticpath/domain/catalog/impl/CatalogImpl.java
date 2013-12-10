/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalog.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.Persistent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.DefaultValueRemovalForbiddenException;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.misc.SupportedLocale;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.catalog.CatalogService;

/**
 * Implementation of Catalog.java that takes into account special persistence-layer restrictions. Since we cannot specify or
 * determine the order in which JPA will load fields from the database it is necessary to specify internal protected methods for
 * JPA to access instance variables without being hampered by consistency logic that may access fields that are not yet loaded.
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals" })
@Entity
@Table(name = CatalogImpl.TABLE_NAME)
@FetchGroups({
	@FetchGroup(name = FetchGroupConstants.PRODUCT_INDEX, attributes = { @FetchAttribute(name = "master"),
				@FetchAttribute(name = "supportedLocalesInternal"), @FetchAttribute(name = "defaultLocaleInternal")
				}),
		@FetchGroup(name = FetchGroupConstants.CATEGORY_INDEX, 
				attributes = { @FetchAttribute(name = "supportedLocalesInternal"), @FetchAttribute(name = "defaultLocaleInternal"),
				@FetchAttribute(name = "code"), @FetchAttribute(name = "master") }),
		@FetchGroup(name = FetchGroupConstants.CATALOG_DEFAULTS, attributes = { @FetchAttribute(name = "name"),
				@FetchAttribute(name = "defaultLocaleInternal"), @FetchAttribute(name = "code") }),
		@FetchGroup(name = FetchGroupConstants.PROMOTION_INDEX, attributes = { @FetchAttribute(name = "code") }),
		@FetchGroup(name = FetchGroupConstants.CATALOG, attributes = { @FetchAttribute(name = "code") }),
		@FetchGroup(name = FetchGroupConstants.CATEGORY_BASIC, attributes = { @FetchAttribute(name = "code") })
})
public class CatalogImpl extends AbstractLegacyEntityImpl implements Catalog {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TCATALOG";
	
	
	/** Maximum string length for a Catalog code. */
	private static final int CATALOG_CODE_LENGTH = 64;

	private boolean master;

	private String name;
	
	private Set<SupportedLocale> supportedCatalogLocales = new HashSet<SupportedLocale>();
	
	private Locale defaultLocale;
	
	private long uidPk;
	
	private String code;
	
	/** Defines 20 as a constant to use for maximum string lengths. */
	private static final int LENGTH_20 = 20;

	/**
	 * Get the Master/Virtual Catalog indicator.
	 * 
	 * @return true for a master catalog
	 */
	@Basic
	@Column(name = "MASTER", nullable = false)
	public boolean isMaster() {
		return master;
	}

	/**
	 * Set the Master/Virtual Catalog indicator.
	 * 
	 * @param master the master to set
	 */
	public void setMaster(final boolean master) {
		this.master = master;
	}

	/**
	 * Get the name of the catalog.
	 * 
	 * @return the name
	 */
	@Basic
	@Column(name = "NAME", unique = true, nullable = false)
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the catalog.
	 * 
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Get the set of supported <code>Cataloglocale</code>s. Necessary because the persistence layer cannot persist
	 * java.util.Locale objects directly.
	 * 
	 * @return the supported CatalogLocales
	 */
	@OneToMany(targetEntity = CatalogLocaleImpl.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@ElementJoinColumn(name = "CATALOG_UID", nullable = false)
	@ElementForeignKey(name = "TCATALOGSUPPORTEDLOCALE_IBFK_1")
	@ElementDependent
	protected Set<SupportedLocale> getSupportedLocalesInternal() {
		return supportedCatalogLocales;
	}

	/**
	 * Set the supported locales in the form of CatalogLocale objects, which can be persisted.
	 * 
	 * @param supportedLocales the supportedLocales to set
	 */
	protected void setSupportedLocalesInternal(final Set<SupportedLocale> supportedLocales) {
		this.supportedCatalogLocales = supportedLocales;
	}
	
	/**
	 * Gets the unmodifiable collection of this catalog's supported locales.
	 * 
	 * @return unmodifiable collection of this catalog's supported locales, which is empty if no locales are supported
	 */
	@Transient
	public Set<Locale> getSupportedLocales() {
		Set<Locale> supportedLocales = new HashSet<Locale>();
		if (isMaster()) {
			for (SupportedLocale cLocale : getSupportedLocalesInternal()) {
				supportedLocales.add(cLocale.getLocale());
			}
		} else {
			supportedLocales.addAll(getAllMasterCatalogLocales());
		}
		return Collections.unmodifiableSet(supportedLocales);
	}
	
	/**
	 * Gets all the catalog locales.
	 * 
	 * @return a collection of {@link Locale} object
	 */
	@Transient
	protected Collection<Locale> getAllMasterCatalogLocales() {
		CatalogService catalogService = getBean(ContextIdNames.CATALOG_SERVICE);
		return catalogService.findAllCatalogLocales();
	}

	/**
	 * Set the collection of locales that are supported by this catalog.
	 * 
	 * @param supportedLocales the supportedLocales to set
	 * @throws DefaultValueRemovalForbiddenException if the new locales do not contain the default locale, or if a Store that is
	 *             using this Catalog has a default Locale that is missing from the given collection
	 */
	public void setSupportedLocales(final Collection<Locale> supportedLocales) throws DefaultValueRemovalForbiddenException {
		if (!isMaster()) {
			throw new UnsupportedOperationException("Cannot set supported locales to a virtual catalog");
		}
		if ((supportedLocales.contains(this.getDefaultLocale()) || this.getDefaultLocale() == null)) {
			Set<SupportedLocale> supportedCatalogLocales = new HashSet<SupportedLocale>();
			for (Locale locale : supportedLocales) {
				SupportedLocale cLocale = getBean(ContextIdNames.CATALOG_LOCALE);
				cLocale.setLocale(locale);
				supportedCatalogLocales.add(cLocale);
			}
			setSupportedLocalesInternal(supportedCatalogLocales);
		} else {
			throw new DefaultValueRemovalForbiddenException(
					"Cannot remove default: " + this.getDefaultLocale() + " from collection of supported locales");
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
	 * Gets the default locale for this <code>Catalog</code>, for JPA's use only.
	 * 
	 * @return the default locale for this <code>Catalog</code>
	 */
	@Persistent(optional = false)
	@Externalizer("toString")
	@Factory("org.apache.commons.lang.LocaleUtils.toLocale")
	@Column(name = "DEFAULT_LOCALE", length = LENGTH_20)
	protected Locale getDefaultLocaleInternal() {
		return defaultLocale;
	}
	
	/**
	 * Gets the default locale for this <code>Catalog</code>.
	 * 
	 * @return the default locale for this <code>Catalog</code>
	 */
	@Transient
	public Locale getDefaultLocale() {
		return getDefaultLocaleInternal();
	}
	
	/**
	 * Sets the default locale for this <code>Catalog</code>, for JPA's use only.
	 * 
	 * @param defaultLocale the default locale for this <code>Catalog</code>
	 */
	protected void setDefaultLocaleInternal(final Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	/**
	 * Sets the default locale for this <code>Catalog</code>. Adds it to the collection of supported Locales if necessary.
	 * 
	 * @param defaultLocale the default locale for this <code>Catalog</code>
	 */
	public void setDefaultLocale(final Locale defaultLocale) {
		if (defaultLocale == null) {
			throw new IllegalArgumentException("Default Locale cannot be set to null");
		}
		this.setDefaultLocaleInternal(defaultLocale);
		if (isMaster()) {
			addSupportedLocale(defaultLocale);
		}
	}
	
	/**
	 * Adds the given Locale to the collection of supported Locales.
	 * 
	 * @param locale the locale to add
	 */
	public void addSupportedLocale(final Locale locale) {
		if (!isMaster()) {
			throw new UnsupportedOperationException("Cannot add a supported locale to a virtual catalog");
		}
		if (this.getSupportedLocales().contains(locale)) {
			return;
		}
		SupportedLocale cLocale = getBean(ContextIdNames.CATALOG_LOCALE);
		cLocale.setLocale(locale);
		this.getSupportedLocalesInternal().add(cLocale);
	}
	
	/**
	 * Gets the unique code associated with the {@link Catalog}.
	 * 
	 * @return the unique code associated with the {@link Catalog}
	 */
	@Basic(optional = false)
	@Column(name = "CATALOG_CODE", length = CATALOG_CODE_LENGTH, unique = true)
	public String getCode() {
		return code;
	}

	/**
	 * Sets the unique code associated with the {@link Catalog}.
	 * 
	 * @param code the unique code associated with the {@link Catalog}
	 */
	public void setCode(final String code) {
		this.code = code;
	}
	
	/**
	 * Return the GUID. This implementation calls getCode().
	 * @return the catalog code.
	 */
	@Transient
	@Override
	public String getGuid() {
		return this.getCode();
	}

	/**
	 * Sets the Guid. This implementation calls setCode().
	 *
	 * @param guid the catalog code to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.setCode(guid);
	}
	
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof CatalogImpl)) {
			return false;
		}
		
		CatalogImpl catalog = (CatalogImpl) other;
		return ObjectUtils.equals(code, catalog.code);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(code);
	}
}
