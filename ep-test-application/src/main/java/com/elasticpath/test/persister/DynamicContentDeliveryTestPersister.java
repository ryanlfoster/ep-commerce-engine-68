package com.elasticpath.test.persister;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.ContentSpace;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.Parameter;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.contentspace.ContentSpaceService;
import com.elasticpath.service.contentspace.DynamicContentService;
import com.elasticpath.service.sellingcontext.SellingContextService;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;
import com.elasticpath.tags.domain.ConditionalExpression;
import com.elasticpath.tags.domain.TagDefinition;
import com.elasticpath.tags.service.TagConditionService;
import com.elasticpath.tags.service.TagDefinitionService;

/**
 * Allow to create, save, update DynamicContentDelivery into database.
 */
public class DynamicContentDeliveryTestPersister {

	private final BeanFactory beanFactory;

	private final DynamicContentDeliveryService dynamicContentDeliveryService;

	private final DynamicContentService dynamicContentService;

	private final ContentSpaceService contentSpaceService;

	private final SellingContextService sellingContextService;

	private final TagConditionService tagConditionService;

	private final TagDefinitionService tagDefinitionService;

	/**
	 * Construct DynamicContentDeliveryTestPersister.
	 * @param beanFactory - elastic path bean factory
	 */
	public DynamicContentDeliveryTestPersister(final BeanFactory beanFactory) {

		this.beanFactory = beanFactory;

		dynamicContentDeliveryService = this.beanFactory.getBean(ContextIdNames.DYNAMIC_CONTENT_DELIVERY_SERVICE);

		dynamicContentService = this.beanFactory.getBean(ContextIdNames.DYNAMIC_CONTENT_SERVICE);

		contentSpaceService = this.beanFactory.getBean(ContextIdNames.CONTENTSPACE_SERVICE);

		sellingContextService = this.beanFactory.getBean(ContextIdNames.SELLING_CONTEXT_SERVICE);

		tagConditionService = this.beanFactory.getBean(ContextIdNames.TAG_CONDITION_SERVICE);

		tagDefinitionService = this.beanFactory.getBean(ContextIdNames.TAG_DEFINITION_SERVICE);

	}

	/**
	 * Persist DynamicContentAssignment into database.
	 * @param dynamicContentDeliveryName instance to persist.
	 * @param dynamicContentName dynamic content name.
	 * @param priority priority for this assignment 1..10 (1 is the highest)
	 * @param contentspaces the array of contentspace names
	 * @return persistent instance of DynamicContentDelivery.
	 */
	public DynamicContentDelivery persistDynamicContentAssignment(
			final String dynamicContentDeliveryName, final String dynamicContentName,
			final int priority, final String...contentspaces) {

		final DynamicContent content = findDynamicContent(dynamicContentName);

		final DynamicContentDelivery dynamicContentAssignment = beanFactory.getBean(ContextIdNames.DYNAMIC_CONTENT_DELIVERY);
		dynamicContentAssignment.setGuid(dynamicContentDeliveryName);
		dynamicContentAssignment.setName(dynamicContentDeliveryName);
		dynamicContentAssignment.setPriority(priority);
		dynamicContentAssignment.setDynamicContent(content);

		if (contentspaces != null && contentspaces.length > 0) {
			for (String contentspace : contentspaces) {
				final ContentSpace space = findContentSpace(contentspace);
				if (space != null) {
					dynamicContentAssignment.getContentspaces().add(space);
				}
			}
		}

		return dynamicContentDeliveryService.saveOrUpdate(dynamicContentAssignment);

	}

	/**
	 * create conditional expression.
	 *
	 * @param tagDictionaryGuid the tag identifier
	 * @param condition the condition build using DSL Builder
	 * @return conditional expression
	 */
	public ConditionalExpression createConditionalExpression(
			final String tagDictionaryGuid, final String conditionGuid, final String condition) {

		ConditionalExpression conditionalExpression = beanFactory.getBean(ContextIdNames.TAG_CONDITION);
		conditionalExpression.setGuid("guid_" + conditionGuid);
		conditionalExpression.setTagDictionaryGuid(tagDictionaryGuid);
		conditionalExpression.setName("name_" + tagDictionaryGuid);
		conditionalExpression.setDescription("desc_" + tagDictionaryGuid);
		conditionalExpression.setConditionString(condition);

		return conditionalExpression;

	}

