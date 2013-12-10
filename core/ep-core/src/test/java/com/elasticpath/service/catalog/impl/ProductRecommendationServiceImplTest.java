/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.service.catalog.impl.ProductRecommendationServiceImpl.RecommendationSet;

/**
 * Test cases for the <code>ProductRecommendationService</code>.
 */
public class ProductRecommendationServiceImplTest {

	private static final int NUM_RECOMMENDATIONS_FOR_PROD6 = 4;

	private static final int NUM_RECOMMENDATIONS_FOR_PROD1 = 3;

	private static final int NUM_ITEMS_BOUGHT_BY_CUST2 = 3;

	private static final int NUM_ITEMS_BOUGHT_BY_CUST1 = 4;

	private static final Long PRODUCT7 = new Long(1006L);

	private static final Long PRODUCT6 = new Long(1005L);

	private static final Long PRODUCT5 = new Long(1004L);

	private static final Long PRODUCT4 = new Long(1003L);

	private static final Long PRODUCT3 = new Long(1002L);

	private static final Long PRODUCT2 = new Long(1001L);

	private static final Long PRODUCT1 = new Long(1000L);

	private static final Long CUST4 = new Long(103L);

	private static final Long CUST3 = new Long(102L);

	private static final Long CUST2 = new Long(101L);

	private static final Long CUST1 = new Long(100L);

	private ProductRecommendationServiceImpl productRecommendationService;

	private RecommendationSet recommendationSet;
	
	private static final int MAX_HISTORY_DAYS = 4;
	
	private static final int MAX_RECOMMENDATIONS = 4;
	
	private static final String TEST_STORE_CODE = "TestStoreCode";
	
	/**
	 * Prepare for the tests.
	 * 
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		productRecommendationService = new ProductRecommendationServiceImpl() {
			@Override
			int getMaxOrderHistoryDays(final String storeCode) {
				return MAX_HISTORY_DAYS;
			}
			@Override
			int getMaxRecommendations(final String storeCode) {
				return MAX_RECOMMENDATIONS;
			}
		};
	}

	/**
	 * Test the recommendation Set.
	 */
	@Test
	public void testRecommendationSet() {
		recommendationSet = new RecommendationSet(MAX_RECOMMENDATIONS);
		recommendationSet.addRecommendation(PRODUCT3);
		recommendationSet.addRecommendation(PRODUCT4);
		recommendationSet.addRecommendation(PRODUCT5);
		recommendationSet.addRecommendation(PRODUCT5);
		recommendationSet.addRecommendation(PRODUCT5);
		recommendationSet.addRecommendation(PRODUCT5);
		recommendationSet.addRecommendation(PRODUCT4);
		recommendationSet.addRecommendation(PRODUCT1);
		recommendationSet.addRecommendation(PRODUCT7);

		List<Long> recommendations = recommendationSet.getRecommendations();
		assertEquals(MAX_RECOMMENDATIONS, recommendations.size());
		Iterator<Long> recommendationIter = recommendations.iterator();
		assertEquals(PRODUCT5, recommendationIter.next());
		assertEquals(PRODUCT4, recommendationIter.next());
		Long thirdRecommendation = recommendationIter.next();
		assertTrue(thirdRecommendation.equals(PRODUCT1) || thirdRecommendation.equals(PRODUCT7) || thirdRecommendation.equals(PRODUCT3));

	}

	/**
	 * Test case for the intermediate step of computing the customer to products purchased map.
	 */
	@Test
	public void testCustomerToPurchasedProductsMap() {
		Map<Long, Set<Long>> customerToProductsPurchasedMap = 
			productRecommendationService.createCustomerToPurchasedProductsMap(getCustomerPurchaseData());

		Set<Long> cust1Set = customerToProductsPurchasedMap.get(CUST1);
		assertEquals(NUM_ITEMS_BOUGHT_BY_CUST1, cust1Set.size());
		assertTrue(cust1Set.contains(PRODUCT1));
		assertTrue(cust1Set.contains(PRODUCT2));
		assertTrue(cust1Set.contains(PRODUCT3));
		assertTrue(cust1Set.contains(PRODUCT6));

		Set<Long> cust2Set = customerToProductsPurchasedMap.get(CUST2);
		assertEquals(NUM_ITEMS_BOUGHT_BY_CUST2, cust2Set.size());
		assertTrue(cust2Set.contains(PRODUCT3));
		assertTrue(cust2Set.contains(PRODUCT4));
		assertTrue(cust2Set.contains(PRODUCT5));

		Set<Long> cust4Set = customerToProductsPurchasedMap.get(CUST4);
		assertEquals(1, cust4Set.size());
		assertTrue(cust4Set.contains(PRODUCT7));

	}

