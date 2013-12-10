package com.elasticpath.importexport.common.adapters.catalogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.attribute.impl.AttributeImpl;
import com.elasticpath.importexport.common.adapters.catalogs.helper.impl.AttributeGroupHelperImpl;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;

/**
 * Tests AttributeGroupHelperImpl.
 */
public class AttributeGroupHelperImplTest {

	private static final String ATTRIBUTE_KEY = "attribute_key";

	private final AttributeGroupHelperImpl attributeGroupHelper = new AttributeGroupHelperImpl();
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private Attribute mockAttribute;
	
	private CachingService mockCachingService;
	
	private ElasticPath mockElasticPath;

	@Before
	public void setUp() throws Exception {
		mockAttribute = context.mock(Attribute.class);
		mockElasticPath = context.mock(ElasticPath.class);
		mockCachingService = context.mock(CachingService.class);
		
		setUpAttributeGroupHelper(attributeGroupHelper);
	}

	private void setUpAttributeGroupHelper(final AttributeGroupHelperImpl attributeGroupHelper) {
		attributeGroupHelper.setCachingService(mockCachingService);
		attributeGroupHelper.setElasticPath(mockElasticPath);
	}
	
	/**
	 * Test createAssignedAttributes.
	 */
	@Test
	public void testCreateAssignedAttributes() {
		final Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<AttributeGroupAttribute>();
		
		final AttributeGroupAttribute mockAttributeGroupAttribute = context.mock(AttributeGroupAttribute.class);

		context.checking(new Expectations() {
			{
				oneOf(mockAttributeGroupAttribute).getAttribute();
				will(returnValue(mockAttribute));
				oneOf(mockAttribute).getKey();
				will(returnValue(ATTRIBUTE_KEY));
			}
		});
		
		attributeGroupAttributes.add(mockAttributeGroupAttribute);
		
		List<String> result = attributeGroupHelper.createAssignedAttributes(attributeGroupAttributes);
		assertEquals(Arrays.asList(ATTRIBUTE_KEY), result);
	}

	/**
	 * Tests FindAttribute method.
	 */
	@Test(expected = PopulationRollbackException.class)
	public void testFindAttribute() {
		final String goodAttributeKey = "good_attribute";
		final String badAttributeKey = "bad_attribute";

		context.checking(new Expectations() {
			{
				oneOf(mockCachingService).findAttribiteByKey(goodAttributeKey);
				will(returnValue(mockAttribute));
				oneOf(mockCachingService).findAttribiteByKey(badAttributeKey);
				will(returnValue(null));
			}
		});
		
		assertEquals(mockAttribute, attributeGroupHelper.findAttribute(goodAttributeKey));
		
		attributeGroupHelper.findAttribute(badAttributeKey);
	}

	/**
	 * Tests populateAttributeGroupAttributes.
	 */
	@Test
	public void testPopulateAttributeGroupAttributes() {
		final AttributeGroupAttribute mockAttributeGroupAttribute = context.mock(AttributeGroupAttribute.class);
		
		final Set<AttributeGroupAttribute> attributeGroupAttributeSet = new HashSet<AttributeGroupAttribute>();
		attributeGroupAttributeSet.add(mockAttributeGroupAttribute);

		AttributeGroupHelperImpl testAttributeGroupHelper = new AttributeGroupHelperImpl() {
			@Override
			public Attribute findAttribute(final String attributeKey) {
				assertEquals(ATTRIBUTE_KEY, attributeKey);
				return mockAttribute;
			}
			@Override
			public boolean isAttributeGroupExist(final Set<AttributeGroupAttribute> attributeGroupAttributes, 
												 final AttributeGroupAttribute groupToFind) {
				assertEquals(attributeGroupAttributeSet, attributeGroupAttributes);
				assertEquals(mockAttributeGroupAttribute, groupToFind);
				return false;
			}
		};
		setUpAttributeGroupHelper(testAttributeGroupHelper);

		context.checking(new Expectations() {
			{
				oneOf(mockElasticPath).getBean(with(any(String.class)));
				will(returnValue(mockAttributeGroupAttribute));
				oneOf(mockAttributeGroupAttribute).setAttribute(mockAttribute);
			}
		});
		
		final List<String> assignedAttributes = Arrays.asList(ATTRIBUTE_KEY);
		testAttributeGroupHelper.populateAttributeGroupAttributes(attributeGroupAttributeSet, assignedAttributes, "SOME_TYPE_ATTRIBUTE");
	}


	
	/**
	 * Tests isAttributeGroupExist method.
	 */
	@Test
	public void testIsAttributeGroupExist() {
		final Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<AttributeGroupAttribute>();
		
		final AttributeGroupAttribute attributeGroupAttribute1 = context.mock(AttributeGroupAttribute.class);
		final AttributeGroupAttribute attributeGroupAttribute2 = context.mock(AttributeGroupAttribute.class, "second attribute group attribute");

		context.checking(new Expectations() {
			{
				allowing(attributeGroupAttribute1).getAttribute();
				will(returnValue(new AttributeStubImpl()));
				allowing(attributeGroupAttribute2).getAttribute();
				will(returnValue(new AttributeStubImpl()));
			}
		});
		
		attributeGroupAttributes.add(attributeGroupAttribute1);
		
		assertTrue(attributeGroupHelper.isAttributeGroupExist(attributeGroupAttributes, attributeGroupAttribute1));
		assertFalse(attributeGroupHelper.isAttributeGroupExist(attributeGroupAttributes, attributeGroupAttribute2));
	}
	
	/**
	 * AttributeStub Implementation. Overrides equals and hash code for simplifying test.
	 */
	private static class AttributeStubImpl extends AttributeImpl {
		private static final long serialVersionUID = -904862902119229947L;

		@Override
		public boolean equals(final Object obj) {
			return this == obj; // instance equal
		}
		@Override
		public int hashCode() {
			return 1;
		}
	}
}
