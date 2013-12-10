/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.targetedselling.impl;

import java.util.Collection;

import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;
import com.elasticpath.service.targetedselling.DynamicContentResolutionAlgorithm;

/**
 * Action resolution algorithm that takes in mind the dynamic content delivery priority.
 */
public class DynamicContentByDeliveryPriorityImpl implements DynamicContentResolutionAlgorithm {

	
	@Override
	public DynamicContent resolveDynamicContent(
			final Collection<DynamicContentDelivery> dynamicContentDeliveries) {
		
		if (dynamicContentDeliveries == null || dynamicContentDeliveries.isEmpty()) {
			return null;
		} 
		return applyResolutionAlgorithm(dynamicContentDeliveries);

	}
	
	/**
	 * Resolves the dynamic content delivery by priority. 
	 * @param dynamicContentDelivery - collection of DynamicContentDelivery
	 * @return single content qualifying
	 */
	protected DynamicContent applyResolutionAlgorithm(
			final Collection<DynamicContentDelivery> dynamicContentDelivery) {
		
		int priority = Integer.MAX_VALUE;
		DynamicContent content = null;
		
		for (DynamicContentDelivery delivery : dynamicContentDelivery) {
		
			if (delivery.getPriority() < priority) {
				
				content = delivery.getDynamicContent();
				priority = delivery.getPriority();
				
			}
			
		}
		
		return content;

	}
	
}
