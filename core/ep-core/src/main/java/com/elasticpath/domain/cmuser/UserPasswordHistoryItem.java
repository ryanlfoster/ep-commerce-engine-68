package com.elasticpath.domain.cmuser;

import java.util.Date;

import com.elasticpath.persistence.api.Persistable;

/**
 * Contains information about password used by CM user in the past and date when it was changed.
 */
public interface UserPasswordHistoryItem extends Persistable {

	/**
	 * Gets the date when password was expired.
	 * 
	 * @return old password expiration date
	 */
	Date getExpirationDate();

	/**
	 * Sets the date when password was expired.
	 * 
	 * @param date old password expiration date
	 */
	void setExpirationDate(final Date date);
	
	/**
	 * Gets old encrypted password.
	 * 
	 * @return old encrypted password used in the past
	 */
	String getOldPassword();

	/**
	 * Sets old encrypted password.
	 * 
	 * @param password old encrypted password
	 */
	void setOldPassword(final String password);
	
	/**
	 * Compares this password history item with another one and returns true if and only if they are structural unified.
	 * 
	 * @param rhs right hand side object to compare with
	 * @return true if and only if objects are structural unified
	 */
	boolean equals(final Object rhs);

	/**
	 * Calculates hash code based on password value.
	 * 
	 * @return the hash code
	 */
	int hashCode();
}
