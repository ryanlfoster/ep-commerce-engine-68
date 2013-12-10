package com.elasticpath.service.search.impl;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.lucene.search.BooleanQuery;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.misc.SearchConfig;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.UpdateType;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.SearchConfigFactory;
import com.elasticpath.service.search.index.QueryComposer;
import com.elasticpath.service.search.index.QueryComposerFactory;
import com.elasticpath.service.search.query.SearchCriteria;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test case for {@link IndexNotificationServiceImpl}.
 */
public class IndexNotificationServiceImplTest {

	private IndexNotificationServiceImpl indexNotificationServiceImpl;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private QueryComposerFactory mockQueryComposerFactory;
	
	private PersistenceEngine mockPersistenceEngine;

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	private SearchConfigFactory mockSearchConfigFactory;

	private SearchConfig mockSearchConfig;
	
	/**
	 * Prepares for tests.
	 * 
	 * @throws Exception in case of any errors
	 */
	@Before
	public void setUp() throws Exception {
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

		mockSearchConfig = context.mock(SearchConfig.class);
		mockSearchConfigFactory = context.mock(SearchConfigFactory.class);
		context.checking(new Expectations() {
			{
				allowing(mockSearchConfigFactory).getSearchConfig(with(any(String.class)));
				will(returnValue(mockSearchConfig));
			}
		});

		indexNotificationServiceImpl = new IndexNotificationServiceImpl();
		indexNotificationServiceImpl.setPersistenceEngine(mockPersistenceEngine);
		indexNotificationServiceImpl.setSearchConfigFactory(mockSearchConfigFactory);
		
		mockQueryComposerFactory = context.mock(QueryComposerFactory.class);
		indexNotificationServiceImpl.setQueryComposerFactory(mockQueryComposerFactory);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for {@link IndexNotificationServiceImpl#add(IndexNotification)}.
	 */
	@Test
	public void testSaveOrUpdate() {
		final IndexNotification indexNotification = context.mock(IndexNotification.class);

		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).saveOrUpdate(with(same(indexNotification)));
				will(returnValue(null));
			}
		});
		indexNotificationServiceImpl.add(indexNotification);
	}
	
	/**
	 * Test method for {@link IndexNotificationServiceImpl#remove(IndexNotification)}.
	 */
	@Test
	public void testRemove() {
		final IndexNotification indexNotification = context.mock(IndexNotification.class);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).delete(with(same(indexNotification)));
			}
		});
		indexNotificationServiceImpl.remove(indexNotification);
	}
	
	/**
	 * Test method for {@link IndexNotificationServiceImpl#findByIndexType(IndexType)}.
	 */
	@Test
	public void testFindByIndexType() {
		final IndexType indexType = IndexType.PRODUCT;
		final Collection<IndexNotification> notifications = new ArrayList<IndexNotification>();
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).retrieveByNamedQuery(
						"INDEXNOTIFY_FIND_BY_INDEX_TYPE",
						Collections.<String, Object>singletonMap("type", indexType.getIndexName()));
				will(returnValue(notifications));
			}
		});
		assertSame(notifications, indexNotificationServiceImpl.findByIndexType(indexType));
	}
	
	/**
	 * Test adding via query with non-fuzzy composed query.
	 */
	@Test
	public void testAddViaQueryNonFuzzy() {
		final IndexType indexType = IndexType.CATEGORY;
		final UpdateType updateType = UpdateType.UPDATE;
		final SearchCriteria searchCriteria = context.mock(SearchCriteria.class);
		final QueryComposer queryComposer = context.mock(QueryComposer.class);
		final IndexNotification mockIndexNotification = context.mock(IndexNotification.class);
		context.checking(new Expectations() {
			{
				allowing(searchCriteria).getIndexType();
				will(returnValue(indexType));
				oneOf(beanFactory).getBean("indexNotification");
				will(returnValue(mockIndexNotification));

				oneOf(mockIndexNotification).setIndexType(indexType);
				oneOf(mockIndexNotification).setUpdateType(updateType);
				oneOf(mockIndexNotification).setQueryString(with(any(String.class)));

				oneOf(mockQueryComposerFactory).getComposerForCriteria(searchCriteria);
				will(returnValue(queryComposer));

				oneOf(queryComposer).composeQuery(with(searchCriteria), with(any(SearchConfig.class)));
				will(returnValue(new BooleanQuery()));

				oneOf(mockPersistenceEngine).saveOrUpdate(with(any(Persistable.class)));
				will(returnValue(null));
			}
		});

		indexNotificationServiceImpl.addViaQuery(updateType, searchCriteria, false);
	}

	/**
	 * Test adding via query with fuzzy composed query.
	 */
	@Test
	public void testAddViaQueryFuzzy() {
		final IndexType indexType = IndexType.CATEGORY;
		final UpdateType updateType = UpdateType.UPDATE;
		final SearchCriteria searchCriteria = context.mock(SearchCriteria.class);
		final QueryComposer queryComposer = context.mock(QueryComposer.class);
		final IndexNotification mockIndexNotification = context.mock(IndexNotification.class);
		context.checking(new Expectations() {
			{
				allowing(searchCriteria).getIndexType();
				will(returnValue(indexType));

				oneOf(beanFactory).getBean("indexNotification");
				will(returnValue(mockIndexNotification));

				oneOf(mockIndexNotification).setIndexType(indexType);
				oneOf(mockIndexNotification).setUpdateType(updateType);
				oneOf(mockIndexNotification).setQueryString(with(any(String.class)));

				oneOf(mockQueryComposerFactory).getComposerForCriteria(searchCriteria);
				will(returnValue(queryComposer));

				oneOf(queryComposer).composeFuzzyQuery(with(searchCriteria), with(any(SearchConfig.class)));
				will(returnValue(new BooleanQuery()));

				oneOf(mockPersistenceEngine).saveOrUpdate(with(any(Persistable.class)));
				will(returnValue(null));
			}
		});

		indexNotificationServiceImpl.addViaQuery(updateType, searchCriteria, true);
	}
	
	/**
	 * Tests adding update index notification for specified index type and entity uid. 
	 */
	@Test
	public void testAddNotificationForEntityIndexUpdate() {
		final IndexType indexType = IndexType.CATEGORY;
		final Long affectedUid = 1L;
		
		final IndexNotification mockIndexNotification = context.mock(IndexNotification.class);
		context.checking(new Expectations() {
			{
				oneOf(beanFactory).getBean("indexNotification");
				will(returnValue(mockIndexNotification));

				oneOf(mockIndexNotification).setIndexType(indexType);
				oneOf(mockIndexNotification).setUpdateType(UpdateType.UPDATE);
				never(mockIndexNotification).setQueryString(with(any(String.class)));
				oneOf(mockIndexNotification).setAffectedUid(affectedUid);
				oneOf(mockIndexNotification).setAffectedEntityType("singleUnit");
				oneOf(mockPersistenceEngine).saveOrUpdate(mockIndexNotification);
				will(returnValue(null));
			}
		});


		indexNotificationServiceImpl.addNotificationForEntityIndexUpdate(indexType, affectedUid);
	}
}
