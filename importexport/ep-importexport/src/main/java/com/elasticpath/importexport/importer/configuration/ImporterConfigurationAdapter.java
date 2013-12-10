package com.elasticpath.importexport.importer.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.elasticpath.importexport.common.types.JobType;

/**
 * The xml adapter for data transformation between <code>ImportStrategyConfiguration</code> and
 * <code>Map&lt;JobType, ImporterConfiguration></code>.
 */
public class ImporterConfigurationAdapter extends XmlAdapter<ImportStrategyConfiguration, Map<JobType, ImporterConfiguration>> {

	@Override
	public ImportStrategyConfiguration marshal(final Map<JobType, ImporterConfiguration> configurationMap) throws Exception {
		ImportStrategyConfiguration importStrategyConfiguration = new ImportStrategyConfiguration();
		importStrategyConfiguration.setImporterConfigurationList(new ArrayList<ImporterConfiguration>(configurationMap.values()));
		return importStrategyConfiguration;
	}

	@Override
	public Map<JobType, ImporterConfiguration> unmarshal(final ImportStrategyConfiguration importStrategyConfiguration) throws Exception {
		Map<JobType, ImporterConfiguration> configurationMap = new HashMap<JobType, ImporterConfiguration>();
		for (ImporterConfiguration configuration : importStrategyConfiguration.getImporterConfigurationList()) {
			configurationMap.put(configuration.getJobType(), configuration);
		}
		return configurationMap;
	}

}
