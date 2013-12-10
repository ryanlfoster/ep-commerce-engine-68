package com.elasticpath.service.search.solr.query;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import org.apache.lucene.search.Query;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.query.PromotionSearchCriteria;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test <code>CustomerQueryComposerImpl</code>.
 */
public class PromotionQueryComposerImplTest extends QueryComposerTestCase {

	private PromotionQueryComposerImpl promotionQueryComposerImpl;

	private PromotionSearchCriteria searchCriteria;


	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();

		promotionQueryComposerImpl = new PromotionQueryComposerImpl();
		promotionQueryComposerImpl.setAnalyzer(getAnalyzer());
		promotionQueryComposerImpl.setIndexUtility(getIndexUtility());

		searchCriteria = new PromotionSearchCriteria();
	}

	/**
	 * Test method for wrong search criteria.
	 */
	@Test
	public void testWrongSearchCriteria() {
		final SearchCriteria wrongSearchCriteria = new AbstractSearchCriteriaImpl() {
			private static final long serialVersionUID = -468654387312398770L;

			public void optimize() {
				// do nothing
			}

			public IndexType getIndexType() {
				return null;
			}
		};

		try {
			promotionQueryComposerImpl.composeQuery(wrongSearchCriteria, getSearchConfig());
			fail("EpServiceException expected for wrong search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		try {
			promotionQueryComposerImpl.composeFuzzyQuery(wrongSearchCriteria, getSearchConfig());
			fail("EpServiceException expected for wrong search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test method for empty search criteria.
	 */
	@Test
	public void testEmptyCriteria() {
		try {
			promotionQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for no parameters in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		try {
			promotionQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for no parameters in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
	}

	/**
	 * Test method for {@link PromotionSearchCriteria#setPromotionName(String)}.
	 */
	@Test
	public void testPromotionName() {
		final String promotionName = "promotion name";
		searchCriteria.setPromotionName(promotionName);

		Query query = promotionQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.PROMOTION_NAME, promotionName.split("\\s"));
		query = promotionQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsFuzzy(query, SolrIndexConstants.PROMOTION_NAME, promotionName.split("\\s"));
	}

	/**
	 * Test method for {@link PromotionSearchCriteria#setStoreCode(String)}.
	 */
	@Test
	public void testStoreUid() {
		final String storeUid = "12345";
		searchCriteria.setStoreCode(storeUid);

		Query query = promotionQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.STORE_CODE, storeUid);
		query = promotionQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsNotFuzzy(query, SolrIndexConstants.STORE_CODE, storeUid);
	}

	/**
	 * Test method for {@link PromotionSearchCriteria#setRuleSetUid(String)}.
	 */
	@Test
	public void testRuleSetUid() {
		final String ruleSetUid = "54321";
		searchCriteria.setRuleSetUid(ruleSetUid);

		Query query = promotionQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.PROMOTION_RULESET_UID, ruleSetUid);
		query = promotionQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsNotFuzzy(query, SolrIndexConstants.PROMOTION_RULESET_UID, ruleSetUid);
	}

	/** Test method for {@link PromotionSearchCriteria#setEnabled(Boolean)}. */
	@Test
	public void testEnabled() {
		final Boolean enabled = Boolean.TRUE;
		searchCriteria.setEnabled(enabled);

		Query query = promotionQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.PROMOTION_STATE, String.valueOf(enabled));
		query = promotionQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsNotFuzzy(query, SolrIndexConstants.PROMOTION_STATE, String.valueOf(enabled));
	}

	/** Test method for {@link PromotionSearchCriteria#setActive(Boolean)}. */
	@Test
	public void testActive() {
		final Boolean active = true;
		searchCriteria.setActive(active);

		Query query = promotionQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.END_DATE);
		query = promotionQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsDateRange(query, SolrIndexConstants.END_DATE);
	}

	/**
	 * Test method for {@link PromotionSearchCriteria#setFilterUids(Set)}.
	 */
	@Test
	public void testFilterUids() {
		final Set<Long> emptySet = Collections.emptySet();
		final Set<Long> someSet = new HashSet<Long>(Arrays.asList(123L, 34325L, 123124124L));
		Query query;

		searchCriteria.setFilterUids(emptySet);
		try {
			query = promotionQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for empty filter UID set in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}
		try {
			query = promotionQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
			fail("EpServiceException expected for empty filter UID set in search criteria.");
		} catch (EpServiceException e) {
			assertNotNull(e);
		}

		searchCriteria.setFilterUids(someSet);
		query = promotionQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContainsSet(query, SolrIndexConstants.OBJECT_UID, someSet);
		query = promotionQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContainsSet(query, SolrIndexConstants.OBJECT_UID, someSet);
	}
	
	/**
	 * Test that setting {@link PromotionSearchCriteria#setCatalogUid(Long)} in the searchCriteria
	 * will make it into the final queries.
	 */
	@Test
	public void testCatalogUid() {
		final Long catalogUid = 234L;
		
		searchCriteria.setCatalogUid(catalogUid);
		Query query = promotionQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CATALOG_UID, String.valueOf(catalogUid));
		query = promotionQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CATALOG_UID, String.valueOf(catalogUid));
	}
	
	/**
	 * Test that setting {@link PromotionSearchCriteria#setCatalogCode(String)} in the searchCriteria
	 * will make it into the final queries.
	 */
	@Test
	public void testCatalogCode() {
		final String catalogCode = "catalogCode";
		
		searchCriteria.setCatalogCode(catalogCode);
		Query query = promotionQueryComposerImpl.composeQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CATALOG_CODE, String.valueOf(catalogCode));
		query = promotionQueryComposerImpl.composeFuzzyQuery(searchCriteria, getSearchConfig());
		assertQueryContains(query, SolrIndexConstants.CATALOG_CODE, String.valueOf(catalogCode));
	}

	@Override
	protected QueryComposer getComposerUnderTest() {
		return promotionQueryComposerImpl;
	}

	@Override
	protected SearchCriteria getCriteriaUnderTest() {
		return searchCriteria;
	}
}
