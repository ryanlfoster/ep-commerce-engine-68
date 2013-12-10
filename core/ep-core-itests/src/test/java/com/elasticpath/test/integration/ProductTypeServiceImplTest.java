package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.DuplicateKeyException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.ProductTypeService;
import com.elasticpath.service.tax.TaxCodeService;
import com.elasticpath.test.db.DbTestCase;

/**
 * An integration test for the GiftCertificateServiceImpl, we are testing from a client's point of view with Spring and the Database up and running.
 */
public class ProductTypeServiceImplTest extends DbTestCase {
	
	private static final String PRODUCT_TYPE_TEST_NAME = "Test Name";
	private static final String PRODUCT_TYPE_TEST_NAME_UPDATED = "Test Name Updated";
	private static final String PRODUCT_TYPE_TEST_GUID = "Test GUID";
	private static final String PRODUCT_TYPE_TEST_TEMPLATE = "Test Template";
	private static final String SKU_OPTION_KEY = "Test SKU Option";
	
	private static final String PRODUCT_TYPE_TAX_CODE = "NONE";

	/** The main object under test. */
	@Autowired
	private ProductTypeService service;

	@Autowired
	private AttributeService attributeService;
	
	@Autowired
	private CatalogService catalogService;
	
	/**
	 * Tests the creation of a ProductType.
	 */
	@DirtiesDatabase
	@Test
	public void testAdd() {
		ProductType productType = addProductType();
		assertNotNull("A valid ProductType reference should be returned.", productType);
	}
	
	/**
	 * Tests the failure of duplicate ProductType inserts.
	 */
	@DirtiesDatabase
	@Test
	public void testAddDuplicate() {
		// initial insert
		addProductType();
		
		try {
			// duplicate insert
			addProductType();
			
			fail("This duplicate ProductType insert should fail.");
			
		} catch (DuplicateKeyException e) {
			assertNotNull(e);
		}
	}
	
	/**
	 * Tests findProductType.
	 */
	@DirtiesDatabase
	@Test
	public void testFindProductType() {
		addProductType();
		
		ProductType productType = service.findProductType(PRODUCT_TYPE_TEST_NAME);
		assertNotNull("The newly created ProductType should be found.", productType);
	}
	
	/**
	 * Tests findAllProductTypeFromCatalog.
	 */
	@DirtiesDatabase
	@Test
	public void testFindAllProductTypeFromCatalog() {
		ProductType productType = addProductType();
		
		List<ProductType> productTypes = service.findAllProductTypeFromCatalog(productType.getCatalog().getUidPk());
		assertNotNull(productTypes);
		assertEquals("Only the one newly created ProductType should be found.", productTypes.size(), 1);
	}
	
	/**
	 * Tests isInUse.
	 */
	@DirtiesDatabase
	@Test
	public void testIsInUse() {
		// add a product to use a product type
		Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(
				scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse());
		
		assertFalse("A non-existent entry cannot be in use.", service.isInUse(-1));
		assertTrue("The newly created product and type must be found.", service.isInUse(product.getProductType().getUidPk()));
	}
	
	/**
	 * Tests listUsedUids.
	 */
	@DirtiesDatabase
	@Test
	public void testListUsedUids() {
		// add a product to use a product type
		persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(
				scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse());
		
		List<Long> list = service.listUsedUids();
		
		assertNotNull("The list method may not return null.", list);
		assertTrue("The number of matches found must be at least one.", list.size() >= 1);
	}
	
	/**
	 * Tests the {@link com.elasticpath.domain.catalog.ProductLoadTuner} used with in the initialize method.
	 */
	@DirtiesDatabase
	@Test
	public void testInitialize() {
		// create a product type
		ProductType productType = persisterFactory.getCatalogTestPersister().persistDefaultSingleSkuProductType(scenario.getCatalog());
		
		// create product and SKU attributes
		addProductAttribute(productType);
		addSkuAttribute(productType);
		
		// flush
		service.update(productType);
		
		// reset what initialize() should populate
		// product attributes
		productType.setProductAttributeGroupAttributes(null);
		assertNull(productType.getProductAttributeGroupAttributes());
		
		// SKU attributes
		productType.setSkuAttributeGroup(null);
		assertNotNull("A new group is created internally if null.", productType.getSkuAttributeGroup());
		assertNull("Product attributes should be null.", productType.getSkuAttributeGroup().getAttributeGroupAttributes()); 
		
		// reload using the initialize load tuner
		productType = service.initialize(productType);
		
		// check that the product and SKU attributes have been reloaded
		assertNotNull("Initialized product attributes may not be null.", productType.getProductAttributeGroupAttributes());
		assertFalse("Initialized product attributes may not be empty.", productType.getProductAttributeGroupAttributes().isEmpty());
		
		assertNotNull("Initialized SKU attribute group may not be null.", productType.getSkuAttributeGroup());
		assertFalse("Initialized SKU attributes may not be empty.", productType.getSkuAttributeGroup().getAttributeGroupAttributes().isEmpty());
	}
	
