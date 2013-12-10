package com.elasticpath.domain.misc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.shipping.Region;
import com.elasticpath.domain.shipping.impl.RegionImpl;

/**
 * Test <code>RegionCodeComparatorImpl</code>.
 */
public class RegionCodeComparatorImplTest {

	private static final String COUNTRY_CODE_CA = "CA";

	private static final String SUB_COUNTRY_CODE_AB = "AB";

	private static final String SUB_COUNTRY_CODE_BC = "BC";

	private static final String COUNTRY_CODE_US = "US";

	private RegionCodeComparatorImpl regionCountryCodeComparatorImpl;

	@Before
	public void setUp() throws Exception {
		this.regionCountryCodeComparatorImpl = new RegionCodeComparatorImpl();
	}

	/**
	 * Test method for 'compare(Object, Object)'.
	 */
	@Test
	public void testCompare() {

		Region region1 = new RegionImpl(COUNTRY_CODE_CA,
				 Arrays.asList(SUB_COUNTRY_CODE_AB, SUB_COUNTRY_CODE_BC));
		Region region2 = new RegionImpl(COUNTRY_CODE_US);
		Region region3 = new RegionImpl(COUNTRY_CODE_CA);
		assertTrue(this.regionCountryCodeComparatorImpl.compare(region1, region2) < 0);
		assertTrue(this.regionCountryCodeComparatorImpl.compare(region2, region1) > 0);
		assertEquals(0, this.regionCountryCodeComparatorImpl.compare(region1, region3));
	}

	/**
	 * Test method for 'compare(Object, Object)'.
	 */
	@Test
	public void testCompareError() {
		Region emptyRegion = new RegionImpl();
		emptyRegion.setCountryCode(null);
		Region validRegion = new RegionImpl("CA");

		try {
			this.regionCountryCodeComparatorImpl.compare(emptyRegion, validRegion);
			fail("EpSystemException expected.");
		} catch (EpSystemException e) {
			// succeed!
			assertNotNull(e);
		}

		try {
			this.regionCountryCodeComparatorImpl.compare(validRegion, emptyRegion);
			fail("EpSystemException expected.");
		} catch (EpSystemException e) {
			// succeed!
			assertNotNull(e);
		}

		// not needed with the generics based Comparator
//		try {
//			this.regionCountryCodeComparatorImpl.compare(new Object(), validRegion);
//			fail("ClassCastException expected.");
//		} catch (ClassCastException e) {
//			// succeed!
//			assertNotNull(e);
//		}
//
//		try {
//			this.regionCountryCodeComparatorImpl.compare(validRegion, new Object());
//			fail("ClassCastException expected.");
//		} catch (ClassCastException e) {
//			// succeed!
//			assertNotNull(e);
//		}

		try {
			this.regionCountryCodeComparatorImpl.compare(null, validRegion);
			fail("ClassCastException expected.");
		} catch (EpSystemException e) {
			// succeed!
			assertNotNull(e);
		}


	}
}

