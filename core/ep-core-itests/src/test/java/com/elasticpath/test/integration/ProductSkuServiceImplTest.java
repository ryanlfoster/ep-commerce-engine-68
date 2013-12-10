package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuService;
import com.elasticpath.test.db.DbTestCase;

/**
 * An integration test for ProductServiceImpl, we are testing from a client's point of view with Spring and the Database up and running.
 */
public class ProductSkuServiceImplTest extends DbTestCase {

	/** The main object under test. */
	@Autowired
	@Qualifier("productService")
	private ProductService service;
	
	@Autowired
	private ProductSkuService skuService;

	/**
	 * Tests removing of a product.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveProduct() {
		Product product = this.persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		Collection<ProductSku> productSkus = product.getProductSkus().values();
		assertFalse("no sku was generated", productSkus.isEmpty());
		
		Date dateBeforeRemoveProduct = new Date();
		
		service.removeProductTree(product.getUidPk());
		
		List<Long> deletedSkuUids = skuService.findSkuUidsByDeletedDate(dateBeforeRemoveProduct);
		assertFalse("no skus were deleted", deletedSkuUids.isEmpty());
		assertEquals("the number of the deleted sku uids is wrong", productSkus.size(), deletedSkuUids.size());
		for (ProductSku productSku : productSkus) {
			assertTrue("the sku uid is not in the deleted sku uid list", deletedSkuUids.contains(productSku.getUidPk()));
		}
	}
	
	/**
	 * Tests product sku removal.
	 */
	@DirtiesDatabase
	@Test
	public void testRemoveSku() {
		Product product = this.persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
		ProductSku sku = product.getDefaultSku();
		product.removeSku(sku);
		
		Date dateBeforeRemoveSku = new Date();
		service.saveOrUpdate(product);
		
		List<Long> deletedSkuUids = skuService.findSkuUidsByDeletedDate(dateBeforeRemoveSku);
		assertEquals("one sku is found in deleted sku", deletedSkuUids.size(), 1);
		assertEquals("the deleted sku uidpk is not correct", deletedSkuUids.get(0).longValue(), sku.getUidPk());
		
	}
}
