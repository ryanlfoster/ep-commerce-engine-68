package com.elasticpath.importexport.common.manifest;

import com.elasticpath.importexport.common.types.JobType;

/**
 * The ManifestBuilder. Builds The Manifest.
 */
public interface ManifestBuilder {

	/**
	 * Adds a Resource.
	 * 
	 * @param jobType to use
	 * @param resource to add
	 */
	void addResource(final JobType jobType, final String resource);

	/**
	 * Builds the Manifest.
	 *
	 * @return Manifest instance.
	 */
	Manifest build();

}