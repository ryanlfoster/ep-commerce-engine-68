/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.catalog.ProductRecommendationService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.service.store.StoreService;

/**
 * Create recommendation associations between the 
 * products bought by each customer in a specified period for a 
 * specific store, thereby creating "Customers who purchased this
 * product also purchased product X" associations between products.
 * <p>
 * Recommendations are created in both directions.  That is, if 
 * <code>A</code> and <code>B</code> were bought by one
 * customer then two recommendations will be created <code>A -&gt; B</code>
 * and <code>B -&gt; A</code>
 * </p>
 * <p>
 * All relationships between co-purchased products are calculated but only
 * the most popular recommendations are persisted and made available to 
 * consumers of product recommendations.
 * </p>
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * Customer 1 purchased Products 1, 2, 3<br/> 
 * Customer 2 purchased Products 1, 2, 4, 5<br/>
 * <br/>
 * The maximum number of recommendations created is limited to 4.
 * <p>
 * Recommendations created:
 * <ul>
 *   <li>1 => 2 (3 4 5) (3 4 5) (3 4 5)
 *   <li>2 => 1 (3 4 5) (3 4 5) (3 4 5)
 *   <li>3 => (1 2) (1 2)
 *   <li>4 => (1 2 5) (1 2 5) (1 2 5)
 *   <li>5 => (1 2 4) (1 2 4) (1 2 4)
 * </ul>
 * Note that the relationship between product 1 and product 2 is twice as 
 * popular as the other recommendations - therefore their order in the 
 * recommendations is ensured.<br />
 * All other recommendations (denoted by the bracketed values) show that the
 * ordering of equally popular recommendations is undefined. 
 * </p>
 */
public class ProductRecommendationServiceImpl extends AbstractEpPersistenceServiceImpl implements ProductRecommendationService {

	private static final Logger LOG = Logger.getLogger(ProductRecommendationServiceImpl.class);
	
	private static final String HISTORY_DAYS_PATH = "COMMERCE/STORE/PRODUCTRECOMMENDATIONS/numberOrderHistoryDays";

	private static final String MAX_RECOMMENDATIONS_PATH = "COMMERCE/STORE/PRODUCTRECOMMENDATIONS/numberMaxRecommendations";

	/** Setting this value as the history months or max recommendations disables the product recommendation feature. */
	private static final int DISABLE_VALUE = -1;

	private ProductService productService;
	
	private ProductAssociationService productAssociationService;
	
	private SettingsReader settingsReader;
	
	private StoreService storeService;

	private TimeService timeService;

	private FetchGroupLoadTuner fetchGroupLoadTuner;
	
	/**
	 * Get the max number of days of order history to use in computing the recommendations for the store
	 * represented by the given code.
	 * Calls {@link #getSettingsReader()}.
	 * @param storeCode the store code
	 * @return the max number of days of order history
	 */
	int getMaxOrderHistoryDays(final String storeCode) {
		return Integer.parseInt(getSettingsReader().getSettingValue(HISTORY_DAYS_PATH, storeCode).getValue());
	}
	
	/**
	 * Get the max number of recommendations for the store represented by the given code.
	 * Calls {@link #getSettingsReader()}.
	 * @param storeCode the store code
	 * @return the max number of recommendations
	 */
	int getMaxRecommendations(final String storeCode) {
		return Integer.parseInt(getSettingsReader().getSettingValue(MAX_RECOMMENDATIONS_PATH, storeCode).getValue());
	}

	/**
	 * Re-compute product recommendations for each product in the system, on a store-by-store basis.
	 * For each store's catalog, product recommendations will be computed based on "customers who
	 * purchased this product also purchased this other product". 
	 */
	public void updateRecommendations() {
		final long startTime = System.currentTimeMillis();
		LOG.info("Start product recommendation quartz job at: " + new Date(startTime));
		
		final List<Store> allStores = getStoreService().findAllCompleteStores();
		for (Store store : allStores) {
			if (isProductRecommendationEnabled(store)) {
				updateProductRecommendations(store);
			}
		}
		
		LOG.info("Product recommendation quartz job completed in (ms): " + (System.currentTimeMillis() - startTime));
	}
	
