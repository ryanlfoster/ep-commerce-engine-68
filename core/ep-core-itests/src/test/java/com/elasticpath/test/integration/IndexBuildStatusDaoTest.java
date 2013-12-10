package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.domain.search.IndexStatus;
import com.elasticpath.persistence.dao.IndexBuildStatusDao;
import com.elasticpath.service.search.IndexType;

/**
 * Tests DAO operations on <code>IndexBuildStatusDao</code>.
 */
public class IndexBuildStatusDaoTest extends BasicSpringContextTest {

	private static final int NUM_OF_DEFAULT_INDEX_TYPES = 7;

	/** The main object under test. */
	@Autowired
	private IndexBuildStatusDao indexBuildStatusDao;
	
	/**
	 * Tests get() method.
	 */
	@DirtiesDatabase
	@Test
	public void testGet() {
		IndexBuildStatus indexBuildStatus = indexBuildStatusDao.get(IndexType.CATEGORY);
		
		assertEquals(IndexType.CATEGORY, indexBuildStatus.getIndexType());
		assertEquals(null, indexBuildStatus.getLastBuildDate());
		assertEquals(IndexStatus.MISSING, indexBuildStatus.getIndexStatus());
	}

	/**
	 * Tests list() method.
	 */
	@DirtiesDatabase
	@Test
	public void testList() {
		List<IndexBuildStatus> indexBuildStatusList = indexBuildStatusDao.list();
		assertEquals(NUM_OF_DEFAULT_INDEX_TYPES, indexBuildStatusList.size());

		assertTrue(findIndexType(indexBuildStatusList, IndexType.CATEGORY));
		assertTrue(findIndexType(indexBuildStatusList, IndexType.CUSTOMER));	
		assertTrue(findIndexType(indexBuildStatusList, IndexType.PRODUCT));
		assertTrue(findIndexType(indexBuildStatusList, IndexType.PROMOTION));
		assertTrue(findIndexType(indexBuildStatusList, IndexType.CMUSER));
		assertTrue(findIndexType(indexBuildStatusList, IndexType.SHIPPING_SERVICE_LEVEL));
		assertTrue(findIndexType(indexBuildStatusList, IndexType.SKU));
	}
	
	/**
	 * Tests saveOrUpdate method.
	 */
	@DirtiesDatabase
	@Test
	public void testSave() {
		final Date buildDate = new Date();

		// save
		final IndexBuildStatus indexBuildStatusBeforeSave = indexBuildStatusDao.get(IndexType.PRODUCT);
		indexBuildStatusBeforeSave.setLastBuildDate(buildDate);
		indexBuildStatusBeforeSave.setIndexStatus(IndexStatus.COMPLETE);		
		indexBuildStatusDao.saveOrUpdate(indexBuildStatusBeforeSave);

		// check
		final IndexBuildStatus indexBuildStatusAfterSave = indexBuildStatusDao.get(IndexType.PRODUCT);
		assertEquals(IndexType.PRODUCT, indexBuildStatusAfterSave.getIndexType());
		assertEquals(DateUtils.truncate(buildDate, Calendar.SECOND), 
				DateUtils.truncate(indexBuildStatusAfterSave.getLastBuildDate(), Calendar.SECOND));
		assertEquals(IndexStatus.COMPLETE, indexBuildStatusAfterSave.getIndexStatus());
	}
	
	/*
	 * Utility: tests if list contains IndexBuildStatus with type.  
	 */
	private static boolean findIndexType(final List<IndexBuildStatus> list, final IndexType type) {
		for (IndexBuildStatus indexBuildStatus : list) {
			if (indexBuildStatus.getIndexType().equals(type)) {
				return true;
			}
		}
		return false;
	}

}
