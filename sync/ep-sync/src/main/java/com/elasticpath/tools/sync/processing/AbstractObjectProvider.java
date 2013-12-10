/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.processing;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This implementation takes a number of the elements and iterates through 
 * those elements one by one by using {@link #getElement(int)}.
 * 
 * @param <T> the class this provider works on
 */
public abstract class AbstractObjectProvider<T> implements Iterable<T> {

	/**
	 * Gets the number of elements for this provider.
	 * 
	 * @return the number of elements this provider will contribute
	 */
	protected abstract int getSize();

	/**
	 * Gets the element at {@code index}.
	 * 
	 * @param index the index of the element
	 * @return the element at the given index
	 */
	protected abstract T getElement(int index);

	/**
	 * @return a new instance
	 */
	public Iterator<T> iterator() {
		final int size = getSize();
		return new Iterator<T>() {

			private int index = 0;

			/**
			 * @return true if there's a next element
			 */
			public boolean hasNext() {
				return index < size;
			}

			/**
			 * @return the next element
			 */
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException("No more elements available");
				}
				return getElement(index++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
}
