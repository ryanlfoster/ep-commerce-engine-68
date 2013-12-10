package com.elasticpath.service.contentspace.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.cache.SimpleTimeoutCache;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.sellingcontext.SellingContext;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.contentspace.DynamicContentResolutionException;
import com.elasticpath.service.contentspace.DynamicContentRuntimeService;
import com.elasticpath.service.sellingcontext.SellingContextRetrievalStrategy;
import com.elasticpath.service.targetedselling.DynamicContentDeliveryService;
import com.elasticpath.service.targetedselling.DynamicContentResolutionAlgorithm;
import com.elasticpath.tags.TagSet;
import com.elasticpath.tags.service.ConditionEvaluatorService;

/**
 * Runtime service that provides the resolution functionality for dynamic content 
 * deliveries for the content spaces provided given the preconditions defines within tage set.
 */
public class DynamicContentRuntimeServiceImpl implements DynamicContentRuntimeService {

	private DynamicContentDeliveryService dynamicContentDeliveryService;
	
	private SellingContextRetrievalStrategy sellingContextRetrievalStrategy;
	
	private DynamicContentResolutionAlgorithm dynamicContentResolutionAlgorithm;
	
	private ConditionEvaluatorService conditionEvaluatorService;
	
	private static final long DEFAULT_CACHE_TIMEOUT = 60000;
	
	private final SimpleTimeoutCache<String, List<DynamicContentDelivery>> contentSpaceDynamicContentDeliveryCache = 
		new SimpleTimeoutCache<String, List<DynamicContentDelivery>>(DEFAULT_CACHE_TIMEOUT);
	
	/**
	 * resolve dynamic content deliveries for the content spaces provided given the preconditions 
	 * defines within tag set.
	 *
	 * @param tagSet the preconditions
	 * @param contentSpaceName the content space for which to resolve the deliveries
	 * @return resolved dynamic content for the given content space
	 * @throws DynamicContentResolutionException is throws in case of errors (usually when resolution does 
	 *         not return a result
	 */
	public DynamicContent resolve(final TagSet tagSet, final String contentSpaceName) throws DynamicContentResolutionException {
		final List<DynamicContentDelivery> dynamicContentDelivery = getContentSpaceDelivery(contentSpaceName);
		
		if (CollectionUtils.isEmpty(dynamicContentDelivery)) {
			throw new DynamicContentResolutionException(
					"No DynamicContentDelivery was found for space [" + contentSpaceName + "].");
		}

		final List<DynamicContentDelivery> evaluated = findSatisfiedDeliveries(dynamicContentDelivery, tagSet);

		if (CollectionUtils.isEmpty(evaluated)) {
			throw new DynamicContentResolutionException(
					"DynamicContentDelivery was found for space [" + contentSpaceName + "] but none of the tag conditions matched.");
		}
		
		final DynamicContent dynamicContent = resolveDynamicContents(contentSpaceName, evaluated);
		if (dynamicContent == null) {
			throw new DynamicContentResolutionException(
					"DynamicContent resolution algorithm was unable to resolve the DynamicContent for [" + contentSpaceName + "].");
		}
		
		return dynamicContent;
	}
	
	/**
	 * Gets the dynamic content assignment service.
	 *
	 * @return dynamic content assignment service
	 */
	DynamicContentDeliveryService getDynamicContentDeliveryService() {
		return dynamicContentDeliveryService;
	}

	/**
	 * Sets the dynamic content assignment service.
	 *
	 * @param dynamicContentDeliveryService dynamic content assignment service to be set
	 */
	public void setDynamicContentDeliveryService(
			final DynamicContentDeliveryService dynamicContentDeliveryService) {
		this.dynamicContentDeliveryService = dynamicContentDeliveryService;
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
	 * uses evaluator service to filter out DCA that do not meet the existing conditions
	 * and check its validity.
	 * @param dynamicContentDeliveries - active assignments for content space
	 * @param tagSet tag cloud used by the evaluator service
	 * @return collection of DCA whose conditions are met
	 */
	protected List<DynamicContentDelivery> findSatisfiedDeliveries(
			final Collection<DynamicContentDelivery> dynamicContentDeliveries,
			final TagSet tagSet) {

		final List<DynamicContentDelivery> evaluatedAssignments = new ArrayList<DynamicContentDelivery>();
		for (DynamicContentDelivery assignment : dynamicContentDeliveries) {
			if (assignmentSatisfied(tagSet, assignment.getSellingContextGuid())) {
				evaluatedAssignments.add(assignment);
			}
		}
		return evaluatedAssignments;
	}

	
	private boolean assignmentSatisfied(final TagSet tagSet, final String sellingContextGuid) {
		if (StringUtils.isBlank(sellingContextGuid)) {
			return true;
		}
		SellingContext sellingContext = getSellingContextRetrievalStrategy().getByGuid(sellingContextGuid);
		return sellingContext == null || sellingContext.isSatisfied(getConditionEvaluatorService(), tagSet);
	}
	
	/**
	 * Set the action resolution algorithm.
	 * 
	 * @param dynamicContentResolutionAlgorithm the algorithm to set
	 */
	public void setActionResolutionAlgorithm(final DynamicContentResolutionAlgorithm dynamicContentResolutionAlgorithm) {
		this.dynamicContentResolutionAlgorithm = dynamicContentResolutionAlgorithm;
	}
	
	/**
	 * Get the action resolution algorithm.
	 * @return actionResolutionAlgorithm
	 */
	DynamicContentResolutionAlgorithm getActionResolutionAlgorithm() {
		return dynamicContentResolutionAlgorithm;
	}
	
	/**
	 * uses correct algorithm to generate a single content assignment which corresponds to this content space and check its validity.
	 * @param contentSpaceName the content space name
	 * @param dynamicContentDeliveries - active assignments
	 * @return resolved dynamic content
	 */
	protected DynamicContent resolveDynamicContents(
			final String contentSpaceName,
			final Collection<DynamicContentDelivery> dynamicContentDeliveries) {
		return getActionResolutionAlgorithm().resolveDynamicContent(dynamicContentDeliveries);
	}
	
	/**
	 * get list of dynamic content delivery for given content space.
	 *
	 * @param contentSpaceName the content space on web page
	 * @return list of dynamic content delivery for this content space
	 */
	protected List<DynamicContentDelivery> getContentSpaceDelivery(final String contentSpaceName) {
		List<DynamicContentDelivery> dynamicContentDelivery = contentSpaceDynamicContentDeliveryCache.get(contentSpaceName);
		if (dynamicContentDelivery == null) {
			dynamicContentDelivery = this.getDynamicContentDeliveryService().findByContentSpaceName(contentSpaceName);
			contentSpaceDynamicContentDeliveryCache.put(contentSpaceName, dynamicContentDelivery);
		}
		return dynamicContentDelivery;
	}

	/**
	 * get selling context service.
	 *
	 * @return selling context service
	 */
	SellingContextRetrievalStrategy getSellingContextRetrievalStrategy() {
		return sellingContextRetrievalStrategy;
	}

	/**
	 * set selling context retrieval strategy.
	 *
	 * @param retrievalStrategy the selling context service
	 */
	public void setSellingContextRetrievalStrategy(final SellingContextRetrievalStrategy retrievalStrategy) {
		this.sellingContextRetrievalStrategy = retrievalStrategy;
	}
	
	/**
	 * Set the timeout for caching dynamic content deliveries assigned to a content space.
	 * @param timeout time in milliseconds
	 */
	public void setContentSpaceDeliveryCacheTimeout(final long timeout) {
		contentSpaceDynamicContentDeliveryCache.setTimeout(timeout);
	}

}
