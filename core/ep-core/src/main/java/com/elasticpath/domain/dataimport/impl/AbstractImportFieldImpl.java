/*
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.domain.dataimport.impl;

import java.math.BigDecimal;
import java.util.Locale;

import com.elasticpath.commons.constants.ImportConstants;
import com.elasticpath.commons.exception.EpNonNullBindException;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.service.dataimport.ImportGuidHelper;

/**
 * Represents a template <code>ImportField</code>.
 */
public abstract class AbstractImportFieldImpl extends AbstractEpDomainImpl implements ImportField {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private final String name;

	private final String type;

	private boolean required;

	/**
	 * A primaryRequired field is a field that is required for
	 * delete or update imports, whereas a required field is
	 * required for creation as well.
	 */
	private final boolean primaryRequired;

	/**
	 * The default constructor.
	 *
	 * @param name the name of the import field
	 * @param type the type of the import field
	 * @param required set it to <code>true</code> if the import field is required
	 * @param primaryRequired set it to <code>true</code> if the import field is a required primary field
	 */
	public AbstractImportFieldImpl(final String name, final String type, final boolean required, final boolean primaryRequired) {
		this.name = name;
		this.type = type;
		this.required = required;
		this.primaryRequired = primaryRequired;
	}

	/**
	 * Return the import field name.
	 *
	 * @return the import field name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the import field type.
	 *
	 * @return the import field type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Return <code>true</code> if it is a required field.
	 *
	 * @return <code>true</code> if it is required
	 */
	public boolean isRequired() {
		return required;
	}

	@Override
	public void setRequired(final boolean required) {
		this.required = required;
	}

	/**
	 * Return <code>true</code> if it is a required primary field.
	 *
	 * @return <code>true</code> if it is required
	 */
	public boolean isRequiredPrimaryField() {
		return primaryRequired;
	}

	/**
	 * Check the given string value is null or not.
	 *
	 * @param value the string value to check
	 * @return <code>true</code> if it is <code>null</code> or "null", otherwise <code>false</code>
	 */
	protected boolean checkNullValue(final String value) {

		if (value == null) {
			return true;
		}

		final String trimedValue = value.trim().toLowerCase(Locale.ENGLISH);
		for (int i = 0; i < ImportConstants.IMPORT_NULL_VALUES.length; i++) {
			if (trimedValue.equals(ImportConstants.IMPORT_NULL_VALUES[i])) {
				return true;
			}
		}

		return false;
	}

	/**
	 * A dummy implementation of the method for most import fields.
	 *
	 * @param persistenceObject the <code>Persistence</code> instance on which to set value
	 * @param value the value to set
	 * @param service the service used to load an associated entity if the given value is a guid.
	 */
	public void checkStringValue(final Object persistenceObject, final String value, final ImportGuidHelper service) {
		setStringValue(persistenceObject, value, service);
	}

	/**
	 * Return <code>true</code> if this field is dependent on <code>Catalog</code>. Set the default to false.
	 *
	 * @return <code>true</code> if this is a Catalog dependent field.
	 */
	public boolean isCatalogObject() {
		return false;
	}

	/**
	 * Verifies if the value is not null.
	 * Throws exception if it is.
	 *
	 * @param value value to verify.
	 * @param name field name.
	 */
	protected void verifyNotNull(final String value, final String name) {
		if (checkNullValue(value)) {
			throw new EpNonNullBindException(name);
		}
	}

	/**
	 * Verifies if the value is positive.
	 * Throws exception if it is not.
	 *
	 * @param value value to verify.
	 */
	protected void verifyPositive(final String value) {
		if (new BigDecimal(value).compareTo(BigDecimal.ZERO) > 0) {
			return;
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Verifies if the value is not negative.
	 * Throws exception if it is.
	 *
	 * @param value value to verify.
	 */
	protected void verifyNonNegative(final String value) {
		if (new BigDecimal(value).compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Verifies if the value is integer.
	 * Throws exception if it is not.
	 *
	 * @param value value to verify.
	 */
	protected void verifyInteger(final String value) {
		try {
			new BigDecimal(value).toBigIntegerExact();
		} catch (ArithmeticException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
