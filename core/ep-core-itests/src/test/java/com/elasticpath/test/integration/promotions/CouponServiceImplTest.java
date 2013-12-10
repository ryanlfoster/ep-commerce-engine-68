package com.elasticpath.test.integration.promotions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.commons.pagination.SortingDirection;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageModelDtoSortingField;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.CouponTestPersister;
import com.elasticpath.test.persister.PromotionTestPersister;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Integration test for {@code CouponServiceImpl}.
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods" })
public class CouponServiceImplTest extends BasicSpringContextTest {

	private static final int TEN = 10;

	private static final int FOUR = 4;

	private static final int THREE = 3;

	@Autowired
	private CouponService couponService;
	
	private PromotionTestPersister promoPersister;

	private SimpleStoreScenario scenario;
	
	private CouponTestPersister couponTestPersister;
	
	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		promoPersister = getTac().getPersistersFactory().getPromotionTestPersister();
		couponTestPersister = getTac().getPersistersFactory().getCouponTestPersister();
		scenario = getTac().useScenario(SimpleStoreScenario.class);
	}
	
	/**
	 * Test basic CRUD ops for coupon dao.
	 */
	@DirtiesDatabase
	@Test	

	public void testCRUD() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("test promo 1", scenario.getStore().getCode(), "rule_code1");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 1, CouponUsageType.LIMIT_PER_ANY_USER);
		
		Coupon coupon = getBeanFactory().getBean(ContextIdNames.COUPON);
		coupon.setCouponCode("abc");
		coupon.setCouponConfig(config);
		
		Coupon addedCoupon = couponService.add(coupon);
		Coupon foundCoupon = couponService.findByCouponCode(addedCoupon.getCouponCode());
		assertEquals("The find method should return the coupon we added", coupon.getCouponCode(), foundCoupon.getCouponCode());
		
		coupon.setCouponCode("xyz");
		Coupon updatedCoupon = null;
		try {
			updatedCoupon = couponService.update(addedCoupon);
		} catch (EpServiceException e) {
			fail("Couldn't find coupon on update");
		}
		assertEquals("Updated object should have same couponCode as added object", addedCoupon.getCouponCode(), updatedCoupon.getCouponCode());
		assertEquals("Added object should have same couponCode as added object", addedCoupon.getCouponConfig(), updatedCoupon.getCouponConfig());
		
		couponService.delete(updatedCoupon);
	}
	
	/**
	 * Tests that updating the coupon config for a coupon.
	 */
	@DirtiesDatabase
	@Test(expected = DataAccessException.class)
	public void testUpdateCouponCode() {
		Rule rule1 = promoPersister.createAndPersistSimpleShoppingCartPromotion("test promo 1", scenario.getStore().getCode(), "rule_code1");
		Rule rule2 = promoPersister.createAndPersistSimpleShoppingCartPromotion("test promo 2", scenario.getStore().getCode(), "rule_code2");

		CouponConfig config1 = couponTestPersister.createAndPersistCouponConfig(rule1.getCode(), 1, CouponUsageType.LIMIT_PER_ANY_USER);
		CouponConfig config2 = couponTestPersister.createAndPersistCouponConfig(rule2.getCode(), 2, CouponUsageType.LIMIT_PER_ANY_USER);

		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config1, "coupon_code1");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config2, "coupon_code2");
		
		coupon2.setCouponCode(coupon1.getCouponCode());
		couponService.update(coupon2);
	}
	
	/**
	 * Test duplicate code.
	 */
	@DirtiesDatabase
	@Test(expected = DataAccessException.class)
	public void testDuplicateCodeOnAdd() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("test promo 1", scenario.getStore().getCode(), "rule_code1");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 1, CouponUsageType.LIMIT_PER_ANY_USER);
		
		Coupon coupon1 = getBeanFactory().getBean(ContextIdNames.COUPON);
		coupon1.setCouponCode("abc");
		coupon1.setCouponConfig(config);
		
		Coupon coupon2 = getBeanFactory().getBean(ContextIdNames.COUPON);
		coupon2.setCouponCode("abc");
		coupon2.setCouponConfig(config);
		
		couponService.add(coupon1);
		couponService.add(coupon2);
	}
	
	/**
	 * Tests deleting all usages by coupon code.
	 */
	@DirtiesDatabase
	@Test
	public void testDeleteAllUsagesByCouponCode() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "coupon_code1");
		couponTestPersister.createAndPersistCoupon(config, "coupon_code2");
		
		couponTestPersister.createAndPersistCouponUsage(coupon1, "email1");
		couponTestPersister.createAndPersistCouponUsage(coupon1, "email2");

		Collection<Coupon> foundRuleCode = couponService.findCouponsForRuleCode(config.getRuleCode());
		assertEquals("We should have two coupons.", 2, foundRuleCode.size());
		
		List<CouponUsage> foundUsages = couponTestPersister.findCouponUsageByCouponCode(coupon1.getCouponCode());
		assertEquals("We should have two coupon usages.", 2, foundUsages.size());
		
		couponService.deleteCouponsByCouponConfigGuid(config.getGuid());
		
		foundRuleCode = couponService.findCouponsForRuleCode(config.getRuleCode());
		assertEquals("We should have no coupons since all has been deleted.", 0, foundRuleCode.size());
		
		foundUsages = couponTestPersister.findCouponUsageByCouponCode(coupon1.getCouponCode());
		assertEquals("We should have no coupon usages since all has been deleted.", 0, foundUsages.size());
	}

	/**
	 * Test that we can identify coupon codes that already exist in the system.
	 */
	@DirtiesDatabase
	@Test
	public void testFindCouponCodesFromList() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		
		couponTestPersister.createAndPersistCoupon(config, "coupon1");
		couponTestPersister.createAndPersistCoupon(config, "coupon3");
		couponTestPersister.createAndPersistCoupon(config, "coupon4");
		couponTestPersister.createAndPersistCoupon(config, "coupon5");
	
		Collection<String> couponsToCheck = new HashSet<String>();
		for (int i = 1; i <= Integer.valueOf("5"); i++) {
			couponsToCheck.add("coupon" + i);
		}
		
		Collection<String> foundCoupons = couponService.findExistingCouponCodes(couponsToCheck);
		
		assertTrue("coupon 1 should be found", foundCoupons.contains("coupon1"));
		assertFalse("coupon 2 should not be found", foundCoupons.contains("coupon2"));
		assertTrue("coupon 3 should be found", foundCoupons.contains("coupon3"));
		assertTrue("coupon 4 should be found", foundCoupons.contains("coupon4"));
		assertTrue("coupon 5 should be found", foundCoupons.contains("coupon5"));
	}
	
	/**
	 * Test that we can find coupon codes with search criteria.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeWithCriteria() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		Coupon coupon3 = couponTestPersister.createAndPersistCoupon(config, "cde");
		Coupon coupon4 = couponTestPersister.createAndPersistCoupon(config, "bcd");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { new SearchCriterion("couponCode", "d") };
		Collection<Coupon> actualCoupons = couponService.findCouponsForCouponConfigId(config.getUidPk(), searchCriteria, 0, TEN, orderingFields);
			
		assertEquals("Three coupons have d.", THREE, actualCoupons.size());
		assertTrue("d at the beginning", actualCoupons.contains(coupon2));
		assertTrue("d in the middle", actualCoupons.contains(coupon3));
		assertTrue("d at the end", actualCoupons.contains(coupon4));
	}
	
	/**
	 * Test that we can find coupon codes with search criteria status in use.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeWithStatusCriteriaInUse() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		couponTestPersister.createAndPersistCoupon(config, "abc", true);
		couponTestPersister.createAndPersistCoupon(config, "def", true);
		Coupon coupon3 = couponTestPersister.createAndPersistCoupon(config, "cde", false);
		couponTestPersister.createAndPersistCoupon(config, "bcd", true);
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { new SearchCriterion("status", "in_use") };
		Collection<Coupon> actualCoupons = couponService.findCouponsForCouponConfigId(config.getUidPk(), searchCriteria, 0, TEN, orderingFields);
			
		assertEquals("One coupon should match.", 1, actualCoupons.size());	
		assertTrue("d in the middle", actualCoupons.contains(coupon3));
	}
	
	/**
	 * Test that we can find coupon codes with search criteria status suspended.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeWithStatusCriteriaSuspended() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		couponTestPersister.createAndPersistCoupon(config, "abc", false);
		couponTestPersister.createAndPersistCoupon(config, "def", false);
		Coupon coupon3 = couponTestPersister.createAndPersistCoupon(config, "cde", true);
		couponTestPersister.createAndPersistCoupon(config, "bcd", false);
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { new SearchCriterion("status", "suspended") };
		Collection<Coupon> actualCoupons = couponService.findCouponsForCouponConfigId(config.getUidPk(), searchCriteria, 0, TEN, orderingFields);
			
		assertEquals("One coupon should match.", 1, actualCoupons.size());	
		assertTrue("d in the middle", actualCoupons.contains(coupon3));
	}
	
	/**
	 * Test that we can find coupon codes with search criteria for coupon and status.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeWithCouponAndStatusCriteria() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		couponTestPersister.createAndPersistCoupon(config, "abc", false);
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def", false);
		couponTestPersister.createAndPersistCoupon(config, "cde", true);
		couponTestPersister.createAndPersistCoupon(config, "bcd", true);
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { new SearchCriterion("couponCode", "d"), new SearchCriterion("status", "in_use") };
		Collection<Coupon> actualCoupons = couponService.findCouponsForCouponConfigId(config.getUidPk(), searchCriteria, 0, TEN, orderingFields);
			
		assertEquals("One coupon should match.", 1, actualCoupons.size());
		assertTrue("d at the beginning", actualCoupons.contains(coupon2));
	}
	
	/**
	 * Test that we can count coupon codes with search criteria.
	 */
	@DirtiesDatabase
	@Test
	public void testGetCountWithCriteria() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		couponTestPersister.createAndPersistCoupon(config, "abc");
		couponTestPersister.createAndPersistCoupon(config, "def");
		couponTestPersister.createAndPersistCoupon(config, "cde");
		couponTestPersister.createAndPersistCoupon(config, "bcd");
		
		SearchCriterion[] searchCriteria = { new SearchCriterion("couponCode", "d") };
		long actualResult = couponService.getCountForSearchCriteria(config.getUidPk(), searchCriteria);
			
		assertEquals("Three coupons have d.", THREE, actualResult);
	}
	
	/**
	 * Test that we can find coupon codes with search criteria.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeWithNoCriteria() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		Coupon coupon3 = couponTestPersister.createAndPersistCoupon(config, "cde");
		Coupon coupon4 = couponTestPersister.createAndPersistCoupon(config, "bcd");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<Coupon> actualCoupons = couponService.findCouponsForCouponConfigId(config.getUidPk(), searchCriteria, 0, TEN, orderingFields);
			
		assertEquals("All coupon should be found.", FOUR, actualCoupons.size());
		assertTrue("no d", actualCoupons.contains(coupon1));
		assertTrue("d at the beginning", actualCoupons.contains(coupon2));
		assertTrue("d in the middle", actualCoupons.contains(coupon3));
		assertTrue("d at the end", actualCoupons.contains(coupon4));
	}
	
	/**
	 * Test that we can find coupon codes with pagination.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodePage1() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "abc");
		couponTestPersister.createAndPersistCoupon(config, "def");
		couponTestPersister.createAndPersistCoupon(config, "cde");
		Coupon coupon4 = couponTestPersister.createAndPersistCoupon(config, "bcd");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<Coupon> actualCoupons = couponService.findCouponsForCouponConfigId(config.getUidPk(), searchCriteria, 0, 2, orderingFields);
			
		assertEquals("Two coupons should be found.", 2, actualCoupons.size());
		Iterator<Coupon> couponIterator = actualCoupons.iterator();
		assertEquals("abc is first", coupon1, couponIterator.next());
		assertEquals("bcd is second", coupon4, couponIterator.next());
	}
	
	/**
	 * Test that we can find coupon codes with pagination.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodePage2() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		Coupon coupon3 = couponTestPersister.createAndPersistCoupon(config, "cde");
		couponTestPersister.createAndPersistCoupon(config, "bcd");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<Coupon> actualCoupons = couponService.findCouponsForCouponConfigId(config.getUidPk(), searchCriteria, 2, 2, orderingFields);
			
		assertEquals("Two coupons should be found.", 2, actualCoupons.size());
		Iterator<Coupon> couponIterator = actualCoupons.iterator();
		assertEquals("cde is third", coupon3, couponIterator.next());
		assertEquals("def is fourth", coupon2, couponIterator.next());
	}
	
	/**
	 * Test that we can find coupon codes with pagination.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodePage3() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		couponTestPersister.createAndPersistCoupon(config, "abc");
		couponTestPersister.createAndPersistCoupon(config, "def");
		couponTestPersister.createAndPersistCoupon(config, "cde");
		couponTestPersister.createAndPersistCoupon(config, "bcd");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<Coupon> actualCoupons = couponService.findCouponsForCouponConfigId(config.getUidPk(), searchCriteria, FOUR, 2, orderingFields);
			
		assertEquals("No coupons should be found.", 0, actualCoupons.size());
	}
	
	/**
	 * Test that we can find coupon codes with pagination when only half of the page will be full.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeHalfPage() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		couponTestPersister.createAndPersistCoupon(config, "cde");
		couponTestPersister.createAndPersistCoupon(config, "bcd");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<Coupon> actualCoupons = couponService.findCouponsForCouponConfigId(config.getUidPk(), searchCriteria, THREE, 2, orderingFields);
		
		Iterator<Coupon> couponIterator = actualCoupons.iterator();
		assertEquals("One coupon should be found.", 1, actualCoupons.size());
		assertEquals("def is fourth", coupon2, couponIterator.next());
	}
	
	/**
	 * Test that we can find coupon codes with reverse order sorting.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodePage1ReverseOrder() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		Coupon coupon3 = couponTestPersister.createAndPersistCoupon(config, "cde");
		couponTestPersister.createAndPersistCoupon(config, "bcd");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(
				CouponUsageModelDtoSortingField.COUPON_CODE,
				SortingDirection.DESCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<Coupon> actualCoupons = couponService.findCouponsForCouponConfigId(config.getUidPk(), searchCriteria, 0, 2, orderingFields);
			
		assertEquals("Two coupons should be found.", 2, actualCoupons.size());
		Iterator<Coupon> couponIterator = actualCoupons.iterator();
		assertEquals("def is first", coupon2, couponIterator.next());
		assertEquals("cde is second", coupon3, couponIterator.next());
	}
	
	/**
	 * Test that we can find coupon codes and sort them by status..
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeSortByStatus() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "def", true);
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "abc", false);
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.STATUS, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<Coupon> actualCoupons = couponService.findCouponsForCouponConfigId(config.getUidPk(), searchCriteria, 0, 2, orderingFields);
			
		assertEquals("Two coupons should be found.", 2, actualCoupons.size());
		Iterator<Coupon> couponIterator = actualCoupons.iterator();
		assertEquals("abc is first", coupon2, couponIterator.next());
		assertEquals("def is second", coupon1, couponIterator.next());
	}
	
	/**
	 * Test that we can find coupon codes and sort them by status..
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeSortByStatusDescending() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "def", true);
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "abc", false);
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(
				CouponUsageModelDtoSortingField.STATUS,
				SortingDirection.DESCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<Coupon> actualCoupons = couponService.findCouponsForCouponConfigId(config.getUidPk(), searchCriteria, 0, 2, orderingFields);
			
		assertEquals("Two coupons should be found.", 2, actualCoupons.size());
		Iterator<Coupon> couponIterator = actualCoupons.iterator();
		assertEquals("def is first", coupon1, couponIterator.next());
		assertEquals("abc is second", coupon2, couponIterator.next());
	}
	
	/**
	 * Test that we can find coupon codes by rule id given a list of coupon codes to verify against.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByRuleId() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "def", true);
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "abc", false);
		
		Set <String> couponCodes = new HashSet <String>();
		couponCodes.add("def");
		couponCodes.add("abc");
		Collection<Coupon> actualCoupons = couponService.findCouponsForRuleFromCouponCodes(rule.getUidPk(), couponCodes);
		
		assertEquals("Two coupons should be found.", 2, actualCoupons.size());
		assertTrue("def should be found", actualCoupons.contains(coupon1));
		assertTrue("abc should be found", actualCoupons.contains(coupon2));
	}
	
	/**
	 * Test that we can find coupon codes by rule id given a list of coupon codes to verify against.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByRuleId2() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "def", true);
		
		Set <String> couponCodes = new HashSet <String>();
		couponCodes.add("def");
		couponCodes.add("abc");
		Collection<Coupon> actualCoupons = couponService.findCouponsForRuleFromCouponCodes(rule.getUidPk(), couponCodes);
		
		assertEquals("One coupon should be found.", 1, actualCoupons.size());
		Iterator<Coupon> couponIterator = actualCoupons.iterator();
		assertEquals("def is first", coupon1, couponIterator.next());
	}
}
