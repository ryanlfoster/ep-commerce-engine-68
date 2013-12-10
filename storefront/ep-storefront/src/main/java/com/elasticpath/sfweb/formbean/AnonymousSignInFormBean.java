/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean;

/**
 * Form bean for anonymous customer sign-in.
 */
public interface AnonymousSignInFormBean extends EpFormBean {

	/**
	 * Set the anonymous customer's email address.
	 * 
	 * @param email the customer's email address
	 */
	void setEmail(final String email);

	/**
	 * Get the anonymous customer's email address.
	 * 
	 * @return the email address
	 */
	String getEmail();
}
