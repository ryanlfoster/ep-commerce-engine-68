/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean;


/** Form bean used to collect information in the edit account controller/screen. */
public interface EditAccountFormBean extends EpFormBean {

	/**
	 * Set the customer's first name.
	 * @param firstName the customer's first name
	 */
	void setFirstName(final String firstName);
	
	/**
	 * Get the customer's first name.
	 * @return the first name
	 */
	String getFirstName();
	
	/**
	 * Set the customer's last name.
	 * @param lastName the customer's last name
	 */
	void setLastName(final String lastName);

	/**
	 * Get the customer's last name.
	 * @return the customer's last name
	 */
	String getLastName();
	
	/**
	 * Set the customer's email address.
	 * @param email the customer's email address
	 */
	void setEmail(final String email);
	
	/**
	 * Get the customer's email address.
	 * @return the customer's email address.
	 */
	String getEmail();

	/**
	 * Get the confirm password.
	 * @return the confirm password
	 */
	String getConfirmPassword();

	/**
	 * Set the confirm password.
	 * @param confirmPassword the confirm password
	 */
	void setConfirmPassword(final String confirmPassword);

	/**
	 * Get the password.
	 * @return the password
	 */
	String getPassword();

	/**
	 * Set the password.
	 * @param password the password
	 */
	void setPassword(final String password);
	
		
	/**
	 * Gets the phone number associated .
	 *
	 * @return the phone number.
	 */
	String getPhoneNumber();

	/**
	 * Sets the phone number.
	 *
	 * @param phoneNumber the new phone number.
	 */
	void setPhoneNumber(final String phoneNumber);

	
}
