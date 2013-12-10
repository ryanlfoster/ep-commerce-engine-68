package com.elasticpath.importexport.importer.configuration;

import java.util.Collections;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.types.RequiredJobType;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.importexport.importer.types.ImportStrategyType;

/**
 * Importer Configuration represents properties for collision strategies. Class is designed for JAXB and emulates the set of UI controls.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class ImporterConfiguration {

	@XmlAttribute(name = "type", required = true)
	private RequiredJobType requiredJobType;

	@XmlElement(name = "importstrategy", required = false, defaultValue = "INSERT_OR_UPDATE")
	private ImportStrategyType importStrategyType = ImportStrategyType.INSERT_OR_UPDATE;

	@XmlJavaTypeAdapter(DependentElementAdapter.class)
	@XmlElement(name = "dependentelements")
	private Map<DependentElementType, DependentElementConfiguration> dependentElementMap;

	/**
	 * Gets the job type.
	 * 
	 * @return the jobType
	 */
	public JobType getJobType() {
		return requiredJobType.getJobType();
	}

	/**
	 * Sets the job type.
	 * 
	 * @param jobType the jobType to set
	 */
	public void setJobType(final JobType jobType) {
		for (RequiredJobType requiredJobType : RequiredJobType.values()) {
			if (requiredJobType.getJobType().equals(jobType)) {
				this.requiredJobType = requiredJobType;
				break;
			}
		}
	}

	/**
	 * Gets the import strategy type.
	 * 
	 * @return the importStrategyType
	 */
	public ImportStrategyType getImportStrategyType() {
		return importStrategyType;
	}

	/**
	 * Sets the import strategy type.
	 * 
	 * @param importStrategyType the importStrategyType to set
	 */
	public void setImportStrategyType(final ImportStrategyType importStrategyType) {
		this.importStrategyType = importStrategyType;
	}

	/**
	 * Gets the dependent element map.
	 * 
	 * @return the dependentElementMap
	 */
	public Map<DependentElementType, DependentElementConfiguration> getDependentElementMap() {
		if (dependentElementMap == null) {
			return Collections.emptyMap();
		}
		return dependentElementMap;
	}

	/**
	 * Sets the dependent element map.
	 * 
	 * @param dependentElementMap the dependentElementMap to set
	 */
	public void setDependentElementMap(final Map<DependentElementType, DependentElementConfiguration> dependentElementMap) {
		this.dependentElementMap = dependentElementMap;
	}

	/**
	 * Gets the dependent element configuration by dependent element type.
	 * 
	 * @param dependentElementType the dependent element type
	 * @return appropriate dependent element configuration or default configuration in case import configuration does not contain this information
	 */
	public DependentElementConfiguration getDependentElementConfiguration(final DependentElementType dependentElementType) {
		DependentElementConfiguration dependentElementConfiguration = getDependentElementMap().get(dependentElementType);
		if (dependentElementConfiguration == null) {
			dependentElementConfiguration = new DependentElementConfiguration();
			dependentElementConfiguration.setDependentElementType(dependentElementType);
		}

		return dependentElementConfiguration;
	}

	/**
	 * Gets collection strategy type by dependent element type. 
	 * 
	 * @param dependentElementType the dependent element type
	 * @return appropriate collection strategy type or CLEAR_COLLECTION type in case import configuration does not contain this information
	 */
	public CollectionStrategyType getCollectionStrategyType(final DependentElementType dependentElementType) {
		return getDependentElementConfiguration(dependentElementType).getCollectionStrategyType();
	}
}
