/**
 * 
 */
package com.elasticpath.service.pricing.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.pricing.service.impl.PLAStackLookupStrategy;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.pricing.impl.PriceListStackImpl;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.service.ConditionEvaluatorService;

/**
 * Test for PriceListLookupServiceImpl.
 */
public class PLAStackLookupStrategyTest {
	
	private static final int TEN = 10;
	
	private static final int FIVE = 5;
	
	private static final long THREE = 3;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final PLAStackLookupStrategy strategy = new PLAStackLookupStrategy();
	
	private final PriceListAssignmentService priceListAssignmentService = context.mock(PriceListAssignmentService.class);
	
	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	
	private final Catalog catalog = context.mock(Catalog.class);
	
	private final Currency currency = Currency.getInstance("USD");
	
	private List<PriceListAssignment> priceListAssignments;

	private void setupEmptyUSDPriceListAssignments() {
		
		priceListAssignments = new ArrayList<PriceListAssignment>();
		
		context.checking(new Expectations() { {
			
			allowing(catalog).getGuid(); will(returnValue("MYCATALOG"));
			allowing(catalog).getCode(); will(returnValue("MYCATALOG"));
			
			allowing(priceListAssignmentService).listByCatalogAndCurrencyCode("MYCATALOG", currency.getCurrencyCode(), true);
			will(returnValue(priceListAssignments));
			
			allowing(beanFactory).getBean(ContextIdNames.PRICE_LIST_STACK);
			will(returnValue(new PriceListStackImpl()));
			
		} });
		
	}
	
	private void setupThreeUSDPriceListAssignments() {
		
		setupEmptyUSDPriceListAssignments();
		
		final PriceListAssignment pla1 = context.mock(PriceListAssignment.class, "pla1");
		final PriceListDescriptor pla1d = context.mock(PriceListDescriptor.class, "pla1d");
		priceListAssignments.add(pla1);
		
		final PriceListAssignment pla2 = context.mock(PriceListAssignment.class, "pla2");
		final PriceListDescriptor pla2d = context.mock(PriceListDescriptor.class, "pla2d");
		priceListAssignments.add(pla2);
		
		final PriceListAssignment pla3 = context.mock(PriceListAssignment.class, "pla3");
		final PriceListDescriptor pla3d = context.mock(PriceListDescriptor.class, "pla3d");
		priceListAssignments.add(pla3);
		
		
		context.checking(new Expectations() { {
			
			allowing(pla1).getUidPk(); will(returnValue(2L));
			allowing(pla1).getPriority(); will(returnValue(TEN));
			allowing(pla1).getPriceListDescriptor(); will(returnValue(pla1d));
			allowing(pla1).getCatalog(); will(returnValue(catalog));
			allowing(pla1d).getGuid(); will(returnValue("2"));
			
			allowing(pla2).getUidPk(); will(returnValue(1L));
			allowing(pla2).getPriority(); will(returnValue(TEN));
			allowing(pla2).getPriceListDescriptor(); will(returnValue(pla2d));
			allowing(pla2).getCatalog(); will(returnValue(catalog));
			allowing(pla2d).getGuid(); will(returnValue("1"));
			
			allowing(pla3).getUidPk(); will(returnValue(THREE));
			allowing(pla3).getPriority(); will(returnValue(FIVE));
			allowing(pla3).getPriceListDescriptor(); will(returnValue(pla3d));
			allowing(pla3).getCatalog(); will(returnValue(catalog));
			allowing(pla3d).getGuid(); will(returnValue("3"));
			
		} });
		
	}
	
	
	/**
	 * Test that {@link PLAStackLookupStrategy#getPriceListStack(Catalog, Currency)} provided
	 * there exists three product list assignments with no selling context attached this method will return
	 * a price list stack containing guids of price list descriptors associated with those assignment and the
	 * ordering of those guid's will depend of the priority of the assignments.
	 */
	@Test
	public void testGetPriceListStack() {
		
		setupThreeUSDPriceListAssignments();
		strategy.setPriceListAssignmentService(priceListAssignmentService);
		strategy.setBeanFactory(beanFactory);
		PriceListStack priceListStack = strategy.getPriceListStack(catalog.getCode(), currency, null);
		assertNotNull(priceListStack);
		
		//check for high priority assignment, it must be first in stack
		assertEquals("3", priceListStack.getPriceListStack().get(0));
		
		//check if two assignments with same priority. 
		//If conflicting priorities exist, first PLA created is the first in the stack.
		assertEquals("1", priceListStack.getPriceListStack().get(1));
		assertEquals("2", priceListStack.getPriceListStack().get(2));
		
		
		
	}

