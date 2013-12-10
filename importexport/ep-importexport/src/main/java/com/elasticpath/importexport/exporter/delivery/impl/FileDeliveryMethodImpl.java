package com.elasticpath.importexport.exporter.delivery.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.elasticpath.importexport.common.exception.runtime.ExportRuntimeException;

/**
 * Simple delivery method writes file into file system. 
 */
public class FileDeliveryMethodImpl extends AbstractDeliveryMethodImpl {

	private File target;
	
	/**
	 * Starts next file's delivering process.<br>
	 * Creates target path and delivers file to this target.
	 *
	 * @param fileName the name of file to be delivered
	 * @return outputStream of destination to deliver into
	 */	
	public OutputStream deliver(final String fileName) {
		try {			
			return new FileOutputStream(createFileWithDirs(fileName));
		} catch (FileNotFoundException e) {
			throw new ExportRuntimeException("IE-20300", e, fileName);			
		} 
	}

	/**
	 * Initialize target full path to deliver into.
	 * If destination folder doesn't exist it will be created
	 * 
	 * @param target full path to folder in the local file system to deliver into
	 */
	public void initialize(final String target) {
		this.target = new File(target);
	}
	
	/*
	 * Creates File, and all directories that in path to file 
	 * @return File
	 */
	private File createFileWithDirs(final String fileName) {		
		File outputFile = new File(target.getAbsolutePath() + File.separatorChar + fileName);
		File parentDir  = outputFile.getParentFile();
		if (parentDir != null) {
			parentDir.mkdirs();
		}
		return outputFile;
	}
}
