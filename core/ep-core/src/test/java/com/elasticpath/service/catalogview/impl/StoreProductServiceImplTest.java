package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Availability;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalog.impl.BundleConstituentImpl;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.LinkedCategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.domain.catalog.impl.ProductConstituentImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.catalog.impl.StoreProductLoadTunerImpl;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.domain.catalogview.impl.StoreProductImpl;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.domain.store.impl.WarehouseImpl;
import com.elasticpath.sellingchannel.inventory.ProductInventoryShoppingService;
import com.elasticpath.service.catalog.ProductAssociationRetrieveStrategy;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.impl.BundleIdentifierImpl;
import com.elasticpath.service.catalogview.AvailabilityStrategy;
import com.elasticpath.service.catalogview.IndexProduct;
import com.elasticpath.service.catalogview.ProductAvailabilityService;
import com.elasticpath.service.catalogview.ProductRetrieveStrategy;
import com.elasticpath.service.order.AllocationService;
import com.elasticpath.service.search.query.ProductAssociationSearchCriteria;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Tests {@link StoreProductServiceImpl}.
 */
@SuppressWarnings({"PMD.TooManyStaticImports", "PMD.TooManyMethods", "PMD.ExcessiveImports" })
public class StoreProductServiceImplTest {

	private static final int DEFAULT_CATALOG_UIDPK = 1234;
	private static final String TEST_STORE_CODE = "testStoreCode";
	private static final String SKU = "SKU";
	private final StoreProductServiceImpl storeProductServiceImpl = new StoreProductServiceImpl();
	private static final int SIXTY_SECONDS = 60 * 1000;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private AllocationService allocationService;
	private AvailabilityStrategy availabilityStrategy;
	private ProductAvailabilityService availabilityService;
	private ProductInventoryShoppingService productInventoryShoppingService;
	private BeanFactoryExpectationsFactory expectationsFactory;
	private BeanFactory beanFactory;
    private final BundleIdentifierImpl bundleIdentifier = new BundleIdentifierImpl();
	private ProductService productService;
	private ProductRetrieveStrategy productRetrieveStrategy;

