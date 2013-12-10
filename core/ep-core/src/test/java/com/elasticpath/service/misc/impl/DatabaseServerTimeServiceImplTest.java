package com.elasticpath.service.misc.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Query;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>DatabaseServerTimeServiceImpl</code>.
 */

public class DatabaseServerTimeServiceImplTest {

	private static final String ORACLE_DB_NAME = "oracle";
	private static final String TIME_RETRIEVE_QUERY = "SELECT CURRENT_TIMESTAMP FROM JPA_GENERATED_KEYS WHERE ID='DEFAULT'";
	private static final String ORACLE_TIME_RETRIEVE_QUERY = "SELECT LOCALTIMESTAMP FROM DUAL";

	private static final long THRESHOLD = 1000;

	private DatabaseServerTimeServiceImpl timeService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private PersistenceEngine mockPersistenceEngine;
	private PersistenceSession mockPersistenceSession;
	private Connection mockPersistenceConnection;
	private DatabaseMetaData mockPersistenceMetaData;
	private Query mockQuery;

	/**
	 * Prepares for a test.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		mockPersistenceSession = context.mock(PersistenceSession.class);
		mockPersistenceConnection = context.mock(Connection.class);
		mockPersistenceMetaData = context.mock(DatabaseMetaData.class);
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).getPersistenceSession();
				will(returnValue(mockPersistenceSession));

				oneOf(mockPersistenceEngine).getConnection();
				will(returnValue(mockPersistenceConnection));

				oneOf(mockPersistenceConnection).getMetaData();
				will(returnValue(mockPersistenceMetaData));

				oneOf(mockPersistenceConnection).close();
			}
		});

		this.timeService = new DatabaseServerTimeServiceImpl();
		this.timeService.setPersistenceEngine(mockPersistenceEngine);
	}

	/**
	 * Cleans up after a test.
	 *
	 * @throws Exception on error
	 */
	@After
	public void tearDown() throws Exception {
		// Reset the database type back to null, so the next test will determine it from scratch
		this.timeService.setDatabaseType(null);
		expectationsFactory.close();
	}

	/**
	 * Test getting the current date and time using the database time service.
	 */
	@Test
	public void testGetCurrentTime() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceMetaData).getDatabaseProductName();
				will(returnValue(""));
			}
		});
		mockQuery = context.mock(Query.class);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceSession).createSQLQuery(TIME_RETRIEVE_QUERY);
				will(returnValue(mockQuery));
			}
		});

		final List<Date> results = new ArrayList<Date>();
		final Date now = new Date();
		results.add(now);
		context.checking(new Expectations() {
			{
				allowing(mockQuery).list();
				will(returnValue(results));
			}
		});
		assertSame(now, this.timeService.getCurrentTime());
	}

	/**
	 * Test getting the current date and time from an Oracle database time service.
	 */
	@Test
	public void testGetCurrentTimeOracle() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceMetaData).getDatabaseProductName();
				will(returnValue(ORACLE_DB_NAME));
			}
		});
		mockQuery = context.mock(Query.class);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceSession).createSQLQuery(ORACLE_TIME_RETRIEVE_QUERY);
				will(returnValue(mockQuery));
			}
		});

		final List<Date> results = new ArrayList<Date>();
		final Date now = new Date();
		results.add(now);
		context.checking(new Expectations() {
			{
				allowing(mockQuery).list();
				will(returnValue(results));
			}
		});
		assertSame(now, this.timeService.getCurrentTime());
	}

	/**
	 * Test getting no results from the database for the current date and time.
	 * This should then return the current date and time from the application server.
	 */
	@Test
	public void testGetCurrentTimeNoResults() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceMetaData).getDatabaseProductName();
				will(returnValue(""));
			}
		});
		mockQuery = context.mock(Query.class);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceSession).createSQLQuery(TIME_RETRIEVE_QUERY);
				will(returnValue(mockQuery));
			}
		});

		final List<Date> results = new ArrayList<Date>();
		final Date now = new Date();
		context.checking(new Expectations() {
			{
				allowing(mockQuery).list();
				will(returnValue(results));
			}
		});
		final Date currentTime = this.timeService.getCurrentTime();
		assertNotNull(currentTime);

		// Verify that the fallback date returned is within a second of the date of the call
		final Date minThreshold = new Date(now.getTime() - THRESHOLD);
		assertTrue(currentTime.after(minThreshold));
		final Date maxThreshold = new Date(now.getTime() + THRESHOLD);
		assertTrue(currentTime.before(maxThreshold));
	}

	/**
	 * Test getting null for results from the database for the current date and time.
	 * This should then return the current date and time from the application server.
	 */
	@Test
	public void testGetCurrentTimeNullResults() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceMetaData).getDatabaseProductName();
				will(returnValue(""));
			}
		});
		mockQuery = context.mock(Query.class);
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceSession).createSQLQuery(TIME_RETRIEVE_QUERY);
				will(returnValue(mockQuery));
			}
		});

		final List<Date> results = null;
		final Date now = new Date();
		context.checking(new Expectations() {
			{
				allowing(mockQuery).list();
				will(returnValue(results));
			}
		});
		final Date currentTime = this.timeService.getCurrentTime();
		assertNotNull(currentTime);

		// Verify that the fallback date returned is within a second of the date of the call
		final Date minThreshold = new Date(now.getTime() - THRESHOLD);
		assertTrue(currentTime.after(minThreshold));
		final Date maxThreshold = new Date(now.getTime() + THRESHOLD);
		assertTrue(currentTime.before(maxThreshold));
	}
}