	/**
	 * Tests the update of a ProductType instance.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdate() {
		addProductType();
		
		ProductType productType = service.findProductType(PRODUCT_TYPE_TEST_NAME);
		assertNotNull(productType);
		
		productType.setName(PRODUCT_TYPE_TEST_NAME_UPDATED);
		ProductType result = service.update(productType);
		
		assertNotSame("Expected update() to not return the same reference.", productType, result);
		
		ProductType oldProductType = service.findProductType(PRODUCT_TYPE_TEST_NAME);
		assertNull("Name has been changed, thus no longer present.", oldProductType);
		
		ProductType newProductType = service.findProductType(PRODUCT_TYPE_TEST_NAME_UPDATED);
		assertNotNull("Should be found by newly updated name.", newProductType);
	}
	
	/**
	 * Tests the update of a ProductType instance with a new product attribute.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateAddProductAttribute() {
		// create product type
		ProductType productType = persisterFactory.getCatalogTestPersister().persistDefaultSingleSkuProductType(scenario.getCatalog());
		assertNotNull(productType);
		
		// add a product attribute
		addProductAttribute(productType);
		
		// flush: persist the product type and attribute
		ProductType result = service.update(productType);
		assertNotSame("Update should not return the same reference.", productType, result);
		
		assertNotNull("Product attribute group should not be null.", result.getProductAttributeGroup());
		assertNotNull("Product attributes group should not be null.", result.getProductAttributeGroup().getAttributeGroupAttributes());
		assertFalse("Product attributes group should not be empty.", result.getProductAttributeGroup().getAttributeGroupAttributes().isEmpty());
	}
	
	/**
	 * Tests the update of a ProductType instance with a new SKU attribute.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateAddSkuAttribute() {
		// create product type
		ProductType productType = addProductType();
		assertNotNull(productType);
		
		// add a SKU attribute
		addSkuAttribute(productType);
		
		// flush: persist the product type and attribute
		ProductType result = service.update(productType);
		
		// *not* the same reference
		assertNotSame(productType, result);
		
		assertNotNull(result.getSkuAttributeGroup());
		assertNotNull(result.getSkuAttributeGroup().getAttributeGroupAttributes());
		assertFalse(result.getSkuAttributeGroup().getAttributeGroupAttributes().isEmpty());
	}
	
	/**
	 * Tests the update of a ProductType instance with a new SKU option.
	 */
	@DirtiesDatabase
	@Test
	public void testUpdateAddSkuOption() {
		addProductType();
		
		// get the newly created product type
		ProductType productType = service.findProductType(PRODUCT_TYPE_TEST_NAME);
		assertNotNull(productType);
		
		// get all available catalogs in the DB
		List<Catalog> catalogs = catalogService.findAllCatalogs();
		assertFalse(catalogs.isEmpty());
		
		// create a SKU attribute
		SkuOption skuOption = getBeanFactory().getBean(ContextIdNames.SKU_OPTION);
		skuOption.initialize();
		skuOption.setCatalog(catalogs.get(0));
		skuOption.setOptionKey(SKU_OPTION_KEY);
		
		// add the SKU option to the product type
		assertTrue(productType.getSkuOptions().isEmpty());
		productType.addOrUpdateSkuOption(skuOption);
		
		// persist the product type and attribute
		ProductType result = service.update(productType);
		
		// not the same reference
		assertNotSame(productType, result);
		
		assertNotNull(result.getSkuOptions());
		assertFalse(result.getSkuOptions().isEmpty());
	}
	
