package com.elasticpath.tags.service;

/**
 * Interface that allows a tag value to be decorated as required for the DSL
 * conversion by DSL Builder. 
 */
public interface DSLValueDecorator {
	
	/**
	 * Decorate given value to string.
	 * @return decorated value.
	 */
	String decorate();

}
