package com.elasticpath.tools.sync.job.impl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.job.SynchronizationDataDao;

/**
 * Default implementation of SynchronizationDataDao.
 * 
 * @param <E> object to read/write.
 */
public class SynchronizationDataDaoImpl<E> implements SynchronizationDataDao<E> {

	private static final Logger LOG = Logger.getLogger(SynchronizationDataDaoImpl.class);

	@SuppressWarnings("unchecked")
	@Override
	public E readFromFile(final String fileName) throws SyncToolRuntimeException {
		try {
			ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName));
			try {
				return (E) inputStream.readObject();
			} finally {
				try {
					inputStream.close();
				} catch (IOException e) {
					LOG.warn("Could not close file : " + fileName);
				}
			}
		} catch (Exception e) {
			throw new SyncToolRuntimeException("Unable to read data", e);
		}
	}

	@Override
	public void saveToFile(final E object, final String fileName) throws SyncToolRuntimeException {
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
			try {
				outputStream.writeObject(object);				
			} finally {
				try {
					outputStream.close();
				} catch (IOException e) {
					LOG.warn("Could not close file : " + fileName);
				}
			}
		} catch (Exception e) {
			throw new SyncToolRuntimeException("Unable to write data", e);
		}
	}

}