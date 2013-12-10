package com.elasticpath.commons.security;

/**
 * <code>ValidationError</code> represents single validation error.
 */
public class ValidationError {
	private String key;

	private Object[] params;

	/**
	 * Constructs the instance of this class with the given error key.
	 * 
	 * @param key error key
	 */
	public ValidationError(final String key) {
		this.key = key;
		this.params = new Object[] {};
	}

	/**
	 * Constructs the instance of this class with the given error key and params.
	 * 
	 * @param key error key
	 * @param params params
	 */
	public ValidationError(final String key, final Object[] params) { // NOPMD
		this.key = key;
		this.params = params;
	}

	/**
	 * Gets error key.
	 * 
	 * @return error key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Sets error key.
	 * 
	 * @param key error key
	 */
	public void setKey(final String key) {
		this.key = key;
	}

	/**
	 * Gets error parameters.
	 * 
	 * @return error parameters
	 */
	public Object[] getParams() {
		return params; // NOPMD
	}

	/**
	 * Sets error parameters.
	 * 
	 * @param params error parameters
	 */
	public void setParams(final Object[] params) { // NOPMD
		this.params = params;
	}
}
