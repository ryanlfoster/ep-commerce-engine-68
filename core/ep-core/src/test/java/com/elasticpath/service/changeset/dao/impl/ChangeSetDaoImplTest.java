package com.elasticpath.service.changeset.dao.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;

import com.elasticpath.service.changeset.ChangeSetSearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * The junit class for changeSetDaoImpl.
 */
public class ChangeSetDaoImplTest {
	
	private final ChangeSetDaoImpl changeSetDao = new ChangeSetDaoImpl();
	
	/**
	 * Test query for count not contain order by clause.
	 */
	@Test
	public void testQueryForCountDoesNotContainOrderBy() {
		String queryString = changeSetDao.buildQueryString(new ChangeSetSearchCriteria(), Collections.emptyList(), true);
		assertFalse("query for counting should not contain order by clause", queryString.contains("ORDER BY"));
	}
	
	/**
	 * Test query contain order by clause.
	 */
	@Test
	public void testQueryForContainOrderBy() {
		ChangeSetSearchCriteria criteria = new ChangeSetSearchCriteria();
		criteria.setSortingOrder(SortOrder.ASCENDING);
		criteria.setSortingType(StandardSortBy.NAME);
		String queryString = changeSetDao.buildQueryString(criteria, Collections.emptyList(), false);
		assertTrue("query should contain order by clause", queryString.contains("ORDER BY"));
	}

}
