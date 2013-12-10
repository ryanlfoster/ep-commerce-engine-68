/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.RuleBase;

import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.domain.rules.EpRuleBase;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.store.Store;

/**
 * Provides the ability to read rules from the database. Take note that this does not write
 * anything to the database. This rules engine is implemented using Drools Rules 3.0.
 */
public class DBReadingRuleEngineImpl extends AbstractRuleEngineImpl {

	private final Map<String, EpRuleBase> cartRuleBases = new ConcurrentHashMap<String, EpRuleBase>();

	private final SimpleTimeoutCache <String, Boolean> cachedCatalogRuleBaseState = new SimpleTimeoutCache <String, Boolean>(300000);

	private final Map <String, EpRuleBase> catalogRuleBases = new ConcurrentHashMap<String, EpRuleBase>();

	@Override
	protected RuleBase getCartRuleBase(final Store store) {
		final String code = store.getCode();
		EpRuleBase ruleBase;

		if (cartRuleBases.containsKey(code)) {
			ruleBase = cartRuleBases.get(code);

			EpRuleBase newRuleBase;
			if (ruleBase == null) {
				newRuleBase = getRuleService().findRuleBaseByScenario(store, null, RuleScenarios.CART_SCENARIO);
			} else {
				newRuleBase = getRuleService().findChangedStoreRuleBases(code, RuleScenarios.CART_SCENARIO,
						ruleBase.getLastModifiedDate());
			}
			if (newRuleBase != null) {
				ruleBase = newRuleBase;
			}
		} else {
			ruleBase = getRuleService().findRuleBaseByScenario(store, null, RuleScenarios.CART_SCENARIO);
		}
		
		if (ruleBase == null) {
			cartRuleBases.remove(code);
		} else {
			cartRuleBases.put(code, ruleBase);
			return ruleBase.getRuleBase();
		}
		
		// just in case the rule base will be modified externally
		return createRuleBase();
	}

	@Override
	protected RuleBase getCatalogRuleBase(final Store store) {
		final String code = store.getCatalog().getCode();

		Boolean cacheIsValid = cachedCatalogRuleBaseState.get(code);
		EpRuleBase storedRuleBase = catalogRuleBases.get(code);
		if (cacheIsValid == null) {
			storedRuleBase = getValidRuleBase(store, code, storedRuleBase);
			if (storedRuleBase == null) {
				catalogRuleBases.remove(code);
			} else {
				catalogRuleBases.put(code, storedRuleBase);
				cachedCatalogRuleBaseState.put(code, true);
			}
		}

		if (storedRuleBase != null) {
			return storedRuleBase.getRuleBase();
		}

		// just in case the rule base will be modified externally
		return createRuleBase();
	}

	private EpRuleBase getValidRuleBase(final Store store, final String code, final EpRuleBase storedRuleBase) {
		EpRuleBase ruleBase = null;
		if (storedRuleBase == null) {
			ruleBase = getRuleService().findRuleBaseByScenario(null, store.getCatalog(), RuleScenarios.CATALOG_BROWSE_SCENARIO);
		} else {
			ruleBase = getRuleService().findChangedCatalogRuleBases(code, RuleScenarios.CATALOG_BROWSE_SCENARIO,
					storedRuleBase.getLastModifiedDate());
			if (ruleBase == null) {
				ruleBase = storedRuleBase;
			}
		}
		return ruleBase;
	}


	/**
	 * Set timeout for reading updates to catalog rule base.
	 * @param milliseconds milliseconds timeout.
	 */
	public void setCatalogRuleBaseCacheTimeout(final long milliseconds) {
		cachedCatalogRuleBaseState.setTimeout(milliseconds);
	}
}
