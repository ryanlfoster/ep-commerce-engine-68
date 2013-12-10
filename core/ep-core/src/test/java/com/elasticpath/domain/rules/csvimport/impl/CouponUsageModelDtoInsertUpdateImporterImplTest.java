package com.elasticpath.domain.rules.csvimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.dto.CouponUsageModelDto;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.csvimport.ImportValidRow;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.impl.ImportBadRowImpl;
import com.elasticpath.domain.dataimport.impl.ImportFaultImpl;
import com.elasticpath.domain.rules.Coupon;
import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.domain.rules.impl.CouponConfigImpl;
import com.elasticpath.domain.rules.impl.CouponImpl;
import com.elasticpath.domain.rules.impl.CouponUsageImpl;
import com.elasticpath.service.rules.CouponService;
import com.elasticpath.service.rules.CouponUsageService;

/**
 * 
 * The junit test class for CouponUsageModelDtoInsertUpdateImporterImpl.
 *
 */
public class CouponUsageModelDtoInsertUpdateImporterImplTest {
	private static final String SHOULD_GET_ONE_BAD_ROW = "should get one bad row";
	private static final String SHOULD_NOT_GET_BAD_ROW = "should not get bad row";

	private final CouponUsageModelDtoInsertUpdateImporterImpl importer = new CouponUsageModelDtoInsertUpdateImporterImpl();
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private final CouponService couponService = context.mock(CouponService.class);
	private final CouponUsageService couponUsageService = context.mock(CouponUsageService.class);
	private final BeanFactory beanFactory = context.mock(BeanFactory.class);

	private static final String COUPON_ONE = "coupon1";
	private static final String EMAIL_ONE = "test@ep.com";
	private static final String ROW_STRING = "row1";
	private static final String ROW2_STRING = "row2";

	@SuppressWarnings("unchecked")
	private final ImportValidRow<CouponUsageModelDto> row = context.mock(ImportValidRow.class, ROW_STRING);

	@SuppressWarnings("unchecked")
	private final ImportValidRow<CouponUsageModelDto> row2 = context.mock(ImportValidRow.class, ROW2_STRING);

	private final CouponUsageModelDto dto1 = new CouponUsageModelDto(0, COUPON_ONE, null, false);
	private final CouponUsageModelDto dto2 = new CouponUsageModelDto(1, "", null, false);
	private final CouponUsageModelDto dto3 = new CouponUsageModelDto(2, COUPON_ONE, "invalid email", false);
	private final CouponUsageModelDto dto4 = new CouponUsageModelDto(3, COUPON_ONE, EMAIL_ONE, false);
	private final Coupon coupon1 = new CouponImpl();

	private CouponConfig couponConfig;
	private List<ImportValidRow<CouponUsageModelDto>> validRows;

	/**
	 * The setup method.
	 */
	@Before
	public void setUp() {
		importer.setBeanFactory(beanFactory);
		importer.setCouponService(couponService);
		importer.setCouponUsageService(couponUsageService);
		
		coupon1.setCouponCode(COUPON_ONE);
		couponConfig = new CouponConfigImpl();
		couponConfig.setGuid("couponConfigGuid1");
		coupon1.setCouponConfig(couponConfig);
		
		validRows = new ArrayList<ImportValidRow<CouponUsageModelDto>>();
	}
	
	/**
	 * Test import dtos for coupon, this is the happy path.
	 */
	@Test
	public void testImportDtosForCoupon() {
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_COUPON);
		
		validRows.add(row);
		final Set<String> codes = new HashSet<String>();
		codes.add(COUPON_ONE);
		
		context.checking(new Expectations() { {
			allowing(row).getDto(); will(returnValue(dto1));

			oneOf(couponService).findCouponsForRuleCodeFromCouponCodes(null, codes);
			will(returnValue(Collections.emptySet()));
			
			oneOf(couponService).findExistingCouponCodes(codes);
			will(returnValue(Collections.emptySet()));
			
			oneOf(beanFactory).getBean("coupon");
			will(returnValue(coupon1));
			
			oneOf(couponService).add(coupon1);
			will(returnValue(coupon1));
		} });

		List<ImportBadRow> badRows = importer.importDtos(validRows, couponConfig);
		
