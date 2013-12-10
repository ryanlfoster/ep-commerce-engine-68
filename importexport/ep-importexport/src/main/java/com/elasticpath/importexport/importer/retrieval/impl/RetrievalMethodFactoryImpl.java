package com.elasticpath.importexport.importer.retrieval.impl;

import java.util.Map;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.TransportType;
import com.elasticpath.importexport.importer.configuration.RetrievalConfiguration;
import com.elasticpath.importexport.importer.retrieval.RetrievalMethod;
import com.elasticpath.importexport.importer.retrieval.RetrievalMethodFactory;

/**
 * RetrievalMethodFactory creates and initializes ready to use retrieval methods.
 */
public class RetrievalMethodFactoryImpl implements RetrievalMethodFactory {

	private Map<TransportType, AbstractRetrievalMethodImpl> retrievalMethods;

	/**
	 * Constructs retrieval method from configuration.
	 * 
	 * @param retrievalConfiguration the retrieval configuration
	 * @return instance of RetrievalMethod
	 * @throws ConfigurationException factory exception
	 */
	public RetrievalMethod createRetrievalMethod(final RetrievalConfiguration retrievalConfiguration) throws ConfigurationException {
		final TransportType methodType = retrievalConfiguration.getMethod();
		final AbstractRetrievalMethodImpl retrievalMethod = retrievalMethods.get(methodType);

		if (retrievalMethod == null) {
			throw new ConfigurationException("Retrival method of type " + methodType.toString() + " doesn't exist");
		}

		retrievalMethod.initialize(retrievalConfiguration.getSource());
		return retrievalMethod;
	}

	/**
	 * Gets available retrieval methods.
	 * 
	 * @return the retrievalMethods
	 */
	public Map<TransportType, AbstractRetrievalMethodImpl> getRetrievalMethods() {
		return retrievalMethods;
	}

	/**
	 * Sets available retrieval methods.
	 * 
	 * @param retrievalMethods the retrievalMethods to set
	 */
	public void setRetrievalMethods(final Map<TransportType, AbstractRetrievalMethodImpl> retrievalMethods) {
		this.retrievalMethods = retrievalMethods;
	}
}
