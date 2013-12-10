package com.elasticpath.service.search.solr.query;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.apache.lucene.search.Query;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.query.ShippingServiceLevelSearchCriteria;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test <code>ShippingServiceLevelQueryComposerImpl</code>.
 */
public class ShippingServiceLevelQueryComposerImplTest extends QueryComposerTestCase {

	private static final String WHITESPACE_REGEX = "\\s";

	private ShippingServiceLevelQueryComposerImpl shippingServiceLevelQueryComposerImpl;

	private ShippingServiceLevelSearchCriteria searchCriteria;


	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();

		shippingServiceLevelQueryComposerImpl = new ShippingServiceLevelQueryComposerImpl();
		shippingServiceLevelQueryComposerImpl.setAnalyzer(getAnalyzer());
		shippingServiceLevelQueryComposerImpl.setIndexUtility(getIndexUtility());

		searchCriteria = new ShippingServiceLevelSearchCriteria();
	}

	/**
	 * Test method for wrong search criteria.
	 */
	@Test
	public void testWrongSearchCriteria() {
		final SearchCriteria wrongSearchCriteria = new AbstractSearchCriteriaImpl() {
			private static final long serialVersionUID = -1747661260953694183L;

			public void optimize() {
				// do nothing
			}

			public IndexType getIndexType() {
				return null;
			}
		};

		try {
			shippingServiceLevelQueryComposerImpl.composeQuery(wrongSearchCriteria, getSearchConfig());
			fail("EpServiceException expected for wrong search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		try {
			shippingServiceLevelQueryComposerImpl.composeFuzzyQuery(wrongSearchCriteria, getSearchConfig());
			fail("EpServiceException expected for wrong search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test method for empty search criteria.
	 */
	@Override
	public void testEmptyCriteria() {
		try {
			shippingServiceLevelQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for no parameters in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		try {
			shippingServiceLevelQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for no parameters in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test method for {@link com.elasticpath.service.search.query.ShippingServiceLevelSearchCriteria.setServiceLevelName(String)}.
	 */
	@Test
	public void testServiceLevelName() {
		final String serviceLevelName = "service level name";
		searchCriteria.setServiceLevelNameExact(serviceLevelName);

		Query query = shippingServiceLevelQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.SERVICE_LEVEL_NAME_EXACT, String.valueOf(serviceLevelName));
		query = shippingServiceLevelQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.SERVICE_LEVEL_NAME_EXACT, String.valueOf(serviceLevelName));
	}

	/**
	 * Test method for {@link com.elasticpath.service.search.query.ShippingServiceLevelSearchCriteria.setServiceLevelCode(String)}.
	 */
	@Test
	public void testServiceLevelCode() {
		final String serviceLevelCode = "service level code";
		searchCriteria.setServiceLevelCode(serviceLevelCode);

		Query query = shippingServiceLevelQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.SERVICE_LEVEL_CODE, String.valueOf(serviceLevelCode));
		query = shippingServiceLevelQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.SERVICE_LEVEL_CODE, String.valueOf(serviceLevelCode));
	}

	/**
	 * Test method for {@link com.elasticpath.service.search.query.ShippingServiceLevelSearchCriteria.setCarrier(String)}.
	 */
	@Test
	public void testCarrier() {
		final String carrier = "carrier name";
		searchCriteria.setCarrierExact(carrier);

		Query query = shippingServiceLevelQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CARRIER_EXACT, String.valueOf(carrier));
		query = shippingServiceLevelQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CARRIER_EXACT, String.valueOf(carrier));
	}

	/**
	 * Test method for {@link com.elasticpath.service.search.query.ShippingServiceLevelSearchCriteria.setActive(Boolean)}.
	 */
	@Test
	public void testActive() {
		final Boolean active = Boolean.TRUE;
		searchCriteria.setActiveFlag(active);

		Query query = shippingServiceLevelQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.ACTIVE_FLAG, String.valueOf(active));
		query = shippingServiceLevelQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.ACTIVE_FLAG, String.valueOf(active));
	}

	/**
	 * Test method for {@link com.elasticpath.service.search.query.ShippingServiceLevelSearchCriteria.setActive(Boolean)}.
	 */
	@Test
	public void testActiveNoSpecified() {
		// specify any parameter to keep criteria not empty
		searchCriteria.setServiceLevelCode("service level code");

		Query query = shippingServiceLevelQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryNotContains(query, SolrIndexConstants.ACTIVE_FLAG, String.valueOf(Boolean.TRUE));
		query = shippingServiceLevelQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryNotContains(query, SolrIndexConstants.ACTIVE_FLAG, String.valueOf(Boolean.TRUE));

		query = shippingServiceLevelQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryNotContains(query, SolrIndexConstants.ACTIVE_FLAG, String.valueOf(Boolean.FALSE));
		query = shippingServiceLevelQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryNotContains(query, SolrIndexConstants.ACTIVE_FLAG, String.valueOf(Boolean.FALSE));
	}

	/**
	 * Test method for {@link com.elasticpath.service.search.query.UserSearchCriteria.setStore(String)}.
	 */
	@Test
	public void testStore() {
		final String store = "store name";
		searchCriteria.setStore(store);

		Query query = shippingServiceLevelQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.STORE_NAME, store.split(WHITESPACE_REGEX));
		query = shippingServiceLevelQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.STORE_NAME, store.split(WHITESPACE_REGEX));
	}

	/**
	 * Test method for {@link com.elasticpath.service.search.query.UserSearchCriteria.setRegion(String)}.
	 */
	@Test
	public void testRegion() {
		final String region = "region name";
		searchCriteria.setRegion(region);

		Query query = shippingServiceLevelQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.REGION, region.split(WHITESPACE_REGEX));
		query = shippingServiceLevelQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.REGION, region.split(WHITESPACE_REGEX));
	}

	@Override
	protected QueryComposer getComposerUnderTest() {
		return shippingServiceLevelQueryComposerImpl;
	}

	@Override
	protected SearchCriteria getCriteriaUnderTest() {
		return searchCriteria;
	}
}
