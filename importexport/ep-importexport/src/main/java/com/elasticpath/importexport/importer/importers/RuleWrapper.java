package com.elasticpath.importexport.importer.importers;

import com.elasticpath.domain.rules.Rule;

/**
 * Wraps old Rule and provides access to fresh version of the same Rule instance which has been updated.
 */
public interface RuleWrapper extends Rule {

	/**
	 * @return a copy of this rule which has been updated
	 */
	Rule getUpdatedRule();

	/**
	 * @param rule new rule encapsulated by this wrapper
	 */
	void setUpdatedRule(final Rule rule);

	/**
	 * @return a rule as it was before update
	 */
	Rule getOldRule();

	/**
	 * Takes rule snapshot.
	 */
	void takeSnapshot();
}
