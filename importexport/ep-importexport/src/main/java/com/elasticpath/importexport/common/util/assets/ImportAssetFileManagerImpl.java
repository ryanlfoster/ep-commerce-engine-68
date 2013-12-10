package com.elasticpath.importexport.common.util.assets;

import java.io.File;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.Selectors;
import org.apache.log4j.Logger;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.common.types.PackageType;
import com.elasticpath.importexport.common.util.Message;

/**
 * High level utility class that provides methods for uploading files with using different settings (protocol, ...).
 */
public class ImportAssetFileManagerImpl extends AbstractAssetFileManagerImpl implements ImportAssetFileManager {

	private static final Logger LOG = Logger.getLogger(ImportAssetFileManagerImpl.class);
	
	private FileObject packageContainer;
	
	@Override
	public void initialize(final Map<String, String> vfsSettings, 
			final PackageType packageType, final String packageName) throws ConfigurationException {		
		super.initializeSettings(vfsSettings);
	
		final File packageFile = new File(packageName);
		final String readProtocol = getPackageProtocol(packageType);
	
		try {
			packageContainer = getVfsFileSystemManager().resolveFile(readProtocol + packageFile.getAbsolutePath());
		} catch (FileSystemException e) {
			LOG.error(e.getMessage());
			throw new ConfigurationException("Could not initialize assets engine", e);
		}
	}
	
	@Override
	public void close() {
		closeQuietly(packageContainer);
	}

	/**
	 * Uploads the given file to the configured destination.
	 * 
	 * @param source file name to retrieve content from
	 * @return number of uploaded files
	 */
	public boolean importFile(final String source) {
		final String destFile = StringUtils.removeStart(source, FileName.SEPARATOR);
		
		FileObject destination = null;
		FileObject fileToUpload = null; 
		
		try {			
			destination = getVfsFileSystemManager().resolveRelativeFile(destFile);
			LOG.info("file destination is " + destination.getName().toString());
			destination.createFile();
			
			fileToUpload = packageContainer.resolveFile(source);
			if (fileToUpload == null) {
				LOG.error(new Message("IE-40404", source));
				return false;
			}
			destination.copyFrom(fileToUpload, Selectors.SELECT_SELF);
			LOG.info("Upload file: " + source + " to " + destination.getName());
			return true;
		} catch (FileSystemException e) {
			throw new ImportRuntimeException("IE-40400", e);
		} finally {
			closeQuietly(fileToUpload);
			closeQuietly(destination);
		}
	}
	
	/**
	 * Helper method that closes fileObject's and catches any FileSystemExceptions
	 * that could be thrown when closing the objects.
	 * @param theFileObject which will be closed
	 */
	void closeQuietly(final FileObject theFileObject) {
		if (theFileObject == null) {
			return;
		}
		try {
			theFileObject.close();
		} catch (FileSystemException fse) {
			LOG.warn("Failed to close file object", fse);
		}
	}
	
	/**
	 * Creates protocol string from packageType.
	 * 
	 * @param packageType the packageType
	 * @return the protocol string like "zip:file://"
	 * @throws ImportRuntimeException if protocol is not supported 
	 */
	String getPackageProtocol(final PackageType packageType) {
		String readProtocol = null;
		if (PackageType.ZIP.equals(packageType)) {
			readProtocol = "zip:file://";
		} else if (PackageType.NONE.equals(packageType)) {
			readProtocol = "file://";
		}
		return readProtocol;
	}
}
