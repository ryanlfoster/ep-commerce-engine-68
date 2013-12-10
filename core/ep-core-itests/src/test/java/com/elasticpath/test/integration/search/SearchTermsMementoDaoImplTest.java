/*
 * Copyright (c) Elastic Path Software Inc., 2012.
 */
package com.elasticpath.test.integration.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityExistsException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.search.query.SearchTermsMemento;
import com.elasticpath.domain.search.query.SearchTermsMemento.SearchTermsId;
import com.elasticpath.domain.search.query.impl.SearchTermsMementoImpl;
import com.elasticpath.persistence.dao.SearchTermsMementoDao;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Tests DAO operations on <code>SearchTermsMemento</code>.
 */
public class SearchTermsMementoDaoImplTest extends BasicSpringContextTest {

	@Autowired
	private SearchTermsMementoDao searchTermsMementoDao;
	
	private void assertDoesNotExist(final SearchTermsMemento.SearchTermsId id) {
		assertFalse("The SearchTermsMemento with id [" + id.getValue() + "] should not exist.", searchTermsMementoDao.exists(id));
		assertNull("The SearchTermsMemento with id [" + id.getValue() + "] should not exist.", searchTermsMementoDao.find(id));
	}
	
	private void assertExist(final SearchTermsMemento.SearchTermsId id) {
		assertTrue("The SearchTermsMemento with id [" + id.getValue() + "] should exist.", searchTermsMementoDao.exists(id));
		assertNotNull("The SearchTermsMemento with id [" + id.getValue() + "] should exist.", searchTermsMementoDao.find(id));
	}
	
	private SearchTermsId assertCreateAndSave() {
		final String idValue = "FOO";
		final SearchTermsId id = new SearchTermsId(idValue);
		
		assertDoesNotExist(id);
		
		SearchTermsMemento origSearchTermsMemento = new SearchTermsMementoImpl();
		origSearchTermsMemento.setId(id);
		origSearchTermsMemento.setSearchTermsRepresentation("BAR");
		return assertCreateAndSave(origSearchTermsMemento);
	}
	
	private SearchTermsId assertCreateAndSave(final SearchTermsMemento origSearchTermsMemento) {
		searchTermsMementoDao.saveSearchTermsMemento(origSearchTermsMemento);
		assertExist(origSearchTermsMemento.getId());
		return origSearchTermsMemento.getId();
	}
	
	/**
	 * Test that a SearchTermsMemento can be saved to the DB and retrieved.
	 */
	@DirtiesDatabase
	@Test
	public void testCreateAndExistsAndFind() {
		final SearchTermsId id = new SearchTermsId("FOO");
		assertDoesNotExist(id);
		
		SearchTermsMemento origSearchTermsMemento = new SearchTermsMementoImpl();
		origSearchTermsMemento.setId(id);
		origSearchTermsMemento.setSearchTermsRepresentation("BAR");
		assertCreateAndSave(origSearchTermsMemento);
		assertEquals("The retrieved SearchTermsMemento should equal the original one.", origSearchTermsMemento, searchTermsMementoDao.find(id));
	}
	
	/**
	 * Test that a SearchTermsMemento can be saved to the DB and then removed.
	 */
	@DirtiesDatabase
	@Test
	public void testRemove() {
		final SearchTermsId id = assertCreateAndSave();
		searchTermsMementoDao.remove(id);
		assertDoesNotExist(id);
	}
	
	/**
	 * Test that a SearchTermsMemento cannot be updated.
	 */
	@DirtiesDatabase
	@Test(expected = EntityExistsException.class)
	public void testUpdate() {
		final SearchTermsId id = assertCreateAndSave();
		SearchTermsMemento retrievedSearchTermsMemento = searchTermsMementoDao.find(id);
		retrievedSearchTermsMemento.setId(new SearchTermsId("HEY"));
		searchTermsMementoDao.saveSearchTermsMemento(retrievedSearchTermsMemento);
	}
	
}
