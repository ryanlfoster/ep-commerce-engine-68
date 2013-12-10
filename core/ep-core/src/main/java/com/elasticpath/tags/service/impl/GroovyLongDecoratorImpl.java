package com.elasticpath.tags.service.impl;

/**
 * 
 * Long decorator.
 *
 */
public class GroovyLongDecoratorImpl extends AbstractGroovyNumberDecorator {

	/**
	 * Constructor. 
	 * @param value given value.
	 */
	GroovyLongDecoratorImpl(final Object value) {
		super(value);
	}

	@Override
	protected String getGroovyTypeSuffix() {
		return "L";
	}


}
