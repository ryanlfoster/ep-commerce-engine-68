/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.sfweb.formbean;


/**
 * This bean represents forgotten password form.
 */
public interface ForgottenPasswordFormBean  extends EpFormBean {
	/**
	 * Set the email.
	 * @param email the email to set.
	 */
	void setEmail(String email);
	
	/**
	 * Return the email.
	 * @return the email.
	 */
	String getEmail();
	
}
