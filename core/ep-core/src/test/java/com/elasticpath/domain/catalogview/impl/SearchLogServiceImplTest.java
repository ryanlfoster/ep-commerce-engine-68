package com.elasticpath.domain.catalogview.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.search.SfSearchLog;
import com.elasticpath.domain.search.impl.SfSearchLogImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.catalogview.impl.SfSearchLogServiceImpl;

/**
 * Tests for <code>SearchLogServiceImpl</code>.
 */
public class SearchLogServiceImplTest {

	private SfSearchLogServiceImpl sfSearchLogService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PersistenceEngine mockPersistenceEngine;

	/**
	 * Set up the test case.
	 * 
	 * @throws Exception if an error occurs
	 */
	@Before
	public void setUp() throws Exception {

		sfSearchLogService = new SfSearchLogServiceImpl();
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		sfSearchLogService.setPersistenceEngine(mockPersistenceEngine);
	}

	/**
	 * Test method for get and set persistence engine.
	 */
	@Test
	public void testGetSetPersistenceEngine() {
		assertNotNull(this.sfSearchLogService.getPersistenceEngine());
	}

	/**
	 * Test method for add.
	 */
	@Test
	public void testAdd() {
		final SfSearchLog sfSearchLog = new SfSearchLogImpl();

		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).save(with(same(sfSearchLog)));
			}
		});
		sfSearchLogService.add(sfSearchLog);
	}

	/**
	 * Test method for update.
	 */
	@Test
	public void testUpdate() {
		final SfSearchLog sfSearchLog = new SfSearchLogImpl();
		final SfSearchLog updatedSfSearchLog = new SfSearchLogImpl();

		// expectations
		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).merge(with(same(sfSearchLog)));
				will(returnValue(updatedSfSearchLog));
			}
		});
		final SfSearchLog returnedSfSearchLog = sfSearchLogService.update(sfSearchLog);
		assertSame(updatedSfSearchLog, returnedSfSearchLog);
	}

	/**
	 * Test method for load.
	 */
	@Test
	public void testLoad() {
		final long uid = 1234L;
		final SfSearchLog sfSearchLog = new SfSearchLogImpl();
		sfSearchLog.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).load(SfSearchLog.class, uid);
				will(returnValue(sfSearchLog));
			}
		});
		final SfSearchLog loadedSfSearchLog = this.sfSearchLogService.load(uid);
		assertSame(sfSearchLog, loadedSfSearchLog);
	}

	/**
	 * Test method for get.
	 */
	@Test
	public void testGet() {
		final long uid = 1234L;
		final SfSearchLog sfSearchLog = new SfSearchLogImpl();
		sfSearchLog.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).get(SfSearchLog.class, uid);
				will(returnValue(sfSearchLog));
			}
		});
		final SfSearchLog loadedSfSearchLog = this.sfSearchLogService.get(uid);
		assertSame(sfSearchLog, loadedSfSearchLog);
	}

	/**
	 * Test method for getObject.
	 */
	@Test
	public void testGetObject() {
		final long uid = 1234L;
		final SfSearchLog sfSearchLog = new SfSearchLogImpl();
		sfSearchLog.setUidPk(uid);
		// expectations
		context.checking(new Expectations() {
			{
				allowing(mockPersistenceEngine).get(SfSearchLog.class, uid);
				will(returnValue(sfSearchLog));
			}
		});
		final Object loadedSfSearchLog = this.sfSearchLogService.getObject(uid);
		assertSame(sfSearchLog, loadedSfSearchLog);
	}
}
