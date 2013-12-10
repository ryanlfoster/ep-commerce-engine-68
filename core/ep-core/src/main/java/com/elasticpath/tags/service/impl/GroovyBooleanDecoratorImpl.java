package com.elasticpath.tags.service.impl;

/**
 * 
 */
public class GroovyBooleanDecoratorImpl extends AbstractDecorator {

	/**
	 * Constructor.
	 * @param value given value for decoration.
	 */
	public GroovyBooleanDecoratorImpl(final Object value) {
		super(value);
	}
	
	@Override
	public String decorate() {
		if (Boolean.valueOf(getValue().toString())) {
			return Boolean.TRUE.toString();
		}
		return Boolean.FALSE.toString();
	}
	
}
