package com.elasticpath.commons.util;

import java.util.Locale;

import org.apache.commons.lang.ObjectUtils;

/**
 * 
 * Case insensitive pair of strings, to perform kind of
 * join operation on java side.
 *
 */
public class PairInsensitiveString extends Pair<String, String> {

	private static final long serialVersionUID = 1L;

	private final Locale locale;

	/**
	 * Create a new object strings pair.
	 * 
	 * @param first the first string
	 * @param second the second string
	 * @param locale locale.
	 */
	public PairInsensitiveString(final String first, final String second, final Locale locale) {
		super(first, second);
		this.locale = locale;
	}

	private String toLowerCase(final String string) {
		if (string != null) {
			return string.toLowerCase(locale);
		}
		return null;
	}

	/**
	 * @return the hashCode base on the hashCodes of first and second.
	 */
	@Override
	public int hashCode() {
		final int startVal = 13;
		int hashCode = startVal;
		final int prime = 37;
		hashCode += prime * ObjectUtils.hashCode(toLowerCase(getFirst()));
		hashCode += prime * ObjectUtils.hashCode(toLowerCase(getSecond()));
		return hashCode;
	}

	/**
	 * @param other the other object to test for equality.
	 * @return true if both object's first and second are equal, false otherwise.
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PairInsensitiveString)) {
			return false;
		}

		final PairInsensitiveString otherPair = (PairInsensitiveString) other;

		return ObjectUtils.equals(toLowerCase(getFirst()), toLowerCase(otherPair.getFirst()))
		&& ObjectUtils.equals(toLowerCase(getSecond()), toLowerCase(otherPair.getSecond()));
	}

}
