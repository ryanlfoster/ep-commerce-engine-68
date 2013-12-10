package com.elasticpath.test.integration.topseller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.TopSeller;
import com.elasticpath.domain.catalog.TopSellerProduct;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.TopSellerService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.OrderTestPersister;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.TestDataPersisterFactory;

/**
 * Test that the Top Seller service behaves as expected.
 */
public class TopSellerServiceImplTest extends DbTestCase {

	private static final String BRAND_CODE = "MYBRAND";
	@Autowired private OrderService orderService;
	@Autowired private TopSellerService topSellerService;
	@Autowired private ProductService productService;
	private CatalogTestPersister catalogTestPersister;
	private OrderTestPersister orderTestPersister;
	private TaxTestPersister taxTestPersister;
	private TaxCode taxCode;

	/**
	 * Set up required for each test.
	 *
	 * @throws Exception if an exception occurs
	 */
	@Before
	public void setUp() throws Exception {
		TestDataPersisterFactory persisterFactory = getTac().getPersistersFactory();
		orderTestPersister = persisterFactory.getOrderTestPersister();
		catalogTestPersister = persisterFactory.getCatalogTestPersister();
		taxTestPersister = persisterFactory.getTaxTestPersister();
		catalogTestPersister.persistBrand(BRAND_CODE, scenario.getCatalog());
		taxCode = taxTestPersister.getTaxCode(TaxTestPersister.TAX_CODE_GOODS);
	}

	/**
	 * Test update top sellers.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateTopSellers() {
		Product product1 = catalogTestPersister.persistProductWithSku(scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse(),
				BigDecimal.TEN, scenario.getStore().getDefaultCurrency(), BRAND_CODE, "product1", "Product 1", "Product1Sku1", 
				taxCode.getCode(), AvailabilityCriteria.ALWAYS_AVAILABLE, Integer.MAX_VALUE);
		orderTestPersister.createOrderWithSkusQuantity(scenario.getStore(), 10, product1.getDefaultSku());
		
		topSellerService.updateTopSellers();
		
		Product loadedProduct = productService.get(product1.getUidPk());
		assertEquals("The product sales count should have been updated", 10, loadedProduct.getSalesCount());

		TopSeller topSellerCategory = topSellerService.findTopSellerByCategoryUid(scenario.getCategory().getUidPk());
		assertNotNull("There should be a top seller for the category", topSellerCategory);
		
		assertEquals("There should be 1 top seller product uid" , 1, topSellerCategory.getProductUids().size());
		assertEquals("There should be 1 top seller product", 1, topSellerCategory.getTopSellerProducts().size());
		
		Long productUid = topSellerCategory.getProductUids().iterator().next();
		assertEquals("The product uid should match the sold product", product1.getUidPk(), productUid.longValue());
		
		TopSellerProduct topSellerProduct = topSellerCategory.getTopSellerProducts().get(productUid);
		assertEquals("The sales count should match", 10, topSellerProduct.getSalesCount());
		
		TopSeller topSellerStore = topSellerService.findTopSellerByCategoryUid(0L);
		assertNotNull("There should be a top seller for the store", topSellerStore);
		
		assertEquals("There should be 1 top seller product uid" , 1, topSellerStore.getProductUids().size());
		assertEquals("There should be 1 top seller product", 1, topSellerStore.getTopSellerProducts().size());
		
		productUid = topSellerStore.getProductUids().iterator().next();
		assertEquals("The product uid should match the sold product", product1.getUidPk(), productUid.longValue());
		
		topSellerProduct = topSellerStore.getTopSellerProducts().get(productUid);
		assertEquals("The sales count should match", 10, topSellerProduct.getSalesCount());
		
	}
	
	/**
	 * Test update top sellers with failed order.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateTopSellersWithFailedOrder() {
		Product product1 = catalogTestPersister.persistProductWithSku(scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse(),
				BigDecimal.TEN, scenario.getStore().getDefaultCurrency(), BRAND_CODE, "product1", "Product 1", "Product1Sku1", 
				taxCode.getCode(), AvailabilityCriteria.ALWAYS_AVAILABLE, Integer.MAX_VALUE);
		Order order = orderTestPersister.createOrderWithSkusQuantity(scenario.getStore(), 10, product1.getDefaultSku());
		order.failOrder();
		orderService.update(order);
		
		topSellerService.updateTopSellers();
		
		Product loadedProduct = productService.get(product1.getUidPk());
		assertEquals("The product sales count should not have been updated", 0, loadedProduct.getSalesCount());
		
		TopSeller topSellerCategory = topSellerService.findTopSellerByCategoryUid(scenario.getCategory().getUidPk());
		assertNull("There should be no top seller for the category", topSellerCategory);

		TopSeller topSellerStore = topSellerService.findTopSellerByCategoryUid(0L);
		assertNull("There should not be a top seller for the store", topSellerStore);
	}
}