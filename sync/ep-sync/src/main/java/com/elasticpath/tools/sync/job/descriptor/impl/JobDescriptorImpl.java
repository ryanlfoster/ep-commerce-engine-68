package com.elasticpath.tools.sync.job.descriptor.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptor;

/**
 * Lists <code>JobDecriptorEntry</code> objects describing add or delete operation on one single object.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "jobdescriptor")
public class JobDescriptorImpl implements JobDescriptor {

	private static final long serialVersionUID = -8346605515920408723L;

	@XmlElement(name = "transactionjobdescriptor", required = false, type = TransactionJobDescriptorImpl.class)
	private final List<TransactionJobDescriptor> transactionJobDescriptors = new ArrayList<TransactionJobDescriptor>();

	@Override
	public List<TransactionJobDescriptor> getTransactionJobDescriptors() {
		return transactionJobDescriptors;
	}

	/**
	 * Adds a transaction job descriptor.
	 * 
	 * @param descriptor the descriptor to add
	 */
	public void addTransactionJobDescriptor(final TransactionJobDescriptor descriptor) {
		this.transactionJobDescriptors.add(descriptor);
	}

}
