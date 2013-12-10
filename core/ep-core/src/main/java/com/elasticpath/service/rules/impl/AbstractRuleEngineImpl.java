package com.elasticpath.service.rules.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.drools.QueryResult;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.impl.EnvironmentFactory;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.SimpleCache;
import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.discounts.Discount;
import com.elasticpath.domain.discounts.DiscountItemContainer;
import com.elasticpath.domain.discounts.ShoppingCartDiscountItemContainer;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.rules.impl.ActiveRuleImpl;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.rules.EpRuleEngine;
import com.elasticpath.service.rules.PromotionRuleDelegate;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.domain.TagDictionary;
import com.elasticpath.tags.service.ConditionEvaluatorService;

/**
 * Abstract class for exposing common methods for all rule engines.
 */
public abstract class AbstractRuleEngineImpl extends AbstractEpServiceImpl implements EpRuleEngine {

	/**
	 * Key used to put the rules into the Shopper.
	 */
	public static final String RULE_IDS_KEY = "RULE_IDS_KEY"; 

	private RuleService ruleService;

	private RuleEngineDataStrategy dataStrategy;

	private PromotionRuleDelegate promotionRuleDelegate;

	private static final Logger LOG = Logger.getLogger(AbstractRuleEngineImpl.class);

	private static final int DEFAULT_CACHE_TIMEOUT = 300000;

	private static final String SESSION_CONFIGURATION_ID = "SESSION_CONFIGURATION_ID";

	private static SimpleTimeoutCache<String, SessionConfiguration> statefulSessionConfiguration = 
		new SimpleTimeoutCache<String, SessionConfiguration>(DEFAULT_CACHE_TIMEOUT);

	@Override
	public void fireCatalogPromotionRules(final Collection<? extends Product> products, final Set<Long> ruleTracker, final Currency activeCurrency,
			final Store store, final Map<String, List<Price>> prices) {
		if (store == null) {
			throw new IllegalArgumentException("Store must not be null");
		}

		if (ruleTracker == null) {
			throw new IllegalArgumentException("RuleTracker must not be null");
		}

		if (products.isEmpty()) {
			return;
		}

		SessionConfiguration sessionConfiguration = statefulSessionConfiguration.get(SESSION_CONFIGURATION_ID);
		if (sessionConfiguration == null) {
			sessionConfiguration = new SessionConfiguration();
			statefulSessionConfiguration.put(SESSION_CONFIGURATION_ID, sessionConfiguration);
		}

		StatefulSession workingMemory = getCatalogRuleBase(store).newStatefulSession(sessionConfiguration, EnvironmentFactory.newEnvironment());

		try {
			assertObject(workingMemory, promotionRuleDelegate);
			assertObject(workingMemory, activeCurrency);
			assertObject(workingMemory, ruleTracker);

			assertObject(workingMemory, prices);

			for (Product product : products) {
				assertObject(workingMemory, product);
			}

			workingMemory.setFocus(RuleAction.DEFAULT_AGENDA_GROUP);
			workingMemory.fireAllRules();
		} finally {
			workingMemory.dispose();
		}
	}

	/**
	 * Executes order promotion rules on the specified shopping cart. Only rules affecting cart item discounts are fired by this method. To fire cart
	 * subtotal discount rules call fireOrderPromotionSubtotalRules.
	 * 
	 * @param shoppingCart the cart to which promotion rules are to be applied.
	 */
	public void fireOrderPromotionRules(final ShoppingCart shoppingCart) {
		firePromotionRulesForGroup(shoppingCart, RuleAction.DEFAULT_AGENDA_GROUP);
	}

	/**
	 * Executes order promotion rules on the specified shopping cart. Only rules affecting cart subtotal discounts are fired by this method. To fire
	 * cart item discount rules call fireOrderPromotionRules.
	 * 
	 * @param shoppingCart the cart to which promotion rules are to be applied.
	 */
	public void fireOrderPromotionSubtotalRules(final ShoppingCart shoppingCart) {
		firePromotionRulesForGroup(shoppingCart, RuleAction.SUBTOTAL_DEPENDENT_AGENDA_GROUP);
	}

