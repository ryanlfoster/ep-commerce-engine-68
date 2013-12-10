/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean.impl;

import com.elasticpath.sfweb.formbean.AnonymousSignInFormBean;

/**
 * Form bean for anonymous customer sign-in.
 *
 */
public class AnonymousSignInFormBeanImpl extends EpFormBeanImpl implements AnonymousSignInFormBean {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	private String email;
	
	/**
	 * Set the anonymous customer's email address.
	 * @param email the customer's email address
	 */
	public void setEmail(final String email) {
		this.email = email;
	}
	
	/**
	 * Get the anonymous customer's email address.
	 * @return the email address
	 */
	public String getEmail() {
		return this.email;
	}
	
}
