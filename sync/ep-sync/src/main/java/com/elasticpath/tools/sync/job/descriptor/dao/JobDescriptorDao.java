/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.job.descriptor.dao;

import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;


/**
 * A DAO for loading/saving a {@link JobDescriptor}.
 */
public interface JobDescriptorDao {

	/**
	 * Saves a job descriptor.
	 * 
	 * @param jobDescriptor the instance to save
	 */
	void save(JobDescriptor jobDescriptor);

	
	/**
	 * Loads a job descriptor.
	 * 
	 * @return the loaded instance
	 */
	JobDescriptor load();
}
