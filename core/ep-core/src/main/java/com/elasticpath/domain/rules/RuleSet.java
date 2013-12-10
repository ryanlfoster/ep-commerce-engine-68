/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.Entity;

import java.util.Date;
import java.util.Set;

/**
 * Represents a set of rules.
 */
public interface RuleSet extends Entity {

	/**
	 * Discount query name used in the rule engine.
	 */
	String QUERY_NAME = "query_discount";

	/**
	 * Discount name used in the discount query.
	 */
	String DISCOUNT_NAME = "discount";
	
	/**
	 * Get the name of this rule set.
	 * 
	 * @return the name of this rule set.
	 */
	String getName();

	/**
	 * Set the name of this rule set.
	 * 
	 * @param name the name of this rule set
	 */
	void setName(final String name);

	/**
	 * Get the id of the scenario that this rule set applies to.
	 * 
	 * @return the scenario id <code>(constant in RuleScenarios)</code>
	 */
	int getScenario();

	/**
	 * Set the scenario that this rule set applies to.
	 * 
	 * @param scenarioId a constant in <code>RuleScenarios</code>.
	 */
	void setScenario(int scenarioId);

	/**
	 * Get the rules in this rule set.
	 * 
	 * @return the rules
	 */
	Set<Rule> getRules();

	/**
	 * Set the rules in this rule set.
	 * 
	 * @param rules a set of <code>Rule</code> objects
	 */
	void setRules(final Set<Rule> rules);

	/**
	 * Returns the Drools code corresponding to this rule set filtered by store/catalog where applicable.
	 * 
	 * @param store the store this rule has to be applied
	 * @return the rule code.
	 * @throws EpDomainException if the rule set is not well formed
	 */
	String getRuleCode(Store store) throws EpDomainException;

	/**
	 * Adds a rule to the rule set.
	 * 
	 * @param rule the rule to add
	 */
	void addRule(final Rule rule);

	/**
	 * Get the names of classes imported by this rule set.
	 * 
	 * @see addImport
	 * @return the set of imports (fully qualified class names)
	 */
	Set<String> getImports();

	/**
	 * Checks that the rule set domain model is well formed. For example, rule conditions must have all required parameters specified.
	 * 
	 * @throws EpDomainException if the structure is not correct.
	 */
	void validate() throws EpDomainException;
	
	/**
	 * Returns the date when the rule set was last modified.
	 *
	 * @return the date when the rule set was last modified
	 */
	Date getLastModifiedDate();

	/**
	 * Set the date when the rule set was last modified.
	 *
	 * @param lastModifiedDate the date when the rule set was last modified
	 */
	void setLastModifiedDate(Date lastModifiedDate);
}
