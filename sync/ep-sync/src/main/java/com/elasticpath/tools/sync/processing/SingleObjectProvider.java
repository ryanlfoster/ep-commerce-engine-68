/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.processing;

/**
 * An implementation of {@link Iterable} that takes only one object as a source for the iterator.
 * 
 * @param <T> the class of the object
 */
public class SingleObjectProvider<T> extends AbstractObjectProvider<T> {

	private final T object;

	/**
	 * Constructor.
	 * 
	 * @param object the object to use
	 */
	public SingleObjectProvider(final T object) {
		super();
		this.object = object;
	}

	/**
	 * Gets the element of the given index.
	 * 
	 * @param index the index of the element
	 * @return the object
	 */
	@Override
	protected T getElement(final int index) {
		return object;
	}

	/**
	 * Gets the number of objects for this provider.
	 * 
	 * @return the size
	 */
	@Override
	protected int getSize() {
		return 1;
	}

}
