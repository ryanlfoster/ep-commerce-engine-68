/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.formbean;

/**
 * A form bean for representing customer attributes in a web based form.
 */
public interface CustomerFormBean extends EpFormBean {

	/**
	 * Gets the <code>Customer</code>'s first name.
	 * 
	 * @return the first name.
	 */
	String getFirstName();
	
	/**
	 * Sets the <code>Customer</code>'s first name.
	 * 
	 * @param firstName the new first name.
	 */
	void setFirstName(final String firstName);

	/**
	 * Gets the <code>Customer</code>'s last name.
	 * 
	 * @return the last name.
	 */
	String getLastName();

	/**
	 * Sets the <code>Customer</code>'s last name.
	 * 
	 * @param lastName the new last name.
	 */
	void setLastName(final String lastName);

	/**
	 * Sets the clear-text password. The password will be encrypted using a secure hash like MD5 or SHA1 and saved as password.
	 * 
	 * @param clearTextPassword the clear-text password.
	 */
	void setClearTextPassword(final String clearTextPassword);

	/**
	 * Gets the clear-text password (only available at creation time).
	 * 
	 * @return the clear-text password.
	 */
	String getClearTextPassword();

	/**
	 * Sets the confirm clear-text password. This is to compare with the ClearTextPassword and make sure they are the same.
	 * 
	 * @param confirmClearTextPassword the user confirmClearTextPassword.
	 */
	void setConfirmClearTextPassword(final String confirmClearTextPassword);

	/**
	 * Gets the clear-text confirm password (only available at creation time).
	 * 
	 * @return the clear-text confirm password.
	 */
	String getConfirmClearTextPassword();
	
	/**
	 * Gets the email address of this <code>Customer</code>.
	 * 
	 * @return the email address.
	 */
	String getEmail();

	/**
	 * Sets the email address of this <code>Customer</code>.
	 * 
	 * @param email the new email address.
	 */
	void setEmail(final String email);

	/**
	 * Indicates whether the user wishes to be notified of news.
	 * 
	 * @return true if need to be notified, false otherwise
	 */
	boolean isToBeNotified();

	/**
	 * Set whether the user wishes to be notified of news.
	 * 
	 * @param toBeNotified set to true to indicate that need to be notified of news
	 */
	void setToBeNotified(final boolean toBeNotified);
	
	/**
	 * Gets the phone number associated with this <code>Customer</code>.
	 * 
	 * @return the phone number.
	 */
	String getPhoneNumber();

	/**
	 * Sets the phone number associated with this <code>Customer</code>.
	 * 
	 * @param phoneNumber the new phone number.
	 */
	void setPhoneNumber(final String phoneNumber);

}
