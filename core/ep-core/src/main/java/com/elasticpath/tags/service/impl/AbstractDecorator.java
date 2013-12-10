package com.elasticpath.tags.service.impl;

import com.elasticpath.tags.service.DSLValueDecorator;

/**
 * Abstract decorator that allows a tag value to be decorated as required for the DSL
 * conversion by DSL Builder. 
 */
public abstract class AbstractDecorator implements DSLValueDecorator {
	
	private final Object value;

	/**
	 * @return value to decorate
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * Constructor.
	 * @param value value to decorate.
	 */
	public AbstractDecorator(final Object value) {
		if (null == value) {
			throw new IllegalArgumentException("Null value is not allowed");
		}
		this.value = value;
	}
	
}
