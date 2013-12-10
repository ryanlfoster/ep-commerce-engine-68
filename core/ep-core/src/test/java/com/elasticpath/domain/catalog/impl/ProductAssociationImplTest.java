/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.misc.impl.RandomGuidImpl;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test cases for <code>ProductAssociationImpl</code>.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports" })
public class ProductAssociationImplTest {

	private static final long TIME_UNIT = 20000;

	private static final int TEST_ORDERING = 5;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ProductAssociation productAssociation;

    private BeanFactory beanFactory;

    private BeanFactoryExpectationsFactory expectationsFactory;

	/**
	 * Prepare for each test.
	 * 
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		productAssociation = new ProductAssociationImpl();
		beanFactory = context.mock(BeanFactory.class);
		expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
	}

	@After
	public void tearDown() {
		expectationsFactory.close();
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductAssociationImpl.setAssociationType(int)'.
	 */
	@Test
	public void testGetSetAssociationType() {
		productAssociation.setAssociationType(ProductAssociation.UP_SELL);
		assertEquals(ProductAssociation.UP_SELL, productAssociation.getAssociationType());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductAssociationImpl.setTargetProduct(Product)'.
	 */
    @Test
	public void testGetSetTargetProduct() {
		Product product = new ProductImpl();
		productAssociation.setTargetProduct(product);
		assertSame(product, productAssociation.getTargetProduct());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductAssociationImpl.getStartDate()'.
	 */
    @Test
	public void testGetSetStartDate() {
		Date testDate = new Date();
		productAssociation.setStartDate(testDate);
		assertSame(testDate, productAssociation.getStartDate());
		
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductAssociationImpl.getStartDate()'.
	 */
    @Test
	public void testGetSetStartDateAfterEndDate() {
		Date startDate = new Date();
		Date endDate = new Date(startDate.getTime() + TIME_UNIT);
		productAssociation.setStartDate(startDate);
		productAssociation.setEndDate(endDate);

		Date badStartDate = new Date(endDate.getTime() + TIME_UNIT);

		try {
			productAssociation.setStartDate(badStartDate);
			fail("Expected an EpDomainException due to start date after end date");
		} catch (EpDomainException epde) {
			// Success
			assertNotNull(epde);
		}
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductAssociationImpl.getEndDate()'.
	 */
    @Test
	public void testGetSetEndDate() {
		Date startDate = new Date();
		Date endDate = new Date(startDate.getTime() + TIME_UNIT);
		productAssociation.setEndDate(endDate);
		assertSame(endDate, productAssociation.getEndDate());

		productAssociation.setStartDate(startDate);
		Date badEndDate = new Date(startDate.getTime() - TIME_UNIT);

		try {
			productAssociation.setEndDate(badEndDate);
			fail("Expected an EpDomainException due to end date < start date");
		} catch (EpDomainException epde) {
			// Success
			assertNotNull(epde);
		}

		productAssociation.setEndDate(null);
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductAssociationImpl.getDefaultQuantity()'.
	 */
    @Test
	public void testGetSetDefaultQuantity() {
		assertEquals(1, productAssociation.getDefaultQuantity());
		productAssociation.setDefaultQuantity(2);
		assertEquals(2, productAssociation.getDefaultQuantity());
		
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductAssociationImpl.getOrdering()'.
	 */
    @Test
	public void testGetSetOrdering() {
		productAssociation.setOrdering(TEST_ORDERING);
		assertEquals(TEST_ORDERING, productAssociation.getOrdering());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductAssociationImpl.isSourceProductDependent()'.
	 */
    @Test
	public void testGetSetIsSourceProductDependent() {
		assertFalse(productAssociation.isSourceProductDependent());
		productAssociation.setSourceProductDependent(true);
		assertTrue(productAssociation.isSourceProductDependent());
	}

	/**
	 * Test for isValid().
	 */
    @Test
	public void testIsValid() {
		Date beforeNow = new Date();
		beforeNow.setTime(beforeNow.getTime() - TIME_UNIT * 2);

		Date afterNow = new Date();
		afterNow.setTime(afterNow.getTime() + TIME_UNIT);

		productAssociation.setStartDate(beforeNow);
		assertTrue(productAssociation.isValid());

		productAssociation.setStartDate(afterNow);
		assertFalse(productAssociation.isValid());

		productAssociation.setStartDate(beforeNow);
		productAssociation.setEndDate(afterNow);
		assertTrue(productAssociation.isValid());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductAssociationImpl.isValidAssociationType(int)'.
	 */
    @Test
	public void testIsValidAssociationType() {
		assertTrue(productAssociation.isValidAssociationType(ProductAssociation.ACCESSORY));
		assertFalse(productAssociation.isValidAssociationType(Integer.MAX_VALUE));
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductAssociationImpl.equals()'.
	 */
    @Test
	public void testEqualsAndHashcode() {
		
		// Two different non-persistent associations - both have zero as their id, but are 
		// different in memory.
		ProductAssociationImpl assoc1 = new ProductAssociationImpl();
		ProductAssociationImpl assoc2 = new ProductAssociationImpl();

		assertFalse("Shouldn't be equal to null", assoc1.equals(null));  // NOPMD - position literal first in test
		assertFalse("Shouldn't be equal to a random object", assoc1.equals(new Object()));
		assertFalse("Shouldn't be equal to a string", assoc1.equals("association 1"));  // NOPMD - position literal first in test
		assertEquals("The same object must always be equal", assoc1, assoc1);
		assertFalse("Very likely wrong that hashcode should be zero", 0 == assoc1.hashCode());
		assertEquals("hashCode must be same with equal objects", assoc1.hashCode(), assoc1.hashCode());
		
		assertEquals("Associations with no data should be equal", assoc1, assoc2);
		
		final int atype5000 = 5000;
		final int atype9999 = 9999;
		// Fake up persistent association
		ProductAssociationImpl assoc3 = new ProductAssociationImpl();
		assoc3.setAssociationType(atype5000);
		assertEquals(assoc3, assoc3);

		// Fake up duplicate association instance (retrieved in another 
		// persistence session for example. 
		ProductAssociationImpl assoc4 = new ProductAssociationImpl();
		assoc4.setAssociationType(atype5000);
		
		assertEquals("These should match as they represent the same row in the database", assoc4, assoc3);
		assertEquals("hashCode must be the same for equal objects", assoc4.hashCode(), assoc3.hashCode());
		
		// Fake up different persistent association instance.
		ProductAssociationImpl assoc5 = new ProductAssociationImpl();
		assoc5.setAssociationType(atype9999);
		
		assertFalse("Shouldn't be equal to a different object", assoc5.equals(assoc3));
		assertFalse("Shouldn't be equal to a different object", assoc5.equals(assoc4));		
		assertEquals("Should be equal to itself", assoc5, assoc5);	
		assertEquals("hashCode must be the same for equal objects", assoc5.hashCode(), assoc5.hashCode());
	}
	
	/**
	 * Tests equals() for an associations in two different catalogs.
	 */
    @Test
	public void testEqualsWithDifferentCatalogs() {
		ProductAssociation assoc1 = new ProductAssociationImpl();
		ProductAssociation assoc2 = new ProductAssociationImpl();
		
		Catalog catalog1 = new CatalogImpl();
		catalog1.setCode("catalog1");
		Catalog catalog2 = new CatalogImpl();
		catalog2.setCode("catalog2");
		
		assoc1.setCatalog(catalog1);
		assoc2.setCatalog(catalog2);
		
		assertFalse("Two associations should not be equal if in different catalogs", assoc1.equals(assoc2));
		
		// assign the same catalog to assoc2
		assoc2.setCatalog(catalog1);
		
		assertEquals("Associations should be equal if in one and the same catalog", assoc1, assoc2);
	}

	/**
	 * tests adding/removing product associations to HashSet.
	 */
    @Test
	public void testEqualsHashcodeUsingHashSet() {
		Set<ProductAssociation> testSet = new HashSet<ProductAssociation>();
		ProductAssociation assoc1 = new ProductAssociationImpl();
		assoc1.setAssociationType(1);
		testSet.add(assoc1);
		ProductAssociation assoc2 = new ProductAssociationImpl();
		assoc2.setAssociationType(2);
		testSet.add(assoc2);
		testSet.add(assoc2);
		// make a persistent association that will return the same hash code as a non-persistent one
		ProductAssociation assocWithHashCode1 = new ProductAssociationImpl();
		assocWithHashCode1.setAssociationType(1);
		testSet.add(assocWithHashCode1);

		testSet.remove(assocWithHashCode1);
		testSet.remove(assocWithHashCode1);
		assertEquals(1, testSet.size());
		testSet.add(assocWithHashCode1);
		
		ProductAssociation assoc3 = new ProductAssociationImpl();
		final int three = 3;
		assoc3.setAssociationType(three);
		
		testSet.add(assoc3);
		testSet.add(assoc3);
		final int size = 3;
		assertEquals(size, testSet.size());
		testSet.remove(assoc3);
		final int sizeAfterRemove = 2;
		assertEquals(sizeAfterRemove, testSet.size());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.ProductAssociationImpl.setDefaultValues()'.
	 */
    @Test
	public void testInitialize() {
        expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.RANDOM_GUID, RandomGuidImpl.class);
        
		assertNull(productAssociation.getStartDate());
		productAssociation.initialize();
		assertNotNull(productAssociation.getStartDate());
	}

    /**
     * Test method for 'com.elasticpath.domain.catalog.impl.ProductAssociationImpl.deepCopy()'.
     */
    @Test
    public void testDeepCopy() {
    	expectationsFactory.allowingBeanFactoryGetBean(ContextIdNames.PRODUCT_ASSOCIATION, ProductAssociationImpl.class);

		Date now = new Date();
		Date tomorrow = DateUtils.addDays(now, 1);

    	productAssociation.setAssociationType(2);
    	productAssociation.setCatalog(new CatalogImpl());
    	productAssociation.setDefaultQuantity(1);
    	productAssociation.setEndDate(tomorrow);
    	productAssociation.setGuid("guid");
    	productAssociation.setOrdering(0);
    	productAssociation.setSourceProduct(new ProductImpl());
    	productAssociation.setSourceProductDependent(false);
    	productAssociation.setStartDate(now);
    	productAssociation.setTargetProduct(new ProductImpl());
    	productAssociation.setUidPk(1L);

    	ProductAssociation copy = productAssociation.deepCopy();
    	assertEquals(productAssociation, copy);
    }
	    
}
