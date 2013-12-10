package com.elasticpath.tags.service.impl;

/**
 * 
 * BigDecimal decorator.
 *
 */
public class GroovyBigDecimalDecoratorImpl extends AbstractGroovyNumberDecorator {

	/**
	 * Constructor. 
	 * @param value given value.
	 */
	GroovyBigDecimalDecoratorImpl(final Object value) {
		super(value);
	}

	@Override
	protected String getGroovyTypeSuffix() {
		return "G";
	}

	/**
	 * When converting to decimal need to explicitly to check for floating point
	 * since Groovy will treat it as BigInteger rather than BigDecimal. 
	 * @return a big decimal representation as string
	 */
	@Override
	protected String getValueAsString() {
		final String stringValue = super.getValueAsString();
		if (!stringValue.contains(".")) {
			return stringValue + ".0";
		}
		return stringValue;
	}
	
	


}
