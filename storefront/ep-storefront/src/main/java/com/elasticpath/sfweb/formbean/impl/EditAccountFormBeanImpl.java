/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean.impl;

import com.elasticpath.sfweb.formbean.EditAccountFormBean;

/** Form bean used to collect information in the edit account controller/screen. */
public class EditAccountFormBeanImpl extends EpFormBeanImpl implements EditAccountFormBean {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String confirmPassword;
	private String phoneNumber;
		
	/**
	 * Set the customer's first name.
	 * @param firstName the customer's first name
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}
	
	/**
	 * Get the customer's first name.
	 * @return the first name
	 */
	public String getFirstName() {
		return this.firstName;
	}
	
	/**
	 * Set the customer's last name.
	 * @param lastName the customer's last name
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Get the customer's last name.
	 * @return the customer's last name
	 */
	public String getLastName() {
		return this.lastName;
	}
	
	/**
	 * Set the customer's email address.
	 * @param email the customer's email address
	 */
	public void setEmail(final String email) {
		this.email = email;
	}
	
	/**
	 * Get the customer's email address.
	 * @return the customer's email address.
	 */
	public String getEmail() {
		return this.email;
	}	
	
	/**
	 * Get the confirm password.
	 * @return the confirm password
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}

	/**
	 * Set the confirm password.
	 * @param confirmPassword the confirm password
	 */
	public void setConfirmPassword(final String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	/**
	 * Get the password.
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Set the password.
	 * @param password the password
	 */
	public void setPassword(final String password) {
		this.password = password;
	}
	

	/**
	 * Gets the phone number associated .
	 *
	 * @return the phone number.
	 */
	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	/**
	 * Sets the phone number.
	 *
	 * @param phoneNumber the new phone number.
	 */
	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
