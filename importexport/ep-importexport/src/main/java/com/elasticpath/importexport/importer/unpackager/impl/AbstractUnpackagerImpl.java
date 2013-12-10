package com.elasticpath.importexport.importer.unpackager.impl;

import com.elasticpath.importexport.importer.retrieval.RetrievalMethod;
import com.elasticpath.importexport.importer.unpackager.Unpackager;


/**
 * Abstract unpackager contains common data for all packagers and allows factory to provide initialization. 
 */
public abstract class AbstractUnpackagerImpl implements Unpackager {
	
	/**
	 * Initialize unpackager with retrieval method.
	 *
	 * @param retrievalMethod retrieval method to be used
	 */
	public abstract void initialize(final RetrievalMethod retrievalMethod);	
}
