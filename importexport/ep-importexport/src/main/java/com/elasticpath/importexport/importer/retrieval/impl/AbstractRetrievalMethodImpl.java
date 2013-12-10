package com.elasticpath.importexport.importer.retrieval.impl;

import com.elasticpath.importexport.importer.retrieval.RetrievalMethod;

/**
 * Prepare information general for all retrieval methods.  
 */
public abstract class AbstractRetrievalMethodImpl implements RetrievalMethod {

	private String source;

	/**
	 * Initializes retrieval method with source to retrieve data from.
	 * 
	 * @param source full source path string representation
	 */
	public void initialize(final String source) {
		this.source = source;
	}

	/**
	 * Gets full path to source this retrieval method retrieves data from.
	 * 
	 * @return source full path string representation
	 */
	protected String getSource() {
		return source;
	}
}
