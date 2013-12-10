package com.elasticpath.importexport.exporter.configuration;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.importexport.common.configuration.ConfigurationOption;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.types.RequiredJobType;

/**
 * ExporterConfiguration determines exporter type.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ExporterConfiguration {

	@XmlAttribute(name = "type", required = true)
	private RequiredJobType type;
	
	@XmlElement(name = "option")
	private List<ConfigurationOption> options;
	
	@XmlElement(name = "include")
	private List<OptionalExporterConfiguration> optionalExporterConfigurationList;
	
	/**
	 * Get export job type.
	 *
	 * @return JobType export job type
	 */
	public JobType getType() {
		return type.getJobType();
	}
	
	/**
	 * Set export job type.
	 *
	 * @param jobType export job type
	 */
	public void setType(final RequiredJobType jobType) {
		this.type = jobType;
	}

	/**
	 * Gets Optional Exporter Configuration by its job type.
	 * 
	 * @param jobType type of optional exporter
	 * @return Optional Exporter Configuration or null if it is not contained in configuration
	 */
	public OptionalExporterConfiguration getOptionalExporterConfiguration(final JobType jobType) {
		for (OptionalExporterConfiguration configuration : getOptionalExporterConfigurationList()) {
			if (configuration.getType().equals(jobType)) {
				return configuration;
			}
		}
		
		return null;
	}

	/**
	 * Gets optional exporter configuration list.
	 * 
	 * @return the list with configurations of optional exporters.
	 */
	public List<OptionalExporterConfiguration> getOptionalExporterConfigurationList() {
		if (optionalExporterConfigurationList == null) {
			return Collections.emptyList();
		}
		return this.optionalExporterConfigurationList;
	}
	
	/**
	 * Sets the optionalExporterConfigurationList.
	 * 
	 * @param optionalExporterConfigurationList the optionalExporterConfigurationList to set
	 */
	public void setOptionalExporterConfigurationList(final List<OptionalExporterConfiguration> optionalExporterConfigurationList) {
		this.optionalExporterConfigurationList = optionalExporterConfigurationList;
	}
	
	/**
	 * Gets option corresponding to the given key.
	 * 
	 * @param key option key
	 * @return string with option value or null if option could not be found
	 */
	public String getOption(final String key) {
		for (ConfigurationOption option : getOptions()) {
			if (option.getKey().equals(key)) {
				return option.getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * Sets optional exporter options.
	 * 
	 * @param options the list with options to use
	 */
	public void setOptions(final List<ConfigurationOption> options) {
		this.options = options;
	}
	
	/**
	 * Gets list of options.
	 * 
	 * @return list of options.
	 */
	protected List<ConfigurationOption> getOptions() {
		if (options == null) {
			return Collections.emptyList();
		}
		
		return options;
	}
	
}
