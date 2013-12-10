package com.elasticpath.importexport.exporter.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.importexport.common.configuration.PackagerConfiguration;
import com.elasticpath.importexport.common.transformers.TransformerConfiguration;

/**
 * ExportConfiguration represents high level query for export execution. Class is designed for JAXB and emulates the set of UI controls.
 */
@XmlRootElement(name = "exportconfiguration")
@XmlAccessorType(XmlAccessType.NONE)
public class ExportConfiguration {

	@XmlElement(name = "exporter", required = false)
	private ExporterConfiguration exporterConfiguration;

	@XmlElementWrapper(name = "exporters")
	@XmlElement(name = "exporter", required = true)
	private final List<ExporterConfiguration> exporterConfigurations = new ArrayList<ExporterConfiguration>();

	@XmlElement(name = "packager", required = true)
	private PackagerConfiguration packagerConfiguration;

	@XmlElement(name = "delivery", required = true)
	private DeliveryConfiguration deliveryConfiguration;

	@XmlElementWrapper(name = "transformerschain")
	@XmlElement(name = "transformer", required = true)
	private List<TransformerConfiguration> transformerConfigurationList;

	private int currentExporterConfiguration = 0;

	/**
	 * Gets DeliveryConfiguration to create and initialize delivery method.
	 * 
	 * @return Delivery Configuration
	 */
	public DeliveryConfiguration getDeliveryConfiguration() {
		return deliveryConfiguration;
	}

	/**
	 * Sets DeliveryConfiguration to create delivery method according to.
	 * 
	 * @param deliveryConfiguration to configure delivery method
	 */
	public void setDeliveryConfiguration(final DeliveryConfiguration deliveryConfiguration) {
		this.deliveryConfiguration = deliveryConfiguration;
	}

	/**
	 * Gets the List of Transformer Configurations to create and initialize a chain of transformers. JAXB guarantees that order of transformers in a
	 * chain isn't changed after unmarshalling it from XML.
	 * 
	 * @return List of TransformerConfiguration instances to build a chain of transformers
	 */
	public List<TransformerConfiguration> getTransformerConfigurationList() {
		if (transformerConfigurationList == null) {
			return Collections.emptyList();
		}
		return transformerConfigurationList;
	}

	/**
	 * Sets a List of Transformer Configurations to create a chain of transformers according to.
	 * 
	 * @param transformerConfigurationList a List of transformer configurations
	 */
	public void setTransformerConfigurationList(final List<TransformerConfiguration> transformerConfigurationList) {
		this.transformerConfigurationList = transformerConfigurationList;
	}

	/**
	 * Gets packager configuration to create and initialize Packager for export job.
	 * 
	 * @return packager configuration
	 */
	public PackagerConfiguration getPackagerConfiguration() {
		return packagerConfiguration;
	}

	/**
	 * Sets packager configuration to create Packager according to.
	 * 
	 * @param packagerConfiguration packager configuration
	 */
	public void setPackagerConfiguration(final PackagerConfiguration packagerConfiguration) {
		this.packagerConfiguration = packagerConfiguration;
	}

	/**
	 * Gets the "current" Exporter Configuration to create and initialize exporters. The "current" exporter configuration can be updated by passing a
	 * "previous" to findNextExporterConfiguration.
	 * 
	 * @return Exporter Configuration
	 */
	public ExporterConfiguration getExporterConfiguration() {

		return getExporterConfigurations().get(currentExporterConfiguration);
	}

	/**
	 * Will find the "next" <exporter/> in a series of <exporters/>. This method is safe to use even if there is only one <exporter/> and no
	 * <exporters/>. Callers must pass in "null" to get the first exporter. Note: After successfully calling this method, calls to
	 * getExporterConfiguration() will return the same ExporterConfiguration.
	 * 
	 * @param previous the last exporter returned by this method, or null to get the first (or only) exporter.
	 * @return null if there is no next exporter, otherwise a ExporterConfiguration.
	 */
	public ExporterConfiguration findNextExporterConfiguration(final ExporterConfiguration previous) {

		if (previous == null) {
			currentExporterConfiguration = 0;
			return getExporterConfiguration();
		}

		for (int i = 0; i < (getExporterConfigurations().size() - 1); i++) {
			if (exporterConfigurations.get(i) == previous) {
				currentExporterConfiguration = i + 1;
				return getExporterConfiguration();
			}
		}

		return null;

	}

	/**
	 * Gets a list of Exporter Configurations as defined in <exporters/>. NOTE: If no <exporters/> element is defined, then this method will build a
	 * list containing the defined <exporter/> in it. However, if <exporters/> are defined, then the stand-alone <exporter/> is ignored.
	 * 
	 * @return a list of zero or more <exporter/>'s in it.
	 */
	public List<ExporterConfiguration> getExporterConfigurations() {
		if (exporterConfigurations.isEmpty() && exporterConfiguration != null) {
			exporterConfigurations.add(exporterConfiguration);
		}
		return exporterConfigurations;
	}

	/**
	 * Set Exporter Configuration to configure exporters according to.
	 * 
	 * @param exporterConfiguration the with exporter configurations
	 */
	public void setExporterConfiguration(final ExporterConfiguration exporterConfiguration) {
		this.exporterConfiguration = exporterConfiguration;
	}
}
