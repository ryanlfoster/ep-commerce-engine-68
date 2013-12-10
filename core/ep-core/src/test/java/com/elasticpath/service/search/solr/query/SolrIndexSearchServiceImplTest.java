package com.elasticpath.service.search.solr.query;

import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.search.query.CategorySearchCriteria;
import com.elasticpath.service.search.query.CustomerSearchCriteria;
import com.elasticpath.service.search.query.OrderSearchCriteria;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.PromotionSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/**
 * Test <code>IndexSearchServiceImpl</code>.
 */
public class SolrIndexSearchServiceImplTest extends AbstractEPTestCase {


	private SolrIndexSearchServiceImpl indexSearchService;

	/**
	 * Prepare for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();

		this.indexSearchService = new SolrIndexSearchServiceImpl();
	}

	/**
	 * Test method for
	 * 'com.elasticpath.service.index.impl.IndexSearchServiceImpl.search(SearchCriteria)'.
	 */
	@Test
	public void testSearch() {
		SearchCriteria searchCriteria = new CategorySearchCriteria();
		stubGetBean(ContextIdNames.SOLR_SEARCH_RESULT, new SolrIndexSearchResult());
		this.indexSearchService.search(searchCriteria);

		searchCriteria = new CustomerSearchCriteria();
		this.indexSearchService.search(searchCriteria);

		searchCriteria = new ProductSearchCriteria();
		this.indexSearchService.search(searchCriteria);

		searchCriteria = new OrderSearchCriteria();
		this.indexSearchService.search(searchCriteria);

		searchCriteria = new PromotionSearchCriteria();
		this.indexSearchService.search(searchCriteria);
	}

}
