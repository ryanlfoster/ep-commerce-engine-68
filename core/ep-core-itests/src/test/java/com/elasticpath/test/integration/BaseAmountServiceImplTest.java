package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.pricing.service.PriceListHelperService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.exceptions.DuplicateBaseAmountException;
import com.elasticpath.service.pricing.BaseAmountFactory;
import com.elasticpath.service.pricing.BaseAmountService;
import com.elasticpath.service.pricing.PriceListDescriptorService;
import com.elasticpath.service.pricing.exceptions.BaseAmountNotExistException;
import com.elasticpath.test.persister.TaxTestPersister;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Test the BaseAmountServiceImpl.
 */
public class BaseAmountServiceImplTest extends BasicSpringContextTest {

	@Autowired
	private PriceListDescriptorService priceListDescriptorService;

	@Autowired
	private BaseAmountService baseAmountService;

	@Autowired
	private BaseAmountFactory baFactory;

	private String plGuid;

	private SimpleStoreScenario scenario;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		final PriceListDescriptor descriptor = getBeanFactory().getBean(ContextIdNames.PRICE_LIST_DESCRIPTOR);
		plGuid = descriptor.getGuid();
		descriptor.setCurrencyCode(Currency.getInstance(Locale.CANADA).getCurrencyCode());
		descriptor.setName("TEST");
		priceListDescriptorService.add(descriptor);
		scenario = getTac().useScenario(SimpleStoreScenario.class);
	}

	/**
	 * Test creating a pricelist and adding baseamounts.
	 */
	@DirtiesDatabase
	@Test
	public void testBasic() {
		//Create a new BaseAmount
		final String objectGuid = "OBJ_GUID";
		final String objectType = "SKU";
		final BigDecimal qty = new BigDecimal("5");
		final BigDecimal listPrice = new BigDecimal("13");
		final BigDecimal salePrice = new BigDecimal("12");
		final BaseAmount amount = baFactory.createBaseAmount(null, objectGuid, objectType, qty, listPrice, salePrice, plGuid);

		assertNotNull("The BaseAmount created by the factory should have a non-null GUID", amount.getObjectGuid());
		//Persist the new BaseAmount
		final BaseAmount saved = baseAmountService.add(amount);
		assertEquals(amount.getGuid(), saved.getGuid());
		assertEquals(qty, saved.getQuantity());
		assertEquals(objectGuid, saved.getObjectGuid());
		assertEquals(objectType, saved.getObjectType());
	}

	/**
	 * Test adding multiple baseamounts to the same price list with varying fields.
	 */
	@Ignore
	@DirtiesDatabase
	@Test
	public void testMultipleAdd() {
		//Create a new BaseAmount
		final String objectGuid = "OBJ_GUID";
		final String objectType = "SKU";
		final BigDecimal qty = new BigDecimal("5");
		final BigDecimal listPrice = new BigDecimal("13");
		final BigDecimal salePrice = new BigDecimal("12");
		final BaseAmount amount = baFactory.createBaseAmount(null, objectGuid, objectType, qty, listPrice, salePrice, plGuid);
		final BaseAmount amount2 = baFactory.createBaseAmount(null, objectGuid + "DIFF", objectType, qty, listPrice, salePrice, plGuid);
		final BaseAmount amount3 = baFactory.createBaseAmount(null, objectGuid, objectType  + "DIFF", qty, listPrice, salePrice, plGuid);
		final BaseAmount amount4 = baFactory.createBaseAmount(null, objectGuid, objectType, qty.add(BigDecimal.ONE), listPrice, salePrice, plGuid);

		baseAmountService.add(amount);
		baseAmountService.add(amount2);
		baseAmountService.add(amount3);
		baseAmountService.add(amount4);
	}

	/**
	 * Test duplicate GUID.
	 */
	@DirtiesDatabase
	@Test(expected = DuplicateBaseAmountException.class)
	public void testDuplicateGuid() {
		//Create a new BaseAmount
		final String objectGuid = "OBJ_GUID";
		final String objectType = "SKU";
		final BigDecimal qty = new BigDecimal("5");
		final BigDecimal listPrice = new BigDecimal("13");
		final BigDecimal salePrice = new BigDecimal("12");
		final BaseAmount amount = baFactory.createBaseAmount("BA1", objectGuid, objectType, qty, listPrice, salePrice, plGuid);
		final BaseAmount amount2 = baFactory.createBaseAmount("BA1", objectGuid, objectType, qty, listPrice, salePrice, plGuid);
		baseAmountService.add(amount);
		baseAmountService.add(amount2);
	}

	/**
	 * Test duplicate key fields.
	 */
	@DirtiesDatabase
	@Test(expected = DuplicateBaseAmountException.class)
	public void testDuplicateKeyFields() {
		//Create a new BaseAmount
		final String objectGuid = "OBJ_GUID";
		final String objectType = "SKU";
		final BigDecimal qty = new BigDecimal("5");
		final BigDecimal listPrice = new BigDecimal("13");
		final BigDecimal salePrice = new BigDecimal("12");
		final BaseAmount amount = baFactory.createBaseAmount(null, objectGuid, objectType, qty, listPrice, salePrice, plGuid);
		final BaseAmount amount2 = baFactory.createBaseAmount(null, objectGuid, objectType, qty, listPrice, salePrice, plGuid);
		baseAmountService.add(amount);
		baseAmountService.add(amount2);
	}

	/**
	 * Test basic CRUD ops for baseAmount dao.
	 */
	@DirtiesDatabase
	@Test
	public void testCRUD() {
		final BaseAmount amount = baFactory.createBaseAmount(null, "OBJ_GUID", "SKU",
				new BigDecimal(12), new BigDecimal(13), new BigDecimal(11), plGuid);
		assertNotNull(amount.getObjectGuid());
		final BaseAmount savedAmount = baseAmountService.add(amount);
		assertEquals(savedAmount.getGuid(), baseAmountService.findByGuid(amount.getGuid()).getGuid());

		savedAmount.setListValue(BigDecimal.TEN);
		savedAmount.setSaleValue(BigDecimal.ONE);
		BaseAmount updatedAmount = null;
		try {
			updatedAmount = baseAmountService.updateWithoutLoad(savedAmount);
		} catch (final BaseAmountNotExistException e) {
			fail("Couldn't find BA on update");
		}
		assertEquals(savedAmount.getListValue(), updatedAmount.getListValue());
		assertEquals(savedAmount.getSaleValue(), updatedAmount.getSaleValue());

		assertEquals("The updated BaseAmount should have the same guid as the original baseAmount.",
				savedAmount.getGuid(), updatedAmount.getGuid());
		assertTrue("The BaseAmount returned from update() should have the same Quantity as the BaseAmount passed into the method.",
				savedAmount.getQuantity().compareTo(updatedAmount.getQuantity()) == 0);
		assertEquals("The BaseAmount returned from update() should have the same ObjectGuid as the BaseAmount passed into the method.",
				savedAmount.getObjectGuid(), updatedAmount.getObjectGuid());
		assertEquals("The BaseAmount returned from update() should have the same ObjectType as the BaseAmount passed into the method.",
				savedAmount.getObjectType(), updatedAmount.getObjectType());

		baseAmountService.delete(updatedAmount);
	}
	/**
	 * Test the delete of baseAmount that is linked to a product in a catalog.
	 */
	@DirtiesDatabase
	@Test
	public void testDeleteWithAssignedToCatalog() {
		final String baseAmountGuid = "TO_DELETE_BA_GUID";

		final PriceListHelperService priceListHelperService = getBeanFactory().getBean(ContextIdNames.PRICE_LIST_HELPER_SERVICE);
		final Currency currency = priceListHelperService.getDefaultCurrencyFor(scenario.getCatalog());

		final String priceListDescriptorGuid = scenario.getCatalog().getCode() + "_" + currency.getCurrencyCode();
		final PriceListDescriptor priceListDescriptor = priceListDescriptorService.findByGuid(priceListDescriptorGuid);

		assertNotNull(priceListDescriptor);
		final Product product = getTac().getPersistersFactory().getCatalogTestPersister().persistSimpleProduct("BASE_AMOUNT_TEST_PRODUCT", "newTestType",
									scenario.getCatalog(), scenario.getCategory(),
									getTac().getPersistersFactory().getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS));
		getTac().getPersistersFactory().getCatalogTestPersister().addProductBaseAmount(baseAmountGuid, priceListDescriptorGuid,
														product.getCode(), new BigDecimal(13), new BigDecimal(13), new BigDecimal(11));

		final BaseAmount savedAmount =  baseAmountService.findByGuid(baseAmountGuid);
		assertNotNull(savedAmount.getGuid());

		baseAmountService.delete(savedAmount);
		assertNull("BaseAmount is not longer in database after delele", baseAmountService.findByGuid(baseAmountGuid));
	}
	/**
	 * Test that with no UIDPK set to the BaseAmount, we can still do an update.
	 * This captures the case for when a BaseAmountDTO is transformed to a BaseAmount over
	 * client side service, persistence information is lost. Update operation still should perform
	 * because we have GUID identifier.
	 */
	@DirtiesDatabase
	@Test
	@SuppressWarnings("deprecation")
	public void testUpdateWithNoUid() {
		final BaseAmount amount = baFactory.createBaseAmount(null, "OBJ_GUID", "SKU",
				new BigDecimal(12), new BigDecimal(13), new BigDecimal(11), plGuid);
		assertNotNull(amount.getObjectGuid());
		final BaseAmount savedAmount = baseAmountService.add(amount);
		assertEquals(savedAmount.getGuid(), baseAmountService.findByGuid(amount.getGuid()).getGuid());
		savedAmount.setListValue(BigDecimal.TEN);
		savedAmount.setSaleValue(BigDecimal.ONE);
		//Set UID to 0. i.e. looks unpersisted
		savedAmount.setUidPk(0);
		BaseAmount updatedAmount = null;
		try {
			updatedAmount = baseAmountService.update(savedAmount);
		} catch (final BaseAmountNotExistException e) {
			fail("Couldn't find BA on update");
		}
		assertEquals(savedAmount.getListValue(), updatedAmount.getListValue());
		assertTrue(savedAmount.getSaleValue().compareTo(updatedAmount.getSaleValue()) == 0);

		assertEquals("The updated BaseAmount should have the same guid as the original baseAmount.",
				savedAmount.getGuid(), updatedAmount.getGuid());
		assertTrue("The BaseAmount returned from update() should have the same Quantity as the BaseAmount passed into the method.",
				savedAmount.getQuantity().compareTo(updatedAmount.getQuantity()) == 0);
		assertEquals("The BaseAmount returned from update() should have the same ObjectGuid as the BaseAmount passed into the method.",
				savedAmount.getObjectGuid(), updatedAmount.getObjectGuid());
		assertEquals("The BaseAmount returned from update() should have the same ObjectType as the BaseAmount passed into the method.",
				savedAmount.getObjectType(), updatedAmount.getObjectType());

		baseAmountService.delete(updatedAmount);
	}


	/**
	 * Test that if we get a BaseAmount to update, but GUID doesn't match any existing BaseAmounts,
	 * a sensible exception is thrown.
	 * @throws BaseAmountNotExistException is expected
	 */
	@DirtiesDatabase
	@Test(expected = BaseAmountNotExistException.class)
	@SuppressWarnings("deprecation")
	public void testExceptionOnUpdate() throws BaseAmountNotExistException {
		final BaseAmount amount2 = baFactory.createBaseAmount("NON_EXISTING_GUID", "OBJ_GUID", "SKU",
				new BigDecimal(12), new BigDecimal(13), new BigDecimal(11), plGuid);
		baseAmountService.update(amount2);
	}

	@DirtiesDatabase
	@Test
	public void testFindUidByGuid() {
	     boolean result = baseAmountService.guidExists("nonExistentBaseAmount");
	    assertFalse("base amount should not exist", result);

	       //Create a new BaseAmount
        final String objectGuid = "OBJ_GUID";
        final String objectType = "SKU";
        final BigDecimal qty = new BigDecimal("5");
        final BigDecimal listPrice = new BigDecimal("13");
        final BigDecimal salePrice = new BigDecimal("12");
        final BaseAmount amount = baFactory.createBaseAmount(null, objectGuid, objectType, qty, listPrice, salePrice, plGuid);
        final BaseAmount addedBaseAmount = baseAmountService.add(amount);


         result = baseAmountService.guidExists(addedBaseAmount.getGuid());
        assertTrue("base amount should exist", result);


	}
}