	/**
	 * Tests the removal of a ProductType.
	 */
	@DirtiesDatabase
	@Test
	public void testRemove() {
		addProductType();
		
		ProductType productType = service.findProductType(PRODUCT_TYPE_TEST_NAME);
		assertNotNull(productType);
		
		service.remove(productType);
		
		// not longer present
		ProductType oldProductType = service.findProductType(PRODUCT_TYPE_TEST_NAME);
		assertNull(oldProductType);
	}
	
	/**
	 * Tests the listing of all product types.
	 */
	@DirtiesDatabase
	@Test
	public void testList() {
		addProductType("Name 1", "GUID 1");
		addProductType("Name 2", "GUID 2");
		
		List<ProductType> list = service.list();
		assertNotNull(list);
		assertEquals(2, list.size());
	}

	/**
	 * Test the behavior of find by sku code.
	 */
	@DirtiesDatabase
	@Test
	public void testFindBySkuCode() {
		Product product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(
				scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse());
		
		String skuCode = product.getDefaultSku().getSkuCode();
		ProductType productType = service.findBySkuCode(skuCode);
		assertEquals("The product type found should be the one beloning to the product", product.getProductType(), productType);
	}

	//-------------------------- Helper Methods -------------------------------//
	
	/**
	 * Adds a test ProductType to the database.
	 */
	public ProductType addProductType() {
		return addProductType(PRODUCT_TYPE_TEST_NAME, PRODUCT_TYPE_TEST_GUID);
	}

	/**
	 * Adds a test ProductType to the database.
	 */
	public ProductType addProductType(final String productTypeName, final String guid) {
		// get all available catalogs in the DB
		List<Catalog> catalogs = catalogService.findAllCatalogs();
		assertFalse(catalogs.isEmpty());
		
		// get a tax code to assign to the product type
		TaxCodeService taxCodeService = getBeanFactory().getBean("taxCodeService");
		TaxCode taxCode = taxCodeService.findByCode(PRODUCT_TYPE_TAX_CODE);
		assertNotNull(taxCode);
		
		// create test product type
		ProductType productType = getBeanFactory().getBean(ContextIdNames.PRODUCT_TYPE);
		productType.initialize();
		
		productType.setName(productTypeName);
		productType.setGuid(guid);
		productType.setTemplate(PRODUCT_TYPE_TEST_TEMPLATE);
		productType.setCatalog(catalogs.get(0));
		productType.setTaxCode(taxCode);
		
		ProductType result = service.add(productType);
		assertSame(result, productType);
		assertTrue("ProductType should be persistent.", productType.isPersisted());
		
		return result;
	}
	
	/**
	 * Adds a product attribute to the ProductType.
	 * 
	 * @param productType The product type to add the product attribute to.
	 */
	public void addProductAttribute(final ProductType productType) {
		// list all product attributes
		List<Attribute> allProductAttributes = attributeService.getProductAttributes();
		assertNotNull(allProductAttributes);
		assertFalse(allProductAttributes.isEmpty());
		
		// just use the first available product attribute to create a group attribute
		final AttributeGroupAttribute productTypeAttribute = getBeanFactory().getBean(ContextIdNames.PRODUCT_TYPE_PRODUCT_ATTRIBUTE);
		productTypeAttribute.setAttribute(allProductAttributes.get(0));
		
		// create and associate the product attribute group to the new product type
		Set<AttributeGroupAttribute> productAttributes = new HashSet<AttributeGroupAttribute>();
		productAttributes.add(productTypeAttribute);
		productType.setProductAttributeGroupAttributes(productAttributes);
	}
	
	/**
	 * Adds a SKU attribute to the given ProductType.
	 * 
	 * @param productType The product type to add the SKU attribute to.
	 */
	private void addSkuAttribute(final ProductType productType) {
		// create a SKU attribute
		Attribute skuAttribute = getBeanFactory().getBean(ContextIdNames.ATTRIBUTE);
		skuAttribute.initialize();
		skuAttribute.setAttributeType(AttributeType.BOOLEAN);
		skuAttribute.setAttributeUsageId(AttributeUsage.SKU);
		
		attributeService.add(skuAttribute);
		
		// create a group attribute
		AttributeGroupAttribute groupAttribute = getBeanFactory().getBean(ContextIdNames.PRODUCT_TYPE_SKU_ATTRIBUTE);
		groupAttribute.setAttribute(skuAttribute);
		
		// create and associate the SKU attribute group to the new product type
		productType.getSkuAttributeGroup().addAttributeGroupAttribute(groupAttribute);
	}
	
}
