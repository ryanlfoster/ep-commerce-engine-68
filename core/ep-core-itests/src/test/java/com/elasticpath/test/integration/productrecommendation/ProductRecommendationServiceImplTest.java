package com.elasticpath.test.integration.productrecommendation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.catalog.ProductRecommendationService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.OrderTestPersister;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.TestDataPersisterFactory;

/**
 * Test that ProductRecommendationServiceImpl behaves as expected.
 */
public class ProductRecommendationServiceImplTest extends DbTestCase {
	
	private static final String BRAND_CODE = "MYBRAND";
	
	@Autowired private OrderService orderService;
	private OrderTestPersister orderTestPersister;
	private CatalogTestPersister catalogTestPersister;
	private TaxTestPersister taxTestPersister;
	
	@Autowired private ProductRecommendationService productRecommendationService;
	@Autowired private ProductAssociationService productAssociationService;
	
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
	 * Test update recommendations.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateRecommendations() {
		Product product1 = catalogTestPersister.persistProductWithSku(scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse(),
				BigDecimal.TEN, scenario.getStore().getDefaultCurrency(), BRAND_CODE, "product1", "Product 1", "Product1Sku1", 
				taxCode.getCode(), AvailabilityCriteria.ALWAYS_AVAILABLE, Integer.MAX_VALUE);

		Product product2 = catalogTestPersister.persistProductWithSku(scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse(),
				BigDecimal.TEN, scenario.getStore().getDefaultCurrency(), BRAND_CODE, "product2", "Product 2", "Product2Sku1", 
				taxCode.getCode(), AvailabilityCriteria.ALWAYS_AVAILABLE, Integer.MAX_VALUE);
		
		orderTestPersister.createOrderWithSkusQuantity(scenario.getStore(), 1, product1.getDefaultSku(), product2.getDefaultSku());
		
		productRecommendationService.updateRecommendations();
		
		ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
		criteria.setAssociationType(ProductAssociation.RECOMMENDATION);
		List<ProductAssociation> associations = productAssociationService.findByCriteria(criteria);
		
		assertEquals("There should be 2 associations", 2, associations.size());
		ProductAssociation productAssociation1 = associations.get(0);
		ProductAssociation productAssociation2 = associations.get(1);
		
		assertEquals("The first association source should be the target of the second", 
				productAssociation1.getSourceProduct(), productAssociation2.getTargetProduct());
		assertEquals("The first association target should be the source of the second", 
				productAssociation1.getTargetProduct(), productAssociation2.getSourceProduct());

		assertTrue("One of the associations source product should be product 1", product1.equals(productAssociation1.getSourceProduct())
				|| product1.equals(productAssociation2.getSourceProduct()));
		assertTrue("One of the associations source product should be product 2", product2.equals(productAssociation1.getSourceProduct())
				|| product2.equals(productAssociation2.getSourceProduct()));
		
	}

	/**
	 * Test update recommendations with failed orders.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateRecommendationsWithFailedOrders() {
		Product product1 = catalogTestPersister.persistProductWithSku(scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse(),
				BigDecimal.TEN, scenario.getStore().getDefaultCurrency(), BRAND_CODE, "product1", "Product 1", "Product1Sku1", 
				taxCode.getCode(), AvailabilityCriteria.ALWAYS_AVAILABLE, Integer.MAX_VALUE);
		
		Product product2 = catalogTestPersister.persistProductWithSku(scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse(),
				BigDecimal.TEN, scenario.getStore().getDefaultCurrency(), BRAND_CODE, "product2", "Product 2", "Product2Sku1", 
				taxCode.getCode(), AvailabilityCriteria.ALWAYS_AVAILABLE, Integer.MAX_VALUE);
		
		Order order = orderTestPersister.createOrderWithSkusQuantity(scenario.getStore(), 1, product1.getDefaultSku(), product2.getDefaultSku());
		order.failOrder();
		orderService.update(order);

		productRecommendationService.updateRecommendations();
		
		ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
		criteria.setAssociationType(ProductAssociation.RECOMMENDATION);
		List<ProductAssociation> associations = productAssociationService.findByCriteria(criteria);
		
		assertEquals("There should be no associations", 0, associations.size());
	}
}