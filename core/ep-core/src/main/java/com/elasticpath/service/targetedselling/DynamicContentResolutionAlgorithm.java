package com.elasticpath.service.targetedselling;

import java.util.Collection;

import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.targetedselling.DynamicContentDelivery;

/**
 * Action resolution algorithm abstraction.
 */
public interface DynamicContentResolutionAlgorithm {

	/**
	 * Resolves dynamic content out from all the given dynami content delivery based on some algorithm.
	 * 
	 * @param dynamicContentDeliveries the collection of dynamic content deliveries to resolve dynamic content from
	 * @return a dynamic content for given delivery which satisfies this algorithms constraints
	 */
	DynamicContent resolveDynamicContent(final Collection<DynamicContentDelivery> dynamicContentDeliveries);
}
