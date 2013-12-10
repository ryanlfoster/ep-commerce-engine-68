package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.settings.DefinedValueDTO;
import com.elasticpath.importexport.common.dto.settings.SettingDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;
import com.elasticpath.importexport.importer.importers.CollectionsStrategy;
import com.elasticpath.importexport.importer.importers.SavingStrategy;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;

/**
 * Provides info for import of Setting Definitions.
 */
public class SettingDefinitionImporterImpl extends AbstractImporterImpl<SettingDefinition, SettingDTO> {

	private DomainAdapter<SettingDefinition, SettingDTO> settingDefinitionAdapter;

	private DomainAdapter<SettingValue, DefinedValueDTO> settingValueAdapter;	

	private SettingsService settingsService;

	private SavingStrategy<SettingValue, DefinedValueDTO> commonSavingStrategy;

	private SavingManager<SettingValue> settingValueSavingManager;

	@Override
	public void initialize(final ImportContext context, final SavingStrategy<SettingDefinition, SettingDTO> savingStrategy) {
		super.initialize(context, savingStrategy);
		commonSavingStrategy = AbstractSavingStrategy.createStrategy(ImportStrategyType.INSERT_OR_UPDATE, getSettingValueSavingManager());

		getSavingStrategy().setSavingManager(new SavingManager<SettingDefinition>() {

			@Override
			public SettingDefinition update(final SettingDefinition persistable) {
				return settingsService.updateSettingDefinition(persistable);
			}

			@Override
			public void save(final SettingDefinition persistable) {
				update(persistable);
			}

		});
	}

	@Override
	protected SettingDefinition findPersistentObject(final SettingDTO dto) {
		return settingsService.getSettingDefinition(dto.getNameSpace());
	}

	@Override
	protected DomainAdapter<SettingDefinition, SettingDTO> getDomainAdapter() {
		return settingDefinitionAdapter;
	}

	@Override
	protected String getDtoGuid(final SettingDTO dto) {
		return dto.getNameSpace();
	}

	@Override
	protected void setImportStatus(final SettingDTO object) {
		getStatusHolder().setImportStatus("(" + object.getNameSpace() + ")");
	}

	@Override
	public String getImportedObjectName() {
		return SettingDTO.ROOT_ELEMENT;
	}

	/**
	 * Returns the collections strategy for setting definitions.
	 * 
	 * @return appropriate collections strategy
	 */
	@Override
	protected CollectionsStrategy<SettingDefinition, SettingDTO> getCollectionsStrategy() {
		return new SettingDefinitionCollectionsStrategy(getContext().getImportConfiguration().getImporterConfiguration(JobType.SYSTEMCONFIGURATION));
	}

	@Override
	public boolean executeImport(final SettingDTO object) {
		sanityCheck();
		
		setImportStatus(object);
		
		final SettingDefinition obtainedSettingDefinition = findPersistentObject(object);
		checkDuplicateGuids(object, obtainedSettingDefinition);
		final SettingDefinition settingDefinition = getSavingStrategy().populateAndSaveObject(obtainedSettingDefinition, object);

		// if settingDefinition == null it means that this Setting Definition was not imported because of import strategies reasons
		if (settingDefinition != null) {
			// Imports the dependent data
			final LifecycleListener lifecycleListener = new DefaultLifecycleListener() {

				@Override
				public void beforeSave(final Persistable persistable) {
					// Method setSettingDefinition doesn't exist in <code>SettingValue</code> interface.
					((SettingValueImpl) persistable).setSettingDefinition(settingDefinition);
				}
			};

			commonSavingStrategy.setLifecycleListener(lifecycleListener);
			saveSettingValues(object);
			return true;
		}

		return false;
	}

	private void saveSettingValues(final SettingDTO object) {
		commonSavingStrategy.setDomainAdapter(settingValueAdapter);

		for (DefinedValueDTO valueDto : object.getDefinedValues()) {
			SettingValue settingValue = settingsService.getSettingValue(object.getNameSpace(), valueDto.getContext());

			commonSavingStrategy.populateAndSaveObject(settingValue, valueDto);
		}
	}

