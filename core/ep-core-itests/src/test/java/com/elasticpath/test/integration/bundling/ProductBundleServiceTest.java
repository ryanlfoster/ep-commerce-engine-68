package com.elasticpath.test.integration.bundling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.CyclicBundleException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductLoadTunerImpl;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.persistence.dao.ProductBundleDao;
import com.elasticpath.service.catalog.BundleConstituentFactory;
import com.elasticpath.service.catalog.ProductBundleService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

/**
 * Test that product bundles can function like a product using the Product Service.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class ProductBundleServiceTest extends BasicSpringContextTest {
	private static final String COULD_NOT_RETRIEVE_NEWLY_ADDED_CONSTITUENTS = "Could not retrieve newly added constituents";

	private static final String TESTTYPE = "TESTTYPE";

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductBundleService productBundleService;

	@Autowired
	private BundleConstituentFactory constituentFactory;

	private CatalogTestPersister catalogPersister;

	private SimpleStoreScenario scenario;
	private Product constituent1;
	private ProductBundle bundle1;
	private Product constituent2;
	private ProductBundle bundle2;

	/**
	 * A setup for the integration test.
	 */
	@Before
	public void initialize() {
		catalogPersister = getTac().getPersistersFactory().getCatalogTestPersister();
		scenario = getTac().useScenario(SimpleStoreScenario.class);
	}

	private void setUpNestedBundleConstituents() {
		constituent1 = generateSimpleProduct(TESTTYPE, "constituent1");
		constituent1 = productService.saveOrUpdate(constituent1);

		bundle1 = generateSimpleProductBundle(TESTTYPE, "bundle1");
		bundle1.addConstituent(createDefaultConstituent(constituent1));
		bundle1 = (ProductBundle) productService.saveOrUpdate(bundle1);

		constituent2 = generateSimpleProduct(TESTTYPE, "constituent2");
		constituent2 = productService.saveOrUpdate(constituent2);

		bundle2 = generateSimpleProductBundle(TESTTYPE, "bundle2");
		bundle2.addConstituent(createDefaultConstituent(bundle1));
		bundle2.addConstituent(createDefaultConstituent(constituent2));
		bundle2 = (ProductBundle) productService.saveOrUpdate(bundle2);
	}

	/**
	 * Creates a bundle constituent with the quantity of 1.
	 *
	 * @param product
	 * @return bundle constituent
	 */
	private BundleConstituent createDefaultConstituent(final Product product) {
		return constituentFactory.createBundleConstituent(product, 1);
	}

	/**
	 * Tests cyclic dependencies.
	 * For instance:
	 * There are 2 bundles:
	 * bundle1 containing bundle2 as a constituent and
	 * bundle3 containing bundle4 as a constituent.
	 *
	 * If we add bundle3 as a constituent to bundle2 and
	 * bundle1 as a constituent to bundle4 we will create a cyclic dependency:
	 * bundle1->bundle2->bundle3->bundle4->bundle1 ...
	 *
	 * The system should prevent such a structure.
	 */
	@DirtiesDatabase
	@Test(expected=CyclicBundleException.class)
	public void testBundleContainingItself() {
		ProductBundle bundle1 = generateSimpleProductBundle(TESTTYPE);
		final String guid1 = bundle1.getGuid();
		productService.saveOrUpdate(bundle1);
		
		bundle1 = productBundleService.findByGuid(guid1);
		
		ProductBundle bundle2 = generateSimpleProductBundle(TESTTYPE);
		final String guid2 = bundle2.getGuid();
		productService.saveOrUpdate(bundle2);
		bundle2 = productBundleService.findByGuid(guid2);

		ProductBundle bundle3 = generateSimpleProductBundle(TESTTYPE);
		final String guid3 = bundle3.getGuid();
		productService.saveOrUpdate(bundle3);
		bundle3 = productBundleService.findByGuid(guid3);
		
		ProductBundle bundle4 = generateSimpleProductBundle(TESTTYPE);
		final String guid4 = bundle4.getGuid();
		bundle4 = productBundleService.findByGuid(guid4);
		
		bundle1.addConstituent(createDefaultConstituent(bundle2));
		bundle3.addConstituent(createDefaultConstituent(bundle4));

		productService.saveOrUpdate(bundle1);
		productService.saveOrUpdate(bundle3);

		bundle1 = productBundleService.findByGuid(guid1);
		bundle3 = productBundleService.findByGuid(guid3);

		bundle2.addConstituent(createDefaultConstituent(bundle3));
		bundle4.addConstituent(createDefaultConstituent(bundle1));

		productService.saveOrUpdate(bundle2);
		productService.saveOrUpdate(bundle4);
	}

	/**
	 * Tests that cyclic dependency is detected before sending request to JPA.
	 * See {@link #testBundleContainingItself} comment regarding cyclic dependencies.
	 */
	@DirtiesDatabase
	@Test(expected = CyclicBundleException.class)
	public void testCyclicDependencyExceptionIsThrown() {
		final ProductBundle bundle1 = generateSimpleProductBundle(TESTTYPE);
		final ProductBundle savedBundle1 = (ProductBundle) productService.saveOrUpdate(bundle1);

		final ProductBundle bundle2 = generateSimpleProductBundle(TESTTYPE);
		final ProductBundle savedBundle2 = (ProductBundle) productService.saveOrUpdate(bundle2);

		savedBundle1.addConstituent(createDefaultConstituent(savedBundle2));
		savedBundle2.addConstituent(createDefaultConstituent(savedBundle1));

		productService.saveOrUpdate(savedBundle1);
		productService.saveOrUpdate(savedBundle2);
	}

	/**
	 * Tests saving and retrieving a simple product bundle.
	 * <p/>
	 * Should be able to use the ProductService to do CRUD operations as a product bundle IS a product.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveProductBundle() {
		final ProductBundle bundle = generateSimpleProductBundle(TESTTYPE);
		productService.saveOrUpdate(bundle);

		final ProductBundle retrieved = productBundleService.findByGuid(bundle.getGuid());

		assertNotNull("Could not retrieve newly added ProductBundle", retrieved);
	}

	/**
	 * Tests retrieving a simple product bundle from {@link ProductService} after saving.
	 * <p/>
	 * Should be able to use the ProductService to do CRUD operations as a product bundle IS a product.
	 */
	@DirtiesDatabase
	@Test
	public void testRetrievingSavedProductBundleFromProductService() {		
		final Product product = generateSimpleProduct(TESTTYPE);
		productService.saveOrUpdate(product);
		
		final ProductBundle bundle = generateSimpleProductBundle(TESTTYPE);
		bundle.addConstituent(createDefaultConstituent(product));
		productService.saveOrUpdate(bundle);

		final ProductBundle retrieved = productBundleService.findByGuid(bundle.getGuid());
		final Product retrievedAsProduct = productService.findByGuid(bundle.getGuid());

		assertEquals("Bundle != Bundle as a Product", retrieved.getGuid(), retrievedAsProduct.getGuid());
		assertEquals("Bundle should have one constituent product", 1, retrieved.getConstituents().size());
		assertEquals("Product (bundle) should have one constituent product", 1, ((ProductBundle) retrievedAsProduct).getConstituents().size());
	}

	/**
	 * Tests retrieving a product bundles and products through {@link ProductService} after saving.
	 * <p/>
	 * Should be able to use the ProductService to do CRUD operations as a product bundle IS a product.
	 */
	@DirtiesDatabase
	@Test
	public void testEqualityOfGetProductBundlesAndGetProducts() {
		ProductBundle bundle = generateSimpleProductBundle(TESTTYPE);
		bundle = (ProductBundle) productService.saveOrUpdate(bundle);

		final Product product = generateSimpleProduct(TESTTYPE);
		productService.saveOrUpdate(product);
		bundle.addConstituent(createDefaultConstituent(product));

		productService.saveOrUpdate(bundle);

		final List<ProductBundle> bundles = productBundleService.getProductBundles();
		final List<Long> products = productService.findAllUids();

		assertEquals("Products should contain both bundle and product", 2, products.size());
		assertEquals("Bundles should contain only one bundle", 1, bundles.size());
	}

	/**
	 * Tests saving and retrieving a product bundle with two constituents that are the same product.
	 * <p/>
	 * Should be able to use the ProductService to do CRUD operations as a product bundle IS a product. Should be able to cast a Product to a
	 * ProductBundle.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveProductBundleWithTwoSameProductConstituent() {
		final ProductBundle bundle = generateSimpleProductBundle(TESTTYPE);
		Product product = generateSimpleProduct(TESTTYPE);
		product = productService.saveOrUpdate(product);

		bundle.addConstituent(createDefaultConstituent(product));
		bundle.addConstituent(createDefaultConstituent(product));
		productService.saveOrUpdate(bundle);

		final ProductBundle retrieved = productBundleService.findByGuid(bundle.getGuid());
		assertNotNull("Could not retrieve newly added Product bundle", retrieved);
		assertFalse(COULD_NOT_RETRIEVE_NEWLY_ADDED_CONSTITUENTS, retrieved.getConstituents().isEmpty());
		assertEquals("Failed to add multiple items of one product as constituent to ProductBundle", 2, retrieved.getConstituents().size());
	}

	/**
	 * Tests saving and retrieving a product bundle with constituents.
	 * <p/>
	 * Should be able to use the ProductService to do CRUD operations as a product bundle IS a product. Should be able to cast a Product to a
	 * ProductBundle.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveProductBundleWithConstituent() {
		final ProductBundle bundle = generateSimpleProductBundle(TESTTYPE);

		final Product product = generateSimpleProduct(TESTTYPE);
		productService.saveOrUpdate(product);
		bundle.addConstituent(createDefaultConstituent(product));

		productService.saveOrUpdate(bundle);
		final ProductBundle retrieved = productBundleService.findByGuid(bundle.getGuid());
		assertNotNull("Could not retrieve newly added Product bundle", retrieved);
		assertFalse(COULD_NOT_RETRIEVE_NEWLY_ADDED_CONSTITUENTS, retrieved.getConstituents().isEmpty());

		final Product retrievedAsProduct = productService.findByGuid(bundle.getGuid());
		assertNotNull("Could not retrieve ProductBundle as a Product", retrievedAsProduct);

		assertEquals("Bundle != Bundle as a Product", retrieved.getUidPk(), retrievedAsProduct.getUidPk());

		final ProductBundle retrievedAsProductCastToBundle = (ProductBundle) retrievedAsProduct;

		assertEquals("dynamic cast of retrievedAsProduct to ProductBundle does not equal retrieved ProductBundle", retrieved,
				retrievedAsProductCastToBundle);
		assertEquals("Product cast to ProductBundle should contain the constituent", retrievedAsProductCastToBundle.getConstituents().size(), 1);

		assertNull("Should not be able to retrieve a base class Product as a ProductBundle", productBundleService.findByGuid(product.getGuid()));
		assertNotNull("Should be able to retrieve a base class Product as a Product", productService.findByGuid(product.getGuid()));

		final Product product2 = generateSimpleProduct(TESTTYPE);
		productService.saveOrUpdate(product2);
		retrieved.addConstituent(createDefaultConstituent(product2));
		assertNotNull("Failed to saveOrUpdate ProductBundle", productService.saveOrUpdate(retrieved));
		final ProductBundle updatedBundle = productBundleService.findByGuid(retrieved.getGuid());
		assertEquals("updated ProductBundle != original ProductBundle", retrieved.getUidPk(), updatedBundle.getUidPk());
		assertEquals("Failed to add additional constituent to ProductBundle", 2, updatedBundle.getConstituents().size());

		assertNull("Should not be able to retrieve a base class Product as a ProductBundle", productBundleService.findByGuid(product2.getGuid()));
	}

	/**
	 * Find a ProductBundle by constituent guid.
	 */
	@DirtiesDatabase
	@Test
	public void testFindProductBundleByConstituentGuid() {
		final ProductBundle bundle = generateSimpleProductBundle(TESTTYPE);

		final Product product = generateSimpleProduct(TESTTYPE);
		productService.saveOrUpdate(product);
		final BundleConstituent defaultConstituent = createDefaultConstituent(product);
		bundle.addConstituent(defaultConstituent);

		productService.saveOrUpdate(bundle);

		final ProductBundleDao dao = getBeanFactory().getBean("productBundleDao");
		final ProductBundle foundBundle = dao.findByBundleConstituentGuid(defaultConstituent.getGuid());
		assertNotNull(foundBundle);
		assertEquals("Should have one constituent", 1, foundBundle.getConstituents().size());
		assertEquals("Found bundle should have constituent with same code",
				defaultConstituent.getGuid(), foundBundle.getConstituents().get(0).getGuid());

	}

	/**
	 * Tests saving and retrieving a product bundle with nested bundles.
	 */
	@DirtiesDatabase
	@Test
	public void testProductBundleWithNestedBundles() {
		final ProductBundle bundle = generateSimpleProductBundle(TESTTYPE);

		final Product product = generateSimpleProduct(TESTTYPE);
		productService.saveOrUpdate(product);
		final Product product2 = generateSimpleProduct(TESTTYPE);
		productService.saveOrUpdate(product2);
		final Product product3 = generateSimpleProduct(TESTTYPE);
		productService.saveOrUpdate(product3);

		bundle.addConstituent(createDefaultConstituent(product));
		bundle.addConstituent(createDefaultConstituent(product2));
		bundle.addConstituent(createDefaultConstituent(product3));
		productService.saveOrUpdate(bundle);

		final ProductBundle retrieved = productBundleService.findByGuid(bundle.getGuid());
		assertNotNull("Could not retrieve newly added Product bundle", retrieved);
		assertFalse(COULD_NOT_RETRIEVE_NEWLY_ADDED_CONSTITUENTS, retrieved.getConstituents().isEmpty());

		final Product retrievedAsProduct = productService.findByGuid(bundle.getGuid());
		assertNotNull("Could not retrieve ProductBundle as a Product", retrievedAsProduct);

		assertEquals("Bundle != Bundle as a Product", retrieved.getUidPk(), retrievedAsProduct.getUidPk());

		final ProductBundle bundle2 = generateSimpleProductBundle(TESTTYPE);
		final Product product4 = generateSimpleProduct(TESTTYPE);
		productService.saveOrUpdate(product4);
		bundle2.addConstituent(createDefaultConstituent(product4));
		bundle2.addConstituent(createDefaultConstituent(retrieved)); // nested bundle!
		productService.saveOrUpdate(bundle2);
		final ProductBundle compositeBundle = productBundleService.findByGuid(bundle2.getGuid());
		assertNotNull("Could not retrieve newly added nested ProductBundle", compositeBundle);
		assertFalse(COULD_NOT_RETRIEVE_NEWLY_ADDED_CONSTITUENTS, compositeBundle.getConstituents().isEmpty());
		final ProductBundle nestedBundle = productBundleService.findByGuid(
				compositeBundle.getConstituents().get(1).getConstituent().getProduct().getGuid());
		assertNotNull("Could not retrieve constituent as ProductBundle", nestedBundle);
		assertEquals("nested bundle should have 3 constituents", nestedBundle.getConstituents().size(), Integer.parseInt("3"));
		assertEquals("nested bundld is not original bundle", nestedBundle, retrieved);
	}

	/**
	 * Tests removing constituents from a product bundle.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveConstituents() {
		final ProductBundle bundle = generateSimpleProductBundle(TESTTYPE);
		final Product product = generateSimpleProduct(TESTTYPE);
		productService.saveOrUpdate(product);
		bundle.addConstituent(createDefaultConstituent(product));
		productService.saveOrUpdate(bundle);
		final ProductBundle retrieved = productBundleService.findByGuid(bundle.getGuid());
		final List<BundleConstituent> removals = new LinkedList<BundleConstituent>();
		removals.addAll(retrieved.getConstituents());
		for (final BundleConstituent cons : removals) {
			retrieved.removeConstituent(cons);
		}
		productService.saveOrUpdate(retrieved);

		final ProductBundle retrievedAfterUpdate = productBundleService.findByGuid(bundle.getGuid());
		assertTrue("Failed to delete constituents from ProductBundle", retrievedAfterUpdate.getConstituents().isEmpty());
	}

	/**
	 * Test deleting a product bundle with the ProductService.
	 */
	@DirtiesDatabase
	@Test
	public void testDeleteBundle() {
		final ProductBundle bundle = generateSimpleProductBundle(TESTTYPE);
		Product product = generateSimpleProduct(TESTTYPE);
		product = productService.saveOrUpdate(product);
		bundle.addConstituent(createDefaultConstituent(product));
		productService.saveOrUpdate(bundle);

		assertNotNull("Failed to create ProductBundle", productBundleService.findByGuid(bundle.getGuid()));
		productBundleService.removeProductTree(bundle.getUidPk());
		assertNull("Failed to delete ProductBundle as a Product", productBundleService.findByGuid(bundle.getGuid()));

		assertNotNull("Bundle constituent deleted when bundle deleted", productService.findByGuid(product.getGuid()));
	}

	/**
	 * Test bundle hiding (dependent on constituent hiding).
	 */
	@DirtiesDatabase
	@Test
	public void testHiddenBundle() {
		ProductBundle bundle = generateSimpleProductBundle(TESTTYPE);
		Product product = generateSimpleProduct(TESTTYPE);
		
		product.setHidden(true);
		product = productService.saveOrUpdate(product);
		
		bundle.addConstituent(createDefaultConstituent(product));
		bundle = (ProductBundle) productService.saveOrUpdate(bundle);

		ProductBundle retrieved = productBundleService.findByGuid(bundle.getGuid());
		assertTrue("Bundle should be hidden because its constituent is hidden", retrieved.isHidden());

		product.setHidden(false);
		product = productService.saveOrUpdate(product);

		retrieved = productBundleService.findByGuid(bundle.getGuid());
		assertFalse("Bundle should be not hidden because the constituent is no longer hidden", retrieved.isHidden());
		final Product retrievedAsProduct = productService.findByGuid(bundle.getGuid());
		assertFalse("Bundle retrieved as product should be not hidden because the constituent is no longer hidden", retrievedAsProduct.isHidden());

		retrieved.setHidden(true);
		bundle = (ProductBundle) productService.saveOrUpdate(retrieved);

		retrieved = productBundleService.findByGuid(bundle.getGuid());
		assertTrue("Bundle should now be hidden", retrieved.isHidden());
	}

	/**
	 * Test bundle min order qty (dependent on constituent min order qty).
	 */
	@DirtiesDatabase
	@Test
	public void testBundleMinOrderQty() {
		final int five = 5;
		final int three = 3;
		final int four = 4;
		final int two = 2;
		final int one = 1;

		final ProductBundle bundle1 = generateSimpleProductBundle(TESTTYPE, "B1");
		final ProductBundle bundle2 = generateSimpleProductBundle(TESTTYPE, "B2");
		bundle1.setMinOrderQty(three);
		bundle2.setMinOrderQty(four);
		final Product product = generateSimpleProduct(TESTTYPE);
		product.setMinOrderQty(five);
		productService.saveOrUpdate(product);
		bundle1.addConstituent(createDefaultConstituent(product));
		productService.saveOrUpdate(bundle1);

		ProductBundle retrieved = productBundleService.findByGuid(bundle1.getGuid());
		assertEquals("Bundle min order qty should be 1", one, retrieved.getMinOrderQty());

		product.setMinOrderQty(two);
		productService.saveOrUpdate(product);

		retrieved = productBundleService.findByGuid(bundle1.getGuid());
		assertEquals("Bundle min order qty should be 1", one, retrieved.getMinOrderQty());

		retrieved.setMinOrderQty(one);
		retrieved = (ProductBundle) productService.saveOrUpdate(retrieved);

		retrieved = productBundleService.findByGuid(bundle1.getGuid());
		assertEquals("Bundle min order qty should be 1", one, retrieved.getMinOrderQty());

		// nested case
		retrieved.addConstituent(createDefaultConstituent(bundle2));
		productService.saveOrUpdate(retrieved);

		retrieved = productBundleService.findByGuid(bundle1.getGuid());
		assertEquals("Bundle min order qty should be 1", one, retrieved.getMinOrderQty());
	}

	/**
	 * Test start and end date of a bundle (dependent on constituent start and end date).
	 */
	@DirtiesDatabase
	@Test
	public void testStartEndDate() {
		final long delta = 5000;
		final long endDelta = 1000;
		final Date now = new Date();
		final ProductBundle bundle = generateSimpleProductBundle(TESTTYPE);
		assertNull("Bundle end date should not be set", bundle.getEndDate());

		final Product constituent = generateSimpleProduct(TESTTYPE);
		constituent.setStartDate(now);
		constituent.setEndDate(new Date(now.getTime() + endDelta));
		productService.saveOrUpdate(constituent);
		bundle.addConstituent(createDefaultConstituent(constituent));

		assertNotNull("Bundle end date should not be null, even though we didn't set it directly", bundle.getEndDate());
		assertEquals("Bundle should get the end date from the constituent", bundle.getEndDate(), constituent.getEndDate());

		final Date later = new Date(now.getTime() + delta);
		bundle.setStartDate(later);
		final Date bundleRootEndDate = new Date(later.getTime() + endDelta);
		bundle.setEndDate(bundleRootEndDate);
		productService.saveOrUpdate(bundle);

		final Product retrieved = productService.findByGuid(bundle.getGuid());
		assertTrue("Bundle should start after the constituent", retrieved.getStartDate().after(constituent.getStartDate()));
		assertTrue("Bundle should end at the same time as the constituent", !retrieved.getEndDate().before(constituent.getEndDate())
				&& !retrieved.getEndDate().after(constituent.getEndDate()));

		final Date evenLater = new Date(later.getTime() + delta);
		constituent.setStartDate(evenLater);
		constituent.setEndDate(new Date(evenLater.getTime() + endDelta));
		productService.saveOrUpdate(constituent);

		ProductBundle retrievedBundle = productBundleService.findByGuid(bundle.getGuid());
		assertTrue("Bundle should start at the same time as the constituent", !retrievedBundle.getStartDate().before(constituent.getStartDate())
				&& !retrievedBundle.getStartDate().after(constituent.getStartDate()));
		assertTrue("Bundle should end before the constituent", retrievedBundle.getEndDate().before(constituent.getEndDate()));

		// constituent.setStartDate(null); column is not nullable!
		constituent.setEndDate(null);
		productService.saveOrUpdate(constituent);
		retrievedBundle = productBundleService.findByGuid(bundle.getGuid());
		assertEquals("Bundle should not get start time from constituent, since it is null", bundle.getEndDate(), bundleRootEndDate);
	}

	private ProductBundle generateSimpleProductBundle(final String productType) {
		return generateSimpleProductBundle(productType, Utils.uniqueCode("bundle"));
	}

	private ProductBundle generateSimpleProductBundle(final String productType, final String bundleCode) {
		ProductBundle bundle = catalogPersister.createSimpleProductBundle(productType, bundleCode, scenario.getCatalog(), 
				scenario.getCategory(), getTac().getPersistersFactory().getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS));
		addDefaultSku(bundle, bundleCode);
		return bundle;
	}

	private Product generateSimpleProduct(final String productType) {
		return generateSimpleProduct(productType, Utils.uniqueCode("product"));
	}

	private Product generateSimpleProduct(final String productType, final String productCode) {
		Product product = catalogPersister.createSimpleProduct(productType, productCode, scenario.getCatalog(), 
				getTac().getPersistersFactory().getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS), scenario.getCategory());
		addDefaultSku(product, productCode);
		return product;
	}

	private void addDefaultSku(final Product product, final String skuCode) {
		final List<SkuOptionValue> optionValues = Collections.emptyList();
		ProductSku productSku = catalogPersister.createProductSku(skuCode, optionValues, new Date(), null, true);
		product.setDefaultSku(productSku);
	}

	/**
	 * Test find by guids with a product load tuner.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByGuidsWithLoadTuner() {
		setUpNestedBundleConstituents();

				// not loading it with a load tuner
		final List<ProductBundle> retrivedBundlesWithoutLoadTuner = productBundleService.findByGuids(Arrays.asList(bundle1.getGuid(), bundle2.getGuid()));
		assertEquals(2, retrivedBundlesWithoutLoadTuner.size());
		for (final ProductBundle retrivedBundleWithoutLoadTuner : retrivedBundlesWithoutLoadTuner) {
			assertNull(retrivedBundleWithoutLoadTuner.getAttributeValueMap());
		}

		final ProductLoadTuner loadTuner = new ProductLoadTunerImpl();
		loadTuner.setLoadingAttributeValue(true);

		// loading with a load tuner
		final List<ProductBundle> retrivedBundlesWithLoadTuner = productBundleService.findByGuids(Arrays.asList(bundle1.getGuid(), bundle2.getGuid()),
				loadTuner);
		assertEquals(2, retrivedBundlesWithLoadTuner.size());
		for (final ProductBundle retrivedBundleWithLoadTuner : retrivedBundlesWithLoadTuner) {
			assertNotNull(retrivedBundleWithLoadTuner.getAttributeValueMap());
		}
	}

	/**
	 * Test that finding bundle by product code returns the product bundles that directly contain the product.
	 */
	@DirtiesDatabase
	@Test
	public void testFindBundlesByProductCode() {
		setUpNestedBundleConstituents();

		assertEquals(1, productBundleService.findByProduct(constituent1.getCode()).size());
	}

	/**
	 * Test that finding bundle by product code returns all product bundles that contain the product even indirectly.
	 */
	@DirtiesDatabase
	@Test
	public void testFindAllBundlesByProductCode() {
		setUpNestedBundleConstituents();

		assertEquals(1, productBundleService.findProductBundlesContaining(constituent1).size());
		assertEquals(2, productBundleService.findAllProductBundlesContaining(constituent1).size());
	}


	@DirtiesDatabase
	@Test
	public void testFindBundleUidsByProduct() {
		setUpNestedBundleConstituents();

		assertEquals("Direct uid relationship should have been found",
				Collections.singleton(bundle1.getUidPk()),
				productBundleService.findProductBundleUidsContainingProduct(constituent1));
	}


	@DirtiesDatabase
	@Test
	public void testFindAllBundleUidsByProduct() {
		setUpNestedBundleConstituents();

		assertEquals("Direct and indirect relationships should have been found",
				new HashSet<Long>(Arrays.asList(bundle1.getUidPk(), bundle2.getUidPk())),
				productBundleService.findAllProductBundleUidsContainingProduct(constituent1));
	}

	/**
	 * Tests if bundle exists.
	 */
	@DirtiesDatabase
	@Test
	public void testBundleExists() {
	    final ProductBundle bundle = generateSimpleProductBundle(TESTTYPE);
	    productService.saveOrUpdate(bundle);
	    final String guid = bundle.getGuid();

        boolean result = productBundleService.bundleExistsWithGuid(guid);
        assertTrue("Bundle should exist", result);

        result = productBundleService.bundleExistsWithGuid("nonExistentGuid");
        assertFalse("Bundle should not exist", result);

	}
	
	/**
	 * Test finding a bundle uid by sku code.
	 */
	@DirtiesDatabase
	@Test
	public void testFindUidBySkuCode() {
	    final ProductBundle bundle = (ProductBundle) productService.saveOrUpdate(generateSimpleProductBundle(TESTTYPE));
		long bundleUid = productBundleService.findBundleUidBySkuCode(bundle.getDefaultSku().getSkuCode());
		assertEquals("The uidPk of the found bundle should match", bundle.getUidPk(), bundleUid);
	}

	/**
	 * Test finding a bundle uid by sku code for a base product should return null.
	 */
	@DirtiesDatabase
	@Test
	public void testFindUidBySkuCodeShouldNotFindProduct() {
		final Product bundle = productService.saveOrUpdate(generateSimpleProduct(TESTTYPE));
		Long bundleUid = productBundleService.findBundleUidBySkuCode(bundle.getDefaultSku().getSkuCode());
		assertNull("There should be no uid found for a non-bundle product", bundleUid);
	}


}