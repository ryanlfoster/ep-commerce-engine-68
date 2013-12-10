package com.elasticpath.service.catalog;

import com.elasticpath.domain.catalog.SelectionRule;

/**
 * A factory to create {@link SelectionRule}s.
 */
public interface SelectionRuleFactory {

	/**
	 * Creates a SELECT_ALL rule.
	 * SELECT_ALL expects all constituents to be selected.
	 * @return a {@link SelectionRule} for SELECT_ALL
	 */
	SelectionRule createSelectAllRule();

	/**
	 * Creates a SELECT_N rule.
	 * SELECT_N expects n constituents out of all bundle constituents to be selected.
	 * 
	 * @param quantity the quantity
	 * @return a {@link SelectionRule} for SELECT_MULTIPLE
	 */
	SelectionRule createSelectNRule(int quantity);

}