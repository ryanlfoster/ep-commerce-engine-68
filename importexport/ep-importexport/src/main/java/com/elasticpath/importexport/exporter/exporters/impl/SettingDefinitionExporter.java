package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.settings.DefinedValueDTO;
import com.elasticpath.importexport.common.dto.settings.SettingDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.custom.setting.SettingResult;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Exporter for <code>SettingDefinition</code>.
 */
public class SettingDefinitionExporter extends AbstractExporterImpl<SettingDefinition, SettingDTO, Long> {

	private static final Logger LOG = Logger.getLogger(SettingDefinitionExporter.class);
	
	private DomainAdapter<SettingDefinition, SettingDTO> settingDefinitionAdapter;

	private DomainAdapter<SettingValue, DefinedValueDTO> settingValueAdapter;	
	
	private SettingsService settingsService;

	private Map<Long, SettingDefinitionDescriptor> settingDefinitionMap;
	
	private ImportExportSearcher importExportSearcher;
	
	/**
	 * Holder for SettingDefinition and the list of its value contexts.
	 */
	private static final class SettingDefinitionDescriptor {
		
		private final SettingDefinition settingDefinition;
		
		private final List<String> contexts;

		public SettingDefinitionDescriptor(final SettingDefinition settingDefinition, final List<String> contexts) {
			this.settingDefinition = settingDefinition;
			this.contexts = contexts;
		}

		/**
		 * Gets the value of settingDefinition.
		 *
		 * @return the value of settingDefinition.
		 */
		public SettingDefinition getSettingDefinition() {
			return settingDefinition;
		}

		/**
		 * Gets the value of contexts.
		 *
		 * @return the value of contexts.
		 */
		public List<String> getContexts() {
			return contexts;
		}
	}

	/**
	 * Prepares the list of compound GUIDs of objects to be exported.
	 * 
	 * @param context Export Context
	 * @throws ConfigurationException if find any configuration problems
	 */
	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		List<Object> searchCompoundGuids = importExportSearcher.searchCompoundGuids(getContext().getSearchConfiguration(), EPQueryType.CONFIGURATION);

		settingDefinitionMap = new HashMap<Long, SettingDefinitionDescriptor>();

		Map<String, SettingDefinition> pathLookup = new HashMap<String, SettingDefinition>();
		for (Object object : searchCompoundGuids) {
			SettingResult settingResult = (SettingResult) object;

			SettingDefinition settingDefinition = pathLookup.get(settingResult.getPath());
			if (settingDefinition == null) {
				settingDefinition = settingsService.getSettingDefinition(settingResult.getPath());
				pathLookup.put(settingResult.getPath(), settingDefinition);
				if (settingDefinition == null) {
					continue;
				}
			}

			SettingDefinitionDescriptor settingDefinitionDescriptor = settingDefinitionMap.get(settingDefinition.getUidPk());
			if (settingDefinitionDescriptor == null) {
				List<String> contexts = new ArrayList<String>();
				contexts.add(settingResult.getContext());
				settingDefinitionMap.put(settingDefinition.getUidPk(), new SettingDefinitionDescriptor(settingDefinition, contexts));
			} else {
				settingDefinitionDescriptor.getContexts().add(settingResult.getContext());
			}			
		}

		LOG.info("The list for " + settingDefinitionMap.size() + " setting definitions is retrieved from database.");

		addDependentExporter(new SettingValueExporter());
	}

	@Override
	protected List<SettingDefinition> findByIDs(final List<Long> subList) {
		final List<SettingDefinition> settingDefinitions = new ArrayList<SettingDefinition>();

		for (Long settingUid : subList) {
			settingDefinitions.add(settingDefinitionMap.get(settingUid).getSettingDefinition());
		}

		return settingDefinitions;
	}

	@Override
	protected DomainAdapter<SettingDefinition, SettingDTO> getDomainAdapter() {
		return settingDefinitionAdapter;
	}

	@Override
	protected Class<? extends SettingDTO> getDtoClass() {
		return SettingDTO.class;
	}

	@Override
	public JobType getJobType() {
		return JobType.SYSTEMCONFIGURATION;
	}
	
	@Override
	protected List<Long> getListExportableIDs() {
		return new ArrayList<Long>(settingDefinitionMap.keySet());
	}

	@Override
	public Class< ? >[] getDependentClasses() {
		return new Class< ? >[] {}; // Because dependencies are handled internally 
	}

	/**
	 * Dependent Exporter for Setting Values.
	 */
	private final class SettingValueExporter extends AbstractDependentExporterImpl<SettingValue, DefinedValueDTO, SettingDTO> {

		public void bindWithPrimaryObject(final List<DefinedValueDTO> dependentDtoObjects, final SettingDTO primaryDtoObject) {
			primaryDtoObject.setDefinedValues(dependentDtoObjects);
		}

		public List<SettingValue> findDependentObjects(final long primaryObjectUid) {			
			final SettingDefinitionDescriptor descriptor = settingDefinitionMap.get(primaryObjectUid);

			if (descriptor == null || descriptor.getContexts().isEmpty()) {
				LOG.info("No values were found for setting deffinition with uid : " + primaryObjectUid);
				return Collections.emptyList();
			}

			List<SettingValue> settingValues = new ArrayList<SettingValue>();

			for (String context : descriptor.getContexts()) {
				final SettingValue settingValue = settingsService.getSettingValue(descriptor.getSettingDefinition().getPath(), context);

				if (settingValue.isPersisted()) {
					settingValues.add(settingValue);
				}
			}

			return settingValues;
		}

		public DomainAdapter<SettingValue, DefinedValueDTO> getDomainAdapter() {
			return settingValueAdapter;
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
	public final void setSettingValueAdapter(final DomainAdapter<SettingValue, DefinedValueDTO> settingValueAdapter) {
		this.settingValueAdapter = settingValueAdapter;
	}

	/**
	 * @param importExportSearcher the importExportSearcher to set
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}
}
