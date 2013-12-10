package com.elasticpath.service.search.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NullArgumentException;

import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;

/**
 * An advanced search criteria used for filtering results with another search criteria. This
 * criteria may also join together different search criteria's to form the basis of an
 * {@link Relationship#AND ANDed} search or {@link Relationship#OR ORed} search.
 * 
 * @param <T> the type of search criteria this filter is based on
 */
public class FilteredSearchCriteria<T extends SearchCriteria> extends AbstractSearchCriteriaImpl implements SearchCriteria,
		Iterable<Map.Entry<T, Collection<T>>> {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private List<Map.Entry<T, Collection<T>>> searchCriterias;

	private Relationship relationship = Relationship.AND;
	
	/**
	 * Adds a search criteria (and a list of filters) to the list of things to search for. No
	 * filters will be applied to this search criteria.
	 * 
	 * @param searchCriteria the search criteria to search for
	 * @throws IllegalArgumentException if <code>searchCriteria</code>s index type is not the
	 *             same as others that have been added
	 */
	public void addCriteria(final T searchCriteria) throws IllegalArgumentException {
		addCriteria(null, searchCriteria);
	}
	
	/**
	 * Adds a search criteria (and a list of filters) to the list of things to search for. The
	 * given list of filters may be <code>null</code> if there should be no filters applied to
	 * the given search criteria.
	 * 
	 * @param searchCriteria the search criteria to search for
	 * @param filters list of filters to be applied to the search criteria
	 * @throws IllegalArgumentException if <code>searchCriteria</code>s index type is not the
	 *             same as others that have been added or if the <code>filter</code>s index
	 *             type differs from the given <code>searchCriteria</code>s index type
	 */
	public void addCriteria(final T searchCriteria, final T... filters) throws IllegalArgumentException {
		addCriteria(filters, searchCriteria);
	}
	
	@SuppressWarnings("PMD.UnusedPrivateMethod") // This method is used, for some PMD doesn't realize.
	private void addCriteria(final T[] filters, final T searchCriteria) {
		if (searchCriterias == null) {
			searchCriterias = new ArrayList<Map.Entry<T, Collection<T>>>();
		}

		checkTypeValid(searchCriteria, filters);

		final Collection<T> emptyCollection = Collections.emptyList();
		Entry<T, Collection<T>> entry = new Entry<T, Collection<T>>(searchCriteria, emptyCollection);
		searchCriterias.add(entry);
		if (filters != null) {
			entry.setValue(Arrays.asList(filters));
		}
	}

	private void checkTypeValid(final T searchCriteria, final T... filters) {
		// if a previous search criteria is given, only need to compare the first (because all of
		// them must be the same)
		if (searchCriterias != null && !searchCriterias.isEmpty()
				&& !searchCriterias.get(0).getKey().getIndexType().equals(searchCriteria.getIndexType())) {
			throw new IllegalArgumentException(String.format(
					"searchCriteria's index type(%1$s) must be the same as other search criterias(%2$s)", searchCriteria
							.getIndexType(), searchCriterias.get(0).getKey().getIndexType()));
		}
		if (filters != null) {
			// only need to check against the given search criteria because given search criteria
			// is checked against all other search criteria's
			for (T criteria : filters) {
				if (criteria.getIndexType() != searchCriteria.getIndexType()) {
					throw new IllegalArgumentException(String.format(
							"filters's index type(%1$s) must be the same as searchCriteria(%2$s)", searchCriteria.getIndexType(),
							searchCriteria.getIndexType()));
				}
			}
		}
	}

	/**
	 * Removes the given <code>searchCriteria</code>. Only the first instance of this search
	 * criteria will be removed.
	 * 
	 * @param searchCriteria the search criteria to remove
	 * @return whether the operation was successful or the <code>searchCriteria</code> didn't
	 *         exist
	 */
	public boolean removeCriteria(final T searchCriteria) {
		for (Iterator<Map.Entry<T, Collection<T>>> criteriaIter = searchCriterias.iterator(); criteriaIter.hasNext();) {
			if (searchCriteria.equals(criteriaIter.next().getKey())) {
				criteriaIter.remove();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Retrieves the entry {@link SearchCriteria} from the list of search criteria's at the given index.
	 * 
	 * @param index the index to get
	 * @return the entry {@link SearchCriteria} from the list of search criteria's at the given index
	 */
	public Map.Entry<T, Collection<T>> getCriteria(final int index) {
		return searchCriterias.get(index);
	}
	
	/**
	 * Returns the number of criteria's in this filtered criteria. If this filtered criteria
	 * contains more than <code>Integer.MAX_VALUE</code> elements, returns
	 * <code>Integer.MAX_VALUE</code>.
	 * 
	 * @return the number of elements in this filtered criteria
	 */
	public int size() {
		if (searchCriterias == null) {
			return 0;
		}
		return searchCriterias.size();
	}
	
	/**
     * Returns <code>true</code> if no search criteria's have been added.
     *
     * @return <code>true</code> if no search criteria's have been added
     */
	public boolean isEmpty() {
		if (searchCriterias == null) {
			return true;
		}
		return searchCriterias.isEmpty();
	}
	
	/**
     * Returns an iterator over the criteria's in this filtered criteria.
     *
     * @return an iterator over the criteria's in this filtered criteria
     */
	public Iterator<Map.Entry<T, Collection<T>>> iterator() {
		if (searchCriterias == null) {
			final List<Map.Entry<T, Collection<T>>> emptyList = Collections.emptyList();
			return emptyList.iterator();
		}
		return searchCriterias.iterator();
	}

	/**
	 * Gets the {@link Relationship} of each search criteria.
	 * 
	 * @return the {@link Relationship} of each search criteria
	 */
	public Relationship getRelationship() {
		return relationship;
	}

	/**
	 * Sets the {@link Relationship} of each search criteria. All share the same relationship.
	 * 
	 * @param relationship the {@link Relationship} of each search criteria
	 */
	public void setRelationship(final Relationship relationship) {
		if (relationship == null) {
			throw new NullArgumentException("relationship");
		}
		this.relationship = relationship;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * <b>Warning</b>: Deeply nested {@link FilteredSearchCriteria} may have performance
	 * problems.
	 * </p>
	 */
	public void optimize() {
		if (searchCriterias != null) {
			for (Map.Entry<T, Collection<T>> entry : searchCriterias) {
				entry.getKey().optimize();
				for (T criteria : entry.getValue()) {
					criteria.optimize();
				}
			}
		}
	}

	/**
	 * Returns the index type of the stored search criteria (or <code>null</code> if there are
	 * no inner search criteria's).
	 * 
	 * @return the index type of the stored search criteria
	 */
	public IndexType getIndexType() {
		if (searchCriterias == null || searchCriterias.isEmpty()) {
			return null;
		}
		return searchCriterias.get(0).getKey().getIndexType();
	}

	/** Represents how search criteria's are related to each other. */
	public enum Relationship {
		/** Each search criteria is ANDed together giving a more restrictive set of conditions. */
		AND,
		/** Each search criteria is ORed together giving a broader set of conditions. */
		OR;
	}
	
	/**
	 * Entry of class of the filtered search criteria.
	 */
	private class Entry<K, V> implements Map.Entry<K, V> {
		
		private final K key;
		
		private V value;
		
		public Entry(final K key, final V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(final V value) {
			final V oldValue = this.value;
			this.value = value;
			return oldValue;
		}
	}
}
