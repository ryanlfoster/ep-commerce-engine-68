package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.InventoryDetails;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.impl.InventoryDtoImpl;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Tests the {@code InventoryCalculatorImpl}.
 */
public class InventoryCalculatorImplTest {
	private static final String PRODUCT_CONSTITUENT = "productConstituent";
	private static final String SKU_CHILD2 = "skuChild2";
	private static final String SKU_CHILD1 = "skuChild1";
	private static final String SKUCODE = "SKUCODE";
	private static final int THREE = 3;
	private static final int SEVEN = 7;
	private static final int EIGHT = 8;
	private static final int NINE = 9;
	private static final int TEN = 10;
	private static final long WAREHOUSE_UID = 1L;

	private InventoryCalculatorImpl inventoryCalculator;
	private ProductInventoryManagementService productInventoryManagementService;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private BeanFactoryExpectationsFactory expectationsFactory;

	/** */
	@Before
	public void setUp() {
		productInventoryManagementService = context.mock(ProductInventoryManagementService.class);
		inventoryCalculator = new InventoryCalculatorImpl() {
			@Override
			protected InventoryDetails createInventoryDetails() {
				return new InventoryDetails();
			}
		};
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests that the available quantity can be retrieved for a simple product.
	 */
	@Test
	public void testGetAvailableQuantityInStockProduct() {

		final InventoryDtoImpl inventory = new InventoryDtoImpl();
		inventory.setQuantityOnHand(TEN);

		final Map<String, InventoryDto> inventoryMap = new HashMap<String, InventoryDto>();
		inventoryMap.put(SKUCODE, inventory);

		Product product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);

		ProductSkuImpl productSku = new ProductSkuImpl();
		productSku.setSkuCode(SKUCODE);

		productSku.setProduct(product);

		context.checking(new Expectations() {
			{
				oneOf(productInventoryManagementService).getInventoriesForSkusInWarehouse(inventoryMap.keySet(), WAREHOUSE_UID);
				will(returnValue(inventoryMap));
			}
		});
		InventoryDetails inventoryDetails = inventoryCalculator.getInventoryDetails(
				productInventoryManagementService, productSku, WAREHOUSE_UID);
		assertEquals(TEN, inventoryDetails.getAvailableQuantityInStock());
	}

	/**
	 * Tests that the in stock quantity for a bundle is based on the in stock quantity of the single constituent.
	 */
	@Test
	public void testGetAvailableQuantityInStockBundleOne() {

		expectationsFactory.allowingBeanFactoryGetBean(PRODUCT_CONSTITUENT, ProductConstituentImpl.class);

		ProductSkuImpl productSkuRoot = new ProductSkuImpl();

		ProductSkuImpl productSkuChild1 = new ProductSkuImpl();
		productSkuChild1.setSkuCode("CHILDSKU1");
		final InventoryDtoImpl inventoryChild1 = new InventoryDtoImpl();
		inventoryChild1.setQuantityOnHand(NINE);
		final Map<String, InventoryDto> inventoryMapChild1 = new HashMap<String, InventoryDto>();
		inventoryMapChild1.put("CHILDSKU1", inventoryChild1);

		ProductBundle bundle = new ProductBundleImpl();
		bundle.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setDefaultSku(productSkuChild1);
		child1.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		const1.setConstituent(child1);
		const1.setQuantity(1);
		bundle.addConstituent(const1);

		productSkuRoot.setProduct(bundle);

		context.checking(new Expectations() {
			{
				oneOf(productInventoryManagementService).getInventoriesForSkusInWarehouse(inventoryMapChild1.keySet(), WAREHOUSE_UID);
				will(returnValue(inventoryMapChild1));
			}
		});

		InventoryDetails inventoryDetails = inventoryCalculator.getInventoryDetails(
				productInventoryManagementService, productSkuRoot, WAREHOUSE_UID);
		assertEquals(NINE, inventoryDetails.getAvailableQuantityInStock());
	}

	/**
	 * Tests that the in stock quantity for a bundle is based on the in stock quantity of many constituents.
	 */
	@Test
	public void testGetAvailableQuantityInStockBundleMany() {

		expectationsFactory.allowingBeanFactoryGetBean(PRODUCT_CONSTITUENT, ProductConstituentImpl.class);

		ProductSkuImpl productSkuRoot = new ProductSkuImpl();
		productSkuRoot.setGuid("skuRoot");

		ProductBundle bundle = new ProductBundleImpl();
		bundle.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);

