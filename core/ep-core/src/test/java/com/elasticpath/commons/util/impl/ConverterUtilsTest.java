package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Test;

import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.commons.exception.EpBigDecimalBindException;
import com.elasticpath.commons.exception.EpBooleanBindException;
import com.elasticpath.commons.exception.EpIntBindException;

/**
 * Tests the ConverterUtils methods.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
public class ConverterUtilsTest {
	/**
	 * Test method for {@link com.elasticpath.commons.util.impl.ConverterUtils#string2Int(String)}.
	 */
	@Test
	public void testString2Int() {
		int intValue = ConverterUtils.string2Int(String.valueOf(Integer.MAX_VALUE));
		assertEquals(String.valueOf(Integer.MAX_VALUE), String.valueOf(intValue));

		// Testing a bad int string
		try {
			ConverterUtils.string2Int("A bad int string");
			fail("Expecting an EpIntBindException.");
		} catch (final EpIntBindException e) {
			// succeed
			assertNotNull(e);
		}
	}

	/**
	 * Test method for {@link com.elasticpath.commons.util.impl.ConverterUtils#string2BigDecimal(String)}.
	 */
	@Test
	public void testString2BigDecimal() {
		final String bigDecimalString = "234.45";
		BigDecimal bigDecimalValue = ConverterUtils.string2BigDecimal(bigDecimalString);
		assertEquals(bigDecimalString, ConverterUtils.bigDecimal2String(bigDecimalValue));

		// Testing a bad bigDecimal string
		try {
			ConverterUtils.string2BigDecimal("A bad bigDecimal string");
			fail("Expecting an EpBigDecimalBindException.");
		} catch (final EpBigDecimalBindException e) {
			// succeed
			assertNotNull(e);
		}

		// Testing null
		assertEquals(GlobalConstants.NULL_VALUE, ConverterUtils.bigDecimal2String(null));
	}
	
	/**
	 * Test method for {@link com.elasticpath.commons.util.impl.ConverterUtils#string2Boolean(String)}.
	 */
	@Test
	public void testString2Boolean() {
		assertTrue(ConverterUtils.string2Boolean("true"));
		assertTrue(ConverterUtils.string2Boolean("TRue"));
		assertFalse(ConverterUtils.string2Boolean("false"));
		assertFalse(ConverterUtils.string2Boolean("FalSe"));

		// Testing a bad boolean string
		try {
			ConverterUtils.string2Boolean("A bad boolean string");
			fail("Expecting an EpBooleanBindException.");
		} catch (final EpBooleanBindException e) {
			// succeed
			assertNotNull(e);
		}
	}
}
