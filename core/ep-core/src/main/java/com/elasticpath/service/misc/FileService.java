package com.elasticpath.service.misc;

import java.util.List;

import com.elasticpath.service.EpService;

/**
 * Provides file download and upload service.
 */
public interface FileService extends EpService {

	/**
	 * Read the sub folders.
	 *
	 * @param rootFolder the root folder for different type of asset, e.g., images for image asset
	 * @param subPath the subPath under root folder
	 * @return the sub folder path
	 *
	 */
	List<String> getSubFolders(final String rootFolder, final String subPath);

	/**
	 * Get the file list under asset sub folder.
	 *
	 * @param rootFolder the root folder for different type of asset, e.g., images for image asset
	 * @param subPath the subPath under root folder
	 * @return the list of file name with the sub path under root folder
	 *
	 */
	List<String> getFilesByFolder(final String rootFolder, final String subPath);

	/**
	 * Check if the file exists.
	 *
	 * @param rootFolder the root folder for different type of asset, e.g., images for image asset
	 * @param filePath the filePath under root folder
	 * @return true if file exist, otherwise false
	 *
	 */
	boolean isFileExist(final String rootFolder, final String filePath);

	/**
	 * Delete the folder and all files under it.
	 *
	 * @param rootFolder the root folder for different type of asset, e.g., images for image asset
	 * @param subPath the subPath under root folder
	 * @return true if the entire folder deleted, otherwise false
	 *
	 */
	boolean deleteEntireFolder(final String rootFolder, final String subPath);

	/**
	 * Delete the file.
	 *
	 * @param rootFolder the root folder for different type of asset, e.g., images for image asset
	 * @param filePath the filePath under root folder
	 * @return true if the entire folder deleted, otherwise false
	 *
	 */
	boolean deleteFile(final String rootFolder, final String filePath);

	/**
	 * Rename the file.
	 *
	 * @param rootFolder the root folder for different type of asset, e.g., images for image asset
	 * @param oriFilePath the filePath under root folder
	 * @param newFilePath the filePath under root folder
	 * @return true if rename successful, otherwise false
	 *
	 */
	boolean renameFile(final String rootFolder, final String oriFilePath, final String newFilePath);


	/**
	 * Create the folder.
	 *
	 * @param rootFolder the root folder for different type of asset, e.g., images for image asset
	 * @param subPath the subPath under root folder
	 * @return true if the entire folder deleted, otherwise false
	 *
	 */
	boolean createFolder(final String rootFolder, final String subPath);



}
