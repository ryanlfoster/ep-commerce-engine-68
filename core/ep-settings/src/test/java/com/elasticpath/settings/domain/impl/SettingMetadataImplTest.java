package com.elasticpath.settings.domain.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the metadata class.
 *
 */
public class SettingMetadataImplTest {
	private final SettingMetadataImpl metadata1 = new SettingMetadataImpl();
	private final SettingMetadataImpl metadata2 = new SettingMetadataImpl();
	private final SettingMetadataImpl metadata1more = new SettingMetadataImpl();


	/**
	 * Set up the metadata obj.
	 */
	@Before
	public void setUp() {
		metadata1.setUidPk(1);
		metadata1.setKey("key1");
		metadata1.setValue("value1");
		metadata1.setUidPk(2);
		metadata2.setKey("key2");
		metadata2.setValue("value2");
		metadata1more.setKey("key1");
		metadata1more.setValue("value1");
	}
	
	/**
	 * Test that equals works on only the key and value.
	 */
	@Test
	public void testEquals() {
		//Equals symetric
		assertEquals(metadata1, metadata1);
		//Equals reflexive
		assertEquals(metadata1, metadata1more);
		assertEquals(metadata1more, metadata1);
		
		assertFalse(metadata1.equals(metadata2));
	}
	
	/**
	 * Test that hashcode works with the equals and operates only on key and value.
	 */
	@Test
	public void testHashCode() {
		assertEquals(metadata1.hashCode(), metadata1.hashCode());
		assertEquals(metadata1.hashCode(), metadata1more.hashCode());
	}
}
