package com.elasticpath.importexport.importer.configuration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elasticpath.importexport.common.configuration.PackagerConfiguration;
import com.elasticpath.importexport.common.transformers.TransformerConfiguration;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.types.ImportStrategyType;

/**
 * Import Configuration represents high level query for import execution. Class is designed for JAXB and emulates the set of UI controls.
 */
@XmlRootElement(name = "importconfiguration")
@XmlAccessorType(XmlAccessType.NONE)
public class ImportConfiguration {

	@XmlElement(name = "xmlvalidation")
	private boolean xmlValidation = true;

	@XmlJavaTypeAdapter(ImporterConfigurationAdapter.class)
	@XmlElement(name = "importstrategy")
	private Map<JobType, ImporterConfiguration> importerConfigurationMap;

	@XmlElement(name = "packager", required = true)
	private PackagerConfiguration packagerConfiguration;

	@XmlElement(name = "retrieval", required = true)
	private RetrievalConfiguration retrievalConfiguration;

	@XmlElementWrapper(name = "transformerschain")
	@XmlElement(name = "transformer")
	private List<TransformerConfiguration> transformerConfigurationList;

	/**
	 * Gets RetrievalConfiguration to create and initialize retrieval method.
	 * 
	 * @return RetrievalConfiguration
	 */
	public RetrievalConfiguration getRetrievalConfiguration() {
		return retrievalConfiguration;
	}

	/**
	 * Sets RetrievalConfiguration to create and initialize retrieval method according to.
	 * 
	 * @param retrievalConfiguration the retrievalConfiguration
	 */
	public void setRetrievalConfiguration(final RetrievalConfiguration retrievalConfiguration) {
		this.retrievalConfiguration = retrievalConfiguration;
	}

	/**
	 * Gets the List of Transformer Configurations to create and initialize a chain of transformers.
	 * 
	 * @return list of TransformerConfigurations to configure transformers for import job processing
	 */
	public List<TransformerConfiguration> getTransformerConfigurationList() {
		if (transformerConfigurationList == null) {
			return Collections.emptyList();
		}
		return transformerConfigurationList;
	}

	/**
	 * Sets the List of Transformer Configurations to create and initialize a chain of transformers according to.
	 * 
	 * @param transformerConfigurationList to configure transformers chain for import job processing
	 */
	public void setTransformerConfigurationList(final List<TransformerConfiguration> transformerConfigurationList) {
		this.transformerConfigurationList = transformerConfigurationList;
	}

	/**
	 * Gets packager configuration to create and initialize appropriate unpackager.
	 * 
	 * @return packager configuration
	 */
	public PackagerConfiguration getPackagerConfiguration() {
		return packagerConfiguration;
	}

	/**
	 * Sets packager configuration to create and initialize appropriate unpackager according to.
	 * 
	 * @param packagerConfiguration configuration
	 */
	public void setPackagerConfiguration(final PackagerConfiguration packagerConfiguration) {
		this.packagerConfiguration = packagerConfiguration;
	}

	/**
	 * Gets the xmlValidation condition.
	 * 
	 * @return true if import job have to execute XML validation, false otherwise
	 */
	public boolean isXmlValidation() {
		return xmlValidation;
	}

	/**
	 * Sets the xmlValidation condition.
	 * 
	 * @param xmlValidation true if import job have to execute XML validation, false otherwise
	 */
	public void setXmlValidation(final boolean xmlValidation) {
		this.xmlValidation = xmlValidation;
	}

	/**
	 * Gets the importer configuration map.
	 * 
	 * @return the importerConfigurationMap
	 */
	public Map<JobType, ImporterConfiguration> getImporterConfigurationMap() {
		if (importerConfigurationMap == null) {
			return Collections.emptyMap();
		}
		return importerConfigurationMap;
	}

	/**
	 * Sets the importer configuration map.
	 * 
	 * @param importerConfigurationMap the importerConfigurationMap to set
	 */
	public void setImporterConfigurationMap(final Map<JobType, ImporterConfiguration> importerConfigurationMap) {
		this.importerConfigurationMap = importerConfigurationMap;
	}

	/**
	 * Gets the importer configuration by job type.
	 * 
	 * @param jobType the jobType
	 * @return appropriate importer configuration or default importer configuration in case import configuration does not contain this information
	 */
	public ImporterConfiguration getImporterConfiguration(final JobType jobType) {
		ImporterConfiguration importerConfiguration = getImporterConfigurationMap().get(jobType);
		if (importerConfiguration == null) {
			importerConfiguration = new ImporterConfiguration();
			importerConfiguration.setJobType(jobType);
		}
		return importerConfiguration;
	}
	
	/**
	 * Gets import strategy type by job type.
	 * 
	 * @param jobType the job type
	 * @return appropriate import strategy type or INSERT_OR_UPDATE tyep in case import configuration does not contain this information
	 */
	public ImportStrategyType getImportStrategyType(final JobType jobType) {
		return getImporterConfiguration(jobType).getImportStrategyType();
	}
}
