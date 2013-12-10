/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.cmweb.formbean;


/**
 * <code>SignInFormBean</code> represents the command object for sign-in form.
 */
//CHECKSTYLE:OFF
@SuppressWarnings("PMD.MethodNamingConventions")
public interface SignInFormBean { 
	/**
	 * Get the j_username (for the username input field on the sign-in form).
	 * 
	 * @return j_username.
	 */
	String getJ_username();  
	
	/**
	 * Set the j_username.
	 * 
	 * @param j_username the user input for username field on the sign-in form.
	 */
	void setJ_username(final String j_username); 
	
	/**
	 * Get the j_password (for the password input field on the sign-in form).
	 * 
	 * @return j_password.
	 */
	String getJ_password(); 
	
	/**
	 * Set the j_password.
	 * 
	 * @param j_password the user input for password field on the sign-in form.
	 */
	void setJ_password(final String j_password); 
	
}
