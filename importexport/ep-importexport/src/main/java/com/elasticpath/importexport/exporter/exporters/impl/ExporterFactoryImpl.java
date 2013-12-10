package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.ExporterConfiguration;
import com.elasticpath.importexport.exporter.configuration.OptionalExporterConfiguration;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.Exporter;
import com.elasticpath.importexport.exporter.exporters.ExporterFactory;

/**
 * Factory makes exporters and initializes them using export context.
 */
public class ExporterFactoryImpl implements ExporterFactory {

	private Map<JobType, List<Exporter>> exportJobMap;

	private Map<JobType, List<JobType>> availableOptionalJobMap;

	private static final Logger LOG = Logger.getLogger(ExporterFactoryImpl.class);

	private ExporterConfiguration exporterConfiguration = null;
	
	/**
	 * Create the list of exporters for export processing.  Repeated calls to this method will return each exporter configuration in sequence.
	 * 
	 * @param context the context with information for configuring of exporters
	 * @throws ConfigurationException if there is something wrong
	 * @return the list of exporters or null if there are none available.
	 */
	public List<Exporter> createExporterSequence(final ExportContext context) throws ConfigurationException {
		exporterConfiguration = context.getExportConfiguration().findNextExporterConfiguration(exporterConfiguration);
		
		if (exporterConfiguration == null) {
			return null;
		}
		
		JobType jobType = exporterConfiguration.getType();

		final List<Exporter> exporterSequence = new ArrayList<Exporter>();

		initializeExporterListByJobType(context, exporterSequence, jobType);

		List<OptionalExporterConfiguration> optionalExporterConfigurationList = exporterConfiguration.getOptionalExporterConfigurationList();
		addOptionalExporterSequence(context, exporterSequence, optionalExporterConfigurationList, jobType);

		return exporterSequence;
	}

	private void addOptionalExporterSequence(final ExportContext context, final List<Exporter> exporterSequence,
			final List<OptionalExporterConfiguration> optionalExporterConfigurationList, final JobType requiredJobType)
			throws ConfigurationException {
		List<JobType> availableOptionalJobTypes = availableOptionalJobMap.get(requiredJobType);
		
		if (availableOptionalJobTypes == null) {
			LOG.warn("Unable to find required optional exporter for " + requiredJobType);
		} else {
			for (OptionalExporterConfiguration optionalExporterConfiguration : optionalExporterConfigurationList) {
				final JobType jobType = optionalExporterConfiguration.getType();
				
				if (availableOptionalJobTypes.contains(jobType)) {
					initializeExporterListByJobType(context, exporterSequence, jobType);
				}
			}
		}
	}

	private void initializeExporterListByJobType(final ExportContext context, final List<Exporter> exporterSequence, final JobType jobType)
			throws ConfigurationException {
		final List<Exporter> exporterList = exportJobMap.get(jobType);

		if (exporterList == null || exporterList.isEmpty()) {
			throw new ConfigurationException("Exporter for export job type " + jobType.toString() + " doesn't exist");
		}

		for (Exporter exporter : exporterList) {
			exporter.initialize(context);
			exporterSequence.add(exporter);
		}
	}

	/**
	 * Gets the exportJobMap.
	 * 
	 * @return the map that contain export jobs
	 */
	public Map<JobType, List<Exporter>> getExportJobMap() {
		return exportJobMap;
	}

	/**
	 * Sets the exportJobMap.
	 * 
	 * @param exportJobMap the map that contain export jobs
	 */
	public void setExportJobMap(final Map<JobType, List<Exporter>> exportJobMap) {
		this.exportJobMap = exportJobMap;
	}

	/**
	 * Gets the availableOptionalJobMap.
	 * 
	 * @return the availableOptionalJobMap
	 */
	public Map<JobType, List<JobType>> getAvailableOptionalJobMap() {
		return availableOptionalJobMap;
	}

	/**
	 * Sets the availableOptionalJobMap.
	 * 
	 * @param availableOptionalJobMap the availableOptionalJobMap to set
	 */
	public void setAvailableOptionalJobMap(final Map<JobType, List<JobType>> availableOptionalJobMap) {
		this.availableOptionalJobMap = availableOptionalJobMap;
	}

	@Override
	public Set<Exporter> getAllConfiguredExporters(final ExportContext context) {
		Set<Exporter> allExporters = new HashSet<Exporter>();

		for (ExporterConfiguration exporterConfiguration : context.getExportConfiguration().getExporterConfigurations()) {
			allExporters.addAll(exportJobMap.get(exporterConfiguration.getType()));
			for (OptionalExporterConfiguration optionalExporter : exporterConfiguration.getOptionalExporterConfigurationList()) {
				allExporters.addAll(exportJobMap.get(optionalExporter.getType()));
			}
		}
		return allExporters;
	}

}
