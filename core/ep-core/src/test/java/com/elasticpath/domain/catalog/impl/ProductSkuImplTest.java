/*
 * Copyright (c) Elastic Path Software Inc., 2006
 *
 */
package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.InventoryDetails;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.exception.EpInvalidValueBindException;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeValueGroupFactoryImpl;
import com.elasticpath.domain.attribute.impl.ExtAttributeValueFactoryTestImpl;
import com.elasticpath.domain.attribute.impl.ExtAttributeValueGroupFactoryTestImpl;
import com.elasticpath.domain.attribute.impl.ExtAttributeValueGroupTestImpl;
import com.elasticpath.domain.attribute.impl.ExtAttributeValueTestImpl;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.misc.RandomGuid;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.skuconfiguration.impl.JpaAdaptorOfSkuOptionValueImpl;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionImpl;
import com.elasticpath.domain.skuconfiguration.impl.SkuOptionValueImpl;
import com.elasticpath.inventory.InventoryKey;
import com.elasticpath.inventory.dao.InventoryDao;
import com.elasticpath.inventory.dao.InventoryJournalDao;
import com.elasticpath.inventory.domain.Inventory;
import com.elasticpath.inventory.domain.impl.InventoryImpl;
import com.elasticpath.inventory.impl.InventoryFacadeImpl;
import com.elasticpath.inventory.strategy.InventoryStrategy;
import com.elasticpath.inventory.strategy.impl.InventoryJournalRollupImpl;
import com.elasticpath.inventory.strategy.impl.JournalingInventoryStrategy;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.impl.ProductInventoryManagementServiceImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>ProductSkuImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.TooManyStaticImports", "PMD.ExcessiveImports" })
public class ProductSkuImplTest {

    private static final String EP_DOMAIN_EXCEPTION_MUST_NOT_BE_THROWN = "EpDomainException must not be thrown";

    private static final String EP_DOMAIN_EXCEPTION_MUST_BE_THROWN = "EpDomainException must be thrown";

    private static final int QTY_500 = 500;

    private static final int QTY_4 = 4;

    private static final int QTY_3 = 3;

    private static final int QUANTITY_ON_HAND = 5;

    private static final int RESERVED_QUANTITY = 3;

    private static final int REORDER_MINIMUM = 2;

    private static final String DOMAIN_EXCEPTION_EXPECTED = "EpDomainException expected.";

    private ProductSkuImpl productSkuImpl;

    private InventoryDao inventoryDao;
    private InventoryJournalDao inventoryJournalDao;
    
    @Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

    private static final long WAREHOUSE_UID = 100L;

    private Product product;

    private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory expectationsFactory;

    /**
     * Prepare for tests.
     *
     * @throws Exception in case of error happens
     */
    @Before
    public void setUp() throws Exception {
        beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
        product = context.mock(Product.class);

        context.checking(new Expectations() {
            {
                allowing(beanFactory).getBean(ContextIdNames.RANDOM_GUID); will(returnValue(new RandomGuidImpl()));
                allowing(product).setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
            }
        });


        productSkuImpl = new ProductSkuImpl();

        productSkuImpl.setGuid((new RandomGuidImpl()).toString());
        productSkuImpl.initialize();

        inventoryDao = context.mock(InventoryDao.class);
        inventoryJournalDao = context.mock(InventoryJournalDao.class);
    }

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

    /**
     * Test method for 'com.elasticpath.domain.impl.productSkuImpl.getStartDate()'.
     */
    @Test
    public void testGetStartDate() {
        // Start Date is supposed to have a default value .
        assertNotNull(productSkuImpl.getStartDate());
    }

    /**
     * Test method for 'com.elasticpath.domain.impl.productSkuImpl.setStartDate(Date)'.
     */
    @Test
    public void testSetStartDate() {
        final Date date = new Date();
        productSkuImpl.setStartDate(date);
        assertSame(date, productSkuImpl.getStartDate());
    }


    /**
     * Test method for 'com.elasticpath.domain.impl.productSkuImpl.getEndDate()'.
     */
    @Test
    public void testGetEndDate() {
        assertNull(productSkuImpl.getEndDate());
    }

