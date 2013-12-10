package com.elasticpath.test.integration.cart;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.common.pricing.service.PriceListHelperService;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.catalog.BundleConstituentFactory;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.impl.SelectionRuleFactoryImpl;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.util.Utils;

/**
 * Integration test for {@code ShoppingItemAssemblerImpl}. Concentrates on proving that deeply nested bundles
 * are processed correctly for the {@code AddToCartController}. <br/> 
 * 
 * Note that this test should probably be against
 * {@code CartDirector} so that the module interface is tested. However, at present I don't wish to test the 
 * other functionality of {@code CartDirector} (i.e. pricing).
 */
public class ShoppingItemAssemblerIntegrationTest extends DbTestCase {
	
	@Autowired
	private ShoppingItemAssembler assembler;

	@Autowired
	private ProductService productService;

	@Autowired
	private BundleConstituentFactory constituentFactory;

	@Autowired
	private PriceListHelperService priceListHelperService;
	
	private static final String TESTTYPE = "TESTTYPE";

	/**
	 * Tests calling {@code createShoppingItem} for a non-bundle product.
	 */
	@DirtiesDatabase
	@Test
	public void testCreatingShoppingItemFromNonBundleProduct() {
		persistProductWithSku();
		ShoppingItemDto shoppingItemDto = new ShoppingItemDto("skuCode", 1);
		
		ShoppingItem shoppingItem = assembler.createShoppingItem(shoppingItemDto);
		
		assertEquals("Sku should be what is in DTO", "skuCode", shoppingItem.getProductSku().getSkuCode());
		assertEquals("Quantity should be what is in DTO", 1, shoppingItem.getQuantity());
	}
	
	/**
	 * Tests calling {@code createShoppingItem} for a nested bundle with 1 level.
	 */
	@DirtiesDatabase
	@Test
	public void testCreatingShoppingItemFrom1LevelBundle() {
		persist1LevelBundle();
		ShoppingItemDto rootDto = new ShoppingItemDto("skuCode", 1);
		ShoppingItemDto child1 = new ShoppingItemDto("sku2", 1);
		rootDto.addConstituent(child1);
		
		ShoppingItem shoppingItem = assembler.createShoppingItem(rootDto);
		
		assertEquals("Sku should be what is in DTO", "skuCode", shoppingItem.getProductSku().getSkuCode());
		assertEquals("Quantity should be what is in DTO", 1, shoppingItem.getQuantity());
		assertEquals("1 level, 1 child", 1, shoppingItem.getChildren().size());
		
		ShoppingItem childItem = shoppingItem.getChildren().get(0);
		assertEquals("The child dto's sku", "sku2", childItem.getProductSku().getSkuCode());
		assertEquals("The child dto's quantity", 1, childItem.getQuantity());
	}
	
	/**
	 * Tests calling {@code createShoppingItem} for a nested bundle with 3 levels.
	 */
	@Ignore("Known to fail but this test demonstrates the failure")
	@DirtiesDatabase
	@Test
	public void testCreatingShoppingItemFrom3LevelBundle() {
		persist3LevelBundle();
		
		ShoppingItemDto shoppingItemDto = new ShoppingItemDto("sku5", 1);
		ShoppingItemDto child1 = new ShoppingItemDto("sku6", 1);
		shoppingItemDto.addConstituent(child1);
		ShoppingItemDto grandchild1 = new ShoppingItemDto("sku4", 1);
		child1.addConstituent(grandchild1);
		
		ShoppingItemDto greatgrandchild1 = new ShoppingItemDto("sku7", 1);
		grandchild1.addConstituent(greatgrandchild1);
		
		ShoppingItem shoppingItem = assembler.createShoppingItem(shoppingItemDto);
		
		assertEquals("Sku should be what is in DTO", "sku5", shoppingItem.getProductSku().getSkuCode());
		assertEquals("Quantity should be what is in DTO", 1, shoppingItem.getQuantity());
		
		assertEquals(1, shoppingItem.getBundleItems().size());
		ShoppingItem childShoppingItem = shoppingItem.getBundleItems().get(0);
		assertEquals("level 1 dto should match item", "sku6", childShoppingItem.getProductSku().getSkuCode());
		
		assertEquals(1, childShoppingItem.getBundleItems().size());
		ShoppingItem grandchildShoppingItem = childShoppingItem.getBundleItems().get(0);
		assertEquals("level 2 dto should match item", "sku4", grandchildShoppingItem.getProductSku().getSkuCode());
		
		assertEquals("Should be 1 child at the 3rd level", 1, grandchildShoppingItem.getBundleItems().size());
		ShoppingItem greatgrandchildShoppingItem = grandchildShoppingItem.getBundleItems().get(0);
		assertEquals("level 3 dto should match item", "sku7", greatgrandchildShoppingItem.getProductSku().getSkuCode());
	}
	
