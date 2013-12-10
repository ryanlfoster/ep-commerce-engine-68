package com.elasticpath.test.integration.importjobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test import job for Product.
 */
public class ImportProductTest extends ImportJobTestCase {

	@Autowired
	@Qualifier("productService")
	private ProductService productService;

	private void assertProductOne() {
		Product product = productService.findByGuid("101", null);
		assertNotNull("Product should have been found", product);
		final AttributeValueGroup attributes = product.getAttributeValueGroup();

		assertEquals("Desc en 1", attributes.getAttributeValue("description", Locale.ENGLISH).getValue());
		assertEquals("Desc fr 1", attributes.getAttributeValue("description", Locale.FRENCH).getValue());

		assertEquals("101", product.getCode());
		assertEquals(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK, product.getAvailabilityCriteria());
		assertEquals("F00004", product.getBrand().getCode());
		assertEquals("Displ Name en 1", product.getDisplayName(Locale.ENGLISH));
		assertEquals("Displ Name fr 1", product.getDisplayName(Locale.FRENCH));
		assertEquals(null, product.getEndDate());
		assertEquals(null, product.getExpectedReleaseDate());
		assertEquals("Image 1.jpg", product.getImage());
		assertEquals(1, product.getMinOrderQty());
		assertEquals(0, product.getPreOrBackOrderLimit());

		{ // assert locale dependent fields: For English
			LocaleDependantFields localeDependantFields = product.getLocaleDependantFields(Locale.ENGLISH);
			assertEquals("SEO desc en 1", localeDependantFields.getDescription());
			assertEquals("Displ Name en 1", localeDependantFields.getDisplayName());
			assertEquals("SEO title en 1", localeDependantFields.getTitle());
			assertEquals("SEO keywords en 1", localeDependantFields.getKeyWords());
			assertEquals(null, localeDependantFields.getUrl());
		}

		{ // assert locale dependent fields: For French
			LocaleDependantFields localeDependantFields = product.getLocaleDependantFields(Locale.FRENCH);
			assertEquals("SEO desc fr 1", localeDependantFields.getDescription());
			assertEquals("Displ Name fr 1", localeDependantFields.getDisplayName());
			assertEquals("SEO title fr 1", localeDependantFields.getTitle());
			assertEquals("SEO keywords fr 1", localeDependantFields.getKeyWords());
			assertEquals(null, localeDependantFields.getUrl());
		}
		final Map<String, ProductSku> skus = product.getProductSkus();
		assertEquals(1, skus.size()); // this is a single sku import
		assertEquals("SKU101", skus.values().iterator().next().getSkuCode());
	}

	private void assertProductOneAbsent() {
		assertNull(productService.findByGuid("101", null));
	}

	private void assertProductTwo() {
		Product product = productService.findByGuid("102", null);
		assertNotNull("Product should have been found", product);
		final AttributeValueGroup attributes = product.getAttributeValueGroup();

		assertEquals("Desc en 2", attributes.getAttributeValue("description", Locale.ENGLISH).getValue());
		assertEquals("Desc fr 2", attributes.getAttributeValue("description", Locale.FRENCH).getValue());

		assertEquals("102", product.getCode());
		assertEquals(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK, product.getAvailabilityCriteria());
		assertEquals("F00004", product.getBrand().getCode());
		assertEquals("Displ Name en 2", product.getDisplayName(Locale.ENGLISH));
		assertEquals("Displ Name fr 2", product.getDisplayName(Locale.FRENCH));
		assertEquals(null, product.getEndDate());
		assertEquals(null, product.getExpectedReleaseDate());
		assertEquals("Image 2.jpg", product.getImage());
		assertEquals(1, product.getMinOrderQty());
		assertEquals(0, product.getPreOrBackOrderLimit());

		{ // assert locale dependent fields: For English
			LocaleDependantFields localeDependantFields = product.getLocaleDependantFields(Locale.ENGLISH);
			assertEquals("SEO desc en 2", localeDependantFields.getDescription());
			assertEquals("Displ Name en 2", localeDependantFields.getDisplayName());
			assertEquals("SEO title en 2", localeDependantFields.getTitle());
			assertEquals("SEO keywords en 2", localeDependantFields.getKeyWords());
			assertEquals(null, localeDependantFields.getUrl());
		}

		{ // assert locale dependent fields: For French
			LocaleDependantFields localeDependantFields = product.getLocaleDependantFields(Locale.FRENCH);
			assertEquals("SEO desc fr 2", localeDependantFields.getDescription());
			assertEquals("Displ Name fr 2", localeDependantFields.getDisplayName());
			assertEquals("SEO title fr 2", localeDependantFields.getTitle());
			assertEquals("SEO keywords fr 2", localeDependantFields.getKeyWords());
			assertEquals(null, localeDependantFields.getUrl());
		}

		final Map<String, ProductSku> skus = product.getProductSkus();
		assertEquals(1, skus.size()); // this is a single sku import
		assertEquals("SKU102", skus.values().iterator().next().getSkuCode());
	}

