package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.service.catalog.CategoryTypeService;

/**
 * The unit tests for catalog Category type change set resolver.
 */
public class CatalogCategoryTypeChangeSetResolverImplTest {
	
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private CategoryTypeService categoryTypeService;
	private CategoryType categoryType;
	private final CatalogCategoryTypeChangeSetResolverImpl resolver = new CatalogCategoryTypeChangeSetResolverImpl();
	
	
	/**
	 * The setup code.
	 */
	@Before
	public void setUp() {
		categoryTypeService = context.mock(CategoryTypeService.class);
		categoryType = context.mock(CategoryType.class);
		resolver.setCategoryTypeService(categoryTypeService);
		
	}
	
	
	/**
	 * 	Test for getting the dependency list from a given category Type.
	 */
	@Test
	public void testGetDependency() {
		
		final AttributeGroup attributeGroup = context.mock(AttributeGroup.class);
		
		final Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<AttributeGroupAttribute>();
		final AttributeGroupAttribute attributeGroupAttribute = context.mock(AttributeGroupAttribute.class);
		attributeGroupAttributes.add(attributeGroupAttribute);
		final Attribute attribute = context.mock(Attribute.class);
		
		
		context.checking(new Expectations() { {
			oneOf(categoryType).getAttributeGroup(); will(returnValue(attributeGroup));
			oneOf(attributeGroup).getAttributeGroupAttributes(); will(returnValue(attributeGroupAttributes));
			oneOf(attributeGroupAttribute).getAttribute(); will(returnValue(attribute));
		} });
		
		Set< ? > dependencies = resolver.getChangeSetDependency(categoryType);
		assertEquals("number of dependencies should be 1", 1, dependencies.size());
		assertEquals("dependent object should be attribute", attribute, dependencies.iterator().next());
		
	}

}
