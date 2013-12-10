package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.UtilityImpl;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.BrandImpl;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.CatalogLocaleImpl;
import com.elasticpath.domain.catalog.impl.CategoryImpl;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.BrandLocalizedPropertyValueImpl;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.test.jmock.AbstractEPServiceTestCase;

/**
 * Tests the generation of product data in xml format.
 */
public class ProductXmlServiceImplTest extends AbstractEPServiceTestCase {

	private static final long PRODUCT_UID = 1234L;
	private static final String PRODUCT_NAME = "TestProdDisplayName";
	private static final String ATTRIBUTE_KEY_DESCRIPTION = "description";
	private static final String PRODUCT_DESCRIPTION = "TestProdDescription";
	private static final String PRODUCT_BRAND_NAME = "TestBrandName";
	private static final String SF_BASE_URL = "http://www.testbaseurl.com/epsf";
	private static final String SPECIAL_CHARS = "& < > \" \'";
	private static final String ESCAPED_CHARS = "&amp; &lt; &gt; &quot; &apos;";
	private static final String CATEGORY_TAG_START = "<category name=\"";
	private static final long LEVEL1_CATEGORY_UID = 1111L;
	private static final String LEVEL1_CATEGORY_NAME = "TestCat1DisplayName";
	private static final long LEVEL2_CATEGORY_UID = 2222L;
	private static final String LEVEL2_CATEGORY_NAME = "TestCat2DisplayName";

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		stubGetBean(ContextIdNames.CATALOG_LOCALE, CatalogLocaleImpl.class);
		stubGetBean(ContextIdNames.UTILITY, UtilityImpl.class);
	}

	/**
	 * Tests the formatting of product data into XML.
	 */
	@Test
	public void testFormatProductWithOneCategoryAsXml() {

		// Create an instance of Product with one parent category
		Product product = createProduct(PRODUCT_UID);
		product = addCategories(product, 1);

		// Format the product into XML and validate the results
		ProductXmlServiceImpl productXmlService = new ProductXmlServiceImpl();
		String productXml = productXmlService.generateMinimalXml(SF_BASE_URL, product, false);

		// Validate the generated product XML string
		assertTrue(contains(productXml, "<?xml version=\"1.0\" encoding=\"utf-8\"?>"));
		assertTrue(contains(productXml, "<product xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""));
		assertTrue(contains(productXml, "productName=\"" + PRODUCT_NAME + "\""));
		assertTrue(contains(productXml, "description=\"" + PRODUCT_DESCRIPTION + "\""));
		assertTrue(contains(productXml, "brand=\"" + PRODUCT_BRAND_NAME + "\""));
		assertTrue(contains(productXml, "productBaseUrl=\"" + SF_BASE_URL + "\""));

		assertTrue(contains(productXml, CATEGORY_TAG_START + LEVEL1_CATEGORY_NAME + "\""
				+ " code=\"" + LEVEL1_CATEGORY_UID + "\">"));
	}

	/**
	 * Tests the formatting of a product with multiple categories into XML.
	 */
	@Test
	public void testFormatProductWithTwoCategoriesAsXml() {
		// Create an instance of Product with two levels of categories
		Product product = createProduct(PRODUCT_UID);
		product = addCategories(product, 2);

		// Format the product into XML and validate the results
		ProductXmlServiceImpl productXmlService = new ProductXmlServiceImpl();
		String productXml = productXmlService.generateMinimalXml("http://www.testbaseurl.com/epsf", product, false);

		// Validate the catalog hierarchy in the generated product XML string
		assertTrue(contains(productXml, CATEGORY_TAG_START + LEVEL1_CATEGORY_NAME + "\""
				+ " code=\"" + LEVEL1_CATEGORY_UID + "\">"));
		assertTrue(contains(productXml, CATEGORY_TAG_START + LEVEL2_CATEGORY_NAME + "\""
				+ " code=\"" + LEVEL2_CATEGORY_UID + "\">"));
	}

	/**
	 * Tests the formatting of a product with multiple categories into XML.
	 */
	@Test
	public void testFormatProductWithSpecialCharsAsXml() {
		// Create an instance of Product with two levels of categories
		Product product = createProductWithSpecialChars(PRODUCT_UID);
		product = addCategoriesWithSpecialChars(product);

		// Format the product into XML and validate the results
		ProductXmlServiceImpl productXmlService = new ProductXmlServiceImpl();
		String productXml = productXmlService.generateMinimalXml("http://www.testbaseurl.com/epsf", product, false);

		// Validate that all special characters in the generated product XML string are escaped
		assertTrue(contains(productXml, "productName=\"" + ESCAPED_CHARS + "\""));
		assertTrue(contains(productXml, "description=\"" + ESCAPED_CHARS + "\""));
		assertTrue(contains(productXml, "brand=\"" + ESCAPED_CHARS + "\""));
		assertTrue(contains(productXml, CATEGORY_TAG_START + ESCAPED_CHARS + "\""));
	}

	/**
	 * Creates a product with base data for testing.
	 *
	 * @param productUid - the uid of the product to create
	 * @return the generated test product
	 */
	private Product createProduct(final long productUid) {
		final Product product = createProductOverridingGetDisplayName(PRODUCT_NAME);
		product.initialize();
		product.setUidPk(productUid);

		// Set the brand name
		BrandImpl productBrand = new BrandImpl();
		productBrand.setLocalizedPropertiesMap(new HashMap<String, LocalizedPropertyValue>());
		productBrand.setLocalizedProperties(new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = 8219932126987598712L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl();
			}
		});
		productBrand.getLocalizedProperties().setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.US, PRODUCT_BRAND_NAME);
		product.setBrand(productBrand);
		
		// Set product description attribute
		AttributeValueGroup attributeValues = product.getAttributeValueGroup();
		AttributeImpl attribute = new AttributeImpl();
		attribute.initialize();
		attribute.setKey(ATTRIBUTE_KEY_DESCRIPTION);
		attribute.setAttributeType(AttributeType.LONG_TEXT);
		attributeValues.setAttributeValue(attribute, Locale.US, PRODUCT_DESCRIPTION);
		
		product.setCode(PRODUCT_NAME);
		
		return product;
	}

	/**
	 * Creates a product with xml special characters in it.
	 *
	 * @param productUid - the uid of the product to create
	 * @return the generated test product
	 */
	private Product createProductWithSpecialChars(final long productUid) {
		final Product product = createProductOverridingGetDisplayName(SPECIAL_CHARS);
		product.initialize();
		product.setUidPk(productUid);
		product.setCode(SPECIAL_CHARS);

		// Set the brand name
		BrandImpl productBrand = new BrandImpl();
		productBrand.setLocalizedPropertiesMap(new HashMap<String, LocalizedPropertyValue>());
		productBrand.setLocalizedProperties(new LocalizedPropertiesImpl() {
			private static final long serialVersionUID = 6955045925969878594L;

			@Override
			protected LocalizedPropertyValue getNewLocalizedPropertyValue() {
				return new BrandLocalizedPropertyValueImpl();
			}
		});
		productBrand.getLocalizedProperties().setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, Locale.US, SPECIAL_CHARS);
		product.setBrand(productBrand);

		// Set product description attribute
		AttributeValueGroup attributeValues = product.getAttributeValueGroup();
		Attribute attribute = new AttributeImpl();
		attribute.initialize();
		attribute.setKey(ATTRIBUTE_KEY_DESCRIPTION);
		attribute.setAttributeType(AttributeType.LONG_TEXT);
		attributeValues.setAttributeValue(attribute, Locale.US, SPECIAL_CHARS);

		return product;
	}

	/**
	 * Creates the test category with xml special characters and adds it to the product.
	 *
	 * @param product - the product to add the category to
	 * @return the given product with the test category associated with it.
	 */
	private Product addCategoriesWithSpecialChars(final Product product) {
		// Create parent category only
		Category parentCategory = createCategoryOverridingDisplayName(SPECIAL_CHARS);
		parentCategory.setUidPk(LEVEL1_CATEGORY_UID);
		parentCategory.setCode(SPECIAL_CHARS);
		parentCategory.setCatalog(createMasterCatalog());
		Set<Category> categories = new HashSet<Category>();
		categories.add(parentCategory);
		product.setCategories(categories);
				
		product.setCategoryAsDefault(parentCategory);

		return product;
	}
	
	private Catalog createMasterCatalog() {
		Catalog catalog = new CatalogImpl();
		catalog.setMaster(true);
		catalog.setDefaultLocale(Locale.US);
		catalog.setCode("code 6359");
		return catalog;
	}

	/**
	 * Creates the test category hierarchy for the product.
	 *
	 * @param product - the product to associate the categories with
	 * @param numberOfCategories - Number of levels of categories to create
	 * @return the given product with test categories associated with it
	 */
	private Product addCategories(final Product product, final int numberOfCategories) {
	
		final Catalog catalog = createMasterCatalog();

		if (numberOfCategories == 1) {
			// Create parent category only
			Category parentCategory = createCategoryOverridingDisplayName(LEVEL1_CATEGORY_NAME);
			//parentCategory.setCatalog(getCatalog());
			parentCategory.setUidPk(LEVEL1_CATEGORY_UID);
			parentCategory.setCode(LEVEL1_CATEGORY_NAME);
			parentCategory.setCatalog(catalog);
			product.setCategoryAsDefault(parentCategory);
			//It's not enough to set default Category
			Set<Category> categories = new HashSet<Category>();
			categories.add(parentCategory);
			product.setCategories(categories);
		} else if (numberOfCategories == 2) {
			// Create parent category
			Category parentCategory = createCategoryOverridingDisplayName(LEVEL2_CATEGORY_NAME);
			parentCategory.setUidPk(LEVEL2_CATEGORY_UID);
			parentCategory.setCode(LEVEL2_CATEGORY_NAME);
			parentCategory.setCatalog(catalog);
			product.setCategoryAsDefault(parentCategory);

			// Create level 1 category
			Category level1Category = createCategoryOverridingDisplayName(LEVEL1_CATEGORY_NAME);
			level1Category.initialize();
			level1Category.setUidPk(LEVEL1_CATEGORY_UID);
			level1Category.setCode(LEVEL1_CATEGORY_NAME);
			level1Category.setCatalog(catalog);
			parentCategory.setParent(level1Category);
		}

		return product;
	}
	
	/**
	 * Returns true if the <code>searchString</code> is found within <code>source</code>.
	 *
	 * @param source - the string to search within
	 * @param searchString - the string to search for
	 * @return true if the search string is found, false otherwise
	 */
	private boolean contains(final String source, final String searchString) {
		return source.indexOf(searchString) >= 0;
	}

	private CategoryImpl createCategoryOverridingDisplayName(final String categoryDisplayName) {
		return new CategoryImpl() {
			private static final long serialVersionUID = 5000000001L;
			@Override
			public String getDisplayName(final Locale locale) {
				return categoryDisplayName;
			}
		};
	}

	private ProductImpl createProductOverridingGetDisplayName(final String productDisplayName) {
		return new ProductImpl() {
			private static final long serialVersionUID = 5000000001L;
			@Override
			public String getDisplayName(final Locale locale) {
				return productDisplayName;
			}
		};
	}
}
