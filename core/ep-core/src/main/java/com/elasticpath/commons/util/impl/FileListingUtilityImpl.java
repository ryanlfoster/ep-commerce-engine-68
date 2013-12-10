/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.util.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.log4j.Logger;

import com.elasticpath.commons.util.FileListingUnavailableException;
import com.elasticpath.commons.util.FileListingUtility;
import com.elasticpath.commons.util.VfsFileSystemManager;

/**
 * Provides utility methods for listing file and folder names based on a VfsFileSystemManager.
 */
public class FileListingUtilityImpl implements FileListingUtility {

	private static final Logger LOG = Logger.getLogger(FileListingUtilityImpl.class);

	private VfsFileSystemManager fileSystemManager;

	private boolean initialized;

	@Override
	public List<String> findFolderNames(final String folderName) {
		if (!initialized) {
			throw new FileListingUnavailableException("Folder names were not able to be retrieved because file system manager was not initialized.");
		}
		String normalizedFolderName = normalize(folderName);
		List<String> folderNames = new ArrayList<String>();
		try {
			FileObject folder = fileSystemManager.resolveRelativeFile(normalizedFolderName);
			if (folder.exists() && folder.getType().equals(FileType.FOLDER)) {
				for (FileObject childFolder : folder.getChildren()) {
					if (childFolder.getType().equals(FileType.FOLDER)) {
						folderNames.add(childFolder.getName().getBaseName());
					}
				}
			}
		} catch (FileSystemException e) {
			LOG.error("Error attempting to resolve folder: " + normalizedFolderName, e);
		}
		return folderNames;
	}

	/**
	 * Normalize folder name to be used in a VFS.
	 * @param folderName name of folder
	 * @return normalized folder name
	 */
	protected String normalize(final String folderName) {
		if (StringUtils.isEmpty(folderName)) {
			return "";
		}
		// We are dealing with VFS which only uses '/' as a path
		// separator - change any Window's style path separators.
		return folderName.replaceAll("\\\\", "/");
	}

	@Override
	public List<String> findFileNames(final String folderName, final String regex, final boolean includeExtension) {
		if (!initialized) {
			throw new FileListingUnavailableException("File names were not able to be retrieved because file system manager was not initialized.");
		}
		String normalizedFolderName = normalize(folderName);
		List<String> fileNames = new ArrayList<String>();
		try {
			FileObject folder = fileSystemManager.resolveRelativeFile(normalizedFolderName);
			if (folder.exists() && folder.getType().equals(FileType.FOLDER)) {
				Pattern pattern = Pattern.compile(regex);
				for (FileObject childFile : folder.getChildren()) {
					String childName = childFile.getName().getBaseName();
					if (childFile.getType().equals(FileType.FILE) && pattern.matcher(childName).matches()) {
						fileNames.add(getFormattedFileName(childName, includeExtension));
					}
				}
			}
		} catch (FileSystemException e) {
			LOG.error("Error attempting to resolve folder: " + normalizedFolderName, e);
		}
		return fileNames;
	}

	private String getFormattedFileName(final String fileName, final boolean includeExtension) {
		String formattedFileName = fileName;
		if (!includeExtension) {
			int periodPosition = fileName.lastIndexOf('.');
			if (periodPosition != -1) {
				formattedFileName = fileName.substring(0, periodPosition);
			}
		}
		return formattedFileName;
	}
	
	/**
	 * Set the file system manager and initializes it.
	 * @param fileSystemManager the VFS file system manager
	 */
	public void setFileSystemManager(final VfsFileSystemManager fileSystemManager) {
		this.fileSystemManager = fileSystemManager;
		initialized = fileSystemManager.initialize();
	}
}
