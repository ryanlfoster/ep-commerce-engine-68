/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.test.db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.service.contentspace.DynamicContentService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test named queries for dynamic content.
 */
public class DynamicContentTest extends DbTestCase {

	@Autowired
	private DynamicContentService dynamicContentService;
	
	private static final String CONTENT_WRAPPER_ID   = "contentWrapperId"; 
	
	private static final String DYNAMIC_CONTENT_NAME = "Winter";

	/**
	 * Test add operation.
	 */	
	@DirtiesDatabase
	@Test
	public void testAddDynamicContent() {		
		final DynamicContent dynamicContent  = getBeanFactory().getBean(ContextIdNames.DYNAMIC_CONTENT);
		dynamicContent.setContentWrapperId(CONTENT_WRAPPER_ID);
		dynamicContent.setName(DYNAMIC_CONTENT_NAME);
		
		DynamicContent dynamicContentPersisted = getTxTemplate().execute(new TransactionCallback<DynamicContent>() {
			@Override
			public DynamicContent doInTransaction(final TransactionStatus arg0) {
				return dynamicContentService.add(dynamicContent);
			}
		});		
		
		 
		assertNotNull(dynamicContentPersisted);
		assertTrue(dynamicContentPersisted.isPersisted());
	}
	

	/**
	 * Create single DC and persist it.
	 * @return uidPk of stored object. 
	 */
	private long createSingleDynamicContent() {
		final DynamicContent dynamicContent  = getBeanFactory().getBean(ContextIdNames.DYNAMIC_CONTENT);
		dynamicContent.setContentWrapperId(CONTENT_WRAPPER_ID);
		dynamicContent.setName(DYNAMIC_CONTENT_NAME);
		return dynamicContentService.add(dynamicContent).getUidPk();
		
	}
	

	
	/**
	 * Test for find all named query.
	 */
	@DirtiesDatabase
	@Test
	public void testFindAll() {
		getTxTemplate().execute(new TransactionCallback<DynamicContent>() {
			@Override
			public DynamicContent doInTransaction(final TransactionStatus arg0) {
				createSingleDynamicContent();
				List<DynamicContent> allDynamicContent = getPersistenceEngine().retrieveByNamedQuery("DYNAMIC_CONTENT_SELECT_ALL");
				assertNotNull(allDynamicContent);
				assertFalse(allDynamicContent.isEmpty());
				return null;
				
			}
		});
	}
	
	/**
	 * Test find by name named query.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByName() {
		getTxTemplate().execute(new TransactionCallback<DynamicContent>() {
			@Override
			public DynamicContent doInTransaction(final TransactionStatus arg0) {
				createSingleDynamicContent();
				List<DynamicContent> allDynamicContent = getPersistenceEngine().retrieveByNamedQuery("DYNAMIC_CONTENT_FIND_BY_NAME",
						DYNAMIC_CONTENT_NAME);
				assertNotNull(allDynamicContent);
				assertFalse(allDynamicContent.isEmpty());
				return null;
				
			}
		});
	}
	
	/**
	 * Test find by name using like operation named query.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByNameLike() {
		getTxTemplate().execute(new TransactionCallback<DynamicContent>() {
			@Override
			public DynamicContent doInTransaction(final TransactionStatus arg0) {
				createSingleDynamicContent();
				List<DynamicContent> allDynamicContent = getPersistenceEngine().retrieveByNamedQuery("DYNAMIC_CONTENT_FIND_BY_NAME_LIKE", "%int%");
				assertNotNull(allDynamicContent);
				assertFalse(allDynamicContent.isEmpty());
				return null;
				
			}
		});
	}
	
	/**
	 * Test find by name using like operation named query.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByNameLikeAndUsedInDCA() {
		getTxTemplate().execute(new TransactionCallback<DynamicContent>() {
			@Override
			public DynamicContent doInTransaction(final TransactionStatus arg0) {
				createSingleDynamicContent();
				List<DynamicContent> allDynamicContent = getPersistenceEngine().retrieveByNamedQuery("DYNAMIC_CONTENT_FIND_BY_NAME_LIKE_ASSIGNED",
						"%int%");
				assertNotNull(allDynamicContent);
				assertTrue(allDynamicContent.isEmpty());
				return null;
				
			}
		});
	}
	
	
	/**
	 * Test find by name using like operation named query.
	 */
	@DirtiesDatabase
	@Test
	public void testIsDynamicContentInUse() {
		getTxTemplate().execute(new TransactionCallback<DynamicContent>() {
			@Override
			public DynamicContent doInTransaction(final TransactionStatus arg0) {
				long uidPk = createSingleDynamicContent();
				final List<Long> results = getPersistenceEngine().retrieveByNamedQuery("DYNAMIC_CONTENT_IN_USE", uidPk);
				assertNotNull(results);
				assertTrue(results.isEmpty());
				return null;
				
			}
		});
	}
	
	
	/**
	 * Test for getAllByContentWrapperId method. 
	 */
	@DirtiesDatabase
	@Test
	public void testGetAllByContentWrapperId() {
		getTxTemplate().execute(new TransactionCallback<DynamicContent>() {
			@Override
			public DynamicContent doInTransaction(final TransactionStatus arg0) {
				createSingleDynamicContent();
				List<DynamicContent> dynamicContentList = getPersistenceEngine().retrieveByNamedQuery("DYNAMIC_CONTENT_SELECT_BY_WRAPPER_ID",
						CONTENT_WRAPPER_ID);
				assertNotNull(dynamicContentList);
				assertFalse(dynamicContentList.isEmpty());
				return null;
			}
		});
		
		
		
		
	}
	

}
