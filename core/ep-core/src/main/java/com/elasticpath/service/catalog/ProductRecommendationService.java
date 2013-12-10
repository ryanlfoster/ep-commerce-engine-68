/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog;

import com.elasticpath.service.EpPersistenceService;


/**
 * Provides services for computing product recommendations.
 * (E.g. customers who purchased this product also purchased product X.)
 * Product Recommendations are a type of <code>ProductAssociation</code> 
 */
public interface ProductRecommendationService extends EpPersistenceService {

	/**
	 * Re-compute product recommendations for each product in the system. 
	 */
	void updateRecommendations();
	
}
