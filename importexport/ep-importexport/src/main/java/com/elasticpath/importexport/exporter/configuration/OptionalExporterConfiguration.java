package com.elasticpath.importexport.exporter.configuration;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.elasticpath.importexport.common.configuration.ConfigurationOption;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.types.OptionalJobType;

/**
 * OptionalExporterConfiguration determines optional exporter type.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class OptionalExporterConfiguration {

	@XmlAttribute(name = "type", required = true)
	private OptionalJobType type;

	@XmlElement(name = "option")
	private List<ConfigurationOption> options;
	
	/**
	 * Gets the export job type.
	 * 
	 * @return the type
	 */
	public JobType getType() {
		return type.getJobType();
	}

	/**
	 * Sets the export job type.
	 * 
	 * @param type the type to set
	 */
	public void setType(final JobType type) {
		for (OptionalJobType optionalJobType : OptionalJobType.values()) {
			if (optionalJobType.getJobType().equals(type)) {
				this.type = optionalJobType;
				return;
			}
		}
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
