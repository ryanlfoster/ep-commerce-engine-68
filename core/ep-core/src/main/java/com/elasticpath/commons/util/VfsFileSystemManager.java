package com.elasticpath.commons.util;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

import com.elasticpath.commons.exception.EpFileManagerException;
import com.elasticpath.domain.FileSystemConnectionInfo;

/**
 * Manages a VFS file system.
 *
 */
public interface VfsFileSystemManager {

	/**
	 * Initialise the VFS connection.
	 * 
	 * @return true if the initialisation was successful.
	 */
	boolean initialize();

	/**
	 * Get the connection info required for access the file system.
	 * 
	 * @return a <code>FileSystemConnectionInfo</code> object
	 */
	FileSystemConnectionInfo getConnectionInfo();

	/**
	 * Set the connection info required to make a connection.
	 * 
	 * @param connectionInfo the connectionInfo to set
	 */
	void setConnectionInfo(final FileSystemConnectionInfo connectionInfo);

	/**
	 * Get the last file system error.
	 * 
	 * @return the error
	 */
	EpFileManagerException getError();

	/**
	 * Locates a file by name.
	 * 
	 * @param fileName the name of the file
	 * @return the file object (never returns null)
	 * @throws FileSystemException on error parsing the file name
	 */
	FileObject resolveFile(final String fileName) throws FileSystemException;

	/**
	 * Locates a file by name relative to the given base path.
	 * 
	 * @param base a <code>FileObject</code> to use as the base path
	 * @param fileName the name of the file
	 * @return the <code>FileObject</code> for the file
	 * @throws FileSystemException on error parsing the file name
	 */
	FileObject resolveFile(final FileObject base, final String fileName)
			throws FileSystemException;

	/**
	 * Locates a file relative to the root path.
	 * 
	 * @param fileName the name of the file
	 * @return the file object
	 * @throws FileSystemException on error finding the file or parsing the name
	 */
	FileObject resolveRelativeFile(final String fileName)
			throws FileSystemException;

	/**
	 * Get the root path. Different virtual file systems can have different return for the root path, 
	 * some return the full absolute path. This method will get the root path for the connection and
	 * append the path given in the connection info.
	 * 
	 * @return the root path
	 */
	String getRootPath();

	/**
	 * Get the full path of the given file name that is relative to the given base.
	 * This method ensures there are the right number of file separators between the
	 * base path and the filename.
	 *  
	 * @param base a <code>FileObject</code> to use as the base path
	 * @param fileName the name of the file to get the path for
	 * @return the full path of the file
	 */
	String fullPathOfRelativeFile(final FileObject base, final String fileName);

	/**
	 * Append the given filename to the given base path, ensuring the correct
	 * use of file separators.
	 * 
	 * @param fileName the filename of the file to append
	 * @param baseURI the base path
	 * @return the full path
	 */
	String appendFileNameToPath(final String fileName, final String baseURI);

}