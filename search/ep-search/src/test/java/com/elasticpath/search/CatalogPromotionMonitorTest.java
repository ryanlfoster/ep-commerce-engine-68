/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleCondition;

/**
 * Test that the CatalogPromotionMonitor class behaves as expected.
 */
public class CatalogPromotionMonitorTest {

	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private CatalogPromotionMonitor catalogPromotionMonitor;

	private final List<Collection<Rule>> notifyList = new ArrayList<Collection<Rule>>();

	/**
	 * Set up objects required for each test.
	 */
	@Before
	public void setUp() {
		catalogPromotionMonitor = new CatalogPromotionMonitor() {
			// Mock out actual notify method
			@Override
			protected void notifyPromoChanged(final Collection<Rule> promoRules) {
				notifyList.add(new HashSet<Rule>(promoRules));
			}
		};
	}

	/**
	 * Test that no notification gets sent out if there are no rules.
	 */
	@Test
	public void testNotifyPromoChangedWithNoRules() {
		catalogPromotionMonitor.batchNotifyPromoChanged(Collections.<Rule>emptySet());
		assertTrue("There should not have been any notifications sent", notifyList.isEmpty());
	}

	/**
	 * Test that a notification will get sent if there are a small number of rules.
	 */
	@Test
	public void testNotifyPromoChangedWithAFewRules() {
		final int conditionCount = 2;
		final int ruleCount = 1;
		final Collection<Rule> rules = createRules(ruleCount, conditionCount);
		
		final int maxClauseCount = 10;
		catalogPromotionMonitor.setMaxClauseCount(maxClauseCount);
		
		catalogPromotionMonitor.batchNotifyPromoChanged(rules);
		assertEquals("There should have been only 1 notification call", 1, notifyList.size());
		assertEquals("The notified rule set should be the one passed to the batch method", rules, notifyList.get(0));
	}

	/**
	 * Test that multiple notifications will get sent out if there are many rules.
	 * 
	 * Each rule will require (number of conditions + 3) criteria which is what
	 * the maxClauseCount is limiting.
	 */
	@Test
	public void testNotifyPromoChangedWithManyRules() {
		final int conditionCount = 2;
		final int ruleCount = 10;
		final Collection<Rule> rules = createRules(ruleCount, conditionCount);

		final int maxClauseCount = 10;
		catalogPromotionMonitor.setMaxClauseCount(maxClauseCount);
		
		final int expectedCalls = 5; 
		final int expectedRulesPerCall = 2;
		catalogPromotionMonitor.batchNotifyPromoChanged(rules);
		assertEquals("There should have been 5 notification calls", expectedCalls, notifyList.size());
		Collection<Rule> notifiedRules = new HashSet<Rule>();
		for (Collection<Rule> subset : notifyList) {
			assertEquals("Each call should have contained 2 rules", expectedRulesPerCall, subset.size());
			notifiedRules.addAll(subset);
		}
		assertEquals("The notification calls should have covered all rules", rules, notifiedRules);
	}

	/**
	 * Create a collection of simple rules with the given conditions.
	 *
	 * @param ruleCount the number of rules to create
	 * @param conditionCount the number of conditions to create
	 * @return a collection of rules
	 */
	private Collection<Rule> createRules(final int ruleCount, final int conditionCount) {
		final Collection<RuleCondition> conditions = new HashSet<RuleCondition>();
		for (int i = 0; i < conditionCount; i++) {
			conditions.add(context.mock(RuleCondition.class, "condition" + i));
		}
		
		final Collection<Rule> rules = new HashSet<Rule>();
		for (int i = 0; i < ruleCount; i++) {
			final Rule rule = context.mock(Rule.class, "Rule " + i);
			context.checking(new Expectations() {
				{
					allowing(rule).getConditions(); will(returnValue(conditions));
				}
			});
			rules.add(rule);
		}
		return rules;
	}

}

