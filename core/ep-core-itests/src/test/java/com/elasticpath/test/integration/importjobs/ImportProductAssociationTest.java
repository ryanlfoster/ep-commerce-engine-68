package com.elasticpath.test.integration.importjobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test import job for ProductAssociation.
 */
public class ImportProductAssociationTest extends ImportJobTestCase {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductAssociationService productAssociationService;

	/**
	 * Test import ProductAssociation insert.
	 */
	@DirtiesDatabase
	@Test
	public void testImportProductAssociationInsert() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());
		executeImportJob(createInsertProductImportJob());

		executeImportJob(createInsertProductAssociationImportJob());

		Product product = productService.findByGuid("102", null);
		assertNotNull("Product should have been found", product);

		Set<ProductAssociation> productAssociations = productAssociationService.getAssociations(product.getCode(), product.getMasterCatalog()
				.getCode(), true);
		assertEquals(1, productAssociations.size());
		for (ProductAssociation productAssociation : productAssociations) {
			if (productAssociation.getTargetProduct().getCode().equals("101")) {
				assertEquals("101", productAssociation.getTargetProduct().getCode());
				assertEquals(2, productAssociation.getAssociationType());
				assertFalse(productAssociation.isSourceProductDependent());
				assertEquals(0, productAssociation.getOrdering());
				assertEquals(3, productAssociation.getDefaultQuantity());
				assertTrue(new Date().after(productAssociation.getStartDate()));
				assertNull(productAssociation.getEndDate());
			} else {
				fail("Unexpected association: " + productAssociation);
			}
		}

	}
	
	/**
	 * Test import ProductAssociation insert/update.
	 */
	@DirtiesDatabase
	@Test
	public void testImportProductAssociationInsertUpdate() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());
		executeImportJob(createInsertProductImportJob());

		executeImportJob(createInsertUpdateProductAssociationImportJob());

		Product product = productService.findByGuid("103", null);
		assertNotNull("Product should be found", product);

		Set<ProductAssociation> productAssociations = productAssociationService.getAssociations(product.getCode(), product.getMasterCatalog()
				.getCode(), true);
		assertEquals(1, productAssociations.size());
		for (ProductAssociation productAssociation : productAssociations) {
			if (productAssociation.getTargetProduct().getCode().equals("101")) {
				assertEquals("101", productAssociation.getTargetProduct().getCode());
				assertEquals(3, productAssociation.getAssociationType());
				assertTrue(productAssociation.isSourceProductDependent());
				assertEquals(0, productAssociation.getOrdering());
				assertEquals(4, productAssociation.getDefaultQuantity());
				assertTrue(new Date().after(productAssociation.getStartDate()));
				assertNull(productAssociation.getEndDate());
			} else {
				fail("Unexpected association: " + productAssociation);
			}
		}
	}
}
