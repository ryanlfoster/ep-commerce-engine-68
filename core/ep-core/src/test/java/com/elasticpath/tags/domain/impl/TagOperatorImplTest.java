package com.elasticpath.tags.domain.impl;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

import org.apache.commons.lang.StringUtils;

/**
 * Test for tag definition class.
 */
public class TagOperatorImplTest {

	private static final String TAG_OPERATOR_NAME = "TAG_OPERATOR_NAME";
	private static final String TAG_NAME_EN = "English Name";
	private static final String TAG_NAME_RU = "Russkii Variant";
	private static final String RU_COUNTRY_CODE = "ru";
	private static final String BLANK_STRING = "    ";
	private TagOperatorImpl tagOperator;
	
	/**
	 * Tests that if language is not provided the default value of guid
	 * from tag operator is returned.
	 */
	@Test
	public void testGetLocalizedNameWhenLanguageIsNotProvided() {
		
		tagOperator = new TagOperatorImpl();
		tagOperator.setGuid(TAG_OPERATOR_NAME);
		
		final String resultForNull = tagOperator.getName(null);
		assertEquals("Test result for NULL failed", TAG_OPERATOR_NAME, resultForNull);
	}
	
	/**
	 * Test that if provided language is not specified for the tag operator localized name the value of guid
	 * from tag operator is returned.
	 */
	@Test
	public void testGetLocalizedNameWithMissingLanguage() {
		
		tagOperator = new TagOperatorImpl() {
			private static final long serialVersionUID = 4083398741992440L;

			String getDisplayNameLocalizedPropertyFromLocalizedProperties(final Locale locale) {
				return null;
			}
		};
		tagOperator.setGuid(TAG_OPERATOR_NAME);
		
		final String resultForMissing = tagOperator.getName(Locale.UK);
		assertEquals("Test result for UK (missing) language failed", TAG_OPERATOR_NAME, resultForMissing);
		
		
		
	}
	
	/**
	 * Test that if provided language is specified for the tag operator but the localized value of name
	 * is blank then guid from tag operator is returned.
	 */
	@Test
	public void testGetLocalizedNameWithBlankLocalizedValue() {
		
		tagOperator = new TagOperatorImpl() {
			private static final long serialVersionUID = -3487905009166434521L;

			String getDisplayNameLocalizedPropertyFromLocalizedProperties(final Locale locale) {
				return StringUtils.EMPTY;
			}
		};
		tagOperator.setGuid(TAG_OPERATOR_NAME);
		
		final String resultForEmpty = tagOperator.getName(Locale.UK);
		assertEquals("Test result for UK (empty) language failed", TAG_OPERATOR_NAME, resultForEmpty);
		
		tagOperator = new TagOperatorImpl() {
			private static final long serialVersionUID = 7039937363849328835L;

			String getDisplayNameLocalizedPropertyFromLocalizedProperties(final Locale locale) {
				return BLANK_STRING;
			}
		};
		tagOperator.setGuid(TAG_OPERATOR_NAME);
		
		final String resultForBlank = tagOperator.getName(Locale.UK);
		assertEquals("Test result for UK (blank) language failed", TAG_OPERATOR_NAME, resultForBlank);
		
	}
	
	/**
	 * Test that if provided language's localized values exist for tag definition they are
	 * correctly retrieved.
	 */
	@Test
	public void testGetLocalizedName() {
		
		tagOperator = new TagOperatorImpl() {
			private static final long serialVersionUID = -1083497217348734800L;

			String getDisplayNameLocalizedPropertyFromLocalizedProperties(final Locale locale) {
				if (locale.equals(Locale.ENGLISH)) {
					return TAG_NAME_EN;
				} else if (locale.equals(new Locale(RU_COUNTRY_CODE))) {
					return TAG_NAME_RU;
				} 
				return null;
			}
		};
		tagOperator.setGuid(TAG_OPERATOR_NAME);
		
		final String resultForEnglish = tagOperator.getName(Locale.ENGLISH);
		assertEquals("Test result for EN language failed", TAG_NAME_EN, resultForEnglish);

		final String resultForRussian = tagOperator.getName(new Locale(RU_COUNTRY_CODE));
		assertEquals("Test result for RU language failed", TAG_NAME_RU, resultForRussian);
	}
	
}
