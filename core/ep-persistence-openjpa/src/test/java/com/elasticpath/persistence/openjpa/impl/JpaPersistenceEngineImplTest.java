/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.persistence.openjpa.impl;

import static org.junit.Assert.assertEquals;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.Query;

import org.apache.openjpa.kernel.Broker;
import org.apache.openjpa.persistence.OpenJPAQuery;
import org.apache.openjpa.persistence.QueryOperationType;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;

import com.elasticpath.persistence.api.Entity;
import com.elasticpath.persistence.api.FlushMode;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceSessionFactory;
import com.elasticpath.persistence.openjpa.ChangeType;
import com.elasticpath.persistence.openjpa.PersistenceEngineEntityListener;
import com.elasticpath.persistence.openjpa.PersistenceInterceptor;
import com.elasticpath.persistence.openjpa.impl.JpaPersistenceEngineImpl.JpaListener;
import com.elasticpath.persistence.openjpa.impl.JpaPersistenceEngineImpl.JpaTransactionListener;

/**
 * Test that the methods of the JPA Persistence Engine behave as expected.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.TooManyStaticImports", "PMD.UselessOverridingMethod" })
public class JpaPersistenceEngineImplTest {

	private static final String MERGED_OBJECT = "mergedObject";

	private static final String LIST_PARAMETER_NAME = "list";

	private static final String SOME_PARAMETER = "someParameter";
	private static final String SOME_OTHER_PARAMETER = "someOtherParameter";

	private static final String NAMED_QUERY = "NAMED_QUERY";

	private static final String RAWTYPES_WARNING_CODE = "rawtypes";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private JpaPersistenceEngineImpl persistenceEngine;
	
	private JpaPersistenceEngineImpl listeningPersistenceEngine;
	
	private EntityManager entityManager;

	private PersistenceSessionFactory sessionFactory;
	
	private Broker broker;
	
	private static final long UIDPK = 1000L;

	/**
	 * Set up mocks etc required by all tests.
	 * 
	 * @throws Exception in case of error during setup
	 */
	@Before
	public void setUp() throws Exception {
		// Mock objects that get injected
		entityManager = context.mock(EntityManager.class);
		sessionFactory = context.mock(PersistenceSessionFactory.class);
		broker = context.mock(Broker.class);
	
		// Set up the persistence engine to be tested, stub out listening methods and OpenJPA internals (e.g. broker).		
		persistenceEngine = new JpaPersistenceEngineImpl() {
			@Override
			public Broker getBroker() {
				return broker;
			}			

			@Override
			void beginBulkOperation(final String queryName, final OpenJPAQuery query, final Object[] parameters, final ChangeType changeType) {
				// Do nothing
			}

			@Override
			public void beginSingleOperation(final Persistable object, final ChangeType changeType) {
				// Do nothing
			}

			@Override
			void endBulkOperation(final ChangeType changeType) {
				// Do nothing
			}

			@Override
			public void endSingleOperation(final Persistable object, final ChangeType changeType) {
				// Do nothing
			}

			@Override
			public <T> T escapeParameter(final T parameter) {
				return super.escapeParameter(parameter);
			}
			
		};
		persistenceEngine.setEntityManager(entityManager);
		persistenceEngine.setSessionFactory(sessionFactory);
		
		// Set up a persistence engine to test for listening.
		listeningPersistenceEngine = new JpaPersistenceEngineImpl() {
			@Override
			public Broker getBroker() {
				return broker;
			}	
		};
		listeningPersistenceEngine.setEntityManager(entityManager);
		listeningPersistenceEngine.setSessionFactory(sessionFactory);
	}

	/**
	 * Test that bulk update creates a query and executes update on that query.
	 */
	@Test
	public final void testBulkUpdate() {
		final String queryString = "UPDATE SomeObjectImpl so SET so.one = 1";
		final OpenJPAQuery query = context.mock(OpenJPAQuery.class);
		context.checking(new Expectations() {
			{
				oneOf(entityManager).createQuery(queryString); will(Expectations.returnValue(query));
				oneOf(query).getOperation(); will(Expectations.returnValue(QueryOperationType.UPDATE));
				oneOf(query).executeUpdate();
			}
		});
		persistenceEngine.bulkUpdate(queryString);
	}

	/**
	 * Test that bulk update with a parameter creates a query, sets the parameter and executes
	 * update on that query.
	 */
	@Test
	public final void testBulkUpdateWithParameter() {
		final String queryString = "UPDATE SomeObjectImpl so SET so.one = 1 WHERE so.something = ?1";
		final OpenJPAQuery query = context.mock(OpenJPAQuery.class);
		final Object[] parameters = new Object[] { SOME_PARAMETER };
		context.checking(new Expectations() {
			{
				oneOf(entityManager).createQuery(queryString); will(Expectations.returnValue(query));
				oneOf(query).getOperation(); will(Expectations.returnValue(QueryOperationType.UPDATE));
				oneOf(query).setParameter(1, SOME_PARAMETER);
				oneOf(query).executeUpdate();
			}
		});
		persistenceEngine.bulkUpdate(queryString, parameters);
	}

	/**
	 * Test that clear calls the entity manager clear.
	 */
	@Test
	public final void testClear() {
		context.checking(new Expectations() {
			{
				oneOf(entityManager).clear();
			}
		});
		persistenceEngine.clear();
	}

	/**
	 * Test the delete method gets a reference to the object and calls the entity manager's remove
	 * and then calls a flush operation.
	 */
	@Test
	public final void testDelete() {
		final Persistable object = context.mock(Persistable.class);
		context.checking(new Expectations() {
			{
				oneOf(object).getUidPk(); will(Expectations.returnValue(UIDPK));
				oneOf(entityManager).getReference(object.getClass(), UIDPK); will(Expectations.returnValue(object));
				oneOf(entityManager).remove(object);
			}
		});
		persistenceEngine.delete(object);
	}

	/**
	 * Test execute session update generates a query and executes update on it.
	 */
	@Test
	public final void testExecuteSessionUpdate() {
		final String sql = "UPDATE SomeObjectImpl so SET so.one = 1";
		final OpenJPAQuery query = context.mock(OpenJPAQuery.class);
		context.checking(new Expectations() {
			{
				oneOf(entityManager).createQuery(sql); will(Expectations.returnValue(query));
				oneOf(query).getOperation(); will(Expectations.returnValue(QueryOperationType.UPDATE));
				oneOf(query).executeUpdate();
			}
		});
		persistenceEngine.executeSessionUpdate(sql);
	}

	/**
	 * Test flush calls the entity manager's flush.
	 */
	@Test
	public final void testFlush() {
		context.checking(new Expectations() {
			{
				oneOf(entityManager).flush();
			}
		});
		persistenceEngine.flush();
	}

	/**
	 * Test that the get method calls the eneity manager's find method.
	 */
	@Test
	public final void testGet() {
		final Class<Persistable> persistenceClass = Persistable.class;
		final Persistable object = context.mock(Persistable.class);
		context.checking(new Expectations() {
			{
				oneOf(entityManager).find(persistenceClass, UIDPK);
				will(Expectations.returnValue(object));
			}
		});
		Persistable result = persistenceEngine.get(persistenceClass, UIDPK);
		assertEquals("Returned object should be the result of the entity manager call", object, result);
	}

	/**
	 * Test Initialize calls the entity manager's refresh method.
	 */
	@Test
	public final void testInitialize() {
		final Persistable object = context.mock(Persistable.class); 
		context.checking(new Expectations() {
			{
				oneOf(entityManager).refresh(object);
			}
		});
		persistenceEngine.initialize(object);
	}

	/**
	 * Test the load method calls the entity manager's find method.
	 */
	@Test
	public final void testLoad() {
		final Class<Persistable> persistenceClass = Persistable.class;
		final Persistable object = context.mock(Persistable.class);
		context.checking(new Expectations() {
			{
				oneOf(entityManager).find(persistenceClass, UIDPK);
				will(Expectations.returnValue(object));
			}
		});
		Persistable result = persistenceEngine.load(persistenceClass, UIDPK);
		assertEquals("Returned object should be the result of the entity manager call", object, result);
	}

	/**
	 * Test load with new session creates a new session, calls the find method
	 * on the new entity manager and finally closes the new entity manager.
	 */
	@Test
	public final void testLoadWithNewSession() {
		final Class<Persistable> persistenceClass = Persistable.class;
		final Persistable object = context.mock(Persistable.class);
		final EntityManager newEntityManager = context.mock(EntityManager.class, "newEntityManager");
		final PlatformTransactionManager txManager = context.mock(PlatformTransactionManager.class);
		final JpaSessionImpl session = new JpaSessionImpl(entityManager, txManager, false) {

			@Override
			public EntityManager getEntityManager() {
				return newEntityManager;
			}
			
		};
		context.checking(new Expectations() {
			{
				oneOf(sessionFactory).createPersistenceSession(); will(Expectations.returnValue(session));
				oneOf(newEntityManager).find(persistenceClass, UIDPK); will(Expectations.returnValue(object));
				oneOf(newEntityManager).close();
			}
		});
		Persistable result = persistenceEngine.loadWithNewSession(persistenceClass, UIDPK);
		assertEquals("Returned object should be the result of the entity manager call", object, result);
	}

	/**
	 * Test that the merge method will execute the object's before persist action and then call the entity manager's
	 * merge function and flush. Confirm that the return value is a different object to that passed in.
	 */
	@Test
	public final void testMerge() {
		final FakePersistablePersistenceInterceptor object = context.mock(FakePersistablePersistenceInterceptor.class);
		final FakePersistablePersistenceInterceptor mergedObject = context.mock(FakePersistablePersistenceInterceptor.class, MERGED_OBJECT);
		context.checking(new Expectations() {
			{
				oneOf(object).executeBeforePersistAction();
				oneOf(entityManager).merge(object); will(Expectations.returnValue(mergedObject));
			}
		});
		Persistable result = persistenceEngine.merge(object);
		Assert.assertNotSame("Returned object should be different to the one given", object, result);
	}

	/**
	 * Test that calling retrieve with a collection of queries will create the query objects and
	 * consolidate the results.
	 */
	@Test
	public final void testRetrieveListOfQueries() {
		// Create the list of query strings
		final String query1 = "SELECT fo FROM FirstObjectImpl fo";
		final String query2 = "SELECT so FROM SecondObjectImpl so";
		final List<String> queries = new ArrayList<String>();
		queries.add(query1);
		queries.add(query2);
		
		// Mock the Query objects and Query results
		final Query query1Query = context.mock(Query.class, "query1");
		final Query query2Query = context.mock(Query.class, "query2");
		final List<Persistable> results1 = new ArrayList<Persistable>();
		final List<Persistable> results2 = new ArrayList<Persistable>();
		final Persistable obj1 = context.mock(Persistable.class, "result1");
		final Persistable obj2 = context.mock(Persistable.class, "result2");
		results1.add(obj1);
		results2.add(obj2);
		
		// Set expectations
		context.checking(new Expectations() {
			{
				oneOf(entityManager).createQuery(query1); will(Expectations.returnValue(query1Query));
				oneOf(entityManager).createQuery(query2); will(Expectations.returnValue(query2Query));
				oneOf(query1Query).getResultList(); will(Expectations.returnValue(results1));
				oneOf(query2Query).getResultList(); will(Expectations.returnValue(results2));
			}
		});
		List<Persistable> results = persistenceEngine.retrieve(queries);
		Assert.assertTrue("The first query's result should be present", results.contains(obj1));
		Assert.assertTrue("The second query's result should be present", results.contains(obj2));
	}

	/**
	 * Test that calling retrieve with a collection of queries that take parameters will create the query objects,
	 * set the parameters on each, and consolidate the results.
	 */
	@Test
	public final void testRetrieveListofQueriesWithParameters() {
		// Create the list of query strings
		final String query1 = "SELECT fo FROM FirstObjectImpl fo WHERE fo.field = ?1";
		final String query2 = "SELECT so FROM SecondObjectImpl so WHERE fo.field = ?1";
		final List<String> queries = new ArrayList<String>();
		queries.add(query1);
		queries.add(query2);

		// Mock the Query objects and Query results
		final Query query1Query = context.mock(Query.class, "query1");
		final Query query2Query = context.mock(Query.class, "query2");
		final List<Persistable> results1 = new ArrayList<Persistable>();
		final List<Persistable> results2 = new ArrayList<Persistable>();
		final Persistable obj1 = context.mock(Persistable.class, "result1");
		final Persistable obj2 = context.mock(Persistable.class, "result2");
		results1.add(obj1);
		results2.add(obj2);

		// Set expectations
		context.checking(new Expectations() {
			{
				oneOf(entityManager).createQuery(query1); will(Expectations.returnValue(query1Query));
				oneOf(entityManager).createQuery(query2); will(Expectations.returnValue(query2Query));
				oneOf(query1Query).setParameter(1, SOME_PARAMETER);
				oneOf(query2Query).setParameter(1, SOME_PARAMETER);
				oneOf(query1Query).getResultList(); will(Expectations.returnValue(results1));
				oneOf(query2Query).getResultList(); will(Expectations.returnValue(results2));
			}
		});

		final Object[] parameters = new Object[] { SOME_PARAMETER };
		List<Persistable> results = persistenceEngine.retrieve(queries, parameters);
		Assert.assertTrue("The first query's result should be present", results.contains(obj1));
		Assert.assertTrue("The second query's result should be present", results.contains(obj2));
	}

	/**
	 * Test that the save method calls the object's executeBeforePersistAction method
	 * followed by the entity manager's persist method.
	 */
	@Test
	public final void testSave() {
		final FakePersistablePersistenceInterceptor object = context.mock(FakePersistablePersistenceInterceptor.class);
		context.checking(new Expectations() {
			{
				oneOf(object).executeBeforePersistAction();
				oneOf(entityManager).persist(object);
			}
		});
		persistenceEngine.save(object);
	}

	/**
	 * Test that calling save or merge with a new object will result in a call to
	 * the object's executeBeforePersistAction method followed by the entity manager's persist method.
	 */
	@Test
	public final void testSaveOrMergeWithNewObject() {
		final FakePersistablePersistenceInterceptor object = context.mock(FakePersistablePersistenceInterceptor.class);
		context.checking(new Expectations() {
			{
				oneOf(object).isPersisted(); will(Expectations.returnValue(false));
				oneOf(object).executeBeforePersistAction();
				oneOf(entityManager).persist(object);
			}
		});
		Persistable result = persistenceEngine.saveOrMerge(object);
		Assert.assertSame("Returned object should be the same as the one passed in", result, object);
	}

	/**
	 * Test that calling save or merge with an existing object will result in a call to
	 * the object's executeBeforePersistAction method followed by the entity manager's merge method.
	 */
	@Test
	public final void testSaveOrMergeWithExistingObject() {
		final FakePersistablePersistenceInterceptor object = context.mock(FakePersistablePersistenceInterceptor.class);
		final FakePersistablePersistenceInterceptor mergedObject = context.mock(FakePersistablePersistenceInterceptor.class, MERGED_OBJECT);
		context.checking(new Expectations() {
			{
				oneOf(object).isPersisted(); will(Expectations.returnValue(true));
				oneOf(object).executeBeforePersistAction();
				oneOf(entityManager).merge(object); will(Expectations.returnValue(mergedObject));
			}
		});
		Persistable result = persistenceEngine.saveOrMerge(object);
		Assert.assertNotSame("Returned object should be different to the one passed in", result, object);
	}
	
	/**
	 * Test that calling update results in a call to the object's executeBeforePersistAction
	 * followed by a call to the entity manager's merge method.
	 */
	@Test
	public final void testUpdate() {
		final FakePersistablePersistenceInterceptor object = context.mock(FakePersistablePersistenceInterceptor.class);
		final FakePersistablePersistenceInterceptor mergedObject = context.mock(FakePersistablePersistenceInterceptor.class, MERGED_OBJECT);
		context.checking(new Expectations() {
			{
				oneOf(object).executeBeforePersistAction();
				oneOf(entityManager).merge(object); will(Expectations.returnValue(mergedObject));
			}
		});
		Persistable result = persistenceEngine.update(object);
		Assert.assertNotSame("Returned object should be different to the one passed in", result, object);
	}

	/**
	 * Test that a call to set query parameters sets all of the query's parameters.
	 */
	@Test
	public final void testSetQueryParameters() {
		final Query query = context.mock(Query.class);
		final String param1 = "parameter1";
		final Long param2 = 10L;
		final Boolean param3 = false;
		final int three = 3;
		final Object[] parameters = new Object[] { param1, param2, param3 };
		context.checking(new Expectations() {
			{
				oneOf(query).setParameter(1, param1);
				oneOf(query).setParameter(2, param2);
				oneOf(query).setParameter(three, param3);
			}
		});
		persistenceEngine.setQueryParameters(query, parameters);
	}

	/**
	 * Test the execute named query method creates a named query and then executes it.
	 */
	@Test
	public final void testExecuteNamedQuery() {
		final OpenJPAQuery query = context.mock(OpenJPAQuery.class);
		final int rowsUpdated = 103;
		context.checking(new Expectations() {
			{
				oneOf(entityManager).createNamedQuery(NAMED_QUERY); will(Expectations.returnValue(query));
				oneOf(query).getOperation(); will(Expectations.returnValue(QueryOperationType.UPDATE));
				oneOf(query).executeUpdate(); will(Expectations.returnValue(rowsUpdated));
			}
		});
		int result = persistenceEngine.executeNamedQuery(NAMED_QUERY);
		assertEquals("The return value should be the row count returned by executeUpdate", rowsUpdated, result);
	}

	/**
	 * Test that the insert list into query method substitutes the list of parameters for the named parameter
	 * when it calls the entity manager's create query method.
	 */
	@Test
	public final void testInsertListIntoQuery() {
		final String queryString = "SELECT so FROM SomeObjectImpl so WHERE so.uidPk in (:list) AND so.field = ?1";
		final String parameters = "1001, 1002, 1003";
		final OpenJPAQuery query = context.mock(OpenJPAQuery.class);
		context.checking(new Expectations() {
			{
				oneOf(query).getQueryString(); will(Expectations.returnValue(queryString));
				oneOf(entityManager).createQuery("SELECT so FROM SomeObjectImpl so WHERE so.uidPk in (1001, 1002, 1003) AND so.field = ?1");
			}
		});
		persistenceEngine.insertListIntoQuery(query, LIST_PARAMETER_NAME, parameters);
	}

	
	/**
	 * Test that splitting a large collection (i.e. > 900 parameters) creates the correct number of
	 * resultant collection strings.
	 */
	@Test
	public final void testSplitCollectionWithLargeCollection() {
		final int parameterCount = 1000;
		final Set<Long> values = new HashSet<Long>();
		for (long l = 0; l < parameterCount; l++) {
			values.add(l);
		}
		List<String> result = persistenceEngine.splitCollection(values, parameterCount);
		assertEquals("The method should have split the collection into 2 strings", 2, result.size());
	}

	/**
	 * Test that splitting a small collection doesn't actually split the collection but just returns
	 * a single collection string.
	 */
	@Test
	public final void testSplitCollectionWithSmallCollection() {
		final int parameterCount = 3;
		final List<Long> values = new ArrayList<Long>();
		for (long l = 1; l <= parameterCount; l++) {
			values.add(l);
		}
		List<String> result = persistenceEngine.splitCollection(values, parameterCount);
		assertEquals("There should only be a single result string", 1, result.size());
		assertEquals("The results string should be the collection passed in", "1,2,3", result.get(0));
	}

	/**
	 * Test that splitting a small collection doesn't actually split the collection but just returns
	 * a single collection string. A single quote ' should be escaped to '' according to SQL escaping rules.
	 */
	@Test
	public final void testSplitCollectionWithEscaping() {
		final int parameterCount = 3;
		final List<String> values = new ArrayList<String>();
		values.add("SNAP' IT UP");
		values.add("SNAP IT UP UK");
		values.add("SLR' WORLD");
		List<String> result = persistenceEngine.splitCollection(values, parameterCount);
		assertEquals("There should only be a single result string", 1, result.size());
		assertEquals("The results string should be the collection passed in", "'SNAP'' IT UP','SNAP IT UP UK','SLR'' WORLD'", result.get(0));
	}

	/**
	 * Tests that if a parameter is a string and it contains single quotes they will be escaped according to SQL rules.
	 */
	@Test
	public final void testEscapeParameterWithStrings() {
		final String noSingleQuotes = "some text";
		final String hasSingleQuotes = "some' text'";
		String result = persistenceEngine.escapeParameter(noSingleQuotes);
		assertEquals("The string should be the same as before", noSingleQuotes, result);
		result = persistenceEngine.escapeParameter(hasSingleQuotes);
		assertEquals("Single quotes should be escaped", "some'' text''", result);
	}
	
	/**
	 * Currently only string parameters are escaped. Parameters of other types should not be processed.
	 */
	@Test
	public final void testEscapeParameterWithNonStrings() {
		Integer one = 1;
		Integer result = persistenceEngine.escapeParameter(one);
		Assert.assertSame("Same object should be returned", one, result);
	}
	
	/**
	 * Test that when save is called that the JpaListener is added to the broker and then removed after the actual save,
	 * and that the correct listener event gets called.
	 */
	@Test
	public void testListeningOnSave() {
		final FakePersistablePersistenceInterceptor persistableObject = context.mock(FakePersistablePersistenceInterceptor.class);
		final PersistenceEngineEntityListener listener = context.mock(PersistenceEngineEntityListener.class);

		@SuppressWarnings(RAWTYPES_WARNING_CODE)
		final Class<?>[] emptyArray = new Class[1]; // Used to get the correct class type for JMock.
		
		listeningPersistenceEngine.addPersistenceEngineEntityListener(listener);
		
		context.checking(new Expectations() { {
			oneOf(broker).addLifecycleListener(with(Expectations.aNonNull(JpaListener.class)), with(Expectations.aNull(emptyArray.getClass())));
			oneOf(broker).addTransactionListener(with(Expectations.aNonNull(JpaTransactionListener.class)));
			oneOf(persistableObject).executeBeforePersistAction();
			oneOf(entityManager).persist(persistableObject);
			oneOf(listener).beginSingleOperation(persistableObject, ChangeType.CREATE);
			oneOf(listener).endSingleOperation(persistableObject, ChangeType.CREATE);
		} });
		
		listeningPersistenceEngine.save(persistableObject);
	}
	
	/**
	 * Test that when merge is called that the JpaListener is added to the broker and then removed after the actual merge.
	 */
	@Test
	public void testListeningOnMerge() {
		final FakePersistablePersistenceInterceptor persistableObject = context.mock(FakePersistablePersistenceInterceptor.class);
		final Persistable mergedObject = context.mock(Persistable.class, MERGED_OBJECT);

		@SuppressWarnings(RAWTYPES_WARNING_CODE)
		final Class<?>[] emptyArray = new Class[1]; // Used to get the correct class type for JMock.
		
		context.checking(new Expectations() { {
			oneOf(broker).addLifecycleListener(with(Expectations.aNonNull(JpaListener.class)), with(Expectations.aNull(emptyArray.getClass())));
			oneOf(broker).addTransactionListener(with(Expectations.aNonNull(JpaTransactionListener.class)));
			oneOf(persistableObject).executeBeforePersistAction();
			oneOf(entityManager).merge(persistableObject); will(Expectations.returnValue(mergedObject));
		} });
		
		listeningPersistenceEngine.merge(persistableObject);
		
	}
	
	/**
	 * Test that when delete is called that the JpaListener is added to the broker and then removed after the actual delete.
	 */
	@Test
	public void testListeningOnDelete() {
		final Entity entity = context.mock(Entity.class);

		@SuppressWarnings(RAWTYPES_WARNING_CODE)
		final Class<?>[] emptyArray = new Class[1]; // Used to get the correct class type for JMock.
		
		context.checking(new Expectations() { {
			oneOf(broker).addLifecycleListener(with(Expectations.aNonNull(JpaListener.class)), with(Expectations.aNull(emptyArray.getClass())));
			oneOf(broker).addTransactionListener(with(Expectations.aNonNull(JpaTransactionListener.class)));
			oneOf(entity).getUidPk(); will(Expectations.returnValue(UIDPK));
			oneOf(entityManager).getReference(entity.getClass(), UIDPK); will(Expectations.returnValue(entity));
			oneOf(entityManager).remove(entity);
		} });
		
		listeningPersistenceEngine.delete(entity);
		
	}

	/**
	 * Test that when we make two changes, one after the other, that the start and stop is only called once for each change.
	 */
	@Test
	public final void testConsecutiveChanges() {
		final FakePersistablePersistenceInterceptor persistableObject = context.mock(FakePersistablePersistenceInterceptor.class);
		final PersistenceEngineEntityListener listener = context.mock(PersistenceEngineEntityListener.class);

		@SuppressWarnings(RAWTYPES_WARNING_CODE)
		final Class<?>[] emptyArray = new Class[1]; // Used to get the correct class type for JMock.
		
		listeningPersistenceEngine.addPersistenceEngineEntityListener(listener);
		
		context.checking(new Expectations() { {
				exactly(1)
						.of(broker)
						.addLifecycleListener(
								with(Expectations.aNonNull(JpaListener.class)),
								with(Expectations.aNull(emptyArray.getClass())));
			oneOf(broker).addTransactionListener(with(Expectations.aNonNull(JpaTransactionListener.class)));
			exactly(2).of(persistableObject).executeBeforePersistAction();
			exactly(2).of(entityManager).persist(persistableObject);
			exactly(2).of(listener).beginSingleOperation(persistableObject, ChangeType.CREATE);
			exactly(2).of(listener).endSingleOperation(persistableObject, ChangeType.CREATE);
		} });
		
		listeningPersistenceEngine.save(persistableObject);
		listeningPersistenceEngine.save(persistableObject);
	}

	/**
	 * Test method for {@link JpaPersistenceEngineImpl#retrieveByNamedQuery(java.lang.String, java.lang.Object[])}.
	 */
	@Test
	public final void testRetrieveByNamedQueryStringFlushModeObjectArray() {
		final Query query = context.mock(Query.class);

		context.checking(new Expectations() { {
			oneOf(entityManager).createNamedQuery(NAMED_QUERY); will(returnValue(query));
			oneOf(query).setParameter(1, SOME_PARAMETER);
			oneOf(query).setParameter(2, SOME_OTHER_PARAMETER);
			oneOf(query).setFlushMode(FlushModeType.COMMIT);
			oneOf(query).getResultList(); will(returnValue(Collections.singletonList("result")));
		} });

		final Object[] parameters = new Object[] { SOME_PARAMETER, SOME_OTHER_PARAMETER };
		List<Object> result = persistenceEngine.retrieveByNamedQuery(NAMED_QUERY, FlushMode.COMMIT, parameters);
		assertEquals("Retrieve gives back results from OpenJPA", Collections.singletonList("result"), result);
	}
	
	// TODO - finish implementing the tests below
	
	/**
	 * Test method for {@link JpaPersistenceEngineImpl#retrieve(java.lang.String)}.
	 */
	@Ignore
	public final void testRetrieveString() {
		final String query = "SELECT so FROM SomeObjectImpl so";
		persistenceEngine.retrieve(query);
	}

	/**
	 * Test method for {@link JpaPersistenceEngineImpl#retrieve(java.lang.String, int, int)}.
	 */
	@Ignore
	public final void testRetrieveStringIntInt() {
		final String queryStr = "SELECT slo FROM SomeLargeObjectImpl slo";
		final int firstResult = 10;
		final int maxResults = 100;
		persistenceEngine.retrieve(queryStr, firstResult, maxResults);
	}

	/**
	 * Test method for {@link JpaPersistenceEngineImpl#retrieve(java.lang.String, java.lang.Object[])}.
	 */
	@Ignore
	public final void testRetrieveStringObjectArray() {
		final String query = "SELECT so FROM SomeObjectImpl so WHERE so.field = ?1";
		final Object[] parameters = new Object[] { SOME_PARAMETER };
		persistenceEngine.retrieve(query, parameters);
	}

	/**
	 * Test method for {@link JpaPersistenceEngineImpl#retrieveWithNewSession(java.lang.String, java.lang.Object[])}.
	 */
	@Ignore
	public final void testRetrieveWithNewSessionStringObjectArray() {
		final String query = "SELECT so FROM SomeObjectImpl so WHERE so.field = ?1";
		final Object[] parameters = new Object[] { SOME_PARAMETER };
		persistenceEngine.retrieveWithNewSession(query, parameters);
	}

	/**
	 * Test method for {@link com.elasticpath.persistence.openjpa.impl.JpaPersistenceEngineImpl
	 * #retrieveWithListWithNewSession(java.lang.String, java.lang.String, java.util.Collection, java.lang.Object[])}.
	 */
	@Ignore
	public final void testRetrieveWithListWithNewSession() {
		final String query = "SELECT so FROM SomeObjectImpl so WHERE so.uidPk in (:list) AND so.field = ?1";
		final Object[] parameters = new Object[] { SOME_PARAMETER };
		final Set<Long> values = new HashSet<Long>();
		final Long uidPk2 = 1001L;
		values.add(UIDPK);
		values.add(uidPk2);
		persistenceEngine.retrieveWithListWithNewSession(query, LIST_PARAMETER_NAME, values, parameters);
	}

	/**
	 * Test method for {@link JpaPersistenceEngineImpl#retrieveWithNewSession(java.lang.String)}.
	 */
	@Ignore
	public final void testRetrieveWithNewSessionString() {
		final String query = "SELECT so FROM SomeObjectImpl so";
		persistenceEngine.retrieveWithNewSession(query);
	}

	/**
	 * Test method for {@link JpaPersistenceEngineImpl#retrieve(java.lang.String, java.lang.Object[], boolean)}.
	 */
	@Ignore
	public final void testRetrieveStringObjectArrayBoolean() {
		final String query = "SELECT so FROM SomeObjectImpl so WHERE so.field = ?1";
		final Object[] parameters = new Object[] { SOME_PARAMETER };
		boolean cacheQuery = false;
		persistenceEngine.retrieve(query, parameters, cacheQuery);
	}

	/**
	 * Test method for {@link JpaPersistenceEngineImpl#retrieve(java.lang.String, java.lang.Object[], int, int)}.
	 */
	@Ignore
	public final void testRetrieveStringObjectArrayIntInt() {
		final String queryStr = "SELECT slo FROM SomeLargeObjectImpl slo WHERE slo.field = ?1";
		final Object[] parameters = new Object[] { SOME_PARAMETER };
		final int firstResult = 1;
		final int maxResults = 10;
		persistenceEngine.retrieve(queryStr, parameters, firstResult, maxResults);
	}

	/**
	 * Test method for {@link JpaPersistenceEngineImpl#retrieveByNamedQuery(java.lang.String)}.
	 */
	@Ignore
	public final void testRetrieveByNamedQueryString() {
		persistenceEngine.retrieveByNamedQuery(NAMED_QUERY);
	}

	/**
	 * Test method for {@link JpaPersistenceEngineImpl#retrieveByNamedQuery(java.lang.String, java.lang.Object[])}.
	 */
	@Ignore
	public final void testRetrieveByNamedQueryStringObjectArray() {
		final Object[] parameters = new Object[] { SOME_PARAMETER };
		persistenceEngine.retrieveByNamedQuery(NAMED_QUERY, parameters);
	}

	/**
	 * Test method for {@link JpaPersistenceEngineImpl#retrieveByNamedQuery(java.lang.String, java.util.Map)}.
	 */
	@Ignore
	public final void testRetrieveByNamedQueryStringMapOfStringObject() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("parameter", SOME_PARAMETER);
		persistenceEngine.retrieveByNamedQuery(NAMED_QUERY, parameters);
	}

	/**
	 * Test method for {@link JpaPersistenceEngineImpl#retrieveByNamedQuery(java.lang.String, int, int)}.
	 */
	@Ignore
	public final void testRetrieveByNamedQueryStringIntInt() {
		final int firstResult = 1;
		final int maxResults = 10;
		persistenceEngine.retrieveByNamedQuery(NAMED_QUERY, firstResult, maxResults);
	}

	/**
	 * Test method for {@link com.elasticpath.persistence.openjpa.impl.JpaPersistenceEngineImpl
	 * #retrieveByNamedQuery(java.lang.String, java.lang.Object[], int, int)}.
	 */
	@Ignore
	public final void testRetrieveByNamedQueryStringObjectArrayIntInt() {
		final Object[] parameters = new Object[] { SOME_PARAMETER };
		final int firstResult = 1;
		final int maxResults = 10;
		persistenceEngine.retrieveByNamedQuery(NAMED_QUERY, parameters, firstResult, maxResults);
	}

	/**
	 * Test method for {@link com.elasticpath.persistence.openjpa.impl.JpaPersistenceEngineImpl
	 * #retrieveByNamedQueryWithList(java.lang.String, java.lang.String, java.util.Collection)}.
	 */
	@Ignore
	public final void testRetrieveByNamedQueryWithListStringStringCollectionOfE() {
		final Set<Long> values = new HashSet<Long>();
		final Long uidPk2 = 1001L;
		values.add(UIDPK);
		values.add(uidPk2);
		persistenceEngine.retrieveByNamedQueryWithList(NAMED_QUERY, LIST_PARAMETER_NAME, values);
	}

	/**
	 * Test method for {@link com.elasticpath.persistence.openjpa.impl.JpaPersistenceEngineImpl
	 * #retrieveByNamedQueryWithList(java.lang.String, java.lang.String, java.util.Collection, java.lang.Object[])}.
	 */
	@Ignore
	public final void testRetrieveByNamedQueryWithListStringStringCollectionOfEObjectArray() {
		final Object[] parameters = new Object[] { SOME_PARAMETER };
		final Set<Long> values = new HashSet<Long>();
		final Long uidPk2 = 1001L;
		values.add(UIDPK);
		values.add(uidPk2);
		persistenceEngine.retrieveByNamedQueryWithList(NAMED_QUERY, LIST_PARAMETER_NAME, values, parameters);
	}

	/**
	 * Test method for {@link JpaPersistenceEngineImpl#executeNamedQuery(java.lang.String, java.lang.Object[])}.
	 */
	@Ignore
	public final void testExecuteNamedQueryStringObjectArray() {
		final Object[] parameters = new Object[] { SOME_PARAMETER };
		persistenceEngine.executeNamedQuery(NAMED_QUERY, parameters);
	}

	/**
	 * Test method for {@link com.elasticpath.persistence.openjpa.impl.JpaPersistenceEngineImpl
	 * #executeNamedQueryWithList(java.lang.String, java.lang.String, java.util.Collection)}.
	 */
	@Ignore
	public final void testExecuteNamedQueryWithListStringStringCollectionOfE() {
		final Set<Long> values = new HashSet<Long>();
		final Long uidPk2 = 1001L;
		values.add(UIDPK);
		values.add(uidPk2);
		persistenceEngine.executeNamedQueryWithList(NAMED_QUERY, LIST_PARAMETER_NAME, values);
	}

	/**
	 * Test method for {@link com.elasticpath.persistence.openjpa.impl.JpaPersistenceEngineImpl
	 * #executeNamedQueryWithList(java.lang.String, java.lang.String, java.util.Collection, java.lang.Object[])}.
	 */
	@Ignore
	public final void testExecuteNamedQueryWithListStringStringCollectionOfEObjectArray() {
		final Object[] parameters = new Object[] { SOME_PARAMETER };
		final Set<Long> values = new HashSet<Long>();
		final Long uidPk2 = 1001L;
		values.add(UIDPK);
		values.add(uidPk2);
		persistenceEngine.executeNamedQueryWithList(NAMED_QUERY, LIST_PARAMETER_NAME, values, parameters);
	}

	/**
	 * Interface which extends both Persistable and PersistenceInterceptor.
	 */
	private interface FakePersistablePersistenceInterceptor extends Persistable, PersistenceInterceptor {
		
	}
}
