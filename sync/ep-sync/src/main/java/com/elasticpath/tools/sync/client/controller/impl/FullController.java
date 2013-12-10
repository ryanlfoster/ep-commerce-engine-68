package com.elasticpath.tools.sync.client.controller.impl;

import org.apache.log4j.Logger;

import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.job.DomainSorter;
import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.SourceObjectCache;
import com.elasticpath.tools.sync.job.TransactionJob;
import com.elasticpath.tools.sync.job.TransactionJobBuilder;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.job.dao.TransactionJobDao;
import com.elasticpath.tools.sync.job.descriptor.JobDescriptor;
import com.elasticpath.tools.sync.job.descriptor.SourceSyncRequestAdapter;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.job.descriptor.dao.JobDescriptorDao;
import com.elasticpath.tools.sync.processing.SerializableObject;
import com.elasticpath.tools.sync.processing.SerializableObjectListener;
import com.elasticpath.tools.sync.processing.SyncJobObjectProcessor;

/**
 * This controller is used to do synchronization between two live systems. It performs the following steps: <li>load data from the source system
 * (usually database) <li>process the data <li>send data to the target system
 */
public class FullController extends AbstractSyncController {

    private static final Logger LOG = Logger.getLogger(FullController.class);

    private static final String SOURCE_SYNC_REQ_ADAPTER = "changeSetAdapter";

    private SyncJobObjectProcessor objectProcessor;

    private JobDescriptorDao jobDescriptorDao;

    private TransactionJobDao transactionJobDao;

    private DomainSorter domainSorter;



    /**
     * Builds TransactionJob.
     * <p>
     * Template method :
     * <ul>
     * <li>Builds {@link JobDescriptor} from source using <code>adapterParam</code></li>
     * <li>Saves {@link JobDescriptor}</li>
     * <li>Builds {@link TransactionJob} using pre-built {@link JobDescriptor}</li>
     * <li>Saves {@link TransactionJob}</li>
     * </ul>
     * </p>
     *
     * @param objectListener the object listener to be used to notify for new available objects
     */
    @Override
    protected void loadTransactionJob(final SerializableObjectListener objectListener) {
        final JobDescriptor jobDescriptor = buildJobDescriptor();

         final SourceObjectCache sourceObjectCache = getSyncBeanFactory().getSourceBean("sourceObjectCache");
        getJobDescriptorDao().save(jobDescriptor);

        final TransactionJob transactionJob = buildTransactionJob(jobDescriptor);

        for (final TransactionJobUnit jobUnit : transactionJob.getTransactionJobUnits()) {
            domainSorter.sort(jobUnit.getJobDescriptorEntries());
        }


        if (sourceObjectCache.supportsPreloading()) {
            for (final TransactionJobUnit jobUnit : transactionJob.getTransactionJobUnits()) {
                for (final TransactionJobDescriptorEntry entry : jobUnit.getJobDescriptorEntries()) {

                    sourceObjectCache.load(entry.getGuid(), entry.getType());

                }
            }
        }

        getTransactionJobDao().save(transactionJob);


        // notify the listener of the new objects
        for (final SerializableObject object : transactionJob) {
            objectListener.processObject(object);
            if (object instanceof JobEntry) {
                final JobEntry entry = (JobEntry) object;
                sourceObjectCache.remove(entry.getGuid(), entry.getType());
            }
        }
    }

    /**
     * Builds JobDescriptor from source using <code>adapterParam</code>.
     *
     * @return {@link JobDescriptor} instance
     */
    protected JobDescriptor buildJobDescriptor() {
        final SourceSyncRequestAdapter sourceSyncRequestAdapter = getSourceSyncRequestAdapter();

        final String adapterParameter = getSyncToolConfiguration().getAdapterParameter();
        LOG.info("Building Job Descriptor for : " + adapterParameter);

        final JobDescriptor jobDescriptor = sourceSyncRequestAdapter.buildJobDescriptor(adapterParameter);

        LOG.info("Job Descriptor has been built");

        return jobDescriptor;
    }

    /**
     * @return {@link SourceSyncRequestAdapter} instance
     */
    protected SourceSyncRequestAdapter getSourceSyncRequestAdapter() {
        final SourceSyncRequestAdapter sourceSyncRequestAdapter = getSyncBeanFactory().getSourceBean(SOURCE_SYNC_REQ_ADAPTER);

        if (sourceSyncRequestAdapter == null) {
            throw new SyncToolConfigurationException("Unable to get source sync request adapter for type: " + SOURCE_SYNC_REQ_ADAPTER);
        }

        LOG.info("Using Adapter : " + SOURCE_SYNC_REQ_ADAPTER);

        return sourceSyncRequestAdapter;
    }

    /**
     * Builds TransactionJob using pre builded {@link JobDescriptor}.
     *
     * @param jobDescriptor the {@link JobDescriptor} which was built using {@link #buildJobDescriptor}.
     * @return TransactionJob instance.
     */
    protected TransactionJob buildTransactionJob(final JobDescriptor jobDescriptor) {
        if (jobDescriptor == null) {
            throw new SyncToolConfigurationException("Unable to build job unit. Job Descriptor wan't provided");
        }

        LOG.debug("Building job unit...");
        final TransactionJobBuilder transactionJobBuilder = getTransactionJobFromBean();

        final TransactionJob transactionJob = transactionJobBuilder.build(jobDescriptor, false);

        LOG.debug("Job Unit has been built");

        return transactionJob;
    }

    protected TransactionJobBuilder getTransactionJobFromBean() {
        return getSyncBeanFactory().getSourceBean("cachingTransactionJobBuilder");
    }

    /**
     * Sets the {@link DomainSorter} instance to use.
     *
     * @param domainSorter {@link DomainSorter} instance to use
     */
    public void setDomainSorter(final DomainSorter domainSorter) {
        this.domainSorter = domainSorter;
    }

    /**
     * @return the object processor instance
     */
    @Override
    protected SyncJobObjectProcessor getObjectProcessor() {
        return objectProcessor;
    }

    /**
     * @param objectProcessor the objectProcessor to set
     */
    public void setObjectProcessor(final SyncJobObjectProcessor objectProcessor) {
        this.objectProcessor = objectProcessor;
    }

    /**
     * @return the jobDescriptorDao
     */
    protected JobDescriptorDao getJobDescriptorDao() {
        return jobDescriptorDao;
    }

    /**
     * @param jobDescriptorDao the jobDescriptorDao to set
     */
    public void setJobDescriptorDao(final JobDescriptorDao jobDescriptorDao) {
        this.jobDescriptorDao = jobDescriptorDao;
    }

    /**
     * @return the transactionJobDao
     */
    protected TransactionJobDao getTransactionJobDao() {
        return transactionJobDao;
    }

    /**
     * @param transactionJobDao the transactionJobDao to set
     */
    public void setTransactionJobDao(final TransactionJobDao transactionJobDao) {
        this.transactionJobDao = transactionJobDao;
    }

    /**
     * Initializes both source and target systems.
     *
     * @param sourceSystem the source system configuration
     * @param targetSystem the target system configuration
     */
    @Override
    protected void initConfig(final SystemConfig sourceSystem, final SystemConfig targetSystem) {
        sourceSystem.initSystem();
        targetSystem.initSystem();
    }

	/**
	 * Calls destroy cleanup on both source and target systems.
	 *
	 * @param sourceSystem the source system
	 * @param targetSystem the target system
	 */
	@Override
	protected void destroyConfig(final SystemConfig sourceSystem, final SystemConfig targetSystem) {
		sourceSystem.destroySystem();
		targetSystem.destroySystem();
	}

}
