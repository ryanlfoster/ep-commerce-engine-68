package com.elasticpath.tools.sync.job.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;
import com.elasticpath.tools.sync.job.Command;
import com.elasticpath.tools.sync.job.DomainSorter;
import com.elasticpath.tools.sync.job.GlobalEpDependencyDescriptor;
import com.elasticpath.tools.sync.job.SortingPolicy;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;

/**
 * Default implementation of <code>DomainSorter</code>. Allows registering of custom sorting
 * policies to handle sorting entities of the same type which have special ordering requirements 
 * i.e. categories where master categories should be added before linked categories.
 */
public class DomainSorterImpl implements DomainSorter {

	private GlobalEpDependencyDescriptor globalEpDependencyDescriptor;

	private Map<Class< ? >, SortingPolicy> customSortingPolicy = new HashMap<Class< ? >, SortingPolicy>();

	@Override
	public void sort(final List< ? extends TransactionJobDescriptorEntry> epDomainObjects) {
		Collections.sort(epDomainObjects, domainObjectComparator);
	}

	private final Comparator<TransactionJobDescriptorEntry> domainObjectComparator = new Comparator<TransactionJobDescriptorEntry>() {

		private static final int LEFT_FIRST = -1;
		private static final int RIGHT_FIRST = 1;
		
		/**
		 * Entries are compared by domain object type and synchronisation command. Remove is always < then Update. Otherwise it depends on position
		 * in globalEpDependencyDescriptor
		 * 
		 * @param leftEntry left hand side parameter
		 * @param rightEntry right hand side parameter
		 * @return 0 if entries are equal, -1 if left < right, 1 if left > right
		 */
		public int compare(final TransactionJobDescriptorEntry leftEntry, final TransactionJobDescriptorEntry rightEntry) {
			if (Command.REMOVE.equals(leftEntry.getCommand()) && Command.UPDATE.equals(rightEntry.getCommand())) {
				return RIGHT_FIRST;
			} else if (Command.UPDATE.equals(leftEntry.getCommand()) && Command.REMOVE.equals(rightEntry.getCommand())) {
				return LEFT_FIRST;
			} else if (shouldCustomSort(leftEntry, rightEntry)) {
				return compareWithCustomPolicy(leftEntry, rightEntry);
			} else if (bothAreUpdates(leftEntry, rightEntry)) { 
				return compareEntriesWithoutSortingPolicy(leftEntry, rightEntry);
			} else if (bothAreRemoves(leftEntry, rightEntry)) {
				return -compareEntriesWithoutSortingPolicy(leftEntry, rightEntry);
			}
			throw new SyncToolRuntimeException("Unexpected Command type");
		}

		/**
		 * We only have the necessary information to do custom sorts when both are updates, and we only do them if both 
		 * entries are the same type and a policy has been registered for that type. 
		 */
		private boolean shouldCustomSort(final TransactionJobDescriptorEntry leftEntry, final TransactionJobDescriptorEntry rightEntry) {
			return typesAreSame(leftEntry, rightEntry) && hasCustomSortPolicy(leftEntry.getType());
		}

		private boolean hasCustomSortPolicy(final Class< ? > type) {
			return customSortingPolicy.get(type) != null;
		}

		private boolean bothAreRemoves(final TransactionJobDescriptorEntry leftEntry, final TransactionJobDescriptorEntry rightEntry) {
			return Command.REMOVE.equals(leftEntry.getCommand()) && Command.REMOVE.equals(rightEntry.getCommand());
		}

		private boolean bothAreUpdates(final TransactionJobDescriptorEntry leftEntry, final TransactionJobDescriptorEntry rightEntry) {
			return Command.UPDATE.equals(leftEntry.getCommand()) && Command.UPDATE.equals(rightEntry.getCommand());
		}
		
		private boolean typesAreSame(final TransactionJobDescriptorEntry leftEntry, final TransactionJobDescriptorEntry rightEntry) {
			return rightEntry.getType().equals(leftEntry.getType());
		}

		private int compareWithCustomPolicy(final TransactionJobDescriptorEntry leftEntry, final TransactionJobDescriptorEntry rightEntry) {
			return customSortingPolicy.get(leftEntry.getType()).compare(leftEntry, rightEntry);
		}

		/**
		 * Left before right means index(left) < index(right)
		 * Left after right means index(left) > index(right)
		 * Left equals right means index(left) == index(right)
		 * 
		 *  This function returns index(left) - index(right) so: 
		 *  	l before r: -
		 *  	r before l: +
		 *					0  	  
		 * 
		 * Which matches the standard equals contract. When doing removes
		 * reverse the order by negating the result.
		 */
		private int compareEntriesWithoutSortingPolicy(final TransactionJobDescriptorEntry leftEntry, 
				final TransactionJobDescriptorEntry rightEntry) {
			return globalEpDependencyDescriptor.getPlace(leftEntry.getType()) - globalEpDependencyDescriptor.getPlace(rightEntry.getType());
		}
	};

	/**
	 * @param globalEpDependencyDescriptor contains sorted list of dependent domain object types.
	 */
	public void setGlobalEpDependencyDescriptor(final GlobalEpDependencyDescriptor globalEpDependencyDescriptor) {
		this.globalEpDependencyDescriptor = globalEpDependencyDescriptor;
	}

	/**
	 * @param customSortingPolicy the customSortingPolicy to set
	 */
	public void setCustomSortingPolicy(final Map<Class< ? >, SortingPolicy> customSortingPolicy) {
		this.customSortingPolicy = customSortingPolicy;
	}

}
