package com.elasticpath.domain.dataimport.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.exception.EpUnsupportedOperationException;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.persistence.api.Entity;

/**
 * Represents a template <code>ImportDataType</code>.
 */
public abstract class AbstractImportDataTypeImpl extends AbstractEpDomainImpl implements ImportDataType {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private final Map<String, ImportField> importFields = new LinkedHashMap<String, ImportField>();

	private final List<ImportField> importFieldList = new ArrayList<ImportField>();

	private List<ImportField> requiredImportFieldList;

	private List<ImportField> optionalImportFieldList;

	private String name;

	private Collection<Locale> locales;

	private Collection<Currency> currencies;

	private Currency requiredCurrency;

	private Locale requiredLocale;

	private static final String SEPARATOR = " - ";

	/**
	 * The default constructor.
	 */
	public AbstractImportDataTypeImpl() {
		super();
	}

	/**
	 * Do a sanity check.
	 *
	 * @throws EpDomainException in case the sanity check fails.
	 */
	protected abstract void sanityCheck() throws EpDomainException;

	/**
	 * Check the type of the given persistence object.
	 *
	 * @param object the persistence object
	 * @throws EpBindException -- in case the type doesn't match
	 */
	protected abstract void typeCheck(final Object object) throws EpBindException;

	/**
	 * Return all import fields as a <code>Map</code> The name of an import field will be used as key, and the import field itself will be put as a
	 * value.
	 *
	 * @return all import fields as a <code>Map</code>
	 */
	public Map<String, ImportField> getImportFields() {
		return importFields;
	}

	/**
	 * Return all required import fields.
	 *
	 * @return all required import fields.
	 */
	public List<ImportField> getRequiredImportFields() {
		sanityCheck();
		if (requiredImportFieldList == null) {
			populateRequiredAndOptionalImportFields();
		}

		return requiredImportFieldList;
	}

	/**
	 * Return all optional import fields.
	 *
	 * @return all optional import fields.
	 */
	public List<ImportField> getOptionalImportFields() {
		sanityCheck();
		if (optionalImportFieldList == null) {
			populateRequiredAndOptionalImportFields();
		}
		return optionalImportFieldList;
	}

	/**
	 * Return an import field with the given name.
	 *
	 * @param importFieldName the import field name.
	 * @return an import field with the given name
	 */
	public ImportField getImportField(final String importFieldName) {
		sanityCheck();
		ImportField returnValue = importFields.get(importFieldName);
		if (returnValue == null) {
			throw new EpDomainException("ImportField for " + importFieldName + " doesn't exist.");
		}
		return returnValue;
	}

	private void populateRequiredAndOptionalImportFields() {
		requiredImportFieldList = new ArrayList<ImportField>();
		optionalImportFieldList = new ArrayList<ImportField>();
		for (final ImportField importField : importFieldList) {
			if (importField.isRequired()) {
				requiredImportFieldList.add(importField);
			} else {
				optionalImportFieldList.add(importField);
			}
		}
	}

	/**
	 * Returns all import fields as a <code>list</code>.
	 *
	 * @return all import fields as a <code>list</code>.
	 */
	protected List<ImportField> getImportFieldList() {
		return importFieldList;
	}

	/**
	 * Clear all added import fields.
	 */
	protected void clearAllImportFields() {
		importFieldList.clear();
		importFields.clear();
	}

	/**
	 * Adds the given <code>ImportField</code>.
	 *
	 * @param name the name of the <code>ImportField</code>
	 * @param field the <code>ImportField</code>
	 */
	protected void addImportField(final String name, final ImportField field) {
		importFieldList.add(field);
		importFields.put(name, field);
	}

	/**
	 * A dummy implementation.
	 *
	 * @param entity -- not used.
	 */
	public void clearValueObjects(final Entity entity) {
		throw new EpUnsupportedOperationException("Should never reach here.");
	}

	private void readObject(final java.io.ObjectInputStream aInputStream) throws IOException, ClassNotFoundException { //NOPMD
	     //always perform the default de-serialization first
	     aInputStream.defaultReadObject();
	}

	/**
	 * Returns import data type name.
	 *
	 * @return import data type name.
	 */
	public String getName() {
		if (name == null) {
			if (getTypeName() == null) {
				name = getPrefixOfName();
			} else {
				name = getPrefixOfName() + SEPARATOR + getTypeName();
			}
		}
		return name;
	}

	/**
	 * Returns import data type name part which is usually after separator.
	 *
	 * @return null.
	 */
	public String getTypeName() {
		return null;
	}

	/**
	 * Gets PREFIX_OF_IMPORT_DATA_TYPE_NAME constant.
	 *
	 * @return PREFIX_OF_IMPORT_DATA_TYPE_NAME
	 */
	public abstract String getPrefixOfName();

	@Override
	public void setSupportedLocales(final Collection<Locale> locales) {
		this.locales = locales;
	}

	@Override
	public Collection<Locale> getSupportedLocales() {
		if (locales == null) {
			locales = new HashSet<Locale>();
			locales.add(Locale.getDefault());
		}
		return locales;
	}

	@Override
	public void setSupportedCurrencies(final Collection<Currency> currencies) {
		this.currencies = currencies;
	}

	@Override
	public Collection<Currency> getSupportedCurrencies() {
		if (currencies == null) {
			currencies = new HashSet<Currency>();
			currencies.add(Currency.getInstance(Locale.getDefault()));
		}
		return currencies;
	}

	/**
	 * Sets the currency that is required by this data type;
	 * typically this is the store's or the catalog's default currency.
	 * @param currency the required currency
	 */
	public void setRequiredCurrency(final Currency currency) {
		requiredCurrency = currency;
	}

	/**
	 * Gets the currency that is required by this data type;
	 * typically this is the store's or the catalog's default currency.
	 * @return the currency required by this data type
	 */
	public Currency getRequiredCurrency() {
		return requiredCurrency;
	}

	/**
	 * Sets the locale that is required by this data type;
	 * typically this is the store's or the catalog's default locale.
	 * @param locale the required locale
	 */
	public void setRequiredLocale(final Locale locale) {
		requiredLocale = locale;
	}

	/**
	 * Gets the local that is required by this data type;
	 * typically this is the store's or the catalog's default locale.
	 * @return the locale that is required by this data type
	 */
	public Locale getRequiredLocale() {
		return requiredLocale;
	}

	@Override
	public List<ImportType> getSupportedImportTypes() {
		return AbstractImportTypeImpl.getAllImportTypes();
	}
}
