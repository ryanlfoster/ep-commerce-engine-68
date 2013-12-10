package com.elasticpath.domain.dataimport;

import com.elasticpath.domain.EpDomain;
import com.elasticpath.service.dataimport.ImportGuidHelper;

/**
 * Represents an import field.
 */
public interface ImportField extends EpDomain {

	/**
	 * Return the import field name.
	 *
	 * @return the import field name
	 */
	String getName();

	/**
	 * Return the import field type.
	 *
	 * @return the import field type
	 */
	String getType();

	/**
	 * Return <code>true</code> if it is a required field.
	 *
	 * @return <code>true</code> if it is required
	 */
	boolean isRequired();

	/**
	 * @param required true if this field is required, false if not.
	 */
	void setRequired(boolean required);

	/**
	 * Return the value of the field as a string with the given {@code Object}.
	 *
	 * @param object the {@code Object} from which to retrieve the value
	 * @return the <code>String</code> value of the field
	 */
	String getStringValue(Object object);

	/**
	 * Sets the string value to the field of the given {@code Object}.
	 *
	 * @param object the {@code Object} on which to set the value
	 * @param value the value to set
	 * @param service the service used to load an associated entity if the given value is a guid.
	 */
	void setStringValue(Object object, String value, ImportGuidHelper service);

	/**
	 * Checks the given string value of the field.
	 *
	 * @param object the {@code Object} on which to check the value
	 * @param value the value to check
	 * @param service the service used to load an associated entity if the given value is a guid.
	 */
	void checkStringValue(Object object, String value, ImportGuidHelper service);

	/**
	 * Return <code>true</code> if this field is dependent on <code>Catalog</code>.
	 *
	 * @return <code>true</code> if this is a Catalog dependent field.
	 */
	boolean isCatalogObject();

	/**
	 * Return <code>true</code> if it is a required field.
	 *
	 * @return <code>true</code> if it is required
	 */
	boolean isRequiredPrimaryField();
}
