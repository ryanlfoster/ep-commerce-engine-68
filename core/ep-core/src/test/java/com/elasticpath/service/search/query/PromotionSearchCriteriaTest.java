package com.elasticpath.service.search.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Test <code>PromotionSearchCriteriaImpl</code>.
 */
public class PromotionSearchCriteriaTest {


	private PromotionSearchCriteria promotionSearchCriteria;

	@Before
	public void setUp() throws Exception {
		this.promotionSearchCriteria = new PromotionSearchCriteria();
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PromotionSearchCriteriaImpl.getRuleSetUid()'.
	 */
	@Test
	public void testGetRuleSetUid() {
		assertNull(this.promotionSearchCriteria.getRuleSetUid());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PromotionSearchCriteriaImpl.setRuleSetUid(String)'.
	 */
	@Test
	public void testSetRuleSetUid() {
		final String ruleSetUid = "100";
		this.promotionSearchCriteria.setRuleSetUid(ruleSetUid);
		assertEquals(ruleSetUid, this.promotionSearchCriteria.getRuleSetUid());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PromotionSearchCriteriaImpl.getStoreCode()'.
	 */
	@Test
	public void testGetStoreUid() {
		assertNull(this.promotionSearchCriteria.getStoreCode());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PromotionSearchCriteriaImpl.setStoreCode(String)'.
	 */
	@Test
	public void testSetStoreCode() {
		final String storeUid = "1";
		this.promotionSearchCriteria.setStoreCode(storeUid);
		assertEquals(storeUid, this.promotionSearchCriteria.getStoreCode());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PromotionSearchCriteriaImpl.getPromotionName()'.
	 */
	@Test
	public void testGetPromotionName() {
		assertNull(this.promotionSearchCriteria.getPromotionName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PromotionSearchCriteriaImpl.setPromotionName(String)'.
	 */
	@Test
	public void testSetPromotionName() {
		final String promoName = "Promotion Name";
		this.promotionSearchCriteria.setPromotionName(promoName);
		assertSame(promoName, this.promotionSearchCriteria.getPromotionName());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PromotionSearchCriteriaImpl.isEmpty()'.
	 */
	@Test
	public void testIsEmpty() {
		promotionSearchCriteria.setEnabled(null);
		promotionSearchCriteria.setActive(null);

		promotionSearchCriteria.setRuleSetUid(null);
		promotionSearchCriteria.setStoreCode(null);
		promotionSearchCriteria.setPromotionName(null);
		assertTrue("search criteria should be empty", promotionSearchCriteria.isEmpty());

		promotionSearchCriteria.setEnabled(true);
		assertFalse("not empty (1 property set)", promotionSearchCriteria.isEmpty());

		promotionSearchCriteria.setActive(true);
		assertFalse("not empty (2 properties set)", promotionSearchCriteria.isEmpty());

		promotionSearchCriteria.setEnabled(false);
		assertFalse("not empty (2 properties set, enabled already set)", promotionSearchCriteria.isEmpty());

		promotionSearchCriteria.setRuleSetUid("100");
		assertFalse("not empty (3 properties set)", promotionSearchCriteria.isEmpty());
		
		promotionSearchCriteria.setRuleSetUid(null);
		promotionSearchCriteria.setEnabled(null);
		promotionSearchCriteria.setActive(null);
		assertTrue("all properties cleared, should be empty", promotionSearchCriteria.isEmpty());
	}
	
	/**
	 * Test method for 'com.elasticpath.domain.search.impl.PromotionSearchCriteriaImpl.clear()'.
	 */
	@Test
	public void testClear() {
		promotionSearchCriteria.setEnabled(null);
		promotionSearchCriteria.setActive(null);
		promotionSearchCriteria.setRuleSetUid(null);
		promotionSearchCriteria.setStoreCode(null);
		promotionSearchCriteria.setPromotionName(null);
		promotionSearchCriteria.setCatalogUid(null);
		assertTrue("nothing set, criteria should be clear", promotionSearchCriteria.isEmpty());

		promotionSearchCriteria.setEnabled(true);
		assertFalse("not empty (1 property set)", this.promotionSearchCriteria.isEmpty());

		promotionSearchCriteria.setActive(false);
		assertFalse("not empty (2 properties set)", promotionSearchCriteria.isEmpty());

		this.promotionSearchCriteria.clear();
		assertTrue("criteria should be cleared", promotionSearchCriteria.isEmpty());
	}
}
