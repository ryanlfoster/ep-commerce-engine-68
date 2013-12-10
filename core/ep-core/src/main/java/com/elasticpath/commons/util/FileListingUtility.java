/**
 * 
 */
package com.elasticpath.commons.util;

import java.util.List;

/**
 * Provides utility methods for listing file and folder names.
 */
public interface FileListingUtility {

	/**
	 * Finds the folder relative to the root path and returns its list of child folder names. An empty list is return if folder name is not found or
	 * if no child folders exist.
	 * 
	 * @param folderName the name of the folder
	 * @return the list of child folder names
	 * @throws FileListingUnavailableException if the listing cannot be obtained
	 */
	List<String> findFolderNames(final String folderName);

	/**
	 * Finds the folder relative to the root path matching the given regex string and returns its list of child file names. An empty list is return
	 * if folder name is not found or if no child files exist.
	 * 
	 * @param folderName the name of the folder
	 * @param regex the regex string
	 * @param includeExtension include extension in file name 
	 * @return the list of child folder names
	 * @throws FileListingUnavailableException if the listing cannot be obtained
	 */
	List<String> findFileNames(final String folderName, final String regex, final boolean includeExtension);

}
