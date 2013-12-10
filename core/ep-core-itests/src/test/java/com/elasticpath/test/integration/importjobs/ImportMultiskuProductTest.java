package com.elasticpath.test.integration.importjobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.AttributeValueGroup;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test import job for Product.
 */
public class ImportMultiskuProductTest extends ImportJobTestCase {

	private void assertProductOne() {
		ProductService productService = getBeanFactory().getBean(ContextIdNames.PRODUCT_SERVICE);
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
	}

	private void assertProductOneAbsent() {
		ProductService productService = getBeanFactory().getBean(ContextIdNames.PRODUCT_SERVICE);
		assertNull(productService.findByGuid("101", null));
	}

	private void assertProductTwo() {
		ProductService productService = getBeanFactory().getBean(ContextIdNames.PRODUCT_SERVICE);
		Product product = productService.findByGuid("102", null);
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
	}

	private void assertProductTwoUpdated() {
		ProductService productService = getBeanFactory().getBean(ContextIdNames.PRODUCT_SERVICE);
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

	}

	private void assertProductThree() {
		ProductService productService = getBeanFactory().getBean(ContextIdNames.PRODUCT_SERVICE);
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
	}

	/**
	 * Test import Product insert.
	 */
	@DirtiesDatabase
	@Test
	public void testImportProductInsert() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());

		List<ImportBadRow> badRows = executeImportJob(createInsertMultiskuProductImportJob());
		assertEquals(0, badRows.size());

		assertProductOne();
		assertProductTwo();
	}
//
//	/**
//	 * Test import Product insert/update.
//	 */
//	@DirtiesDatabase
//	@Test
//	public void testImportProductInsertUpdate() throws Exception {
//		executeImportJob(createInsertCategoriesImportJob());
//		executeImportJob(createInsertMultiskuProductImportJob());
//		List<ImportBadRow> badRows = executeImportJob(createInsertUpdateMultiskuProductImportJob());
//		assertEquals(0, badRows.size());
//
//		assertProductOne();
//		assertProductTwoUpdated();
//		assertProductThree();
//	}

	/**
	 * Test import Product update.
	 */
	@DirtiesDatabase
	@Test
	public void testImportProductUpdate() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());
		executeImportJob(createInsertMultiskuProductImportJob());
		List<ImportBadRow> badRows = executeImportJob(createUpdateMultiskuProductImportJob());
		assertEquals(0, badRows.size());

		assertProductOne();
		assertProductTwoUpdated();

	}

//	/**
//	 * Test input Product delete.
//	 */
//	@DirtiesDatabase
//	@Test
//	public void testImportProductDelete() throws Exception {
//		executeImportJob(createInsertCategoriesImportJob());
//		executeImportJob(createInsertMultiskuProductImportJob());
//		List<ImportBadRow> badRows = executeImportJob(createDeleteMultiskuProductImportJob());
//		assertEquals(0, badRows.size());
//
//		assertProductOneAbsent();
//		assertProductTwo();
//	}
}
