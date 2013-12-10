package com.elasticpath.tools.sync.job.custom;

import org.apache.log4j.Logger;

import com.elasticpath.commons.util.SimpleCache;
import com.elasticpath.commons.util.impl.SimpleCacheImpl;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.tools.sync.beanfactory.SyncBeanFactory;
import com.elasticpath.tools.sync.job.Command;
import com.elasticpath.tools.sync.job.SortingPolicy;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;

/**
 * Compares two categories. Necessary because categories must be sorted in order of
 * their hierarchy. Only valid for entries which both have the same Command type, where
 * command type is either UPDATE or REMOVE.
 */
public class CategorySortingPolicy implements SortingPolicy {

	private static final Logger LOG = Logger.getLogger(CategorySortingPolicy.class);

	private SyncBeanFactory syncBeanFactory;

	private final SimpleCache categoryCache = new SimpleCacheImpl();

	@Override
	public int compare(final TransactionJobDescriptorEntry leftEntry, final TransactionJobDescriptorEntry rightEntry) {
		final Category leftCategory = getCategory(leftEntry);
		final Category rightCategory = getCategory(rightEntry);
		//When performing deletes we likely won't have the categories, because they've been
		//deleted already so we just compare the guids to give a testable ordering.
		if (leftCategory == null || rightCategory == null) {
			return leftEntry.getGuid().compareTo(rightEntry.getGuid());
		}
		if (leftEntry.getCommand().equals(Command.REMOVE) && rightEntry.getCommand().equals(Command.REMOVE)) {
			return -compare(leftCategory, rightCategory);
		} else if (leftEntry.getCommand().equals(Command.UPDATE) && rightEntry.getCommand().equals(Command.UPDATE)) {
			return compare(leftCategory, rightCategory);
		}
		LOG.error("CategorysortingPolicy only supports comparisons on jobs with the same command type.");
		return 0;
	}

	/**
	 * Compares two entries which must be of the same type.
	 *
	 * Return values:
	 * -1 if left is master and right is linked
	 *  1 if left is linked and right is master
	 *
	 * If linked status same:
	 *  0 if equal
	 * -1 if left is ancestor of right
	 *  1 if right is ancestor of left
	 *  Arbitrary if not equal and neither is ancestor. Current implementation sorts
	 *  lexicographically by category code.
	 *
	 * @param leftCategory a transaction job descriptor entry
	 * @param rightCategory a transaction job descriptor entry
	 * @return see comment
	 */
	public int compare(final Category leftCategory, final Category rightCategory) {
		if (leftCategory.isLinked() == rightCategory.isLinked()) {
			return compareInternal(leftCategory, rightCategory);
		}

		if (leftCategory.isLinked()) {
			return 1;
		}

		return -1;
	}

	/**
	 * Gets the category (possibly from a cache).
	 * @param entry the descriptor entry
	 * @return the category
	 */
	protected Category getCategory(final TransactionJobDescriptorEntry entry) {

	    final String guid = entry.getGuid();
        final Category category = categoryCache.getItem(guid);
		if (category != null) {
		    return category;
		}

        final EntityLocator entityLocator = syncBeanFactory.getSourceBean("entityLocator");
		 final Category uncachedCategory = (Category) entityLocator.locatePersistenceForSorting(guid, entry.getType());
		 categoryCache.putItem(guid, uncachedCategory);
		 return uncachedCategory;
	}

	/**
	 * Compares two categories by their depth.
	 *
	 * @param leftCategory left category
	 * @param rightCategory right category
	 * @return 0 if equal, -1 of left is ancestor of right, 1 if right ancestor of right, arbitrary otherwise
	 */
	int compareInternal(final Category leftCategory, final Category rightCategory) {
		final int depthDiff = getDepth(leftCategory) - getDepth(rightCategory);
		if (depthDiff == 0) {
			return leftCategory.getCode().compareTo(rightCategory.getCode());
		}
		return depthDiff;
	}

	/**
	 * Get the depth of the category, that is, how many levels of parents it has above it.
	 * If the category has no parent, it's depth is 0.
	 *
	 * @param category the category (must not be null)
	 * @return the depth (which is >= 0)
	 */
	int getDepth(final Category category) {
		int depth = 0;
		Category tempCat = category;
		while (tempCat.getParent() != null) {
			depth++;
			tempCat = tempCat.getParent();
		}
		return depth;
	}

	/**
	 *
	 * @return the syncBeanFactory
	 */
	public SyncBeanFactory getSyncBeanFactory() {
		return syncBeanFactory;
	}

	/**
	 *
	 * @param syncBeanFactory the syncBeanFactory to set
	 */
	public void setSyncBeanFactory(final SyncBeanFactory syncBeanFactory) {
		this.syncBeanFactory = syncBeanFactory;
	}

}