		// The root actually has inventory but we do not want to see it returned.
		final InventoryDtoImpl inventoryDto = new InventoryDtoImpl();
		inventoryDto.setQuantityOnHand(TEN);

		final Map<String, InventoryDto> inventoryMap = new HashMap<String, InventoryDto>();

		final InventoryDtoImpl inventoryChild1 = new InventoryDtoImpl();
		inventoryChild1.setQuantityOnHand(NINE);

		ProductSku productSkuChild1 = new ProductSkuImpl();
		productSkuChild1.setSkuCode(SKU_CHILD1);
		inventoryMap.put(SKU_CHILD1, inventoryChild1);

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		child1.setDefaultSku(productSkuChild1);
		const1.setConstituent(child1);
		const1.setQuantity(1);
		bundle.addConstituent(const1);

		final InventoryDtoImpl inventoryChild2 = new InventoryDtoImpl();
		inventoryChild2.setQuantityOnHand(EIGHT);

		ProductSku productSkuChild2 = new ProductSkuImpl();
		productSkuChild2.setSkuCode(SKU_CHILD2);
		inventoryMap.put(SKU_CHILD2, inventoryChild2);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		child2.setDefaultSku(productSkuChild2);
		const2.setConstituent(child2);
		const2.setQuantity(1);
		bundle.addConstituent(const2);

		productSkuRoot.setProduct(bundle);

		context.checking(new Expectations() {
			{
				oneOf(productInventoryManagementService).getInventoriesForSkusInWarehouse(inventoryMap.keySet(), WAREHOUSE_UID);
				will(returnValue(inventoryMap));
			}
		});

