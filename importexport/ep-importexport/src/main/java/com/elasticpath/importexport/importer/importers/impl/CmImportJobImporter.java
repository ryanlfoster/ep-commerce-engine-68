package com.elasticpath.importexport.importer.importers.impl;

import java.util.Collections;

import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.cmimportjob.CmImportJobDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.dataimport.ImportService;

/**
 * Implements an importer for {@link ImportJob}s.
 */
public class CmImportJobImporter extends AbstractImporterImpl<ImportJob, CmImportJobDTO> {

	private DomainAdapter<ImportJob, CmImportJobDTO> importJobAdapter;

	private ImportService importJobService;

	@Override
	public String getImportedObjectName() {
		return CmImportJobDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final CmImportJobDTO dto) {
		return dto.getGuid();
	}

	@Override
	protected DomainAdapter<ImportJob, CmImportJobDTO> getDomainAdapter() {
		return this.importJobAdapter;
	}

	@Override
	protected ImportJob findPersistentObject(final CmImportJobDTO dto) {
		ImportJob importJob;
		try {
			importJob = importJobService.findImportJob(dto.getName());
		} catch (EpServiceException e) {
			importJob = null;
		}
		return importJob;
	}

	@Override
	protected void setImportStatus(final CmImportJobDTO dto) {
		getStatusHolder().setImportStatus("(" + dto.getName() + ")");
	}

	public void setCmImportJobAdapter(final DomainAdapter<ImportJob, CmImportJobDTO> importJobAdapter) {
		this.importJobAdapter = importJobAdapter;
	}

	public void setCmImportJobService(final ImportService importJobService) {
		this.importJobService = importJobService;
	}

	@Override
	protected CollectionsStrategy<ImportJob, CmImportJobDTO> getCollectionsStrategy() {
		return new ImportJobCollectionsStrategy(getContext().getImportConfiguration().getImporterConfiguration(JobType.CM_IMPORT_JOB));
	}

	@Override
	public Class<? extends CmImportJobDTO> getDtoClass() {
		return CmImportJobDTO.class;
	}

	/**
	 * Implements a {@linkplain CollectionsStrategy} for {@link ImportJob}s, allowing import mappings to be cleared.
	 */
	public class ImportJobCollectionsStrategy implements CollectionsStrategy<ImportJob, CmImportJobDTO> {

		private final boolean clearProperties;

		/**
		 * Default constructor.
		 * 
		 * @param importerConfiguration The current importer configuration.
		 */
		public ImportJobCollectionsStrategy(final ImporterConfiguration importerConfiguration) {
			clearProperties = importerConfiguration.getCollectionStrategyType(DependentElementType.CM_IMPORT_MAPPINGS).equals(
					CollectionStrategyType.CLEAR_COLLECTION);
		}

		@Override
		public void prepareCollections(final ImportJob importJob, final CmImportJobDTO dto) {
			if (clearProperties) {
				importJob.setMappings(Collections.<String, Integer>emptyMap());
			}
		}

		@Override
		public boolean isForPersistentObjectsOnly() {
			return true;
		}
	}
}
