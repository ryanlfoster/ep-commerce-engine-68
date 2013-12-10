/**
 * 
 */
package com.elasticpath.ql.parser;

/**
 * The runtime <code>SearchExecutionException</code> is thrown due to some error conditions while 
 * searching against a query. Up to this point the query is supposed to be correct and can not throw ParseException.  
 */
public class SearchExecutionException extends RuntimeException {

	private static final long serialVersionUID = 1945601069336501830L;

	/**
	 * Creates a new <code>SearchExecutionException</code> object with the given message.
	 *
	 * @param message the reason for this <code>SearchExecutionException</code>.
	 */
	public SearchExecutionException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>SearchExecutionException</code> object using the given message and cause exception.
	 *
	 * @param message the reason for this <code>SearchExecutionException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>SearchExecutionException</code>.
	 */
	public SearchExecutionException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