    /**
     * Test method for 'com.elasticpath.domain.impl.productSkuImpl.setEndDate(Date)'.
     */
    @Test
    public void testSetEndDate() {
        final Date date = new Date();
        productSkuImpl.setEndDate(date);
        assertSame(date, productSkuImpl.getEndDate());
    }


    /**
     * Test method for 'com.elasticpath.domain.impl.ProductSkuImpl.getSkuCode()'.
     */
    @Test
    public void testGetSetSkuCode() {
        final String testSku1 = "testSku1";
        productSkuImpl.setSkuCode(testSku1);
        assertSame(testSku1, productSkuImpl.getSkuCode());

        final String testSku2 = "testSku2";
        productSkuImpl.setSkuCode(testSku2);
        assertSame(testSku2, productSkuImpl.getSkuCode());
    }

    /** Test method that checks for sufficient quantity. */
    @Test
    public void testSufficientQuantity() {
        final Map<String, Inventory> inventoryMap = new HashMap<String, Inventory>();
        productSkuImpl.setProductInternal(product);
        final Inventory inventory = getInitializedInventory();
        inventoryMap.put(productSkuImpl.getSkuCode(), inventory);
        final int expectedMapCalls = 5;

        context.checking(new Expectations() { {
        	exactly(expectedMapCalls).of(inventoryDao).getInventoryMap(inventoryMap.keySet(), WAREHOUSE_UID);
        	will(returnValue(inventoryMap));
        	
        	allowing(inventoryJournalDao).getRollup(with(any(InventoryKey.class)));
        	will(returnValue(new InventoryJournalRollupImpl()));
        	
        	allowing(product).getAvailabilityCriteria(); will(returnValue(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK));
        } });

        assertTrue(getProductInventoryManagementService().hasSufficientInventory(productSkuImpl, WAREHOUSE_UID, 1));
        assertTrue(getProductInventoryManagementService().hasSufficientInventory(productSkuImpl, WAREHOUSE_UID, 2));
        assertFalse(getProductInventoryManagementService().hasSufficientInventory(productSkuImpl, WAREHOUSE_UID, QTY_3));
        assertFalse(getProductInventoryManagementService().hasSufficientInventory(productSkuImpl, WAREHOUSE_UID, QTY_4));
        assertFalse(getProductInventoryManagementService().hasSufficientInventory(productSkuImpl, WAREHOUSE_UID, QTY_500));

        try {
            getProductInventoryManagementService().hasSufficientInventory(productSkuImpl, WAREHOUSE_UID, 0);
            fail(DOMAIN_EXCEPTION_EXPECTED);
        } catch (EpDomainException epde) {
            assertNotNull(epde);
        }

        try {
            getProductInventoryManagementService().hasSufficientInventory(productSkuImpl, WAREHOUSE_UID, -1);
            fail(DOMAIN_EXCEPTION_EXPECTED);
        } catch (EpDomainException epde) {
            assertNotNull(epde);
        }

    }

    /** Test method that checks for sufficient quantity. */
    @Test
    public void testSufficientQuantityWhenInfinite() {
        productSkuImpl.setProductInternal(product);

        context.checking(new Expectations() {
            {
                allowing(product).getAvailabilityCriteria(); will(returnValue(AvailabilityCriteria.ALWAYS_AVAILABLE));
            }
        });

        assertTrue(getProductInventoryManagementService().hasSufficientInventory(productSkuImpl, WAREHOUSE_UID, QTY_500));

    }

