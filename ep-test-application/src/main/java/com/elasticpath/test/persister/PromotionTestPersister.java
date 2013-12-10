package com.elasticpath.test.persister;

import java.util.Date;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.rules.RuleSetService;
import com.elasticpath.service.rules.impl.DBCompilingRuleEngineImpl;
import com.elasticpath.service.sellingcontext.SellingContextService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.TagConditionService;
import com.elasticpath.test.common.exception.TestApplicationException;

/**
 * Persister allows to create and save into database promotion rules.
 */
public class PromotionTestPersister {

	private final DBCompilingRuleEngineImpl ruleEngine;

	// Services for saving and updating persisted objects.

	private final RuleService ruleService;

	private final RuleSetService ruleSetService;

	private final StoreService storeService;

	private final CatalogService catalogService;
	
	private final BeanFactory beanFactory;
	
	private final SellingContextService sellingContextService;
	
	private final TagConditionService tagConditionService;

	private final TestApplicationContext tac;
	/**
	 * Promotion test persister constructor.
	 * 
	 * @param beanFactory the bean factory
	 */
	public PromotionTestPersister(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		tac = beanFactory.getBean("testApplicationContext");
		ruleService = beanFactory.getBean(ContextIdNames.RULE_SERVICE);
		ruleSetService = beanFactory.getBean(ContextIdNames.RULE_SET_SERVICE);
		storeService = beanFactory.getBean(ContextIdNames.STORE_SERVICE);
		catalogService = beanFactory.getBean(ContextIdNames.CATALOG_SERVICE);
		sellingContextService = beanFactory.getBean(ContextIdNames.SELLING_CONTEXT_SERVICE);
		tagConditionService = beanFactory.getBean(ContextIdNames.TAG_CONDITION_SERVICE);
		ruleEngine = beanFactory.getBean("epRuleEngine");
		ruleEngine.resetRuleBaseCache();
	}

	/**
	 * Create catalog promotion rule.
	 * 
	 * @param promotionName the promotion name
	 * @param catalogCode the catalog code
	 * @return rule instance
	 */
	public Rule createCatalogPromotion(final String promotionName, final String catalogCode) {
		final Rule promotionRule = createPromotion(promotionName, ruleSetService.findByScenarioId(RuleScenarios.CATALOG_BROWSE_SCENARIO));


		Catalog catalog = catalogService.findByCode(catalogCode);
		if (catalog == null) {
			throw new TestApplicationException("Catalog with name " + catalogCode + " doesn't exists in DB.");
		}
		promotionRule.setCatalog(catalog);
		return promotionRule;
	}
	
	/**
	 * Create catalog promotion rule, but with the specified code as promo code.
	 * @param promotionName the promotion name
	 * @param promoCode the catalog code
	 * @param catalogCode rule instance
	 * @return created promotion rule.
	 */
	public Rule createCatalogPromotion(final String promotionName, final String promoCode, final String catalogCode) {
		final Rule promotionRule = createPromotion(promotionName, promoCode, 
				ruleSetService.findByScenarioId(RuleScenarios.CATALOG_BROWSE_SCENARIO), false);
		Catalog catalog = catalogService.findByCode(catalogCode);
		if (catalog == null) {
			throw new TestApplicationException("Catalog with name " + catalogCode + " doesn't exists in DB.");
		}
		promotionRule.setCatalog(catalog);
		return promotionRule;
	}

	/**
	 * Create shopping cart promotion rule.
	 * 
	 * @param promotionName the promotion name
	 * @param storeCode the store code
	 * @return configured rule instance
	 */
	public Rule createLimitedUsagePromotion(final String promotionName, final String storeCode) {
		final Rule promotionRule = createPromotion(promotionName, ruleSetService.findByScenarioId(RuleScenarios.CART_SCENARIO));

		return promotionRule;
	}

	/**
	 * Create shopping cart promotion rule.
	 * 
	 * @param promotionName the promotion name
	 * @param storeCode the store code
	 * @param code promo code (not a coupon code)
	 * @return configured rule instance
	 */
	public Rule createShoppingCartPromotion(final String promotionName, final String storeCode, final String code) {
		final Rule promotionRule = createPromotion(promotionName, code, 
				ruleSetService.findByScenarioId(RuleScenarios.CART_SCENARIO), false);


		final Store store = storeService.findStoreWithCode(storeCode);
		if (store == null) {
			throw new TestApplicationException("Store with code " + storeCode + " doesn't exists in DB.");
		}
		promotionRule.setStore(store);
		promotionRule.setCatalog(store.getCatalog());
		return promotionRule;
	}
	
	/**
	 * Create shopping cart promotion rule.
	 * 
	 * @param promotionName the promotion name
	 * @param storeCode the store code
	 * @param code promo code (not a coupon code)
	 * @param couponEnabled enabled for coupons
	 * @return configured rule instance
	 */
	public Rule createShoppingCartPromotion(final String promotionName, final String storeCode, final String code, final boolean couponEnabled) {
		final Rule promotionRule = createPromotion(promotionName, code, 
				ruleSetService.findByScenarioId(RuleScenarios.CART_SCENARIO), couponEnabled);


		final Store store = storeService.findStoreWithCode(storeCode);
		if (store == null) {
			throw new TestApplicationException("Store with code " + storeCode + " doesn't exists in DB.");
		}
		promotionRule.setStore(store);
		promotionRule.setCatalog(store.getCatalog());
		return promotionRule;
	}