		InventoryDetails inventoryDetails = inventoryCalculator.getInventoryDetails(
				productInventoryManagementService, productSkuRoot, WAREHOUSE_UID);
		assertEquals(EIGHT, inventoryDetails.getAvailableQuantityInStock());
	}

	/**
	 * Tests that the in stock quantity for a bundle is based on the in stock quantity of nested constituents.
	 */
	@Test
	public void testGetAvailableQuantityInStockBundleDeep() {

		expectationsFactory.allowingBeanFactoryGetBean(PRODUCT_CONSTITUENT, ProductConstituentImpl.class);

		ProductSkuImpl productSkuRoot = new ProductSkuImpl();

		ProductBundle bundle = new ProductBundleImpl();
		bundle.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);


		ProductSkuImpl productSkuChild1 = new ProductSkuImpl();
		productSkuChild1.setSkuCode(SKU_CHILD1);

		BundleConstituent const1 = new BundleConstituentImpl();
		ProductBundle child1 = new ProductBundleImpl();
		child1.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		child1.setDefaultSku(productSkuChild1);
		const1.setConstituent(child1);
		const1.setQuantity(1);
		bundle.addConstituent(const1);

		final InventoryDtoImpl inventoryChild2 = new InventoryDtoImpl();
		inventoryChild2.setQuantityOnHand(SEVEN);

		ProductSkuImpl productSkuChild2 = new ProductSkuImpl();
		productSkuChild2.setSkuCode(SKU_CHILD2);

		final Map<String, InventoryDto> inventoryMapChild2 = new HashMap<String, InventoryDto>();
		inventoryMapChild2.put(SKU_CHILD2, inventoryChild2);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		child2.setDefaultSku(productSkuChild2);
		const2.setConstituent(child2);
		const2.setQuantity(1);
		child1.addConstituent(const2);

		productSkuRoot.setProduct(bundle);

		context.checking(new Expectations() {
			{
				oneOf(productInventoryManagementService).getInventoriesForSkusInWarehouse(inventoryMapChild2.keySet(), WAREHOUSE_UID);
				will(returnValue(inventoryMapChild2));
			}
		});

		InventoryDetails inventoryDetails = inventoryCalculator.getInventoryDetails(
				productInventoryManagementService, productSkuRoot, WAREHOUSE_UID);
		assertEquals(SEVEN, inventoryDetails.getAvailableQuantityInStock());
	}

	/**
	 * Tests that the in stock quantity for a bundle is based on the in stock quantity of nested constituents.
	 * Tests the case where two constituents have the same product sku.
	 */
	@Test
	public void testGetAvailableQuantityInStockTwoOfSameSku() {

		expectationsFactory.allowingBeanFactoryGetBean(PRODUCT_CONSTITUENT, ProductConstituentImpl.class);

		ProductSkuImpl productSkuRoot = new ProductSkuImpl();
		productSkuRoot.setSkuCode("skuRoot");

		ProductBundle bundle = new ProductBundleImpl();
		bundle.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);

		final InventoryDtoImpl inventoryChild1 = new InventoryDtoImpl();
		inventoryChild1.setQuantityOnHand(1);

		ProductSkuImpl productSkuChild1 = new ProductSkuImpl();
		productSkuChild1.setSkuCode(SKU_CHILD1);

		final Map<String, InventoryDto> inventoryMapChild1 = new HashMap<String, InventoryDto>();
		inventoryMapChild1.put(SKU_CHILD1, inventoryChild1);

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		child1.setDefaultSku(productSkuChild1);
		const1.setConstituent(child1);
		const1.setQuantity(1);
		bundle.addConstituent(const1);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		child2.setDefaultSku(productSkuChild1);
		const2.setConstituent(child2);
		const2.setQuantity(1);
		bundle.addConstituent(const2);

		productSkuRoot.setProduct(bundle);

		context.checking(new Expectations() {
			{
				oneOf(productInventoryManagementService).getInventoriesForSkusInWarehouse(inventoryMapChild1.keySet(), WAREHOUSE_UID);
				will(returnValue(inventoryMapChild1));
			}
		});

		InventoryDetails inventoryDetails = inventoryCalculator.getInventoryDetails(
				productInventoryManagementService, productSkuRoot, WAREHOUSE_UID);
		assertEquals("Only one of the sku but two skus in the bundle", 0, inventoryDetails.getAvailableQuantityInStock());
	}

	/**
	 * Tests in stock quantity when a bundle has a bundle quantity > 1.
	 */
	@Test
	public void testGetAvailableQuantityBundleQuantity() {

		expectationsFactory.allowingBeanFactoryGetBean(PRODUCT_CONSTITUENT, ProductConstituentImpl.class);

		ProductSkuImpl productSkuRoot = new ProductSkuImpl();
		productSkuRoot.setSkuCode("skuRoot");

		ProductBundle bundle = new ProductBundleImpl();
		bundle.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);

		final InventoryDtoImpl inventoryChild1 = new InventoryDtoImpl();
		inventoryChild1.setQuantityOnHand(NINE);

		ProductSku productSkuChild1 = new ProductSkuImpl();
		productSkuChild1.setSkuCode(SKU_CHILD1);

		final Map<String, InventoryDto> inventoryMapChild1 = new HashMap<String, InventoryDto>();
		inventoryMapChild1.put(SKU_CHILD1, inventoryChild1);

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setDefaultSku(productSkuChild1);
		child1.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		const1.setConstituent(child1);
		const1.setQuantity(THREE);
		bundle.addConstituent(const1);

		productSkuRoot.setProduct(bundle);

		context.checking(new Expectations() {
			{
				oneOf(productInventoryManagementService).getInventoriesForSkusInWarehouse(inventoryMapChild1.keySet(), WAREHOUSE_UID);
				will(returnValue(inventoryMapChild1));
			}
		});

		InventoryDetails inventoryDetails = inventoryCalculator.getInventoryDetails(
				productInventoryManagementService, productSkuRoot, WAREHOUSE_UID);
		assertEquals(THREE, inventoryDetails.getAvailableQuantityInStock());
	}

	/**
	 * Tests that the in stock quantity for a bundle is based on the in stock quantity of nested constituents.
	 * Tests a nested bundle with a quantity that needs to be multiplied down the tree.
	 *
	 */
	@Test
	public void testGetAvailableQuantityInStockBundleQuantityDeep() {

		expectationsFactory.allowingBeanFactoryGetBean(PRODUCT_CONSTITUENT, ProductConstituentImpl.class);

		ProductSkuImpl productSkuRoot = new ProductSkuImpl();

		ProductBundle bundle = new ProductBundleImpl();
		bundle.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);

		ProductSkuImpl productSkuChild1 = new ProductSkuImpl();
		productSkuChild1.setSkuCode(SKU_CHILD1);

		BundleConstituent const1 = new BundleConstituentImpl();
		ProductBundle child1 = new ProductBundleImpl();
		child1.setDefaultSku(productSkuChild1);
		child1.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		const1.setConstituent(child1);
		const1.setQuantity(THREE);
		bundle.addConstituent(const1);

		final InventoryDtoImpl inventoryChild2 = new InventoryDtoImpl();
		inventoryChild2.setQuantityOnHand(SEVEN);

		ProductSkuImpl productSkuChild2 = new ProductSkuImpl();
		productSkuChild2.setSkuCode(SKU_CHILD2);

		final Map<String, InventoryDto> inventoryMapChild2 = new HashMap<String, InventoryDto>();
		inventoryMapChild2.put(SKU_CHILD2, inventoryChild2);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setDefaultSku(productSkuChild2);
		child2.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		const2.setConstituent(child2);
		const2.setQuantity(2);
		child1.addConstituent(const2);

		productSkuRoot.setProduct(bundle);

		context.checking(new Expectations() {
			{
				oneOf(productInventoryManagementService).getInventoriesForSkusInWarehouse(inventoryMapChild2.keySet(), WAREHOUSE_UID);
				will(returnValue(inventoryMapChild2));
			}
		});

		InventoryDetails inventoryDetails = inventoryCalculator.getInventoryDetails(
				productInventoryManagementService, productSkuRoot, WAREHOUSE_UID);
		assertEquals("The leaf node requires 6 but there is only 7 in stock", 1, inventoryDetails.getAvailableQuantityInStock());
	}

	/**
	 * Tests that the in stock quantity for a bundle is based on the in stock quantity of nested constituents but that
	 * always available constituents do not affect the inventory calculation.
	 */
	@Test
	public void testGetAvailableQuantityNestedConstituentAlwaysAvailable() {

		// Setup is one bundle with two direct children. One with available if in stock and the other is always available.
		expectationsFactory.allowingBeanFactoryGetBean(PRODUCT_CONSTITUENT, ProductConstituentImpl.class);

		ProductSkuImpl productSkuRoot = new ProductSkuImpl();

		ProductBundle bundle = new ProductBundleImpl();
		bundle.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);

		final InventoryDtoImpl inventoryChild1 = new InventoryDtoImpl();
		inventoryChild1.setQuantityOnHand(NINE);

		ProductSkuImpl productSkuChild1 = new ProductSkuImpl();
		productSkuChild1.setSkuCode(SKU_CHILD1);

		final Map<String, InventoryDto> inventoryMapChild1 = new HashMap<String, InventoryDto>();
		inventoryMapChild1.put(SKU_CHILD1, inventoryChild1);

		BundleConstituent const1 = new BundleConstituentImpl();
		Product child1 = new ProductImpl();
		child1.setDefaultSku(productSkuChild1);
		child1.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		const1.setConstituent(child1);
		const1.setQuantity(1);
		bundle.addConstituent(const1);

		// Child2 has no inventory.

		ProductSkuImpl productSkuChild2 = new ProductSkuImpl();
		productSkuChild2.setSkuCode(SKU_CHILD2);

		BundleConstituent const2 = new BundleConstituentImpl();
		Product child2 = new ProductImpl();
		child2.setDefaultSku(productSkuChild2);
		child2.setAvailabilityCriteria(AvailabilityCriteria.ALWAYS_AVAILABLE);
		const2.setConstituent(child2);
		const2.setQuantity(1);
		bundle.addConstituent(const2);

		productSkuRoot.setProduct(bundle);

		context.checking(new Expectations() {
			{
				oneOf(productInventoryManagementService).getInventoriesForSkusInWarehouse(inventoryMapChild1.keySet(), WAREHOUSE_UID);
				will(returnValue(inventoryMapChild1));
			}
		});

		InventoryDetails inventoryDetails = inventoryCalculator.getInventoryDetails(
				productInventoryManagementService, productSkuRoot, WAREHOUSE_UID);
		assertEquals(NINE, inventoryDetails.getAvailableQuantityInStock());
	}
}
