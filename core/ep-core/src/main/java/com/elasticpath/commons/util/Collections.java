package com.elasticpath.commons.util;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * A supplement to <code>java.util.Collections</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.AvoidDuplicateLiterals" })
public final class Collections {
	private Collections() {
		// static class
	}

	/**
	 * The empty sorted set (immutable). This set is serializable.
	 *
	 * @see #emptySet()
	 */
	@SuppressWarnings("rawtypes")
	private static final SortedSet EMPTY_SORTED_SET = new EmptySortedSet();

	/**
	 * @param <T> the set type
	 * @return an empty immutable {@link SortedSet}
	 */
	@SuppressWarnings("unchecked")
	public static <T> SortedSet<T> emptySortedSet() {
		return EMPTY_SORTED_SET;
	}


	/**
	 * The empty entry sorted set (immutable). This set is serializable.
	 *
	 * @see #emptySet()
	 */
	@SuppressWarnings("rawtypes")
	private static final SortedSet EMPTY_SORTED_ENTRY_SET = new EmptySortedSet();

	/**
	 * @serial include
	 */
	@SuppressWarnings("unchecked")
	private static class EmptySortedSet<T> extends AbstractSet<T> implements Serializable, SortedSet<T> {
		// use serialVersionUID from JDK 1.2.2 for interoperability
		private static final long serialVersionUID = 1582296315990362920L;

		@Override
		public Iterator<T> iterator() {
			return new Iterator<T>() {
				public boolean hasNext() {
					return false;
				}

				public T next() {
					throw new NoSuchElementException();
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean contains(final Object obj) {
			return false;
		}

		// Preserves singleton property
		private Object readResolve() {
			return EMPTY_SORTED_SET;
		}

		/**
		 * Returns <code>null</code>.
		 *
		 * @return <code>null</code>.
		 */
		public Comparator<Object> comparator() {
			return null;
		}

		/**
		 * Returns <code>EMPTY_SORTED_SET</code>.
		 *
		 * @return <code>EMPTY_SORTED_SET</code>.
		 */
		public SortedSet<T> subSet(final Object arg0, final Object arg1) {
			return EMPTY_SORTED_SET;
		}

		/**
		 * Returns <code>EMPTY_SORTED_SET</code>.
		 *
		 * @return <code>EMPTY_SORTED_SET</code>.
		 */
		public SortedSet<T> headSet(final Object arg0) {
			return EMPTY_SORTED_SET;
		}

		/**
		 * Returns <code>EMPTY_SORTED_SET</code>.
		 *
		 * @return <code>EMPTY_SORTED_SET</code>.
		 */
		public SortedSet<T> tailSet(final Object arg0) {
			return EMPTY_SORTED_SET;
		}

		/**
		 * Returns <code>null</code>.
		 *
		 * @return <code>null</code>.
		 */
		public T first() {
			return null;
		}

		/**
		 * Returns <code>null</code>.
		 *
		 * @return <code>null</code>.
		 */
		public T last() {
			return null;
		}
	}

	/**
	 * The empty sorted map (immutable). This map is serializable.
	 */
	@SuppressWarnings("rawtypes")
	private static final SortedMap EMPTY_SORTED_MAP = new EmptySortedMap();

	/**
	 * @param <K> the key type
	 * @param <V> the value type
	 * @return empty immutable {@link SortedMap}
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> SortedMap<K, V> emptySortedMap() {
		return EMPTY_SORTED_MAP;
	}

	/**
	 * Represents a empty sorted map.
	 */
	@SuppressWarnings("unchecked")
	private static class EmptySortedMap<K, V> extends AbstractMap<K, V> implements Serializable, SortedMap<K, V> {
		private static final long serialVersionUID = 6428348081105594320L;

		/**
		 * Returns 0.
		 *
		 * @return 0.
		 */
		@Override
		public int size() {
			return 0;
		}

		/**
		 * Returns <code>true</code>.
		 *
		 * @return <code>true</code>.
		 */
		@Override
		public boolean isEmpty() {
			return true;
		}

		/**
		 * Returns <code>false</code>.
		 *
		 * @param key not used
		 * @return <code>false</code>.
		 */
		@Override
		public boolean containsKey(final Object key) {
			return false;
		}

		/**
		 * Returns <code>false</code>.
		 *
		 * @param key not used
		 * @return <code>false</code>.
		 */
		@Override
		public boolean containsValue(final Object value) {
			return false;
		}

		/**
		 * Returns <code>null</code>.
		 *
		 * @param key not used
		 * @return <code>null</code>.
		 */
		@Override
		public V get(final Object key) {
			return null;
		}

		/**
		 * Returns <code>EMPTY_SORTED_SET</code>.
		 *
		 * @return <code>EMPTY_SORTED_SET</code>.
		 */
		@Override
		public Set<K> keySet() {
			return EMPTY_SORTED_SET;
		}

		/**
		 * Returns <code>EMPTY_SORTED_SET</code>.
		 *
		 * @return <code>EMPTY_SORTED_SET</code>.
		 */
		@Override
		public Collection<V> values() {
			return EMPTY_SORTED_SET;
		}

		/**
		 * Returns <code>EMPTY_SORTED_SET</code>.
		 *
		 * @return <code>EMPTY_SORTED_SET</code>.
		 */
		@Override
		public Set<Entry<K, V>> entrySet() {
			return EMPTY_SORTED_ENTRY_SET;
		}

		@Override
		public boolean equals(final Object object) {
			return (object instanceof Map) && ((Map< ? , ? >) object).size() == 0;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		// Preserves singleton property
		private Object readResolve() {
			return EMPTY_SORTED_MAP;
		}

		/**
		 * Returns <code>null</code>.
		 *
		 * @return <code>null</code>.
		 */
		public Comparator<Object> comparator() {
			return null;
		}

		/**
		 * Returns <code>EMPTY_SORTED_MAP</code>.
		 *
		 * @param arg0 not used
		 * @param arg1 not used *
		 * @return <code>EMPTY_SORTED_MAP</code>.
		 */
		public SortedMap<K, V> subMap(final Object arg0, final Object arg1) {
			return EMPTY_SORTED_MAP;
		}

		/**
		 * Returns <code>EMPTY_SORTED_MAP</code>.
		 *
		 * @param arg0 not used
		 * @return <code>EMPTY_SORTED_MAP</code>.
		 */
		public SortedMap<K, V> headMap(final Object arg0) {
			return EMPTY_SORTED_MAP;
		}

		/**
		 * Returns <code>EMPTY_SORTED_MAP</code>.
		 *
		 * @param arg0 not used
		 * @return <code>EMPTY_SORTED_MAP</code>.
		 */
		public SortedMap<K, V> tailMap(final Object arg0) {
			return EMPTY_SORTED_MAP;
		}

		/**
		 * Returns <code>null</code>.
		 *
		 * @return <code>null</code>.
		 */
		public K firstKey() {
			return null;
		}

		/**
		 * Returns <code>null</code>.
		 *
		 * @return <code>null</code>.
		 */
		public K lastKey() {
			return null;
		}
	}
}
