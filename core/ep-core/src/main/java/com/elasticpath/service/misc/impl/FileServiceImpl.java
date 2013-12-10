package com.elasticpath.service.misc.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.misc.FileService;

/**
 * Provide file download and upload service.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.AvoidDuplicateLiterals" })
public class FileServiceImpl extends AbstractEpServiceImpl implements FileService {
	private static final int FILE_LIST_BUFFER_SIZE = 1024;
	
	private AssetRepository assetRepository;

	/**
	 * Read the sub folders.
	 *
	 * @param rootFolder the root folder for different type of asset, e.g., images for image asset
	 * @param subPath the subPath under root folder
	 * @return the sub folder path
	 *
	 */
	public List<String> getSubFolders(final String rootFolder, final String subPath) {

		String pathname = getAbsoluteFilePath(rootFolder, subPath);


		File fDir = new File(pathname);
		File[] flist = fDir.listFiles();

		ArrayList<String> subFolders = new ArrayList<String>(FILE_LIST_BUFFER_SIZE);
		String folderPath = null;

		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				folderPath = flist[i].getAbsolutePath();
				folderPath = folderPath.substring(getSubAssetPath(rootFolder).length());

				folderPath = folderPath.replaceAll("\\\\", "/");
				subFolders.add(folderPath);

			}
		}
		return subFolders;
	}


	/**
	 * Get the file list under asset sub folder.
	 *
	 * @param rootFolder the root folder for different type of asset, e.g., images for image asset
	 * @param subPath the subPath under root folder
	 * @return the list of file name with the sub path under root folder
	 *
	 */
	public List<String> getFilesByFolder(final String rootFolder, final String subPath) {

		String pathname = getAbsoluteFilePath(rootFolder, subPath);

		File fDir = new File(pathname);
		File[] flist = fDir.listFiles();

		ArrayList<String> filePathList = new ArrayList<String>(FILE_LIST_BUFFER_SIZE);
		String filePath = null;

		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isFile()) {
				filePath = flist[i].getAbsolutePath();
				filePath = filePath.substring(getSubAssetPath(rootFolder).length());
				filePath = filePath.replaceAll("\\\\", "/");

				filePathList.add(filePath);
			}
		}

		return filePathList;
	}

	/**
	 * Check if the file exists.
	 *
	 * @param rootFolder the root folder for different type of asset, e.g., images for image asset
	 * @param filePath the filePath under root folder
	 * @return true if file exist, otherwise false
	 *
	 */
	public boolean isFileExist(final String rootFolder, final String filePath) {

		String pathname = getAbsoluteFilePath(rootFolder, filePath);

		try {
			return new File(pathname).exists();
		} catch (SecurityException e) {
			throw new EpSystemException("Fail to check file " + pathname, e);
		}

	}


	/**
	 * Delete the folder and all files under it.
	 *
	 * @param rootFolder the root folder for different type of asset, e.g., images for image asset
	 * @param subPath the subPath under root folder
	 * @return true if the entire folder deleted, otherwise false
	 *
	 */
	public boolean deleteEntireFolder(final String rootFolder, final String subPath) {

		String folderPath = getAbsoluteFilePath(rootFolder, subPath);
		return deleteEntireFolder(folderPath);

	}

	/**
	 * Create the folder.
	 *
	 * @param rootFolder the root folder for different type of asset, e.g., images for image asset
	 * @param subPath the subPath under root folder
	 * @return true if the entire folder deleted, otherwise false
	 *
	 */
	public boolean createFolder(final String rootFolder, final String subPath) {

		String folderPath = getAbsoluteFilePath(rootFolder, subPath);

		File path = new File(folderPath);
		if (path.exists()) {
			throw new EpSystemException(path + " already exists.");
		}
		return path.mkdirs();

	}

	/**
	 * Delete the file.
	 *
	 * @param rootFolder the root folder for different type of asset, e.g., images for image asset
	 * @param filePath the filePath under root folder
	 * @return true if the entire folder deleted, otherwise false
	 *
	 */
	public boolean deleteFile(final String rootFolder, final String filePath) {

		String fileFullPath = getAbsoluteFilePath(rootFolder, filePath);
		return deleteFile(fileFullPath);

	}

	private String getAbsoluteFilePath(final String rootFolder, final String subPath) {

		String assetPath = getSubAssetPath(rootFolder);

		String pathname = null;
		if (subPath.startsWith("/")) {
			pathname = assetPath  + subPath;
		} else {
			pathname = assetPath  + File.separator + subPath;
		}

		return pathname;
	}

	private String getSubAssetPath(final String rootFolder) {
		return getAssetRepository().getCatalogAssetPath()
			+ File.separator 
			+ rootFolder;
	}

	private boolean deleteEntireFolder(final String folder) {
		File fDir = new File(folder);
		File[] flist = fDir.listFiles();
		String fileName = null;

		for (int i = 0; i < flist.length; i++) {
			fileName = flist[i].getName();

			if (flist[i].isDirectory()) {
				deleteEntireFolder(folder + File.separator + fileName);
			} else if (!deleteFile(folder + File.separator + fileName)) {
				return false;
			}
		}
		return deleteFile(folder);

	}

	private boolean deleteFile(final String fileFullPath) {
		if (fileFullPath == null) {
			return false;
		}
		File file = new File(fileFullPath);

		try {
			return file.delete();
		} catch (SecurityException e) {
			throw new EpSystemException("Fail to delete file " + fileFullPath, e);
		}
	}

	/**
	 * Rename the file.
	 *
	 * @param rootFolder the root folder for different type of asset, e.g., images for image asset
	 * @param oriFilePath the filePath under root folder
	 * @param newFilePath the filePath under root folder
	 * @return true if rename successful, otherwise false
	 *
	 */
	public boolean renameFile(final String rootFolder, final String oriFilePath, final String newFilePath) {

		String pathname = getAbsoluteFilePath(rootFolder, oriFilePath);
		String newPathname = getAbsoluteFilePath(rootFolder, newFilePath);

		try {
			File oriFile = new File(pathname);
			File newFile = new File(newPathname);

			boolean isRenSucceed = false;
			isRenSucceed = oriFile.renameTo(newFile);
			if (!isRenSucceed) {
				throw new EpSystemException("Fail to rename file " + pathname + " to " + newPathname);
			}
			return isRenSucceed;

		} catch (SecurityException e) {
			throw new EpSystemException("Fail to rename file " + pathname + " to " + newPathname, e);
		}

	}

	/**
	 * @return the assetRepository
	 */
	public AssetRepository getAssetRepository() {
		return assetRepository;
	}

	/**
	 * @param assetRepository the assetRepository to set
	 */
	public void setAssetRepository(final AssetRepository assetRepository) {
		this.assetRepository = assetRepository;
	}
}
