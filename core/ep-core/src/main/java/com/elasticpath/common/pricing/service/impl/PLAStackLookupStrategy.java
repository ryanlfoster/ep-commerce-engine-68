package com.elasticpath.common.pricing.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.common.pricing.service.PriceListStackLookupStrategy;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.impl.PriceListAssignmentByPriorityComparatorImpl;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.service.ConditionEvaluatorService;

/**
 * Build price list stacks through {@link PriceListAssignment}s.
 */
public class PLAStackLookupStrategy implements PriceListStackLookupStrategy {
	
	private final PriceListAssignmentByPriorityComparatorImpl priceListAssignmentComparator = new PriceListAssignmentByPriorityComparatorImpl();

	private ConditionEvaluatorService conditionEvaluatorService;

	private BeanFactory beanFactory;

	private PriceListAssignmentService priceListAssignmentService;

	@Override
	public PriceListStack getPriceListStack(final String catalogCode,
			final Currency currency, final TagSet tagSet) {
		List<PriceListAssignment> allPriceListAssignments = getPriceListAssignmentService().listByCatalogAndCurrencyCode(
				catalogCode,
				currency.getCurrencyCode(),
				true
		);
		
		final PriceListStack stack = createPriceListStack(currency);
		
		if (CollectionUtils.isNotEmpty(allPriceListAssignments)) {
			
			if (tagSet != null) {
				allPriceListAssignments = 
					filterPriceListAssignmentsForTagSet(tagSet, allPriceListAssignments);
			}
			
			populatePriceListStack(orderPriceListAssignment(allPriceListAssignments), stack);
			
		}
		return stack;
	}
	
	
	/**
	 * provided the current tag set available removes price assignments that do not satisfy the 
	 * selling context.
	 * @param tagSet the current tag set
	 * @param allPriceListAssignments all price lists
	 */
	private List<PriceListAssignment> filterPriceListAssignmentsForTagSet(final TagSet tagSet,
			final List<PriceListAssignment> allPriceListAssignments) {
		
		List<PriceListAssignment> availablePriceListAssignments = new ArrayList<PriceListAssignment>();
		for (PriceListAssignment pla : allPriceListAssignments) {
			
			final SellingContext context = pla.getSellingContext();
			
			final boolean isApplicable = context == null 
										|| context.isSatisfied(getConditionEvaluatorService(), tagSet);
			if (isApplicable) {
				availablePriceListAssignments.add(pla);
			}
		}
		return availablePriceListAssignments;
	}
	
	/**
	 * Create (@link PriceListStack} and fill price list guids from given  list of {@link PriceListAssignment}.
	 * @param currency the currency
	 * @return instance of {@link PriceListStack} 
	 */
	protected PriceListStack createPriceListStack(final Currency currency) {
		PriceListStack priceListStack = beanFactory.getBean(ContextIdNames.PRICE_LIST_STACK);
		priceListStack.setCurrency(currency);
		return priceListStack;
	}

	/**
	 * adds sorted price list assignments to stack.
	 * @param priceListAssignments given  list of {@link PriceListAssignment}
	 * @param priceListStack new stack object to populate
	 */
	private void populatePriceListStack(
			final List<PriceListAssignment> priceListAssignments,
			final PriceListStack priceListStack) {
		if (CollectionUtils.isNotEmpty(priceListAssignments)) {
			for (PriceListAssignment priceListAssignment : priceListAssignments) {
				priceListStack.addPriceList(priceListAssignment.getPriceListDescriptor().getGuid());
			}
		}
	}

	
	/**
	 * Reorder given price list assignments by priority and uidPk. 
	 * @param priceListAssignments list to reorder
	 * @return sorted list
	 */
	private List<PriceListAssignment> orderPriceListAssignment(final List<PriceListAssignment> priceListAssignments) {
		List<PriceListAssignment> sortedResult = new ArrayList<PriceListAssignment>();
		sortedResult.addAll(priceListAssignments);
		Collections.sort(sortedResult, priceListAssignmentComparator);
		return sortedResult;
	}


	/**
	 * Set the spring bean factory to use.
	 * @param beanFactory instance
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	

	/**
	 * Get the evaluator service instance.
	 * @return  the evaluator service.
	 */
	ConditionEvaluatorService getConditionEvaluatorService() {
		return conditionEvaluatorService;
	}
	/**
	 * Set the evaluator service.
	 * @param conditionEvaluatorService service instance.
	 */
	public void setConditionEvaluatorService(
			final ConditionEvaluatorService conditionEvaluatorService) {
		this.conditionEvaluatorService = conditionEvaluatorService;
	}


	/**
	 *
	 * @param priceListAssignmentService the priceListAssignmentService to set
	 */
	public void setPriceListAssignmentService(final PriceListAssignmentService priceListAssignmentService) {
		this.priceListAssignmentService = priceListAssignmentService;
	}


	/**
	 *
	 * @return the priceListAssignmentService
	 */
	public PriceListAssignmentService getPriceListAssignmentService() {
		return priceListAssignmentService;
	}


	/**
	 *
	 * @return the beanFactory
	 */
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
	
}
