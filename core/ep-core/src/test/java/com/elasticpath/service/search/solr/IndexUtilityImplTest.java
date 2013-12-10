package com.elasticpath.service.search.solr;


import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.test.util.Assert;

/**
 * Tests the {@link IndexUtilityImpl} class.
 */
public class IndexUtilityImplTest {

	IndexUtilityImpl indexUtility = new IndexUtilityImpl();

	/** 
	 * Tests {@link IndexUtilityImpl#escapeFieldValue(String)} returns same value when
	 * it has no white space.
	 */
	@Test
	public void testEscapeFieldValueReturnsSameValueWhenValueHasNoWhitespace() {
		// given
		String fieldValue = "FIELDVALUE";
		String escapedValue = "FIELDVALUE";
		
		// test
		Assert.assertEquals(escapedValue, indexUtility.escapeFieldValue(fieldValue));
	}
	
	/** 
	 * Tests {@link IndexUtilityImpl#escapeFieldValue(String)} returns escaped value when
	 * it has non-alphanumeric characters.
	 */
	@Test
	public void testEscapeFieldValueReturnsEscapedValueWhenValueHasNonAlphanumericCharacters() {
		// given
		String fieldValue = " FIELD VALUE !-()[]\"' ";
		String escapedValue = "FIELDVALUE";
		
		// test
		Assert.assertEquals(escapedValue, indexUtility.escapeFieldValue(fieldValue));
	}

	@Test
	public void testCreateAttributeFieldName() {
		HashMap<String, String> solrAttributeTypeExt = new HashMap<String, String>();
		solrAttributeTypeExt.put("integerValue", "_mockInt");
		solrAttributeTypeExt.put("shortTextValue", "_mockShortText");
		solrAttributeTypeExt.put("longTextValue", "_mockLongText");
		indexUtility.setSolrAttributeTypeExt(solrAttributeTypeExt);

		// expectedSuffix, attributeType, stringTypeOnly, minimalStringAnalysis

		// integer convert to string, minimal analysis
		testCreateAttributeFieldNameSuffix("_s", AttributeType.INTEGER, true, true);

		// integer convert to string, non-minimal analysis (dismax qf workaround)
		testCreateAttributeFieldNameSuffix("_stringForDismax", AttributeType.INTEGER, true, false);

		// minimal analysis text
		testCreateAttributeFieldNameSuffix("_code", AttributeType.SHORT_TEXT, true, true);
		testCreateAttributeFieldNameSuffix("_code", AttributeType.LONG_TEXT, false, true);

		// non-minimal analysis text
		testCreateAttributeFieldNameSuffix("_mockShortText", AttributeType.SHORT_TEXT, true, false);
		testCreateAttributeFieldNameSuffix("_mockLongText", AttributeType.LONG_TEXT, false, false);

		// non-string type no conversion
		testCreateAttributeFieldNameSuffix("_mockInt", AttributeType.INTEGER, false, true);
		testCreateAttributeFieldNameSuffix("_mockInt", AttributeType.INTEGER, false, false);

		// fallback default if not in map
		testCreateAttributeFieldNameSuffix("_st", AttributeType.DATE, false, false);
	}

	private void testCreateAttributeFieldNameSuffix(
			final String expectedAttributeFieldNameSuffix, final AttributeType attributeType,
			final boolean stringTypeOnly, final boolean minimalStringAnalysis) {

		Attribute shortTextAttribute = new AttributeImpl();
		shortTextAttribute.setLocaleDependant(false);
		shortTextAttribute.setAttributeType(attributeType);

		Attribute attribute = shortTextAttribute;
		String attributeFieldName = indexUtility.createAttributeFieldName(attribute, null, stringTypeOnly, minimalStringAnalysis);
		Assert.assertTrue(
				String.format("Expected attributeFieldName '%s' to end with '%s'",
						attributeFieldName, expectedAttributeFieldNameSuffix),
				StringUtils.endsWith(attributeFieldName, expectedAttributeFieldNameSuffix));
	}
}
