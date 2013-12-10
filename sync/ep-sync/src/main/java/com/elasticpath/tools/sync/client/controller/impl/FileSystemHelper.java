package com.elasticpath.tools.sync.client.controller.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.client.SyncToolConfiguration;
import com.elasticpath.tools.sync.configuration.marshal.XMLMarshaller;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.impl.JobDescriptorImpl;
import com.elasticpath.tools.sync.processing.SerializableObject;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;

/**
 * The Helper class which abstracts a way of saving and reading {@link com.elasticpath.tools.sync.job.TransactionJob}s and {@link JobDescriptor}s.
 * <p>It hides operations with directories. It generates subDirectory depends on parameter if it needs.</p> 
 */
public class FileSystemHelper {
	
	private static final Logger LOG = Logger.getLogger(FileSystemHelper.class);
	
	private static final FastDateFormat TIMESTAMP_FORMAT = FastDateFormat.getInstance("yyyyMMddHHmmss");

	private int objectsLimitPerFile;
	
	private SyncToolConfiguration syncToolConfiguration;

	private boolean addTimestampToFolder;

	private String workingFolder;
	
	/**
	 * A marker object for an end-of-file. 
	 * This is driven by the fact that {@link ObjectInputStream} does not have any function to check for EOF.
	 */
	private static final String EOF = "EOF";
	
	/**
	 * The path where the output files will be written.
	 */
	private String getRootPath() {
		String rootPath = syncToolConfiguration.getRootPath();
		return FilenameUtils.concat(rootPath, syncToolConfiguration.getSubDir());
	}
	
	private String getWorkingFolderPath() {
		if (workingFolder == null) {
			String rootPath = syncToolConfiguration.getRootPath();
			if (rootPath == null) {
				throw new IllegalArgumentException("Root Path should be valid");
			}
			String adapterParameter = syncToolConfiguration.getAdapterParameter();
			if (addTimestampToFolder) {
				this.workingFolder = FilenameUtils.concat(rootPath, generateSubPath(adapterParameter));
			} else {
				this.workingFolder = FilenameUtils.concat(rootPath, adapterParameter);
			}
			LOG.info("Using working folder path : " + this.workingFolder);
		}
		return this.workingFolder;
	}
	
	private static String generateSubPath(final String subParam) {
		LOG.info("Generating sub directory name");
		return subParam + "_" + TIMESTAMP_FORMAT.format(new Date());
	}