	private void firePromotionRulesForGroup(final ShoppingCart shoppingCart, final String agendaGroup) {
		if (shoppingCart == null) {
			throw new IllegalArgumentException("Shopping cart cannot be null");
		}

		final List<Long> uidPks = evaluateApplicableRules(shoppingCart);

		// check if the shopping cart has a store set
		if (shoppingCart.getStore() != null && CollectionUtils.isNotEmpty(uidPks)) {

			SessionConfiguration sessionConfiguration = statefulSessionConfiguration.get(SESSION_CONFIGURATION_ID);
			if (sessionConfiguration == null) {
				sessionConfiguration = new SessionConfiguration();
				statefulSessionConfiguration.put(SESSION_CONFIGURATION_ID, sessionConfiguration);
			}

			StatefulSession workingMemory = getCartRuleBase(shoppingCart.getStore()).newStatefulSession(sessionConfiguration,
					EnvironmentFactory.newEnvironment());

			try {
				assertObject(workingMemory, promotionRuleDelegate);
				assertObject(workingMemory, shoppingCart);
				for (long ruleId : uidPks) {
					assertObject(workingMemory, new ActiveRuleImpl(ruleId));
				}
				workingMemory.setFocus(agendaGroup);
				workingMemory.fireAllRules();
				QueryResults queryResults = workingMemory.getQueryResults(RuleSet.QUERY_NAME);
				applyDiscount(queryResults, createShoppingCartDiscountItemContainer(shoppingCart));
			} catch (IllegalArgumentException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Rule Engine could not get query results", e);
				}
			} finally {
				workingMemory.dispose();
			}
		}
	}

	/**
	 * Uses groovy engine to fire all who conditions that are applicable to rules.
	 * 
	 * @param shoppingCart the cart
	 * @return list of uidPks of rules that are applicable.
	 */
	private List<Long> evaluateApplicableRules(final ShoppingCart shoppingCart) {

		final Shopper shopper = shoppingCart.getShopper();
		final SimpleCache simpleCache = shopper.getCache();

		if (!simpleCache.isInvalidated(RULE_IDS_KEY)) {
			return shopper.getCache().getItem(RULE_IDS_KEY);
		}
		
		final List<Object[]> sellingContextsWithRuleUidPks = getSellingContextsForRules();
		final ConditionEvaluatorService service = getBean(ContextIdNames.TAG_CONDITION_EVALUATOR_SERVICE);

		final List<Long> ruleIds = new ArrayList<Long>();
		TagSet tagSet = shopper.getTagSet();
		for (Object[] obj : sellingContextsWithRuleUidPks) {
			final SellingContext scxt = (SellingContext) obj[1];
			if (scxt == null
					|| scxt.isSatisfied(service, tagSet, TagDictionary.DICTIONARY_PROMOTIONS_SHOPPER_GUID, TagDictionary.DICTIONARY_TIME_GUID)) {
				ruleIds.add((Long) obj[0]);
			}
		}

		shopper.getCache().putItem(RULE_IDS_KEY, ruleIds);

		return ruleIds;
	}
	
	/**
	 * Method to extract selling context associated with rule and the rule uidPk.
	 * 
	 * @return object[sellingContext, ruleUidPk].
	 */
	protected List<Object[]> getSellingContextsForRules() {
		List<Object[]> result = getDataStrategy().findRuleIdSellingContextByScenario(RuleScenarios.CART_SCENARIO);
		if (result == null) {
			return Collections.emptyList();
		}
		return result;
	}

	/**
	 * Apply the discount. Default behaviour is to apply all.
	 * 
	 * @param queryResults query result that consist of discount objects in working memory.
	 * @param discountItemContainer the discountItemContainer to apply discount.
	 */
	protected void applyDiscount(final QueryResults queryResults, final DiscountItemContainer discountItemContainer) {
		if (queryResults == null) {
			return;
		}
		for (Iterator<QueryResult> it = queryResults.iterator(); it.hasNext();) {
			Discount discount = (Discount) it.next().get(RuleSet.DISCOUNT_NAME);
			discount.apply(discountItemContainer);
		}
	}

	/**
	 * Get the promotion receiver.
	 * 
	 * @param shoppingCart the shopping cart that receives the promotion
	 * @return promotion receiver.
	 */
	private DiscountItemContainer createShoppingCartDiscountItemContainer(final ShoppingCart shoppingCart) {
		ShoppingCartDiscountItemContainer discountItemContainer = getBean(ContextIdNames.SHOPPING_CART_DISCOUNT_ITEM_CONTAINER);
		discountItemContainer.setShoppingCart(shoppingCart);
		return discountItemContainer;
	}

	/**
	 * Creates a new Drools <code>RuleBase</code> which will be based on the configuration returned
	 * {@link #createRuleConfiguration()}.
	 *
	 * @return the newly created Drools rulebase
	 */
	protected RuleBase createRuleBase() {
		return RuleBaseFactory.newRuleBase(createRuleConfiguration());
	}

	/**
	 * Creates a new Drools <code>RuleConfiguration</code> which will be based on the class loader of this class.
	 * 
	 * In an OSGi environment this means that Drools will have access to all classes in core. In
	 * a web application environment this will mean that Drools has access to all classes available
	 * to the web application class loader.
	 *
	 * @return the newly created Drools <code>RuleConfiguration</code>
	 */
	protected RuleBaseConfiguration createRuleConfiguration() {
		return new RuleBaseConfiguration(getClass().getClassLoader());
	}

	/**
	 * Gets the catalog {@link RuleBase} associated with the given {@link Store} s catalog.
	 * 
	 * @param store the store to get the catalog from
	 * @return a {@link RuleBase} for the given {@link Store}
	 */
	protected abstract RuleBase getCatalogRuleBase(Store store);

	/**
	 * Gets the cart {@link RuleBase} associated with the given {@link Store}.
	 * 
	 * @param store the store to get the rule base for
	 * @return a {@link RuleBase} for the given {@link Store}
	 */
	protected abstract RuleBase getCartRuleBase(Store store);

	/**
	 * Assert the given object into the given working memory.
	 * <p>
	 * Note: don't try to do refactoring by removing this method. This method is put here to make profiling easier.
	 * 
	 * @param workingMemory the working memory
	 * @param object the object
	 */
	private void assertObject(final WorkingMemory workingMemory, final Object object) {
		workingMemory.insert(object);
	}

	/**
	 * Sets the {@link PromotionRuleDelegate} instance to use.
	 * 
	 * @param promotionRuleDelegate the {@link PromotionRuleDelegate} instance to use
	 */
	public void setPromotionRuleDelegate(final PromotionRuleDelegate promotionRuleDelegate) {
		this.promotionRuleDelegate = promotionRuleDelegate;
	}

	/**
	 * @return the rule service
	 */
	protected RuleService getRuleService() {
		return ruleService;
	}

	/**
	 * Sets the {@link RuleService} instance to use.
	 * 
	 * @param ruleService the {@link RuleService} instance to use
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	/**
	 * @param dataStrategy the dataStrategy to set
	 */
	public void setDataStrategy(final RuleEngineDataStrategy dataStrategy) {
		this.dataStrategy = dataStrategy;
	}

	/**
	 * @return the dataStrategy
	 */
	public RuleEngineDataStrategy getDataStrategy() {
		return dataStrategy;
	}

}
