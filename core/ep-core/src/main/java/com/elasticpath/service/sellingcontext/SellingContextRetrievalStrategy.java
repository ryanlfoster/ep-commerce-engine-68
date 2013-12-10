package com.elasticpath.service.sellingcontext;

import com.elasticpath.domain.sellingcontext.SellingContext;

/**
 * The interface for selling context retrieval strategy.
 */
public interface SellingContextRetrievalStrategy {

	/**
	 * Get the selling context by the guid.
	 * 
	 * @param guid the selling context guid
	 * @return the selling context
	 */
	SellingContext getByGuid(final String guid);

}
