package com.elasticpath.service.rules;

/**
 * Extension {@link EpRuleEngine} to include the ability to recompile the rule engine.
 */
public interface RecompilingRuleEngine extends EpRuleEngine {

	/**
	 * Regenerates the rule base using a new set of rules retrieved from the persistence layer. If
	 * the date is not set in the properties files then we set the current date.
	 */
	void recompileRuleBase();
}
