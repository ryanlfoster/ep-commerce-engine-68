/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.rules.DiscountType;
import com.elasticpath.domain.rules.RuleAction;
import com.elasticpath.domain.rules.RuleElementType;
import com.elasticpath.domain.rules.RuleExceptionType;

/**
 * Test cases for <code>AbstractRuleActionImpl</code>.
 */
public class AbstractRuleActionImplTest {

	private static final int TEST_SALIENCE_VALUE = -2345;
	private static final String TEST_AGENDA_GROUP = "Group";
	
	private AbstractRuleActionImpl abstractRuleActionImpl;
	
	@Before
	public void setUp() throws Exception {
		abstractRuleActionImpl = new AbstractRuleActionImpl() {

			private static final long serialVersionUID = 6675481780272583902L;

			@Override
			protected String getElementKind() {
				return null;
			}

			@Override
			public RuleElementType getElementType() {
				return null;
			}

			@Override
			public boolean appliesInScenario(final int scenarioId) {
				return false;
			}

			@Override
			public String[] getParameterKeys() {
				return null;
			}

			@Override
			public RuleExceptionType[] getAllowedExceptions() {
				return null;
			}

			@Override
			public String getRuleCode() throws EpDomainException {
				return null;
			}
			
			public DiscountType getDiscountType() {
				return null;
			}
			
		};
		
	}

	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleActionImpl.getSalience()'.
	 */
	@Test
	public void testGetSetSalience() {
		abstractRuleActionImpl.setSalience(TEST_SALIENCE_VALUE);
		assertEquals(TEST_SALIENCE_VALUE, abstractRuleActionImpl.getSalience());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.rules.impl.AbstractRuleActionImpl.getAgendaGroup()'.
	 */
	@Test
	public void testGetSetAgendaGroup() {
		assertEquals(RuleAction.DEFAULT_AGENDA_GROUP, abstractRuleActionImpl.getAgendaGroup());
		abstractRuleActionImpl.setAgendaGroup(TEST_AGENDA_GROUP);
		assertEquals(TEST_AGENDA_GROUP, abstractRuleActionImpl.getAgendaGroup());
	}

}
