package com.elasticpath.service.search;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * The Type-Safe Enum for Index Type. <br>
 * <code>IndexType</code> was designed for future subclassing.
 *
 * <p>
 * An example for subclass follows:
 *
 * <pre>
 *     package my.package;
 *     public class MyIndexType extends IndexType {
 *
 *         public static final MyIndexType NEW_TYPE = new MyIndexType(&quot;MY_NEW_INDEX_TYPE&quot;);
 *
 *         ...
 *
 *         protected MyIndexType(final String value) {
 *             super(value);
 *         }
 *     }
 * </pre>
 *
 * </p>
 */
public class IndexType implements Serializable {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/*
	 * The declaration of values map should be placed before any IndexType
	 * constant.
	 */
	private static Map<String, IndexType> typeValuesMap = new HashMap<String, IndexType>();

	/** A product index type. */
	public static final IndexType PRODUCT = new IndexType("product");

	/** A category index type. */
	public static final IndexType CATEGORY = new IndexType("category");

	/** A customer index type. */
	public static final IndexType CUSTOMER = new IndexType("customer");

	/** A promotion index type. */
	public static final IndexType PROMOTION = new IndexType("promotion");

	/** A cmuser index type. */
	public static final IndexType CMUSER = new IndexType("cmuser");

	/** A shipping service level index type. */
	public static final IndexType SHIPPING_SERVICE_LEVEL = new IndexType("shippingservicelevel");

	/** A SKU index type. */
	public static final IndexType SKU = new IndexType(SolrIndexConstants.SKU_SOLR_CORE);

	/*
	 * The Value Code which is saved in Index Type.
	 */
	private final String value;

	/**
	 * The constructor. <br>
	 * Saves the value and registers itself in typeValuesMap (a static Map).
	 * <br>
	 * Subclasses will invoke this constructor and register themselves
	 * automatically. <br>
	 * IndexType constructor is protected. This at least protects the class
	 * from <br>
	 * having anyone add a new Type and forces subclassing of IndexType
	 * before <br>
	 * adding a new Type.
	 *
	 * @param value
	 *            the string representation of IndexType
	 * @throws IllegalArgumentException
	 *             if value is null or exist in typeValuesMap, to prevent
	 *             duplication of value code in subclasses.
	 */
	protected IndexType(final String value) {
		if (value == null) {
			throw new IllegalArgumentException("Value of IndexType shuld not be null");
		}

		this.value = value;

		if (typeValuesMap.containsKey(value)) {
			throw new IllegalArgumentException("Duplicate IndexType value found : " + value);
		}

		typeValuesMap.put(value, this);
	}

	/**
	 * This implementation is simple and final for preventing overriding in
	 * subclass. Two IndexType instances are equal if their values are equal.
	 * {@inheritDoc}
	 *
	 * @see Object#equals(Object)
	 */
	@Override
	public final boolean equals(final Object obj) {
		if (!(obj instanceof IndexType)) {
			return false;
		}
		return value.equals(((IndexType) obj).value); // this is correct
														// only while value
														// is checked for
														// null in
														// constructor.
	}

	/**
	 * This implementation is simple and final for preventing overriding in
	 * subclass. {@inheritDoc}
	 *
	 * @see Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		return value.hashCode();
	}

	/**
	 * Gets the value.
	 *
	 * @return the value.
	 */
	public final String getIndexName() {
		return value;
	}

	/**
	 * Returns the IndexType constant corresponding to specified value. <br>
	 * Subclasses can use this method for theirs values, <br>
	 * because every IndexType constant registers itself in values Map.
	 *
	 * @param value
	 *            the IndexType value
	 * @return the IndexType constant instance if value was found, or null
	 *         otherwise.
	 */
	public static IndexType findFromName(final String value) {
		return typeValuesMap.get(value);
	}

	/**
	 * Gets the collection of all index types.
	 *
	 * @return the collection of index types
	 */
	public static Collection<IndexType> values() {
		return typeValuesMap.values();
	}

	/**
	 * Returns string representation of value.
	 *
	 * @return value
	 */
	@Override
	public String toString() {
		return value;
	}

}
