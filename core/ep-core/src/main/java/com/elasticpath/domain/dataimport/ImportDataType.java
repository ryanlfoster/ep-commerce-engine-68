package com.elasticpath.domain.dataimport;

import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.Persistable;

/**
 * Represents an import data type.
 */
public interface ImportDataType extends EpDomain {
	/**
	 * Separator used in import data type name.
	 * @deprecated This is an implementation detail
	 */
	@Deprecated
	String SEPARATOR = " - ";

	/**
	 * Returns import data type name.
	 *
	 * @return import data type name.
	 */
	String getName();

	/**
	 * Returns import data type name part which is usually after separator.
	 *
	 * @return import data type name part which is usually after separator.
	 */
	String getTypeName();

	/**
	 * Returns import data type name message key. This is a key for a message before separator.
	 *
	 * @return message key.
	 */
	String getNameMessageKey();

	/**
	 * Return all import fields as a <code>Map</code> The name of an import field will be used as key, and the import field itself will be put as a
	 * value.
	 *
	 * @return all import fields as a <code>Map</code>
	 */
	Map<String, ImportField> getImportFields();

	/**
	 * Return all required import fields.
	 *
	 * @return all required import fields.
	 */
	List<ImportField> getRequiredImportFields();

	/**
	 * Return all optional import fields.
	 *
	 * @return all optional import fields.
	 */
	List<ImportField> getOptionalImportFields();

	/**
	 * Return an import field with the given name.
	 *
	 * @param importFieldName the import field name.
	 * @return an import field with the given name
	 */
	ImportField getImportField(String importFieldName);

	/**
	 * Initialize the import data type based on the given base object.
	 *
	 * @param baseObject the base object used to initialize the import data type.
	 */
	void init(Object baseObject);

	/**
	 * Return the field name for guid. If it doesn't exist, return <code>null</code>.
	 *
	 * @return the field name for guid
	 */
	String getGuidFieldName();

	/**
	 * Returns the meta object used to intialize the import data type.
	 *
	 * @return the meta object used to intialize the import data type
	 */
	Object getMetaObject();

	/**
	 * Returns the import job runner bean name.
	 *
	 * @return the import job runner bean name.
	 */
	String getImportJobRunnerBeanName();


	/**
	 * Returns <code>true</code> if this import data type imports entity.
	 *
	 * @return <code>true</code> if this import data type imports entity
	 */
	boolean isEntityImport();

	/**
	 * Returns <code>true</code> if this import data type imports value object.
	 *
	 * @return <code>true</code> if this import data type imports value object
	 */
	boolean isValueObjectImport();

	/**
	 * Add or update the given value object to the given entity.
	 *
	 * @param entity the entity
	 * @param object the value object
	 */
	void saveOrUpdate(Entity entity, Persistable object);

	/**
	 * Create and return a new value object.
	 *
	 * @return a new value object
	 */
	Persistable createValueObject();

	/**
	 * Clear the value objects of the given entity.
	 * @param entity the entity
	 */
	void clearValueObjects(Entity entity);

	/**
	 * Delete entity.
	 * @param entity for delete.
	 */
	void deleteEntity(Entity entity);

	/**
	 * Set the unmodifiable collection of locales that are supported by this data type.
	 *
	 * @param locales supported locales
	 */
	void setSupportedLocales(Collection<Locale> locales);

	/**
	 * Get the unmodifiable collection of locales that are supported by this data type.
	 *
	 * @return collection of locales
	 */
	Collection<Locale> getSupportedLocales();

	/**
	 * Set the unmodifiable collection of currencies that are supported by this data type.
	 *
	 * @param currencies supported currencies
	 */
	void setSupportedCurrencies(Collection<Currency> currencies);

	/**
	 * Get the unmodifiable collection of currencies that are supported by this data type.
	 *
	 * @return collection of currencies
	 */
	Collection<Currency> getSupportedCurrencies();

	/**
	 * Sets the currency that is required by this data type;
	 * typically this is the store's or the catalog's default currency.
	 * @param currency the required currency
	 */
	void setRequiredCurrency(Currency currency);

	/**
	 * Gets the currency that is required by this data type;
	 * typically this is the store's or the catalog's default currency.
	 * @return the currency required by this data type
	 */
	Currency getRequiredCurrency();

	/**
	 * Sets the locale that is required by this data type;
	 * typically this is the store's or the catalog's default locale.
	 * @param locale the required locale
	 */
	void setRequiredLocale(Locale locale);

	/**
	 * Gets the local that is required by this data type;
	 * typically this is the store's or the catalog's default locale.
	 * @return the locale that is required by this data type
	 */
	Locale getRequiredLocale();

	/**
	 * Get the supported import types.
	 *
	 * @return the list of import type
	 */
	List<ImportType> getSupportedImportTypes();
}
