package com.elasticpath.importexport.importer.importers.impl;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.Importer;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.persistence.api.Persistable;

/**
 * Test for importer factory implementation.
 */
public class ImporterFactoryImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Check create importer for non-supported job type.
	 */
	@Test(expected = ConfigurationException.class)
	public void testCreateImporterForNonSupportedJobType() throws Exception {
		final ImporterFactoryImpl factory = new ImporterFactoryImpl();
		factory.setImporterMap(new HashMap<JobType, Importer<? super Persistable, ? super Dto>>());
		factory.createImporter(JobType.ASSETS, null, null);
	}

	/**
	 * Check create importer for supported job type.
	 */
	@SuppressWarnings({"JUnitTestMethodWithNoAssertions", "unchecked"})
	@Test
	public void testCreateImporterForSupportedJobType() throws Exception {
		final SavingStrategy<? extends Persistable, ? extends Dto> savingStrategy = context.mock(SavingStrategy.class);

		final ImportContext importContext = new ImportContext(new ImportConfiguration());

		final ImporterFactoryImpl factory = new ImporterFactoryImplWithMockedStrategy(savingStrategy);

		final Map<JobType, Importer<? super Persistable, ? super Dto>> importerMap =
				new HashMap<JobType, Importer<? super Persistable, ? super Dto>>();

		final Importer<? super Persistable, ? super Dto> importer = context.mock(Importer.class);

		context.checking(new Expectations() {
			{
				oneOf(importer).initialize(with(importContext), with(any(SavingStrategy.class)));
			}
		});

		importerMap.put(JobType.PRODUCT, importer);
		factory.setImporterMap(importerMap);
		factory.createImporter(JobType.PRODUCT, importContext, null);
	}

	/** This type was added to fix a checkstyle EOF exception. */
	private static class ImporterFactoryImplWithMockedStrategy extends ImporterFactoryImpl {
		private final SavingStrategy<? extends Persistable, ? extends Dto> savingStrategy;

		public ImporterFactoryImplWithMockedStrategy(final SavingStrategy<? extends Persistable, ? extends Dto> savingStrategy) {
			this.savingStrategy = savingStrategy;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected <T extends Persistable, K extends Dto> SavingStrategy<T, K> createSavingStrategy(final ImportStrategyType strategyType,
				final SavingManager<T> savingManager) {
			return (SavingStrategy<T, K>) savingStrategy;
		}

	}
}
