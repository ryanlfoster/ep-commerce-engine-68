/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules;

import java.util.Set;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.persistence.api.Persistable;

/**
 * A rule element is a component of a rule such as a condition and an action.
 */

public interface RuleElement extends Persistable {
	/**
	 * Check if this rule element is valid in the specified scenario.
	 *
	 * @param scenarioId the Id of the scenario to check (defined in RuleScenarios)
	 * @return true if the rule element is applicable in the given scenario
	 */
	boolean appliesInScenario(int scenarioId);

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
	 * Must be implemented by subclasses. Returns the <code>RuleElementType</code> associated with this <code>RuleElement</code> subclass. The
	 * <code>RuleElementType</code>'s property key must match this class' discriminator-value and the spring context bean id for this
	 * <code>RuleElement</code> implementation.
	 * 
	 * @return the <code>RuleElementType</code> associated with this <code>RuleElement</code> subclass.
	 */
	RuleElementType getElementType();
	
	/**
	 * Get the kind of this <code>RuleElement</code> (e.g. eligibility, condition, action).
	 *
	 * @return the kind
	 */
	String getKind();

	/**
	 * Set the kind of this <code>RuleElement</code> (e.g. eligibility, condition, action)
	 *
	 * @param kind the kind of the rule element
	 */
	void setKind(final String kind);

	/**
	 * Get the type of action, i.e. cartSubtotalAmountDiscountAction, productAmountDiscountAction and etc. Make sure it matches the bean id in spring
	 * configuration for the implementation of an action.
	 *
	 * @return the action type
	 */
	String getType();

	/**
	 * Set the type of action.
	 *
	 * @param type the type of action
	 */
	void setType(final String type);

	/**
	 * Get the parameters associated with this rule action.
	 *
	 * @return the parameters
	 */
	Set<RuleParameter> getParameters();

	/**
	 * Set the parameters of this rule action.
	 *
	 * @param parameters a set of <code>RuleParameter</code> objects
	 */
	void setParameters(final Set<RuleParameter> parameters);

	/**
	 * Add a parameter of this rule action.
	 *
	 * @param ruleParameter a <code>RuleParameter</code> object
	 */
	void addParameter(final RuleParameter ruleParameter);

	/**
	 * Return the array of the allowed <code>RuleException</code> types for the rule.
	 *
	 * @return an array of <code>RuleExceptionType</code>s of the allowed <code>RuleException</code> types for the rule.
	 */
	RuleExceptionType[] getAllowedExceptions();

	/**
	 * Get the <code>RuleException</code> associated with this <code>RuleCondition</code>.
	 *
	 * @return the set of ruleExceptions
	 */
	Set<RuleException> getExceptions();

	/**
	 * Set the exceptions of this rule condition.
	 *
	 * @param ruleExceptions a set of <code>RuleException</code> objects.
	 */
	void setExceptions(Set<RuleException> ruleExceptions);
	
	/**
	 * Add an exception to this rule element.
	 *
	 * @param ruleException the <code>RuleException</code> object to add
	 */
	void addException(final RuleException ruleException);

	/**
	 * Returns the Drools code corresponding to this <code>RuleCondition</code>.
	 *
	 * @return the drools code
	 * @throws EpDomainException if the <code>RuleCondition</code> is not well formed
	 */
	String getRuleCode() throws EpDomainException;
	
	/**
	 * Set the identifier for the rule that contains this action.
	 * (For traceablility)
	 * @param ruleId the id of the rule containing this action.
	 */
	void setRuleId(final long ruleId);
	
	/**
	 * Returns the value of a parameter with the specified key.
	 * 
	 * @param key The key of the parameter to be returned
	 * @return the value of the parameter with the specified key or "" if no matching parameter was found.
	 */
	String getParamValue(final String key);

}
