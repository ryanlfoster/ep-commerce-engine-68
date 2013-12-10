/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.search.query;

import java.io.Serializable;

/**
 * A search hint is an addition to a search that helps retrieve 
 * better results or convey some information to the search process.
 * @param <T> the type of the object for this search hint
 */
public class SearchHint<T> implements Serializable {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	private String hintId;
	
	private T value;

	
	/**
	 * Constructor.
	 * 
	 * @param hintId the hint ID
	 * @param value the value
	 */
	public SearchHint(final String hintId, final T value) {
		super();
		this.hintId = hintId;
		this.value = value;
	}

	/**
	 * Gets the value for this hint.
	 * 
	 * @return the value the value object
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Sets the value object.
	 * 
	 * @param value the value to set
	 */
	public void setValue(final T value) {
		this.value = value;
	}

	/**
	 * Gets the hint ID.
	 * 
	 * @return the hintId
	 */
	public String getHintId() {
		return hintId;
	}

	/**
	 * Sets the hint ID.
	 * 
	 * @param hintId the hintId to set
	 */
	public void setHintId(final String hintId) {
		this.hintId = hintId;
	}
	
}
