package com.elasticpath.tags.service.impl;

import java.math.BigDecimal;

import com.elasticpath.tags.service.DSLValueDecorator;

/**
 * Factory of {@link DSLValueDecorator}s. 
 */
public final class GroovyDSLValueDecoratorFactory {
	
	/**
	 * Get the groovy value type decorator.
	 * @param dataType canonical name of data type.
	 * @param value to decorate
	 * @return particular instance of DSLValueDecorator for given dataType.
	 */
	public static DSLValueDecorator getValueDecorator(final String dataType, final Object value) {
		if (BigDecimal.class.getCanonicalName().equals(dataType)) {
			return new GroovyBigDecimalDecoratorImpl(value);
		} else if (Long.class.getCanonicalName().equals(dataType)) {
			return new GroovyLongDecoratorImpl(value);
		} else if (Float.class.getCanonicalName().equals(dataType)) {
			return new GroovyFloatDecoratorImpl(value);
		} else if (Integer.class.getCanonicalName().equals(dataType)) {
			return new GroovyIntegerDecoratorImpl(value);
		} else if (Boolean.class.getCanonicalName().equals(dataType)) {
			return new GroovyBooleanDecoratorImpl(value);
		}
		return new GroovyStringDecoratorImpl(value);
	}
	
	private GroovyDSLValueDecoratorFactory() {
		//instantiation protection.
	}

}
