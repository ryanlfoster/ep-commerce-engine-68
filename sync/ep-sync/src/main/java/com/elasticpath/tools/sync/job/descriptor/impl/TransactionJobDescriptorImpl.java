package com.elasticpath.tools.sync.job.descriptor.impl;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * Transaction job descriptor object.
 */
@XmlAccessorType(XmlAccessType.NONE)
public class TransactionJobDescriptorImpl implements TransactionJobDescriptor {

	private static final long serialVersionUID = -4512168437211726144L;
	
	@XmlElement(name = "name", required = false)
	private String name;

	@XmlElementWrapper(name = "transactionentries", required = false)
	@XmlElement(name = "transactionentry", required = false, type = TransactionJobDescriptorEntryImpl.class)
	private List<TransactionJobDescriptorEntry> jobDescriptorEntries;

	@Override
	public List<TransactionJobDescriptorEntry> getJobDescriptorEntries() {
		return jobDescriptorEntries;
	}

	@Override
	public void setJobDescriptorEntries(final List<TransactionJobDescriptorEntry> jobDescriptorEntries) {
		this.jobDescriptorEntries = jobDescriptorEntries;
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
	public String toString() {
		return new ToStringBuilder(this).
		append("name", getName()).
		toString();
		
	}
	
	
}
