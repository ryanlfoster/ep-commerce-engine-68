package com.elasticpath.importexport.common.util.assets;

import java.util.Map;

import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.PackageType;

/**
 * Interface that provides methods for working with assets (Import part).
 */
public interface ImportAssetFileManager {

	/**
	 * Initializes Virtual File System manager and loads all connection options.
	 * 
	 * @param vfsSettings the connection info containing VFS connection options
	 * @param packageType the package Type
	 * @param packageName the package Name
	 * @throws ConfigurationException if FVS manager couldn't be created
	 */
	void initialize(final Map<String, String> vfsSettings, final PackageType packageType, final String packageName) throws ConfigurationException;
	
	/**
	 * Uploads the given file to the configured destination.
	 * 
	 * @param source file name to retrieve content from
	 * @return true if file was properly imported
	 */
	boolean importFile(final String source);
	
	
	/**
	 * Closes PackageContainer which was created on initialize.
	 */
	void close();
}
