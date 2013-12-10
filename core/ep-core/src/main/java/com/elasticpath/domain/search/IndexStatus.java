package com.elasticpath.domain.search;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;

/**
 * The Type-Safe Enum for Index Status.
 * <br><code>IndexStatus</code> was designed for future subclassing.
 * 
 * <p>
 * An example for subclass follows:
 * <pre>
 *     package my.package;          
 *     public class MyIndexStatus extends IndexStatus {
 *     
 *         public static final MyIndexStatus NEW_STATUS = new MyIndexStatus("MY_NEW_INDEX_STATUS");
 *         
 *         ...
 *
 *         protected MyIndexStatus(final String value) {
 *             super(value);
 *         }
 *     }
 * </pre>
 * </p>
 */
public class IndexStatus implements Serializable {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/*
	 * The declaration of values map should be placed before any IndexStatus constant.
	 */
	private static Map<String, IndexStatus> statusValuesMap = new HashMap<String, IndexStatus>();
	
	/** Status Complete. */
	public static final IndexStatus COMPLETE = new IndexStatus("COMPLETE");

	/** Status Rebuild in Progress. */
	public static final IndexStatus REBUILD_IN_PROGRESS = new IndexStatus("REBUILD_IN_PROGRESS");

	/** Status Update in Progress. */
	public static final IndexStatus UPDATE_IN_PROGRESS = new IndexStatus("UPDATE_IN_PROGRESS");

	/** Status Missing. */
	public static final IndexStatus MISSING = new IndexStatus("MISSING");

	/*
	 * The Value Code which is saved in Index Status.
	 */
	private final String value;

	/**
	 * The constructor.
	 * <br> Saves the value and registers itself in statusValuesMap (a static Map).
	 * <br> Subclasses will invoke this constructor and register themselves automatically.
	 * <br> IndexStatus constructor is protected. This at least protects the class from 
	 * <br> having anyone add a new Status and forces subclassing of IndexStatus before 
	 * <br> adding a new Status.
	 *
	 * @param value the string representation of IndexStatus
	 * @throws IllegalArgumentException if value is null or exist in statusValuesMap, to prevent duplication of value code in subclasses.
	 */
	protected IndexStatus(final String value) {
		if (value == null) {
			throw new IllegalArgumentException("Value of IndexStatus shuld not be null");
		}
		
		this.value = value;

		if (statusValuesMap.containsKey(value)) {
			throw new IllegalArgumentException("Duplicate IndexStatus value found : " + value);
		}

		statusValuesMap.put(value, this);
	}

	/**
	 * This implementation is simple and final for preventing overriding in subclass.
	 * Two IndexStatus instances are equal if their values are equal.
	 * {@inheritDoc}
	 * 
	 * @see Object#equals(Object)
	 */
	@Override
	public final boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof IndexStatus)) {
			return false;
		}
		
		IndexStatus status = (IndexStatus) obj;
		return ObjectUtils.equals(value, status.value);
	}

	/**
	 * This implementation is simple and final for preventing overriding in subclass.
	 * {@inheritDoc}
	 * 
	 * @see Object#hashCode()
	 */
	@Override
	public final int hashCode() {
		return ObjectUtils.hashCode(value);
	}

	/**
	 * Gets the value.
	 *
	 * @return the value.
	 */
	public final String getValue() {
		return value;
	}

	/**
     * Returns the IndexStatus constant corresponding to specified value.
     * <br> Subclasses can use this method for theirs values, 
     * <br> because every IndexStatus constant registers itself in values Map.
     *
	 * @param value the IndexStatus value
	 * @return the IndexStatus constant instance if value was found, or null otherwise.
	 */
	public static IndexStatus valueOf(final String value) {
		return statusValuesMap.get(value);
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
