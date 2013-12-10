package com.elasticpath.validation.validators.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link NotBlankValidatorForString}.
 */
public class NotBlankValidatorForStringTest {

	private NotBlankValidatorForString validator;

	/** Test initialization. */
	@Before
	public void initialize() {
		validator = new NotBlankValidatorForString();
	}

	/** {@code null} values should be allowed. */
	@Test
	public void testNull() {
		assertTrue("Null values are allowed", validator.isValid(null, null));
	}

	/** The empty string is blank. */
	@Test
	public void testEmpty() {
		assertFalse("Empty string is blank", validator.isValid("", null));
	}

	/** Test whitespace and tabs. */
	@Test
	public void testBlank() {
		assertFalse("Strings with just whitespace shouldn't pass", validator.isValid("   ", null));
		assertFalse("Tabs shouldn't validate", validator.isValid("	", null));
		assertFalse("Whitespace with tabs shouldn't validate", validator.isValid("  	  ", null));
	}

	/** Blanks surrounding a non-blank character are allowed. */
	@Test
	public void test1Ascii() {
		assertTrue("There's at least one non-blank character", validator.isValid("  a  ", null));
	}

	/** Standard input, no surrounding blanks. */
	@Test
	public void testStandardNoBlanks() {
		assertTrue("No blanks here!", validator.isValid("hazmat", null));
	}
}
