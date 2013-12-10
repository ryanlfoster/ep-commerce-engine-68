/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.search.index.solr.document.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.search.index.pipeline.stats.impl.PipelinePerformanceImpl;
import com.elasticpath.search.index.solr.builders.impl.SkuIndexBuilder;
import com.elasticpath.search.index.solr.document.impl.SkuSolrInputDocumentCreator.SkuCatalogFields;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.service.search.SkuSearchResultType;
import com.elasticpath.service.search.index.Analyzer;
import com.elasticpath.service.search.solr.AnalyzerImpl;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Test that {@link SkuIndexBuilder} behaves as expected.
 */
public class SkuIndexBuilderTest {

	private SkuIndexBuilder skuIndexBuilder;
	private SkuSolrInputDocumentCreator skuDocumentCreator;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ProductSkuService skuService;

	private SolrInputDocument document;

	private Analyzer analyzer;

	private ProductSku sku;

	/**
	 * Set up required for each test.
	 *
	 * @throws java.lang.Exception in case of error during setup
	 */
	@Before
	public void setUp() throws Exception {
		skuService = context.mock(ProductSkuService.class);
		document = new SolrInputDocument();
		analyzer = new AnalyzerImpl();

		skuIndexBuilder = new SkuIndexBuilder();
		skuDocumentCreator = new SkuSolrInputDocumentCreator();
		skuIndexBuilder.setSkuService(skuService);

		skuDocumentCreator.setAnalyzer(analyzer);
		skuDocumentCreator.setPipelinePerformance(new PipelinePerformanceImpl());

		sku = context.mock(ProductSku.class);
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
		skuDocumentCreator.addResultTypeToDocument(document, sku);
		assertEquals("expected product when sku belongs to a single-sku product", String.valueOf(SkuSearchResultType.PRODUCT.getSortOrder()),
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
		skuDocumentCreator.addResultTypeToDocument(document, sku);
		assertEquals("expected sku when sku belongs to a multi-sku product", String.valueOf(SkuSearchResultType.PRODUCT_SKU.getSortOrder()),
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
		skuDocumentCreator.addResultTypeToDocument(document, sku);
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

		Map<Long, SkuCatalogFields> catalogMap = skuDocumentCreator.addAvailableCatalogsToDocument(document, sku);
		assertEquals("There should a 1 result in the map", 1, catalogMap.size());

		Map.Entry<Long, SkuCatalogFields> entry = catalogMap.entrySet().iterator().next();
		assertEquals("The map should be keyed by catalog uid", catalogUid, entry.getKey().longValue());
		assertTrue("The sku should be available", entry.getValue().isAvailable());
		assertEquals("The default locale should be english", Locale.ENGLISH, entry.getValue().getDefaultLocale());
		assertEquals("The supported locales should come from the catalog", locales, entry.getValue().getSupportedLocales());

		String catalogCode = (String) document.getFieldValue(SolrIndexConstants.CATALOG_CODE);
		assertEquals("The document should contain the catalog code", "TEST_CATALOG", catalogCode);
	}

}
