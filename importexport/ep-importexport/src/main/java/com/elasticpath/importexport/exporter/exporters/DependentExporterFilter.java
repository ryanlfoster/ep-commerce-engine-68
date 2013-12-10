package com.elasticpath.importexport.exporter.exporters;


/**
 * Provides a means for dependent data to be filtered. The precise meaning of filtering is left up to the
 * implementation.
 */
public interface DependentExporterFilter {
	/**
	 * Decides whether the object with the given primary uid should be filtered.
	 * 
	 * @param primaryObjectUid parent object which requires a dependency
	 * @return whether the object should be filtered
	 */
	boolean isFiltered(long primaryObjectUid);
}
