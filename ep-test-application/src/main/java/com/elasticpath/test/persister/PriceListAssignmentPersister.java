package com.elasticpath.test.persister;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.service.pricing.PriceListDescriptorService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.TagDictionary;

/**
 * Convenience class that provides methods to persist Price List Assignments.
 */
public class PriceListAssignmentPersister {

	private final BeanFactory beanFactory;
	
	private final PriceListAssignmentService priceListAssignmentService;
	
	private final CatalogService catalogService;
	
	private final PriceListDescriptorService priceListDescriptorService;
	
	/**
	 * Construct PriceListAssignmentPersister.
	 * @param beanFactory - elastic path bean factory
	 */
	public PriceListAssignmentPersister(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		priceListAssignmentService = beanFactory.getBean(ContextIdNames.PRICE_LIST_ASSIGNMENT_SERVICE);
		catalogService = beanFactory.getBean(ContextIdNames.CATALOG_SERVICE);
		priceListDescriptorService = beanFactory.getBean(ContextIdNames.PRICE_LIST_DESCRIPTOR_SERVICE);
	}
	
	/**
	 * Creates and persists price list assignment, by default sets it to non-hidden.
	 * 
	 * @param catalogGuid catalog guid
	 * @param priceListDescriptorGuid price list descriptor guid
	 * @param name name of price list assignment
	 * @param description description of price list assignment
	 * @param priority priority of price list assignment
	 * @return created price list assignment object
	 */
	public PriceListAssignment createPriceListAssignment(final String catalogGuid, final String priceListDescriptorGuid,
			final String name, final String description, final int priority) {
		PriceListAssignment priceListAssignment = buildPriceListAssignment(
				catalogGuid, priceListDescriptorGuid, name, description,
				priority, false);
		return priceListAssignmentService.saveOrUpdate(priceListAssignment);
	}

	private PriceListAssignment buildPriceListAssignment(
			final String catalogGuid, final String priceListDescriptorGuid,
			final String name, final String description, final int priority, final boolean isHidden) {
		Catalog catalog = catalogService.findByGuid(catalogGuid, null);
		PriceListDescriptor priceListDescriptor = priceListDescriptorService.findByGuid(priceListDescriptorGuid);
		PriceListAssignment priceListAssignment = beanFactory.getBean(ContextIdNames.PRICE_LIST_ASSIGNMENT);
		priceListAssignment.setName(name);
		priceListAssignment.setDescription(description);
		priceListAssignment.setPriority(priority);
		priceListAssignment.setCatalog(catalog);
		priceListAssignment.setPriceListDescriptor(priceListDescriptor);
		priceListAssignment.setHidden(isHidden);
		return priceListAssignment;
	}

	
	/**
	 * Creates and persists price list assignment with selling context. By default sets the PLA to non-hidden.
	 * 
	 * @param catalogGuid catalog guid
	 * @param priceListDescriptorGuid price list descriptor guid
	 * @param name name of price list assignment
	 * @param description description of price list assignment
	 * @param priority priority of price list assignment
	 * @param timeCondition time condition closure
	 * @param shopperCondition shopper condition closure
	 * @return created price list assignment object
	 */
	public PriceListAssignment createPriceListAssignment(final String catalogGuid, final String priceListDescriptorGuid,
			final String name, final String description, final int priority,
			final String timeCondition, final String shopperCondition) {
		PriceListAssignment priceListAssignment = buildPriceListAssignment(
				catalogGuid, priceListDescriptorGuid, name, description,
				priority, timeCondition, shopperCondition, false);
		return priceListAssignmentService.saveOrUpdate(priceListAssignment);
	}
	
	/**
	 * Creates and persists price list assignment with selling context. By default sets the PLA to non-hidden.
	 * 
	 * @param priceListAssignmentGuid price list assignment guid
	 * @param catalogGuid catalog guid
	 * @param priceListDescriptorGuid price list descriptor guid
	 * @param name name of price list assignment
	 * @param description description of price list assignment
	 * @param priority priority of price list assignment
	 * @param timeCondition time condition closure
	 * @param shopperCondition shopper condition closure
	 * @return created price list assignment object
	 */
	public PriceListAssignment createPriceListAssignment(final String priceListAssignmentGuid, 
			final String catalogGuid, final String priceListDescriptorGuid,
			final String name, final String description, final int priority,
			final String timeCondition, final String shopperCondition) {
		PriceListAssignment priceListAssignment = buildPriceListAssignment(
				catalogGuid, priceListDescriptorGuid, name, description,
				priority, timeCondition, shopperCondition, false);
		priceListAssignment.setGuid(priceListAssignmentGuid);
		return priceListAssignmentService.saveOrUpdate(priceListAssignment);
	}


