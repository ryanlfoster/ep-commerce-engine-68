package com.elasticpath.service.changeset.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.impl.AttributeGroupAttributeImpl;
import com.elasticpath.domain.attribute.impl.AttributeGroupImpl;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.catalog.impl.ProductTypeImpl;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.service.catalog.ProductTypeService;

/** Test for Product Type Dependency Resolver.
 */
public class ProductTypeChangeSetDependencyResolverTest {

	private static final String SINGLE_DEPENDENT_OBJECT_ERROR_MESSAGE = "There should be only 1 dependent object";
	private static final String PRODUCT_TYPE_GUID = "PRODUCT_TYPE_GUID";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private ProductTypeChangeSetDependencyResolver resolver;
	private ProductType productType;
	
	/**
	 * Setup code.
	 */
	@Before
	public void setUp() {
		resolver = new ProductTypeChangeSetDependencyResolver();
		productType = new  ProductTypeImpl();

	}

	/**
	 * Test object that isnt a product type.
	 */
	@Test
	public void testNonProductType() {

		Object object = new Object();
		Set<?> changeSetDependency = resolver.getChangeSetDependency(object);
		assertEquals("There should be no dependent objects", 0, changeSetDependency.size());

	}
	/**
	 * Test that a sku attribute will show up in the dependent item list.
	 */
	@Test
	public void testSkuAttributeConflicts() {
		
		final Attribute attribute = context.mock(Attribute.class);
		final AttributeGroup attributeGroup = createAttributeGroup(attribute);
		productType.setSkuAttributeGroup(attributeGroup);
		productType.setProductAttributeGroup(new AttributeGroupImpl());
		
		Set<?> changeSetDependency = resolver.getChangeSetDependency(productType);
		assertEquals("Attribute should be a dependent object", attribute, changeSetDependency.iterator().next());
		assertEquals(SINGLE_DEPENDENT_OBJECT_ERROR_MESSAGE, 1, changeSetDependency.size());
		
	}

	/**
	 * Test that a sku attribute will show up in the dependent item list.
	 */
	@Test
	public void testEmptyConflicts() {

		productType.setSkuAttributeGroup(new AttributeGroupImpl());
		productType.setProductAttributeGroup(new AttributeGroupImpl());

		Set<?> changeSetDependency = resolver.getChangeSetDependency(productType);
		assertEquals("There should be no dependent objects", 0, changeSetDependency.size());

	}
	
	/**
	 * Test that a sku attribute will show up in the dependent item list.
	 */
	@Test
	public void testProductAttributeConflicts() {

		final Attribute attribute = context.mock(Attribute.class);
		final AttributeGroup attributeGroup = createAttributeGroup(attribute);
		productType.setSkuAttributeGroup(new AttributeGroupImpl());
		productType.setProductAttributeGroup(attributeGroup);

		Set<?> changeSetDependency = resolver.getChangeSetDependency(productType);
		assertEquals("Attribute should be a dependent object", attribute, changeSetDependency.iterator().next());
		assertEquals(SINGLE_DEPENDENT_OBJECT_ERROR_MESSAGE, 1, changeSetDependency.size());
	}
	
	/**
	 * Test that both sku and product attributes are in the dependency list.
	 */
	@Test
	public void testProductAndSKuAttributeConflicts() {

		final Attribute skuAttribute = context.mock(Attribute.class, "SkuAttribute");
		final Attribute productAttribute = context.mock(Attribute.class, "ProductAttribute");
		final AttributeGroup skuAttributeGroup = createAttributeGroup(skuAttribute);
		final AttributeGroup productAttributeGroup = createAttributeGroup(productAttribute);
		productType.setSkuAttributeGroup(skuAttributeGroup);
		productType.setProductAttributeGroup(productAttributeGroup);

		Set<?> changeSetDependency = resolver.getChangeSetDependency(productType);
		assertTrue("Sku attribute should be a dependent object", changeSetDependency.contains(skuAttribute));
		assertTrue("Product attribute should be a dependent object", changeSetDependency.contains(productAttribute));
		assertEquals("There should be only 2 dependent objects", 2, changeSetDependency.size());
	}
	
	
	private AttributeGroup createAttributeGroup(final Attribute attribute) {
		final AttributeGroup attributeGroup = new AttributeGroupImpl();
		final AttributeGroupAttribute attributeGroupAttribute = new AttributeGroupAttributeImpl();
		final LinkedHashSet<AttributeGroupAttribute> attributeGroupAttributes = new LinkedHashSet<AttributeGroupAttribute>();
		
		attributeGroupAttribute.setAttribute(attribute);
		attributeGroupAttributes.add(attributeGroupAttribute);
		attributeGroup.setAttributeGroupAttributes(attributeGroupAttributes);
		
		return attributeGroup;
	}
	
	/**
	 * Tests the getObject method.
	 */
	@Test
	public void testGetObject() {
		final ProductTypeService productTypeService = context.mock(ProductTypeService.class);
		final BusinessObjectDescriptor businessDescriptor = context.mock(BusinessObjectDescriptor.class);
		context.checking(new Expectations() { {
			oneOf(businessDescriptor).getObjectIdentifier(); will(returnValue(PRODUCT_TYPE_GUID));
			oneOf(productTypeService).findByGuid(PRODUCT_TYPE_GUID); will(returnValue(productType));
		} });
		resolver.setProductTypeService(productTypeService);
		Object object = resolver.getObject(businessDescriptor, ProductType.class);
		assertEquals("Object should be a productType", productType, object);
	}
	
	/**
	 * Tests that sku options are dependent objects.
	 */
	@Test
	public void testSkuOptions() {
		productType.setSkuAttributeGroup(new AttributeGroupImpl());
		productType.setProductAttributeGroup(new AttributeGroupImpl());
		
		SkuOption skuOption = context.mock(SkuOption.class);
		productType.addOrUpdateSkuOption(skuOption);
		
		Set<?> changeSetDependency = resolver.getChangeSetDependency(productType);
		assertEquals("Sku Option should be a dependent object", skuOption, changeSetDependency.iterator().next());
		assertEquals(SINGLE_DEPENDENT_OBJECT_ERROR_MESSAGE, 1, changeSetDependency.size());
	}
}
