package com.elasticpath.tools.sync.job.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.job.Command;
import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.JobEntryCreator;
import com.elasticpath.tools.sync.job.TransactionJob;
import com.elasticpath.tools.sync.job.TransactionJobBuilder;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.job.descriptor.impl.TransactionJobDescriptorEntryImpl;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;
import com.elasticpath.tools.sync.target.AssociatedDaoAdapter;
import com.elasticpath.tools.sync.target.DaoAdapter;
import com.elasticpath.tools.sync.target.DaoAdapterFactory;

/**
 * Having provided JobDescriptor information, groups those job descriptor entries which belong to the same business object and the same operation,
 * retrieves appropriate source objects from the source environment and populates a JobUnit.
 */
public class TransactionJobBuilderImpl implements TransactionJobBuilder {

    private static final Logger LOG = Logger.getLogger(TransactionJobBuilderImpl.class);

    private EntityLocator entityLocator;

    private DaoAdapterFactory daoAdapterFactory;

    @Override
    public TransactionJob build(final JobDescriptor jobDescriptor, final boolean populate) throws SyncToolConfigurationException {
        LOG.debug("Is about to build a list of TransactionJobUnit objects.");
        final TransactionJob transactionJob = new TransactionJobImpl();
        for (final TransactionJobDescriptor transactionJobDescriptor : jobDescriptor.getTransactionJobDescriptors()) {
            final TransactionJobUnit transactionJobUnit = createTransactionJobUnit(transactionJobDescriptor, populate);
            transactionJob.addTransactionJobUnit(transactionJobUnit);
        }

        return transactionJob;
    }

    /**
     * Creates transaction job unit that will be processed in one transaction.
     *
     * @param transactionJobDescriptor the job descriptor list that should be processed in one transaction, this list should be ordered based on
     *            domain model dependencies.
     * @param populate should the created job entry be populated right from the beginning
     * @return transaction job unit
     * @throws SyncToolConfigurationException in case entityLocator has not been not properly initialized
     */
    TransactionJobUnit createTransactionJobUnit(final TransactionJobDescriptor transactionJobDescriptor, final boolean populate)
            throws SyncToolConfigurationException {

        final TransactionJobUnit transactionJobUnit = new TransactionJobUnitImpl(new JobEntryCreator() {

            public JobEntry createJobEntry(final TransactionJobUnit transactionJobUnit, final TransactionJobDescriptorEntry descriptorEntry) {
                if (descriptorEntry instanceof JobEntry) {
                    return (JobEntry) descriptorEntry;
                }
                return TransactionJobBuilderImpl.this.createJobEntry(transactionJobUnit, descriptorEntry);
            }
        });

        transactionJobUnit.setName(transactionJobDescriptor.getName());
        for (final TransactionJobDescriptorEntry entry : transactionJobDescriptor.getJobDescriptorEntries()) {
            final Collection<TransactionJobDescriptorEntry> descriptorEntries = createDependentEntries(entry);
            descriptorEntries.add(entry);

            for (final TransactionJobDescriptorEntry descriptorEntry : descriptorEntries) {
                if (populate) {
                    transactionJobUnit.addJobEntry(createJobEntry(transactionJobUnit, descriptorEntry));
                } else {
                    transactionJobUnit.addJobEntry(descriptorEntry);
                }
            }
        }

        return transactionJobUnit;
    }

    /**
     * Build a list of {@link TransactionJobDescriptorEntry}. This handles adding associated types to the list as well as those that are specified.
     */
    private Collection<TransactionJobDescriptorEntry> createDependentEntries(final TransactionJobDescriptorEntry jobDescriptorEntry) {
        final List<TransactionJobDescriptorEntry> result = new ArrayList<TransactionJobDescriptorEntry>();

        final DaoAdapter<? super Persistable> adapter = daoAdapterFactory.getDaoAdapter(jobDescriptorEntry.getType());
        for (final Class<? extends Persistable> clazz : adapter.getAssociatedTypes()) {
			@SuppressWarnings("unchecked")
			final AssociatedDaoAdapter<Persistable> associatedAdapter = (AssociatedDaoAdapter<Persistable>) daoAdapterFactory.getDaoAdapter(clazz);

            for (final String guid : associatedAdapter.getAssociatedGuids(jobDescriptorEntry.getType(), jobDescriptorEntry.getGuid())) {
                result.add(createJobDescriptorEntry(associatedAdapter.getType(), guid, jobDescriptorEntry.getCommand()));
            }
        }

        return result;
    }

    /**
     * Create a new job descriptor entry.
     *
     * @param type the class of the entry
     * @param guid the guid of the object this entry relates to
     * @param command the command
     * @return a {@link TransactionJobDescriptorEntry}
     */
    protected TransactionJobDescriptorEntry createJobDescriptorEntry(final Class<?> type, final String guid, final Command command) {
        final TransactionJobDescriptorEntry entry = new TransactionJobDescriptorEntryImpl();
        entry.setGuid(guid);
        entry.setType(type);
        entry.setCommand(command);
        return entry;
    }

    /**
     * Creates job entry based in given descriptor entry.
     *
     * @param transactionJobUnit the parent transaction job unit
     * @param descriptorEntry the job descriptor entry
     * @return job entry
     */
    public JobEntry createJobEntry(final TransactionJobUnit transactionJobUnit, final TransactionJobDescriptorEntry descriptorEntry) {
        final JobEntry jobEntry = new JobEntryImpl();
        jobEntry.setGuid(descriptorEntry.getGuid());
        jobEntry.setType(descriptorEntry.getType());
        jobEntry.setCommand(descriptorEntry.getCommand());
        jobEntry.setSourceObject(getObject(descriptorEntry));
        jobEntry.setTransactionJobUnitName(transactionJobUnit.getName());
        return jobEntry;
    }

    /**
     * Retrieves persistent object by job descriptor.
     *
     * @param descriptorEntry the job descriptor entry
     * @return persistent object
     * @throws SyncToolConfigurationException in case entityLocator has not been not properly initialized
     */
    protected Persistable getObject(final TransactionJobDescriptorEntry descriptorEntry) throws SyncToolConfigurationException {
        return entityLocator.locatePersistence(descriptorEntry.getGuid(), descriptorEntry.getType());
    }

    /**
     * @param entityLocator the entityLocator to set
     */
    public void setEntityLocator(final EntityLocator entityLocator) {
        this.entityLocator = entityLocator;
    }

    /**
     * @param daoAdapterFactory the daoAdapterFactory to set
     */
    public void setDaoAdapterFactory(final DaoAdapterFactory daoAdapterFactory) {
        this.daoAdapterFactory = daoAdapterFactory;
    }

    /**
     * @return the entityLocator
     */
    public EntityLocator getEntityLocator() {
        return entityLocator;
    }
}
