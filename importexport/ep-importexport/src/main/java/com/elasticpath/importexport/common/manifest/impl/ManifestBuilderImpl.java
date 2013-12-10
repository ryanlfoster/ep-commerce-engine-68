package com.elasticpath.importexport.common.manifest.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.importexport.common.exception.runtime.EngineRuntimeException;
import com.elasticpath.importexport.common.manifest.Manifest;
import com.elasticpath.importexport.common.manifest.ManifestBuilder;
import com.elasticpath.importexport.common.types.JobType;

/**
 * The ManifestBuilder. Builds The Manifest.
 */
public class ManifestBuilderImpl implements ManifestBuilder {

	private List<JobType> jobTypePriority;

	private final Map<JobType, String> resources = new HashMap<JobType, String>();

	/**
	 * Sets jobTypePriority.
	 * 
	 * @return the jobTypePriority
	 */
	public List<JobType> getJobTypePriority() {
		return jobTypePriority;
	}

	/**
	 * Sets jobTypePriority.
	 * 
	 * @param jobTypePriority the jobTypePriority to set
	 */
	public void setJobTypePriority(final List<JobType> jobTypePriority) {
		this.jobTypePriority = jobTypePriority;
	}

	/**
	 * Adds a Resource.
	 * 
	 * @param jobType to use
	 * @param resource to add
	 */
	public void addResource(final JobType jobType, final String resource) {
		if (!jobTypePriority.contains(jobType)) {
			throw new EngineRuntimeException("IE-40100", jobType.getTagName());
		}
		resources.put(jobType, resource);
	}

	/**
	 * Builds the Manifest.
	 * 
	 * @return Manifest instance.
	 */
	public Manifest build() {
		Manifest manifest = new Manifest();
		for (JobType jobType : jobTypePriority) {
			final String resource = resources.get(jobType);
			if (resource != null) {
				manifest.addResource(resource);
			}
		}
		return manifest;
	}
}