	/**
	 * Reads TransactionJob from file.
	 * 
	 * @param objectListener the object listener
	 * @param fileName the file name
	 * @throws SyncToolRuntimeException if file cannot be read
	 */
	public void readTransactionJobFromFile(final String fileName, final SerializableObjectListener objectListener) {
		try {
			File file = new File(getFilePath(getRootPath(), fileName, false));
			if (!file.exists()) {
				throw new FileNotFoundException("Cannot find file: " + file.getAbsolutePath());
			}
			for (int index = 1; file.exists(); index++) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Reading from file: " + file.getName());
				}
				ObjectInputStream inputStream = new ObjectInputStream(
						new BufferedInputStream(
								new FileInputStream(file)));
				try {
					for (Object obj = inputStream.readObject();
							!ObjectUtils.equals(EOF, obj);
								obj = inputStream.readObject()) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("Object read: " + obj);
						}
						
						objectListener.processObject((SerializableObject) obj);
					}
				} finally {
					try {
						inputStream.close();
					} catch (IOException e) {
						LOG.warn("Could not close file : " + fileName);
					}
				}
				file = new File(getFilePath(getRootPath(), fileName, index, false));
			}
		} catch (IOException e) {
			throw new SyncToolRuntimeException("Unable to read Job Unit", e);
		} catch (ClassNotFoundException e) {
			throw new SyncToolRuntimeException("Unable to process Job Unit", e);
		}
	}
	
	/**
	 * Saves TransactionJob to file.
	 * 
	 * @param objectProvider the object provider
	 * @param fileName the file name to save to
	 * @throws SyncToolRuntimeException if file cannot be written.
	 */
	public void saveTransactionJobToFile(final Iterable< ? extends SerializableObject> objectProvider, final String fileName) {
		try {			
			Iterator< ? extends SerializableObject> iterator = objectProvider.iterator();
			File file = new File(getFilePath(getWorkingFolderPath(), fileName, false));
			for (int index = 1; iterator.hasNext(); index++) {
				ObjectOutputStream outputStream = new ObjectOutputStream(
						new BufferedOutputStream(
								new FileOutputStream(file)));
				try {
					 for (int objectsRead = 0;
					 		iterator.hasNext() && objectsRead <= objectsLimitPerFile; 
					 			objectsRead++) {
						SerializableObject object = iterator.next();
						outputStream.writeObject(object);
						
						if (LOG.isDebugEnabled()) {
							LOG.debug("Object saved: " + object);
						}
					 }
					 outputStream.writeObject(EOF);
				} finally {
					try {
						outputStream.close();
					} catch (IOException e) {
						LOG.warn("Could not close file : " + fileName);
					}
				}
				file = new File(getFilePath(getWorkingFolderPath(), fileName, index, false));
			}
		} catch (Exception e) {
			throw new SyncToolRuntimeException("Unable to write Job Unit", e);
		}
	}
	
	/**
	 * Saves JobDescriptor.
	 * 
	 * @param jobDescriptor the {@link JobDescriptor} instance
	 * @param fileName the file name.
	 * @throws SyncToolRuntimeException if file cannot be written.
	 */
	public void saveJobDescriptor(final JobDescriptor jobDescriptor, final String fileName) {
		try {
			OutputStream outputStream = new BufferedOutputStream(
					new FileOutputStream(getFilePath(getWorkingFolderPath(), fileName, true)));
			try {
				new XMLMarshaller(JobDescriptorImpl.class).marshal(jobDescriptor, outputStream);
			} finally {
				try {
					outputStream.close();
				} catch (IOException e) {
					LOG.warn("Could not close file : " + fileName);
				}
			}
		} catch (FileNotFoundException e) {
			throw new SyncToolRuntimeException("Unable to marshal job descriptor", e);
		}
	}
	
	/**
	 * Gets file path for fileName and creates directories if it needs.
	 * 
	 * @param fileName should be just a name of the file without any path elements.
	 * @param index 
	 * @param createDirs if it is true root directories will be created (for write)
	 * @return concatenated file path
	 */
	private String getFilePath(final String rootPath, final String fileName, final int index, final boolean createDirs) {
		StringBuilder indexedFileName = 
			new StringBuilder(FilenameUtils.getBaseName(fileName)) 
			.append(index)
			.append('.')
			.append(FilenameUtils.getExtension(fileName));
		return getFilePath(rootPath, indexedFileName.toString(), createDirs);
	}

	/**
	 * Gets file path for fileName and creates directories if it needs.
	 * 
	 * @param fileName should be just a name of the file without any path elements.
	 * @param index 
	 * @param createDirs if it is true root directories will be created (for write)
	 * @return concatenated file path
	 */
	private String getFilePath(final String rootPath, final String fileName, final boolean createDirs) {
		if (createDirs && !(new File(rootPath).mkdirs())) {
			LOG.warn("Could not create directories : " + fileName);
		}
		return FilenameUtils.concat(rootPath, fileName);
	}

	/**
	 *
	 * @return the syncToolConfiguration
	 */
	public SyncToolConfiguration getSyncToolConfiguration() {
		return syncToolConfiguration;
	}

	/**
	 *
	 * @param syncToolConfiguration the syncToolConfiguration to set
	 */
	public void setSyncToolConfiguration(final SyncToolConfiguration syncToolConfiguration) {
		this.syncToolConfiguration = syncToolConfiguration;
	}

	/**
	 *
	 * @return the objectsLimitPerFile
	 */
	public int getObjectsLimitPerFile() {
		return objectsLimitPerFile;
	}

	/**
	 *
	 * @param objectsLimitPerFile the objectsLimitPerFile to set
	 */
	public void setObjectsLimitPerFile(final int objectsLimitPerFile) {
		this.objectsLimitPerFile = objectsLimitPerFile;
	}

	/**
	 *
	 * @return the addTimestampToFolder
	 */
	public boolean isAddTimestampToFolder() {
		return addTimestampToFolder;
	}

	/**
	 *
	 * @param addTimestampToFolder the addTimestampToFolder to set
	 */
	public void setAddTimestampToFolder(final boolean addTimestampToFolder) {
		this.addTimestampToFolder = addTimestampToFolder;
	}
	
	
	
}
