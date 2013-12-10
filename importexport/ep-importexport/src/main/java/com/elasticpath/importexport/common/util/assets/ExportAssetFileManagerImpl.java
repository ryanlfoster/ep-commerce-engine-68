package com.elasticpath.importexport.common.util.assets;

import java.util.Map;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.exception.runtime.ExportRuntimeException;

/**
 * High level utility class that provides methods for loading files with using different settings (protocol, ...).
 */
public class ExportAssetFileManagerImpl extends AbstractAssetFileManagerImpl implements ExportAssetFileManager {

	@Override
	public void initialize(final Map<String, String> vfsSettings) throws ConfigurationException {
		super.initializeSettings(vfsSettings);
	}
	
	/**
	 * Loads file by its name relative to the server root path.
	 * 
	 * @param fileName relative file name
	 * @return input stream with file content
	 */
	public FileObject exportFile(final String fileName) {
		FileObject file = null;
		try {
			file = getVfsFileSystemManager().resolveRelativeFile(fileName);
			if (!isFileAvailableForReading(file)) {
				throw new ExportRuntimeException("IE-40401", fileName);
			}
			return file;
		} catch (FileSystemException e) {
			throw new ExportRuntimeException("IE-40402", e, fileName);
		}
	}

	/**
	 * Checks if given file object is available for reading.
	 * 
	 * @param file the file object
	 * @return true if file object is available for reading and false otherwise
	 * @throws FileSystemException in case of incorrect file object
	 */
	boolean isFileAvailableForReading(final FileObject file) throws FileSystemException {
		return file.exists() && file.isReadable();
	}

}