	private Rule createPromotion(final String promotionName, final RuleSet ruleSet) {
		return createPromotion(promotionName, null, ruleSet, false);
	}
	
	/**
	 *
	 * Create promotion.
	 * 
	 * @param promotionName
	 * @param code
	 * @param ruleSet we have to know what kind of promotion need to be created
	 * @return rule
	 */
	private Rule createPromotion(final String promotionName, final String code, 
								 final RuleSet ruleSet, final boolean couponEnabled) {
		final Rule promotionRule = beanFactory.getBean(ContextIdNames.PROMOTION_RULE); 
		promotionRule.setRuleSet(ruleSet);
		promotionRule.initialize();
		
		if (ruleSet.getScenario() ==  RuleScenarios.CATALOG_BROWSE_SCENARIO) {
			//shopping cart scenario must use selling context for set the date ranges
			promotionRule.setStartDate(new Date());			
		}
		
		
		promotionRule.setCmUser(tac.getPersistersFactory().getStoreTestPersister().getCmUser());
		promotionRule.setName(promotionName);
		if (code != null) {
			promotionRule.setCode(code);
		}
		promotionRule.setCouponEnabled(couponEnabled);
		promotionRule.setEnabled(true);
		promotionRule.setDescription("Simple shopping cart promotion rule: discount for the given product sku");
		return promotionRule;
	}

	/**
	 * Save configured rule into a database.
	 * 
	 * @param promotionRule to be saved
	 * @return persisted rule
	 */
	public Rule persistPromotionRule(final Rule promotionRule) {
		Rule persistedPromotionRule = ruleService.add(promotionRule);
		ruleEngine.recompileRuleBase();
		return persistedPromotionRule;
	}

	/**
	 * Returns a shopping cart promotion with the given name.
	 * 
	 * @param promotionName the promotion name
	 * @return rule with the given name.
	 */
	public Rule findShoppingCartPromotionByName(final String promotionName) {
		return ruleService.findByName(promotionName);
	}

	/**
	 * Update a given promotion rule.
	 * 
	 * @param promotionRule promotion rule to update
	 * @return updated promotion rule
	 */
	public Rule updatePromotionRule(final Rule promotionRule) {
		Rule updatedPromotionRule = ruleService.update(promotionRule);
		ruleEngine.recompileRuleBase();
		return updatedPromotionRule;
	}
	
	/**
	 * Update a given promotion rule with conditions cleanup.
	 * 
	 * @param promotionRule promotion rule to update
	 * @return updated promotion rule
	 */
	public Rule updatePromotionRuleWithConditionsCleanup(final Rule promotionRule) {
		if (promotionRule.getSellingContext() != null) {
			SellingContext sellingContext = sellingContextService.getByGuid(promotionRule.getSellingContext().getGuid());
			for (ConditionalExpression conditionalExpression : sellingContext.getConditions().values()) {
				tagConditionService.delete(conditionalExpression);
			}
		}		
		Rule updatedPromotionRule = ruleService.update(promotionRule);
		ruleEngine.recompileRuleBase();
		return updatedPromotionRule;
	}
	
	/**
	 * Creates and persists a simple (10% off subtotal) shopping cart promotion.
	 *  
	 * @param promotionName the name of the promotion
	 * @param storeCode the store code
	 * @param code the promoCode (not coupon code)
	 * @return rule
	 */
	public Rule createAndPersistSimpleShoppingCartPromotion(final String promotionName, final String storeCode, final String code) {
		return createAndPersistSimpleShoppingCartPromotion(promotionName, storeCode, code, false);
	}
	
	/**
	 * Creates and persists a simple (10% off subtotal) shopping cart promotion.
	 *  
	 * @param promotionName the name of the promotion
	 * @param storeCode the store code
	 * @param code the promoCode (not coupon code)
	 * @param couponEnabled whether the promo is coupon enabled
	 * @return rule
	 */
	public Rule createAndPersistSimpleShoppingCartPromotion(final String promotionName, final String storeCode,
			final String code, final boolean couponEnabled) {
		RuleParameter param = beanFactory.getBean(ContextIdNames.RULE_PARAMETER);
		param.setKey(RuleParameter.DISCOUNT_PERCENT_KEY);
		param.setValue("10");

		RuleAction action = beanFactory.getBean(ContextIdNames.CART_SUBTOTAL_PERCENT_DISCOUNT_ACTION);
		action.getParameters().clear();
		action.addParameter(param);
		
		Rule rule = createShoppingCartPromotion(promotionName, storeCode, code, couponEnabled);
		rule.addAction(action);
		
		return persistPromotionRule(rule);
	}
	
}