	/**
	 * create conditional expression.
	 *
	 * @param tagDictionaryGuid the tag identifier
	 * @param condition the condition build using DSL Builder
	 * @return conditional expression
	 */
	public ConditionalExpression createConditionalExpression(
			final String tagDictionaryGuid, final String condition) {

		ConditionalExpression conditionalExpression = beanFactory.getBean(ContextIdNames.TAG_CONDITION);
		conditionalExpression.setTagDictionaryGuid(tagDictionaryGuid);
		conditionalExpression.setName("name_" + tagDictionaryGuid);
		conditionalExpression.setDescription("desc_" + tagDictionaryGuid);
		conditionalExpression.setConditionString(condition);

		return conditionalExpression;

	}

	/**
	 * persist conditions for dynamicContentAssignment.
	 *
	 * @param dynamicContentDelivery the assignment
	 * @param shopper the condition
	 * @param time the condition
	 * @param stores the condition
	 * @return updated assignment with selling context containing conditions
	 */
	public DynamicContentDelivery persistDynamicContentDeliveryConditions(
			final DynamicContentDelivery dynamicContentDelivery,
			final ConditionalExpression shopper, final ConditionalExpression time, final ConditionalExpression stores) {

		SellingContext sellingContext = beanFactory.getBean(ContextIdNames.SELLING_CONTEXT);

		sellingContext.setPriority(1);
		sellingContext.setCondition("SHOPPER", shopper);
		sellingContext.setCondition("STORES", stores);
		sellingContext.setCondition("TIME", time);
		sellingContext.setName("DCA SellingContext for " + dynamicContentDelivery.getName());
		sellingContext.setDescription(sellingContext.getName());

		sellingContext = sellingContextService.saveOrUpdate(sellingContext);

		dynamicContentDelivery.setSellingContext(sellingContext);

		return dynamicContentDeliveryService.saveOrUpdate(dynamicContentDelivery);

	}

	/**
	 * get DynamicContentDelivery from db by its name.
	 *
	 * @param name the DynamicContentAssignment name
	 * @return DynamicContentAssignment
	 */
	public DynamicContentDelivery findDynamicContentDeliveryByName(final String name) {

		return dynamicContentDeliveryService.findByName(name);

	}

	/**
	 * Returns a DynamicContentDelivery by its guid.
	 *
	 * @param guid the unique id to search on
	 * @return DynamicContentDelivery
	 */
	public DynamicContentDelivery findDynamicContentDeliveryByGuid(final String guid) {

		return dynamicContentDeliveryService.findByGuid(guid);

	}

	/**
	 * get all DynamicContentDelivery from db by its name.
	 * @return list of all deliveries
	 */
	public List<DynamicContentDelivery> findDynamicContentDeliveryAll() {

		return dynamicContentDeliveryService.findAll();

	}

	/**
	 * Persist DynamicContentDelivery into database.
	 * @param dynamicContentDelivery instance to persist
	 * @return persistent instance of DynamicContentAssignment.
	 */
	public DynamicContentDelivery persistDynamicContentDelivery(final DynamicContentDelivery dynamicContentDelivery) {

		return dynamicContentDeliveryService.saveOrUpdate(dynamicContentDelivery);

	}

	/**
	 * get list of assignments for content space.
	 *
	 * @param contentspace the content space.
	 * @return list of dca's
	 */
	public List<DynamicContentDelivery> getDynamicContentDelivery(final String contentspace) {
		return dynamicContentDeliveryService.findByContentSpaceName(contentspace);
	}

	/**
	 * Persist DynamicContent into database.
	 * @param dynamicContentName instance to persist.
	 * @param wrapperId the wrapper id.
	 * @return persistent instance of DynamicContent.
	 */
	public DynamicContent persistDynamicContent(final String dynamicContentName, final String wrapperId) {

		final DynamicContent dynamicContent = beanFactory.getBean(ContextIdNames.DYNAMIC_CONTENT);
		dynamicContent.setName(dynamicContentName);
		dynamicContent.setGuid(dynamicContentName);
		dynamicContent.setContentWrapperId(wrapperId);

		return dynamicContentService.saveOrUpdate(dynamicContent);

	}

	/**
	 * Persist DynamicContent into database.
	 * @param dynamicContentName instance to persist.
	 * @param wrapperId the wrapper id.
	 * @param description description
	 * @return persistent instance of DynamicContent.
	 */
	public DynamicContent persistDynamicContent(final String dynamicContentName, final String wrapperId, final String description) {

		final DynamicContent dynamicContent = beanFactory.getBean(ContextIdNames.DYNAMIC_CONTENT);
		dynamicContent.setName(dynamicContentName);
		dynamicContent.setGuid(dynamicContentName);
		dynamicContent.setContentWrapperId(wrapperId);
		dynamicContent.setDescription(description);

		return dynamicContentService.saveOrUpdate(dynamicContent);

	}

