package com.elasticpath.service.catalog.impl;

import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.domain.catalog.impl.SelectionRuleImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.service.catalog.SelectionRuleFactory;

/**
 * A factory to create {@link SelectionRule}s.
 */
public class SelectionRuleFactoryImpl implements SelectionRuleFactory {

	@Override
	public SelectionRule createSelectAllRule() {
		return createSelectionRule(0);
	}
	
	@Override
	public SelectionRule createSelectNRule(final int quantity) {
		return createSelectionRule(quantity);
	}

	private SelectionRule createSelectionRule(final int quantity) {
		final SelectionRuleImpl selectionRuleImpl = new SelectionRuleImpl(quantity);
		selectionRuleImpl.setGuid(new RandomGuidImpl().toString());
		return selectionRuleImpl;
	}

}