		assertTrue(badRows.isEmpty());
		
	}
	
	/**
	 * Test import dtos with blank coupon.
	 */
	@Test
	public void testImprtDtosForBlankCoupon() {
		validRows.add(row);
		
		context.checking(new Expectations() { {
			oneOf(couponService).findCouponsForRuleCodeFromCouponCodes(null, Collections.<String>emptySet());
			will(returnValue(Collections.emptySet()));
			
			oneOf(couponService).findExistingCouponCodes(Collections.<String>emptySet());
			will(returnValue(Collections.emptySet()));
			
			allowing(row).getDto(); will(returnValue(dto2));
			oneOf(row).getRow(); will(returnValue(ROW_STRING));
			oneOf(row).getRowNumber(); will(returnValue(1));
			
			oneOf(beanFactory).getBean(ContextIdNames.IMPORT_FAULT);
			will(returnValue(new ImportFaultImpl()));
			
			oneOf(beanFactory).getBean(ContextIdNames.IMPORT_BAD_ROW);
			will(returnValue(new ImportBadRowImpl()));
		} });
		
		List<ImportBadRow> badRows = importer.importDtos(validRows, couponConfig);
		
		assertEquals(SHOULD_GET_ONE_BAD_ROW, 1, badRows.size());
		
	}
	
	/**
	 *  Test import dtos for the coupon already existing in db.
	 */
	@Test
	public void testImprtDtosForCouponExistInDb() {
		validRows.add(row);
		final Set<String> codes = new HashSet<String>();
		codes.add(COUPON_ONE);
		final Set<Coupon> coupons = new HashSet<Coupon>();
		coupons.add(new CouponImpl());
		
		context.checking(new Expectations() { {
			oneOf(couponService).findCouponsForRuleCodeFromCouponCodes(null, codes);
			will(returnValue(coupons));
			
			oneOf(couponService).findExistingCouponCodes(codes);
			will(returnValue(codes));
			
			allowing(row).getDto(); will(returnValue(dto1));
			oneOf(row).getRow(); will(returnValue(ROW_STRING));
			oneOf(row).getRowNumber(); will(returnValue(1));
			
			oneOf(beanFactory).getBean(ContextIdNames.IMPORT_FAULT);
			will(returnValue(new ImportFaultImpl()));
			
			oneOf(beanFactory).getBean(ContextIdNames.IMPORT_BAD_ROW);
			will(returnValue(new ImportBadRowImpl()));
		} });
		
		List<ImportBadRow> badRows = importer.importDtos(validRows, couponConfig);
		
		assertEquals(SHOULD_GET_ONE_BAD_ROW, 1, badRows.size());
		
	}
	
	/**
	 * Test import dtos for duplicated coupons in same csv file.
	 */
	@Test
	public void testImprtDtosForDuplicatedCouponInCsv() {
		validRows.add(row);
		validRows.add(row2);
		
		final Set<String> codes = new HashSet<String>();
		codes.add(COUPON_ONE);
		
		context.checking(new Expectations() { {
			
			oneOf(couponService).findCouponsForRuleCodeFromCouponCodes(null, codes);
			will(returnValue(Collections.emptySet()));
			
			oneOf(couponService).findExistingCouponCodes(codes);
			will(returnValue(Collections.emptySet()));
			
			allowing(row).getDto(); will(returnValue(dto1));
			allowing(row2).getDto(); will(returnValue(dto1));
			
			oneOf(row2).getRow(); will(returnValue(ROW2_STRING));
			oneOf(row2).getRowNumber(); will(returnValue(2));
			
			oneOf(beanFactory).getBean(ContextIdNames.IMPORT_FAULT);
			will(returnValue(new ImportFaultImpl()));
			
			oneOf(beanFactory).getBean(ContextIdNames.IMPORT_BAD_ROW);
			will(returnValue(new ImportBadRowImpl()));
			
			oneOf(beanFactory).getBean(ContextIdNames.COUPON);
			will(returnValue(coupon1));
			
			oneOf(couponService).add(coupon1);
			will(returnValue(coupon1));
		} });
		
		List<ImportBadRow> badRows = importer.importDtos(validRows, couponConfig);
		
		assertEquals(SHOULD_GET_ONE_BAD_ROW, 1, badRows.size());
		
	}
	
	/**
	 * Test import dtos for coupon and invalid email.
	 */
	@Test
	public void testImprtDtosForCouponAndInvalidEmail() {
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		validRows.add(row);
		
		context.checking(new Expectations() { {
			oneOf(couponService).findCouponsForRuleCodeFromCouponCodes(null, Collections.<String>emptySet());
			will(returnValue(Collections.emptySet()));
			
			oneOf(couponService).findExistingCouponCodes(Collections.<String>emptySet());
			will(returnValue(Collections.emptySet()));
			
			allowing(row).getDto(); will(returnValue(dto3));
			
			oneOf(row).getRow(); will(returnValue(ROW_STRING));
			oneOf(row).getRowNumber(); will(returnValue(1));
			
			oneOf(beanFactory).getBean(ContextIdNames.IMPORT_BAD_ROW);
			will(returnValue(new ImportBadRowImpl()));
			
			oneOf(beanFactory).getBean(ContextIdNames.IMPORT_FAULT);
			will(returnValue(new ImportFaultImpl()));
			

		} });
		
		List<ImportBadRow> badRows = importer.importDtos(validRows, couponConfig);
		
		assertEquals(SHOULD_GET_ONE_BAD_ROW, 1, badRows.size());
		
	}
	
	/**
	 * Test import dtos for coupon and email, this is the happy path.
	 */
	@Test
	public void testImprtDtosForCouponAndEmailSuccess() {
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		validRows.add(row);
		
		final Set<String> codes = new HashSet<String>();
		codes.add(COUPON_ONE);
		
		final CouponUsage couponUsage = new CouponUsageImpl();
		couponUsage.setCoupon(coupon1);
		couponUsage.setCustomerEmailAddress(EMAIL_ONE);
		couponUsage.setSuspended(false);
		
		context.checking(new Expectations() { {
			oneOf(couponService).findCouponsForRuleCodeFromCouponCodes(null, codes);
			will(returnValue(Collections.emptySet()));
			
			oneOf(couponService).findExistingCouponCodes(codes);
			will(returnValue(Collections.emptySet()));
			
			oneOf(couponUsageService).findByCouponCodeAndEmail(COUPON_ONE, EMAIL_ONE);
			will(returnValue(null));
			
			allowing(row).getDto(); will(returnValue(dto4));
			
			allowing(beanFactory).getBean(ContextIdNames.COUPON);
			will(returnValue(coupon1));
			
			oneOf(couponService).add(coupon1);
			will(returnValue(coupon1));
			
			oneOf(beanFactory).getBean(ContextIdNames.COUPON_USAGE);
			will(returnValue(couponUsage));
			
			oneOf(couponUsageService).add(couponUsage);
			will(returnValue(couponUsage));
		} });
		
		List<ImportBadRow> badRows = importer.importDtos(validRows, couponConfig);
		
		assertEquals(SHOULD_NOT_GET_BAD_ROW, 0, badRows.size());
		
	}
	
	/**
	 * Test import dtos for coupon and email for existing coupon config.
	 */
	@Test
	public void testImprtDtosForCouponAndEmailForExistCouponConfig() {
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		validRows.add(row);
		
		final CouponConfig existingCouponConfig = new CouponConfigImpl();
		existingCouponConfig.setGuid("different guid");
		
		final Set<String> codes = new HashSet<String>();
		codes.add(COUPON_ONE);
		final Set<Coupon> coupons = new HashSet<Coupon>();
		coupons.add(coupon1);
		
		final CouponUsage couponUsage = new CouponUsageImpl();
		couponUsage.setCoupon(coupon1);
		couponUsage.setCustomerEmailAddress(EMAIL_ONE);
		couponUsage.setSuspended(false);
		
		context.checking(new Expectations() { {
			oneOf(couponService).findCouponsForRuleCodeFromCouponCodes(null, codes);
			will(returnValue(coupons));
			
			oneOf(couponService).findExistingCouponCodes(codes);
			will(returnValue(codes));
			
			oneOf(couponUsageService).findByCouponCodeAndEmail(COUPON_ONE, EMAIL_ONE);
			will(returnValue(couponUsage));
			
			allowing(row).getDto(); will(returnValue(dto4));
			
			oneOf(beanFactory).getBean(ContextIdNames.IMPORT_FAULT);
			will(returnValue(new ImportFaultImpl()));
			
			oneOf(beanFactory).getBean(ContextIdNames.IMPORT_BAD_ROW);
			will(returnValue(new ImportBadRowImpl()));
						
			oneOf(row).getRow(); will(returnValue(ROW_STRING));
			oneOf(row).getRowNumber(); will(returnValue(1));
		} });
		
		List<ImportBadRow> badRows = importer.importDtos(validRows, couponConfig);
		
		assertEquals(SHOULD_GET_ONE_BAD_ROW, 1, badRows.size());
		
	}
	
	/**
	 * Test import dtos for duplicated coupon and email pair in csv. 
	 */
	@Test
	public void testImprtDtosForCouponAndEmailDuplicatedInCSV() {
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		validRows.add(row);
		validRows.add(row2);
		
		final CouponUsage couponUsage = new CouponUsageImpl();
		couponUsage.setCoupon(coupon1);
		couponUsage.setCustomerEmailAddress(EMAIL_ONE);
		couponUsage.setSuspended(false);
		
		final Set<String> codes = new HashSet<String>();
		codes.add(COUPON_ONE);
		
		context.checking(new Expectations() { {
		
			oneOf(couponService).findCouponsForRuleCodeFromCouponCodes(null, codes);
			will(returnValue(Collections.emptySet()));
				
			oneOf(couponService).findExistingCouponCodes(codes);
			will(returnValue(Collections.emptySet()));
			
			allowing(row).getDto(); will(returnValue(dto4));
			allowing(row2).getDto(); will(returnValue(dto4));
			
			oneOf(couponUsageService).findByCouponCodeAndEmail(COUPON_ONE, EMAIL_ONE);
			will(returnValue(null));
			
			allowing(beanFactory).getBean(ContextIdNames.COUPON);
			will(returnValue(coupon1));
			
			oneOf(couponService).add(coupon1);
			will(returnValue(coupon1));
			
			oneOf(beanFactory).getBean(ContextIdNames.COUPON_USAGE);
			will(returnValue(couponUsage));
			
			oneOf(couponUsageService).add(couponUsage);
			will(returnValue(couponUsage));
			
			oneOf(beanFactory).getBean(ContextIdNames.IMPORT_FAULT);
			will(returnValue(new ImportFaultImpl()));
			
			oneOf(beanFactory).getBean(ContextIdNames.IMPORT_BAD_ROW);
			will(returnValue(new ImportBadRowImpl()));
						
			oneOf(row2).getRow(); will(returnValue(ROW2_STRING));
			oneOf(row2).getRowNumber(); will(returnValue(2));
		} });
		
		List<ImportBadRow> badRows = importer.importDtos(validRows, couponConfig);
		
		assertEquals(SHOULD_GET_ONE_BAD_ROW, 1, badRows.size());
		
	}
	
	/**
	 * Test import dtos for the coupon and email pair existing in db.
	 */
	@Test
	public void testImprtDtosForCouponAndEmailExistInDB() {
		couponConfig.setUsageType(CouponUsageType.LIMIT_PER_SPECIFIED_USER);
		validRows.add(row);
		
		final Set<String> codes = new HashSet<String>();
		codes.add(COUPON_ONE);
		final Set<Coupon> coupons = new HashSet<Coupon>();
		coupons.add(coupon1);
		
		final CouponUsage couponUsage = new CouponUsageImpl();
		couponUsage.setCoupon(coupon1);
		couponUsage.setCustomerEmailAddress(EMAIL_ONE);
		couponUsage.setSuspended(false);
		
		context.checking(new Expectations() { {
			oneOf(couponService).findCouponsForRuleCodeFromCouponCodes(null, codes);
			will(returnValue(coupons));
				
			oneOf(couponService).findExistingCouponCodes(codes);
			will(returnValue(codes));
			
			allowing(row).getDto(); will(returnValue(dto4));
			
			oneOf(couponUsageService).findByCouponCodeAndEmail(COUPON_ONE, EMAIL_ONE);
			will(returnValue(couponUsage));
			
			oneOf(beanFactory).getBean(ContextIdNames.IMPORT_FAULT);
			will(returnValue(new ImportFaultImpl()));
			
			oneOf(beanFactory).getBean(ContextIdNames.IMPORT_BAD_ROW);
			will(returnValue(new ImportBadRowImpl()));
						
			oneOf(row).getRow(); will(returnValue(ROW_STRING));
			oneOf(row).getRowNumber(); will(returnValue(1));
		} });
		
		List<ImportBadRow> badRows = importer.importDtos(validRows, couponConfig);
		
		assertEquals(SHOULD_GET_ONE_BAD_ROW, 1, badRows.size());
		
	}

}
