/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.service.tax.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.impl.PriceImpl;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.LocalizedPropertiesImpl;
import com.elasticpath.domain.misc.impl.StandardMoneyFormatter;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemImpl;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.impl.TaxCategoryImpl;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.test.jmock.AbstractEPTestCase;

/**
 * Tests that the TaxCalculationResultImpl methods work as expected.
 */
public class TaxCalculationResultImplTest extends AbstractEPTestCase {

	private static final String OTHERSKU = "othersku";
	private static final String MYSKU = "mysku";

	private static final Currency CAD = Currency.getInstance(Locale.CANADA);
	private TaxCalculationResultImpl taxCalculationResult;

	/**
	 * Sets up the test case for execution.
	 * 
	 * @throws java.lang.Exception if error occurs
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		stubGetBean(ContextIdNames.MONEY_FORMATTER, StandardMoneyFormatter.class);
		stubGetBean(ContextIdNames.LOCALIZED_PROPERTIES, LocalizedPropertiesImpl.class);
		taxCalculationResult = new TaxCalculationResultImpl();
		taxCalculationResult.setDefaultCurrency(CAD);
	}

	/**
	 * Tests that addTaxValue adds properly the amount to the total.
	 */
	@Test
	public void testAddTaxValue() {
		TaxCategory taxCategory = new TaxCategoryImpl();
		Money amount = newMoney("10");
		
		taxCalculationResult.addTaxValue(taxCategory, amount);
		
		assertTrue(taxCalculationResult.getTaxCategoriesIterator().hasNext());
		
		assertEquals(taxCategory, taxCalculationResult.getTaxCategoriesIterator().next());
		
		assertEquals(amount.getAmount(), taxCalculationResult.getTaxValue(taxCategory).getAmount());
		assertEquals(amount.getAmount(), taxCalculationResult.getTotalTaxes().getAmount());
	}

	/**
	 *
	 */
	private Money newMoney(final String value) {
		return MoneyFactory.createMoney(new BigDecimal(value).setScale(2), CAD);
	}

	/**
	 * Test that addShippingTax() adds properly taxes to the total shipping tax amount.
	 */
	@Test
	public void testAddShippingTax() {
		Money shippingTax = newMoney("3");
		taxCalculationResult.addShippingTax(shippingTax);
		
		assertEquals(shippingTax.getAmount(), taxCalculationResult.getShippingTax().getAmount());

		// add a little bit more
		taxCalculationResult.addShippingTax(shippingTax);
		
		assertEquals(shippingTax.getAmount().multiply(new BigDecimal("2")), taxCalculationResult.getShippingTax().getAmount());
		
	}

	/**
	 * Test method for {@link com.elasticpath.service.tax.impl.TaxCalculationResultImpl#getTaxValue(com.elasticpath.domain.tax.TaxCategory)}.
	 */
	@Test
	public void testGetTaxValue() {
		assertNotNull(taxCalculationResult.getTaxValue(null));
	}

	/**
	 * Test method for {@link com.elasticpath.service.tax.impl.TaxCalculationResultImpl#addToTaxInItemPrice(com.elasticpath.domain.misc.Money)}.
	 */
	@Test
	public void testAddToTaxInItemPrice() {
		taxCalculationResult.addToTaxInItemPrice(newMoney("6"));
		taxCalculationResult.addToTaxInItemPrice(newMoney("3"));
		
		assertEquals(newMoney("9"), taxCalculationResult.getTaxInItemPrice());
	}

	/**
	 * Test method for {@link com.elasticpath.service.tax.impl.TaxCalculationResultImpl#setDefaultCurrency(java.util.Currency)}.
	 */
	@Test
	public void testSetDefaultCurrency() {
		taxCalculationResult = new TaxCalculationResultImpl();
		
		try {
			taxCalculationResult.getTotalTaxes();
			fail("If no default currency has been set then no calculations should be possible.");
		} catch (Exception exc) {
			assertNotNull(exc);
		}
	}

	/**
	 * Test method for {@link com.elasticpath.service.tax.impl.TaxCalculationResultImpl#addItemTax(com.elasticpath.domain.misc.Money)}.
	 */
	@Test
	public void testAddItemTax() {
		taxCalculationResult.addItemTax(MYSKU, newMoney("3"));
		taxCalculationResult.addItemTax(OTHERSKU, newMoney("5"));
		
		assertEquals(new BigDecimal("8").setScale(2), taxCalculationResult.getTotalItemTax().getAmount());
	}
	
