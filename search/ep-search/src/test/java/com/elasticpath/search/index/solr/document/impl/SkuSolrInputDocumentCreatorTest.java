package com.elasticpath.search.index.solr.document.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.search.index.solr.document.impl.SkuSolrInputDocumentCreator.SkuCatalogFields;
import com.elasticpath.service.search.SkuSearchResultType;
import com.elasticpath.service.search.index.Analyzer;
import com.elasticpath.service.search.solr.AnalyzerImpl;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test construction of {@link SolrInputDocument}s from {@link ProductSku}s using {@link SkuSolrInputDocumentCreator}.
 */
public class SkuSolrInputDocumentCreatorTest {
	private static final Date START_DATE = new Date();
	private static final Date END_DATE = new Date();
	private static final String SKU_CODE = "SKU_CODE";
	private static final Long SKU_UIDPK = Long.valueOf(999);
	private static final String BRAND_CODE = "BRAND_CODE";
	private static final String BRAND_NAME = "BRAND_NAME";
	
	private SkuSolrInputDocumentCreator skuSolrInputDocumentCreator;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private SolrInputDocument document;
	private ProductSku sku;
	private Analyzer analyzer;
	
	/**
	 * Set up required for each test.
	 * 
	 * @throws Exception in case of error during setup
	 */
	@Before
	public void setUp() throws Exception {
		document = new SolrInputDocument();
		analyzer = new AnalyzerImpl();
		sku = context.mock(ProductSku.class);
		
		skuSolrInputDocumentCreator = new SkuSolrInputDocumentCreator();
		skuSolrInputDocumentCreator.setAnalyzer(analyzer);
	}

	
	/**
	 * Test adding values from the sku's brand.
	 */
	@Test
	public void testAddingBrandValues() {
		final Brand brand = context.mock(Brand.class);
		final Locale locale = new Locale("");
		context.checking(new Expectations() {
			{
				allowing(brand).getDisplayName(with(any(Locale.class)), with(any(Boolean.class))); will(returnValue(BRAND_NAME));
				oneOf(brand).getCode(); will(returnValue(BRAND_CODE));
			}
		});

		skuSolrInputDocumentCreator.addBrandValues(document, brand, locale);
		assertEquals("Brand name should match document ", BRAND_NAME, document.getFieldValue(SolrIndexConstants.BRAND_NAME));
		assertEquals("Brand name (sort) should match document ", BRAND_NAME, document.getFieldValue(SolrIndexConstants.SORT_BRAND_NAME));
		assertEquals("Brand code should match document ", BRAND_CODE, document.getFieldValue(SolrIndexConstants.BRAND_CODE));
	}

	/**
	 * Test adding values from the sku itself.
	 */
	@Test
	public void testAddSkuValues() {
		final ProductSku sku = context.mock(ProductSku.class, "localSku");

		context.checking(new Expectations() {
			{
				oneOf(sku).getSkuCode(); will(returnValue(SKU_CODE));
				oneOf(sku).getEffectiveStartDate(); will(returnValue(START_DATE));
				oneOf(sku).getEffectiveEndDate(); will(returnValue(END_DATE));
				oneOf(sku).getUidPk(); will(returnValue(Long.valueOf(SKU_UIDPK)));
			}
		});

		
		skuSolrInputDocumentCreator.addSkuValues(document, sku);
		assertEquals("Sku uidpk should match document ", String.valueOf(SKU_UIDPK), document.getFieldValue(SolrIndexConstants.OBJECT_UID));
		assertEquals("Sku code should match document ", SKU_CODE, document.getFieldValue(SolrIndexConstants.PRODUCT_SKU_CODE));
		assertEquals("Sku start date should match document ", analyzer.analyze(START_DATE), document.getFieldValue(SolrIndexConstants.START_DATE));
		assertEquals("Sku end date should match document ", analyzer.analyze(END_DATE), document.getFieldValue(SolrIndexConstants.END_DATE));
	}
	
	/**
	 * Test adding the result type examines the sku's product to determine the correct type.
	 */
	@Test
	public void testAddResultTypeToDocumentSingleSku() {
		final Product singleSkuProduct = context.mock(Product.class);

		context.checking(new Expectations() {
			{
				oneOf(singleSkuProduct).hasMultipleSkus(); will(returnValue(false));
				allowing(sku).getProduct(); will(returnValue(singleSkuProduct));
			}
		});
		
		skuSolrInputDocumentCreator.addResultTypeToDocument(document, sku);
		assertEquals("Expected product type when sku belongs to a single-sku product", String.valueOf(SkuSearchResultType.PRODUCT.getSortOrder()), 
				document.getFieldValue(SolrIndexConstants.SKU_RESULT_TYPE));
	}

