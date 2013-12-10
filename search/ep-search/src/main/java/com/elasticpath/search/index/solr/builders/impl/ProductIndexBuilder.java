package com.elasticpath.search.index.solr.builders.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;

import com.elasticpath.common.pricing.service.PromotedPriceLookupService;
import com.elasticpath.commons.util.IntervalTimer;
import com.elasticpath.domain.search.IndexNotification;
import com.elasticpath.domain.search.IndexNotification.AffectedEntityType;
import com.elasticpath.search.index.pipeline.IndexingPipeline;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalogview.IndexProduct;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.service.rules.RecompilingRuleEngine;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.SolrIndexConstants;
import com.elasticpath.service.search.solr.SolrManager;

/**
 * An implementation of <code>IndexBuilder</code> to create index for products.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.TooManyMethods" })
public class ProductIndexBuilder extends AbstractIndexBuilder { // NOPMD

	private static final long MILLI = 1000L;

	private ProductService productService;

	private CategoryService categoryService;

	private RecompilingRuleEngine ruleEngine;

	private SpellingUpdater spellingUpdater;

	private PromotedPriceLookupService promotedPriceLookupService;

	private SolrManager solrManager;

	private PriceListAssignmentService priceListAssignmentService;

	private IndexingPipeline<Collection<Long>, IndexProduct> productIndexingPipeline;

	private static final Logger LOG = Logger.getLogger(ProductIndexBuilder.class);
	
	/**
	 * Returns index build service name.
	 * 
	 * @return index build service name
	 */
	public String getName() {
		return SolrIndexConstants.PRODUCT_SOLR_CORE;
	}

	/**
	 * Retrieve deleted UIDs.
	 * 
	 * @param lastBuildDate the last build date
	 * @return deleted UIDs.
	 */
	public List<Long> findDeletedUids(final Date lastBuildDate) {
		return productService.findUidsByDeletedDate(lastBuildDate);
	}

	/**
	 * Retrieve added or modified UIDs since last build. <br>
	 * This implementation retrieves both the UIDs of products that have been directly added/modified as well as products that may have been modified
	 * as a result of modifying a containing category.
	 * 
	 * @param lastBuildDate the last build date
	 * @return added or modified UIDs
	 */
	public List<Long> findAddedOrModifiedUids(final Date lastBuildDate) {
		final List<Long> directlyModifiedProductUids = productService.findUidsByModifiedDate(lastBuildDate);
		final Collection<Long> modifiedCategoryUids = findAddedOrModifiedCategoryUids(lastBuildDate);
		final List<Long> indirectlyModifiedProductUids = productService.findUidsByCategoryUids(modifiedCategoryUids);
		final List<Long> indirectlyModifiedBundleUids = productService.findBundleUids(directlyModifiedProductUids);

		final Set<Long> productUidsSet = new HashSet<Long>(directlyModifiedProductUids.size() + indirectlyModifiedProductUids.size()
				+ indirectlyModifiedBundleUids.size());
		productUidsSet.addAll(directlyModifiedProductUids);
		productUidsSet.addAll(indirectlyModifiedProductUids);
		productUidsSet.addAll(indirectlyModifiedBundleUids);
		return new ArrayList<Long>(productUidsSet);
	}

	private Collection<Long> findAddedOrModifiedCategoryUids(final Date lastBuildDate) {
		final List<Long> directlyModifiedCategoryUids = categoryService.findUidsByModifiedDate(lastBuildDate);
		final List<Long> indirectlyModifiedCategoryUids = categoryService.findDescendantCategoryUids(directlyModifiedCategoryUids);
		final Set<Long> categoryUidsSet = new HashSet<Long>(directlyModifiedCategoryUids.size() + indirectlyModifiedCategoryUids.size());
		categoryUidsSet.addAll(directlyModifiedCategoryUids);
		categoryUidsSet.addAll(indirectlyModifiedCategoryUids);
		return categoryUidsSet;
	}

	/**
	 * Retrieve all UIDs.
	 * 
	 * @return all UID
	 */
	public List<Long> findAllUids() {
		return productService.findAllUids();
	}

	/**
	 * Request that the uids of {@code Product} be indexed.
	 * 
	 * @param uids the {@link Product} uids to publish.
	 */
	public void submit(final Collection<Long> uids) {
		productIndexingPipeline.start(new ArrayList<Long>(uids));
	}

	/**
	 * Return the index type this class builds.
	 * 
	 * @return the index type this class builds.
	 */
	public IndexType getIndexType() {
		return IndexType.PRODUCT;
	}

	@Override
	public void onIndexUpdated(final SolrServer server) {
		spellingUpdater.rebuildSpellingIndex(server);
	}
	
	protected PromotedPriceLookupService getPromotedPriceLookupService() {
		return promotedPriceLookupService;
	}

	public void setPromotedPriceLookupService(final PromotedPriceLookupService promotedPriceLookupService) {
		this.promotedPriceLookupService = promotedPriceLookupService;
	}

	@Override
	public void onIndexUpdating(final SolrServer server) {
		super.onIndexUpdating(server);

		// The rule base needs to recompile here because the writing of the rule base quite often
		// happens after a product has been indexed (as they happen in different threads). Not
		// doing this will result in some of the products being indexed against the old rule base
		// and having the SF use the new rule base (which can not only lead to inconsistencies in
		// the facets, but also in the sorting). This will leave some of the products in an
		// inconsistent state and have nothing to notify them that they really are using the wrong
		// rule base.
		// We know that our rule base is semi-smart in that it won't recompile unless some rules
		// have changed.
		ruleEngine.recompileRuleBase();
	}

	/**
	 * Retrieves a set of all Product UIDs that are affected by the given notification. This implementation only supports UPDATE and DELETE
	 * notifications.
	 * 
	 * @param notification the notification of categories or stores that have been updated or deleted
	 * @return a collection of Product UIDs representing the products affected by the given notification
	 * @throws UnsupportedOperationException for notifications other than UPDATE or DELETE
	 */
	@SuppressWarnings("fallthrough")
	public Collection<Long> findUidsByNotification(final IndexNotification notification) {
		switch (notification.getUpdateType()) {
		case UPDATE:
		case DELETE:
			if (AffectedEntityType.CATEGORY.equals(notification.getAffectedEntityType())) {
				return productService.findUidsByCategoryUids(Arrays.asList(notification.getAffectedUid()));
			} else if (AffectedEntityType.STORE.equals(notification.getAffectedEntityType())) {
				return productService.findUidsByStoreUid(notification.getAffectedUid());
			}
			break;
		default:
			break;
		}
		throw new UnsupportedOperationException("not implemented.");
	}

	/**
	 * Sets the product service.
	 * 
	 * @param productService the product service
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * Sets the category service.
	 * 
	 * @param categoryService the category service
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * Sets the {@link RecompilingRuleEngine} instance to use.
	 * 
	 * @param ruleEngine the {@link RecompilingRuleEngine} instance to use
	 */
	public void setRuleEngine(final RecompilingRuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
	}

	/**
	 * Set the minimum spell checking index rebuild interval.
	 * 
	 * @param spellCheckingIndexRebuildInterval The interval in seconds.
	 */
	public void setSpellCheckingIndexRebuildInterval(final int spellCheckingIndexRebuildInterval) {
		createSpellingUpdater(spellCheckingIndexRebuildInterval);
	}

	/**
	 * Sets up a SpellingUpdater.
	 * @param spellCheckingIndexRebuildInterval spellCheckingIndexRebuildInterval in seconds
	 */
	protected void createSpellingUpdater(final int spellCheckingIndexRebuildInterval) {
		spellingUpdater = new IntervalSpellingUpdater(spellCheckingIndexRebuildInterval * MILLI);
	}

	/**
	 * Defines the contract for a spelling index rebuilder/updater.
	 */
	protected interface SpellingUpdater {
		/**
		 * Informs this instance that a rebuild is desired.
		 * @param server the server to rebuild the spelling index.
		 * @return true if the index rebuild runs 
		 */
		boolean rebuildSpellingIndex(final SolrServer server);
	
	}
	
	/**
	 * Rebuilds the spelling index after a certain period of time.
	 */
	protected class IntervalSpellingUpdater implements SpellingUpdater {
	
		private Thread spellingRebuilderThread;

		private final IntervalTimer timer;
		
		/**
		 * Initialises and starts the IntervalTimer.
		 * @param spellingUpdateInterval spellingUpdateInterval in millis
		 */
		IntervalSpellingUpdater(final long spellingUpdateInterval) {
			timer = new IntervalTimer(spellingUpdateInterval);
			timer.setStartPointToNow();
		}

		/**
		 * Informs this instance that a rebuild is desired. If the
		 * SpellingUpdater thread is not already running and the required
		 * interval has elapsed between rebuilds then the SpellingIndex Rebuild will 
		 * be started.
		 * @param server the server to rebuild the spelling index.
		 * @return true if the SpellingIndex Rebuild gets started
		 */
		public boolean rebuildSpellingIndex(final SolrServer server) {
			if (okToRunSpellingRebuild()) {
				spellingRebuilderThread = new Thread(createSpellingUpdaterRunnable(server), "Spelling Updater");
				spellingRebuilderThread.start();
				return true;
			}
			return false;
		}
		
		/**
		 * 
		 * @param server server SolrServer instance
		 * @return a Thread to do the SpellingUpdate
		 */
		protected Runnable createSpellingUpdaterRunnable(final SolrServer server) {
			return new SpellingUpdaterRunnable(server);
		}
		
		private boolean okToRunSpellingRebuild() {
			return !spellingUpdateInProgress() && timer.hasIntervalPassed();
		}

		private boolean spellingUpdateInProgress() {
			return spellingRebuilderThread != null && spellingRebuilderThread.isAlive();
		}
	
		/**
		 * Does the actual work of rebuilding the spelling index.
		 *
		 */
		private class SpellingUpdaterRunnable implements Runnable {

			private final SolrServer server;

			public SpellingUpdaterRunnable(final SolrServer server) {
				this.server = server;
			}

			public void run() {
				LOG.info("SpellingUpdaterRunnable.run start");
				getSolrManager().rebuildSpelling(server);
				timer.setStartPointToNow();
				LOG.info("SpellingUpdaterRunnable.run complete");
			}
		}
	}

	/**
	 * Sets the SOLR Manager instance to use.
	 * 
	 * @param solrManager the SOLR Manager to use
	 */
	public void setSolrManager(final SolrManager solrManager) {
		this.solrManager = solrManager;
	}

	/**
	 * Returns the {@link SolrManager} instance that was used to create the SOLR servers.
	 * 
	 * @return the {@link SolrManager} instance that was used to create the SOLR servers
	 */
	protected SolrManager getSolrManager() {
		return solrManager;
	}

	/**
	 * @return the priceListAssignmentService
	 */
	protected PriceListAssignmentService getPriceListAssignmentService() {
		return priceListAssignmentService;
	}

	/**
	 * @param priceListAssignmentService the priceListAssignmentService to set
	 */
	public void setPriceListAssignmentService(final PriceListAssignmentService priceListAssignmentService) {
		this.priceListAssignmentService = priceListAssignmentService;
	}

	public void setProductIndexingPipeline(final IndexingPipeline<Collection<Long>, IndexProduct> productIndexingPipeline) {
		this.productIndexingPipeline = productIndexingPipeline;
	}

	public IndexingPipeline<Collection<Long>, IndexProduct> getProductIndexingPipeline() {
		return productIndexingPipeline;
	}
}