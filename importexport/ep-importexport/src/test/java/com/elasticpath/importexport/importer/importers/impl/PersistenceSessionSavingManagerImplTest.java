package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;

/**
 * Test for <code>PersistenceSessionSavingManagerImpl</code>.
 */
public class PersistenceSessionSavingManagerImplTest {
	
	private PersistenceSessionSavingManagerImpl savingManager;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PersistenceEngine mockPersistenceEngine;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() throws Exception {
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		savingManager = new PersistenceSessionSavingManagerImpl();
		savingManager.setPersistenceEngine(mockPersistenceEngine);
		assertNotNull(savingManager.getPersistenceEngine());
	}
	
	/**
	 * Test save method of PersistenceSessionSavingManagerImpl.
	 */
	@Test
	public void testSaveMethod() {
		final Persistable persistable = new AbstractPersistableImpl() {

			private static final long serialVersionUID = 4900939730267037616L;

			public long getUidPk() {
				return 1L;
			}

			public void setUidPk(final long uidPk) {
				// do nothing
			}
			
		};

		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).save(persistable);
			}
		});
		savingManager.save(persistable);
	}
	
	/**
	 * Test update method of PersistenceSessionSavingManagerImpl.
	 */
	@Test
	public void testUpdateMethod() {
		
		final Persistable persistable = new AbstractPersistableImpl() {

			private static final long serialVersionUID = -3813241696855251804L;

			public long getUidPk() {
				return 1L;
			}

			public void setUidPk(final long uidPk) {
				// do nothing
			}
			
		};

		context.checking(new Expectations() {
			{
				oneOf(mockPersistenceEngine).update(persistable);
				will(returnValue(persistable));
			}
		});
		assertSame(persistable, savingManager.update(persistable));
	}
}
