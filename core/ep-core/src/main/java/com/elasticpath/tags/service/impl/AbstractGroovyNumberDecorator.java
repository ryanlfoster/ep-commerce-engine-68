package com.elasticpath.tags.service.impl;

/**
 * Abstract number decorator.
 */
public abstract class AbstractGroovyNumberDecorator extends AbstractDecorator {
	
	/**
	 * Constructor.
	 * @param value value to decorate.
	 */
	public AbstractGroovyNumberDecorator(final Object value) {
		super(value);
	}

	/**
	 * Get the type identifier. See http://groovy.codehaus.org/Groovy+Math
	 * @return type identifier.
	 */
	protected abstract String getGroovyTypeSuffix(); 

	@Override
	public final String decorate() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append('(');
		stringBuilder.append(getValueAsString());
		stringBuilder.append(getGroovyTypeSuffix());
		stringBuilder.append(')');
		return stringBuilder.toString();
	}
	
	/**
	 * @return value converted to string.
	 */
	protected String getValueAsString() {
		return getValue().toString();
	}

}
