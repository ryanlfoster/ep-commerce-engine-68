package com.elasticpath.importexport.common.util.assets;

import java.util.Map;

import org.apache.commons.vfs.FileObject;

import com.elasticpath.importexport.common.exception.ConfigurationException;

/**
 * Interface that provides methods for working with assets (Export part).
 */
public interface ExportAssetFileManager {
	
	/**
	 * Initializes Virtual File System manager and loads all connection options.
	 * 
	 * @param vfsSettings the connection info containing VFS connection options
	 * @throws ConfigurationException if FVS manager couldn't be created
	 */
	void initialize(final Map<String, String> vfsSettings) throws ConfigurationException;
	
	/**
	 * Loads file by its name relative to the server root path.
	 * 
	 * @param fileName relative file name
	 * @return input stream with file content
	 */
	FileObject exportFile(final String fileName);
}
