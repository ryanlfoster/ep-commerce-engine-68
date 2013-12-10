package com.elasticpath.cmweb.formbean.impl;

import com.elasticpath.cmweb.formbean.SignInFormBean;


/**
 * This bean represents cmUser sign in form.
 */
//CHECKSTYLE:OFF
@SuppressWarnings({ "PMD.MethodNamingConventions", "PMD.VariableNamingConventions" })
public class SignInFormBeanImpl implements SignInFormBean {
	private String j_username;
	private String j_password;
	
	/**
	 * Get the j_username (for the username input field on the sign-in form).
	 * 
	 * @return j_username.
	 */
	public String getJ_username() {
		return this.j_username;
	}
	
	/**
	 * Set the j_username.
	 * 
	 * @param j_username the user input for username field on the sign-in form.
	 */
	public void setJ_username(final String j_username) {
		this.j_username = j_username;
	}
	
	/**
	 * Get the j_password (for the password input field on the sign-in form).
	 * 
	 * @return j_password.
	 */
	public String getJ_password() {
		return this.j_password;
		
	}
	
	/**
	 * Set the j_password.
	 * 
	 * @param j_password the user input for password field on the sign-in form.
	 */
	public void setJ_password(final String j_password) {
		this.j_password = j_password;
	}
}
