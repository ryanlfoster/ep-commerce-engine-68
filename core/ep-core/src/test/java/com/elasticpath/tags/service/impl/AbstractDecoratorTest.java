package com.elasticpath.tags.service.impl;

import org.junit.Test;

/**
 * AbstractDecorator test.
 */
public class AbstractDecoratorTest {
	
	/**
	 * Test that AbstractDecorator will not work with null values.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAbstractDecoratorNullValue() {
		new AbstractDecorator(null) {
			public String decorate() {
				return null;
			}
		};
	}

}
