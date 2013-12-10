/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.impl.ApplicationContextBackedBeanFactoryImpl;

/**
 * Test <code>UtilityImpl</code>.
 */
public class UtilityImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private static final char DUMMY_CHAR = 'a';

	private static final String EP_SYSTEM_EXCEPTION_EXPECTED = "Expect an EpSystemException.";

	private UtilityImpl utility;


	/**
	 * Prepare for tests.
	 *
	 * @throws Exception in case of failure.
	 */
	@Before
	public void setUp() throws Exception {
		utility = new UtilityImpl();
		final ApplicationContextBackedBeanFactoryImpl beanFactory = new ApplicationContextBackedBeanFactoryImpl();
		utility.setBeanFactory(beanFactory);
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.UtilityImpl.getLocalizedFile()'.
	 */
	@Test
	public void testGetLocalizedFile() {
		final String webInfPath = JUnitUtilityImpl.getInstance().getTestClassesPath();
		final Locale locale = new Locale("en", "CA");
		String result = utility.getLocalizedFile(webInfPath, "conf/misc/UtilityImplTest.test1.vm", ".vm", locale);
		assertEquals("conf/misc/UtilityImplTest.test1_en_CA.vm", result);

		result = utility.getLocalizedFile(webInfPath, "conf/misc/UtilityImplTest.test2.vm", ".vm", locale);
		assertEquals("conf/misc/UtilityImplTest.test2_en.vm", result);

		result = utility.getLocalizedFile(webInfPath, "conf/misc/UtilityImplTest.test3.vm", ".vm", locale);
		assertEquals("conf/misc/UtilityImplTest.test3.vm", result);
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.UtilityImpl.getLocalizedFile()'.
	 */
	@Test
	public void testGetLocalizedFileWithNull() {
		final Locale locale = new Locale("en", "CA");
		final String baseDir = "";
		final String filePath = "dir/111.txt";
		final String extension = ".txt";
		final String differentExtension = ".xml";

		try {
			utility.getLocalizedFile(null, filePath, extension, locale);
			fail(EP_SYSTEM_EXCEPTION_EXPECTED);
		} catch (final EpSystemException e) {
			// Succeed.
			assertNotNull(e);
		}

		try {
			utility.getLocalizedFile(baseDir, null, extension, locale);
			fail(EP_SYSTEM_EXCEPTION_EXPECTED);
		} catch (final EpSystemException e) {
			// Succeed.
			assertNotNull(e);
		}

		try {
			utility.getLocalizedFile(baseDir, filePath, null, locale);
			fail(EP_SYSTEM_EXCEPTION_EXPECTED);
		} catch (final EpSystemException e) {
			// Succeed.
			assertNotNull(e);
		}

		try {
			utility.getLocalizedFile(baseDir, filePath, extension, null);
			fail(EP_SYSTEM_EXCEPTION_EXPECTED);
		} catch (final EpSystemException e) {
			// Succeed.
			assertNotNull(e);
		}

		try {
			utility.getLocalizedFile(baseDir, filePath, differentExtension, null);
			fail(EP_SYSTEM_EXCEPTION_EXPECTED);
		} catch (final EpSystemException e) {
			// Succeed.
			assertNotNull(e);
		}
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.UtilityImpl.escapeName2UrlFriendly(String)'.
	 */
	@Test
	public void testEscapeName2UrlFriendly() {
		final String name1 = "abcdefg";
		assertEquals(name1, this.utility.escapeName2UrlFriendly(name1));

		final String name2 = "  a$&b D cde,?$&23:=+     f&:g\t \r\n ";
		final String result = "a-b-d-cde-23-f-g";
		assertEquals(result, this.utility.escapeName2UrlFriendly(name2));
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.UtilityImpl.checkShortTextMaxLength(String)'.
	 */
	@Test
	public void testCheckShortTextMaxLength() {
		assertTrue(utility.checkShortTextMaxLength(null));
		assertTrue(utility.checkShortTextMaxLength(""));

		final StringBuffer sbf = new StringBuffer();
		for (int i = 1; i <= GlobalConstants.SHORT_TEXT_MAX_LENGTH; i++) {
			sbf.append(DUMMY_CHAR);
		}
		assertTrue(utility.checkShortTextMaxLength(sbf.toString()));

		sbf.append('a');
		assertFalse(utility.checkShortTextMaxLength(sbf.toString()));
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.UtilityImpl.checkLongTextMaxLength(String)'.
	 */
	@Test
	public void testCheckLongTextMaxLength() {
		assertTrue(utility.checkLongTextMaxLength(null));
		assertTrue(utility.checkLongTextMaxLength(""));

		final StringBuffer sbf = new StringBuffer();
		for (int i = 1; i <= GlobalConstants.LONG_TEXT_MAX_LENGTH; i++) {
			sbf.append(DUMMY_CHAR);
		}
		assertTrue(utility.checkLongTextMaxLength(sbf.toString()));

		sbf.append('a');
		assertFalse(utility.checkLongTextMaxLength(sbf.toString()));
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.UtilityImpl.isValidZipPostalCode(String)'.
	 */
	@Test
	public void testIsValidZipPostalCode() {
		assertTrue(utility.isValidZipPostalCode("aaaBBB112"));

		assertFalse(utility.isValidZipPostalCode(null));
		assertFalse(utility.isValidZipPostalCode(""));
		assertFalse(utility.isValidZipPostalCode("-aaaBBB112"));
		assertFalse(utility.isValidZipPostalCode("aaaBBB112-"));
		assertFalse(utility.isValidZipPostalCode("/aaaBBB112"));
		assertFalse(utility.isValidZipPostalCode("/aaaBBB112/"));
		assertFalse(utility.isValidZipPostalCode(" aaaBBB112"));
		assertFalse(utility.isValidZipPostalCode("aaaBBB112 "));
		assertTrue(utility.isValidZipPostalCode("aaa BBB112"));
	}

	/**
	 * Test method for 'com.elasticpath.commons.util.impl.UtilityImpl.isValidZipPostalCode(String)'.
	 */
	@Test
	public void testIsGuidStr() {
		assertTrue(utility.isValidGuidStr("aaaBBB112"));

		assertFalse(utility.isValidGuidStr(null));
		assertFalse(utility.isValidGuidStr(""));
		assertFalse(utility.isValidGuidStr("/aaaBBB112"));
		assertFalse(utility.isValidGuidStr("/aaaBBB112/"));
		assertFalse(utility.isValidGuidStr(" aaaBBB112"));
		assertFalse(utility.isValidGuidStr("aaaBBB112 "));
		assertTrue(utility.isValidGuidStr("aaa-BBB112"));
	}

}
