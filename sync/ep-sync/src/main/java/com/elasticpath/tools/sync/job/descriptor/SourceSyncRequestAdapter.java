package com.elasticpath.tools.sync.job.descriptor;

/**
 * Adapts information about domain objects from a source environment and provides JobDescriptor which contains job entries grouped in a batches. A
 * batch which constitutes a <code>TransactionJobDescriptor</code> must be logically present as a single unit of work. It will be sorted
 * considering natural ep domain objects ordering (if required) and be synchronized in a single transaction.
 */
public interface SourceSyncRequestAdapter {

	/**
	 * Adapts information about objects from source server and builds a JobDescriptor object.
	 * 
	 * @param configuration information uniquely identifying a set of objects from the source environment
	 * @return a list of <code>JobDescriptorEntry</code>
	 */
	JobDescriptor buildJobDescriptor(final Object configuration);

	/**
	 * @param commandResolver command resolver determining command for synchronization
	 */
	void setCommandResolver(final CommandResolver commandResolver);
}
