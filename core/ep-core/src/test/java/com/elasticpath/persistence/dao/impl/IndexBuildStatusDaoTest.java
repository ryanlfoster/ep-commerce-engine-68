package com.elasticpath.persistence.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.domain.search.IndexStatus;
import com.elasticpath.domain.search.impl.IndexBuildStatusImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.search.IndexType;

/**
 * Tests DAO operations on <code>IndexBuildStatus</code>.
 */
public class IndexBuildStatusDaoTest {

	private static final IndexType INDEX_TYPE = IndexType.CATEGORY;
	
	private static final IndexStatus INDEX_STATUS = IndexStatus.COMPLETE;
	
	private static final Date INDEX_DATE = new Date();
	
	private IndexBuildStatusDaoImpl indexBuildStatusDao;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private final PersistenceEngine persistenceEngine = context.mock(PersistenceEngine.class);
	
	/*
	 * Utility method for creating populated IndexBuildStatus.
	 */
	private static IndexBuildStatus createIndexBuildStatus(final IndexType type, final Date date, final IndexStatus status) {
		IndexBuildStatus indexBuildStatus = new IndexBuildStatusImpl();
		indexBuildStatus.setIndexType(type);
		indexBuildStatus.setIndexStatus(status);
		indexBuildStatus.setLastBuildDate(date);
		return indexBuildStatus;
	} 
	
	/**
	 * Setup objects required for all tests.
	 */
	@Before
	public void setUp() { // throws Exception {
		indexBuildStatusDao = new IndexBuildStatusDaoImpl();
		indexBuildStatusDao.setPersistenceEngine(persistenceEngine);
	}


	/**
	 * Tests List() fault on sanityCheck().
	 */
	@Test(expected = EpServiceException.class)
	public void testListSanityCheck() {
		indexBuildStatusDao.setPersistenceEngine(null);
		indexBuildStatusDao.list();
	}
	
	/**
	 * Tests list() method.
	 */
	@Test
	public void testList() {
		// expectations
		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery("INDEXBUILDSTATUS_FIND_ALL");
			will(returnValue(Arrays.asList(createIndexBuildStatus(INDEX_TYPE, INDEX_DATE, INDEX_STATUS))));
		} });
		
		// execute
		List<IndexBuildStatus> indexBuildStatuses = indexBuildStatusDao.list();

		// verify
		assertEquals(1, indexBuildStatuses.size());
		
		IndexBuildStatus indexBuildStatus = indexBuildStatuses.get(0);
		
		assertEquals(INDEX_TYPE, indexBuildStatus.getIndexType());
		assertEquals(INDEX_DATE, indexBuildStatus.getLastBuildDate());
		assertEquals(INDEX_STATUS, indexBuildStatus.getIndexStatus());
	}

	/**
	 * Tests get() method with null index Type.
	 */
	@Test(expected = EpServiceException.class)
	public void testGetNullType() {
		indexBuildStatusDao.get(null);
	}
	
	/**
	 * Tests get() method falls when inconsistent data.
	 */
	@Test(expected = EpServiceException.class)
	public void testGetOnInconsitentData() {
		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery("INDEXBUILDSTATUS_FIND_BY_TYPE", INDEX_TYPE.getIndexName());
			will(returnValue(Arrays.asList(
					createIndexBuildStatus(INDEX_TYPE, INDEX_DATE, INDEX_STATUS), 
					createIndexBuildStatus(INDEX_TYPE, INDEX_DATE, INDEX_STATUS))));
		} });
		
		indexBuildStatusDao.get(INDEX_TYPE);
	}
	
	/**
	 * Tests get() method when index not found.
	 */
	@Test
	public void testGetWhenIndexNotFound() {
		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery("INDEXBUILDSTATUS_FIND_BY_TYPE", INDEX_TYPE.getIndexName());
			will(returnValue(Collections.emptyList()));
		} });
		
		assertNull(indexBuildStatusDao.get(INDEX_TYPE));
	}
	
	/**
	 * Tests get() method.
	 */
	@Test
	public void testGet() {
		context.checking(new Expectations() { {
			oneOf(persistenceEngine).retrieveByNamedQuery("INDEXBUILDSTATUS_FIND_BY_TYPE", INDEX_TYPE.getIndexName());
			will(returnValue(Arrays.asList(createIndexBuildStatus(INDEX_TYPE, INDEX_DATE, INDEX_STATUS))));
		} });
		
		IndexBuildStatus indexBuildStatus = indexBuildStatusDao.get(INDEX_TYPE);
		
		assertEquals(INDEX_TYPE, indexBuildStatus.getIndexType());
		assertEquals(INDEX_DATE, indexBuildStatus.getLastBuildDate());
		assertEquals(INDEX_STATUS, indexBuildStatus.getIndexStatus());
	}

	/**
	 * Tests save() method.
	 */
	@Test
	public void testSave() {
		final IndexBuildStatus indexBuildStatus = createIndexBuildStatus(INDEX_TYPE, INDEX_DATE, INDEX_STATUS);

		context.checking(new Expectations() { {
			oneOf(persistenceEngine).saveOrUpdate(indexBuildStatus);
			will(returnValue(indexBuildStatus));
		} });		
		
		assertSame(indexBuildStatus, indexBuildStatusDao.saveOrUpdate(indexBuildStatus));
	}
}
