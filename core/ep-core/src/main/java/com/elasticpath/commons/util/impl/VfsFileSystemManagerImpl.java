/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.util.impl;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.auth.StaticUserAuthenticator;
import org.apache.commons.vfs.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs.provider.ftp.FtpFileSystemConfigBuilder;
import org.apache.commons.vfs.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.log4j.Logger;

import com.elasticpath.commons.exception.EpFileManagerException;
import com.elasticpath.commons.util.VfsFileSystemManager;
import com.elasticpath.domain.FileSystemConnectionInfo;

/**
 * This class is a wrapper of the VFS <code>FileSystemManager</code> class to make it easy
 * to deal with files using VFS.
 *
 */
public class VfsFileSystemManagerImpl implements VfsFileSystemManager {

	private FileSystemOptions fsOptions;
	
	private EpFileManagerException error;
	
	private FileSystemConnectionInfo connectionInfo;

	private String rootPath;
	
	private static final String PORT_PREFIX = ":"; //$NON-NLS-1$

	private static final String PROTOCOL_SUFFIX = "://";  //$NON-NLS-1$
	
	private static final Logger LOG = Logger.getLogger(VfsFileSystemManagerImpl.class);
	
	/**
	 * Initialise the VFS connection.
	 * 
	 * @return true if the initialisation was successful.
	 */
	public boolean initialize() {
	    try {
            if (getConnectionInfo() == null) {
            	reportError("No connection information was given", null);
            	return false;
            }
            
		    StaticUserAuthenticator auth = new StaticUserAuthenticator(null, getConnectionInfo().getUserName(), getConnectionInfo().getPassword());
		    fsOptions = new FileSystemOptions();
		    DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(fsOptions, auth);
		    if ("ftp".equals(getConnectionInfo().getProtocol())) { //$NON-NLS-1$
    			configureFtp();
		    } else if ("sftp".equals(getConnectionInfo().getProtocol())) { //$NON-NLS-1$
    			configureSftp();
		    }
			return true;
		} catch (FileSystemException e) {
			reportError("Failure initialising VFS Manager", e);
			return false; 
		}
	}

	/**
	 * Configure ftp options.
	 */
	protected void configureFtp() {
		FtpFileSystemConfigBuilder ftpConfigBuilder = FtpFileSystemConfigBuilder.getInstance();
		ftpConfigBuilder.setPassiveMode(fsOptions, true);
		ftpConfigBuilder.setUserDirIsRoot(fsOptions, true);
	}
	
	/**
	 * Configure sftp options.
	 * 
	 * @throws FileSystemException if an error occurs configuring sftp
	 */
	protected void configureSftp() throws FileSystemException {
		SftpFileSystemConfigBuilder sftpConfigBuilder = SftpFileSystemConfigBuilder.getInstance();
		sftpConfigBuilder.setStrictHostKeyChecking(fsOptions, "no"); //$NON-NLS-1$
	}

