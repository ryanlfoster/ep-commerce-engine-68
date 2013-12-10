package com.elasticpath.importexport.common.adapters.catalogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.catalog.impl.CategoryTypeImpl;
import com.elasticpath.importexport.common.adapters.catalogs.helper.AttributeGroupHelper;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.catalogs.CategoryTypeDTO;

/**
 * Verify that CategoryTypeAdapter populates catalog domain object from DTO properly and vice versa. 
 * Nested adapters should be tested separately.
 */
public class CategoryTypeAdapterTest {

	private static final String CATEGORY_TYPE_NAME = "typename";

	private static final String CATEGORY_TYPE_TEMPLATE = "template";

	private static final String ATTRIBUTE_KEY = "attribute_key";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private Attribute mockAttribute;

	private ElasticPath mockElasticPath;
	
	private CachingService mockCachingService;
	
	private AttributeGroupHelper mockAttributeGroupHelper;
	
	/**
	 * Setup test.
	 */
	@Before
	public void setUp() throws Exception {
		mockAttribute = context.mock(Attribute.class);
		
		mockElasticPath = context.mock(ElasticPath.class);
		
		mockCachingService = context.mock(CachingService.class);
		mockAttributeGroupHelper = context.mock(AttributeGroupHelper.class);
		context.checking(new Expectations() {
			{
				allowing(mockCachingService).findAttribiteByKey(ATTRIBUTE_KEY);
				will(returnValue(mockAttribute));

				allowing(mockElasticPath).getBean(ContextIdNames.CATEGORY_TYPE);
				will(returnValue(new CategoryTypeImpl()));
			}
		});
	}

	private CategoryTypeAdapter createDefaultCategoryTypeAdapter() {
		CategoryTypeAdapter categoryTypeAdapter = new CategoryTypeAdapter();
		setUpCategoryTypeAdapter(categoryTypeAdapter);
		return categoryTypeAdapter;
	}
	
	private void setUpCategoryTypeAdapter(final CategoryTypeAdapter categoryTypeAdapter) {
		categoryTypeAdapter.setElasticPath(mockElasticPath);
		categoryTypeAdapter.setCachingService(mockCachingService);
		categoryTypeAdapter.setAttributeGroupHelper(mockAttributeGroupHelper);
	}

	/**
	 * Check that all required fields for DTO object are being set during DTO population.
	 */
	@Test
	public void testPopulateDTO() {
		final CategoryTypeAdapter categoryTypeAdapter = createDefaultCategoryTypeAdapter();
		
		final AttributeGroup mockAttributeGroup = context.mock(AttributeGroup.class);
		final CategoryType mockDomain = context.mock(CategoryType.class);

		context.checking(new Expectations() {
			{
                oneOf(mockDomain).getGuid();
                will(returnValue("AFD5EF35-F98F-4AC3-8893-8107D1EB0CEA"));
				oneOf(mockDomain).getName();
				will(returnValue(CATEGORY_TYPE_NAME));
				oneOf(mockDomain).getTemplate();
				will(returnValue(CATEGORY_TYPE_TEMPLATE));
				oneOf(mockDomain).getAttributeGroup();
				will(returnValue(mockAttributeGroup));
				oneOf(mockAttributeGroup).getAttributeGroupAttributes();
				will(returnValue(Collections.emptySet()));

				oneOf(mockAttributeGroupHelper).createAssignedAttributes(Collections.<AttributeGroupAttribute>emptySet());
				will(returnValue(Collections.emptyList()));
			}
		});

		CategoryTypeDTO dto = categoryTypeAdapter.createDtoObject();
		
		categoryTypeAdapter.populateDTO(mockDomain, dto);
		
		assertEquals(CATEGORY_TYPE_NAME, dto.getName());
		assertEquals(CATEGORY_TYPE_TEMPLATE, dto.getTemplate());
		assertEquals(Collections.emptyList(), dto.getAssignedAttributes());
	}

	/**
	 * Check that all required fields for domain object are being set during domain population.
	 */
	@Test
	public void testPopulateDomain() {
		final CategoryTypeDTO dto = createCategoryTypeDTO();
		final CategoryType mockDomain = context.mock(CategoryType.class);
		context.checking(new Expectations() {
			{
				oneOf(mockDomain).setName(CATEGORY_TYPE_NAME);
				oneOf(mockDomain).setTemplate(CATEGORY_TYPE_TEMPLATE);
			}
		});

		final CategoryTypeAdapter categoryTypeAdapter = new CategoryTypeAdapter() {
			@Override
			Set<AttributeGroupAttribute> createAttributeGroupAttributes(final CategoryType categoryType) {
				assertEquals(mockDomain, categoryType);
				return Collections.emptySet();
			}
		};
		setUpCategoryTypeAdapter(categoryTypeAdapter);

		context.checking(new Expectations() {
			{
				oneOf(mockAttributeGroupHelper).populateAttributeGroupAttributes(
						Collections.<AttributeGroupAttribute>emptySet(), dto.getAssignedAttributes(), ContextIdNames.CATEGORY_TYPE_ATTRIBUTE);
			}
		});
		
		categoryTypeAdapter.populateDomain(dto, mockDomain);
	}

	private CategoryTypeDTO createCategoryTypeDTO() {
		CategoryTypeDTO dto = new CategoryTypeDTO();
		dto.setName(CATEGORY_TYPE_NAME);
		dto.setTemplate(CATEGORY_TYPE_TEMPLATE);
		dto.setAssignedAttributes(Arrays.asList(ATTRIBUTE_KEY));
		return dto;
	}
	
	/**
	 * Tests createAttributeGroupAttributes.
	 */
	@Test
	public void testCreateAttributeGroupAttributes() {
		final CategoryTypeAdapter categoryTypeAdapter = createDefaultCategoryTypeAdapter();
		
		final CategoryType mockDomain = context.mock(CategoryType.class);
		final AttributeGroup mockAttributeGroup = context.mock(AttributeGroup.class);

		final HashSet<AttributeGroupAttribute> testSet = new HashSet<AttributeGroupAttribute>();
		context.checking(new Expectations() {
			{
				oneOf(mockDomain).getAttributeGroup();
				will(returnValue(mockAttributeGroup));

				oneOf(mockAttributeGroup).getAttributeGroupAttributes();
				will(returnValue(null));

				allowing(mockAttributeGroup).setAttributeGroupAttributes(testSet);
				allowing(mockDomain).setAttributeGroup(mockAttributeGroup);
			}
		});
		
		Set<AttributeGroupAttribute> result = categoryTypeAdapter.createAttributeGroupAttributes(mockDomain);
		
		assertEquals(testSet, result);
	}
	
	/**
	 * Check that CreateDtoObject works. 
	 */
	@Test
	public void testCreateDtoObject() {
		assertNotNull(createDefaultCategoryTypeAdapter().createDtoObject());
	}
	
	/**
	 * Check that createDomainObject works.
	 */
	@Test
	public void testCreateDomainObject() {
		assertNotNull(createDefaultCategoryTypeAdapter().createDomainObject());
	}
}
