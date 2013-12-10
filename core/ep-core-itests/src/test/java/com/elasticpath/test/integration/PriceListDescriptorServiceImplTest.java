package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;
import java.util.UUID;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.pricing.service.BaseAmountFilter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.service.pricing.BaseAmountFactory;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PriceListDescriptorService;

/**
 * Test the price list descriptor service.
 */
public class PriceListDescriptorServiceImplTest extends BasicSpringContextTest {

	private static final String DEFAULT_CURRENCY = "CAD";

	private static final String DEFAULT_DESCRIPTION = "TestDescription";

	@Autowired
	private PriceListDescriptorService priceListDescriptorService;
	
	@Autowired
	private BaseAmountService baseAmountService;

	@Autowired
	private BaseAmountFactory baFactory;

	/**
	 * Test basic CRUD ops for price list dao.
	 */
	@DirtiesDatabase
	@Test
	public void testCRUD() {
		String name = "PRICY LIST";
		PriceListDescriptor descriptor = getBeanFactory().getBean(ContextIdNames.PRICE_LIST_DESCRIPTOR);
		String guid = descriptor.getGuid();
		assertNotNull(guid);
		descriptor.setCurrencyCode(Currency.getInstance(Locale.CANADA).getCurrencyCode());
		descriptor.setName(name);
		assertEquals(name, priceListDescriptorService.add(descriptor).getName());
		
		PriceListDescriptor retrieved = priceListDescriptorService.findByGuid(guid);
		assertNotNull(retrieved);
		
		BaseAmount amount = baFactory.createBaseAmount(null, "OBJ_GUID", "SKU", new BigDecimal(12), new BigDecimal(13), new BigDecimal(11), guid);
		assertNotNull(amount.getObjectGuid());
		BaseAmount savedAmount = baseAmountService.add(amount);
		assertEquals(savedAmount.getGuid(), baseAmountService.findByGuid(amount.getGuid()).getGuid());
		
		baseAmountService.delete(savedAmount);
		
		priceListDescriptorService.delete(retrieved);
	}
	
	/**
	 * Test that BaseAmounts can be updated.
	 * @throws Exception on error
	 */
	@DirtiesDatabase
	@Test
	public void testUpdate() throws Exception {
		//Create a new PriceListDescriptor
		PriceListDescriptor descriptor = getBeanFactory().getBean(ContextIdNames.PRICE_LIST_DESCRIPTOR);
		String pldGuid = descriptor.getGuid();
		assertNotNull(pldGuid);
		descriptor.setCurrencyCode(Currency.getInstance(Locale.CANADA).getCurrencyCode());
		String name = "PRICY LIST2";
		descriptor.setName(name);
		//Check that the PLD is returned correctly from the save
		assertEquals("The persisted PriceListDescriptor should equal the one that was saved.", 
				descriptor, priceListDescriptorService.add(descriptor));
		//Check that we can find it again
		PriceListDescriptor retrieved = priceListDescriptorService.findByGuid(pldGuid);
		assertEquals("The found PLD should equal the one that was originally persisted.", descriptor, retrieved);
	}
	
	/**
	 * Test that the service can do filtered searches for BaseAmounts.
	 */
	@DirtiesDatabase
	@Test
	public void testBaseAmountFilterQuery() {
		PriceListDescriptor descriptor = getBeanFactory().getBean(ContextIdNames.PRICE_LIST_DESCRIPTOR);
		String guid1 = descriptor.getGuid();
		descriptor.setCurrencyCode(Currency.getInstance(Locale.CANADA).getCurrencyCode());
		descriptor.setName("test1");
		priceListDescriptorService.add(descriptor);

		descriptor = getBeanFactory().getBean(ContextIdNames.PRICE_LIST_DESCRIPTOR);
		String guid2 = descriptor.getGuid();
		descriptor.setCurrencyCode(Currency.getInstance(Locale.US).getCurrencyCode());
		descriptor.setName("test2");
		priceListDescriptorService.add(descriptor);

		BaseAmount amount1 = baFactory.createBaseAmount(null, "OBJ_GUID1", "SKU", new BigDecimal("4"), new BigDecimal("1.3"), new BigDecimal("1.2"), guid1);
		BaseAmount amount2 = baFactory.createBaseAmount(null, "OBJ_GUID2", "PRODUCT", new BigDecimal("2"), new BigDecimal("2.3"), new BigDecimal("2.2"), guid1);
		BaseAmount amount3 = baFactory.createBaseAmount(null, "OBJ_GUID3", "SKU", new BigDecimal("2"), new BigDecimal("3.3"), new BigDecimal("3.2"), guid2);
		
		
		baseAmountService.add(amount1);
		baseAmountService.add(amount2);
		baseAmountService.add(amount3);
		
		BaseAmountFilter filter = getBeanFactory().getBean(ContextIdNames.BASE_AMOUNT_FILTER);
		filter.setObjectGuid("OBJ_GUID2");
		filter.setObjectType("PRODUCT");
		filter.setQuantity(new BigDecimal("2"));
		
		Collection<BaseAmount> results = baseAmountService.findBaseAmounts(filter);
		assertSame(1, results.size());
		
		BaseAmount match = null;
		for (BaseAmount result : results) {
			if (result.getGuid().equals(amount2.getGuid())
					&& result.getPriceListDescriptorGuid().equals(amount2.getPriceListDescriptorGuid())) {
				match = result;
			}
		}
		assertNotNull(match);
	
		BaseAmountFilter filter2 = getBeanFactory().getBean(ContextIdNames.BASE_AMOUNT_FILTER);
		filter2.setQuantity(new BigDecimal("2"));
		
		Collection<BaseAmount> results2 = baseAmountService.findBaseAmounts(filter2);
		assertSame(2, results2.size());
		
	}
	
	/**
	 * Test that will retrieve all the price lists in the system, given whether or not to include hidden price lists.
	 */
	@DirtiesDatabase
	@Test
	public void testGetPriceListDescriptors() {
		String nonHiddenSalt = String.valueOf(UUID.randomUUID());
		PriceListDescriptor priceListNonHidden = getBeanFactory().getBean(ContextIdNames.PRICE_LIST_DESCRIPTOR);
		priceListNonHidden.setCurrencyCode(DEFAULT_CURRENCY);
		priceListNonHidden.setName("TestPL" + nonHiddenSalt);
		priceListNonHidden.setDescription(DEFAULT_DESCRIPTION + nonHiddenSalt);
		priceListDescriptorService.add(priceListNonHidden);

		String hiddenSalt = String.valueOf(UUID.randomUUID());
		PriceListDescriptor priceListHidden = getBeanFactory().getBean(ContextIdNames.PRICE_LIST_DESCRIPTOR);
		priceListHidden.setCurrencyCode("USD");
		priceListHidden.setName("TestPL" + hiddenSalt);
		priceListHidden.setDescription(DEFAULT_DESCRIPTION + hiddenSalt);
		priceListHidden.setHidden(true);
		priceListDescriptorService.add(priceListHidden);
				
		assertEquals(2, priceListDescriptorService.getPriceListDescriptors(true).size());
		assertEquals(1, priceListDescriptorService.getPriceListDescriptors(false).size());
		assertTrue(priceListDescriptorService.getPriceListDescriptors(false).iterator().next().equals(priceListNonHidden));
		
	}
}