	private PriceListAssignment buildPriceListAssignment(
			final String catalogGuid, final String priceListDescriptorGuid,
			final String name, final String description, final int priority,
			final String timeCondition, final String shopperCondition, final boolean isHidden) {
		
		PriceListAssignment priceListAssignment = buildPriceListAssignment(
				catalogGuid, priceListDescriptorGuid, name, description,
				priority, isHidden);
		
		ConditionalExpression time = beanFactory.getBean(ContextIdNames.TAG_CONDITION);
		time.setConditionString(timeCondition);
		time.setName(time.getGuid());
		time.setDescription(time.getGuid());
		//time = tagConditionService.saveOrUpdate(time);
		
		ConditionalExpression shopper = beanFactory.getBean(ContextIdNames.TAG_CONDITION);
		shopper.setConditionString(shopperCondition);
		shopper.setName(shopper.getGuid());
		shopper.setDescription(shopper.getGuid());
		//shopper = tagConditionService.saveOrUpdate(shopper);
		
		
		SellingContext sellingContext = beanFactory.getBean(ContextIdNames.SELLING_CONTEXT);
		sellingContext.setCondition(TagDictionary.DICTIONARY_PLA_SHOPPER_GUID, shopper);
		sellingContext.setCondition(TagDictionary.DICTIONARY_TIME_GUID, time);
		sellingContext.setName(sellingContext.getGuid());
		sellingContext.setDescription(sellingContext.getGuid());
		
		priceListAssignment.setSellingContext(sellingContext);
		
		return priceListAssignment;
		
	}
	
	/**
	 * Creates and persists conditional price list assignment. By default sets the PLA to non-hidden.
	 *  
	 * @param catalogGuid catalog guid
	 * @param priceListDescriptorGuid price list descriptor guid
	 * @param name name of price list assignment
	 * @param description description of price list assignment
	 * @param priority priority of price list assignment
	 * @param timeCondition time condition dsl string
	 * @param shopperCondition shopper condition dsl string
	 * @return created price list assignment object
	 */
	public PriceListAssignment createConditionalPriceListAssignment(final String catalogGuid, final String priceListDescriptorGuid,
			final String name, final String description, final int priority, final String timeCondition, final String shopperCondition) {
		PriceListAssignment priceListAssignment = buildPriceListAssignment(
				catalogGuid, priceListDescriptorGuid, name, description,
				priority, false);
		if (!StringUtils.isEmpty(timeCondition) || !StringUtils.isEmpty(shopperCondition)) {
			ConditionalExpression who = null;	
			ConditionalExpression when = null;
			
			if (!StringUtils.isEmpty(timeCondition)) {
				when = createConditionalExpression(TagDictionary.DICTIONARY_TIME_GUID, timeCondition);
			}
			
			if (!StringUtils.isEmpty(shopperCondition)) {
				who = createConditionalExpression(TagDictionary.DICTIONARY_PLA_SHOPPER_GUID, shopperCondition);
			}
			
			SellingContext sellingContext = createPriceListAssignmentConditions(priceListAssignment, who, when);
			priceListAssignment.setSellingContext(sellingContext);		
		}
		return priceListAssignmentService.saveOrUpdate(priceListAssignment);
	}

	private SellingContext createPriceListAssignmentConditions(final PriceListAssignment assignment, 
			final ConditionalExpression who, final ConditionalExpression when) {
		SellingContext sellingContext = beanFactory.getBean(ContextIdNames.SELLING_CONTEXT);
		sellingContext.setPriority(1);
		sellingContext.setCondition(TagDictionary.DICTIONARY_PLA_SHOPPER_GUID, who);
		sellingContext.setCondition(TagDictionary.DICTIONARY_STORES_GUID, null);
		sellingContext.setCondition(TagDictionary.DICTIONARY_TIME_GUID, when);
		sellingContext.setName(assignment.getName());
		sellingContext.setDescription("PLA SellingContext for " + assignment.getName());
		return sellingContext;
	}

	private ConditionalExpression createConditionalExpression(final String tagDictionaryGuid, final String condition) {
		ConditionalExpression conditionalExpression = beanFactory.getBean(ContextIdNames.TAG_CONDITION);
		conditionalExpression.setTagDictionaryGuid(tagDictionaryGuid);
		conditionalExpression.setName("name_" + tagDictionaryGuid);
		conditionalExpression.setDescription("desc_" + tagDictionaryGuid);
		conditionalExpression.setConditionString(condition);
		return conditionalExpression;	
	}
	
	/**
	 * Creates and persists conditional price list assignment. By default sets the PLA to non-hidden.
	 *  
	 * @param priceListAssignment the price List Assignment
	 * @param sellingConditionType the type (dictionary guid) for the condition associated to the selling context
	 * @param sellingCondition  the condition string
	 * 
	 */
	public PriceListAssignment persistPLAWithSellingContext(final PriceListAssignment priceListAssignment, 
			final String sellingConditionType, final String sellingCondition) {
		
		// create selling context with condition
		SellingContext sellingContext = priceListAssignment.getSellingContext();
		if (sellingContext == null) {
			sellingContext = this.beanFactory.getBean(ContextIdNames.SELLING_CONTEXT);
			sellingContext.setGuid("pla guid "+ priceListAssignment.getGuid());
			sellingContext.setName("selling context for " + priceListAssignment.getName());
			sellingContext.setDescription("selling context for " + priceListAssignment.getName());
			sellingContext.setPriority(priceListAssignment.getPriority());
		}
		
		ConditionalExpression condition = createConditionalExpression(sellingConditionType, sellingCondition);	
		condition.setGuid(priceListAssignment.getName() + "_" + sellingConditionType);
		sellingContext.setCondition(condition.getTagDictionaryGuid(), condition);
		priceListAssignment.setSellingContext(sellingContext);
		
		return priceListAssignmentService.saveOrUpdate(priceListAssignment);
	}
}
