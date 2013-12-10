package com.elasticpath.validation.service.valang;

import org.springmodules.validation.valang.functions.Function;

import com.elasticpath.tags.domain.Condition;

/**
 * Function that checks the type of data.
 * 
 * Usage:
 * isValidConditionType(condition)
 * 
 * Example:
 * { condition : isValidConditionType(this) is true : 'value does not match type' }
 * 
 * 
 */
public class TagConditionTypeOfFunction extends AbstractValangFunction {
	
	private static final int CONDITION_ARG = 0;
	
	/** function name. */
	public static final String FUNCTION_NAME = "isValidConditionType";

	/**
	 * Constructor for testing only.
	 */
	TagConditionTypeOfFunction() {
		super(new Function[1], 0, 0);
	}
	
	/**
	 * default constructor.
	 * @param arguments the arguments to the function
	 * @param line the line in rule where function appears
	 * @param column the column in rule where function appears
	 */
	public TagConditionTypeOfFunction(final Function[] arguments, final int line, final int column) {
		super(arguments, line, column);
		definedExactNumberOfArguments(1);
	}

	@Override
	protected Boolean doGetResult(final Object target) throws Exception {
		final Object theCondition = getFunctionArgument(CONDITION_ARG, target);

		if (theCondition instanceof Condition) {
			
			final Condition tagCondition = (Condition) theCondition;
			
			// need to make sure we do not get a NullPointer here, maybe need to think how to make it nicer.
			return tagCondition.getTagDefinition() != null 
				&& tagCondition.getTagDefinition().getValueType() != null
				&& tagCondition.getTagDefinition().getValueType().getJavaType() != null
				&& tagCondition.getTagValue() != null
				&& (tagCondition.getTagDefinition().getValueType().getJavaType().
						equals(tagCondition.getTagValue().getClass().getName()));
		}
		return Boolean.FALSE;
	}

}
