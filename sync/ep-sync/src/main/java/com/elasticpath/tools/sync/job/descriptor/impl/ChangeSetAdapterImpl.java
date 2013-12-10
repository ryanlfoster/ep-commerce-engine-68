package com.elasticpath.tools.sync.job.descriptor.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.service.changeset.ChangeSetManagementService;
import com.elasticpath.service.changeset.ChangeSetPolicy;
import com.elasticpath.tools.sync.exception.ChangeSetNotFoundException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.job.descriptor.CommandResolver;
import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.SourceSyncRequestAdapter;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * Adapts <code>ChangeSet</code>, builds <code>JobDescriptor</code>. Each <code>TransactionJobDescriptor</code> contains contents of a change
 * set, implying that a change set should be synchronized in a single transaction. No sorting of entities within each
 * <code>TransactionJobDescriptor</code> in provided.
 */
public class ChangeSetAdapterImpl implements SourceSyncRequestAdapter {

	private static final Logger LOG = Logger.getLogger(ChangeSetAdapterImpl.class);

	private ChangeSetManagementService changeSetManagementService;

	private CommandResolver commandResolver;

	private ChangeSetPolicy changeSetPolicy;

	/**
	 * Builds a JobDescriptor wrapping each change set's contents to a transaction job unit.
	 * 
	 * @param configuration information uniquely identifying a set of objects from the source environment
	 * @return JobDescriptor
	 */
	public JobDescriptor buildJobDescriptor(final Object configuration) {
		Collection<ChangeSet> changeSets = initialize(configuration);
		JobDescriptor jobDescriptor = new JobDescriptorImpl();

		for (ChangeSet changeSet : changeSets) {
			Collection<BusinessObjectDescriptor> businessObjectDescriptors = changeSet.getMemberObjects();

			List<TransactionJobDescriptorEntry> jobDescriptorEntries = resolveCommands(removeDuplicated(businessObjectDescriptors));

			wrapChangeSetToJobDescriptor(changeSet, jobDescriptorEntries, jobDescriptor);
		}
		return jobDescriptor;
	}

	/**
	 * Resolves change set by criteria.
	 * 
	 * @param configuration configuration of change set search criteria
	 * @return a collection of found <code>ChangeSet</code>s.
	 */
	Collection<ChangeSet> initialize(final Object configuration) {		
		final String changeSetGuid = handleConfiguration(configuration);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Is about to get a change set list.");
		}

		ChangeSet changeSet = changeSetManagementService.get(changeSetGuid, null);
		if (changeSet == null) {
			throw new ChangeSetNotFoundException("The target system hasn't been synchronized. Change set '"
					+ changeSetGuid + "' does not exist. Please correct change set guid and try again.", changeSetGuid);
		}
		return Arrays.asList(changeSet);
	}

	private String handleConfiguration(final Object configuration) {
		if (!(configuration instanceof String)) {
			throw new SyncToolRuntimeException("ChangeSetAdapterImpl expects String configuration object");
		}
		return (String) configuration;
	}

	/**
	 * The JobDescriptor will contain a single TransactionJobDescriptor which in turn will contain all job descriptor entries. This means that all
	 * change set's content goes to a single transaction.
	 * 
	 * @param changeSet the originator change set 
	 * @param jobDescriptorEntries a list of job descriptor entries
	 * @param jobDescriptor jobDescriptor to put the TransactionJobDescriptor to
	 */
	void wrapChangeSetToJobDescriptor(final ChangeSet changeSet, final List<TransactionJobDescriptorEntry> jobDescriptorEntries, 
								final JobDescriptor jobDescriptor) {
		TransactionJobDescriptor transactionJobDescriptor = new TransactionJobDescriptorImpl();
		transactionJobDescriptor.setName(changeSet.getName());
		transactionJobDescriptor.setJobDescriptorEntries(jobDescriptorEntries);
		jobDescriptor.addTransactionJobDescriptor(transactionJobDescriptor);
	}

	/**
	 * Removes duplicated business object descriptors from the list.
	 * 
	 * @param allBusinessObjects all descriptors
	 * @return a list of unique business object descriptors
	 */
	Collection<BusinessObjectDescriptor> removeDuplicated(final Collection<BusinessObjectDescriptor> allBusinessObjects) {
		return new HashSet<BusinessObjectDescriptor>(allBusinessObjects);
	}

	/**
	 * Provides a list of <code>JobDescriptorEntry</code> with resolved commands.
	 * 
	 * @param businessObjects business objects to be removed or updated
	 * @return list with resolved commands for each business object
	 */
	List<TransactionJobDescriptorEntry> resolveCommands(final Collection<BusinessObjectDescriptor> businessObjects) {
		List<TransactionJobDescriptorEntry> jobDescriptorEntries = new ArrayList<TransactionJobDescriptorEntry>();

		for (BusinessObjectDescriptor businessObjectDescriptor : businessObjects) {
			jobDescriptorEntries.add(createJobDescriptorEntry(businessObjectDescriptor));
		}

		return jobDescriptorEntries;
	}

	/**
	 * Creates single Job Descriptor Entry corresponding to the given Business Object Descriptor. Which command to use is determined depending on
	 * existence of object on source system.
	 * 
	 * @param businessObjectDescriptor the BusinessObjectDescriptor
	 * @return JobDescriptorEntry with recovered command information
	 */
	TransactionJobDescriptorEntry createJobDescriptorEntry(final BusinessObjectDescriptor businessObjectDescriptor) {
		TransactionJobDescriptorEntry jobDescriptorEntry = new TransactionJobDescriptorEntryImpl();

		jobDescriptorEntry.setGuid(businessObjectDescriptor.getObjectIdentifier());
		jobDescriptorEntry.setType(changeSetPolicy.getObjectClass(businessObjectDescriptor));
		jobDescriptorEntry.setCommand(commandResolver.resolveCommandUsingSourceEnv(businessObjectDescriptor));

		return jobDescriptorEntry;
	}

	/**
	 * @param changeSetManagementService to retrieve <code>ChangeSet</code> objects by GUID
	 */
	public void setChangeSetManagementService(final ChangeSetManagementService changeSetManagementService) {
		this.changeSetManagementService = changeSetManagementService;
	}

	@Override
	public void setCommandResolver(final CommandResolver commandResolver) {
		this.commandResolver = commandResolver;
	}

	/**
	 * @param changeSetPolicy <code>ChangeSetPolicy</code> to retrieve business object class.
	 */
	public void setChangeSetPolicy(final ChangeSetPolicy changeSetPolicy) {
		this.changeSetPolicy = changeSetPolicy;
	}
}
