package com.elasticpath.ql.parser;

/**
 * Application-level parse exception thrown when Ep query is in incorrect syntax. Note that <code>EpQLParseException</code> is external wide
 * exception as opposed to <code>com.elasticpath.ql.parser.gen.ParseException</code> which is internal for JavaCC based parser.
 */
public class EpQLParseException extends Exception {
	private static final long serialVersionUID = -4473163346534751718L;

	/**
	 * Creates a new <code>EpQLParseException</code> object with the given message.
	 * 
	 * @param message the reason for this <code>EpQLParseException</code>.
	 */
	public EpQLParseException(final String message) {
		super(message);
	}

	/**
	 * Creates a new <code>EpQLParseException</code> object using the given message and cause exception.
	 * 
	 * @param message the reason for this <code>EpQLParseException</code>.
	 * @param cause the <code>Throwable</code> that caused this <code>EpQLParseException</code>.
	 */
	public EpQLParseException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
