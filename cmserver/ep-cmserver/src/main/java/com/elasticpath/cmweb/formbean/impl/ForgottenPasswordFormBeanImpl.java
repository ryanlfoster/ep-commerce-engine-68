/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmweb.formbean.impl;

import com.elasticpath.cmweb.formbean.ForgottenPasswordFormBean;

/**
 * The default implementation of <code>ForgottenPasswordFormBean</code>.
 */
public class ForgottenPasswordFormBeanImpl implements ForgottenPasswordFormBean {
	
	/**
	 * Serial version id.
	 */
	public static final long serialVersionUID = 5000000001L;
	
	private String email;

	/**
	 * Set the email.
	 * @param email the email to set.
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * Return the email.
	 * @return the email.
	 */
	public String getEmail() {
		return this.email;
	}

}