	/**
	 * Test adding the result type examines the sku's product to determine the correct type.
	 */
	@Test
	public void testAddResultTypeToDocumentMultiSku() {
		final Product multiSkuProduct = context.mock(Product.class, "multiSkuProduct");
		
		context.checking(new Expectations() {
			{
				oneOf(multiSkuProduct).hasMultipleSkus(); will(returnValue(true));
				allowing(sku).getProduct(); will(returnValue(multiSkuProduct));
			}
		});
		
		skuSolrInputDocumentCreator.addResultTypeToDocument(document, sku);
		assertEquals("Expected sku type when sku belongs to a multi-sku product", String.valueOf(SkuSearchResultType.PRODUCT_SKU.getSortOrder()), 
				document.getFieldValue(SolrIndexConstants.SKU_RESULT_TYPE));
	}
	
	/**
	 * Test adding the result type examines the sku's product to determine the correct type.
	 */
	@Test
	public void testAddResultTypeToDocumentProductBundle() {
		final ProductBundle productBundle = context.mock(ProductBundle.class);
		
		context.checking(new Expectations() {
			{
				oneOf(productBundle).hasMultipleSkus(); will(returnValue(false));
				allowing(sku).getProduct(); will(returnValue(productBundle));
			}
		});
		
		skuSolrInputDocumentCreator.addResultTypeToDocument(document, sku);
		assertEquals("expected bundle when sku belongs to a bundle", String.valueOf(SkuSearchResultType.PRODUCT_BUNDLE.getSortOrder()), 
				document.getFieldValue(SolrIndexConstants.SKU_RESULT_TYPE));
		document.clear();
	}
	
	/**
	 * Test adding available catalogs examines the sku's product's categories to get the catalogs. 
	 */
	@Test
	public void testAddAvailableCatalogsToDocument() {
		final Product product = context.mock(Product.class);
		final Category category = context.mock(Category.class);
		final Set<Category> categories = new HashSet<Category>();
		categories.add(category);
		final Catalog catalog = context.mock(Catalog.class);
		final long catalogUid = 1234L;
		final Set<Locale> locales = new HashSet<Locale>();
		locales.add(Locale.ENGLISH);
		locales.add(Locale.JAPANESE);
		
		context.checking(new Expectations() {
			{
				oneOf(sku).getProduct(); will(returnValue(product));
				
				oneOf(product).getCategories(); will(returnValue(categories));
				
				allowing(category).getCatalog(); will(returnValue(catalog));
				oneOf(category).isAvailable(); will(returnValue(true));
				oneOf(category).getParent(); will(returnValue(null));
		
				oneOf(catalog).getUidPk(); will(returnValue(catalogUid));
				oneOf(catalog).getCode(); will(returnValue("TEST_CATALOG"));
				oneOf(catalog).isMaster(); will(returnValue(true));
				oneOf(catalog).getDefaultLocale(); will(returnValue(Locale.ENGLISH));
				oneOf(catalog).getSupportedLocales(); will(returnValue(locales));
			}
		});
		
		Map<Long, SkuCatalogFields> catalogMap = skuSolrInputDocumentCreator.addAvailableCatalogsToDocument(document, sku);
		assertEquals("There should be 1 result in the map", 1, catalogMap.size());
		
		Map.Entry<Long, SkuCatalogFields> entry = catalogMap.entrySet().iterator().next();
		assertEquals("The map should be keyed by catalog uid", catalogUid, entry.getKey().longValue());
		assertTrue("The sku should be available", entry.getValue().isAvailable());
		assertEquals("The default locale should be english", Locale.ENGLISH, entry.getValue().getDefaultLocale());
		assertEquals("The supported locales should come from the catalog", locales, entry.getValue().getSupportedLocales());
		
		String catalogCode = (String) document.getFieldValue(SolrIndexConstants.CATALOG_CODE);
		assertEquals("The document should contain the catalog code", "TEST_CATALOG", catalogCode);
	}
	
}
