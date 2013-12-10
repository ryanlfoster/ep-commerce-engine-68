/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.job.descriptor.dao.impl;

import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.dao.JobDescriptorDao;

/**
 * An empty implementation of a {@link JobDescriptorDao}.
 */
public class NullJobDescriptorDaoImpl implements JobDescriptorDao {

	/**
	 * Does nothing.
	 * 
	 * @return {@code null}
	 */
	public JobDescriptor load() {
		return null;
	}

	/**
	 * Does nothing.
	 * 
	 * @param jobDescriptor the descriptor
	 */
	public void save(final JobDescriptor jobDescriptor) {
		// intentionally not implemented
	}

}
