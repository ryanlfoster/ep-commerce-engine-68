package com.elasticpath.importexport.exporter.exporters;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exportentry.ExportEntry;

/**
 * Every export job executes using special exporter.
 * Exporter is responsible on analyzing export query, retrieving EP domain objects by query, making
 * adapters based on these objects and marshalling of prepared adapters using XML marshaller
 */
public interface Exporter {

	/**
	 * Retrieve export query from the context and execute export job.
	 *  
	 * @return <code>InputStream</code> 
	 */
	ExportEntry executeExport();
	
	/**
	 * Informs whether exporter has one more entry to be exported or not.
	 * 
	 * @return false if export for the type served by this exporter is finished
	 */
	boolean isFinished();

	/**
	 * Gets the job type of exporter.
	 * 
	 * @return the appropriate jobType
	 */
	JobType getJobType();

	/**
	 * Get (classes of domain objects) (previous exported objects) (depend on) and (this exporter) is responsible for export of them.
	 * 
	 * @return array of domain objects' classes
	 */
	Class< ? >[] getDependentClasses();

	/**
	 * Initialize exporter.
	 * This method must be in interface because not all exporters extends AbstractExporterImpl
	 * 
	 * @param context containing export settings
	 * @throws ConfigurationException if exporter couldn't be initialized
	 */
	void initialize(ExportContext context) throws ConfigurationException;
}
