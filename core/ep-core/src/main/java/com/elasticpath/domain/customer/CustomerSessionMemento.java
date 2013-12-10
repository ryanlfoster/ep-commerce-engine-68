package com.elasticpath.domain.customer;

import java.util.Currency;
import java.util.Date;

import com.elasticpath.base.GloballyIdentifiable;
import com.elasticpath.domain.shopper.ShopperKey;
import com.elasticpath.persistence.api.Persistable;

/**
 * Persistent data for the CustomerSession.
 */
public interface CustomerSessionMemento extends GloballyIdentifiable<String>, ShopperKey, Persistable {

	/**
	 * Get the date the session was created.
	 *
	 * @return the date
	 */
	Date getCreationDate();

	/**
	 * Set the date the session was created.
	 *
	 * @param creationDate the date
	 */
	void setCreationDate(Date creationDate);

	/**
	 * Get the date the session was last accessed.
	 *
	 * @return the date
	 */
	Date getLastAccessedDate();

	/**
	 * Set the date the session was last accessed.
	 *
	 * @param lastAccessedDate the date
	 */
	void setLastAccessedDate(Date lastAccessedDate);

	/**
	 * Get the locale as a string.
	 *
	 * @return the locale string
	 */
	String getLocaleStr();

	/**
	 * Set the locale as a string.
	 *
	 * @param localeStr the string representation of the locale
	 */
	void setLocaleStr(String localeStr);

	/**
	 * Get the currency of the customer corresponding to the shopping cart.
	 *
	 * @return the <code>Currency</code>
	 */
	Currency getCurrency();

	/**
	 * Set the currency of the customer corresponding to the shopping cart.
	 *
	 * @param currency the <code>Currency</code>
	 */
	void setCurrency(Currency currency);

	/**
	 * Get the ipAddress of the user from the shopping cart.
	 *
	 * @return the ipAddress
	 */
	String getIpAddress();

	/**
	 * Set the users ip Address into the shopping cart.
	 *
	 * @param ipAddress the ipAddress of the user.
	 */
	void setIpAddress(String ipAddress);

}