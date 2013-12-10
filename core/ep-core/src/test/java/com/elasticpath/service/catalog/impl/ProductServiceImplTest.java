/**
 * 
 */
package com.elasticpath.service.catalog.impl;

import java.util.ArrayList;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.exception.EpProductInUseException;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.persistence.dao.ProductDao;
import com.elasticpath.service.search.IndexNotificationService;
import com.elasticpath.service.search.IndexType;

/**
 * Tests for the ProductServiceImpl class.
 */
public class ProductServiceImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final ProductDao productDao = context.mock(ProductDao.class);
	private final IndexNotificationService indexNotificationService = context.mock(IndexNotificationService.class);
	
	/**
	 * Tests if #removeProductTree throws exception when product is in a bundle.
	 */
	@Test(expected = EpProductInUseException.class)
	public void testRemoveProductTreeThrowsExceptionWhenProductIsInBundle() {
		final long aProductUid = 1L;
		final Product product = new ProductImpl();
		
		final ArrayList<Product> productsInBundleList = new ArrayList<Product>();
		productsInBundleList.add(product);
		
		ProductServiceImpl service = new ProductServiceImpl() {
			@Override
			public boolean canDelete(final Product product) {
				return false;
			}
		};
		
		service.setProductDao(productDao);
		
		context.checking(new Expectations() { {
			oneOf(productDao).get(aProductUid); will(returnValue(product));
		} });
		
		service.removeProductTree(aProductUid);
	}
	
	/**
	 * Tests that index notification update is posted when a product is added or updated.
	 */
	@Ignore("test does not work")
	@Test
	public void testAddOrUpdateProductAddsIndexNotification() {
		final Product product = new ProductImpl();
		final ProductServiceImpl productService = new ProductServiceImpl();
		productService.setIndexNotificationService(indexNotificationService);
		context.checking(new Expectations() {
			{
				oneOf(productDao).saveOrUpdate(product); will(returnValue(product));
				oneOf(indexNotificationService).addNotificationForEntityIndexUpdate(IndexType.PRODUCT, product.getUidPk());
			}
		});
		
		productService.saveOrUpdate(product);
	}
	
}
