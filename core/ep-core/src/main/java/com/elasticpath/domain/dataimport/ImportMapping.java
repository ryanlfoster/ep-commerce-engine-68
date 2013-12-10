package com.elasticpath.domain.dataimport;

import com.elasticpath.persistence.api.Persistable;

/**
 * Represents one import mapping between an import field and an import column.
 */
public interface ImportMapping extends Persistable {
	/**
	 * Returns the name of import field.
	 * 
	 * @return the name of the import field.
	 */
	String getName();

	/**
	 * Set the name of import field.
	 * 
	 * @param name the name to set
	 */
	void setName(String name);

	/**
	 * Returns the import column number.
	 * 
	 * @return the import column number
	 */
	Integer getColNumber();

	/**
	 * Sets the import column number.
	 * 
	 * @param colNumber the import column number
	 */
	void setColNumber(Integer colNumber);

}
