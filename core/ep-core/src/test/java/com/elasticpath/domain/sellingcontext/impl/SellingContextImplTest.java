package com.elasticpath.domain.sellingcontext.impl;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.ConditionEvaluatorService;

/**
 * Test for evaluation capabilities of Selling Context.
 * The names of the Tag Dictionaries GUIDS must be valid since by default
 * the selling context goes through all GUIDS.
 */
public class SellingContextImplTest  {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private SellingContext sellingContext = null;
	
	private static final String SHOPPER = "SHOPPER";
	private static final String TIME = "TIME";
	private static final String STORE = "STORE";
	
	private ConditionalExpression exp1;
	private ConditionalExpression exp2;
	private ConditionalExpression exp3;
	
	private ConditionEvaluatorService mockConditionEvaluatorService = null;
	
	private static final TagSet TAG_SET = new TagSet();
	
	/**
	 * set up objects for test.
	 */
	@Before
	public void setUp() {
		sellingContext = new SellingContextImpl();
		mockConditionEvaluatorService = context.mock(ConditionEvaluatorService.class);
		exp1 = context.mock(ConditionalExpression.class, "exp1");
		exp2 = context.mock(ConditionalExpression.class, "exp2");
		exp3 = context.mock(ConditionalExpression.class, "exp3");
	}
	
	/**
	 * Tests that isSatisfied method returns true if all conditions are null.
	 */
	@Test
	public void testAllConditionsNull() {
		
		assertEquals(true, sellingContext.isSatisfied(mockConditionEvaluatorService, TAG_SET));
		
	}
	
	/**
	 * Tests that isSatisfied method returns true if all conditions are evaluated to true.
	 */
	@Test
	public void testAllConditionsTrue() {
		
		context.checking(new Expectations() { { 
			allowing(exp1).setTagDictionaryGuid(SHOPPER);
			allowing(exp2).setTagDictionaryGuid(TIME);
			allowing(exp3).setTagDictionaryGuid(STORE);
			allowing(exp1).getConditionString(); will(returnValue(SHOPPER));
			allowing(exp2).getConditionString(); will(returnValue(TIME));
			allowing(exp3).getConditionString(); will(returnValue(STORE));
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp1); will(returnValue(true));
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp2); will(returnValue(true));
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp3); will(returnValue(true));
		} });
		
		sellingContext.setCondition(SHOPPER, exp1);
		sellingContext.setCondition(TIME, exp2);
		sellingContext.setCondition(STORE, exp3);
		
		assertEquals(true, sellingContext.isSatisfied(mockConditionEvaluatorService, TAG_SET));
		
	}
	
	/**
	 * Tests that isSatisfied method returns false if at least one condition is false.
	 */
	@Test
	public void testAtLeastOnConditionFalse() {
		
		context.checking(new Expectations() { { 
			allowing(exp1).setTagDictionaryGuid(SHOPPER);
			allowing(exp2).setTagDictionaryGuid(TIME);
			allowing(exp3).setTagDictionaryGuid(STORE);
			allowing(exp1).getConditionString(); will(returnValue(SHOPPER));
			allowing(exp2).getConditionString(); will(returnValue(TIME));
			allowing(exp3).getConditionString(); will(returnValue(STORE));
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp1); will(returnValue(false));
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp2); will(returnValue(true));
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp3); will(returnValue(true));
		} });
		
		sellingContext.setCondition(SHOPPER, exp1);
		sellingContext.setCondition(TIME, exp2);
		sellingContext.setCondition(STORE, exp3);
		
		assertEquals(false, sellingContext.isSatisfied(mockConditionEvaluatorService, TAG_SET));
		
		context.checking(new Expectations() { { 
			allowing(exp1).setTagDictionaryGuid(SHOPPER);
			allowing(exp2).setTagDictionaryGuid(TIME);
			allowing(exp3).setTagDictionaryGuid(STORE);
			allowing(exp1).getConditionString(); will(returnValue(SHOPPER));
			allowing(exp2).getConditionString(); will(returnValue(TIME));
			allowing(exp3).getConditionString(); will(returnValue(STORE));
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp1); will(returnValue(true));
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp2); will(returnValue(false));
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp3); will(returnValue(true));
		} });
		
		sellingContext.setCondition(SHOPPER, exp1);
		sellingContext.setCondition(TIME, exp2);
		sellingContext.setCondition(STORE, exp3);
		
		assertEquals(false, sellingContext.isSatisfied(mockConditionEvaluatorService, TAG_SET));
		
		context.checking(new Expectations() { { 
			allowing(exp1).setTagDictionaryGuid(SHOPPER);
			allowing(exp2).setTagDictionaryGuid(TIME);
			allowing(exp3).setTagDictionaryGuid(STORE);
			allowing(exp1).getConditionString(); will(returnValue(SHOPPER));
			allowing(exp2).getConditionString(); will(returnValue(TIME));
			allowing(exp3).getConditionString(); will(returnValue(STORE));
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp1); will(returnValue(true));
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp2); will(returnValue(true));
			allowing(mockConditionEvaluatorService).evaluateConditionOnTags(TAG_SET, exp3); will(returnValue(false));
		} });
		
		sellingContext.setCondition(SHOPPER, exp1);
		sellingContext.setCondition(TIME, exp2);
		sellingContext.setCondition(STORE, exp3);
		
		assertEquals(false, sellingContext.isSatisfied(mockConditionEvaluatorService, TAG_SET));
		
	}
	
	
	
}
