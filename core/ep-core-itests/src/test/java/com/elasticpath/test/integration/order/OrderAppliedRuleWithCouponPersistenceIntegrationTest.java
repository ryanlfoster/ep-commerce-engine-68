package com.elasticpath.test.integration.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Test;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.rules.AppliedCoupon;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.rules.impl.AppliedCouponImpl;
import com.elasticpath.domain.rules.impl.AppliedRuleImpl;
import com.elasticpath.domain.rules.impl.CartCurrencyConditionImpl;
import com.elasticpath.domain.rules.impl.CatalogCurrencyAmountDiscountActionImpl;
import com.elasticpath.domain.rules.impl.ProductCategoryConditionImpl;
import com.elasticpath.domain.rules.impl.PromotionRuleImpl;
import com.elasticpath.domain.rules.impl.RuleParameterImpl;
import com.elasticpath.domain.rules.impl.RuleSetImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test persistence of appliedRules with applied coupons on an order.
 * 
 */
public class OrderAppliedRuleWithCouponPersistenceIntegrationTest extends BasicSpringContextTest {

	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final ElasticPath elasticPath = context.mock(ElasticPath.class);

	private static final String CAD = "CAD";
	private static final long PROMOTION_RULE_UID = 12345654321L;
	
	/**
	 * Test persistence of the Applied Rule with the connected coupon.
	 */
	@DirtiesDatabase
	@Test
	public void testPersistOrderWithAppliedRuleWithCoupon() {
		final int usageCount = 12;
		
		// must start with an order for this to work
		Catalog catalog = getTac().getPersistersFactory().getCatalogTestPersister().persistDefaultMasterCatalog();
		Warehouse warehouse = getTac().getPersistersFactory().getStoreTestPersister().persistDefaultWarehouse();
		Store store = getTac().getPersistersFactory().getStoreTestPersister().persistDefaultStore(catalog, warehouse);
		Order order = createEmptyOrder(store);

		Rule rule = getTestPromotionRule();
		rule.setName("xyz");
		AppliedRule appliedRule = new AppliedRuleImpl();
		appliedRule.initialize(rule);
		AppliedCoupon coupon = new AppliedCouponImpl();
		coupon.setCouponCode("CouponCode");
		coupon.setUsageCount(usageCount);
		appliedRule.getAppliedCoupons().add(coupon);
		Set<AppliedRule> appliedRules = new HashSet<AppliedRule>();
		appliedRules.add(appliedRule);
		order.setAppliedRules(appliedRules);
		
		//throws exception if error
		persistOrder(order);
		
		assertNotNull(order.getAppliedRules());
		AppliedRule savedAppliedRule = order.getAppliedRules().iterator().next();
		assertNotNull(savedAppliedRule.getAppliedCoupons());
		
		Iterator<AppliedCoupon> iter = savedAppliedRule.getAppliedCoupons().iterator();
		coupon = iter.next();
		
		assertEquals("UsageCount before and after persistence should be the same.", usageCount, coupon.getUsageCount());

		assertEquals("CouponCode before and after persistence should be the same.", "CouponCode", coupon.getCouponCode());
		
	}
	
	private Order createEmptyOrder(final Store store) {
		final String dummyIp = "192.168.1.1";
		Order order = new OrderImpl();
		order.setCreatedDate(new Date());
		order.setLastModifiedDate(new Date());
		order.setIpAddress(dummyIp);
		order.setStoreCode(store.getCode());
		order.setLocale(Locale.CANADA_FRENCH);

		return order;
	}
	
	private Order persistOrder(final Order order) throws TransactionException {
		return getTxTemplate().execute(new TransactionCallback<Order>() {
			@Override
			public Order doInTransaction(final TransactionStatus arg0) {
				getPersistenceEngine().save(order);
				return order;
			}
		});
	}

	private PersistenceEngine getPersistenceEngine() {
		return getBeanFactory().getBean(ContextIdNames.PERSISTENCE_ENGINE);
	}
	
	private TransactionTemplate getTxTemplate() {
		return getTac().getTxTemplate();
	}
	
	private Rule getTestPromotionRule() {
		PromotionRuleImpl promotionRuleImpl = new PromotionRuleImpl() {
			private static final long serialVersionUID = -2453313068978275806L;

			@Override
			public ElasticPath getElasticPath() {
				return elasticPath;
			}
			
			private final Set<RuleAction> actions = new HashSet<RuleAction>();
			
			@Override
			public void addAction(final RuleAction ruleAction) { //NOPMD
				this.actions.add(ruleAction);
			}
			
			@Override
			public void removeAction(final RuleAction ruleAction) {
				this.actions.remove(ruleAction);
			}
			
			@Override
			public Set<RuleAction> getActions() {
				return this.actions;
			}
		};
		promotionRuleImpl.setUidPk(PROMOTION_RULE_UID);

		// Create a condition that the product is in a particular category
		RuleCondition categoryCondition = new ProductCategoryConditionImpl();
		categoryCondition.addParameter(new RuleParameterImpl(RuleParameter.BOOLEAN_KEY, "true"));
		categoryCondition.addParameter(new RuleParameterImpl(RuleParameter.CATEGORY_CODE_KEY, "8"));
		promotionRuleImpl.addCondition(categoryCondition);

		// Create a condition that the currency is CAD
		RuleCondition currencyCondition = new CartCurrencyConditionImpl();
		currencyCondition.addParameter(new RuleParameterImpl(RuleParameter.CURRENCY_KEY, CAD));
		promotionRuleImpl.addCondition(currencyCondition);
				
		// Create an action
		RuleAction discountAction = new CatalogCurrencyAmountDiscountActionImpl();
		discountAction.addParameter(new RuleParameterImpl(RuleParameter.DISCOUNT_AMOUNT_KEY, "100"));
		discountAction.addParameter(new RuleParameterImpl(RuleParameter.CURRENCY_KEY, CAD));
		discountAction.setRuleId(PROMOTION_RULE_UID);
		promotionRuleImpl.addAction(discountAction);
		
		RuleSet ruleSetImpl = new RuleSetImpl();
		ruleSetImpl.setScenario(RuleScenarios.CATALOG_BROWSE_SCENARIO);
		promotionRuleImpl.setRuleSet(ruleSetImpl);		
		
		return promotionRuleImpl;
	}
}
