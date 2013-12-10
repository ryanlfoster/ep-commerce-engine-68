/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.formbean.impl;

import com.elasticpath.sfweb.formbean.CustomerFormBean;

/**
 * The default implementation of the {@link CustomerFormBean} interface.
 */
public class CustomerFormBeanImpl extends EpFormBeanImpl implements CustomerFormBean {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private String firstName;
	private String lastName;
	private String clearTextPasword;
	private String confirmClearTextPassword;
	private String email;
	private String phoneNumber;
	private boolean toBeNotified;

	/**
	 * Gets the <code>Customer</code>'s first name.
	 *
	 * @return the first name.
	 */
	public String getFirstName() {		
		return this.firstName;
	}
	
	/**
	 * Sets the <code>Customer</code>'s first name.
	 *
	 * @param firstName the new first name.
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the <code>Customer</code>'s last name.
	 *
	 * @return the last name.
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * Sets customer's last name.
	 * 
	 * @param lastName the last name
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}


	/**
	 * Gets the clear text password.
	 * 
	 * @return the clear text password
	 */
	public String getClearTextPassword() {
		return this.clearTextPasword;
	}
	
	/**
	 * Sets the clear text password.
	 * 
	 * @param clearTextPassword the password
	 */
	public void setClearTextPassword(final String clearTextPassword) {
		this.clearTextPasword = clearTextPassword;
	}

	/**
	 * Gets the confirmed clear text password.
	 * 
	 * @return the confirmed clear text password
	 */
	public String getConfirmClearTextPassword() {
		return this.confirmClearTextPassword;
	}

	/**
	 * Sets the confirmed clear text password.
	 * 
	 * @param confirmClearTextPassword the password in plain text
	 */
	public void setConfirmClearTextPassword(final String confirmClearTextPassword) {
		this.confirmClearTextPassword = confirmClearTextPassword;
	}

	/**
	 * Gets the email address.
	 * 
	 * @return the email address
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Sets the email address.
	 * 
	 * @param email the email address
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * Gets the phone number.
	 *
	 * @return the phone number.
	 */
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	/**
	 * Sets the customer's phone number.
	 * 
	 * @param phoneNumber the phone number
	 */
	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Indicates whether the user wishes to be notified of news.
	 *
	 * @return true if need to be notified, false otherwise
	 */
	public boolean isToBeNotified() {
		return this.toBeNotified;
	}

	/**
	 * Set whether the user wishes to be notified of news.
	 *
	 * @param toBeNotified set to true to indicate that need to be notified of news
	 */
	public void setToBeNotified(final boolean toBeNotified) {
		this.toBeNotified = toBeNotified;
	}

}
