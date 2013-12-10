/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.test.integration.promotions;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import org.apache.openjpa.enhance.PersistenceCapable;
import org.apache.openjpa.enhance.StateManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.rules.RuleSetService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.PromotionTestPersister;
import com.elasticpath.test.persister.SellingContextTestPersister;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Integration tests that ensure the rule service behaves as expected.
 */
public class RuleServiceImplTest extends BasicSpringContextTest {

	@Autowired
	private RuleService ruleService;
	
	@Autowired
	private RuleSetService ruleSetService;
	
	/**
	 * Test persisting and loading display name.
	 */
	@DirtiesDatabase
	@Test
	public void testDisplayName() {
		final String englishDisplayName = "English Display Name";
		final String canadianDisplayName = "Canadian Display Name";
		final String germanDisplayName = "Deutsch Angezeigter Name";
		final String ruleCode = "ruleCode";
		
		RuleSet ruleSet = getBeanFactory().getBean(ContextIdNames.RULE_SET);
		ruleSet.setLastModifiedDate(new Date());
		ruleSet = ruleSetService.add(ruleSet);
		
		RuleParameter ruleParam = getBeanFactory().getBean(ContextIdNames.RULE_PARAMETER);
		ruleParam.setKey(RuleParameter.DISCOUNT_PERCENT_KEY);
		ruleParam.setValue("10");
		RuleAction ruleAction = getBeanFactory().getBean(ContextIdNames.CART_SUBTOTAL_PERCENT_DISCOUNT_ACTION);
		ruleAction.getParameters().clear();
		ruleAction.addParameter(ruleParam);
				
		// Create the rule
		Rule rule = getBeanFactory().getBean(ContextIdNames.PROMOTION_RULE);
		rule.setCode(ruleCode);
		rule.setRuleSet(ruleSet);
		rule.addAction(ruleAction);
		
		// Add display names in several locales
		LocalizedProperties localizedProperties = rule.getLocalizedProperties();
		localizedProperties.setValue(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.ENGLISH, englishDisplayName);
		final Locale canadianEnglish = new Locale("en", "CA");
		localizedProperties.setValue(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME, canadianEnglish, canadianDisplayName);
		localizedProperties.setValue(Rule.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.GERMAN, germanDisplayName);
		
		// Save the rule and reload it
		ruleService.add(rule);
		Rule loadedRule = ruleService.findByRuleCode(ruleCode);
		
		assertEquals("Rule should contain the English display name", englishDisplayName, loadedRule.getDisplayName(Locale.ENGLISH));
		assertEquals("Rule should contain the German display name", germanDisplayName, loadedRule.getDisplayName(Locale.GERMAN));
		assertEquals("Rule should contain the Canadian English display name", canadianDisplayName, loadedRule.getDisplayName(canadianEnglish));
	}
	
	/**
	 * Test that the service returns detached instances of selling context from 
	 * the {@code findRuleIdSellingContextByScenario()} method.
	 */
	@DirtiesDatabase
	@Test
	public void testSellingContextDetach() {
		
		// Create rule with selling context
		SimpleStoreScenario scenario = getTac().useScenario(SimpleStoreScenario.class);
		PromotionTestPersister promoPersister = getTac().getPersistersFactory().getPromotionTestPersister();
		SellingContextTestPersister sellingContextPersister = getTac().getPersistersFactory().getSellingContextTestPersister();
		SellingContext sellingContext = sellingContextPersister.createSellingContextWithSingleCondition("shopper");
		Rule promo = promoPersister.createAndPersistSimpleShoppingCartPromotion("testpromo", scenario.getStore().getCode(), "promocode");
		promo.setSellingContext(sellingContext);
		promoPersister.updatePromotionRule(promo);
		
		Transaction transaction = ruleService.getPersistenceEngine().getSharedPersistenceSession().beginTransaction();
		
		List<Object[]> ruleIdSellingContexts = ruleService.findRuleIdSellingContextByScenario(RuleScenarios.CART_SCENARIO);
		assertEquals("There should be x rule/context pairs", 1, ruleIdSellingContexts.size());
		
		for (Object[] object : ruleIdSellingContexts) {
			PersistenceCapable pcSellingContext = (PersistenceCapable) object[1];
			StateManager stateManager = pcSellingContext.pcGetStateManager();
			assertTrue("selling context should be detached" , stateManager.isDetached());
		}
		
		transaction.commit();
	}

}
