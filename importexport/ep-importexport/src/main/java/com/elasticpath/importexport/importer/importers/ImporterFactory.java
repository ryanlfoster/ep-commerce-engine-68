package com.elasticpath.importexport.importer.importers;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.impl.SavingManager;
import com.elasticpath.persistence.api.Persistable;

/**
 * Interface for importer factory.
 */
public interface ImporterFactory {

	/**
	 * Creates appropriate importer based on tagName.
	 * 
	 * @param jobType the job type
	 * @param context the import context for importer initialization
	 * @param savingManager the saving manager for importer
	 * @throws ConfigurationException if could not create Importer
	 * @return the importer
	 */
	Importer<? super Persistable, ? super Dto> createImporter(final JobType jobType, final ImportContext context,
			final SavingManager<? extends Persistable> savingManager) throws ConfigurationException;

}
