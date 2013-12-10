package com.elasticpath.service.search;

import java.util.Collection;

import com.elasticpath.domain.rules.Rule;
import com.elasticpath.service.search.query.FilteredSearchCriteria;

/**
 * Helper class which constructs a search criteria for products that are affected by catalog
 * promotions.
 */
public interface CatalogPromoQueryComposerHelper {

	/**
	 * Constructs a search criteria for affected products by the given collection of promotion
	 * rules.
	 * 
	 * @param promoRules a collection of promotion rules to construct criteria for
	 * @return search criteria that represents the given collection of promotion rules
	 */
	FilteredSearchCriteria< ? > constructSearchCriteria(final Collection<Rule> promoRules);
}