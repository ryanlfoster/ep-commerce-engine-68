package com.elasticpath.domain.catalog.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.domain.catalog.DigitalAsset;

/**
 * Test class <code>DigitalAssetImpl</code>.
 */
public class DigitalAssetImplTest {


	private DigitalAssetImpl digitalAsset;

	@Before
	public void setUp() throws Exception {
		this.digitalAsset = new DigitalAssetImpl();
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.DigitalAssetImpl.setFileName(String)'.
	 */
	@Test
	public void testSetFileName() {
		assertNull(this.digitalAsset.getFileName());
		final String fileName = "aaabbb";
		this.digitalAsset.setFileName(fileName);
		assertEquals(fileName, this.digitalAsset.getFileName());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.DigitalAssetImpl.setExpiryDays(int)'.
	 */
	@Test
	public void testSetExpiryDays() {
		assertEquals(0, this.digitalAsset.getExpiryDays());
		this.digitalAsset.setExpiryDays(-1);
		assertEquals(-1, this.digitalAsset.getExpiryDays());
		this.digitalAsset.setExpiryDays(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, this.digitalAsset.getExpiryDays());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.DigitalAssetImpl.setMaxDownloadTimes(int)'.
	 */
	@Test
	public void testSetMaxDownloadTimes() {
		assertEquals(0, this.digitalAsset.getMaxDownloadTimes());
		this.digitalAsset.setMaxDownloadTimes(-1);
		assertEquals(-1, this.digitalAsset.getMaxDownloadTimes());
		this.digitalAsset.setMaxDownloadTimes(Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, this.digitalAsset.getMaxDownloadTimes());
	}

	/**
	 * Test method for 'com.elasticpath.domain.catalog.impl.DigitalAssetImpl.equals(String)'.
	 */
	@Test
	public void testEqualsAndHashCode() {
		final DigitalAsset anotherDigitalAsset = new DigitalAssetImpl();
		assertEquals(digitalAsset, anotherDigitalAsset);
		assertEquals(anotherDigitalAsset, digitalAsset);
		assertEquals(digitalAsset.hashCode(), anotherDigitalAsset.hashCode());

		final String fileName = "aaabbb";
		this.digitalAsset.setFileName(fileName);
		assertFalse(digitalAsset.equals(anotherDigitalAsset));
		assertFalse(anotherDigitalAsset.equals(digitalAsset));

		anotherDigitalAsset.setFileName(fileName);
		assertEquals(digitalAsset, anotherDigitalAsset);
		assertEquals(anotherDigitalAsset, digitalAsset);
		assertEquals(digitalAsset.hashCode(), anotherDigitalAsset.hashCode());
	}

}
