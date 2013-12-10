package com.elasticpath.commons.util.capabilities.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.elasticpath.commons.util.capabilities.Capabilities;
import com.elasticpath.commons.util.extenum.ExtensibleEnum;

/**
 * Tests the CapabilitiesImpl.
 */
public class CapabilitiesImplTest {
	
	private final ExtensibleEnum eeAA = new ExtensibleEnumAA();
	private final ExtensibleEnum eeBB = new ExtensibleEnumBB();
	private final ExtensibleEnum eeCC = new ExtensibleEnumCC();
	
	/**
	 * Test that no desired capabilities are in fact a subset of an empty Capabilities instance.
	 */
	@Test
	public void testEmptyAndEmpty() {
		Capabilities capabilities = new CapabilitiesImpl();
		assertTrue(capabilities.supports());
	}
	
	/**
	 * Test that no desired capabilities are in fact a subset of a non-empty Capabilities instance.
	 */
	@Test
	public void testEEAAAndEmpty() {
		Capabilities capabilities = new CapabilitiesImpl(eeAA);
		assertTrue(capabilities.supports());
	}
	
	/**
	 * Test that two Capabilities instances with the same ExtensibleEnum are equal and have the same hashCode.
	 */
	@Test
	public void testEqualsAndHash() {
		Capabilities capabilities1 = new CapabilitiesImpl(eeAA);
		Capabilities capabilities2 = new CapabilitiesImpl(eeAA);
		assertEquals(capabilities1, capabilities2);
		assertEquals(capabilities1.hashCode(), capabilities2.hashCode());
	}
	
	/**
	 * Test that a Capabilities instance supports an ExtensibleEnum.
	 */
	@Test
	public void testSupports() {
		Capabilities capabilities = new CapabilitiesImpl(eeAA);
		assertTrue(capabilities.supports(eeAA));
	}
	
	/**
	 * Test that a Capabilities instance does not support an ExtensibleEnum.
	 */
	@Test
	public void testNotSupports() {
		Capabilities capabilities = new CapabilitiesImpl(eeAA);
		assertFalse(capabilities.supports(eeBB));
	}
	
	/**
	 * Test that a Capabilities instance partially supports some ExtensibleEnums, which is actually false.
	 */
	@Test
	public void testPartialSupports() {
		Capabilities capabilities = new CapabilitiesImpl(eeAA, eeBB);
		assertFalse(capabilities.supports(eeBB, eeCC));
	}
	
	/**
	 * Test implementation.
	 */
	private static class ExtensibleEnumAA implements ExtensibleEnum {

		@Override
		public String getName() {
			return "AA";
		}

		@Override
		public int getOrdinal() {
			return 0;
		}
	}
	
	/**
	 * Test implementation.
	 */
	private static class ExtensibleEnumBB implements ExtensibleEnum {

		@Override
		public String getName() {
			return "BB";
		}

		@Override
		public int getOrdinal() {
			return 1;
		}
	}
	
	/**
	 * Test implementation.
	 */
	private static class ExtensibleEnumCC implements ExtensibleEnum {
		
		@Override
		public String getName() {
			return "CC";
		}

		@Override
		public int getOrdinal() {
			return 2;
		}
	}
}