	/**
	 * Sets up the test case.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		allocationService = context.mock(AllocationService.class);
		availabilityService = context.mock(ProductAvailabilityService.class);
		availabilityStrategy = context.mock(AvailabilityStrategy.class);
		productService = context.mock(ProductService.class);
		productRetrieveStrategy = context.mock(ProductRetrieveStrategy.class);
		productInventoryShoppingService = context.mock(ProductInventoryShoppingService.class);

		beanFactory = context.mock(BeanFactory.class);
        expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);

        expectationsFactory.allowingBeanFactoryGetBean("productConstituent", ProductConstituentImpl.class);
        expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRODUCT_ASSOCIATION, ProductAssociationImpl.class);
        expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.BUNDLE_CONSTITUENT, BundleConstituentImpl.class);
        expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);

		storeProductServiceImpl.setAvailabilityStrategies(Collections.singletonList(availabilityStrategy));
		storeProductServiceImpl.setBundleIdentifier(bundleIdentifier);
		storeProductServiceImpl.setProductAvailabilityService(availabilityService);
		storeProductServiceImpl.setProductInventoryShoppingService(productInventoryShoppingService);
		storeProductServiceImpl.setProductRetrieveStrategy(productRetrieveStrategy);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Tests setStoreProductAssociations().
	 * Checks if the product associations' target products
	 * get promotions applied and if associations are set properly.
	 */
	@Test
	public void testSetStoreProductAssociations() {
		final int assocType = 1;

		final Product targetProduct = getProduct();

		final ProductSku targetSku = targetProduct.getDefaultSku();
		targetSku.setSkuCode("ABC");

		final ProductAssociationImpl assoc1 = new ProductAssociationImpl();
		assoc1.setStartDate(new Date(System.currentTimeMillis()	- SIXTY_SECONDS));
		assoc1.setAssociationType(assocType);
		assoc1.setTargetProduct(targetProduct);

		CatalogImpl catalogImpl = new CatalogImpl();
		catalogImpl.setMaster(true);
		catalogImpl.setCode("catalog1");

		Category category = new CategoryImpl();
		category.setCatalog(catalogImpl);

		Set<Category> categories = new HashSet<Category>();
		categories.add(category);

		targetProduct.setCode("productCode");
		targetProduct.setCategories(categories);
		StoreProduct storeProduct = new StoreProductImpl(targetProduct);

		final SkuInventoryDetails inventoryDetails = createDefaultInventoryDetails();
		inventoryDetails.setHasSufficientUnallocatedQty(true);

		List<Warehouse> warehouseList = new ArrayList<Warehouse>();
		final WarehouseImpl warehouse = new WarehouseImpl();
		warehouseList.add(warehouse);

		final Store store = new StoreImpl();
		store.setCatalog(catalogImpl);
		store.setWarehouses(warehouseList);

		final HashSet<ProductAssociation> assocSet = new HashSet<ProductAssociation>();
		assocSet.add(assoc1);

		final ProductAssociationSearchCriteria criteria = new ProductAssociationSearchCriteria();
		criteria.setSourceProductCode("productCode");
		criteria.setCatalogCode("catalog1");
		criteria.setWithinCatalogOnly(true);

		StoreProductServiceImpl service = new StoreProductServiceImpl();
		service.setBundleIdentifier(bundleIdentifier);
		service.setAvailabilityStrategies(Collections.<AvailabilityStrategy>emptyList());
		service.setProductAvailabilityService(availabilityService);
		service.setProductInventoryShoppingService(productInventoryShoppingService);

		final ProductAssociationRetrieveStrategy productAssociationRetrieveStrategy = context.mock(ProductAssociationRetrieveStrategy.class);
		service.setProductAssociationRetrieveStrategy(productAssociationRetrieveStrategy);

		context.checking(new Expectations() { {
			Map<String, SkuInventoryDetails> skuInventoryMap = Collections.singletonMap("ABC", inventoryDetails);

			oneOf(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(targetProduct, store);
				will(returnValue(skuInventoryMap));

			oneOf(productAssociationRetrieveStrategy).getAssociations(criteria);
				will(returnValue(assocSet));

			allowing(allocationService).hasSufficientUnallocatedQty(targetSku, warehouse.getUidPk(), targetProduct.getMinOrderQty());
				will(returnValue(true));

			allowing(availabilityService).isProductAvailable(targetProduct, skuInventoryMap, true); will(returnValue(true));
			allowing(availabilityService).isProductDisplayable(
					targetProduct, store, skuInventoryMap, true); will(returnValue(true));
		}
		});

		service.setStoreProductAssociations(storeProduct, store, "catalog1");

		final Set<ProductAssociation> associationsByType = storeProduct.getAssociationsByType(assocType);
		assertEquals("Expected exactly one ProductAssociation", 1, associationsByType.size());

		final ProductAssociation actualProductAssociation = associationsByType.iterator().next();
		assertTrue("Expected ProductAssociation to have targetProduct that is a StoreProduct",
				actualProductAssociation.getTargetProduct() instanceof StoreProduct);
		assertEquals("Expected StoreProduct to wrap targetProduct", targetProduct,
				((StoreProduct) actualProductAssociation.getTargetProduct()).getWrappedProduct());
	}


	/**
	 * Tests that a product included in a first catalog is not included in a second catalog.
	 */
	@Test
	public void testIsNotIncludedInSecondCatalog() {
		Catalog catalog1 = new CatalogImpl();
		catalog1.setMaster(true);
		catalog1.setUidPk(1);

		Catalog catalog2 = new CatalogImpl();
		catalog2.setMaster(true);
		catalog2.setUidPk(2);

		Set<Category> categories = new HashSet<Category>();
		Category category = new CategoryImpl();
		category.setCatalog(catalog1);
		categories.add(category);

		Product product = getProduct();
		product.setCategories(categories);

		assertTrue("The product should be in the first catalog.", product.isInCatalog(catalog1, true));
		assertFalse("The product should not be in the second catalog.", product.isInCatalog(catalog2, true));
	}

	/**
	 * Tests that a non-master catalog with a non-linked category could have a product that is included by default.
	 */
	@Test
	public void testIsIncludedNonLinkedCategoryVirtualCatalog() {
		Catalog catalog = new CatalogImpl();
		catalog.setMaster(false);

		Product product = getProduct();
		Set<Category> categories = new HashSet<Category>();
		Category category = new CategoryImpl();

		category.setCatalog(catalog);

		categories.add(category);
		product.setCategories(categories);
		assertTrue("Product should be included in a non-linked category in a virtual catalog", product.isInCatalog(catalog, true));
	}

	/**
	 * Tests that a virtual catalog with a linked category could have a product
	 * that is included after setting the included flag on the category.
	 */
	@Test
	public void testIsIncludedLinkedCategoryVirtualCatalog() {
		Catalog catalog = new CatalogImpl();
		Catalog masterCatalog = new CatalogImpl();
		masterCatalog.setMaster(true);
		catalog.setMaster(false);

		Product product = getProduct();
		Set<Category> categories = new HashSet<Category>();
		Category masterCategory = new CategoryImpl();
		masterCategory.setCatalog(masterCatalog);
		Category category = new LinkedCategoryImpl();
		category.setCatalog(catalog);
		category.setMasterCategory(masterCategory);

		categories.add(category);
		product.setCategories(categories);
		assertFalse("Product should not be included by default in a linked category in a virtual catalog", product.isInCatalog(catalog, true));

		// set category included in catalog
		category.setIncluded(true);

		assertTrue("Product should be included in a linked category in a virtual catalog", product.isInCatalog(catalog, true));
	}

	/**
	 * Tests that a master catalog with a category could have a product
	 * that is included by default.
	 */
	@Test
	public void testIsIncludedCategoryMasterCatalog() {
		Catalog catalog = new CatalogImpl();
		catalog.setMaster(true);

		Product product = getProduct();
		Set<Category> categories = new HashSet<Category>();
		Category category = new CategoryImpl();
		category.setCatalog(catalog);
		categories.add(category);
		product.setCategories(categories);

		assertTrue("Product should be included in a category in a master catalog", product.isInCatalog(catalog, true));
	}

	/**
	 * Tests {@link StoreProductServiceImpl#determineSkusAvailability()}. Check
	 * that the SkuVailability map is populated correctly
	 */
	@Test
	public void testDetermineSkusAvailabilitySkuAvailableWithInventoryAndWithinDateRange() {
		final Map<String, SkuInventoryDetails> skuInventoryDetails = new LinkedHashMap<String, SkuInventoryDetails>();
		final Map<String, ProductSku> skus = new HashMap<String, ProductSku>();

		SkuInventoryDetails inventoryDetails = new SkuInventoryDetails();
		inventoryDetails.setHasSufficientUnallocatedQty(true);

		skuInventoryDetails.put(SKU, inventoryDetails);
		skus.put(SKU, getProductSku(SKU, true));

		Product product = getProduct(skus);
		StoreProductImpl storeProduct = new StoreProductImpl(product);
		storeProductServiceImpl.determineSkusAvailability(skus.values(),
				skuInventoryDetails, storeProduct);

		assertTrue(storeProduct.isSkuAvailable(SKU));
	}

	/**
	 * Tests {@link StoreProductServiceImpl#determineSkusAvailability()}. Check
	 * that the SkuVailability map is populated correctly
	 */
	@Test
	public void testDetermineSkusAvailabilityProductSkuNotAvailabeWithInventoryButWithinDateRange() {
		final Map<String, SkuInventoryDetails> skuInventoryDetails = new LinkedHashMap<String, SkuInventoryDetails>();
		final Map<String, ProductSku> skus = new HashMap<String, ProductSku>();

		SkuInventoryDetails inventoryDetails = new SkuInventoryDetails();
		inventoryDetails.setHasSufficientUnallocatedQty(false);

		skuInventoryDetails.put(SKU, inventoryDetails);
		skus.put(SKU, getProductSku(SKU, true));

		Product product = new ProductImpl();
		product.setStartDate(new Date(System.currentTimeMillis() - 1));
		product.setProductSkus(skus);

		StoreProductImpl storeProduct = new StoreProductImpl(product);
		storeProductServiceImpl.determineSkusAvailability(skus.values(),
				skuInventoryDetails, storeProduct);

		assertFalse(storeProduct.isSkuAvailable(SKU));
	}

	/**
	 * Tests {@link StoreProductServiceImpl#determineSkusAvailability()}. Check
	 * that the SkuVailability map is populated correctly
	 */
	@Test
	public void testDetermineSkusAvailabilitySkuAvailabeWithInventoryButNotWithinDateRange() {
		final Map<String, SkuInventoryDetails> skuInventoryDetails = new LinkedHashMap<String, SkuInventoryDetails>();
		final Map<String, ProductSku> skus = new HashMap<String, ProductSku>();

		SkuInventoryDetails inventoryDetails = new SkuInventoryDetails();
		inventoryDetails.setHasSufficientUnallocatedQty(true);

		skuInventoryDetails.put(SKU, inventoryDetails);
		skus.put(SKU, getProductSku(SKU, false));

		Product product = new ProductImpl();
		product.setStartDate(new Date(System.currentTimeMillis() - 1));
		product.setProductSkus(skus);

		StoreProductImpl storeProduct = new StoreProductImpl(product);
		storeProductServiceImpl.determineSkusAvailability(skus.values(),
				skuInventoryDetails, storeProduct);

		assertFalse(storeProduct.isSkuAvailable(SKU));
	}

	@Test
	public void testGetProductForStoreAvailabilityDetailsWithProductWithNoSkus() {
		final Product product = new ProductImpl();
		final Store store = new StoreImpl();

		context.checking(new Expectations() { {
			oneOf(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(product, store);
			will(returnValue(Collections.emptyMap()));
		} });
		StoreProduct storeProduct = storeProductServiceImpl.getProductForStore(product, store);

		assertFalse(storeProduct.isAvailable());
		assertFalse(storeProduct.isDisplayable());
		assertFalse(storeProduct.isPurchasable());
	}

	@Test
	public void testGetProductForStoreAvailabilityDetailsWithSpecifiedSku() {
		final ProductSku sku = new ProductSkuImpl();
		sku.setUidPk(1L);
		sku.setSkuCode("sku");

		final Product product = new ProductImpl();
		product.setUidPk(1L);
		product.addOrUpdateSku(sku);

		final Store store = new StoreImpl();

		context.checking(new Expectations() { {
			oneOf(productRetrieveStrategy).retrieveProduct(with(product.getUidPk()), with(any(StoreProductLoadTuner.class)));
				will(returnValue(product));

			oneOf(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(product, store);
			final SkuInventoryDetails skuInventory = new SkuInventoryDetails();
				will(returnValue(Collections.singletonMap("sku", skuInventory)));

			// If SKU is specified, then we compute availability based on a single sku instead of the whole thing.
			oneOf(availabilityService).isSkuAvailable(product, sku, skuInventory);
				will(returnValue(true));
			oneOf(availabilityService).isSkuDisplayable(product, sku, store, skuInventory);
				will(returnValue(true));

			oneOf(availabilityStrategy).getAvailability(product, true, true, false);
				will(returnValue(Availability.AVAILABLE));
		} });

		StoreProduct storeProduct = storeProductServiceImpl.getProductForStore(
				product.getUidPk(), sku.getUidPk(), store, new StoreProductLoadTunerImpl());
		assertTrue(storeProduct.isAvailable());
		assertTrue(storeProduct.isDisplayable());
		assertFalse("Product is not in catalog", storeProduct.isPurchasable());
	}

	/**
	 * Test getting {@link IndexProduct} with no {@link ProductSku)s.
	 */
	@Test
	public void testGetIndexProductsWithoutSkus() {

		final Collection<Long> productUids = createProductUids();
		final Collection<Product> retrievedProducts = createProductsWithoutSkus(productUids);
		final SkuInventoryDetails defaultInventoryDetails = createDefaultInventoryDetails();

		StoreProductServiceImpl service = new StoreProductServiceImpl() {
			@Override
			Map<String, SkuInventoryDetails> calculateInventoryDetailsForAllSkus(final Product product, final Store store) {
				return createDefaultSkuInventoryDetails(defaultInventoryDetails);
			}
		};

		service.setProductService(productService);

		context.checking(new Expectations() { {
				oneOf(productService).findByUidsWithFetchGroupLoadTuner(productUids, null); will(returnValue(retrievedProducts));
				allowing(productInventoryShoppingService).getSkuInventoryDetails(null, null); will(returnValue(defaultInventoryDetails));
			}
		});

		final Collection<Store> stores = createTestStores();
		final Collection<IndexProduct> retrievedIndexProducts = service.getIndexProducts(productUids, stores, null);
		assertIndexProductsWithoutSkus(productUids, retrievedIndexProducts);
	}

	/**
	 * Test getting {@link IndexProduct} with {@link ProductSku)s.
	 */
	@Test
	public void testGetIndexProductsWithMultipleSkus() {
		final List<Store> stores = createTestStores();
		final Collection<Long> productUids = createProductUids();
		final List<Product> retrievedProductsWithSkus = createProductsWithSkus(productUids, 3);
		final Map<String, SkuInventoryDetails> defaultInventoryDetails = new HashMap<String, SkuInventoryDetails>();

		final SkuInventoryDetails emptyInventory = new SkuInventoryDetails();
		emptyInventory.setAvailableQuantityInStock(0);
		emptyInventory.setHasSufficientUnallocatedQty(false);

		final SkuInventoryDetails hasInventory = new SkuInventoryDetails();
		hasInventory.setAvailableQuantityInStock(1);
		hasInventory.setHasSufficientUnallocatedQty(true);

		defaultInventoryDetails.put("testSkuCode0", emptyInventory);
		defaultInventoryDetails.put("testSkuCode1", emptyInventory);
		defaultInventoryDetails.put("testSkuCode2", hasInventory);

		storeProductServiceImpl.setProductService(productService);

		context.checking(new Expectations() { {
			oneOf(productService).findByUidsWithFetchGroupLoadTuner(productUids, null); will(returnValue(retrievedProductsWithSkus));

			allowing(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(with(any(Product.class)), with(createTestStore()));
			will(returnValue(defaultInventoryDetails));

			Store store = stores.get(0);
			oneOf(availabilityService).isProductAvailable(retrievedProductsWithSkus.get(0), defaultInventoryDetails, false);
			will(returnValue(true));
			oneOf(availabilityService).isProductDisplayable(retrievedProductsWithSkus.get(0), store, defaultInventoryDetails, false);
			will(returnValue(true));

			oneOf(availabilityService).isProductAvailable(retrievedProductsWithSkus.get(1), defaultInventoryDetails, false);
			will(returnValue(true));
			oneOf(availabilityService).isProductDisplayable(retrievedProductsWithSkus.get(0), store, defaultInventoryDetails, false);
			will(returnValue(true));

			oneOf(availabilityService).isProductAvailable(retrievedProductsWithSkus.get(2), defaultInventoryDetails, false);
			will(returnValue(true));
			oneOf(availabilityService).isProductDisplayable(retrievedProductsWithSkus.get(0), store, defaultInventoryDetails, false);
			will(returnValue(true));
		}
		});

		final Collection<IndexProduct> retrievedIndexProducts = storeProductServiceImpl.getIndexProducts(productUids, stores, null);
		assertIndexProductsWithSkus(productUids, retrievedIndexProducts);
	}


	/**
	 * Test getting {@link IndexProduct} with {@link ProductSku)s.
	 */
	@Test
	public void testGetIndexProductsWithMultipleSkusAndNoInventory() {
		final List<Store> stores = createTestStores();
		final Collection<Long> productUids = createProductUids();
		final List<Product> retrievedProductsWithSkus = createProductsWithSkus(productUids, 3);
		final Map<String, SkuInventoryDetails> defaultInventoryDetails = new HashMap<String, SkuInventoryDetails>();

		final SkuInventoryDetails emptyInventory = new SkuInventoryDetails();
		emptyInventory.setAvailableQuantityInStock(0);
		emptyInventory.setHasSufficientUnallocatedQty(false);

		defaultInventoryDetails.put("testSkuCode0", emptyInventory);
		defaultInventoryDetails.put("testSkuCode1", emptyInventory);
		defaultInventoryDetails.put("testSkuCode2", emptyInventory);

		storeProductServiceImpl.setProductService(productService);

		context.checking(new Expectations() { {
			oneOf(productService).findByUidsWithFetchGroupLoadTuner(productUids, null); will(returnValue(retrievedProductsWithSkus));

			allowing(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(with(any(Product.class)), with(createTestStore()));
			will(returnValue(defaultInventoryDetails));

			Store store = stores.get(0);
			oneOf(availabilityService).isProductAvailable(retrievedProductsWithSkus.get(0), defaultInventoryDetails, false);
			will(returnValue(false));
			oneOf(availabilityService).isProductDisplayable(retrievedProductsWithSkus.get(0), store, defaultInventoryDetails, false);
			will(returnValue(false));
			oneOf(availabilityService).isProductAvailable(retrievedProductsWithSkus.get(1), defaultInventoryDetails, false);
			will(returnValue(false));
			oneOf(availabilityService).isProductDisplayable(retrievedProductsWithSkus.get(1), store, defaultInventoryDetails, false);
			will(returnValue(false));
			oneOf(availabilityService).isProductAvailable(retrievedProductsWithSkus.get(2), defaultInventoryDetails, false);
			will(returnValue(false));
			oneOf(availabilityService).isProductDisplayable(retrievedProductsWithSkus.get(2), store, defaultInventoryDetails, false);
			will(returnValue(false));
		}
		});

		final Collection<IndexProduct> retrievedIndexProducts = storeProductServiceImpl.getIndexProducts(productUids, stores, null);

		for (IndexProduct retrievedIndexProduct : retrievedIndexProducts) {
			Long retrievedIndexProductUid = retrievedIndexProduct.getUidPk();
			assertTrue("IndexProduct uid is missing: " + retrievedIndexProductUid, productUids.contains(retrievedIndexProductUid));
			assertFalse("Product should not be available.", retrievedIndexProduct.isAvailable(TEST_STORE_CODE));
			assertFalse("Product should not displayable.", retrievedIndexProduct.isDisplayable(TEST_STORE_CODE));
		}
	}

	/**
	 * Test that the availability is set on the store product.
	 */
	@Test
	public void testAvailabilityStatus() {
		final ProductSku sku = getProductSku(SKU, true);
		final Product product = getProduct(Collections.singletonMap(SKU, sku));
		final Store store = new StoreImpl();
		final SkuInventoryDetails skuInventoryDetails = new SkuInventoryDetails();

		context.checking(new Expectations() {
			{
				final Map<String, SkuInventoryDetails> skuInventoryMap = Collections.singletonMap(SKU, skuInventoryDetails);
				oneOf(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(product, store);
				will(returnValue(skuInventoryMap));

				oneOf(availabilityService).isProductAvailable(product, skuInventoryMap, true);
				will(returnValue(true));
				oneOf(availabilityService).isProductDisplayable(product, store, skuInventoryMap, true);
				will(returnValue(false));

				oneOf(availabilityStrategy).getAvailability(product, true, false, false);
				will(returnValue(Availability.AVAILABLE));
			}
		});

		StoreProduct storeProduct = storeProductServiceImpl.getProductForStore(product, store);
		assertEquals("The availability should have been set", Availability.AVAILABLE, storeProduct.getAvailability());
	}
	
	/**
	 * Test that the product is not available when it has no skus.
	 */
	@Test
	public void testProductNotAvailableWhenNoSkus() {
		final Product product = getProduct(Collections.<String, ProductSku>emptyMap());
		final Store store = new StoreImpl();

		context.checking(new Expectations() {
			{
				oneOf(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(product, store);
				will(returnValue(Collections.emptyMap()));
			}
		});
		
		StoreProduct storeProduct = storeProductServiceImpl.getProductForStore(product, store);
		assertEquals("A product with no skus should not be available", Availability.NOT_AVAILABLE, storeProduct.getAvailability());
	}
	
	/**
	 * Test that availability is null when no relevant availability strategy was set.
	 */
	@Test
	public void testNullAvailabilityWhenNoRelevantStrategy() {
		final ProductSku sku = getProductSku(SKU, true);
		final Product product = getProduct(Collections.singletonMap(SKU, sku));
		final Store store = new StoreImpl();
		final SkuInventoryDetails skuInventoryDetails = createExistingInventoryDetails();

		context.checking(new Expectations() {
			{
				final Map<String, SkuInventoryDetails> skuInventoryMap = Collections.singletonMap(SKU, skuInventoryDetails);
				oneOf(productInventoryShoppingService).getSkuInventoryDetailsForAllSkus(product, store);
				will(returnValue(skuInventoryMap));

				oneOf(availabilityService).isProductAvailable(product, skuInventoryMap, true);
				will(returnValue(true));
				oneOf(availabilityService).isProductDisplayable(product, store, skuInventoryMap, true);
				will(returnValue(false));

				oneOf(availabilityStrategy).getAvailability(product, true, false, false);
				will(returnValue(null));
			}
		});

		StoreProduct storeProduct = storeProductServiceImpl.getProductForStore(product, store);
		assertNull("The availability is null when no appropriate strategy found", storeProduct.getAvailability());
	}
	
	//=================================================================================================
	// Data setup methods
	//=================================================================================================
	
	private void assertIndexProductsWithoutSkus(final Collection<Long> productUids, final Collection<IndexProduct> retrievedIndexProducts) {
		assertEquals("IndexProduct count is incorrect: ", retrievedIndexProducts.size(), retrievedIndexProducts.size());

		for (IndexProduct retrievedIndexProduct : retrievedIndexProducts) {
			Long retrievedIndexProductUid = retrievedIndexProduct.getUidPk();
			assertTrue("IndexProduct uid is missing: " + retrievedIndexProductUid, productUids.contains(retrievedIndexProductUid));
			assertFalse("Product should not be available.", retrievedIndexProduct.isAvailable(TEST_STORE_CODE));
			assertFalse("Product should not be displayable.", retrievedIndexProduct.isDisplayable(TEST_STORE_CODE));
		}
	}

	private void assertIndexProductsWithSkus(final Collection<Long> productUids, final Collection<IndexProduct> retrievedIndexProducts) {
		assertEquals("IndexProduct count is incorrect: ", retrievedIndexProducts.size(), retrievedIndexProducts.size());

		for (IndexProduct retrievedIndexProduct : retrievedIndexProducts) {
			Long retrievedIndexProductUid = retrievedIndexProduct.getUidPk();
			assertTrue("IndexProduct uid is missing: " + retrievedIndexProductUid, productUids.contains(retrievedIndexProductUid));
			assertTrue("Product should be available.", retrievedIndexProduct.isAvailable(TEST_STORE_CODE));
			assertTrue("Product should be displayable.", retrievedIndexProduct.isDisplayable(TEST_STORE_CODE));
		}
	}

	private Map<String, SkuInventoryDetails> createDefaultSkuInventoryDetails(final SkuInventoryDetails defaultInventoryDetails) {
		Map<String, SkuInventoryDetails> returnMap = new HashMap<String, SkuInventoryDetails>();
		returnMap.put("ABC", defaultInventoryDetails);
		return returnMap;
	}

	private SkuInventoryDetails createDefaultInventoryDetails() {
		return new SkuInventoryDetails();
	}

	private SkuInventoryDetails createExistingInventoryDetails() {
		SkuInventoryDetails validSkuInventoryDetails = new SkuInventoryDetails();
		validSkuInventoryDetails.setHasSufficientUnallocatedQty(true);
		validSkuInventoryDetails.setStockDate(new Date());
		validSkuInventoryDetails.setAvailableQuantityInStock(1);
		return validSkuInventoryDetails;
	}

	private List<Store> createTestStores() {
		final List<Store> stores = new ArrayList<Store>();
		Store store = createTestStore();
		stores.add(store);
		return stores;
	}

	private Store createTestStore() {
		Store store = new StoreImpl();
		store.setCode(TEST_STORE_CODE);
		store.setCatalog(createDefaultCatalog());
		return store;
	}

	private Collection<Long> createProductUids() {
		Collection<String> productTextUids = new HashSet<String>();
		productTextUids.add("1234");
		productTextUids.add("2345");
		productTextUids.add("3456");

		final Collection<Long> productUids = new HashSet<Long>();
		for (String productTextUid : productTextUids) {
			productUids.add(new Long(productTextUid));
		}

		return productUids;
	}

	private List<Product> createProductsWithoutSkus(final Collection<Long> productUids) {

		final List<Product> products = new ArrayList<Product>();
		for (Long productUid : productUids) {
			Product newProduct = getProduct();
			newProduct.setUidPk(productUid);
			newProduct.setProductSkus(new HashMap<String, ProductSku>());
			newProduct.setCategoryAsDefault(createDefaultCategory());
			products.add(newProduct);
		}

		return products;
	}

	private Category createDefaultCategory() {
		Category category = new CategoryImpl();
		category.setCatalog(createDefaultCatalog());
		return category;
	}


	private Catalog createDefaultCatalog() {
		Catalog catalog = new CatalogImpl();
		catalog.setUidPk(DEFAULT_CATALOG_UIDPK);
		return catalog;
	}

	private List<Product> createProductsWithSkus(final Collection<Long> productUids, final int skusPerProduct) {
		final List<Product> products = createProductsWithoutSkus(productUids);
		for (Product product : products) {
			Map<String, ProductSku> productSkus = new HashMap<String, ProductSku>();
			for (int i = 0; i < skusPerProduct; i++) {
				String productSkuCode = "testSkuCode" + i;
				ProductSku productSku = getProductSku();
				productSkus.put(productSkuCode, productSku);
			}

			product.setProductSkus(productSkus);
		}
		return products;
	}

	private ProductSku getProductSku(final String code,
			final boolean isWithinDateRange) {
		ProductSku productSku = new ProductSkuImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isWithinDateRange(final Date currentDate) {
				return isWithinDateRange;
			}
		};
		productSku.setSkuCode(code);

		return productSku;
	}
	private Product getProduct(final Map<String, ProductSku> skus) {
		Product product = new ProductImpl();
		product.setStartDate(new Date(System.currentTimeMillis() - 1L));
		product.setProductSkus(skus);

		return product;
	}

	private Product getProduct() {
		final Product product = new ProductImpl();
		product.initialize();
		product.setDefaultSku(getProductSku());
		return product;
	}

	private ProductSku getProductSku() {
		final ProductSku productSku = new ProductSkuImpl();
		productSku.initialize();
		return productSku;
	}

}
