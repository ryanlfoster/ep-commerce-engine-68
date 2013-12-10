/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.exception;


/**
 * This exception will be thrown in case any errors happen when binding a 
 * row, but one field requires other fields to be present.
 */
public class EpRequiredDependentFieldsMissingBindException extends EpBindException {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/** The field with the dependency. */
	private final String dependentField;
	
	/** The value of the dependent field that caused the requirement. */
	private final String fieldValue;

	/** The fields that are required. */
	private final String [] requiredFields;

	
	/**
	 * Create a new instance for the specified field with a default message.
	 * @param dependentField the field with the dependency
	 * @param fieldValue the value of the dependent field that caused the dependency
	 * @param requiredFields the fields that must be provided for the dependent fields
	 */
	public EpRequiredDependentFieldsMissingBindException(final String dependentField, final String fieldValue, final String ... requiredFields) {
		super("Required DependentField missing");
		this.dependentField = dependentField;
		this.fieldValue = fieldValue;
		this.requiredFields = requiredFields;
	}

	/**
	 * @return the field with the dependency.
	 */
	public String getDependentField() {
		return dependentField;
	}

	/**
	 * @return the fields that are required for the dependent field.
	 */
	public String [] getRequiredFields() {
		String [] result = new String [requiredFields.length];
		System.arraycopy(requiredFields, 0, result, 0, requiredFields.length);
		return result;
	}

	/**
	 * @return the value of the dependent field that caused the requirement.
	 */
	public String getFieldValue() {
		return fieldValue;
	}
	
	
}