	private void persist3LevelBundle() {
		ProductBundle bundle = generateSimpleProductBundle(TESTTYPE);
		bundle.setSelectionRule(new SelectionRuleFactoryImpl().createSelectAllRule());
		bundle.setCategoryAsDefault(getScenario().getCategory());

		ProductBundle childLevel1 = generateSimpleProductBundle(TESTTYPE);
		childLevel1.setCategoryAsDefault(getScenario().getCategory());
		getPersisterFactory().getCatalogTestPersister().persistSimpleProductSku("sku6", 1.56, "CAD", true, childLevel1, getScenario().getWarehouse());
		bundle.addConstituent(createDefaultConstituent(childLevel1));
		
		ProductBundle childLevel2 = generateSimpleProductBundle(TESTTYPE);
		childLevel2.setCategoryAsDefault(getScenario().getCategory());
		getPersisterFactory().getCatalogTestPersister().persistSimpleProductSku("sku4", 2.98, "CAD", true, childLevel2, getScenario().getWarehouse());
		childLevel1.addConstituent(createDefaultConstituent(childLevel2));
		
		Product childLevel3 = generateSimpleProduct(TESTTYPE);
		childLevel3.setCategoryAsDefault(getScenario().getCategory());
		getPersisterFactory().getCatalogTestPersister().persistSimpleProductSku("sku7", 3.23, "CAD", true, childLevel3, getScenario().getWarehouse());
		childLevel2.addConstituent(createDefaultConstituent(childLevel3));
		
		productService.saveOrUpdate(bundle);
		
		getPersisterFactory().getCatalogTestPersister().persistSimpleProductSku("sku5", 10.23, "CAD", true, bundle, getScenario().getWarehouse());
	}

	private void persist1LevelBundle() {
		ProductBundle bundle = generateSimpleProductBundle(TESTTYPE);
		bundle.setSelectionRule(new SelectionRuleFactoryImpl().createSelectAllRule());

		Product product = generateSimpleProduct(TESTTYPE);
		
		productService.saveOrUpdate(product);
		bundle.addConstituent(createDefaultConstituent(product));

//		productService.saveOrUpdate(bundle);
		
		getPersisterFactory().getCatalogTestPersister().persistSimpleProductSku("skuCode", 10.23, "CAD", true, bundle, getScenario().getWarehouse());
		
		getPersisterFactory().getCatalogTestPersister().persistSimpleProductSku("sku2", 1.56, "CAD", true, product, getScenario().getWarehouse());
	}
	
	private ProductBundle generateSimpleProductBundle(final String productType) {
		return generateSimpleProductBundle(productType, Utils.uniqueCode("bundle"));
	}

	private ProductBundle generateSimpleProductBundle(final String productType, final String bundleCode) {
		return getPersisterFactory().getCatalogTestPersister().createSimpleProductBundle(productType, bundleCode, scenario.getCatalog(),
				scenario.getCategory(), getTac()
				.getPersistersFactory().getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS));
	}
	/**
	 * Creates a bundle constituent with the quantity of 1.
	 * 
	 * @param product
	 * @return bundle constituent
	 */
	private BundleConstituent createDefaultConstituent(final Product product) {
		return constituentFactory.createBundleConstituent(product, 1);
	}
	
	private Product generateSimpleProduct(final String productType) {
		return generateSimpleProduct(productType, Utils.uniqueCode("product"));
	}

	private Product generateSimpleProduct(final String productType, final String productCode) {
		return getPersisterFactory().getCatalogTestPersister().createSimpleProduct(productType, productCode, scenario.getCatalog(), getTac()
				.getPersistersFactory().getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS), scenario.getCategory());
	}

	private Product persistProductWithSku() {
		TaxCode taxCode = getPersisterFactory().getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS);
		Currency currency = priceListHelperService.getDefaultCurrencyFor(getScenario().getCatalog());
		int orderLimit = Integer.MAX_VALUE;
		Product product = getPersisterFactory().getCatalogTestPersister().persistProductWithSku(
				getScenario().getCatalog(), 
				getScenario().getCategory(), 
				getScenario().getWarehouse(), 
				BigDecimal.TEN, 
				currency, 
				"brandCode", 
				"productCode", 
				"productName", 
				"skuCode", 
				taxCode.getCode(), 
				AvailabilityCriteria.ALWAYS_AVAILABLE, 
				orderLimit);
		return product;
		
	}
}
