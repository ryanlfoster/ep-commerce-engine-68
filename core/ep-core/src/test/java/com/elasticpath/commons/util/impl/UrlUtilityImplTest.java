/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for UrlUtilityImpl.
 */
public class UrlUtilityImplTest {

	private UrlUtilityImpl utility;

	@Before
	public void setUp() throws Exception {
		utility = new UrlUtilityImpl();
		utility.setCharacterEncoding("UTF-8");
	}

	/**
	 * Tests that encoding works properly.
	 */
	@Test
	public void testEncodeText2UrlFriendly() {
		final String name1 = "abcdefg";
		assertEquals(name1, this.utility.encodeText2UrlFriendly(name1, Locale.US));

		final String name2 = " a b ";
		final String result = "a-b";
		assertEquals(result, this.utility.encodeText2UrlFriendly(name2, Locale.US));
	}

	/**
	 * Tests that encoding of GUIDs works properly.
	 */
	@Test
	public void testEncodeGuid2UrlFriendly() {
		final String testName = "Bücher";
		final String encodedString = this.utility.encodeGuid2UrlFriendly(testName);
		assertEquals(testName, this.utility.decodeUrl2Text(encodedString));
	}

	/**
	 * Tests that decoding works properly.
	 */
	@Test
	public void testDecodeUrl2Text() {
		final String name1 = "abcdefg";
		assertEquals(name1, this.utility.decodeUrl2Text(name1));

		final String testName = "Bücher";
		final String encodedString = this.utility.encodeText2UrlFriendly(testName, Locale.GERMAN);
		assertEquals(testName.toLowerCase(Locale.GERMAN), this.utility.decodeUrl2Text(encodedString));
	}

	// /**
	// * Test method for 'com.elasticpath.commons.util.impl.UtilityImpl.date2String(String)'.
	// */
	// public void testDate2String() {
	//
	// final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.CANADA);
	// sdf.setTimeZone(TimeZone.getTimeZone("PDT"));
	//
	// final String dateStr = "Wed May 03 12:21:48 PDT 2006";
	// // Testing the specified date format
	// Date date = this.utility.string2Date(dateStr, sdf);
	//
	// assertEquals(sdf.format(date), this.utility.date2String(date, sdf));
	//
	// // Testing the system default date format
	// date = this.utility.string2Date(dateStr);
	// assertEquals(sdf.format(date), this.utility.date2String(date));
	//
	// // Testing a bad date string
	// try {
	// this.utility.string2Date("A bad date string");
	// fail("Expecting an EpDateBindException.");
	// } catch (final EpDateBindException e) {
	// // succeed
	// assertNotNull(e);
	// }
	// }


}
