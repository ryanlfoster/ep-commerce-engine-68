package com.elasticpath.importexport.common.summary;

/**
 * SummaryLayout Interface.
 */
public interface SummaryLayout {

	/**
	 * Makes formated output of summary.
	 *
	 * @param summary the summary for formated output
	 * @return formated output
	 */
	String format(final Summary summary);	
}
