package com.elasticpath.importexport.exporter.search;

import java.util.List;

import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.ql.parser.EPQueryType;

/**
 * The ImportExport Searcher interface. Has 3 helper methods which facilitates IDs retrieval. Provides validation of query type 
 * against expected type to predict incorrect results while specification incorrect query in a search config.
 */
public interface ImportExportSearcher {

	/**
	 * Searches UIDs using SearchConfiguration.
	 *  
	 * @param config the SearchConfiguration
	 * @param epQueryType the type of import/export job
	 * @return UIDs of found domain objects 
	 */
	List<Long> searchUids(final SearchConfiguration config, final EPQueryType epQueryType);
	
	/**
	 * Searches GUIDs using SearchConfiguration.
	 *  
	 * @param config the SearchConfiguration
	 * @param epQueryType the type of import/export job
	 * @return GUIDs of found domain objects 
	 */
	List<String> searchGuids(final SearchConfiguration config, final EPQueryType epQueryType);
	
	/**
	 * Searches compound GUIDs using SearchConfiguration.
	 *  
	 * @param config the SearchConfiguration
	 * @param epQueryType the type of import/export job
	 * @return compound GUIDs which may be any wrapper object 
	 */
	List<Object> searchCompoundGuids(final SearchConfiguration config, final EPQueryType epQueryType);
}
