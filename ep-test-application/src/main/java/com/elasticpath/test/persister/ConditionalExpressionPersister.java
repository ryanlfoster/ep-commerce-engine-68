package com.elasticpath.test.persister;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.service.TagConditionService;

/**
 * Persists price lists.
 *
 */
public class ConditionalExpressionPersister {

	private final BeanFactory beanFactory;

	private TagConditionService tagConditionService;
	
	/**
	 * Default constructor. 
	 * @param beanFactory - elastic path bean factory
	 */
	public ConditionalExpressionPersister(final BeanFactory beanFactory) {
		super();
		this.beanFactory = beanFactory;
		this.tagConditionService = beanFactory.getBean(ContextIdNames.TAG_CONDITION_SERVICE);
	}

	/**
	 * Create Conditional Expression.
	 *
	 * @param guid object guid
	 * @param tagDictionaryGuid the tag identifier
	 * @param condition the condition build using DSL Builder
	 * @param name name
	 * @param desc description
	 * @return conditional expression
	 */
	public ConditionalExpression createConditionalExpression(
			final String guid,
			final String tagDictionaryGuid, final String name, final String desc, final String condition) {

		ConditionalExpression conditionalExpression =  populateConditionalExpression(guid, tagDictionaryGuid, name, desc, condition);

		return this.tagConditionService.saveOrUpdate(conditionalExpression);	
	}

	/**
	 * Update Conditional Expression.
	 *
	 * @param guid object guid
	 * @param tagDictionaryGuid the tag identifier
	 * @param condition the condition build using DSL Builder
	 * @param name name
	 * @param desc description
	 * @return conditional expression
	 */
	public ConditionalExpression updateConditionalExpression( 
			final String guid,
			final String tagDictionaryGuid, final String name, final String desc, final String condition) {

		ConditionalExpression conditionalExpression = this.tagConditionService.findByGuid(guid);
		conditionalExpression.setTagDictionaryGuid(tagDictionaryGuid);
		conditionalExpression.setName(name);
		conditionalExpression.setDescription(desc);
		conditionalExpression.setConditionString(condition);

		return this.tagConditionService.saveOrUpdate(conditionalExpression);	
	}

	/**
	 * Delete Conditional Expression.
	 * @param tagDictionaryGuid guid
	 */
	public void deleteConditionalExpression(final String tagDictionaryGuid) {
		ConditionalExpression condition =  this.tagConditionService.findByGuid(tagDictionaryGuid);
		this.tagConditionService.delete(condition);
	}
	
	/**
	 * Create Conditional Expression.
	 *
	 * @param guid object guid
	 * @param tagDictionaryGuid the tag identifier
	 * @param condition the condition build using DSL Builder
	 * @param name name
	 * @param desc description
	 * @param isNamed named
	 * @return conditional expression
	 */
	public ConditionalExpression createConditionalExpression(
			final String guid, final String tagDictionaryGuid, final String name, 
			final String desc, final String condition, final boolean isNamed) {

		
		ConditionalExpression conditionalExpression = populateConditionalExpression(guid, tagDictionaryGuid, name, desc, condition);
		conditionalExpression.setNamed(isNamed);

		return this.tagConditionService.saveOrUpdate(conditionalExpression);	
	}
	
	private ConditionalExpression populateConditionalExpression(
			final String guid, final String tagDictionaryGuid, final String name, 
			final String desc, final String condition) {
		ConditionalExpression conditionalExpression = beanFactory.getBean(ContextIdNames.TAG_CONDITION);
		conditionalExpression.setGuid(guid);
		conditionalExpression.setTagDictionaryGuid(tagDictionaryGuid);
		conditionalExpression.setName(name);
		conditionalExpression.setDescription(desc);
		conditionalExpression.setConditionString(condition);
		
		return conditionalExpression;
	}
}
