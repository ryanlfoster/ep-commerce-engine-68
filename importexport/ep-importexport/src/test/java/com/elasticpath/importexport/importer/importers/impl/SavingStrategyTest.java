package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.persistence.api.PersistenceEngine;

/**
 * Test using of different saving strategies.
 */
public class SavingStrategyTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private PersistenceEngine mockPersistenceEngine;

	@SuppressWarnings("unchecked")
	private final DomainAdapter<Persistable, Dto> mockDomainAdapter = context.mock(DomainAdapter.class);

	private LifecycleListener mockLifecycleListener;

	private Dto dto;

	private Persistable persistentObject;

	private Persistable notPersistentObject;

	@Before
	public void setUp() throws Exception {
		mockPersistenceEngine = context.mock(PersistenceEngine.class);
		mockLifecycleListener = context.mock(LifecycleListener.class);

		dto = new Dto() {
			private static final long serialVersionUID = 6318330000022714013L;
		};

		persistentObject = new AbstractPersistableImpl() {

			private static final long serialVersionUID = 2663870334606727009L;

			@Override
			public long getUidPk() {
				return 1;
			}

			@Override
			public void setUidPk(final long uidPk) {
				// do nothing
			}

		};

		notPersistentObject = new AbstractPersistableImpl() {

			private static final long serialVersionUID = -8871421089426953792L;

			@Override
			public long getUidPk() {
				return 0;
			}

			@Override
			public void setUidPk(final long uidPk) {
				// do nothing
			}

		};
	}

	/**
	 * Test for Insert Or Update strategy.
	 */
	@Test
	public void testInsertOrUpdateStrategy() {
		PersistenceSessionSavingManagerImpl savingManager = new PersistenceSessionSavingManagerImpl();
		savingManager.setPersistenceEngine(mockPersistenceEngine);
		SavingStrategy<Persistable, Dto> savingStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT_OR_UPDATE,
				savingManager);

		savingStrategy.setLifecycleListener(mockLifecycleListener);
		savingStrategy.setDomainAdapter(mockDomainAdapter);

		context.checking(new Expectations() {
			{
				oneOf(mockLifecycleListener).beforeSave(persistentObject);
				oneOf(mockLifecycleListener).afterSave(persistentObject);

				oneOf(mockDomainAdapter).buildDomain(dto, persistentObject);
				will(returnValue(persistentObject));
				oneOf(mockPersistenceEngine).update(with(any(Persistable.class)));
				will(returnValue(persistentObject));
			}
		});

		assertEquals(persistentObject, savingStrategy.populateAndSaveObject(persistentObject, dto));

		context.checking(new Expectations() {
			{
				oneOf(mockLifecycleListener).beforeSave(notPersistentObject);
				oneOf(mockLifecycleListener).afterSave(notPersistentObject);

				oneOf(mockDomainAdapter).createDomainObject();
				will(returnValue(notPersistentObject));
				oneOf(mockDomainAdapter).buildDomain(dto, notPersistentObject);
				will(returnValue(notPersistentObject));
				oneOf(mockPersistenceEngine).save(with(any(Persistable.class)));
			}
		});

		assertEquals(notPersistentObject, savingStrategy.populateAndSaveObject(null, dto));
	}

	/**
	 * Test for Insert strategy.
	 */
	@Test
	public void testInsertStrategy() {
		PersistenceSessionSavingManagerImpl savingManager = new PersistenceSessionSavingManagerImpl();
		savingManager.setPersistenceEngine(mockPersistenceEngine);
		SavingStrategy<Persistable, Dto> savingStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT, savingManager);
		
		savingStrategy.setLifecycleListener(mockLifecycleListener);
		savingStrategy.setDomainAdapter(mockDomainAdapter);
		assertNull(savingStrategy.populateAndSaveObject(persistentObject, dto));

		context.checking(new Expectations() {
			{
				oneOf(mockLifecycleListener).beforeSave(notPersistentObject);
				oneOf(mockLifecycleListener).afterSave(notPersistentObject);

				oneOf(mockDomainAdapter).createDomainObject();
				will(returnValue(notPersistentObject));
				oneOf(mockDomainAdapter).buildDomain(dto, notPersistentObject);
				will(returnValue(notPersistentObject));
				oneOf(mockPersistenceEngine).save(with(any(Persistable.class)));
			}
		});
		assertEquals(notPersistentObject, savingStrategy.populateAndSaveObject(null, dto));
	}

	/**
	 * Test for Update strategy.
	 */
	@Test
	public void testUpdateStrategy() {
		PersistenceSessionSavingManagerImpl savingManager = new PersistenceSessionSavingManagerImpl();
		savingManager.setPersistenceEngine(mockPersistenceEngine);
		SavingStrategy<Persistable, Dto> savingStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.UPDATE, savingManager);

		savingStrategy.setLifecycleListener(mockLifecycleListener);
		savingStrategy.setDomainAdapter(mockDomainAdapter);

		context.checking(new Expectations() {
			{
				oneOf(mockLifecycleListener).beforeSave(persistentObject);
				oneOf(mockLifecycleListener).afterSave(persistentObject);

				oneOf(mockDomainAdapter).buildDomain(dto, persistentObject);
				will(returnValue(persistentObject));
				oneOf(mockPersistenceEngine).update(with(any(Persistable.class)));
				will(returnValue(persistentObject));
			}
		});
		assertEquals(persistentObject, savingStrategy.populateAndSaveObject(persistentObject, dto));

		assertNull(savingStrategy.populateAndSaveObject(null, dto));
	}

	@Test(expected = ImportRuntimeException.class)
	public void testStrategiesExceptionsForPopulateAndSaveObject() {
		SavingStrategy<Persistable, Dto> createStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT, null);
		createStrategy.populateAndSaveObject(persistentObject, dto);
	}

	@Test(expected = ImportRuntimeException.class)
	public void testStrategiesExceptionsForSaveDomainObject() {
		SavingStrategy<Persistable, Dto> createStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT, null);
		createStrategy.saveDomainObject(persistentObject);
	}
	
	/**
	 * Test that saving strategy has always default values for collection strategies and life cycle listener.
	 */
	@Test
	public void testDefaultSettings() {
		SavingStrategy<?, ?> createStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT, null);
		
		createStrategy.setCollectionsStrategy(null);
		createStrategy.setLifecycleListener(null);
		
		assertNotNull(createStrategy.getCollectionsStrategy());
		assertNotNull(createStrategy.getLifecycleListener());
	}
}
