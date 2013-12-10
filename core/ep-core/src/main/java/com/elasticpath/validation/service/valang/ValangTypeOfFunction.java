package com.elasticpath.validation.service.valang;

import org.springmodules.validation.valang.functions.Function;

/**
 * Function that checks the type of data.
 * 
 * Usage:
 * typeof(value, className)
 * 
 * Example:
 * { bean : typeof(beanProperty, 'java.lang.Integer') is true : 'value must be integer' }
 * 
 * 
 */
public class ValangTypeOfFunction extends AbstractValangFunction {
	
	private static final int JAVA_TYPE_ARG = 1;
	private static final int VALUE_ARG = 0;
	
	/** function name. */
	public static final String FUNCTION_NAME = "typeof";

	/**
	 * Constructor for testing only.
	 */
	ValangTypeOfFunction() {
		super(new Function[2], 0, 0);
	}
	
	/**
	 * default constructor.
	 * @param arguments the arguments to the function
	 * @param line the line in rule where function appears
	 * @param column the column in rule where function appears
	 */
	public ValangTypeOfFunction(final Function[] arguments, final int line, final int column) {
		super(arguments, line, column);
		definedExactNumberOfArguments(2);
	}

	@Override
	protected Boolean doGetResult(final Object target) throws Exception {
		final Object theValue = getFunctionArgument(VALUE_ARG, target);
		final Object theType = getFunctionArgument(JAVA_TYPE_ARG, target);
		return theType != null && theValue != null && theType.equals(theValue.getClass().getName());
	}

}
