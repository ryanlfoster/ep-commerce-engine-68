package com.elasticpath.importexport.importer.importers.impl;

import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.Importer;
import com.elasticpath.importexport.importer.importers.ImporterFactory;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.persistence.api.Persistable;

/**
 * ImporterFactory creates and initializes ready to use importers.<br>
 * Map with available importers is injected by Spring.
 */
class ImporterFactoryImpl implements ImporterFactory {

	private static final Logger LOG = Logger.getLogger(ImporterFactoryImpl.class);

	private Map<JobType, Importer<? super Persistable, ? super Dto>> importerMap;

	@Override
	public Importer<? super Persistable, ? super Dto> createImporter(final JobType jobType, final ImportContext context,
			final SavingManager<? extends Persistable> savingManager)
			throws ConfigurationException {
		Importer<? super Persistable, ? super Dto> importer = importerMap.get(jobType);
		if (importer == null) {
			throw new ConfigurationException("can not find appropriate importer for job type " + jobType);
		}
		LOG.debug("Creating importer for Tag: " + jobType.getTagName());
		initializeImporterHelper(importer, context, context.getImportConfiguration().getImportStrategyType(jobType), savingManager);
		return importer;
	}

	@SuppressWarnings("unchecked")
	private <T extends Persistable, K extends Dto> void initializeImporterHelper(final Importer<T, K> importer,
			final ImportContext context, final ImportStrategyType strategyType, final SavingManager<?> savingManager)
			throws ConfigurationException {
		final SavingManager<T> generifiedSavingManager = (SavingManager<T>) savingManager;
		SavingStrategy<T, K> savingStrategy = createSavingStrategy(strategyType, generifiedSavingManager);
		importer.initialize(context, savingStrategy);
	}

	/**
	 * Creates saving strategy based on given arguments.
	 * 
	 * @param strategyType the import strategy type
	 * @param savingManager the saving manager
	 * @return new saving strategy
	 * @param <T> type of {@link Persistable} to create the {@link SavingStrategy} for
	 * @param <K> type of {@link Dto} to create the {@link SavingStrategy} for
	 */
	protected <T extends Persistable, K extends Dto> SavingStrategy<T, K> createSavingStrategy(final ImportStrategyType strategyType,
			final SavingManager<T> savingManager) {
		return AbstractSavingStrategy.createStrategy(strategyType, savingManager);
	}

	/**
	 * Gets the importerMap.
	 * 
	 * @return the importerMap
	 */
	public Map<JobType, Importer<? super Persistable, ? super Dto>> getImporterMap() {
		return importerMap;
	}

	/**
	 * Sets the importerMap.
	 * 
	 * @param importerMap the importerMap to set
	 */
	public void setImporterMap(final Map<JobType, Importer<? super Persistable, ? super Dto>> importerMap) {
		this.importerMap = importerMap;
	}
}