    /**
     * Returns an inventory object with these values:. QUANTITY_ON_HAND = 5; RESERVED_QUANTITY = 3; REORDER_MINIMUM = 2
     *
     * @return an initialized inventory object
     */
    private Inventory getInitializedInventory() {
        Inventory inventory = new InventoryImpl();

        inventory.setSkuCode(productSkuImpl.getSkuCode());

        inventory.setReorderMinimum(REORDER_MINIMUM);
        inventory.setQuantityOnHand(QUANTITY_ON_HAND);
        inventory.setReservedQuantity(RESERVED_QUANTITY);
        productSkuImpl.getProduct().setAvailabilityCriteria(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
        inventory.setWarehouseUid(WAREHOUSE_UID);

        return inventory;
    }

    private ProductInventoryManagementService getProductInventoryManagementService() {
        ProductInventoryManagementServiceImpl productInventoryManagementService = 
        	new ProductInventoryManagementServiceImpl();
        InventoryCalculatorImpl inventoryCalculator = new InventoryCalculatorImpl() {
            @Override
            protected InventoryDetails createInventoryDetails() {
                return new InventoryDetails();
            }
        };
        productInventoryManagementService.setInventoryCalculator(inventoryCalculator);
        
        JournalingInventoryStrategy journalingInventoryStrategy = new JournalingInventoryStrategy();
        journalingInventoryStrategy.setInventoryDao(inventoryDao);
        journalingInventoryStrategy.setInventoryJournalDao(inventoryJournalDao);
        
		InventoryFacadeImpl inventoryFacade = new InventoryFacadeImpl();
		Map<String, InventoryStrategy> strategies = new HashMap<String, InventoryStrategy>();
		strategies.put("allocatedjournaling", journalingInventoryStrategy);
		inventoryFacade.setStrategies(strategies);
		inventoryFacade.selectStrategy("allocatedjournaling");
		productInventoryManagementService.setInventoryFacade(inventoryFacade);
		
        return productInventoryManagementService;
    }

    /** Test method setProduct(Product). */
    @Test
    public void testSetProduct() {
        Product product = new ProductImpl();
        productSkuImpl.setProduct(product);
        assertSame(product, productSkuImpl.getProduct());
        assertTrue(product.getProductSkus().containsValue(productSkuImpl));
    }

    /** Test method setOptionValueMap(). */
    @Test
    public void testSetOptionValueMap() {
        final Map<String, SkuOptionValue> optionValueMap = new HashMap<String, SkuOptionValue>();
        final SkuOptionValueImpl skuOptionValueImpl = new SkuOptionValueImpl();
        optionValueMap.put("option_name", skuOptionValueImpl);
        productSkuImpl.setOptionValueMap(optionValueMap);
        assertSame(optionValueMap, productSkuImpl.getOptionValueMap());
        assertTrue(productSkuImpl.getOptionValues().contains(skuOptionValueImpl));
    }

    /** Test method setSkuOptionValue(). */
    @Test
    public void testSetSkuOptionValue() {
        final SkuOptionValue skuOptionValue1 = new SkuOptionValueImpl();
        final String valueCode1 = "aaa";
        skuOptionValue1.setOptionValueKey(valueCode1);

        final SkuOptionValue skuOptionValue2 = new SkuOptionValueImpl();
        final String valueCode2 = "bbb";
        skuOptionValue2.setOptionValueKey(valueCode2);

        final SkuOption skuOption = new SkuOptionImpl();
        skuOption.addOptionValue(skuOptionValue1);
        skuOption.addOptionValue(skuOptionValue2);
        
        final JpaAdaptorOfSkuOptionValueImpl adaptor = new JpaAdaptorOfSkuOptionValueImpl();
        context.checking(new Expectations() {
            {
                oneOf(beanFactory).getBean(ContextIdNames.SKU_OPTION_VALUE_JPA_ADAPTOR);
                	will(returnValue(adaptor));
            }
        });

        productSkuImpl.setSkuOptionValue(skuOption, valueCode1);
        assertEquals(skuOptionValue1.getOptionValueKey(), productSkuImpl.getSkuOptionValue(skuOption).getOptionValueKey());

        // Set sku option to another value
        productSkuImpl.setSkuOptionValue(skuOption, valueCode2);
        assertEquals(skuOptionValue2.getOptionValueKey(), productSkuImpl.getSkuOptionValue(skuOption).getOptionValueKey());

        // Set sku option to a undefined value
        try {
            productSkuImpl.setSkuOptionValue(skuOption, "undefinedCode");
            fail("EpInvalidValueBindException expected!");
        } catch (EpInvalidValueBindException e) {
            // succeed!
            assertNotNull(e);
        }
    }

    /**
     * Test method for 'com.elasticpath.domain.impl.ProductSkuImpl.getDigitalAsset()'.
     */
    @Test
    public void testGetSetDigitalAssets() {

        final DigitalAsset digitalAsset = new DigitalAssetImpl();
        productSkuImpl.setDigital(true);
        assertFalse(productSkuImpl.isDownloadable()); // Digital product can be without associated asset.
        productSkuImpl.setDigitalAsset(digitalAsset);
        assertSame(digitalAsset, productSkuImpl.getDigitalAsset());
        assertTrue(productSkuImpl.isDownloadable());
    }

    /**
     * Test method for 'com.elasticpath.domain.impl.productSkuImpl.setWidth(width)'.
     */
    @Test
    public void testGetSetWidth() {
        final BigDecimal width = BigDecimal.TEN;
        this.productSkuImpl.setWidth(width);
        assertSame(width, productSkuImpl.getWidth());
    }

    /**
     * Test method for 'com.elasticpath.domain.impl.productSkuImpl.setLength()'.
     */
    @Test
    public void testGetSetLength() {
        final BigDecimal length = BigDecimal.TEN;
        this.productSkuImpl.setLength(length);
        assertSame(length, productSkuImpl.getLength());
    }

    /**
     * Test method for 'com.elasticpath.domain.impl.productSkuImpl.setHeight()'.
     */
    @Test
    public void testGetSetHeight() {
        final BigDecimal height = BigDecimal.TEN;
        this.productSkuImpl.setHeight(height);
        assertSame(height, productSkuImpl.getHeight());
    }

    /**
     * Test method for 'com.elasticpath.domain.impl.productSkuImpl.setWeight()'.
     */
    @Test
    public void testGetSetWeight() {
        final BigDecimal weight = BigDecimal.TEN;
        this.productSkuImpl.setWeight(weight);
        assertSame(weight, productSkuImpl.getWeight());
    }

    /**
     * Test that when the product sku's skuCode is changed, the product updates its map of skuCode-productSkus to match.
     */
    @Test
    public void testChangeSkuCode() {
        Product product = new ProductImpl();
        final String skuCode1 = "testSkuCodeOne";
        final String skuCode2 = "testSkuCodeTwo";
        productSkuImpl.setSkuCode(skuCode1);
        productSkuImpl.setProduct(product);
        ProductSku skuInMap = productSkuImpl.getProduct().getProductSkus().get(skuCode1);
        assertNotNull("The product sku map should have been updated with the new skuCode as a key.", skuInMap);
        assertSame("The product sku map should contain the updated sku with the new skuCode as its key.", productSkuImpl, skuInMap);
        productSkuImpl.setSkuCode(skuCode2);
        // try to get the sku from the map using the OLD skuCode as the key
        skuInMap = productSkuImpl.getProduct().getProductSkus().get(skuCode1);
        assertNull("The old skuCode-productSku entry in the map should have been removed.", skuInMap);
        // try to get the sku from the map using the NEW skuCode as the key
        skuInMap = productSkuImpl.getProduct().getProductSkus().get(skuCode2);
        assertNotNull("The product sku map should have been updated with the new skuCode as a key.", skuInMap);
        assertSame("The product sku map should contain the updated sku with the new skuCode as its key.", productSkuImpl, skuInMap);
    }



    /**
     * Test that a new sku gets added to it's parent product's sku collection.
     */
    @Test
    public void testProductBidirectionalRelationshipForNewSku() {
        final Product mockProduct = context.mock(Product.class, "mockProduct");
        context.checking(new Expectations() {
            {
                oneOf(mockProduct).getProductSkus(); will(returnValue(Collections.emptyMap()));
                oneOf(mockProduct).addOrUpdateSku(productSkuImpl);
            }
        });
        productSkuImpl.setProduct(mockProduct);
    }

    /**
     * Test that a sku that is already in its parent product's sku collection doesn't get added again.
     */
    @Test
    public void testProductBidirectionalRelationshipForExistingSku() {
        final Product mockProduct = context.mock(Product.class, "mockProduct");
        final Map<String, ProductSku> skus = new HashMap<String, ProductSku>();
        skus.put(productSkuImpl.getGuid(), productSkuImpl);
        context.checking(new Expectations() {
            {
                oneOf(mockProduct).getProductSkus(); will(returnValue(skus));
                never(mockProduct).addOrUpdateSku(with(any(ProductSku.class)));
            }
        });
        productSkuImpl.setProduct(mockProduct);
    }

        /**
         * Tests validation trigger of ProductSkuImpl.setHeight().
         */
    @Test
    public void testNonNegativeHeight() {
        ProductSku productSku = new ProductSkuImpl();
        try {
            productSku.setHeight(BigDecimal.TEN.negate());
            fail(EP_DOMAIN_EXCEPTION_MUST_BE_THROWN);
        } catch (EpDomainException exception) {
            //exception expected
            assertNotNull(exception);
        }
        try {
            productSku.setHeight(BigDecimal.TEN);
        } catch (EpDomainException exception) {
            fail(EP_DOMAIN_EXCEPTION_MUST_NOT_BE_THROWN);
        }

        try {
            productSku.setHeight(BigDecimal.ZERO);
        } catch (EpDomainException exception) {
            fail(EP_DOMAIN_EXCEPTION_MUST_NOT_BE_THROWN);
        }

        try {
            productSku.setHeight(null);
        } catch (EpDomainException exception) {
            fail(EP_DOMAIN_EXCEPTION_MUST_NOT_BE_THROWN);
        }
    }

    /**
     * Tests validation trigger of ProductSkuImpl.setWeight().
     */
    @Test
    public void testNonNegativeWeight() {
        ProductSku productSku = new ProductSkuImpl();
        try {
        productSku.setWeight(BigDecimal.TEN.negate());
        fail(EP_DOMAIN_EXCEPTION_MUST_BE_THROWN);
        } catch (EpDomainException exception) {
            //exception expected
            assertNotNull(exception);
        }
        try {
            productSku.setWeight(BigDecimal.TEN);
        } catch (EpDomainException exception) {
            fail(EP_DOMAIN_EXCEPTION_MUST_NOT_BE_THROWN);
        }

        try {
            productSku.setWeight(BigDecimal.ZERO);
        } catch (EpDomainException exception) {
            fail(EP_DOMAIN_EXCEPTION_MUST_NOT_BE_THROWN);
        }

        try {
            productSku.setWeight(null);
        } catch (EpDomainException exception) {
            fail(EP_DOMAIN_EXCEPTION_MUST_NOT_BE_THROWN);
        }
    }

    /**
     * Tests validation trigger of ProductSkuImpl.setLength().
     */
    @Test
    public void testNonNegativeLength() {
        ProductSku productSku = new ProductSkuImpl();
        try {
        productSku.setLength(BigDecimal.TEN.negate());
        fail(EP_DOMAIN_EXCEPTION_MUST_BE_THROWN);
        } catch (EpDomainException exception) {
            //exception expected
            assertNotNull(exception);
        }
        try {
            productSku.setLength(BigDecimal.TEN);
        } catch (EpDomainException exception) {
            fail(EP_DOMAIN_EXCEPTION_MUST_NOT_BE_THROWN);
        }

        try {
            productSku.setLength(BigDecimal.ZERO);
        } catch (EpDomainException exception) {
            fail(EP_DOMAIN_EXCEPTION_MUST_NOT_BE_THROWN);
        }

        try {
            productSku.setLength(null);
        } catch (EpDomainException exception) {
            fail(EP_DOMAIN_EXCEPTION_MUST_NOT_BE_THROWN);
        }
    }

    /**
     * Tests validation trigger of ProductSkuImpl.setWidth().
     */
    @Test
    public void testNonNegativeWidth() {
        ProductSku productSku = new ProductSkuImpl();
        try {
        productSku.setWidth(BigDecimal.TEN.negate());
        fail(EP_DOMAIN_EXCEPTION_MUST_BE_THROWN);
        } catch (EpDomainException exception) {
            //exception expected
            assertNotNull(exception);
        }
        try {
            productSku.setWidth(BigDecimal.TEN);
        } catch (EpDomainException exception) {
            fail(EP_DOMAIN_EXCEPTION_MUST_NOT_BE_THROWN);
        }

        try {
            productSku.setWidth(BigDecimal.ZERO);
        } catch (EpDomainException exception) {
            fail(EP_DOMAIN_EXCEPTION_MUST_NOT_BE_THROWN);
        }

        try {
            productSku.setWidth(null);
        } catch (EpDomainException exception) {
            fail(EP_DOMAIN_EXCEPTION_MUST_NOT_BE_THROWN);
        }
    }

    /**
     * Test that the effective start date is calculated correctly.
     *
     * @throws ParseException in case of date parsing error.
     */
    @Test
    public void testEffectiveStartDate() throws ParseException {
    	ProductSku productSku = new ProductSkuImpl();
    	Product product = new ProductImpl();
    	productSku.setProduct(product);

        final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        product.setStartDate(yyyyMMdd.parse("2010-01-01"));
        productSku.setStartDate(null);
        Date effectiveDate = productSku.getEffectiveStartDate();
        assertEquals("The effective start date should be the non-null date", "2010-01-01", yyyyMMdd.format(effectiveDate));

        product.setStartDate(yyyyMMdd.parse("2010-01-02"));
        productSku.setStartDate(yyyyMMdd.parse("2010-03-01"));
        effectiveDate = productSku.getEffectiveStartDate();
        assertEquals("The effective start date should be the greater date", "2010-03-01", yyyyMMdd.format(effectiveDate));

        product.setStartDate(yyyyMMdd.parse("2010-01-03"));
        productSku.setStartDate(yyyyMMdd.parse("2009-01-01"));
        effectiveDate = productSku.getEffectiveStartDate();
        assertEquals("The effective start date should be the greater date", "2010-01-03", yyyyMMdd.format(effectiveDate));
    }

    /**
     * Test that the effective end date is calculated correctly.
     *
     * @throws ParseException in case of date parsing error.
     */
    @Test
    public void testEffectiveEndDate() throws ParseException {
    	ProductSku productSku = new ProductSkuImpl();
    	Product product = new ProductImpl();
    	productSku.setProduct(product);
    	
        final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

        product.setEndDate(null);
        productSku.setEndDate(null);
        Date effectiveDate = productSku.getEffectiveEndDate();
        assertEquals("The effective end date should be the null if both end dates are null", null, effectiveDate);

        product.setStartDate(yyyyMMdd.parse("2010-01-02"));

        product.setEndDate(yyyyMMdd.parse("2010-01-15"));
        productSku.setEndDate(null);
        effectiveDate = productSku.getEffectiveEndDate();
        assertEquals("The effective end date should be the non-null date", "2010-01-15", yyyyMMdd.format(effectiveDate));

        product.setEndDate(null);
        productSku.setEndDate(yyyyMMdd.parse("2010-03-05"));
        effectiveDate = productSku.getEffectiveEndDate();
        assertEquals("The effective end date should be the non-null date", "2010-03-05", yyyyMMdd.format(effectiveDate));

        product.setEndDate(yyyyMMdd.parse("2010-02-01"));
        productSku.setEndDate(yyyyMMdd.parse("2010-03-06"));
        effectiveDate = productSku.getEffectiveEndDate();
        assertEquals("The effective end date should be the lesser date", "2010-02-01", yyyyMMdd.format(effectiveDate));

        product.setEndDate(yyyyMMdd.parse("2010-04-01"));
        productSku.setEndDate(yyyyMMdd.parse("2010-01-05"));
        effectiveDate = productSku.getEffectiveEndDate();
        assertEquals("The effective end date should be the lesser date", "2010-01-05", yyyyMMdd.format(effectiveDate));

        product.setEndDate(yyyyMMdd.parse("2010-04-01"));
        productSku.setEndDate(yyyyMMdd.parse("2009-12-01"));
        effectiveDate = productSku.getEffectiveEndDate();
        assertEquals("The effective end date should be the start date if it would have been earlier", "2010-01-02", yyyyMMdd.format(effectiveDate));
    }
    
    /**
     * Test checking of date within a range.
     * 
     * @throws ParseException in case of date parsing error
     */
    @Test
    public void testIsWithinDateRange() throws ParseException {
        final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date dateToCheck = yyyyMMdd.parse("2010-03-03");
        
    	productSkuImpl.setStartDate(yyyyMMdd.parse("2010-02-02"));
    	productSkuImpl.setEndDate(null);
    	assertTrue("Sku with no end date that started before given date should be within range", productSkuImpl.isWithinDateRange(dateToCheck));
    	
    	productSkuImpl.setStartDate(yyyyMMdd.parse("2010-03-01"));
    	productSkuImpl.setEndDate(yyyyMMdd.parse("2010-12-31"));
    	assertTrue("Sku within range should be recognized as such", productSkuImpl.isWithinDateRange(dateToCheck));
    	
    	productSkuImpl.setStartDate(yyyyMMdd.parse("2010-02-01"));
    	productSkuImpl.setEndDate(yyyyMMdd.parse("2010-02-28"));
    	assertFalse("Sku with end date before given date should not be within range", productSkuImpl.isWithinDateRange(dateToCheck));
    	
    	productSkuImpl.setStartDate(yyyyMMdd.parse("2010-04-01"));
    	productSkuImpl.setEndDate(yyyyMMdd.parse("2010-05-31"));
    	assertFalse("Sku with start date after given date should not be within range", productSkuImpl.isWithinDateRange(dateToCheck));
    }

	@Test
	public void testIsWithinDateRangeWithNullStartDate() {
		productSkuImpl.setStartDate(null);
		productSkuImpl.setEndDate(null);
		assertTrue("Sku with null start date should be treated as open ended", productSkuImpl.isWithinDateRange(new Date()));

		productSkuImpl.setStartDate(null);
		productSkuImpl.setEndDate(new Date(System.currentTimeMillis() - 1));
		assertFalse("End dates should still apply even if start date is null", productSkuImpl.isWithinDateRange(new Date()));
	}

	/**
	 * Ensures that if you override the attribute value map in {@link ProductSku}, then the attribute value group map is
	 * initialized correctly.
	 */
	@Test
	public void testAttributeValueGroupMapExtension() {
		final Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
		final ElasticPath elasticPath = context.mock(ElasticPath.class, "elasticPath-1");

		ProductSku productSku = new ProductSkuImpl() {
			private static final long serialVersionUID = 1L;

			@Override
			public Map<String, AttributeValue> getAttributeValueMap() {
				return attributeValueMap;
			}

			@Override
			public void setAttributeValueMap(final Map<String, AttributeValue> attributeValueMap) {
				// should set the attribute value map, does nothing for this test
			}

			@Override
			public ElasticPath getElasticPath() {
				return elasticPath;
			}
		};

		context.checking(new Expectations() {
			{
				RandomGuid guid = context.mock(RandomGuid.class);

				allowing(elasticPath).getBean(ContextIdNames.RANDOM_GUID);
				will(returnValue(guid));
			}
		});

		productSku.initialize();
		assertSame("Map should be set on the group by the factory",
						attributeValueMap, productSku.getAttributeValueGroup().getAttributeValueMap());
	}

	/**
	 * Test that extension classes can override the AttributeValueGroup and ProductSkuAttributeValue implementation classes.
	 */
	@Test
	public void testThatExtensionClassesCanOverrideAttributeValueImplementations() {
		AttributeImpl attribute = new AttributeImpl();
		attribute.setAttributeType(AttributeType.SHORT_TEXT);
		attribute.setName("name");
		attribute.setKey("name");

		ExtProductSkuImpl sku = new ExtProductSkuImpl();
		sku.getAttributeValueGroup().setStringAttributeValue(attribute, null, "beanie-weenie");

		assertEquals("AttributeValueGroup implementation class should have been overridden",
				ExtAttributeValueGroupTestImpl.class, sku.getAttributeValueGroup().getClass());
		assertEquals("AttributeValueImpl implementation class should have been overridden",
				ExtAttributeValueTestImpl.class,
				sku.getAttributeValueGroup().getAttributeValue("name", null).getClass());
	}

	/**
	 * Faux Sku Domain Impl extension class.
	 */
	private static class ExtProductSkuImpl extends ProductSkuImpl {
		private static final long serialVersionUID = -7416852828431543838L;

		@Override
		protected AttributeValueGroupFactoryImpl getAttributeValueGroupFactory() {
			return new ExtAttributeValueGroupFactoryTestImpl(new ExtAttributeValueFactoryTestImpl());
		}
	}
}