	/**
	 * Test that {@link PLAStackLookupStrategy#getPriceListStack(Catalog, Currency)} provided
	 * there exists three product list assignments with selling context attached this method will return
	 * a price list stack containing only guids of price list descriptors associated with those assignment whose 
	 * selling context are satisfied and the ordering of those guid's will depend of the priority of the 
	 * assignments. On the other hand if we use {@link PLAStackLookupStrategy#getPriceListStack(Catalog, Currency)}
	 * then the price list stack will contain all values.
	 */
	@Test
	public void testGetPriceListStackWithSellingContext() {
		
		setupThreeUSDPriceListAssignments();
		strategy.setPriceListAssignmentService(priceListAssignmentService);
		strategy.setBeanFactory(beanFactory);
		final SellingContext pla1sc = context.mock(SellingContext.class, "pla1sc");
		final SellingContext pla2sc = context.mock(SellingContext.class, "pla2sc");
		final SellingContext pla3sc = context.mock(SellingContext.class, "pla3sc");
		
		context.checking(new Expectations() { {
			
			allowing(priceListAssignments.get(0)).getSellingContext(); will(returnValue(pla1sc));
			allowing(priceListAssignments.get(1)).getSellingContext(); will(returnValue(pla2sc));
			allowing(priceListAssignments.get(2)).getSellingContext(); will(returnValue(pla3sc));
			
			allowing(pla1sc).isSatisfied(with(aNull(ConditionEvaluatorService.class)), with(any(TagSet.class)), with(any(String[].class)));
			will(returnValue(true));
			allowing(pla2sc).isSatisfied(with(aNull(ConditionEvaluatorService.class)), with(any(TagSet.class)), with(any(String[].class)));
			will(returnValue(false));
			allowing(pla3sc).isSatisfied(with(aNull(ConditionEvaluatorService.class)), with(any(TagSet.class)), with(any(String[].class)));
			will(returnValue(true));
			
		} });
		
		PriceListStack priceListStackWithConditionEvaluation = strategy.getPriceListStack(catalog.getCode(), currency, new TagSet());
		assertNotNull(priceListStackWithConditionEvaluation);
		
		//check for high priority assignment, it must be first in stack
		assertEquals("3", priceListStackWithConditionEvaluation.getPriceListStack().get(0));
		
		assertEquals("2", priceListStackWithConditionEvaluation.getPriceListStack().get(1));
		
	}

	/**
	 * Test that {@link PLAStackLookupStrategy#getPriceListStack(Catalog, Currency)} provided
	 * there exists three product list assignments with selling context attached this method will return
	 * a price list stack containing all guids of price list descriptors associated with those assignment 
	 * regardless of whether the selling context is satisfied or not and the ordering of those guid's will 
	 * depend of the priority of the assignments. 
	 */
	@Test
	public void testGetPriceListStackWithSellingContextDisregarded() {
		
		setupThreeUSDPriceListAssignments();
		strategy.setPriceListAssignmentService(priceListAssignmentService);
		strategy.setBeanFactory(beanFactory);
		final SellingContext pla1sc = context.mock(SellingContext.class, "pla1sc");
		final SellingContext pla2sc = context.mock(SellingContext.class, "pla2sc");
		final SellingContext pla3sc = context.mock(SellingContext.class, "pla3sc");
		
		context.checking(new Expectations() { {
			
			allowing(priceListAssignments.get(0)).getSellingContext(); will(returnValue(pla1sc));
			allowing(priceListAssignments.get(1)).getSellingContext(); will(returnValue(pla2sc));
			allowing(priceListAssignments.get(2)).getSellingContext(); will(returnValue(pla3sc));
			
			allowing(pla1sc).isSatisfied(null, null); will(returnValue(true));
			allowing(pla2sc).isSatisfied(null, null); will(returnValue(false));
			allowing(pla3sc).isSatisfied(null, null); will(returnValue(true));
			
		} });
		
		PriceListStack priceListStackWithoutConditionEvaluation = 
			strategy.getPriceListStack(catalog.getCode(), currency, null);
		assertNotNull(priceListStackWithoutConditionEvaluation);
		
		//check for high priority assignment, it must be first in stack
		assertEquals("3", priceListStackWithoutConditionEvaluation.getPriceListStack().get(0));
		
		//check if two assignments with same priority. 
		//If conflicting priorities exist, first PLA created is the first in the stack.
		assertEquals("1", priceListStackWithoutConditionEvaluation.getPriceListStack().get(1));
		assertEquals("2", priceListStackWithoutConditionEvaluation.getPriceListStack().get(2));
		
	}

}
