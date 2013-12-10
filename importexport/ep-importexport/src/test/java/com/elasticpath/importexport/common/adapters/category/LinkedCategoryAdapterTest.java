package com.elasticpath.importexport.common.adapters.category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.impl.LinkedCategoryImpl;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.category.LinkedCategoryDTO;

/**
 * Verify that LinkedCategoryAdapter populates category domain object from DTO properly and vice versa. 
 * <br>Nested adapters should be tested separately.
 */
public class LinkedCategoryAdapterTest {

	private static final String CATALOG_CODE = "catalogCode";

	private static final Boolean EXCLUDED = Boolean.TRUE;
	
	private static final Integer ORDERING = 1;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ElasticPath mockElasticPath;
	
	private CachingService mockCachingService;
	
	private Catalog mockCatalog;

	private LinkedCategoryAdapter linkedCategoryAdapter;
	
	@Before
	public void setUp() throws Exception {
		mockCatalog = context.mock(Catalog.class);
		mockElasticPath = context.mock(ElasticPath.class);
		mockCachingService = context.mock(CachingService.class);

		
		linkedCategoryAdapter = new LinkedCategoryAdapter();
		linkedCategoryAdapter.setElasticPath(mockElasticPath);
		linkedCategoryAdapter.setCachingService(mockCachingService);
	}
	
	/**
	 * Tests PopulateDTO.
	 */
	@Test
	public void testPopulateDTO() {
		final Category mockLinkedCategory = context.mock(Category.class);
		
		LinkedCategoryDTO linkedCategoryDTO = new LinkedCategoryDTO();

		context.checking(new Expectations() {
			{
				oneOf(mockCatalog).getCode();
				will(returnValue(CATALOG_CODE));

				oneOf(mockLinkedCategory).getOrdering();
				will(returnValue(ORDERING));
				oneOf(mockLinkedCategory).getCatalog();
				will(returnValue(mockCatalog));
				oneOf(mockLinkedCategory).isIncluded();
				will(returnValue(!EXCLUDED));
			}
		});
		
		linkedCategoryAdapter.populateDTO(mockLinkedCategory, linkedCategoryDTO);
		
		assertEquals(EXCLUDED, linkedCategoryDTO.getExcluded());
		assertEquals(ORDERING, linkedCategoryDTO.getOrder());
		assertEquals(CATALOG_CODE, linkedCategoryDTO.getVirtualCatalogCode());
	}

	/**
	 * Tests PopulateDomain.
	 */
	@Test
	public void testPopulateDomain() {
		final Category mockLinkedCategory = context.mock(Category.class);
		
		LinkedCategoryDTO linkedCategoryDTO = new LinkedCategoryDTO();
		
		linkedCategoryDTO.setOrder(ORDERING);
		linkedCategoryDTO.setExcluded(EXCLUDED);
		linkedCategoryDTO.setVirtualCatalogCode(CATALOG_CODE);

		context.checking(new Expectations() {
			{
				oneOf(mockCachingService).findCatalogByCode(CATALOG_CODE);
				will(returnValue(mockCatalog));

				oneOf(mockLinkedCategory).setOrdering(ORDERING);
				oneOf(mockLinkedCategory).setCatalog(mockCatalog);
				oneOf(mockLinkedCategory).setIncluded(!EXCLUDED);
			}
		});
		
		linkedCategoryAdapter.populateDomain(linkedCategoryDTO, mockLinkedCategory);
	}

	/**
	 * Check that CreateDtoObject works. 
	 */
	@Test
	public void testCreateDomainObject() {
		context.checking(new Expectations() {
			{
				allowing(mockElasticPath).getBean(ContextIdNames.LINKED_CATEGORY);
				will(returnValue(new LinkedCategoryImpl()));
			}
		});
		assertNotNull(linkedCategoryAdapter.createDomainObject());
	}

	/**
	 * Check that createDomainObject works.
	 */
	@Test
	public void testCreateDtoObject() {
		assertNotNull(linkedCategoryAdapter.createDtoObject());
	}

}
