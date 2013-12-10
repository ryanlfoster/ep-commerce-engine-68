package com.elasticpath.domain.rules;

import java.util.Date;

import org.drools.RuleBase;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.Persistable;

/**
 * Rule base container for the purpose of serializing the rule base to the database.
 */
public interface EpRuleBase extends Persistable {

	/**
	 * Gets the scenario ID this rule base applies in.
	 *
	 * @return the scenario ID this rule base applies in
	 */
	int getScenarioId();
	
	/**
	 * Sets the scenario ID this rule base applies in.
	 *
	 * @param ruleScenarioId the scenario ID this rule base applies in
	 */
	void setScenarioId(int ruleScenarioId);

	/**
	 * Gets the store this rule base belongs to.
	 *
	 * @return the store this rule base belongs to
	 */
	Store getStore();
	
	/**
	 * Sets the store this rule base belongs to.
	 *
	 * @param store the store this rule base belongs to
	 */
	void setStore(Store store);
	
	/**
	 * Gets the catalog this rule base belongs to.
	 *
	 * @return the catalog this rule base belongs to
	 */
	Catalog getCatalog();
	
	/**
	 * Sets the catalog this rule base belongs to.
	 *
	 * @param catalog  the catalog this rule base belongs to
	 */
	void setCatalog(Catalog catalog);
	
	/**
	 * Gets the compiled rule base.
	 *
	 * @return the compiled rule base
	 */
	RuleBase getRuleBase();
	
	/**
	 * Sets the compiled rule base.
	 *
	 * @param ruleBase the compiled rule base
	 */
	void setRuleBase(RuleBase ruleBase);
	
	/**
	 * Returns the date when the rule base was last modified.
	 * 
	 * @return the date when the rule base was last modified
	 */
	Date getLastModifiedDate();
}
