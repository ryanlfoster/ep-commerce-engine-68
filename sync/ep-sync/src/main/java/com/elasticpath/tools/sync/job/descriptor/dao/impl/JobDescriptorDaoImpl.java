/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.job.descriptor.dao.impl;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.client.controller.impl.FileSystemHelper;
import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.dao.JobDescriptorDao;

/**
 * An implementation of the {@link JobDescriptorDao} that saves and loads
 * a {@link JobDescriptor} to/from a file.
 */
public class JobDescriptorDaoImpl implements JobDescriptorDao {

	private static final Logger LOG = Logger.getLogger(JobDescriptorDaoImpl.class);

	private String jobDescriptorFileName;

	private FileSystemHelper fileSystemHelper;		

	/**
	 * Saves a job descriptor to a file.
	 * 
	 * @param jobDescriptor the instance to save
	 */
	public void save(final JobDescriptor jobDescriptor) {
		fileSystemHelper.saveJobDescriptor(jobDescriptor, getJobDescriptorFileName());
		LOG.debug(getJobDescriptorFileName() + " has been saved");
	}

	/**
	 * Load is not supported.
	 * 
	 * @return {@code null}
	 */
	public JobDescriptor load() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 *
	 * @return the fileSystemHelper
	 */
	protected FileSystemHelper getFileSystemHelper() {
		return fileSystemHelper;
	}

	/**
	 *
	 * @param fileSystemHelper the fileSystemHelper to set
	 */
	public void setFileSystemHelper(final FileSystemHelper fileSystemHelper) {
		this.fileSystemHelper = fileSystemHelper;
	}

	/**
	 *
	 * @return the jobDescriptorFileName
	 */
	public String getJobDescriptorFileName() {
		return jobDescriptorFileName;
	}

	/**
	 *
	 * @param jobDescriptorFileName the jobDescriptorFileName to set
	 */
	public void setJobDescriptorFileName(final String jobDescriptorFileName) {
		this.jobDescriptorFileName = jobDescriptorFileName;
	}


}
