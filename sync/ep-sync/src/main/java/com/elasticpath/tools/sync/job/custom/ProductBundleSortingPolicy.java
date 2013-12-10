package com.elasticpath.tools.sync.job.custom;

import com.elasticpath.commons.util.SimpleCache;
import com.elasticpath.commons.util.impl.SimpleCacheImpl;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.tools.sync.beanfactory.SyncBeanFactory;
import com.elasticpath.tools.sync.job.SortingPolicy;
import com.elasticpath.tools.sync.job.descriptor.TransactionJobDescriptorEntry;
import com.elasticpath.tools.sync.merge.configuration.EntityLocator;

/**
 *
 * Product bundle sort policy class.
 *
 */
public class ProductBundleSortingPolicy implements SortingPolicy {

	private SyncBeanFactory syncBeanFactory;
	private final SimpleCache productBundleCache = new SimpleCacheImpl();
	@Override
	public int compare(final TransactionJobDescriptorEntry leftEntry, final TransactionJobDescriptorEntry rightEntry) {
		final ProductBundle leftProductBundle = getProductBundle(leftEntry);
		final ProductBundle rightProductBundle = getProductBundle(rightEntry);

		if (leftProductBundle.hasDescendant(rightProductBundle)) {
			return 1;
		}

		if (rightProductBundle.hasDescendant(leftProductBundle)) {
			return -1;
		}

		return 0;
	}

	/**
	 *
	 * @param entry the descriptor entry
	 * @return the category
	 */
	private ProductBundle getProductBundle(final TransactionJobDescriptorEntry entry) {
	    final String guid = entry.getGuid();
        final ProductBundle productBundle = productBundleCache.getItem(guid);
        if (productBundle != null) {
            return productBundle;
        }

		final EntityLocator entityLocator = syncBeanFactory.getSourceBean("entityLocator");
		final ProductBundle uncachedProductBundle = (ProductBundle) entityLocator.locatePersistenceForSorting(guid, entry.getType());
		productBundleCache.putItem(guid, uncachedProductBundle);
		return uncachedProductBundle;
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