	/**
	 * Re-compute product recommendations for each product in the given Store.
	 * @param store the store in which the product recommendations should be recomputed.
	 */
	protected void updateProductRecommendations(final Store store) {
		final long startTime = System.currentTimeMillis();

		final List<Object[]> results = getPersistenceEngine().retrieveByNamedQuery("CUSTOMER_PRODUCTS_PURCHASED",
				getOrderHistoryStartingDate(store),
				store.getCode());
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Store: " + store.getCode() + " Retrieved recommendations data for(ms): " + (System.currentTimeMillis() - startTime));
		}
		
		Map<Long, Set<Long>> customerToPurchasedProductsMap = createCustomerToPurchasedProductsMap(results);
		Map<Long, RecommendationSet> productToRecommendationsMap = createProductToRecommendationsMap(customerToPurchasedProductsMap, store.getCode());
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Store: " + store.getCode() + " Processed recommendations data for(ms): " + (System.currentTimeMillis() - startTime));
		}
		
		final Catalog catalog = store.getCatalog();
		if (catalog == null) {
			LOG.error("Store: " + store.getCode() + " does not have associated catalog");
			return;
		}
		updateProductRecommendations(productToRecommendationsMap, catalog);
	}
	
	/**
	 * @param store store
	 * @return true if product recommendations are disabled, false if not.
	 */
	protected boolean isProductRecommendationEnabled(final Store store) {
		return getMaxOrderHistoryDays(store.getCode()) != DISABLE_VALUE && getMaxRecommendations(store.getCode()) != DISABLE_VALUE;		
	}

	private Date getOrderHistoryStartingDate(final Store store) {
		Calendar calendar = new GregorianCalendar();
		calendar.add(Calendar.DATE, getMaxOrderHistoryDays(store.getCode()) * -1);
		return calendar.getTime();
	}

	/**
	 * Creates a map of product recommendations from a map of products purchased by each customer. Note: this is protected for unit testing partial
	 * results. Clients should not invoke this method.
	 *
	 * @param customerToPurchasedProductsMap the map produced by calling <code>createCustomerToPurchasedProductsMap</code>
	 * @param storeCode store code
	 * @return a <code>Map</code> of productIds to <code>RecommendationSet</code> objects.
	 */
	protected Map<Long, RecommendationSet> createProductToRecommendationsMap(final Map<Long, Set<Long>> customerToPurchasedProductsMap,
			final String storeCode) {
		Map<Long, RecommendationSet> productRecommendationMap = new HashMap<Long, RecommendationSet>();
		
		for (Set<Long> customerPurchaseSet : customerToPurchasedProductsMap.values()) {
			addPurchaseSetToProductRecommendationMap(productRecommendationMap, customerPurchaseSet, storeCode);
		}
		return productRecommendationMap;
	}

	/**
	 * Adds a set of customer purchases to the given map of products to product recommendations.
	 *
	 * @param productRecommendationMap the main map of products to their recommendation objects.
	 * @param customerPurchaseSet the set of products purchased together.
	 * @param storeCode Store
	 */
	protected void addPurchaseSetToProductRecommendationMap(final Map<Long, RecommendationSet> productRecommendationMap, 
			final Set<Long> customerPurchaseSet, final String storeCode) {

		for (long sourceProductId : customerPurchaseSet) {
			RecommendationSet recommendationSet = productRecommendationMap.get(sourceProductId);

			if (recommendationSet == null) {
				recommendationSet = new RecommendationSet(getMaxRecommendations(storeCode));
			}

			for (long coPurchasedProductId : customerPurchaseSet) {
				if (coPurchasedProductId != sourceProductId) {
					recommendationSet.addRecommendation(coPurchasedProductId);
				}
			}

			productRecommendationMap.put(sourceProductId, recommendationSet);
		}
	}

	/**
	 * Performs the update of a product's recommendations, given the previously computed recommendation information.
	 * This implementation calls updateProductAssociations().
	 *
	 * @param productToRecommendationsMap the Map of product recommendation data produced by <code>createProductToRecommendationsMap</code>.
	 * @param catalog the catalog in which the recommendations should be updated
	 */
	protected void updateProductRecommendations(final Map<Long, RecommendationSet> productToRecommendationsMap, final Catalog catalog) {
		
		for (final Entry<Long, RecommendationSet> entry : productToRecommendationsMap.entrySet()) {
			final RecommendationSet recommendationSet = entry.getValue();
			final Product sourceProduct = productService.getTuned(entry.getKey(), fetchGroupLoadTuner);

			if (sourceProduct == null) {
				if (LOG.isInfoEnabled()) {
					LOG.info("Source product with uidPk: " + entry.getKey() 
							+ " does not exist any more. Skipping updating its recommendations.");
				}
				continue;
			} else {
				updateProductAssociations(sourceProduct, recommendationSet, catalog);
			}
		}
	}

	/**
	 * Updates a products set of Product Associations by removing the recommendation associations that no longer exist and adding any new ones.
	 * This implementation calls deleteObsoleteRecommendations() and addNewRecommendations().
	 *
	 * @param newRecommendations the new set of recommendations for a source product
	 * @param sourceProduct the source product for the recommendations
	 * @param catalog the catalog where the recommendations exist
	 */
	protected void updateProductAssociations(
		final Product sourceProduct, 
		final RecommendationSet newRecommendations,
		final Catalog catalog) {

		addNewRecommendations(sourceProduct, newRecommendations, catalog);
		
		deleteObsoleteRecommendations(sourceProduct, newRecommendations, catalog);
	}
	
	/**
	 * Deletes all existing product recommendations that are not in the new set of product recommendations. 
	 * 
	 * @param sourceProduct the source product for the association
	 * @param newRecommendations the set of new recommendations
	 * @param catalog the catalog the associations exist for
	 */
	protected void deleteObsoleteRecommendations(final Product sourceProduct, 
			final RecommendationSet newRecommendations, final Catalog catalog) {
		final long startTime = System.currentTimeMillis();
		
		int deletedAssociations = getPersistenceEngine().executeNamedQueryWithList("DELETE_OBSOLETE_PRODUCTASSOCIATIONS", 
				"list", newRecommendations.getRecommendations(),
				sourceProduct.getUidPk(), catalog.getUidPk(), ProductAssociation.RECOMMENDATION);
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Number of recommendations deleted: " + deletedAssociations + " for source product(uidpk): " + sourceProduct.getUidPk() 
					+ " and catalog(code): " + catalog.getCode());
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Deleted obsolete recommendations(" + deletedAssociations + ") for(ms): " + (System.currentTimeMillis() - startTime));
		}
	}

	/**
	 * Adds product recommendations if they do not currently exist.
	 * @param newRecommendations the set of new recommendations
	 * @param sourceProduct the source product for the new recommendations
	 * @param catalog the catalog where the recommendations exist
	 */
	protected void addNewRecommendations(final Product sourceProduct, 
			final RecommendationSet newRecommendations, 
			final Catalog catalog) {
		
		final long startTime = System.currentTimeMillis();

		final List<Long> existingAssociationProductUids = getPersistenceEngine().retrieveByNamedQueryWithList(
				"FILTER_EXISTING_PRODUCTASSOCIATIONS_IN_CATALOG",
				"list",
				newRecommendations.getRecommendations(),
				sourceProduct.getUidPk(),
				catalog.getUidPk(),
				ProductAssociation.RECOMMENDATION);
		
		for (long recommendedProductUid : newRecommendations.getRecommendations()) {
			if (!existingAssociationProductUids.contains(recommendedProductUid)) {
				ProductAssociation productAssociation = createProductAssociation(sourceProduct, recommendedProductUid, catalog);
				getProductAssociationService().add(productAssociation);
				
				if (LOG.isDebugEnabled()) {
					LOG.debug("New recommendation set from source product uidpk: " + sourceProduct.getUidPk() 
							+ " to target product with uidpk: " + recommendedProductUid
							+ " in Catalog with code: " + catalog.getCode());
				}
			}
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Added new recommendations for(ms): " + (System.currentTimeMillis() - startTime));
		}
	}

	/**
	 * Creates a new ProductAssociation for a product recommendation.
	 *
	 * @param sourceProduct the source product for the new association
	 * @param recommendedProductUid the UID of the recommended product
	 * @param catalog the catalog where the recommendations exist
	 * @return the newly-created ProductAssociation
	 */
	protected ProductAssociation createProductAssociation(final Product sourceProduct, final long recommendedProductUid, final Catalog catalog) {
		
		final ProductAssociation productAssociation = getBean(ContextIdNames.PRODUCT_ASSOCIATION);
		productAssociation.setAssociationType(ProductAssociation.RECOMMENDATION);
		
		final Product recommendedTargetProduct = productService.getTuned(recommendedProductUid, fetchGroupLoadTuner);
		
		productAssociation.setSourceProduct(sourceProduct);
		productAssociation.setTargetProduct(recommendedTargetProduct);
		productAssociation.setCatalog(catalog);
		productAssociation.setStartDate(timeService.getCurrentTime());
		
		// the default qty is not relevant to recommendations
		productAssociation.setDefaultQuantity(1);
		productAssociation.setSourceProductDependent(false);
		
		return productAssociation;
	}

	/**
	 * This method creates and returns a Map where the key is a <code>Long</code> customer UID and the value is a <code>Set</code> of the
	 * products they have purchased, which are represented as <code>Long</code>product UIDs. This method is protected for access only by unit test
	 * code and should not be called by client code.
	 *
	 * @param purchaseData a <code>List</code> where each item is a <code>List</code> where the first item is a customerUid as a
	 *            <code>Long</code> and the second item is the id of a product they purchased as a <code>Long</code>
	 * @return the <code>Map</code>
	 */
	protected Map<Long, Set<Long>> createCustomerToPurchasedProductsMap(final List<Object[]> purchaseData) {
		Map<Long, Set<Long>> customerToProductMap = new HashMap<Long, Set<Long>>();

		for (Object[] purchaseDataRecord : purchaseData) {
			long customerUid = (Long) purchaseDataRecord[0];
			long productUid =  (Long) purchaseDataRecord[1];
			Set<Long> productSet = customerToProductMap.get(customerUid);
			if (productSet == null) {
				productSet = new HashSet<Long>();
				customerToProductMap.put(customerUid, productSet);
			}
			productSet.add(productUid);
		}

		return customerToProductMap;
	}

	/**
	 * Set the product service.
	 *
	 * @param productService the product service
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * Set the time service.
	 *
	 * @param timeService the time service
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	/***
	 * Set the ProductAssociationService.
	 * 
	 * @param productAssociationService the product association service
	 */
	public void setProductAssociationService(final ProductAssociationService productAssociationService) {
		this.productAssociationService = productAssociationService;
	}
	
	/**
	 * Set the SettingsReader.
	 * 
	 * @param settingsReader the settings reader service
	 */
	public void setSettingsReader(final SettingsReader settingsReader) {
		this.settingsReader = settingsReader;
	}
	
	/***
	 * Set the StoreService.
	 * 
	 * @param storeService the store service
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}
	
	/***
	 * @return the StoreService.
	 */
	protected StoreService getStoreService() {
		return this.storeService;
	}
	
	/**
	 * Gets the ProductAssociationService.
	 * @return the Product Association Service
	 */
	protected ProductAssociationService getProductAssociationService() {
		return this.productAssociationService;
	}
	
	/**
	 * Get the SettingsReader.
	 * @return the SettingsReader
	 */
	protected SettingsReader getSettingsReader() {
		return this.settingsReader;
	}

	/**
	 * There is no object that can be retrieved by this service.
	 *
	 * @param uid the Brand UID.
	 * @return null
	 * @throws EpServiceException - in case of any errors
	 */
	public Object getObject(final long uid) throws EpServiceException {
		throw new EpServiceException("This method is not used");
	}

	/**
	 * Represents a set of product recommendations. The number of times a product recommendation is made is recorded and this class provides access
	 * to a distinct collection of product recommendations, ordered by the number of occurrences, and limited by the <code>maxRecommendations</code>
	 * parameter passed to the constructor. The extra complexity of this implementation is intended to avoid the need to sort the entire
	 * recommendation set for performance reasons.
	 */
	protected static class RecommendationSet {

		/** Maps product ids to their occurrence count. */
		private final Map<Long, Integer> recommendationCountMap = new HashMap<Long, Integer>();

		/** The maximum number of recommendations for any given source product. */
		private final int maxRecommendations;

		/**
		 * Maintains a sorted list of the ids of top N most frequently recommended products where N = maxRecommendations.
		 */
		private final List<Long> topRecommendations;

		/** The lowest number of recommendations for products in the topRecommendations list. */
		private int minRecommendationCount;

		/**
		 * Constructor.
		 *
		 * @param maxRecommendations pass in the maximum number of recommendations. This is expected to be a small number, say < 10. to be returned
		 *            from this <code>RecommendationSet</code>
		 */
		public RecommendationSet(final int maxRecommendations) {
			if (maxRecommendations <= 0) {
				throw new EpServiceException("Max number of recommendations must be > 0");
			}
			this.maxRecommendations = maxRecommendations;
			this.topRecommendations = new ArrayList<Long>(maxRecommendations);
		}

		/**
		 * Add a product recommendation to the set.
		 *
		 * @param recommendedProductId the UID of the recommended product.
		 */
		public void addRecommendation(final long recommendedProductId) {
			int recommendationCount = updateRecommendationCount(recommendedProductId);
			if (recommendationCount >= this.minRecommendationCount) {
				addToTopRecommendations(recommendedProductId, recommendationCount);
			}
		}

		/**
		 * Adds a recommended product Id to the top recommendations list. The id will be added in order of recommendation count and if the list grows
		 * larger than maxRecommendations, it will be truncated. This will also update the minRecommendation
		 *
		 * @param recommendedProductId the id of the product to add to the top recommendations
		 * @param recommendationCount the number of times the product has been recommended
		 */
		private void addToTopRecommendations(final long recommendedProductId, final int recommendationCount) {

			topRecommendations.remove(recommendedProductId);

			// Add the recommendation
			int topRecSize = 0;
			if (topRecommendations.isEmpty()) {
				topRecommendations.add(recommendedProductId);
			} else {
				// Insert it in order of recommendation count
				boolean recommendationInserted = false;
				topRecSize = topRecommendations.size();
				for (int i = 0; i < topRecSize; i++) {
					int currProductRecommendationCount = recommendationCountMap.get(topRecommendations.get(i));
					if (recommendationCount > currProductRecommendationCount) {
						topRecommendations.add(i, recommendedProductId);
						topRecSize++;
						recommendationInserted = true;
						break;
					}
				}
				if (!recommendationInserted) {
					topRecommendations.add(recommendedProductId);
					topRecSize++;
				}
			}

			// truncate if needed
			topRecSize = topRecommendations.size();
			if (topRecSize > this.maxRecommendations) {
				topRecommendations.remove(topRecSize - 1);
				topRecSize--;
			}

			// Re-set the minRecommendationCount to the count for the last item in the list
			this.minRecommendationCount = recommendationCountMap.get(topRecommendations.get(topRecSize - 1));
		}

		/**
		 * Updates the map of recommended product ids to their recommendation count.
		 *
		 * @param recommendedProductId the ID of a new recommended product
		 */
		private int updateRecommendationCount(final long recommendedProductId) {
			Integer recommendationCount = recommendationCountMap.get(recommendedProductId);
			if (recommendationCount == null) {
				recommendationCount = 1;
			} else {
				recommendationCount = recommendationCount + 1;
			}
			recommendationCountMap.put(recommendedProductId, recommendationCount);
			return recommendationCount;
		}

		/**
		 * Get the list of recommended products.
		 *
		 * @return a <code>List</code> of <code>Long</code>s
		 */
		public List<Long> getRecommendations() {
			return topRecommendations;
		}
		
		/**
		 * Determines whether this collection of recommendations contains the given target product uidpk.
		 * @param targetProductUidPk the UidPk of the product to search for
		 * @return true if this collection of recommendations contains a product with the given uidPk, false if not.
		 */
		public boolean contains(final long targetProductUidPk) {
			return topRecommendations.contains(targetProductUidPk);
		}

		/**
		 * Returns a string representation of the top recommendations (a list of their ids) for testing.
		 *
		 * @return a list of ids as a <code>String</code>
		 */
		@Override
		public String toString() {
			StringBuffer out = new StringBuffer();
			out.append("( ");
			for (Long currLong : this.topRecommendations) {
				out.append(currLong.toString() + " "); // NOPMD
			}
			out.append(')');
			return out.toString();
		}
	}

}
