package com.elasticpath.test.persister;

import java.util.Date;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.service.sellingcontext.SellingContextService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * 
 * Selling context persister.
 *
 */
public class SellingContextTestPersister {
	
	private final BeanFactory beanFactory;
	
	private final SellingContextService sellingContextService;
	
	/**
	 * Construct selling context persister.
	 * 
	 * @param beanFactory bean factory
	 */
	public SellingContextTestPersister(final BeanFactory beanFactory) {
		
		this.beanFactory = beanFactory;
		
		sellingContextService = this.beanFactory.getBean(ContextIdNames.SELLING_CONTEXT_SERVICE);
		
	}
	
	/**
	 * Persist conditions for promotions.
	 *
	 * @return selling context promotion tag framework condition
	 */
	public SellingContext createEmptyContext() {		
		SellingContext sellingContext = beanFactory.getBean(ContextIdNames.SELLING_CONTEXT);		
		sellingContext.setPriority(1);
		sellingContext.setName("Selling Context");
		sellingContext.setDescription(sellingContext.getName());
		return sellingContext;		
	}
	
	/**
	 * 
	 * Sdd condition to selling context.
	 * 
	 * @param sellingContext optional selling context
	 * @param tagDictionary tag dictionary
	 * @param condition condition
	 * @return selling context with added condition
	 */
	public SellingContext addAdHocCondition(final SellingContext sellingContext, final String tagDictionary, final String condition) {
		SellingContext currentSellingContext = sellingContext;
		if (currentSellingContext == null) {
			currentSellingContext = createEmptyContext();
		}
		
		ConditionalExpression conditionalExpression = beanFactory.getBean(ContextIdNames.CONDITIONAL_EXPRESSION);
		conditionalExpression.initialize();
		conditionalExpression.setConditionString(condition);
		conditionalExpression.setNamed(false);
		String name = sellingContext.getGuid() + "_" + tagDictionary;
		conditionalExpression.setName(name);
		sellingContext.setCondition(tagDictionary, conditionalExpression);		
		return currentSellingContext;
		
	}
	
	
	/**
	 * Persist conditions for promotions.
	 * 
	 * @deprecated
	 *
	 * @param shopper the condition
	 * @return saved selling context promotion tag framework condition
	 */
	@Deprecated
	public SellingContext createSellingContextWithSingleCondition(final String shopper) {
		
		SellingContext sellingContext = beanFactory.getBean(ContextIdNames.SELLING_CONTEXT);
		
		sellingContext.setPriority(1);
		ConditionalExpression conditionalExpression = beanFactory.getBean(ContextIdNames.CONDITIONAL_EXPRESSION);
		conditionalExpression.setConditionString(shopper);
		conditionalExpression.setNamed(false);
		conditionalExpression.setName("Promo conditional expression " + (new Date()));
		sellingContext.setCondition(TagDictionary.DICTIONARY_PROMOTIONS_SHOPPER_GUID, conditionalExpression);
		sellingContext.setName("Promo SellingContext " + (new Date()));
		sellingContext.setDescription(sellingContext.getName());
		return sellingContext;
		
	}	

}
