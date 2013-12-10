/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.processing;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.iterators.IteratorChain;


/**
 * An object provider that aggregates multiple other object providers.
 * 
 * @param <T> the class this provider works on
 */
public abstract class AbstractAggregationObjectProvider<T> implements Iterable<T> {

	/**
	 *
	 * @return a list of object providers
	 */
	protected abstract List<Iterable<T>> getAllProviders();
	
	/**
	 * Chains the iterators retrieved from the object providers.
	 * 
	 * @return an iterator instance
	 */
	@SuppressWarnings("unchecked")
	public Iterator<T> iterator() {
		IteratorChain iteratorChain = new IteratorChain();
		for (Iterable<T> provider : getAllProviders()) {
			iteratorChain.addIterator(provider.iterator());
		}
		return iteratorChain;
	}

}
