package com.elasticpath.test.integration.importjobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test import job for ProductSku.
 */
public class ImportProductSkuTest extends ImportJobTestCase {

	@Autowired
	private ProductSkuService productSkuService;

	private void assertSkuOne() {
		ProductSku productSku = productSkuService.findBySkuCodeWithAll("SKU101");
		assertNotNull(productSku);
		assertEquals("Image 1.jpg", productSku.getImage());
		assertEquals(3.81, productSku.getWidth().doubleValue(), 0.00001);
		assertEquals(0.91, productSku.getLength().doubleValue(), 0.00001);
		assertEquals(1.81, productSku.getHeight().doubleValue(), 0.00001);
		assertEquals(0.21, productSku.getWeight().doubleValue(), 0.00001);
	}

	private void assertSkuTwo() {
		ProductSku productSku = productSkuService.findBySkuCodeWithAll("SKU102");
		assertNotNull(productSku);
		assertEquals("Image 2.jpg", productSku.getImage());
		assertEquals(3.82, productSku.getWidth().doubleValue(), 0.00001);
		assertEquals(0.92, productSku.getLength().doubleValue(), 0.00001);
		assertEquals(1.82, productSku.getHeight().doubleValue(), 0.00001);
		assertEquals(0.22, productSku.getWeight().doubleValue(), 0.00001);
	}

	private void assertSkuTwoUpdated() {
		ProductSku productSku = productSkuService.findBySkuCodeWithAll("SKU102");
		assertNotNull(productSku);
		assertEquals("Image 2 upd.jpg", productSku.getImage());
		assertEquals(13.82, productSku.getWidth().doubleValue(), 0.00001);
		assertEquals(10.92, productSku.getLength().doubleValue(), 0.00001);
		assertEquals(11.82, productSku.getHeight().doubleValue(), 0.00001);
		assertEquals(10.22, productSku.getWeight().doubleValue(), 0.00001);
	}

	private void assertSkuThree() {
		ProductSku productSku = productSkuService.findBySkuCodeWithAll("SKU103");
		assertNotNull(productSku);
		assertEquals("Image 3.jpg", productSku.getImage());
		assertEquals(3.83, productSku.getWidth().doubleValue(), 0.00001);
		assertEquals(0.93, productSku.getLength().doubleValue(), 0.00001);
		assertEquals(1.83, productSku.getHeight().doubleValue(), 0.00001);
		assertEquals(0.23, productSku.getWeight().doubleValue(), 0.00001);
	}

	/**
	 * Test import ProductSku insert.
	 */
	@DirtiesDatabase
	@Test
	public void testImportProductSkuInsert() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());
		executeImportJob(createInsertMultiskuProductImportJob());
		List<ImportBadRow> badRows = executeImportJob(createInsertProductSkuImportJob());
		assertEquals("There should be no bad import rows.", 0, badRows.size());

		assertSkuOne();
		assertSkuTwo();
	}

	/**
	 * Test import ProductSku insert/update.
	 */
	@DirtiesDatabase
	@Test
	public void testImportProductSkuInsertUpdate() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());
		executeImportJob(createInsertMultiskuProductImportJob());
		executeImportJob(createInsertUpdateMultiskuProductImportJob());
		executeImportJob(createInsertProductSkuImportJob());
		List<ImportBadRow> badRows = executeImportJob(createInsertUpdateProductSkuImportJob());
		assertEquals("There should be no bad import rows.", 0, badRows.size());

		assertSkuOne();
		assertSkuTwoUpdated();
		assertSkuThree();
	}

	/**
	 * Test import ProductSku update.
	 */
	@DirtiesDatabase
	@Test
	public void testImportProductSkuUpdate() throws Exception {
		executeImportJob(createInsertCategoriesImportJob());
		executeImportJob(createInsertMultiskuProductImportJob());
		executeImportJob(createInsertProductSkuImportJob());
		List<ImportBadRow> badRows = executeImportJob(createUpdateProductSkuImportJob());
		assertEquals("There should be no bad import rows.", 0, badRows.size());

		assertSkuOne();
		assertSkuTwoUpdated();
	}
}
