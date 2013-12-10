package com.elasticpath.importexport.importer.configuration;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Import Strategy represents properties of importer configurations.
 * Class is designed for JAXB and emulates the set of UI controls.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ImportStrategyConfiguration {
	
	@XmlElement(name = "importer")
	private List<ImporterConfiguration> importerConfigurationList;
	
	/**
	 * Gets the importer configuration list.
	 * 
	 * @return the importerConfigurationList
	 */
	public List<ImporterConfiguration> getImporterConfigurationList() {
		if (importerConfigurationList == null) {
			return Collections.emptyList();
		}
		return importerConfigurationList;
	}

	/**
	 * Sets the importer configuration list.
	 * 
	 * @param importerConfigurationList the importerConfigurationList to set
	 */
	public void setImporterConfigurationList(final List<ImporterConfiguration> importerConfigurationList) {
		this.importerConfigurationList = importerConfigurationList;
	}
}
