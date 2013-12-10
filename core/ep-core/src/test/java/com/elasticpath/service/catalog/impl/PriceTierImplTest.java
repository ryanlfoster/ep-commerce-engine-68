/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.catalog.impl;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.impl.PriceTierImpl;

/** Test case for <code>PriceTierImpl</code>. */
public class PriceTierImplTest {

	private static final String FIFTY = "50";
    private static final String ONE_HUNDRED = "100";
    private PriceTier priceTier;

	/**
	 * Prepare for the tests.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		priceTier = new PriceTierImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.PriceTierImpl.getComputedPrice()'.
	 */
	@Test
	public void testGetComputedPrice1() {
		BigDecimal highPrice = new BigDecimal(ONE_HUNDRED);
		BigDecimal lowPrice = new BigDecimal(FIFTY);

		priceTier.setComputedPrice(highPrice);
		priceTier.setComputedPrice(lowPrice);

		assertEquals(lowPrice, priceTier.getComputedPrice());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.PriceTierImpl.getComputedPrice()'.
	 */
    @Test
	public void testGetComputedPrice2() {
		BigDecimal highPrice = new BigDecimal(ONE_HUNDRED);
		BigDecimal lowPrice = new BigDecimal(FIFTY);

		priceTier.setComputedPrice(lowPrice);
		priceTier.setComputedPrice(highPrice);

		assertEquals(lowPrice, priceTier.getComputedPrice());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.PriceTierImpl.getComputedPrice()'.
	 */
    @Test
	public void testGetComputedPrice3() {
		BigDecimal highPrice = new BigDecimal(ONE_HUNDRED);
		BigDecimal lowPrice = new BigDecimal(FIFTY);

		priceTier.setComputedPrice(lowPrice);
		priceTier.clearComputedPrice();
		priceTier.setComputedPrice(highPrice);

		assertEquals(highPrice, priceTier.getComputedPrice());
	}
	
	/**
	 * Test getLowestPrice when sale price is lower than list.
	 */
    @Test
	public void testGetLowestPrice1() {
        BigDecimal listPrice = new BigDecimal(ONE_HUNDRED);
        BigDecimal salePrice = new BigDecimal(FIFTY);

        priceTier.setListPrice(listPrice);
        priceTier.setSalePrice(salePrice);
        
        assertEquals(salePrice, priceTier.getLowestPrice());
	}

	/**
	 * Test getLowestPrice when sale price is higher than list.
	 */
    @Test
	public void testGetLowestPrice2() {
	    BigDecimal listPrice = new BigDecimal(FIFTY);
	    BigDecimal salePrice = new BigDecimal(ONE_HUNDRED);
	    
	    priceTier.setListPrice(listPrice);
	    priceTier.setSalePrice(salePrice);
	    
	    assertEquals(listPrice, priceTier.getLowestPrice());
	}
	
    /**
     * Test getLowestPrice when list price is null.
     */
    @Test
    public void testGetLowestPrice3() {
        BigDecimal listPrice = null;
        BigDecimal salePrice = new BigDecimal(FIFTY);

        priceTier.setListPrice(listPrice);
        priceTier.setSalePrice(salePrice);
        
        assertEquals(salePrice, priceTier.getLowestPrice());
    }

    /**
     * Test getLowestPrice when sale price is null.
     */
    @Test
    public void testGetLowestPrice4() {
        BigDecimal listPrice = new BigDecimal(ONE_HUNDRED);
        BigDecimal salePrice = null;

        priceTier.setListPrice(listPrice);
        priceTier.setSalePrice(salePrice);
        
        assertEquals(listPrice, priceTier.getLowestPrice());
    }

    /**
     * Test getLowestPrice when both prices are null.
     */
    @Test
    public void testGetLowestPrice5() {
        BigDecimal listPrice = null;
        BigDecimal salePrice = null;

        priceTier.setListPrice(listPrice);
        priceTier.setSalePrice(salePrice);
        
        assertEquals(null, priceTier.getLowestPrice());
    }

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.PriceTierImpl.setComputedPrice()'.
	 */
    @Test
	public void testSetComputedPriceNegative() {
		BigDecimal negativePrice = new BigDecimal("-100");
		priceTier.setComputedPrice(negativePrice);
		assertEquals(0, (BigDecimal.ZERO).compareTo(priceTier.getComputedPrice()));
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.PriceTierImpl.setSalePrice()'.
	 */
    @Test
	public void testSetSalePriceNegative() {
		BigDecimal negativePrice = new BigDecimal("-100");
		priceTier.setSalePrice(negativePrice);
		assertEquals(0, (BigDecimal.ZERO).compareTo(priceTier.getSalePrice()));
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.PriceTierImpl.setListPrice()'.
	 */
    @Test
	public void testSetListPriceNegative() {
		BigDecimal negativePrice = new BigDecimal("-100");
		priceTier.setListPrice(negativePrice);
		assertEquals(0, (BigDecimal.ZERO).compareTo(priceTier.getListPrice()));
	}

    /**
     * Test compare when both prices are equal.
     */
    @Test
    public void testCompare1() {
        priceTier.setListPrice(new BigDecimal(ONE_HUNDRED));
        
        PriceTier priceTier2 = new PriceTierImpl();
        priceTier2.setListPrice(new BigDecimal(ONE_HUNDRED));
        
        assertEquals(0, priceTier.compareTo(priceTier2));
    }

    /**
     * Test compare when left price is lower.
     */
    @Test
    public void testCompare2() {
        priceTier.setListPrice(new BigDecimal(FIFTY));
        
        PriceTier priceTier2 = new PriceTierImpl();
        priceTier2.setListPrice(new BigDecimal(ONE_HUNDRED));
        
        assertEquals(-1, priceTier.compareTo(priceTier2));
    }
    
    /**
     * Test compare when left price is higher.
     */
    @Test
    public void testCompare3() {
        priceTier.setListPrice(new BigDecimal(ONE_HUNDRED));
        
        PriceTier priceTier2 = new PriceTierImpl();
        priceTier2.setListPrice(new BigDecimal(FIFTY));
        
        assertEquals(1, priceTier.compareTo(priceTier2));
    }
    
    /**
     * Test compare when left price is null.
     */
    @Test
    public void testCompare4() {
        PriceTier priceTier2 = new PriceTierImpl();
        priceTier2.setListPrice(new BigDecimal(ONE_HUNDRED));
        
        assertEquals(-1, priceTier.compareTo(priceTier2));
    }

    /**
     * Test compare when right price is null.
     */
    @Test
    public void testCompare5() {
        priceTier.setListPrice(new BigDecimal(ONE_HUNDRED));
        
        PriceTier priceTier2 = new PriceTierImpl();
        
        assertEquals(1, priceTier.compareTo(priceTier2));
    }

    /**
     * Test compare when both prices are null.
     */
    @Test
    public void testCompare6() {
        PriceTier priceTier2 = new PriceTierImpl();
        
        assertEquals(0, priceTier.compareTo(priceTier2));
    }

}
