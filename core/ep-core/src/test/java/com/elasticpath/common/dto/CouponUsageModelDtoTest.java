package com.elasticpath.common.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for {@code CouponUsageModel}.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class CouponUsageModelDtoTest {

	/**
	 * Tests that 2 CouponUsages which differ only by the case in the coupon code
	 * are still equal. 
	 */
	@Test
	public void testEqualsIgnoresCaseInCouponCode() {
		CouponUsageModelDto couponUsage1 = new CouponUsageModelDto(0, "abc", "test@test.com", false); //$NON-NLS-1$ //$NON-NLS-2$
		CouponUsageModelDto couponUsage2 = new CouponUsageModelDto(0, "Abc", "test@test.com", false);  //$NON-NLS-1$//$NON-NLS-2$
		
		boolean actualResult = couponUsage1.equals(couponUsage2);
		
		assertTrue("The only difference is coupon code case", actualResult); //$NON-NLS-1$
	}
	
	/**
	 * Tests that 2 CouponUsages which differ only by the case in the email address
	 * are still equal. 
	 */
	@Test
	public void testEqualsIgnoresCaseInEmailAddress() {
		CouponUsageModelDto couponUsage1 = new CouponUsageModelDto(0, "abc", "test@test.com", false); //$NON-NLS-1$ //$NON-NLS-2$
		CouponUsageModelDto couponUsage2 = new CouponUsageModelDto(0, "abc", "Test@test.com", false);  //$NON-NLS-1$//$NON-NLS-2$
		
		boolean actualResult = couponUsage1.equals(couponUsage2);
		
		assertTrue("The only difference is email address case", actualResult); //$NON-NLS-1$
	}
	
	/**
	 * Tests that 2 CouponUsages which have different coupon codes are not equal.
	 */
	@Test
	public void testEqualsCouponCodeDifference() {
		CouponUsageModelDto couponUsage1 = new CouponUsageModelDto(0, "abc", "test@test.com", false); //$NON-NLS-1$ //$NON-NLS-2$
		CouponUsageModelDto couponUsage2 = new CouponUsageModelDto(0, "xyz", "test@test.com", false);  //$NON-NLS-1$//$NON-NLS-2$
		
		boolean actualResult = couponUsage1.equals(couponUsage2);
		
		assertFalse("The coupon code is different", actualResult); //$NON-NLS-1$
	}
	
	/**
	 * Tests that 2 CouponUsages which have different email addresses are not equal.
	 */
	@Test
	public void testEqualsEmailAddressDifference() {
		CouponUsageModelDto couponUsage1 = new CouponUsageModelDto(0, "abc", "test@test.com", false); //$NON-NLS-1$ //$NON-NLS-2$
		CouponUsageModelDto couponUsage2 = new CouponUsageModelDto(0, "abc", "diff@test.com", false);  //$NON-NLS-1$//$NON-NLS-2$
		
		boolean actualResult = couponUsage1.equals(couponUsage2);
		
		assertFalse("The email address is different", actualResult); //$NON-NLS-1$
	}
	
	/**
	 * Tests that 2 CouponUsages which differ only by the case in the coupon code
	 * have the same hash code. 
	 */
	@Test
	public void testHashcodeIgnoresCaseInCouponCode() {
		CouponUsageModelDto couponUsage1 = new CouponUsageModelDto(0, "abc", "test@test.com", false); //$NON-NLS-1$ //$NON-NLS-2$
		CouponUsageModelDto couponUsage2 = new CouponUsageModelDto(0, "Abc", "test@test.com", false);  //$NON-NLS-1$//$NON-NLS-2$
		
		int hashcode1 = couponUsage1.hashCode();
		int hashcode2 = couponUsage2.hashCode();
		
		assertEquals("The objects differ only in coupon code case", hashcode1, hashcode2); //$NON-NLS-1$
	}
	
	/**
	 * Tests that 2 CouponUsages which differ only by the case in the email address
	 * have the same hash code. 
	 */
	@Test
	public void testHashcodeIgnoresCaseInEmailAddress() {
		CouponUsageModelDto couponUsage1 = new CouponUsageModelDto(0, "abc", "test@test.com", false); //$NON-NLS-1$ //$NON-NLS-2$
		CouponUsageModelDto couponUsage2 = new CouponUsageModelDto(0, "abc", "Test@test.com", false);  //$NON-NLS-1$//$NON-NLS-2$
		
		int hashcode1 = couponUsage1.hashCode();
		int hashcode2 = couponUsage2.hashCode();
		
		assertEquals("The objects differ only in email address case", hashcode1, hashcode2); //$NON-NLS-1$
	}
	
	/**
	 * Tests that 2 CouponUsages have different coupon codes
	 * have different hash codes. 
	 */
	@Test
	public void testHashcodeDifferentCouponCode() {
		CouponUsageModelDto couponUsage1 = new CouponUsageModelDto(0, "abc", "test@test.com", false); //$NON-NLS-1$ //$NON-NLS-2$
		CouponUsageModelDto couponUsage2 = new CouponUsageModelDto(0, "XYZ", "test@test.com", false);  //$NON-NLS-1$//$NON-NLS-2$
		
		int hashcode1 = couponUsage1.hashCode();
		int hashcode2 = couponUsage2.hashCode();
		
		assertNotSame("The objects have different coupon codes", hashcode1, hashcode2); //$NON-NLS-1$
	}
	
	/**
	 * Tests that 2 CouponUsages have different email addresses
	 * have different hash codes. 
	 */
	@Test
	public void testHashcodeDifferentEmailAddresses() {
		CouponUsageModelDto couponUsage1 = new CouponUsageModelDto(0, "abc", "test@test.com", false); //$NON-NLS-1$ //$NON-NLS-2$
		CouponUsageModelDto couponUsage2 = new CouponUsageModelDto(0, "aBC", "diff@test.com", false);  //$NON-NLS-1$//$NON-NLS-2$
		
		int hashcode1 = couponUsage1.hashCode();
		int hashcode2 = couponUsage2.hashCode();
		
		assertNotSame("The objects have different email addresses", hashcode1, hashcode2); //$NON-NLS-1$
	}
}
