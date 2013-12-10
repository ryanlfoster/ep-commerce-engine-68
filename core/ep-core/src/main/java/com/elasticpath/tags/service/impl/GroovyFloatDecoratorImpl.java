package com.elasticpath.tags.service.impl;

/**
 * Float decorator.
 */
public class GroovyFloatDecoratorImpl extends AbstractGroovyNumberDecorator {

	/**
	 * Constructor. 
	 * @param value given value.
	 */
	GroovyFloatDecoratorImpl(final Object value) {
		super(value);
	}

	@Override
	protected String getGroovyTypeSuffix() {
		return "F";
	}
	
	

}
