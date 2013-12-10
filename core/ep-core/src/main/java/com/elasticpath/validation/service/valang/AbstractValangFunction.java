package com.elasticpath.validation.service.valang;

import org.springframework.beans.BeanWrapper;
import org.springmodules.validation.valang.functions.AbstractFunction;
import org.springmodules.validation.valang.functions.Function;

/**
 * Abstract Valang function. Provides a simple mechanism of getting the
 * arguments and workaround of referencing the current bean by 
 * using "this" keyword as an argument to custom function
 */
public abstract class AbstractValangFunction extends AbstractFunction {
	
	/**
	 * default constructor.
	 * @param arguments the arguments to the function
	 * @param line the line in rule where function appears
	 * @param column the column in rule where function appears
	 */
	public AbstractValangFunction(final Function[] arguments, final int line, final int column) {
		super(arguments, line, column);
	}
	
	@Override
	protected abstract Object doGetResult(Object target) throws Exception;


	/**
	 * @param argumentIndex the index of the argument to the function (starts at 0)
	 * @param target the target object provided by valang when processing the constraint 
	 * 	      (comes as argument to doGetResult(Object target)
	 * @return argument of the function at the specified index
	 */
	Object getFunctionArgument(final int argumentIndex, final Object target) {
		
		// workaround of how to get to the current bean.
		// if to use "this" keyword as an argument it returns the reference
		// to the BeanWrapper that hold the current bean that is being validated
		final Object theObject = getArguments()[argumentIndex].getResult(target);

		if (theObject instanceof BeanWrapper) {
			return ((BeanWrapper) theObject).getWrappedInstance();
		} 
		return theObject;
	}

}