	/**
	 * Test that the product recommendations are being correctly computed.
	 */
	@Test
	public void testProductRecommendationCalculation() {	
		// Create and set the record of customer purchases
		Map<Long, Set<Long>> customerToProductsPurchasedMap = 
			productRecommendationService.createCustomerToPurchasedProductsMap(getCustomerPurchaseData());
		// Compute the product recommendations resulting from the record of customer purchases
		Map<Long, RecommendationSet> productRecommendationMap = 
			productRecommendationService.createProductToRecommendationsMap(customerToProductsPurchasedMap, TEST_STORE_CODE);
		
		RecommendationSet recommendationSet = productRecommendationMap.get(PRODUCT1);
		List<Long> recommendedProducts = recommendationSet.getRecommendations();
		assertEquals(NUM_RECOMMENDATIONS_FOR_PROD1, recommendedProducts.size());
		assertTrue(recommendedProducts.contains(PRODUCT2));
		assertTrue(recommendedProducts.contains(PRODUCT3));
		assertTrue(recommendedProducts.contains(PRODUCT6));

		recommendationSet = productRecommendationMap.get(PRODUCT3);
		recommendedProducts = recommendationSet.getRecommendations();
		assertEquals(MAX_RECOMMENDATIONS, recommendedProducts.size());
		assertTrue(recommendedProducts.contains(PRODUCT1));
		assertTrue(recommendedProducts.contains(PRODUCT2));
		assertTrue(recommendedProducts.contains(PRODUCT6));
		assertTrue(recommendedProducts.contains(PRODUCT5) || recommendedProducts.contains(PRODUCT4));

		recommendationSet = productRecommendationMap.get(PRODUCT6);
		recommendedProducts = recommendationSet.getRecommendations();
		assertEquals(NUM_RECOMMENDATIONS_FOR_PROD6, recommendedProducts.size());
		assertTrue(recommendedProducts.contains(PRODUCT1));
		assertTrue(recommendedProducts.contains(PRODUCT2));
		assertTrue(recommendedProducts.contains(PRODUCT3));
		assertTrue(recommendedProducts.contains(PRODUCT5));

		recommendationSet = productRecommendationMap.get(PRODUCT7);
		recommendedProducts = recommendationSet.getRecommendations();
		assertEquals(0, recommendedProducts.size());
	}

	private List<Object[]> getCustomerPurchaseData() {
		List<Object[]> purchaseData = new ArrayList<Object[]>();

		// Expected recommendations (not ordered)
		// Product id => Recommended products
		// 1 => 2, 3, 6. (3 should only appear once, 1 should not appear)
		// 2 => 1, 3, 6.
		// 3 => 1, 2, 6, 4, 5 order: (2, 1, or 6) followed by (4 or 5)
		// 4 => 3, 5
		// 5 => 3, 4, 6
		// 6 => 1, 2, 3, 5
		// 7 => none

		purchaseData.add(createPurchaseRecord(CUST1, PRODUCT1));
		purchaseData.add(createPurchaseRecord(CUST1, PRODUCT2));
		purchaseData.add(createPurchaseRecord(CUST1, PRODUCT3));
		purchaseData.add(createPurchaseRecord(CUST1, PRODUCT3));
		purchaseData.add(createPurchaseRecord(CUST1, PRODUCT6));
		
		purchaseData.add(createPurchaseRecord(CUST2, PRODUCT3));
		purchaseData.add(createPurchaseRecord(CUST2, PRODUCT4));
		purchaseData.add(createPurchaseRecord(CUST2, PRODUCT5));
		
		purchaseData.add(createPurchaseRecord(CUST3, PRODUCT5));
		purchaseData.add(createPurchaseRecord(CUST3, PRODUCT6));
		
		purchaseData.add(createPurchaseRecord(CUST4, PRODUCT7));

		return purchaseData;
	}

	private Long[] createPurchaseRecord(final Long customerId, final Long productId) {
		Long[] purchaseRecord = new Long[2];
		purchaseRecord[0] = customerId;
		purchaseRecord[1] = productId;
		return purchaseRecord;
	}

}

