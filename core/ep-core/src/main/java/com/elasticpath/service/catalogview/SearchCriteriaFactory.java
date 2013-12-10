package com.elasticpath.service.catalogview;

import com.elasticpath.domain.catalogview.CatalogViewRequest;
import com.elasticpath.service.EpService;
import com.elasticpath.service.search.query.CategorySearchCriteria;
import com.elasticpath.service.search.query.KeywordSearchCriteria;
import com.elasticpath.service.search.query.ProductSearchCriteria;

/**
 * A factory for creating search criteria objects.
 */
public interface SearchCriteriaFactory extends EpService {

	/**
	 * Creates a {@link KeywordSearchCriteria} for the given search request.
	 * <p>
	 * This implementation creates a {@link KeywordSearchCriteria} containing the words in the given <code>CatalogViewRequest</code> as well as any
	 * keywords that are configured synonyms of the words in the given Request. The synonyms are only found one level deep (only a single pass to
	 * find synonyms is performed - synonyms of synonyms are not relevant).
	 * </p>
	 * 
	 * @param catalogViewRequest the catalog view request, which must be a {@link CatalogViewRequest}
	 * @return a {@link KeywordSearchCriteria}
	 */
	KeywordSearchCriteria createKeywordProductCategorySearchCriteria(CatalogViewRequest catalogViewRequest);

	/**
	 * Creates a {@link CategorySearchCriteria} for the given search request.
	 * 
	 * @param catalogViewRequest the catalog view  request, which must be a {@link CatalogViewRequest}
	 * @return a {@link CategorySearchCriteria}
	 */
	CategorySearchCriteria createCategorySearchCriteria(CatalogViewRequest catalogViewRequest);
	
	/**
	 * Creates a {@link ProductSearchCriteria} for the given search request.
	 *
	  @param catalogViewRequest the catalog view request, which must be a {@link CatalogViewRequest}
	 * @return a {@link ProductSearchCriteria}
	 */
	ProductSearchCriteria createProductSearchCriteria(CatalogViewRequest catalogViewRequest);

}
