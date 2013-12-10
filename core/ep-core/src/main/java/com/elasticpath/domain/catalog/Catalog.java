/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalog;

import java.util.Collection;
import java.util.Locale;

import com.elasticpath.persistence.api.Entity;

/**
 * A catalog is a collection of categories and products.
 * 
 * The system shall have a concept of "master" and "virtual" catalogs.
 * Any number of master & virtual catalogs can exist, however a virtual catalog can only consume products from a master catalog.
 * 
 * Master catalogs are independent catalogs.
 * 
 * Each virtual catalog shall have a unique name (cannot have the same name as a master catalog).
 * <ul>
 *   <li>A virtual catalog can inherit categories and/or products from any other master catalogs.</li>
 *   <li>Virtual catalogs CANNOT inherit from other virtual catalogs.</li>
 *   <li>A virtual catalog can have it's own categories but cannot have it's own products, all products must be inherited from master catalogs.</li>
 *   <li>A virtual catalog can consume categories and products from master catalogs.</li>
 *   <li>A virtual catalog inherits all the currencies and locales from all the master catalogs.</li>
 * </ul>
 */
public interface Catalog extends Entity {

	/**
	 * Get the Master/Virtual Catalog indicator.
	 * 
	 * @return true for a master catalog
	 */
	boolean isMaster();

	/**
	 * Set the Master/Virtual Catalog indicator.
	 * 
	 * @param master
	 *            the master to set
	 */
	void setMaster(final boolean master);

	/**
	 * Get the name of the catalog.
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * Set the name of the catalog.
	 * 
	 * @param name
	 *            the name to set
	 */
	void setName(final String name);

	/**
	 * Get the unmodifiable collection of locales that are supported by a master catalog 
	 * or all the all the currencies assigned to all master catalogs in the system for a virtual catalog. 
	 * 
	 * @return the locales that are supported by this catalog
	 */
	Collection<Locale> getSupportedLocales();

	/**
	 * Set the collection of locales that are supported by this catalog.
	 * 
	 * <p><i>Note: In case of a virtual catalog nothing will be set as virtual catalogs inherit 
	 * all the supported locales from the master catalogs.</i>
	 * 
	 * @param supportedLocales the supportedLocales to set
	 * @throws DefaultValueRemovalForbiddenException if the new locales do not contain the default locale,
	 * or if a Store that is using this Catalog has a default Locale that is missing from the given collection 
	 * @throws UnsupportedOperationException in case the catalog is not a master catalog 
	 */
	void setSupportedLocales(final Collection<Locale> supportedLocales) 
		throws DefaultValueRemovalForbiddenException, UnsupportedOperationException;
	
	/**
	 * Adds a supported locale to this catalog. Locales can only be removed by using
	 * setSupportedLocales which checks that the default is not being removed.
	 *
	 * <p><i>Note: In case of a virtual catalog nothing will be added as virtual catalogs inherit 
	 * all the supported locales from the master catalogs.</i>
	 * 
	 * @param locale the supported locale to add.
	 * @throws UnsupportedOperationException in case the catalog is not a master catalog (virtual)
	 */
	void addSupportedLocale(final Locale locale) throws UnsupportedOperationException;
	
	/**
	 * Gets the default locale for this <code>Catalog</code>.
	 * 
	 * @return the default locale for this <code>Catalog</code>
	 */
	Locale getDefaultLocale();

	/**
	 * Sets the default locale for this <code>Catalog</code>.
	 * Adds it to the collection of supported Locales if necessary.
	 * 
	 * @param defaultLocale the default locale for this <code>Catalog</code>
	 */
	void setDefaultLocale(final Locale defaultLocale);
	
	/**
	 * Gets the unique code associated with the {@link Catalog}.
	 * 
	 * @return the unique code associated with the {@link Catalog}
	 */
	String getCode();

	/**
	 * Sets the unique code associated with the {@link Catalog}.
	 * 
	 * @param code the unique code associated with the {@link Catalog}
	 */
	void setCode(final String code);
}