	/**
	 * Multiple calls to addItemTax with the same order sku should replace the old value for the sku with the new value for the sku. 
	 */
	@Test
	public void testAddItemTaxMultipleCalls() {
		taxCalculationResult.addItemTax(MYSKU, newMoney("3"));
		taxCalculationResult.addItemTax(MYSKU, newMoney("5"));
		taxCalculationResult.addItemTax(OTHERSKU, newMoney("5"));
		
		assertEquals("expected getTotalItemTax to be the sum of the latest calls to addItemTax for each sku.", 
				BigDecimal.TEN.setScale(2), taxCalculationResult.getTotalItemTax().getAmount());
		assertEquals("expected getLineItemTax to return last value set for sku.", newMoney("5"), taxCalculationResult.getLineItemTax(MYSKU));
		assertEquals("getLineItemTax should return value set by addItemTax", newMoney("5"), taxCalculationResult.getLineItemTax(OTHERSKU));
	}

	/**
	 * Test method for {@link com.elasticpath.service.tax.impl.TaxCalculationResultImpl#getLineItemTax(java.lang.String)}. Makes sure
	 * that we can look up the values we set for the skus.
	 */
	@Test
	public void testGetItemTax() {
		taxCalculationResult.addItemTax(MYSKU, newMoney("3"));
		taxCalculationResult.addItemTax(OTHERSKU, newMoney("5"));
		
		assertEquals("getLineItemTax should return value set by addItemTax", newMoney("3"), taxCalculationResult.getLineItemTax(MYSKU));
		assertEquals("getLineItemTax should return value set by addItemTax", newMoney("5"), taxCalculationResult.getLineItemTax(OTHERSKU));
	}
	
	/**
	 * Test method for {@link com.elasticpath.service.tax.impl.TaxCalculationResultImpl#getLineItemTax(java.lang.String)} where
	 * get is given an unknown sku.
	 */
	@Test
	public void testGetLineItemTaxNoSku() {
		taxCalculationResult.addItemTax(MYSKU, newMoney("3"));
		taxCalculationResult.addItemTax(OTHERSKU, newMoney("5"));
		
		assertEquals("expected requesting tax for unknown sku to return null", null, taxCalculationResult.getLineItemTax("someothersku"));
	}

	/**
	 * Test method for applyTaxes.  
	 */
	@Test
	public void testApplyTaxes() {
		taxCalculationResult.addItemTax(MYSKU, newMoney("3"));
		taxCalculationResult.addItemTax(OTHERSKU, newMoney("5"));
		Collection<ShoppingItem> lineItems = new ArrayList<ShoppingItem>(2);
		
		ProductSku myProductSku = new ProductSkuImpl();
		myProductSku.setSkuCode(MYSKU);
		
		ProductSku otherProductSku = new ProductSkuImpl();
		otherProductSku.setSkuCode(OTHERSKU);
		
		ShoppingItem myOrderSku = new OrderSkuImpl();
		myOrderSku.setProductSku(myProductSku);
		myOrderSku.setGuid(MYSKU);
		Price price = new PriceImpl();
		price.setListPrice(newMoney("10"));
		myOrderSku.setPrice(1, price);

		ShoppingItem otherOrderSku = new OrderSkuImpl();
		otherOrderSku.setProductSku(otherProductSku);
		otherOrderSku.setGuid(OTHERSKU);
		otherOrderSku.setPrice(1, price);
		
		lineItems.add(myOrderSku);
		lineItems.add(otherOrderSku);
		taxCalculationResult.applyTaxes(lineItems);
		
		assertEquals("expected tax for ordersku to be set to value given to addItemTax", new BigDecimal("3.00"), myOrderSku.getTax().getAmount());
		assertEquals("expected tax for ordersku to be set to value given to addItemTax", new BigDecimal("5.00"), otherOrderSku.getTax().getAmount());
	}

	/**
	 * test applyTaxes with a line item that wasn't included in calculation. Should not throw exception.
	 */
	@Test
	public void testApplyTaxesSkuNoPriceCodeSuccess() {
		taxCalculationResult.addItemTax(MYSKU, newMoney("3"));
		Collection<ShoppingItem> lineItems = new ArrayList<ShoppingItem>(2);
		
		ProductSku myProductSku = new ProductSkuImpl();
		myProductSku.setSkuCode(MYSKU);
		
		ProductSku otherProductSku = new ProductSkuImpl();
		otherProductSku.setSkuCode(OTHERSKU);
		
		ShoppingItem myOrderSku = new ShoppingItemImpl();
//		myOrderSku.setDefaultValues();
		myOrderSku.setProductSku(myProductSku);
		
		ShoppingItem otherOrderSku = new ShoppingItemImpl();
//		otherOrderSku.setDefaultValues();
		otherOrderSku.setProductSku(otherProductSku);
		
		lineItems.add(myOrderSku);
		lineItems.add(otherOrderSku);
	// in order exchange can be added item without price 	
		taxCalculationResult.applyTaxes(lineItems);

	}
	
