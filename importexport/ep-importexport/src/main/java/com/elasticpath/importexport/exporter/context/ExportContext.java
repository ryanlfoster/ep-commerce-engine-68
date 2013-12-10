package com.elasticpath.importexport.exporter.context;

import java.util.ArrayList;

import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;

/**
 * Container for data shared among Export classes: ExportConfiguration, etc.
 */
public class ExportContext {

	private final ExportConfiguration exportConfiguration;

	private final SearchConfiguration searchConfiguration;

	private DependencyRegistry dependencyRegistry = new DependencyRegistry(new ArrayList<Class<?>>());

	private Summary summary;

	/**
	 * Constructor initializes export configuration.
	 * 
	 * @param exportConfiguration the export configuration constructed earlier
	 * @param searchConfiguration the search configuration that was constructed earlier
	 */
	public ExportContext(final ExportConfiguration exportConfiguration, final SearchConfiguration searchConfiguration) {
		this.exportConfiguration = exportConfiguration;
		this.searchConfiguration = searchConfiguration;
	}

	/**
	 * Gets actual export configuration.
	 * 
	 * @return export configuration
	 */
	public ExportConfiguration getExportConfiguration() {
		return exportConfiguration;
	}

	/**
	 * Gets the search configuration.
	 * 
	 * @return the searchConfiguration
	 */
	public SearchConfiguration getSearchConfiguration() {
		return searchConfiguration;
	}

	/**
	 * Gets the dependency registry.
	 * 
	 * @return the dependencyRegistry
	 */
	public DependencyRegistry getDependencyRegistry() {
		return dependencyRegistry;
	}

	/**
	 * Sets the dependency registry.
	 * 
	 * @param dependencyRegistry the dependencyRegistry to set
	 */
	public void setDependencyRegistry(final DependencyRegistry dependencyRegistry) {
		this.dependencyRegistry = dependencyRegistry;
	}

	/**
	 * Sets summary object to collect any kind of summary information except messages which are handled by IESummaryAppender.
	 * 
	 * @param summary Summary
	 */
	public void setSummary(final Summary summary) {
		this.summary = summary;
	}

	/**
	 * Gets summary associated for this export process.
	 * 
	 * @return Summary
	 */
	public Summary getSummary() {
		return summary;
	}
}
