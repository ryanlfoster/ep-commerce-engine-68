package com.elasticpath.tools.sync.job.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.JobEntryCreator;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.processing.AbstractObjectProvider;
import com.elasticpath.tools.sync.processing.SerializableObject;

/**
 * Default implementation of <code>TransactionJobUnit</code>.
 */
public class TransactionJobUnitImpl extends AbstractObjectProvider<SerializableObject> implements TransactionJobUnit {
	
	/**
	 * The serial version UID.
	 */
	private static final long serialVersionUID = 50000000001L;

	private final transient List<TransactionJobDescriptorEntry> jobDescriptorEntries = new ArrayList<TransactionJobDescriptorEntry>();

	private String name;

	private transient JobEntryCreator jobEntryCreator;

	/**
	 *
	 * @param jobEntryCreator the job entry creator
	 */
	public TransactionJobUnitImpl(final JobEntryCreator jobEntryCreator) {
		this.jobEntryCreator = jobEntryCreator;
	}

	@Override
	public List<TransactionJobDescriptorEntry> getJobDescriptorEntries() {
		return jobDescriptorEntries;
	}

	@Override
	public List<JobEntry> createJobEntries() {
		List<JobEntry> entries = new ArrayList<JobEntry>();
		for (TransactionJobDescriptorEntry entry : jobDescriptorEntries) {
			entries.add(jobEntryCreator.createJobEntry(this, entry));
		}
		return entries;
	}
	
	/**
	 * Adds given job unit.
	 * 
	 * @param jobUnit the job unit to add
	 */
	public void addJobEntry(final TransactionJobDescriptorEntry jobUnit) {
		jobDescriptorEntries.add(jobUnit);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	protected JobEntry getElement(final int index) {
		final TransactionJobDescriptorEntry entryDesc = jobDescriptorEntries.get(index);
		return jobEntryCreator.createJobEntry(this, entryDesc);
	}

	@Override
	protected int getSize() {
		return jobDescriptorEntries.size();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
		append("name", getName()).
		toString();
	}

	/**
	 *
	 * @return the jobEntryCreator
	 */
	protected JobEntryCreator getJobEntryCreator() {
		return jobEntryCreator;
	}

	/**
	 *
	 * @param jobEntryCreator the jobEntryCreator to set
	 */
	public void setJobEntryCreator(final JobEntryCreator jobEntryCreator) {
		this.jobEntryCreator = jobEntryCreator;
	}
	
}
