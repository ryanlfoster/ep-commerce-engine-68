package com.elasticpath.service.contentspace;

import java.util.Map;

/**
 * Abstracts a script engine for evaluating expressions.
 */
public interface ScriptEngine {

	/**
	 * Initializes the engine with a context of parameters and their values.
	 * 
	 * @param dynamicContext the dynamic context
	 * @param scriptInitSection multiline string with initial instructions. Can be null
	 */
	void initialize(Map<String, Object> dynamicContext, String scriptInitSection);

	/**
	 * Evaluates an expression.
	 * 
	 * @param expression the expression
	 * @return the result of the expression. Could be null.
	 */
	Object evaluateExpression(String expression);

}
