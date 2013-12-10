/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tags.domain.impl;

import java.io.Serializable;

import com.elasticpath.tags.domain.SelectableValue;

/**
 * 
 * Hold a pair of selectable value - name.
 * 
 * @param <VALUE> type of value object.
 */

public class SelectableValueImpl<VALUE> implements Serializable, SelectableValue<VALUE> {
	
	private final VALUE value;
	
	private final String name;
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 20090706L;

	/**
	 * Create a new value - name pair.
	 * 
	 * @param value the first object
	 * @param name the second object
	 */	
	public SelectableValueImpl(final VALUE value, final String name) {
		super();
		this.value = value;
		this.name = name;
	}
	
	/** 
	 * @return Value of value-name pair.
	 */
	public VALUE getValue() {
		return value;
	}

	/**
	 * @return Name of value-name pair.
	 */
	public String getName() {
		return name;
	}


}
