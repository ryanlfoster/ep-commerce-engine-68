package com.elasticpath.importexport.common.util.assets;

import static com.elasticpath.importexport.common.util.assets.AssetFileManager.PROPERTY_ASSET_VFS_HOST;
import static com.elasticpath.importexport.common.util.assets.AssetFileManager.PROPERTY_ASSET_VFS_PASSWORD;
import static com.elasticpath.importexport.common.util.assets.AssetFileManager.PROPERTY_ASSET_VFS_PORT;
import static com.elasticpath.importexport.common.util.assets.AssetFileManager.PROPERTY_ASSET_VFS_PROTOCOL;
import static com.elasticpath.importexport.common.util.assets.AssetFileManager.PROPERTY_ASSET_VFS_ROOTPATH;
import static com.elasticpath.importexport.common.util.assets.AssetFileManager.PROPERTY_ASSET_VFS_USERNAME;

import java.util.Map;

import org.apache.log4j.Logger;

import com.elasticpath.commons.util.VfsFileSystemManager;
import com.elasticpath.domain.FileSystemConnectionInfo;
import com.elasticpath.domain.impl.FileSystemConnectionInfoImpl;
import com.elasticpath.importexport.common.exception.ConfigurationException;

/**
 * Abstract class for high level utility class that provides methods for loading and uploading files with using different settings (protocol, ...).
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class AbstractAssetFileManagerImpl {
	
	private static final Logger LOG = Logger.getLogger(AbstractAssetFileManagerImpl.class);

	private VfsFileSystemManager vfsFileSystemManager;

	/**
	 * Initializes Virtual File System manager and loads all connection options.
	 * 
	 * @param vfsSettings the connection info containing VFS connection options
	 * @throws ConfigurationException if FVS manager couldn't be created
	 */
	protected void initializeSettings(final Map<String, String> vfsSettings) throws ConfigurationException {
		final FileSystemConnectionInfo connectionInfo = buildConnectionInfo(vfsSettings);

		if (!isConnectionInfoCorrect(connectionInfo)) {
			throw new ConfigurationException("Asset settings are not initialized");
		}

		vfsFileSystemManager.setConnectionInfo(connectionInfo);
		if (!vfsFileSystemManager.initialize()) {
			throw new ConfigurationException(vfsFileSystemManager.getError().getMessage());
		}
	}
	
	/**
	 * Builds the connection info object from vsfSettings.
	 * 
	 * @param vfsSettings the connection info containing VFS connection options
	 * @return instance of <code>FileSystemConnectionInfo</code> object
	 */
	FileSystemConnectionInfo buildConnectionInfo(final Map<String, String> vfsSettings) {
		final FileSystemConnectionInfo connectionInfo = new FileSystemConnectionInfoImpl();
		connectionInfo.setUserName(vfsSettings.get(PROPERTY_ASSET_VFS_USERNAME));
		connectionInfo.setPassword(vfsSettings.get(PROPERTY_ASSET_VFS_PASSWORD));
		connectionInfo.setProtocol(vfsSettings.get(PROPERTY_ASSET_VFS_PROTOCOL));
		connectionInfo.setHost(vfsSettings.get(PROPERTY_ASSET_VFS_HOST));
		connectionInfo.setRootPath(vfsSettings.get(PROPERTY_ASSET_VFS_ROOTPATH));

		String port = vfsSettings.get(PROPERTY_ASSET_VFS_PORT);
		try {
			connectionInfo.setPort(Integer.parseInt(port));
		} catch (NumberFormatException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Could not parse vfs port: " + port); //$NON-NLS-1$
			}
		}
		return connectionInfo;
	}

	/**
	 * Checks if connection info correct.
	 * 
	 * @param connectionInfo the FileSystemConnectionInfo instance
	 * @return true if connectionInfo is not NULL and protocol is not NULL and host is not NULL, false otherwise
	 */
	boolean isConnectionInfoCorrect(final FileSystemConnectionInfo connectionInfo) {
		return connectionInfo != null && connectionInfo.getProtocol() != null && connectionInfo.getHost() != null;
	}
	
	/**
	 * Gets the file VFS File system manager.
	 * 
	 * @return the vfsFileSystemManager
	 */
	public VfsFileSystemManager getVfsFileSystemManager() {
		return vfsFileSystemManager;
	}

	/**
	 * Sets VFS file system manager.
	 * 
	 * @param vfsFileSystemManager the vfsFileSystemManager to set
	 */
	public void setVfsFileSystemManager(final VfsFileSystemManager vfsFileSystemManager) {
		this.vfsFileSystemManager = vfsFileSystemManager;
	}
}
