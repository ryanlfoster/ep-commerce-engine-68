/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.util;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Holds a pair of objects.
 * 
 * @param <FIRST> the type of the first object
 * @param <SECOND> the type of the second object
 */
public class Pair<FIRST, SECOND> implements Serializable {
	private final FIRST first;

	private final SECOND second;

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 610000000L;
	
	/**
	 * Creates a new pair for the given values.
	 *
	 * @param <FIRST> the generic type of the first parameter
	 * @param <SECOND> the generic type of the second parameter
	 * @param first first
	 * @param second second
	 * @return the pair
	 */
	@SuppressWarnings("PMD.ShortMethodName")
	public static <FIRST, SECOND> Pair<FIRST, SECOND> of(final FIRST first, final SECOND second) {
		return new Pair<FIRST, SECOND>(first, second);
	}

	/**
	 * Create a new object pair.
	 * 
	 * @param first the first object
	 * @param second the second object
	 */
	public Pair(final FIRST first, final SECOND second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * @return the first object
	 */
	public FIRST getFirst() {
		return first;
	}

	/**
	 * @return the second object
	 */
	public SECOND getSecond() {
		return second;
	}
	

	/**
	 * @return the hashCode base on the hashCodes of first and second.
	 */
	@Override
	public int hashCode() {
		final int startVal = 13;
		int hashCode = startVal;
		final int prime = 37;
		hashCode += prime * ObjectUtils.hashCode(first);
		hashCode += prime * ObjectUtils.hashCode(second);
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
		if (!(other instanceof Pair)) {
			return false;
		}
		
		Pair<?, ?> otherPair = (Pair<?, ?>) other;
		
		return ObjectUtils.equals(first, otherPair.first) 
			&& ObjectUtils.equals(second, otherPair.second);
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("first", first).append("second", second).toString();
	}
}
