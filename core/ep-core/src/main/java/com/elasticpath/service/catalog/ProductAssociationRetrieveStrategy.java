package com.elasticpath.service.catalog;

import java.util.Set;

import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;

/**
 * The interface for product association retrieve strategy.
 */
public interface ProductAssociationRetrieveStrategy {

	/**
	 * Get associations based on the criteria.
	 * 
	 * @param criteria the product association search criteria
	 * @return the set of product associations
	 */
	Set<ProductAssociation> getAssociations(ProductAssociationSearchCriteria criteria);
}