	/**
	 * Find DynamicContent in database by name.
	 * @param dynamicContentGuid instance to find.
	 * @return persistent instance of DynamicContent.
	 */
	public DynamicContent findDynamicContent(final String dynamicContentName) {

		return dynamicContentService.findByName(dynamicContentName);

	}

	/**
	 * Persist parameter of DynamicContent into database.
	 * @param dynamicContent the dynamic content to append parameter to.
	 * @param parameterName name of parameter (Must end with "Localized" to be localized).
	 * @param parameterValue the value.
	 * @param locale the language
	 * @return persistent instance of DynamicContent.
	 */
	public DynamicContent persistDynamicContentParameter(final DynamicContent dynamicContent,
			final String parameterName, final String parameterValue, final String locale) {

		final List<ParameterValue> parameterValues = dynamicContent.getParameterValues();
		ParameterValue value = null;
		if (CollectionUtils.isNotEmpty(parameterValues)) {
			for (ParameterValue paramValue : parameterValues) {
				if (parameterName.equals(paramValue.getParameterName())) {
					value = paramValue;
					break;
				}
			}
		}

		if (value == null) { // create a new one
			boolean isLocalizable = isLocalizable(locale);

			value = beanFactory.getBean(ContextIdNames.DYNAMIC_CONTENT_WRAPPER_PARAMETER_VALUE);

			final Parameter parameter = beanFactory.getBean(ContextIdNames.DYNAMIC_CONTENT_WRAPPER_USER_INPUT_PARAMETER);
			parameter.setName(parameterName);
			parameter.setParameterId(parameterName);
			parameter.setLocalizable(isLocalizable);
			value.setGuid(dynamicContent.getName() + "_" + parameterName);
			value.setParameter(parameter);
			value.setParameterName(parameter.getParameterId());
			value.setLocalizable(isLocalizable);
			value.setValue(parameterValue, locale);
			dynamicContent.getParameterValues().add(value);
			return dynamicContentService.saveOrUpdate(dynamicContent);
		}
		value.setGuid(dynamicContent.getName() + "_" + parameterName);
		value.setValue(parameterValue, locale);
		return dynamicContentService.saveOrUpdate(dynamicContent);
	}

	private boolean isLocalizable(final String locale) {
		boolean result = false;
		if (locale != null && locale.trim().length() > 0) {
			result = true;
		}
		return result;
	}

	/**
	 * Persist ContentSpace into database.
	 * @param contentSpaceId contentspace id
	 * @param description description
	 * @return persistent instance of ContentSpace
	 */
	public ContentSpace persistContentSpace(final String contentSpaceId, final String description) {

		final ContentSpace contentSpace = beanFactory.getBean(ContextIdNames.CONTENTSPACE);
		contentSpace.setTargetId(contentSpaceId);
		contentSpace.setGuid(contentSpaceId);
		contentSpace.setDescription(description);
		return contentSpaceService.saveOrUpdate(contentSpace);

	}

	/**
	 * Persisnt ContentSpace into the database.
	 * @param guid guid
	 * @param targetId targetId
	 * @param description description
	 * @return persistent instance of ContentSpace
	 */
	public ContentSpace persistContentSpace(final String guid, final String targetId, final String description) {

		final ContentSpace contentSpace = beanFactory.getBean(ContextIdNames.CONTENTSPACE);
		contentSpace.setTargetId(targetId);
		contentSpace.setGuid(guid);
		contentSpace.setDescription(description);
		return contentSpaceService.saveOrUpdate(contentSpace);

	}

	/**
	 * Find ContentSpace in database by guid.
	 * @param contentSpaceName instance to find.
	 * @return persistent instance of ContentSpace.
	 */
	public ContentSpace findContentSpace(final String contentSpaceName) {

		return contentSpaceService.findByName(contentSpaceName);

	}

	/**
	 * Find selling context by GUID.
	 * @param guid the guid of selling context
	 * @return the persistent selling context entity or null
	 */
	public SellingContext findSellingContextByGuid(final String guid) {
		return sellingContextService.getByGuid(guid);
	}


	/**
	 * find conditonal expression by its guid.
	 * @param guid the guid
	 * @return conditional expression
	 */
	public ConditionalExpression findConditionalExpressionByGuid(final String guid) {

		return tagConditionService.findByGuid(guid);

	}

	/**
	 * find tag definition by its guid.
	 * @param guid the guid
	 * @return tag definition
	 */
	public TagDefinition getTagDefinitionByGuid(final String guid) {

		return tagDefinitionService.findByGuid(guid);

	}