	/**
	 * Get the connection info required for access the file system.
	 * 
	 * @return a <code>FileSystemConnectionInfo</code> object
	 */
	public FileSystemConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}

	/**
	 * Set the connection info required to make a connection.
	 * 
	 * @param connectionInfo the connectionInfo to set
	 */
	public void setConnectionInfo(final FileSystemConnectionInfo connectionInfo) {
		this.connectionInfo = connectionInfo;
	}

	/**
	 * Get the last file system error.
	 * 
	 * @return the error
	 */
	public EpFileManagerException getError() {
		return error;
	}

	/**
	 * Locates a file by name.
	 * 
	 * @param fileName the name of the file
	 * @return the file object (never returns null)
	 * @throws FileSystemException on error parsing the file name
	 */
	public FileObject resolveFile(final String fileName) throws FileSystemException {
		return getFileSystemManager().resolveFile(fileName, fsOptions);
	}
	
	/**
	 * Locates a file by name relative to the given base path.
	 * 
	 * @param base a <code>FileObject</code> to use as the base path
	 * @param fileName the name of the file
	 * @return the <code>FileObject</code> for the file
	 * @throws FileSystemException on error parsing the file name
	 */
	public FileObject resolveFile(final FileObject base, final String fileName) throws FileSystemException {
		String fullPath = fullPathOfRelativeFile(base, fileName);
		return resolveFile(fullPath);
	}
	
	/**
	 * Locates a file relative to the root path.
	 * 
	 * @param fileName the name of the file
	 * @return the file object
	 * @throws FileSystemException on error finding the file or parsing the name
	 */
	public FileObject resolveRelativeFile(final String fileName) throws FileSystemException {
		FileObject rootFile = getFileSystemManager().resolveFile(getRootPath(), fsOptions);
		return rootFile.resolveFile(fileName);
	}
	
	/**
	 * Get the root path. Different virtual file systems can have different return for the root path, 
	 * some return the full absolute path. This method will get the root path for the connection and
	 * append the path given in the connection info.
	 * 
	 * @return the root path
	 */
	public String getRootPath() {
		if (rootPath == null) {
			String rootFile = getFilePrefix();
			try {
				FileObject file = getFileSystemManager().resolveFile(rootFile, fsOptions);
				if (file.exists()) {
					rootFile = file.getName().getURI();
				}
			} catch (FileSystemException e) {
				// If the prefix root can't be found it may be ok later with the root path.
				if (LOG.isDebugEnabled()) {
					LOG.debug("Could not resolve root prefix (full path may still be ok): " + rootFile); //$NON-NLS-1$
				}
			}
			String path = getConnectionInfo().getRootPath();
			String fileURI = appendFileNameToPath(path, rootFile);
			if (fileURI.endsWith(FileName.SEPARATOR)) {
				this.rootPath = fileURI.substring(0, fileURI.length() - 1);
			} else {
				this.rootPath = fileURI;
			}
		}
		return rootPath;
	}

	/**
	 * Get the full path of the given file name that is relative to the given base.
	 * This method ensures there are the right number of file separators between the
	 * base path and the filename.
	 *  
	 * @param base a <code>FileObject</code> to use as the base path
	 * @param fileName the name of the file to get the path for
	 * @return the full path of the file
	 */
	public String fullPathOfRelativeFile(final FileObject base, final String fileName) {
		String baseURI = base.getName().getURI();
		return appendFileNameToPath(fileName, baseURI);
	}

	/**
	 * Append the given filename to the given base path, ensuring the correct
	 * use of file separators.
	 * 
	 * @param fileName the filename of the file to append
	 * @param baseURI the base path
	 * @return the full path
	 */
	public String appendFileNameToPath(final String fileName, final String baseURI) {
		String fullPath;
		if (fileName.startsWith(FileName.SEPARATOR) && baseURI.endsWith(FileName.SEPARATOR) && !baseURI.endsWith(PROTOCOL_SUFFIX)) {
			fullPath = baseURI + fileName.substring(FileName.SEPARATOR.length());
		} else if (fileName.startsWith(FileName.SEPARATOR) 
				   || (baseURI.endsWith(FileName.SEPARATOR) && !baseURI.endsWith(PROTOCOL_SUFFIX))) {
			fullPath = baseURI + fileName;
		} else {
			fullPath = baseURI + FileName.SEPARATOR + fileName;
		}
		return fullPath;
	}
	
	/**
	 * Get the appropriate file prefix for the protocol we are using.
	 * 
	 * @return the file prefix string
	 */
	private String getFilePrefix() {
		StringBuffer prefix = new StringBuffer();
		FileSystemConnectionInfo fsInfo = getConnectionInfo();
		prefix.append(fsInfo.getProtocol());
		prefix.append(PROTOCOL_SUFFIX);
		if (fsInfo.getHost() != null) {
			prefix.append(fsInfo.getHost());
			if (fsInfo.getPort() != null) {
				prefix.append(PORT_PREFIX);
				prefix.append(fsInfo.getPort());
			}
		}
		return prefix.toString();
	}

	/**
	 * Report an error.
	 * 
	 * @param message A message describing the error 
	 * @param cause An exception that led to this error
	 */
	protected void reportError(final String message, final Exception cause) {
		if (cause == null) {
			LOG.error(message);
			error = new EpFileManagerException(message);
		} else {
			LOG.error(message, cause);
			error = new EpFileManagerException(message, cause);
		}
	}
	
	/**
	 * Get the file system manager.
	 * 
	 * @return a <code>FileSystemManager</code>.
	 * @throws FileSystemException in an error occurs getting the manager
	 */
	protected FileSystemManager getFileSystemManager() throws FileSystemException {
		return VFS.getManager();
	}
}
