package com.elasticpath.tags.domain.impl;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

import org.apache.commons.lang.StringUtils;

/**
 * Test for tag definition class.
 */
public class TagDefinitionImplTest {

	private static final String TAG_NAME = "TAG_NAME";
	private static final String TAG_NAME_EN = "English Name";
	private static final String TAG_NAME_RU = "Russkii Variant";
	private static final String RU_COUNTRY_CODE = "ru";
	private static final String BLANK_STRING = "    ";
	private TagDefinitionImpl tagDefinition;
	
	/**
	 * Tests that if language is not provided the default value of name
	 * from tag definition is returned.
	 */
	@Test
	public void testGetLocalizedNameWhenLanguageIsNotProvided() {
		
		tagDefinition = new TagDefinitionImpl();
		tagDefinition.setName(TAG_NAME);
		
		final String resultForNull = tagDefinition.getLocalizedName(null);
		assertEquals("Test result for NULL failed", TAG_NAME, resultForNull);
	}
	
	/**
	 * Test that if provided language is not specified for the tag the value of name
	 * from tag definition is returned.
	 */
	@Test
	public void testGetLocalizedNameWithMissingLanguage() {
		
		tagDefinition = new TagDefinitionImpl() {
			private static final long serialVersionUID = -925840996933053867L;

			String getDisplayNameLocalizedPropertyFromLocalizedProperties(final Locale locale) {
				return null;
			}
		};
		tagDefinition.setName(TAG_NAME);
		
		final String resultForMissing = tagDefinition.getLocalizedName(Locale.UK);
		assertEquals("Test result for UK (missing) language failed", TAG_NAME, resultForMissing);
		
		
		
	}
	
	/**
	 * Test that if provided language is specified for the tag but the localized value of name
	 * is blank then from tag definition name is returned.
	 */
	@Test
	public void testGetLocalizedNameWithBlankLocalizedValue() {
		
		tagDefinition = new TagDefinitionImpl() {
			private static final long serialVersionUID = -5618367756845795099L;

			String getDisplayNameLocalizedPropertyFromLocalizedProperties(final Locale locale) {
				return StringUtils.EMPTY;
			}
		};
		tagDefinition.setName(TAG_NAME);
		
		final String resultForEmpty = tagDefinition.getLocalizedName(Locale.UK);
		assertEquals("Test result for UK (empty) language failed", TAG_NAME, resultForEmpty);
		
		tagDefinition = new TagDefinitionImpl() {
			private static final long serialVersionUID = 1659504255456092527L;

			String getDisplayNameLocalizedPropertyFromLocalizedProperties(final Locale locale) {
				return BLANK_STRING;
			}
		};
		tagDefinition.setName(TAG_NAME);
		
		final String resultForBlank = tagDefinition.getLocalizedName(Locale.UK);
		assertEquals("Test result for UK (blank) language failed", TAG_NAME, resultForBlank);
		
	}
	
	/**
	 * Test that if provided language's localized values exist for tag definition they are
	 * correctly retrieved.
	 */
	@Test
	public void testGetLocalizedName() {
		
		tagDefinition = new TagDefinitionImpl() {
			private static final long serialVersionUID = -497628511730942680L;

			String getDisplayNameLocalizedPropertyFromLocalizedProperties(final Locale locale) {
				if (locale.equals(Locale.ENGLISH)) {
					return TAG_NAME_EN;
				} else if (locale.equals(new Locale(RU_COUNTRY_CODE))) {
					return TAG_NAME_RU;
				} 
				return null;
			}
		};
		tagDefinition.setName(TAG_NAME);
		
		final String resultForEnglish = tagDefinition.getLocalizedName(Locale.ENGLISH);
		assertEquals("Test result for EN language failed", TAG_NAME_EN, resultForEnglish);

		final String resultForRussian = tagDefinition.getLocalizedName(new Locale(RU_COUNTRY_CODE));
		assertEquals("Test result for RU language failed", TAG_NAME_RU, resultForRussian);
	}
	
}
