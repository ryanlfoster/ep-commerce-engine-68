/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules;

import java.util.Set;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.persistence.api.Persistable;

/**
 * Represents an exception that can be associated with a rule element (i.e. rule action/condition)
 */
public interface RuleException extends Persistable {

	/**
	 * Checks that the rule set domain model is well formed. For example, rule conditions must have all required parameters specified.
	 *
	 * @throws EpDomainException if the structure is not correct.
	 */
	void validate() throws EpDomainException;

	/**
	 * Return the array of the required parameter keys for the rule.
	 *
	 * @return an array of String of the required parameter keys for the rule.
	 */
	String[] getParameterKeys();
	
	/**
	 * Must be implemented by subclasses. Returns the <code>RuleExceptionType</code> associated with this <code>RuleException</code> subclass. The
	 * <code>RuleExceptionType</code>'s property key must match this class' discriminator-value and the spring context bean id for this
	 * <code>RuleException</code> implementation.
	 * 
	 * @return the <code>RuleExceptionType</code> associated with this <code>RuleException</code> subclass.
	 */
	RuleExceptionType getExceptionType();

	/**
	 * Get the type of exception, i.e. skuException, productException and etc. Make sure it matches the bean id in spring
	 * configuration for the implementation of an exception.
	 *
	 * @return the exception type
	 */
	String getType();

	/**
	 * Set the type of exception.
	 *
	 * @param type the type of exception
	 */
	void setType(final String type);

	/**
	 * Get the parameters associated with this rule exception.
	 *
	 * @return the parameters
	 */
	Set<RuleParameter> getParameters();

	/**
	 * Set the parameters of this rule exception.
	 *
	 * @param parameters a set of <code>RuleParameter</code> objects
	 */
	void setParameters(final Set<RuleParameter> parameters);

	/**
	 * Add a parameter of this rule exception.
	 *
	 * @param ruleParameter a <code>RuleParameter</code> object
	 */
	void addParameter(final RuleParameter ruleParameter);

	/**
	 * Check if this rule element is valid in the specified scenario.
	 *
	 * @param scenarioId the Id of the scenario to check (defined in RuleScenarios)
	 * @return true if the rule element is applicable in the given scenario
	 */
	boolean appliesInScenario(int scenarioId);

	/**
	 * Returns the value of a parameter with the specified key.
	 *
	 * @param key The key of the parameter to be returned
	 * @return the value of the parameter with the specified key or "" if no matching parameter was found.
	 */
	String getParamValue(final String key);
}