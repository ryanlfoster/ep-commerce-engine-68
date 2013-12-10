package com.elasticpath.tags.service.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * Test for GroovyDSLValueDecoratorFactory.
 *
 */
public class GroovyDSLValueDecoratorFactoryTest  {
	
	/**
	 * Test that method returns valid decorators for provided class name.
	 */
	@Test
	public void testGroovyDSLValueDecoratorFactory() {
		
		assertTrue(GroovyDSLValueDecoratorFactory.getValueDecorator("java.lang.String", "") 
				instanceof 
				GroovyStringDecoratorImpl);
		
		assertTrue(GroovyDSLValueDecoratorFactory.getValueDecorator("java.math.BigDecimal", java.math.BigDecimal.ONE) 
				instanceof 
				GroovyBigDecimalDecoratorImpl);
		
		assertTrue(GroovyDSLValueDecoratorFactory.getValueDecorator("java.lang.Long", 0L) 
				instanceof 
				GroovyLongDecoratorImpl);
		
		assertTrue(GroovyDSLValueDecoratorFactory.getValueDecorator("java.lang.Integer", 0) 
				instanceof 
				GroovyIntegerDecoratorImpl);
		
		assertTrue(GroovyDSLValueDecoratorFactory.getValueDecorator("java.lang.Float", .0f) 
				instanceof 
				GroovyFloatDecoratorImpl);
		
		assertTrue(GroovyDSLValueDecoratorFactory.getValueDecorator("com.elasticpath.SomeClassDecribedInRumba52", 0) 
				instanceof 
				GroovyStringDecoratorImpl);
		
		assertTrue(GroovyDSLValueDecoratorFactory.getValueDecorator(null, 0) 
				instanceof 
				GroovyStringDecoratorImpl);

		assertTrue(GroovyDSLValueDecoratorFactory.getValueDecorator("java.lang.Boolean", 0) 
				instanceof 
				GroovyBooleanDecoratorImpl);
		
		
	}
	
	

}
