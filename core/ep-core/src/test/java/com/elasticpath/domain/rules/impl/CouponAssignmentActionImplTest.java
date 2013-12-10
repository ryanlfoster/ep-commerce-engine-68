package com.elasticpath.domain.rules.impl;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Test;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.RuleService;

/**
 * Unit test for {@code CouponAssignmentActionImpl}.
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals" })
public class CouponAssignmentActionImplTest {

	@org.junit.Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	/**
	 * Verifies that, if the rule code parameter is for the same rule that the action
	 * is part of, then we get an exception.
	 */
	@Test(expected = EpDomainException.class)
	public void testRuleCodeIsForOwnAction() {
		final RuleService ruleService = context.mock(RuleService.class);
		
		CouponAssignmentActionImpl action = new CouponAssignmentActionImpl() {
			private static final long serialVersionUID = 8778311863103203817L;

			@Override
			RuleService getRuleService() {
				return ruleService;
			}
		};
		
		action.setRuleId(1);
		
		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.RULE_CODE_KEY);
		ruleParameter.setValue("TestRule");
		action.addParameter(ruleParameter);
		
		final Rule rule = new PromotionRuleImpl();
		rule.setCode("TestRule");
		
		context.checking(new Expectations() { {
			allowing(ruleService).get(1); will(returnValue(rule));
		} });
		
		action.validate();
	}
	
	/**
	 * Verifies that, if the rule code parameter is for a rule does not exist
	 * , then we get an exception.
	 */
	@Test(expected = EpDomainException.class)
	public void testRuleCodeNotExist() {
		final RuleService ruleService = context.mock(RuleService.class);
		
		CouponAssignmentActionImpl action = new CouponAssignmentActionImpl() {
			private static final long serialVersionUID = 9073908553066244177L;

			@Override
			RuleService getRuleService() {
				return ruleService;
			}
		};
		
		action.setRuleId(1);
		
		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.RULE_CODE_KEY);
		ruleParameter.setValue("TestRule");
		action.addParameter(ruleParameter);
		
		final Rule rule = new PromotionRuleImpl();
		rule.setCode("NotTestRule");
		
		context.checking(new Expectations() { {
			allowing(ruleService).get(1); will(returnValue(rule));
			allowing(ruleService).findByRuleCode("TestRule"); will(returnValue(null));
		} });
		
		action.validate();
	}
	
	/**
	 * Verifies that non user specific coupon configs fail validation.
	 */
	@Test(expected = EpDomainException.class)
	public void testNonUserSpecific() {
		final RuleService ruleService = context.mock(RuleService.class);
		final CouponConfigService couponConfigService = context.mock(CouponConfigService.class);
		
		CouponAssignmentActionImpl action = new CouponAssignmentActionImpl() {
			private static final long serialVersionUID = 8855964787963641585L;

			@Override
			RuleService getRuleService() {
				return ruleService;
			}
			
			@Override
			CouponConfigService getCouponConfigService() {
				return couponConfigService;
			}
		};
		
		action.setRuleId(1);
		
		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.RULE_CODE_KEY);
		ruleParameter.setValue("TestRule");
		action.addParameter(ruleParameter);
		
		final Rule rule = new PromotionRuleImpl();
		rule.setCode("NotTestRule");

		final CouponConfig couponConfig = new CouponConfigImpl();
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_COUPON);
		
		context.checking(new Expectations() { {
			allowing(ruleService).get(1); will(returnValue(rule));
			allowing(ruleService).findByRuleCode("TestRule"); will(returnValue(rule));
			allowing(couponConfigService).findByRuleCode("TestRule"); will(returnValue(couponConfig));
		} });
		
		action.validate();
	}
	
	/**
	 * Verifies that a null Prefix causes an exception.
	 */
	@Test(expected = EpDomainException.class)
	public void testNullPrefix() {
		final RuleService ruleService = context.mock(RuleService.class);
		final CouponConfigService couponConfigService = context.mock(CouponConfigService.class);
		
		CouponAssignmentActionImpl action = new CouponAssignmentActionImpl() {
			private static final long serialVersionUID = -969624955649821440L;

			@Override
			RuleService getRuleService() {
				return ruleService;
			}
			
			@Override
			CouponConfigService getCouponConfigService() {
				return couponConfigService;
			}
		};
		
		action.setRuleId(1);
		
		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.RULE_CODE_KEY);
		ruleParameter.setValue("TestRule");
		action.addParameter(ruleParameter);
		
		RuleParameter prefixParameter = new RuleParameterImpl();
		prefixParameter.setKey(RuleParameter.COUPON_PREFIX);
		prefixParameter.setValue(null);
		action.addParameter(prefixParameter);
		
		final Rule rule = new PromotionRuleImpl();
		rule.setCode("NotTestRule");
		
		final CouponConfig couponConfig = new CouponConfigImpl();
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		
		context.checking(new Expectations() { {
			allowing(ruleService).get(1); will(returnValue(rule));
			allowing(ruleService).findByRuleCode("TestRule"); will(returnValue(rule));
			allowing(couponConfigService).findByRuleCode("TestRule"); will(returnValue(couponConfig));
		} });
		
		action.validate();
	}
	
	/**
	 * Verifies that an empty Prefix causes an exception.
	 */
	@Test(expected = EpDomainException.class)
	public void testEmptyPrefix() {
		final RuleService ruleService = context.mock(RuleService.class);
		final CouponConfigService couponConfigService = context.mock(CouponConfigService.class);
		
		CouponAssignmentActionImpl action = new CouponAssignmentActionImpl() {
			private static final long serialVersionUID = 6894543013122558174L;

			@Override
			RuleService getRuleService() {
				return ruleService;
			}
			
			@Override
			CouponConfigService getCouponConfigService() {
				return couponConfigService;
			}
		};
		
		action.setRuleId(1);
		
		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.RULE_CODE_KEY);
		ruleParameter.setValue("TestRule");
		action.addParameter(ruleParameter);
		
		RuleParameter prefixParameter = new RuleParameterImpl();
		prefixParameter.setKey(RuleParameter.COUPON_PREFIX);
		prefixParameter.setValue("");
		action.addParameter(prefixParameter);
		
		final Rule rule = new PromotionRuleImpl();
		rule.setCode("NotTestRule");
		
		final CouponConfig couponConfig = new CouponConfigImpl();
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		
		context.checking(new Expectations() { {
			allowing(ruleService).get(1); will(returnValue(rule));
			allowing(ruleService).findByRuleCode("TestRule"); will(returnValue(rule));
			allowing(couponConfigService).findByRuleCode("TestRule"); will(returnValue(couponConfig));
		} });
		
		action.validate();
	}
	
	/**
	 * Verifies the happy path.
	 */
	@Test
	public void testHappyPath() {
		final RuleService ruleService = context.mock(RuleService.class);
		final CouponConfigService couponConfigService = context.mock(CouponConfigService.class);
		
		CouponAssignmentActionImpl action = new CouponAssignmentActionImpl() {
			private static final long serialVersionUID = -9066899657645788922L;

			@Override
			RuleService getRuleService() {
				return ruleService;
			}
			
			@Override
			CouponConfigService getCouponConfigService() {
				return couponConfigService;
			}
		};
		
		action.setRuleId(1);
		
		RuleParameter ruleParameter = new RuleParameterImpl();
		ruleParameter.setKey(RuleParameter.RULE_CODE_KEY);
		ruleParameter.setValue("TestRule");
		action.addParameter(ruleParameter);

		RuleParameter prefixParameter = new RuleParameterImpl();
		prefixParameter.setKey(RuleParameter.COUPON_PREFIX);
		prefixParameter.setValue("BasicPrefix");
		action.addParameter(prefixParameter);

		final Rule rule = new PromotionRuleImpl();
		rule.setCode("NotTestRule");
		
		final CouponConfig couponConfig = new CouponConfigImpl();
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		
		context.checking(new Expectations() { {
			allowing(ruleService).get(1); will(returnValue(rule));
			allowing(ruleService).findByRuleCode("TestRule"); will(returnValue(rule));
			allowing(couponConfigService).findByRuleCode("TestRule"); will(returnValue(couponConfig));
		} });
		
		action.validate();
	}
}