	/**
	 * Test that addBeforeTaxItemPrice() properly adds the amount to the total value.
	 */
	@Test
	public void testAddBeforeTaxItemPrice() {
		taxCalculationResult.addBeforeTaxItemPrice(newMoney("5"));
		
		assertEquals(new BigDecimal("5").setScale(2), taxCalculationResult.getBeforeTaxSubTotal().getAmount());
	}

	/**
	 * Test method for {@link com.elasticpath.service.tax.impl.TaxCalculationResultImpl#addBeforeTaxShippingCost(com.elasticpath.domain.misc.Money)}.
	 */
	@Test
	public void testAddBeforeTaxShippingCost() {
		taxCalculationResult.addBeforeTaxShippingCost(newMoney("5"));
		
		assertEquals(new BigDecimal("5").setScale(2), taxCalculationResult.getBeforeTaxShippingCost().getAmount());
	}

	/**
	 * Tests that equals() works properly.
	 */
	@Test
	public void testEquals() {
		Currency cAD = Currency.getInstance(Locale.CANADA);
		
		TaxCalculationResultImpl obj1 = newTaxCalculationResult(cAD);
		
		TaxCalculationResultImpl obj2 = newTaxCalculationResult(cAD);
		
		assertEquals(obj1, obj2);
		assertEquals(obj2, obj1);
		assertFalse(obj1.equals(null)); //NOPMD
		
		assertEquals(obj1, obj1);
		
		// make objects not equal
		obj1.setBeforeTaxShippingCost(newMoney("34"));
		
		assertFalse(obj1.equals(obj2));
	}

	/**
	 *
	 * @return
	 */
	private TaxCalculationResultImpl newTaxCalculationResult(final Currency defaultCurrency) {
		TaxCalculationResultImpl result = new TaxCalculationResultImpl();
		result.setDefaultCurrency(defaultCurrency);
		return result;
	}
	
	/**
	 * Tests that hashCode() is implemented properly for it to be 
	 * used in a HashSet for example.
	 */
	@Test
	public void testHashCode() {
		Set<TaxCalculationResult> testSet = new HashSet<TaxCalculationResult>();
		Currency cAD = Currency.getInstance(Locale.CANADA);

		// add to identical objects
		TaxCalculationResultImpl result1 = newTaxCalculationResult(cAD);
		TaxCalculationResultImpl result2 = newTaxCalculationResult(cAD);
		
		testSet.add(result1);
		testSet.add(result2);
		
		assertEquals("Only one of the objects should be in the Set", 1, testSet.size());
		
		TaxCalculationResult taxCalcResult = result1;
		
		taxCalcResult.setDefaultCurrency(CAD);
		testSet.add(taxCalcResult);
		
		assertEquals(2, testSet.size());
	}
	
	/**
	 * Tests setTaxValue.
	 */
	@Test
	public void testSetTaxValue() {
		TaxCategory taxCategory = new TaxCategoryImpl();
		taxCalculationResult.setTaxValue(taxCategory, newMoney("3"));
		assertEquals(newMoney("3"), taxCalculationResult.getTaxValue(taxCategory));
	}

	/**
	 * Tests getTotalTaxes.
	 */
	@Test
	public void testGetTotalTaxes() {
		
		TaxCategory taxCategory = new TaxCategoryImpl();
		taxCategory.setName("tax_category1");
		taxCategory.setLocalizedProperties(new LocalizedPropertiesImpl());
		TaxCategory anotherTaxCategory = new TaxCategoryImpl();
		anotherTaxCategory.setName("tax_category2");
		anotherTaxCategory.setLocalizedProperties(new LocalizedPropertiesImpl());
		
		taxCalculationResult.setTaxValue(anotherTaxCategory, newMoney("15"));
		taxCalculationResult.addTaxValue(taxCategory, newMoney("5"));
		assertEquals(newMoney("20"), taxCalculationResult.getTotalTaxes());

		taxCalculationResult.setTaxValue(anotherTaxCategory, null);
		taxCalculationResult.setTaxValue(taxCategory, null);
		assertEquals(newMoney("0"), taxCalculationResult.getTotalTaxes());
	}
	
	/**
	 * Test tax calculation result representation as a string.
	 */
	@Test
	public void testToString() {
		assertNotNull(taxCalculationResult.toString());
		
	}
}
