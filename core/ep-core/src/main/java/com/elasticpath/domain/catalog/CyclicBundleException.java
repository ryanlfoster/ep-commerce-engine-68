/**
 * 
 */
package com.elasticpath.domain.catalog;

import com.elasticpath.domain.EpDomainException;

/**
 * Exception indicating that a bundle constituent contains a reference to
 * a parent constituent in the constituent tree.
 */
public class CyclicBundleException extends EpDomainException {

	private static final long serialVersionUID = 3214677837480647652L;

	private Product bundle;
	private Product constituent;

	/**
	 * @param message the message string
	 */
	public CyclicBundleException(final String message) {
		super(message);
	}

	/**
	 * @param message the message string
	 * @param cause the cause
	 */
	public CyclicBundleException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	/**
	 * Constructor.
	 * @param bundle the bundle that has a constituent with a cyclical reference.
	 * @param constituent that has a descendant with a reference to that same constituent.
	 */
	public CyclicBundleException(final Product bundle, final Product constituent) {
		super("Bundle " + bundle.getCode() + "'s constituent " + constituent.getCode()	
				+ " has a descendant that references " + constituent.getCode());
		this.bundle = bundle;
		this.constituent = constituent;
	}
	
	/**
	 * @return the bundle that has a constituent with a cyclical reference.
	 */
	public Product getBundle() {
		return this.bundle;
	}
	
	/**
	 * @return the constituent that has a descendant with a reference to that same constituent.
	 */
	public Product getConstituent() {
		return this.constituent;
	}
}