	/**
	 * Implementation of <code>CollectionsStrategy</code> interface for setting definition object.
	 */
	private final class SettingDefinitionCollectionsStrategy implements CollectionsStrategy<SettingDefinition, SettingDTO> {

		private final boolean isSettingValueClearCollection;

		private final boolean isSettingMetadataClearCollection;

		/**
		 * Initializes collection strategies based on configuration.
		 * 
		 * @param importerConfiguration configuration for this importer
		 */
		public SettingDefinitionCollectionsStrategy(final ImporterConfiguration importerConfiguration) {
			final CollectionStrategyType settingValueStrategyType = importerConfiguration.getCollectionStrategyType(
					DependentElementType.SETTING_VALUES);
			final CollectionStrategyType settingMetadataStrategyType = importerConfiguration.getCollectionStrategyType(
					DependentElementType.SETTING_METADATA);

			isSettingValueClearCollection = CollectionStrategyType.CLEAR_COLLECTION.equals(settingValueStrategyType);
			isSettingMetadataClearCollection = CollectionStrategyType.CLEAR_COLLECTION.equals(settingMetadataStrategyType);
		}

		@Override
		public void prepareCollections(final SettingDefinition domainObject, final SettingDTO dto) {
			if (isSettingValueClearCollection) {
				for (SettingValue settingValue : settingsService.getSettingValues(domainObject.getPath())) {
					settingsService.deleteSettingValue(settingValue);
				}
			}

			if (isSettingMetadataClearCollection) {
				domainObject.setMetadata(null);
			}
		}

		@Override
		public boolean isForPersistentObjectsOnly() {
			return true;
		}
	}

	/**
	 * Gets the value of settingDefinitionAdapter.
	 *
	 * @return the value of settingDefinitionAdapter.
	 */
	public final DomainAdapter<SettingDefinition, SettingDTO> getSettingDefinitionAdapter() {
		return settingDefinitionAdapter;
	}

	/**
	 * Sets the value of settingDefinitionAdapter.    
	 *
	 * @param settingDefinitionAdapter the settingDefinitionAdapter to set
	 */
	public final void setSettingDefinitionAdapter(final DomainAdapter<SettingDefinition, SettingDTO> settingDefinitionAdapter) {
		this.settingDefinitionAdapter = settingDefinitionAdapter;
	}

	/**
	 * Gets the value of settingValueAdapter.
	 *
	 * @return the value of settingValueAdapter.
	 */
	public final DomainAdapter<SettingValue, DefinedValueDTO> getSettingValueAdapter() {
		return settingValueAdapter;
	}

	/**
	 * Sets the value of settingValueAdapter.    
	 *
	 * @param settingValueAdapter the settingValueAdapter to set
	 */
	public final void setSettingValueAdapter(
			final DomainAdapter<SettingValue, DefinedValueDTO> settingValueAdapter) {
		this.settingValueAdapter = settingValueAdapter;
	}

	/**
	 * Gets the value of settingsService.
	 *
	 * @return the value of settingsService.
	 */
	public final SettingsService getSettingsService() {
		return settingsService;
	}

	/**
	 * Sets the value of settingsService.    
	 *
	 * @param settingsService the settingsService to set
	 */
	public final void setSettingsService(final SettingsService settingsService) {
		this.settingsService = settingsService;
	}

	@Override
	public Class<? extends SettingDTO> getDtoClass() {
		return SettingDTO.class;
	}

	protected SavingManager<SettingValue> getSettingValueSavingManager() {
		return settingValueSavingManager;
	}

	public void setSettingValueSavingManager(final SavingManager<SettingValue> settingValueSavingManager) {
		this.settingValueSavingManager = settingValueSavingManager;
	}

}
