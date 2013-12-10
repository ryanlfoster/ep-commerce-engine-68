package com.elasticpath.service.security;

/**
 * A factory for creating Salt objects. Note that this can be any object as long it meets the following two requirements:
 * <ul>
 *   <li>The object's toString() method needs to produce the same output for equal salt objects.</li>
 *   <li>The resultant string representation of the salt cannot contain the characters { or }</li>
 * </ul>
 *
 * @param <T> the generic type of the salt object.
 */
public interface SaltFactory<T> {

	/**
	 * Creates a new Salt object.
	 *
	 * @return the salt object of type <T>
	 */
	T createSalt();

}
