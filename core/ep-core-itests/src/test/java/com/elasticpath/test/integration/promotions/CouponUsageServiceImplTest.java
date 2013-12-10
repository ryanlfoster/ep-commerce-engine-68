package com.elasticpath.test.integration.promotions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.pagination.DirectedSortingField;
import com.elasticpath.commons.pagination.SearchCriterion;
import com.elasticpath.commons.pagination.SortingDirection;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.rules.RuleCondition;
import com.elasticpath.domain.rules.impl.CouponConfigImpl;
import com.elasticpath.domain.rules.impl.CouponImpl;
import com.elasticpath.domain.rules.impl.CouponUsageImpl;
import com.elasticpath.domain.rules.impl.LimitedUseCouponCodeConditionImpl;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.rules.CouponConfigService;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageModelDtoSortingField;
import com.elasticpath.service.rules.CouponUsageService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.CouponTestPersister;
import com.elasticpath.test.persister.PromotionTestPersister;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Integration test for {@code CouponServiceImpl}.
 */
@SuppressWarnings({ "PMD.AvoidDuplicateLiterals", "PMD.TooManyMethods" })
public class CouponUsageServiceImplTest extends BasicSpringContextTest {

	private static final int TEN = 10;

	private static final int FOUR = 4;

	private static final int THREE = 3;

	@Autowired
	private CouponUsageService couponUsageService;
	
	private PromotionTestPersister promoPersister;
	
	private SimpleStoreScenario scenario;
	
	private CouponTestPersister couponTestPersister;
	
	@Autowired
	private RuleService ruleService;

	@Autowired
	private CouponService couponService;

	@Autowired
	private CouponConfigService couponConfigService;

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
	 * Test basic CRUD ops for coupon usage dao.
	 */
	@DirtiesDatabase
	@Test	
	public void testCRUD() {
		final boolean activeInCart = true;
		
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("test promo 1", scenario.getStore().getCode(), "xyz");
		
		RuleCondition condition = new LimitedUseCouponCodeConditionImpl();		
		rule.addCondition(condition);
		
		ruleService.update(rule);
		
		Coupon coupon = new CouponImpl();
		coupon.setCouponCode("abc");
		
		CouponConfig couponConfig = new CouponConfigImpl();
		couponConfig.setRuleCode("xyz");
		couponConfig.setGuid("xyzconfig");
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_COUPON);
		couponConfig = couponConfigService.add(couponConfig);
		
		coupon.setCouponConfig(couponConfig);
		
		Coupon addedCoupon = couponService.add(coupon);
		
		CouponUsage couponUsage = new CouponUsageImpl();
		couponUsage.setUseCount(1);
		couponUsage.setCoupon(addedCoupon);
		couponUsage.setCustomerEmailAddress("cust@test.com");
		couponUsage.setActiveInCart(activeInCart);
		
		CouponUsage addedCouponUsage = couponUsageService.add(couponUsage);
				
		CouponUsage updatedCouponUsage = null;
		try {
			updatedCouponUsage = couponUsageService.update(addedCouponUsage);
		} catch (EpServiceException e) {
			fail("Couldn't find couponUsage on update");
		}
		assertEquals("Updated object should have same use count as added object", addedCouponUsage.getUseCount(), updatedCouponUsage.getUseCount());
		assertEquals("Updated object should have same coupon as added object",
				addedCouponUsage.getCoupon().getCouponCode(),
				updatedCouponUsage.getCoupon().getCouponCode());
		assertEquals("Updated object should have same email address as added object",
				addedCouponUsage.getCustomerEmailAddress(),
				updatedCouponUsage.getCustomerEmailAddress());
		
