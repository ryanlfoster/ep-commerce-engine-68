package com.elasticpath.importexport.common.adapters.promotion;

import static org.junit.Assert.assertEquals;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeUsage;
import com.elasticpath.domain.attribute.impl.AttributeUsageImpl;
import com.elasticpath.importexport.common.adapters.catalogs.AttributeAdapter;
import com.elasticpath.importexport.common.dto.catalogs.AttributeDTO;
import com.elasticpath.importexport.common.dto.catalogs.AttributeTypeType;
import com.elasticpath.importexport.common.dto.catalogs.AttributeUsageType;

/**
 * Tests population of <code>AttributeDTO</code> from <code>Attribute</code> and back to front.
 */
public class AttributeAdapterTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final AttributeAdapter attributeAdapter = new AttributeAdapter();

	/**
	 * Tests that all required getters are called during DTO population.
	 */
	@Test
	public void testPopulateDTO() {

		final Attribute attribute = context.mock(Attribute.class);

		final String attributeKey = "attributeKey";
		final String attributeName = "attributeName";
		final AttributeUsage attributeUsage = AttributeUsageImpl.CATEGORY_USAGE;
		final AttributeType attributeType = AttributeType.SHORT_TEXT;
		final boolean localeDependent = true;
		final boolean isRequired = false;
		final boolean isMultiValueEnabled = true;
		final boolean isGlobal = false;

		context.checking(new Expectations() { {
			oneOf(attribute).getKey(); will(returnValue(attributeKey));
			oneOf(attribute).getName(); will(returnValue(attributeName));
			oneOf(attribute).getAttributeUsage(); will(returnValue(attributeUsage));
			oneOf(attribute).getAttributeType(); will(returnValue(attributeType));
			oneOf(attribute).isLocaleDependant(); will(returnValue(localeDependent));
			oneOf(attribute).isRequired(); will(returnValue(isRequired));
			oneOf(attribute).isMultiValueEnabled(); will(returnValue(isMultiValueEnabled));
			oneOf(attribute).isGlobal(); will(returnValue(isGlobal));
		} });

		final AttributeDTO attributeDto = new AttributeDTO();

		attributeAdapter.populateDTO(attribute, attributeDto);

		assertEquals(attributeKey, attributeDto.getKey());
		assertEquals(attributeName, attributeDto.getName());
		assertEquals(AttributeUsageType.valueOf(attributeUsage), attributeDto.getUsage());
		assertEquals(AttributeTypeType.valueOf(attributeType), attributeDto.getType());
		assertEquals(localeDependent, attributeDto.getMultiLanguage());
		assertEquals(isRequired, attributeDto.getRequired());
		assertEquals(isMultiValueEnabled, attributeDto.getMultivalue());
		assertEquals(isGlobal, attributeDto.getGlobal());
	}
}
