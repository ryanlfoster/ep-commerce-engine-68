package com.elasticpath.sellingchannel.inventory.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductBundleImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.dao.InventoryDao;
import com.elasticpath.inventory.dao.InventoryJournalDao;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.domain.impl.InventoryImpl;
import com.elasticpath.inventory.impl.InventoryFacadeImpl;
import com.elasticpath.inventory.strategy.InventoryStrategy;
import com.elasticpath.inventory.strategy.impl.InventoryJournalRollupImpl;
import com.elasticpath.inventory.strategy.impl.JournalingInventoryStrategy;
import com.elasticpath.sellingchannel.inventory.impl.ProductInventoryShoppingServiceImpl.NodeInventory;
import com.elasticpath.service.catalog.impl.ProductInventoryManagementServiceImpl;
import com.elasticpath.service.catalogview.impl.InventoryMessage;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Unit test for the {@code ProductInventoryShoppingServiceImplTest}.
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals" })
public class ProductInventoryShoppingServiceImplTest {

	private static final int THREE = 3;
	private static final int FOUR = 4;
	private static final int WAREHOUSE_UIDPK = 1234;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Set up required before each test.
	 */
	@Before
	public void setUp() {
	    beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test happy path for message code.
	 */
	@Test
	public void testSkuSelection() {
		ShoppingItemDto rootDto = new ShoppingItemDto("skuRoot", 1);
		ShoppingItemDto child1Dto = new ShoppingItemDto("childSkuB", 1);

		rootDto.addConstituent(child1Dto);

		// Setup a bundle with just one constituent.
		// That constituent has two skus and childSkuA is the default

		ProductSku productSku = new ProductSkuImpl();
		productSku.setGuid("ABC");
		ProductBundle rootBundle = new ProductBundleImpl();
		productSku.setProduct(rootBundle);
		final Inventory inventory = new InventoryImpl();
		inventory.setWarehouseUid(new Long(WAREHOUSE_UIDPK));
		inventory.setQuantityOnHand(1);
		BundleConstituent child1Const = new BundleConstituentImpl();
		Product child1Product = new ProductImpl();
		child1Product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		child1Const.setConstituent(child1Product);
		child1Const.setQuantity(1);
		rootBundle.addConstituent(child1Const);

		Map<String, ProductSku> productSkus = new HashMap<String, ProductSku>();
		ProductSku childSkuA = new ProductSkuImpl();
		childSkuA.setGuid("childSkuA");
		childSkuA.setProduct(child1Product);
		ProductSku childSkuB = new ProductSkuImpl();
		childSkuB.setSkuCode("childSkuB");
		childSkuB.setGuid("childSkuB");
		childSkuB.setProduct(child1Product);
		productSkus.put("childSkuA", childSkuA);
		productSkus.put("childSkuB", childSkuB);
		child1Product.setProductSkus(productSkus);
		child1Product.setDefaultSku(childSkuA);

		Store store = new StoreImpl();
		Warehouse warehouse = new WarehouseImpl();
		warehouse.setUidPk(WAREHOUSE_UIDPK);
		List<Warehouse> warehouseList = new ArrayList<Warehouse>();
		warehouseList.add(warehouse);
		store.setWarehouses(warehouseList);

		ProductInventoryShoppingServiceImpl productInventoryShoppingServiceImpl = new ProductInventoryShoppingServiceImpl() {
			@Override
			protected int calculateQuantityRequiringPreOrderBackOrder(
					final int quantity, final ProductSku productSku,
					final Store store, final InventoryDto skuInventory) {
				return 0;
			}
		};

		final InventoryDao inventoryDao = context.mock(InventoryDao.class);
		final InventoryJournalDao inventoryJournalDao = context.mock(InventoryJournalDao.class);
		context.checking(new Expectations() {
			{
				oneOf(inventoryDao).getInventory("childSkuB", WAREHOUSE_UIDPK); will(returnValue(inventory));
	        	allowing(inventoryJournalDao).getRollup(with(any(InventoryKey.class)));
	        	will(returnValue(new InventoryJournalRollupImpl()));
			}
		});

		productInventoryShoppingServiceImpl.setProductInventoryManagementService(createPIMS(inventoryDao, inventoryJournalDao));

		SkuInventoryDetails inventoryDetails = productInventoryShoppingServiceImpl.getSkuInventoryDetails(productSku, store, rootDto);

		assertEquals(InventoryMessage.IN_STOCK, inventoryDetails.getMessageCode());
	}



	private ProductInventoryManagementServiceImpl createPIMS(final InventoryDao inventoryDao, final InventoryJournalDao inventoryJournalDao) {
        JournalingInventoryStrategy journalingInventoryStrategy = new JournalingInventoryStrategy();
        journalingInventoryStrategy.setInventoryDao(inventoryDao);
        journalingInventoryStrategy.setInventoryJournalDao(inventoryJournalDao);

		InventoryFacadeImpl inventoryFacade = new InventoryFacadeImpl();
		Map<String, InventoryStrategy> strategies = new HashMap<String, InventoryStrategy>();
		strategies.put("allocatedjournaling", journalingInventoryStrategy);
		inventoryFacade.setStrategies(strategies);
		inventoryFacade.selectStrategy("allocatedjournaling");

		ProductInventoryManagementServiceImpl productInventoryManagementService = new ProductInventoryManagementServiceImpl();
		productInventoryManagementService.setInventoryFacade(inventoryFacade);
		return productInventoryManagementService;
	}

	/**
	 * Test minOrderQuantity is ignored on the constituents of a bundle.
	 */
	@Test
	public void testPopulateInventoryRequirementsMap() {
		ProductBundle bundle = new ProductBundleImpl();


		Product product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		product.setMinOrderQty(2);
		ProductSkuImpl productSku = new ProductSkuImpl();
		productSku.setGuid("SKU");
		product.addOrUpdateSku(productSku);

		BundleConstituent constituent = new BundleConstituentImpl();
		constituent.setConstituent(product);
		constituent.setQuantity(2);
		bundle.addConstituent(constituent);

		ProductInventoryShoppingServiceImpl impl = new ProductInventoryShoppingServiceImpl();

		HashMap <String, Integer> inventoryRequirementsMap = new HashMap <String, Integer>();
		impl.addInventoryRequirementsToMap(bundle, inventoryRequirementsMap, 1, true);

		assertEquals(Integer.valueOf(2), inventoryRequirementsMap.get(productSku.getSkuCode()));
	}


	/**
	 * Test that message code is returned correctly when in a bundle.
	 * Inventory is 1, min order quantity is 2.
	 * Inventory should be still in stock.
	 */
	@Test
	public void testMessageCodeIgnoresMinOrderQtyForBundles() {
		ProductInventoryShoppingServiceImpl impl = new ProductInventoryShoppingServiceImpl();

		Product product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		ProductSkuImpl productSku = new ProductSkuImpl();
		productSku.setGuid("SKU");
		product.addOrUpdateSku(productSku);
		product.setMinOrderQty(2);

		NodeInventory nodeInventory = impl.new NodeInventory(1, 1, new Date());

		assertEquals(InventoryMessage.IN_STOCK, impl.getMessageCode(productSku, nodeInventory, true, 0));
	}

	/**
	 * Test that message code is returned correctly.
	 * Inventory is 1, min order quantity is 2.
	 * Inventory should not be in stock
	 */
	@Test
	public void testMessageCodeWithMinOrderQty() {
		ProductInventoryShoppingServiceImpl impl = new ProductInventoryShoppingServiceImpl();

		Product product = new ProductImpl();
		product.setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
		ProductSkuImpl productSku = new ProductSkuImpl();
		productSku.setGuid("SKU");
		product.addOrUpdateSku(productSku);
		product.setMinOrderQty(2);

		NodeInventory nodeInventory = impl.new NodeInventory(1, 1, new Date());

		assertEquals(InventoryMessage.OUT_OF_STOCK_WITH_RESTOCK_DATE, impl.getMessageCode(productSku, nodeInventory, false, 0));
	}


	/**
	 * Test that the service checks inventory with the correct quantity.
	 * Minimum order quantity should be considered unless in a bundle.
	 */
	@Test
	public void testCorrectDefaultOrderQuantityRequired() {
		ProductInventoryShoppingServiceImpl impl = new ProductInventoryShoppingServiceImpl();

		int currentRequiredAmount = 1;
		int minOrderQuantity = 2;
		int newQuantityRequired = 2 + 1;

		assertEquals(2 + 1 + 1,
				impl.getRequiredSkuQuantity(minOrderQuantity, currentRequiredAmount, newQuantityRequired, false));
		assertEquals(2 + 2,
				impl.getRequiredSkuQuantity(minOrderQuantity, currentRequiredAmount, newQuantityRequired, true));


		minOrderQuantity = 2 + 1;
		newQuantityRequired = 1;

		assertEquals(2 + 1,
				impl.getRequiredSkuQuantity(minOrderQuantity, currentRequiredAmount, newQuantityRequired, false));
		assertEquals(2,
				impl.getRequiredSkuQuantity(minOrderQuantity, currentRequiredAmount, newQuantityRequired, true));
	}

	/**
	 * Tests that getSkuCodesForInventoryLookup, for a single skued product,
	 * returns one skuCode.
	 */
	@Test
	public void testGetSkuCodesForInventoryLookup1Sku() {
		ProductInventoryShoppingServiceImpl service = new ProductInventoryShoppingServiceImpl();
		Product product = new ProductImpl();
		ProductSku sku = new ProductSkuImpl();
		sku.setSkuCode("ABC");
		sku.setGuid("ABC");
		product.addOrUpdateSku(sku);
		Set<String> resultSet = service.getSkuCodesForInventoryLookup(product, null);
		assertEquals("1 sku code for the sole sku", 1, resultSet.size());
		assertEquals("ABC", resultSet.iterator().next());
	}

	/**
	 * Tests that getSkuCodesForInventoryLookup, for a bundle with 1 level,
	 * returns four skuCodes.
	 */
	@Test
	public void testGetSkuCodesForInventoryLookupBundle1Level() {
		ProductInventoryShoppingServiceImpl service = new ProductInventoryShoppingServiceImpl();

		ProductBundle product = new ProductBundleImpl();
		ProductSku sku = new ProductSkuImpl();
		sku.setSkuCode("ABC");
		sku.setGuid("ABC");
		product.addOrUpdateSku(sku);

		Product product1 = new ProductImpl();
		ProductSku sku1 = new ProductSkuImpl();
		sku1.setSkuCode("DEF");
		sku1.setGuid("DEF");
		product1.addOrUpdateSku(sku1);

		BundleConstituent constituent1 = new BundleConstituentImpl();
		constituent1.setConstituent(product1);
		product.addConstituent(constituent1);

		Product product2 = new ProductImpl();
		ProductSku sku2 = new ProductSkuImpl();
		sku2.setSkuCode("GHI");
		sku2.setGuid("GHI");
		product2.addOrUpdateSku(sku2);

		BundleConstituent constituent2 = new BundleConstituentImpl();
		constituent2.setConstituent(product2);
		product.addConstituent(constituent2);

		Product product3 = new ProductImpl();
		ProductSku sku3 = new ProductSkuImpl();
		sku3.setSkuCode("JKL");
		sku3.setGuid("JKL");
		product3.addOrUpdateSku(sku3);

		BundleConstituent constituent3 = new BundleConstituentImpl();
		constituent3.setConstituent(product3);
		product.addConstituent(constituent3);

		Set<String> resultSet = service.getSkuCodesForInventoryLookup(product, null);
		assertEquals("4 sku codes (1 for root + 1 for each child", FOUR, resultSet.size());
		assertTrue("Contains root sku code", resultSet.contains("ABC"));
		assertTrue("Contains constituent1", resultSet.contains("DEF"));
		assertTrue("Contains constituent2", resultSet.contains("GHI"));
		assertTrue("Contains constituent3", resultSet.contains("JKL"));
	}

	/**
	 * Tests that getSkuCodesForInventoryLookup, for a bundle with 2 levels,
	 * returns three skuCodes.
	 */
	@Test
	public void testGetSkuCodesForInventoryLookupBundle2Levels() {
		ProductInventoryShoppingServiceImpl service = new ProductInventoryShoppingServiceImpl();

		ProductBundle product = new ProductBundleImpl();
		ProductSku sku = new ProductSkuImpl();
		sku.setSkuCode("ABC");
		sku.setGuid("ABC");
		product.addOrUpdateSku(sku);

		ProductBundle product1 = new ProductBundleImpl();
		ProductSku sku1 = new ProductSkuImpl();
		sku1.setSkuCode("DEF");
		sku1.setGuid("DEF");
		product1.addOrUpdateSku(sku1);

		BundleConstituent constituent1 = new BundleConstituentImpl();
		constituent1.setConstituent(product1);
		product.addConstituent(constituent1);

		Product product2 = new ProductImpl();
		ProductSku sku2 = new ProductSkuImpl();
		sku2.setSkuCode("GHI");
		sku2.setGuid("GHI");
		product2.addOrUpdateSku(sku2);

		BundleConstituent constituent2 = new BundleConstituentImpl();
		constituent2.setConstituent(product2);
		product1.addConstituent(constituent2);


		Set<String> resultSet = service.getSkuCodesForInventoryLookup(product, null);
		assertEquals("3 sku codes (1 for root + 1 for each descendant", THREE, resultSet.size());
		assertTrue("Contains root sku code", resultSet.contains("ABC"));
		assertTrue("Contains constituent1", resultSet.contains("DEF"));
		assertTrue("Contains constituent2", resultSet.contains("GHI"));
	}

	/**
	 * Test for when there is no products for getSkuInventoryDetailsForAllSkus.
	 */
	@Test
	public void testGetSkuInventoryDetailsForAllSkusNoProducts() {
		List<Product> products = new ArrayList<Product>();

		final InventoryDao inventoryDao = context.mock(InventoryDao.class);
		final InventoryJournalDao inventoryJournalDao = context.mock(InventoryJournalDao.class);
		ProductInventoryShoppingServiceImpl service = new ProductInventoryShoppingServiceImpl();
		context.checking(new Expectations() { {
				allowing(inventoryDao).getInventoryMap(with(Collections.<String>emptySet()), with(any(Long.class)));
	        	allowing(inventoryJournalDao).getRollup(with(any(InventoryKey.class)));
	        	will(returnValue(new InventoryJournalRollupImpl()));
			} });

		final Store store = setUpStore();

		service.setProductInventoryManagementService(createPIMS(inventoryDao, inventoryJournalDao));

		Map<String, Map<String, SkuInventoryDetails>> productSkuInventoryMap = service.getSkuInventoryDetailsForAllSkus(products, store);
		assertEquals("0 sku code to SkuInventoryDetail map for all products", 0, productSkuInventoryMap.keySet().size());
	}

	/**
	 * Test for getSkuInventoryDetailsForAllSkus when there are some products.
	 */
	@Test
	public void testGetSkuInventoryDetailsForAllSkusSomeProducts() {
		Product product1 = createProductWithSku("code1", new String[] { "sku1" });
		Product product2 = createProductWithSku("code2", new String[] { "sku2" });
		Product product3 = createProductWithSku("code3", new String[] { "sku3", "sku4" });

		final Collection<String> skuCodes = new HashSet<String>();
		skuCodes.add("sku1");
		skuCodes.add("sku2");
		skuCodes.add("sku3");
		skuCodes.add("sku4");

		List<Product> products = new ArrayList<Product>();
		products.add(product1);
		products.add(product2);
		products.add(product3);

		final InventoryDao inventoryDao = context.mock(InventoryDao.class);
		ProductInventoryShoppingServiceImpl service = new ProductInventoryShoppingServiceImpl() {
			@Override
			protected void addInventoryRequirementsToMap(final Product product, final Map<String, Integer> inventoryRequirementsMap,
					final int quantityRequired, final boolean inBundle) {
				// do nothing
			}

			@Override
			SkuInventoryDetails getSkuInventoryDetailsForNode(
					final ProductSku productSku, final Store store, final Product product,
					final Map<String, Integer> inventoryRequirementsMap,
					final ShoppingItemDto shoppingItemDto, final boolean inBundle,
					final Map<String, InventoryDto> skuInventoryMap) {
				return new SkuInventoryDetails();
			}
		};

		final InventoryJournalDao inventoryJournalDao = context.mock(InventoryJournalDao.class);
		context.checking(new Expectations() { {
				allowing(inventoryDao).getInventoryMap(with(skuCodes), with(any(Long.class)));
	        	allowing(inventoryJournalDao).getRollup(with(any(InventoryKey.class)));
	        	will(returnValue(new InventoryJournalRollupImpl()));
			} });

		final Store store = setUpStore();

		service.setProductInventoryManagementService(createPIMS(inventoryDao, inventoryJournalDao));

		Map<String, Map<String, SkuInventoryDetails>> productSkuInventoryMap = service.getSkuInventoryDetailsForAllSkus(products, store);
		assertEquals("3 sku code to SkuInventoryDetail map for all products", THREE, productSkuInventoryMap.keySet().size());
		assertEquals("1 sku code to SkuInventoryDetail map for product with code code1", 1, productSkuInventoryMap.get("code1").keySet().size());
		assertNotNull("Inventory details for product with code code1 and sku sku1 not be null", productSkuInventoryMap.get("code1").get("sku1"));
		assertEquals("1 sku code to SkuInventoryDetail map for product with code code2", 1, productSkuInventoryMap.get("code2").keySet().size());
		assertNotNull("Inventory details for product with code code2 and sku sku1 not be null", productSkuInventoryMap.get("code2").get("sku2"));
		assertEquals("2 sku code to SkuInventoryDetail map for product with code code3", 2, productSkuInventoryMap.get("code3").keySet().size());
		assertNotNull("Inventory details for product with code code3 and sku sku4 not be null", productSkuInventoryMap.get("code3").get("sku4"));
	}

	private Store setUpStore() {
		Store store = new StoreImpl();
		Warehouse warehouse = new WarehouseImpl();
		List<Warehouse> warehouses = new ArrayList<Warehouse>();
		warehouses.add(warehouse);
		store.setWarehouses(warehouses);
		return store;
	}

	private Product createProductWithSku(final String productCode, final String[] skuCodes) {
		Product product = new ProductImpl();
		product.setCode(productCode);
		for (String skuCode : skuCodes) {
			ProductSku sku = new ProductSkuImpl();
			sku.setSkuCode(skuCode);
			sku.setGuid(skuCode);
			product.addOrUpdateSku(sku);
		}


		return product;
	}
}