		couponUsageService.delete(updatedCouponUsage);
	}
	
	/**
	 * Test basic CRUD ops for coupon usage dao when no customer email address.
	 */
	@DirtiesDatabase
	@Test	
	public void testCRUDNoCustomerEmailAddress() {
		final boolean activeInCart = true;
		
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("test promo 1", scenario.getStore().getCode(), "xyz");
		
		RuleCondition condition = new LimitedUseCouponCodeConditionImpl();		
		rule.addCondition(condition);
		
		ruleService.update(rule);
		
		Coupon coupon = new CouponImpl();
		coupon.setCouponCode("abc");
		
		CouponConfig couponConfig = new CouponConfigImpl();
		couponConfig.setRuleCode("xyz");
		couponConfig.setGuid("xyzconfig");
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_COUPON);
		couponConfig = couponConfigService.add(couponConfig);
		
		coupon.setCouponConfig(couponConfig);
		
		Coupon addedCoupon = couponService.add(coupon);
		
		CouponUsage couponUsage = getBeanFactory().getBean(ContextIdNames.COUPON_USAGE);
		couponUsage.setUseCount(1);
		couponUsage.setCoupon(addedCoupon);
		couponUsage.setActiveInCart(activeInCart);
		
		CouponUsage addedCouponUsage = couponUsageService.add(couponUsage);
				
		CouponUsage updatedCouponUsage = null;
		try {
			updatedCouponUsage = couponUsageService.update(addedCouponUsage);
		} catch (EpServiceException e) {
			fail("Couldn't find couponUsage on update");
		}
		assertEquals("Updated object should have same use count as added object", addedCouponUsage.getUseCount(), updatedCouponUsage.getUseCount());
		assertEquals("Updated object should have same coupon as added object",
				addedCouponUsage.getCoupon().getCouponCode(),
				updatedCouponUsage.getCoupon().getCouponCode());
		assertEquals("Updated object should have same email address as added object",
				addedCouponUsage.getCustomerEmailAddress(),
				updatedCouponUsage.getCustomerEmailAddress());
		
		couponUsageService.delete(updatedCouponUsage);
	}
	
	/**
	 * Tests that the find by rule code method finds the rule.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByRuleCode() {
		final boolean activeInCart = false;
		
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("test promo 1", scenario.getStore().getCode(), "xyz");
		
		RuleCondition condition = new LimitedUseCouponCodeConditionImpl();		
		rule.addCondition(condition);
		
		ruleService.update(rule);
		
		Coupon coupon = new CouponImpl();
		coupon.setCouponCode("abc");
		
		final int usageLimit = 10;
		CouponConfig couponConfig = new CouponConfigImpl();
		couponConfig.setRuleCode("xyz");
		couponConfig.setGuid("xyzconfig");
		couponConfig.setUsageLimit(usageLimit);
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_COUPON);
		couponConfig = couponConfigService.add(couponConfig);
		
		coupon.setCouponConfig(couponConfig);
		
		Coupon addedCoupon = couponService.add(coupon);
		
		CouponUsage couponUsage = getBeanFactory().getBean(ContextIdNames.COUPON_USAGE);
		couponUsage.setCoupon(addedCoupon);
		couponUsage.setActiveInCart(activeInCart);
		
		couponUsageService.add(couponUsage);
		
		Collection<CouponUsage> foundCouponUsage = couponUsageService.findByRuleCode("xyz");
		
		assertEquals("The couponUsage found should be the couponUsage we added", couponUsage, foundCouponUsage.iterator().next());
	}
	
	/**
	 * Tests that the findByCouponCodeAndEmail method finds the couponUsage.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeAndEmail() {
		final boolean activeInCart = true;
		
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("test promo 1", scenario.getStore().getCode(), "xyz");
		
		RuleCondition condition = new LimitedUseCouponCodeConditionImpl();		
		rule.addCondition(condition);
		
		ruleService.update(rule);
		
		Coupon coupon = new CouponImpl();
		coupon.setCouponCode("abc");
		
		CouponConfig couponConfig = new CouponConfigImpl();
		couponConfig.setRuleCode("xyz");
		couponConfig.setGuid("xyzconfig");
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_COUPON);
		couponConfig = couponConfigService.add(couponConfig);
		
		coupon.setCouponConfig(couponConfig);
		
		Coupon addedCoupon = couponService.add(coupon);
		
		CouponUsage couponUsage = new CouponUsageImpl();
		couponUsage.setCoupon(addedCoupon);
		couponUsage.setCustomerEmailAddress("test@test.com");
		couponUsage.setActiveInCart(activeInCart);
		
		couponUsageService.add(couponUsage);
		
		CouponUsage foundCouponUsage = couponUsageService.findByCouponCodeAndEmail("abc", "test@test.com");
		
		assertEquals("The couponUsage found should be the couponUsage we added", couponUsage, foundCouponUsage);
	}
	
	/**
	 * Tests finding eligible usages by email address which do not go over limits.
	 */
	@DirtiesDatabase
	@Test
	public void testFindEligibleUsagesByEmailAddressNotOverLimit() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 1, CouponUsageType.LIMIT_PER_ANY_USER);
		Coupon coupon = couponTestPersister.createAndPersistCoupon(config, "coupon_code");
		CouponUsage usage = couponTestPersister.createAndPersistCouponUsage(coupon, "owen.ou@elasticpath.com", 0);

		Collection<CouponUsage> results = 
			couponUsageService.findEligibleUsagesByEmailAddress(usage.getCustomerEmailAddress(), scenario.getStore().getUidPk());

		assertEquals(1, results.size());
	}
	
	/**
	 * Tests finding eligible usages by email address which go over limits.
	 */
	@DirtiesDatabase
	@Test
	public void testFindEligibleUsagesByEmailAddressOverLimit() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 1, CouponUsageType.LIMIT_PER_ANY_USER);
		Coupon coupon = couponTestPersister.createAndPersistCoupon(config, "coupon_code");
		CouponUsage usage = couponTestPersister.createAndPersistCouponUsage(coupon, "owen.ou@elasticpath.com", 1);

		Collection<CouponUsage> results =
			couponUsageService.findEligibleUsagesByEmailAddress(usage.getCustomerEmailAddress(), scenario.getStore().getUidPk());

		assertEquals(0, results.size());
	}
	
	
	/**
	 * Tests finding eligible usages by email address which are set as Active-in-Cart.
	 * Note: for the above tests default is Active in Cart so they will not be affected. This does the same thing but explicitly.
	 */
	@DirtiesDatabase
	@Test
	public void testFindEligibleUsagesWhereTheActiveInCartFlagIsSet() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 1, CouponUsageType.LIMIT_PER_ANY_USER);
		Coupon coupon = couponTestPersister.createAndPersistCoupon(config, "coupon_code");
		CouponUsage usage = couponTestPersister.createAndPersistCouponUsage(coupon, "owen.ou@elasticpath.com", 0, true, false);

		Collection<CouponUsage> results = 
			couponUsageService.findEligibleUsagesByEmailAddress(usage.getCustomerEmailAddress(), scenario.getStore().getUidPk());
		assertEquals(1, results.size());
	}
	
	/**
	 * Tests finding eligible usages by email address which are set as In-Active-in-Cart.
	 * Active in cart shouldn't affect the eligibility of a coupon.
	 */
	@DirtiesDatabase
	@Test
	public void testFindEligibleUsagesWhereAllAreFlaggedInactiveInCart() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 1, CouponUsageType.LIMIT_PER_ANY_USER);
		Coupon coupon = couponTestPersister.createAndPersistCoupon(config, "coupon_code");
		CouponUsage usage = couponTestPersister.createAndPersistCouponUsage(coupon, "owen.ou@elasticpath.com", 0, false, false);

		Collection<CouponUsage> results = 
			couponUsageService.findEligibleUsagesByEmailAddress(usage.getCustomerEmailAddress(), scenario.getStore().getUidPk());
		assertEquals(1, results.size());
	}
	
	/**
	 * Tests finding eligible usages by rule code and email address.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByRuleCodeAndEmail() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		Coupon coupon = couponTestPersister.createAndPersistCoupon(config, "coupon_code");
		final String email1 = "test@test.com";
		final String email2 = "test2@test.com";
		couponTestPersister.createAndPersistCouponUsage(coupon, email1);
		couponTestPersister.createAndPersistCouponUsage(coupon, email2);
		Collection<CouponUsage> usages = couponUsageService.findByRuleCodeAndEmail(rule.getCode(), email1);
		assertEquals("We should have a single usage record", 1, usages.size());
		CouponUsage usage = usages.iterator().next();
		assertEquals("The usage should be for the first email address", email1, usage.getCustomerEmailAddress());
	}
	
	/**
	 * Test that we can find coupon codes with search criteria.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeWithCouponCriteria() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test1@test.com");
		CouponUsage couponUsage3 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test2@test.com");
		CouponUsage couponUsage4 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test3@test.com");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { new SearchCriterion("couponCode", "d") };
		Collection<CouponUsage> actualCouponUsages = couponUsageService
				.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0,
						TEN, orderingFields);
			
		assertEquals("Two couponusages have d.", 2, actualCouponUsages.size());
		assertTrue("test2@test.com", actualCouponUsages.contains(couponUsage3));
		assertTrue("test3@test.com", actualCouponUsages.contains(couponUsage4));
	}
	
	/**
	 * Test that we can find coupon codes with search criteria of email address.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeWithEmailCriteria() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com");
		CouponUsage couponUsage2 = couponTestPersister.createAndPersistCouponUsage(coupon1, "test1@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon2, "test2@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon2, "test3@test.com");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { new SearchCriterion("emailAddress", "test1") };
		Collection<CouponUsage> actualCouponUsages = couponUsageService
				.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0,
						TEN, orderingFields);
			
		assertEquals("One couponusage has test1.", 1, actualCouponUsages.size());
		assertTrue("test1@test.com", actualCouponUsages.contains(couponUsage2));
	}
	
	/**
	 * Test that we can find coupon codes with search criteria of status in use.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeWithStatusInUseCriteria() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");		
		
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com", true);
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test1@test.com", true);
		CouponUsage couponUsage3 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test2@test.com", false);
		CouponUsage couponUsage4 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test3@test.com", false);
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { new SearchCriterion("status", "in_use") };
		Collection<CouponUsage> actualCouponUsages = couponUsageService
				.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0,
						TEN, orderingFields);
			
		assertEquals("Two couponusages are in use.", 2, actualCouponUsages.size());
		assertTrue("test2@test.com", actualCouponUsages.contains(couponUsage3));
		assertTrue("test3@test.com", actualCouponUsages.contains(couponUsage4));
	}
	
	/**
	 * Test that we can find coupon codes with search criteria of status suspended.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeWithStatusSuspendedCriteria() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		
		CouponUsage couponUsage1 = couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com", true);
		CouponUsage couponUsage2 = couponTestPersister.createAndPersistCouponUsage(coupon1, "test1@test.com", true);
		couponTestPersister.createAndPersistCouponUsage(coupon2, "test2@test.com", false);
		couponTestPersister.createAndPersistCouponUsage(coupon2, "test3@test.com", false);
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { new SearchCriterion("status", "suspended") };
		Collection<CouponUsage> actualCouponUsages = couponUsageService
				.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0,
						TEN, orderingFields);
			
		assertEquals("Two couponusages are in use.", 2, actualCouponUsages.size());
		assertTrue("test@test.com", actualCouponUsages.contains(couponUsage1));
		assertTrue("test2@test.com", actualCouponUsages.contains(couponUsage2));
	}
	
	/**
	 * Test that we can find coupon codes with search criteria of status in use.
	 * Status is set on the coupon, not the coupon usage.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeWithStatusInUseCriteriaOnCoupon() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "abc", true);
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def", false);		
		
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com", false);
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test1@test.com", false);
		CouponUsage couponUsage3 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test2@test.com", false);
		CouponUsage couponUsage4 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test3@test.com", false);
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { new SearchCriterion("status", "in_use") };
		Collection<CouponUsage> actualCouponUsages = couponUsageService
				.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0,
						TEN, orderingFields);
			
		assertEquals("Two couponusages are in use.", 2, actualCouponUsages.size());
		assertTrue("test2@test.com", actualCouponUsages.contains(couponUsage3));
		assertTrue("test3@test.com", actualCouponUsages.contains(couponUsage4));
	}
	
	/**
	 * Test that we can find coupon codes with search criteria of status suspended.
	 * Status is set on the coupon, not the coupon usage.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeWithStatusSuspendedCriteriaOnCoupon() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "abc", true);
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def", false);		
		
		CouponUsage couponUsage1 = couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com", false);
		CouponUsage couponUsage2 = couponTestPersister.createAndPersistCouponUsage(coupon1, "test1@test.com", false);
		couponTestPersister.createAndPersistCouponUsage(coupon2, "test2@test.com", false);
		couponTestPersister.createAndPersistCouponUsage(coupon2, "test3@test.com", false);
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { new SearchCriterion("status", "suspended") };
		Collection<CouponUsage> actualCouponUsages = couponUsageService
				.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0,
						TEN, orderingFields);
			
		assertEquals("Two couponusages are in use.", 2, actualCouponUsages.size());
		assertTrue("test@test.com", actualCouponUsages.contains(couponUsage1));
		assertTrue("test1@test.com", actualCouponUsages.contains(couponUsage2));
	}
	
	/**
	 * Test that we can find coupon codes with search criteria of email address.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeWithCouponAndEmailCriteria() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com");
		CouponUsage couponUsage2 = couponTestPersister.createAndPersistCouponUsage(coupon1, "test1@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon2, "test1@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon2, "test3@test.com");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { new SearchCriterion("couponCode", "abc"), new SearchCriterion("emailAddress", "test1") };		
		Collection<CouponUsage> actualCouponUsages = couponUsageService
				.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0,
						TEN, orderingFields);
			
		assertEquals("One couponusage has test1 for email and abc for coupon.", 1, actualCouponUsages.size());
		assertTrue("test1@test.com", actualCouponUsages.contains(couponUsage2));
	}
	
	/**
	 * Test that we can find coupon codes with search criteria of coupon, email address and status.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeWithCouponEmailAndStatusCriteria() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com", true);
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test1@test.com", false);
		couponTestPersister.createAndPersistCouponUsage(coupon2, "test1@test.com", false);
		couponTestPersister.createAndPersistCouponUsage(coupon2, "test3@test.com", false);
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = {
				new SearchCriterion("couponCode", "abc"),
				new SearchCriterion("emailAddress", "test1"),
				new SearchCriterion("status", "suspended") };		
		Collection<CouponUsage> actualCouponUsages = couponUsageService
				.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0,
						TEN, orderingFields);
			
		assertEquals("No coupon usages should be found.", 0, actualCouponUsages.size());
	}
	
	/**
	 * Test that we can count coupon codes with search criteria.
	 */
	@DirtiesDatabase
	@Test
	public void testGetCountWithCriteria() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test1@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon2, "test2@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon2, "test3@test.com");
		
		SearchCriterion[] searchCriteria = { new SearchCriterion("couponCode", "d") };
		long actualResult = couponUsageService.getCountForSearchCriteria(config.getUidPk(), searchCriteria);
			
		assertEquals("Two couponUsages have d.", 2, actualResult);
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
		
		CouponUsage couponUsage1 = couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com");
		CouponUsage couponUsage2 = couponTestPersister.createAndPersistCouponUsage(coupon1, "test1@test.com");
		CouponUsage couponUsage3 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test2@test.com");
		CouponUsage couponUsage4 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test3@test.com");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<CouponUsage> actualCoupons = 
			couponUsageService.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0, TEN, orderingFields);
			
		assertEquals("All couponUsages should be found.", FOUR, actualCoupons.size());
		assertTrue("test1", actualCoupons.contains(couponUsage1));
		assertTrue("test2", actualCoupons.contains(couponUsage2));
		assertTrue("test3", actualCoupons.contains(couponUsage3));
		assertTrue("test4", actualCoupons.contains(couponUsage4));
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
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		Coupon coupon3 = couponTestPersister.createAndPersistCoupon(config, "cde");
		Coupon coupon4 = couponTestPersister.createAndPersistCoupon(config, "bcd");
		
		CouponUsage couponUsage1 = couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon2, "test1@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon3, "test2@test.com");
		CouponUsage couponUsage4 = couponTestPersister.createAndPersistCouponUsage(coupon4, "test3@test.com");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<CouponUsage> actualCoupons = 
			couponUsageService.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0, 2, orderingFields);
			
		assertEquals("Two couponUsages should be found.", 2, actualCoupons.size());
		Iterator<CouponUsage> couponIterator = actualCoupons.iterator();
		assertEquals("abc is first", couponUsage1, couponIterator.next());
		assertEquals("bcd is second", couponUsage4, couponIterator.next());
	}
	
	/**
	 * Test that we can find coupon codes with pagination.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodePage2() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		Coupon coupon3 = couponTestPersister.createAndPersistCoupon(config, "cde");
		Coupon coupon4 = couponTestPersister.createAndPersistCoupon(config, "bcd");
		
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com");
		CouponUsage couponUsage2 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test1@test.com");
		CouponUsage couponUsage3 = couponTestPersister.createAndPersistCouponUsage(coupon3, "test2@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon4, "test3@test.com");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<CouponUsage> actualCoupons = 
			couponUsageService.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 2, 2, orderingFields);
						
		assertEquals("Two coupons should be found.", 2, actualCoupons.size());
		Iterator<CouponUsage> couponIterator = actualCoupons.iterator();
		assertEquals("cde is third", couponUsage3, couponIterator.next());
		assertEquals("def is fourth", couponUsage2, couponIterator.next());
	}
	
	/**
	 * Test that we can find coupon codes with pagination.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodePage3() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		Coupon coupon3 = couponTestPersister.createAndPersistCoupon(config, "cde");
		Coupon coupon4 = couponTestPersister.createAndPersistCoupon(config, "bcd");
		
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon2, "test1@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon3, "test2@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon4, "test3@test.com");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<CouponUsage> actualCoupons = 
			couponUsageService.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, FOUR, 2, orderingFields);
			
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
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		Coupon coupon3 = couponTestPersister.createAndPersistCoupon(config, "cde");
		Coupon coupon4 = couponTestPersister.createAndPersistCoupon(config, "bcd");
		
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com");
		CouponUsage couponUsage2 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test1@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon3, "test2@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon4, "test3@test.com");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.COUPON_CODE, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<CouponUsage> actualCoupons = 
			couponUsageService.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, THREE, 2, orderingFields);
		
		Iterator<CouponUsage> couponIterator = actualCoupons.iterator();
		assertEquals("One coupon should be found.", 1, actualCoupons.size());
		assertEquals("def is fourth", couponUsage2, couponIterator.next());
	}
	
	/**
	 * Test that we can find coupon codes with reverse order sorting.
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodePage1ReverseOrder() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "abc");
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "def");
		Coupon coupon3 = couponTestPersister.createAndPersistCoupon(config, "cde");
		Coupon coupon4 = couponTestPersister.createAndPersistCoupon(config, "bcd");
		
		couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com");
		CouponUsage couponUsage2 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test1@test.com");
		CouponUsage couponUsage3 = couponTestPersister.createAndPersistCouponUsage(coupon3, "test2@test.com");
		couponTestPersister.createAndPersistCouponUsage(coupon4, "test3@test.com");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(
				CouponUsageModelDtoSortingField.COUPON_CODE,
				SortingDirection.DESCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<CouponUsage> actualCoupons = 
			couponUsageService.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0, 2, orderingFields);
			
		assertEquals("Two couponUsages should be found.", 2, actualCoupons.size());
		Iterator<CouponUsage> couponIterator = actualCoupons.iterator();
		assertEquals("def is first", couponUsage2, couponIterator.next());
		assertEquals("cde is second", couponUsage3, couponIterator.next());
	}
	
	/**
	 * Test that we can find coupon codes and sort them by status..
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeSortByStatus() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "def", false);
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "abc", false);
		
		CouponUsage couponUsage1 = couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com", true);
		CouponUsage couponUsage2 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test1@test.com", false);
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.STATUS, SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<CouponUsage> actualCoupons = 
			couponUsageService.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0, 2, orderingFields);
			
		assertEquals("Two coupons should be found.", 2, actualCoupons.size());
		Iterator<CouponUsage> couponIterator = actualCoupons.iterator();
		assertEquals("abc is first", couponUsage2, couponIterator.next());
		assertEquals("def is second", couponUsage1, couponIterator.next());
	}
	
	/**
	 * Test that we can find coupon codes and sort them by status. where the status is on both
	 * coupon and coupon usage.
	 */
	@Ignore // Combined sorting not currently working
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeSortByStatusCouponAndCouponUsage() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "def", false);
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "abc", false);
		
		CouponUsage couponUsage1 = couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com", true);
		CouponUsage couponUsage2 = couponTestPersister.createAndPersistCouponUsage(coupon1, "test1@test.com", false);
		CouponUsage couponUsage3 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test1@test.com", false);
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(CouponUsageModelDtoSortingField.STATUS, SortingDirection.DESCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<CouponUsage> actualCoupons 
			= couponUsageService.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0, TEN, orderingFields);
			
		assertEquals("Three coupon usages should be found.", THREE, actualCoupons.size());
		Iterator<CouponUsage> couponIterator = actualCoupons.iterator();
		assertEquals("abc is first", couponUsage1, couponIterator.next());
		assertEquals("def, test1 is second", couponUsage2, couponIterator.next());
		assertEquals("def, test is third", couponUsage3, couponIterator.next());
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
		
		CouponUsage couponUsage1 = couponTestPersister.createAndPersistCouponUsage(coupon1, "test@test.com");
		CouponUsage couponUsage2 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test1@test.com");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(
				CouponUsageModelDtoSortingField.STATUS,
				SortingDirection.DESCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<CouponUsage> actualCoupons = 
			couponUsageService.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0, 2, orderingFields);
			
		assertEquals("Two coupons should be found.", 2, actualCoupons.size());
		Iterator<CouponUsage> couponIterator = actualCoupons.iterator();
		assertEquals("def is first", couponUsage1, couponIterator.next());
		assertEquals("abc is second", couponUsage2, couponIterator.next());
	}
	
	/**
	 * Test that we can find coupon codes and sort them by status..
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeSortByEmail() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "def", true);
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "abc", false);
		
		CouponUsage couponUsage1 = couponTestPersister.createAndPersistCouponUsage(coupon1, "test2@test.com");
		CouponUsage couponUsage2 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test1@test.com");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(
				CouponUsageModelDtoSortingField.EMAIL_ADDRESS,
				SortingDirection.ASCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<CouponUsage> actualCoupons = 
			couponUsageService.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0, 2, orderingFields);
			
		assertEquals("Two coupons should be found.", 2, actualCoupons.size());
		Iterator<CouponUsage> couponIterator = actualCoupons.iterator();
		CouponUsage element1 = couponIterator.next();
		assertEquals("test1 is first", couponUsage2, element1);
		assertEquals("test2 is second", couponUsage1, couponIterator.next());
	}
	
	/**
	 * Test that we can find coupon codes and sort them by status..
	 */
	@DirtiesDatabase
	@Test
	public void testFindByCouponCodeSortByEmailDescending() {
		Rule rule = promoPersister.createAndPersistSimpleShoppingCartPromotion("promotion", scenario.getStore().getCode(), "promotion");
		CouponConfig config = couponTestPersister.createAndPersistCouponConfig(rule.getCode(), 2, CouponUsageType.LIMIT_PER_COUPON);
		
		Coupon coupon1 = couponTestPersister.createAndPersistCoupon(config, "def", true);
		Coupon coupon2 = couponTestPersister.createAndPersistCoupon(config, "abc", false);
		
		CouponUsage couponUsage1 = couponTestPersister.createAndPersistCouponUsage(coupon1, "test2@test.com");
		CouponUsage couponUsage2 = couponTestPersister.createAndPersistCouponUsage(coupon2, "test1@test.com");
		
		DirectedSortingField[] orderingFields = { new DirectedSortingField(
				CouponUsageModelDtoSortingField.EMAIL_ADDRESS,
				SortingDirection.DESCENDING) };
		SearchCriterion[] searchCriteria = { };
		Collection<CouponUsage> actualCoupons = 
			couponUsageService.findCouponUsagesForCouponConfigId(config.getUidPk(), searchCriteria, 0, 2, orderingFields);
			
		assertEquals("Two coupons should be found.", 2, actualCoupons.size());
		Iterator<CouponUsage> couponIterator = actualCoupons.iterator();
		assertEquals("test2 is first", couponUsage1, couponIterator.next());
		assertEquals("test1 is second", couponUsage2, couponIterator.next());
	}
}
