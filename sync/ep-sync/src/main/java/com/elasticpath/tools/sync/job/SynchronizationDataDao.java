package com.elasticpath.tools.sync.job;

import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;


/**
 * Saves/reads from the file a list of transaction job units or a job descriptor. 
 * 
 * @param <E> object to read/write.
 */
public interface SynchronizationDataDao<E> {

	/**
	 * Reads E from the file.
	 * 
	 * @param fileName file name
	 * @return read object
	 * @throws SyncToolRuntimeException in case of errors
	 */
	E readFromFile(final String fileName) throws SyncToolRuntimeException;

	/**
	 * Saves E to the file.
	 * 
	 * @param object object to save
	 * @param fileName file name 
	 * @throws SyncToolRuntimeException in case of errors
	 */
	void saveToFile(final E object, final String fileName) throws SyncToolRuntimeException;

}
