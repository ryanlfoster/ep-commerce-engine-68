package com.elasticpath.tools.sync.job.impl;

import com.elasticpath.tools.sync.job.JobEntry;
import com.elasticpath.tools.sync.job.SourceObjectCache;
import com.elasticpath.tools.sync.job.TransactionJobUnit;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * A cache aware implementation of the Transaction Job Builder.
 */
public class CacheAwareTransactionJobBuilderImpl extends TransactionJobBuilderImpl {

    private SourceObjectCache sourceObjectCache;

    /**
     * Creates the job entry which has a source object cache.
     * @param transactionJobUnit the job unit.
     * @param descriptorEntry the Transaction job description.
     * @return the job Entry.
     */
    @Override
    public JobEntry createJobEntry(final TransactionJobUnit transactionJobUnit, final TransactionJobDescriptorEntry descriptorEntry) {
        final CacheAwareJobEntryImpl jobEntry = new CacheAwareJobEntryImpl(sourceObjectCache);

        jobEntry.setGuid(descriptorEntry.getGuid());
        jobEntry.setType(descriptorEntry.getType());
        jobEntry.setCommand(descriptorEntry.getCommand());
        jobEntry.setTransactionJobUnitName(transactionJobUnit.getName());
        return jobEntry;
    }

    /**
     * @return the sourceObjectCache
     */
    public SourceObjectCache getSourceObjectCache() {
        return sourceObjectCache;
    }

    /**
     * @param sourceObjectCache the sourceObjectCache to set
     */
    public void setSourceObjectCache(final SourceObjectCache sourceObjectCache) {
        this.sourceObjectCache = sourceObjectCache;
    }

}