	/**
	 * Persist DynamicContentDelivery with Selling context into database.
	 *
	 * @param dcd
	 *            the dynamic content delivery
	 * @param sellingConditionType
	 *            the type (dictionary guid) for the condition associated to the
	 *            selling context
	 * @param sellingCondition
	 *            the condition string
	 */
	public DynamicContentDelivery persistDCDWithSellingContext( final DynamicContentDelivery dcd,
			final String sellingConditionType, final String sellingCondition, final String conditionGuid) {

		// create selling context with condition
		SellingContext sellingContext = findSellingContextByGuid(dcd.getSellingContextGuid());
		if ( sellingContext == null ) {
			sellingContext = beanFactory.getBean(ContextIdNames.SELLING_CONTEXT);
			sellingContext.setGuid("dcd guid "+ dcd.getGuid());
			sellingContext.setName("selling context for " + dcd.getName());
			sellingContext.setDescription("selling context for " + dcd.getName());
			sellingContext.setPriority(dcd.getPriority());
		}

		ConditionalExpression condition = createConditionalExpression(sellingConditionType, conditionGuid, sellingCondition);

		sellingContext.setCondition(condition.getTagDictionaryGuid(), condition);
		sellingContext = sellingContextService.saveOrUpdate(sellingContext);
		dcd.setSellingContext(sellingContext);

		return dynamicContentDeliveryService.saveOrUpdate(dcd);
	}

	/**
	 * Persist DynamicContentDelivery with Selling context into database.
	 *
	 * @param dcd
	 *            the dynamic content delivery
	 * @param sellingConditionType
	 *            the type (dictionary guid) for the condition associated to the
	 *            selling context
	 * @param sellingCondition
	 *            the condition string
	 */
	public DynamicContentDelivery persistDCDWithSellingContext( final DynamicContentDelivery dcd,
			final String sellingConditionType, final String sellingCondition) {

		// create selling context with condition
		SellingContext sellingContext = findSellingContextByGuid(dcd.getSellingContextGuid());
		if ( sellingContext == null ) {
			sellingContext = beanFactory.getBean(ContextIdNames.SELLING_CONTEXT);
			sellingContext.setGuid("dcd guid "+ dcd.getGuid());
			sellingContext.setName("selling context for " + dcd.getName());
			sellingContext.setDescription("selling context for " + dcd.getName());
			sellingContext.setPriority(dcd.getPriority());
		}

		ConditionalExpression condition = createConditionalExpression(sellingConditionType, sellingCondition);
		condition.setGuid(dcd.getName() + "_" + sellingConditionType);

		sellingContext.setCondition(condition.getTagDictionaryGuid(), condition);
		sellingContext = sellingContextService.saveOrUpdate(sellingContext);
		dcd.setSellingContext(sellingContext);

		return dynamicContentDeliveryService.saveOrUpdate(dcd);
	}

	/**
	 * Persist DynamicContentDelivery with Selling context with saved condition  into database.
	 *
	 * The saved condition is existed in the DB, the new created Selling context need to be persisted in
	 * to DB first, then assign the saved condition to it, then to be persisted by Selling Context Service
	 * saveOrUpdate. Otherwise, get exception
	 * EntityExistsException: Attempt to persist detached object ConditionalExpressionImpl
	 *
	 * @param dcd the dynamic content delivery
	 * @param sellingConditionType the type (dictionary guid) for the condition associated to the selling context
	 * @param sellingCondition the condition string
	 */
	public DynamicContentDelivery persistDCDWithSavedCondition( final DynamicContentDelivery dcd, final String savedConditionName) {

		ConditionalExpression condition = findConditionalExpressionByGuid(savedConditionName);

		// if could not find the saved condition, no persistence action needs.
		if (condition == null) {
			return dcd;
		}

		// create selling context with condition
		SellingContext sellingContext = findSellingContextByGuid(dcd.getSellingContextGuid());
		if ( sellingContext == null ) {
			sellingContext = beanFactory.getBean(ContextIdNames.SELLING_CONTEXT);
			sellingContext.setName("selling context for " + dcd.getName());
			sellingContext.setDescription("selling context for " + dcd.getName());
			sellingContext.setPriority(dcd.getPriority());
			sellingContext = sellingContextService.saveOrUpdate(sellingContext);
		}

		sellingContext.setCondition(condition.getTagDictionaryGuid(), condition);
		sellingContext = sellingContextService.saveOrUpdate(sellingContext);

		dcd.setSellingContext(sellingContext);
		return dynamicContentDeliveryService.saveOrUpdate(dcd);
	}
}
