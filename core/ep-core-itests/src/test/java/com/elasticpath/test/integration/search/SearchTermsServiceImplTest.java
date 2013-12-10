package com.elasticpath.test.integration.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.search.query.SearchTerms;
import com.elasticpath.domain.search.query.SearchTermsMemento.SearchTermsId;
import com.elasticpath.service.search.query.SearchTermsService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Integration tests for {@link com.elasticpath.service.search.query.impl.SearchTermsServiceImpl}. 
 */
public class SearchTermsServiceImplTest extends BasicSpringContextTest {
	private static final String KEYWORDS1 = "keyword1 keyword2";
	private static final String KEYWORDS2 = "keyword3 keyword4";
	
	@Autowired
	private SearchTermsService searchTermsService;
	
	private SearchTerms createSearchTerms(final String keywords) {
		SearchTerms searchTerms = getBeanFactory().getBean(ContextIdNames.SEARCH_TERMS);
		searchTerms.setKeywords(keywords);
		return searchTerms;
	}
	
	/**
	 * Test saving a SearchTerms and loading it again to make sure it persists.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveAndLoad() {
		SearchTerms searchTerms = createSearchTerms(KEYWORDS1);
		SearchTermsId id = searchTermsService.saveIfNotExists(searchTerms);
		assertNotNull("ID should not be null after a save.", id);
		
		SearchTerms retrieved = searchTermsService.load(id);
		assertNotNull("the search terms should be loaded from the database", retrieved);
		
		assertEquals("the retrieved should be equal to the saved. ", searchTerms, retrieved);
	}

	/**
	 * Tries to persist a SearchTerms twice. It should result in equal IDs.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveTwice() {
		SearchTerms searchTerms1 = createSearchTerms(KEYWORDS1);
		SearchTermsId id1 = searchTermsService.saveIfNotExists(searchTerms1);
		assertNotNull("ID should not be null after a save.", id1);
		
		SearchTerms searchTerms2 = createSearchTerms(KEYWORDS1);
		SearchTermsId id2 = searchTermsService.saveIfNotExists(searchTerms2);
		assertNotNull("ID should not be null after a save.", id2);
		
		assertEquals("The IDs should be the same, since the objects are equal.", id1, id2);
	}
	
	/**
	 * Tries to persist two different SearchTerms and asserts that they result in different IDs.
	 */
	@DirtiesDatabase
	@Test
	public void testDifferentKeywordsResultInDifferentIds() {
		SearchTerms searchTerms1 = createSearchTerms(KEYWORDS1);
		SearchTermsId id1 = searchTermsService.saveIfNotExists(searchTerms1);
		assertNotNull("ID should not be null after a save.", id1);
		
		SearchTerms searchTerms2 = createSearchTerms(KEYWORDS2);
		SearchTermsId id2 = searchTermsService.saveIfNotExists(searchTerms2);
		assertNotNull("ID should not be null after a save.", id2);
		
		assertFalse("Different keywords should result in different IDs.", id1.equals(id2));
	}
	
	/**
	 * Tries to load a non-existing SearchTerms and asserts the method returns null.
	 */
	@DirtiesDatabase
	@Test
	public void testNotFoundResultsInNull() {
		SearchTermsId id = new SearchTermsId("non-existing ID");
		SearchTerms searchTerms = searchTermsService.load(id);
		assertNull("Loading a non-existant search term should return null.", searchTerms);
	}
}