	private void assertProductTwoUpdated() {
		Product product = productService.findByGuid("102", null);
		final AttributeValueGroup attributes = product.getAttributeValueGroup();

		assertEquals("Desc en 2 upd", attributes.getAttributeValue("description", Locale.ENGLISH).getValue());
		assertEquals("Desc fr 2 upd", attributes.getAttributeValue("description", Locale.FRENCH).getValue());

		assertEquals("102", product.getCode());
		assertEquals(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK, product.getAvailabilityCriteria());
		assertEquals("F00004", product.getBrand().getCode());
		assertEquals("Displ Name en 2 upd", product.getDisplayName(Locale.ENGLISH));
		assertEquals("Displ Name fr 2 upd", product.getDisplayName(Locale.FRENCH));
		assertEquals(null, product.getEndDate());
		assertEquals(null, product.getExpectedReleaseDate());
		assertEquals("Image 2.jpg", product.getImage());
		assertEquals(1, product.getMinOrderQty());
		assertEquals(0, product.getPreOrBackOrderLimit());

		{ // assert locale dependent fields: For English
			LocaleDependantFields localeDependantFields = product.getLocaleDependantFields(Locale.ENGLISH);
			assertEquals("SEO desc en 2 upd", localeDependantFields.getDescription());
			assertEquals("Displ Name en 2 upd", localeDependantFields.getDisplayName());
			assertEquals("SEO title en 2", localeDependantFields.getTitle());
			assertEquals("SEO keywords en 2", localeDependantFields.getKeyWords());
			assertEquals(null, localeDependantFields.getUrl());
		}

		{ // assert locale dependent fields: For French
			LocaleDependantFields localeDependantFields = product.getLocaleDependantFields(Locale.FRENCH);
			assertEquals("SEO desc fr 2 upd", localeDependantFields.getDescription());
			assertEquals("Displ Name fr 2 upd", localeDependantFields.getDisplayName());
			assertEquals("SEO title fr 2", localeDependantFields.getTitle());
			assertEquals("SEO keywords fr 2", localeDependantFields.getKeyWords());
			assertEquals(null, localeDependantFields.getUrl());
		}

		final Map<String, ProductSku> skus = product.getProductSkus();
		assertEquals(1, skus.size()); // this is a single sku import
		assertEquals("SKU102", skus.values().iterator().next().getSkuCode());
	}

	private void assertProductThree() {
		Product product = productService.findByGuid("103", null);
		final AttributeValueGroup attributes = product.getAttributeValueGroup();

		assertEquals("Desc en 3", attributes.getAttributeValue("description", Locale.ENGLISH).getValue());
		assertEquals("Desc fr 3", attributes.getAttributeValue("description", Locale.FRENCH).getValue());

		assertEquals("103", product.getCode());
		assertEquals(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK, product.getAvailabilityCriteria());
		assertEquals("F00004", product.getBrand().getCode());
		assertEquals("Displ Name en 3", product.getDisplayName(Locale.ENGLISH));
		assertEquals("Displ Name fr 3", product.getDisplayName(Locale.FRENCH));
		assertEquals(null, product.getEndDate());
		assertEquals(null, product.getExpectedReleaseDate());
		assertEquals("Image 3.jpg", product.getImage());
		assertEquals(1, product.getMinOrderQty());
		assertEquals(0, product.getPreOrBackOrderLimit());

		{ // assert locale dependent fields: For English
			LocaleDependantFields localeDependantFields = product.getLocaleDependantFields(Locale.ENGLISH);
			assertEquals("SEO desc en 3", localeDependantFields.getDescription());
			assertEquals("Displ Name en 3", localeDependantFields.getDisplayName());
			assertEquals("SEO title en 3", localeDependantFields.getTitle());
			assertEquals("SEO keywords en 3", localeDependantFields.getKeyWords());
			assertEquals(null, localeDependantFields.getUrl());
		}

		{ // assert locale dependent fields: For French
			LocaleDependantFields localeDependantFields = product.getLocaleDependantFields(Locale.FRENCH);
			assertEquals("SEO desc fr 3", localeDependantFields.getDescription());
			assertEquals("Displ Name fr 3", localeDependantFields.getDisplayName());
			assertEquals("SEO title fr 3", localeDependantFields.getTitle());
			assertEquals("SEO keywords fr 3", localeDependantFields.getKeyWords());
			assertEquals(null, localeDependantFields.getUrl());
		}

		final Map<String, ProductSku> skus = product.getProductSkus();
		assertEquals(1, skus.size()); // this is a single sku import
		assertEquals("SKU103", skus.values().iterator().next().getSkuCode());
	}

	/**
	 * Test import Product insert.
	 *
	 * @throws Exception if any exception is thrown
	 */
	@DirtiesDatabase
	@Test
	public void testImportProductInsert() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());

		List<ImportBadRow> badRows = executeImportJob(createInsertProductImportJob());
		assertEquals(0, badRows.size());

		assertProductOne();
		assertProductTwo();
	}

	/**
	 * Test import Product insert/update.
	 *
	 * @throws Exception if any exception is thrown
	 */
	@DirtiesDatabase
	@Test
	public void testImportProductInsertUpdate() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());
		executeImportJob(createInsertProductImportJob());
		List<ImportBadRow> badRows = executeImportJob(createInsertUpdateProductImportJob());
		assertEquals(0, badRows.size());

		assertProductOne();
		assertProductTwoUpdated();
		assertProductThree();
	}

	/**
	 * Test import Product update.
	 *
	 * @throws Exception if any exception is thrown
	 */
	@DirtiesDatabase
	@Test
	public void testImportProductUpdate() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());
		executeImportJob(createInsertProductImportJob());
		List<ImportBadRow> badRows = executeImportJob(createUpdateProductImportJob());
		assertEquals(0, badRows.size());

		assertProductOne();
		assertProductTwoUpdated();

	}

	/**
	 * Test input Product delete.
	 *
	 * @throws Exception if any exception is thrown
	 */
	@DirtiesDatabase
	@Test
	public void testImportProductDelete() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());
		executeImportJob(createInsertProductImportJob());
		List<ImportBadRow> badRows = executeImportJob(createDeleteProductImportJob());
		assertEquals(0, badRows.size());

		assertProductOneAbsent();
		assertProductTwo();
	}
}
