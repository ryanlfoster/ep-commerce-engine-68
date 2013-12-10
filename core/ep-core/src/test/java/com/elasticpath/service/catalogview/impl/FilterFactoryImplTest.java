package com.elasticpath.service.catalogview.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.impl.AttributeRangeFilterImpl;
import com.elasticpath.domain.catalogview.impl.BrandFilterImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfigurationLoader;
import com.elasticpath.test.BeanFactoryExpectationsFactory;

/**
 * Test <code>FilterFactoryImpl</code>.
 */
public class FilterFactoryImplTest {

	private static final String STORE_CODE = "MyCode";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	private FilterFactoryImpl filterFactory;

	private Store store;
	
	private Catalog catalog;

	/**
	 * Prepares for tests.
	 *
	 * @throws Exception -- in case of any errors.
	 */
	@Before
	public void setUp() throws Exception {
		filterFactory = new FilterFactoryImpl();
		catalog = context.mock(Catalog.class);
		
		store = context.mock(Store.class);
		context.checking(new Expectations() {
			{
				allowing(store).getCode(); will(returnValue(STORE_CODE));
				allowing(store).getCatalog(); will(returnValue(catalog));
			}
		});
	}

	/**
	 * Test that calling getFilter() with a Store having a null StoreCode
	 * will throw an EpServiceException.
	 */
	@Test
	public void testGetFilterWithNullStoreCodeThrowsException() {
		try {
			filterFactory.getFilter(null, new StoreImpl());
			fail("Expected an EpServiceException because given Store has null Code");
		} catch (EpServiceException ex) {
			assertNotNull(ex);
		}
	}
	
	/**
	 * Test that if the requested filter is a Category filter and the given store's
	 * Catalog is null, an EpServiceException will be thrown.
	 */
	@Test
	public void testGetFilterCategoryWithNullStoreCatalogThrowsException() {
		final Store store = context.mock(Store.class, "mockStore");
		context.checking(new Expectations() {
			{
				allowing(store).getCode(); will(returnValue(STORE_CODE));
				allowing(store).getCatalog(); will(returnValue(null));
			}
		});
		try {
			filterFactory.getFilter(SeoConstants.CATEGORY_PREFIX, store);
			fail("Expected an EpServiceException because given Store has null Catalog");
		} catch (EpServiceException ex) {
			assertNotNull(ex);
		}
	}
	
	/**
	 * Test method for 'com.elasticpath.service.search.impl.FilterFactoryImpl.getFilter(String)'.
	 */
	@Test
	public void testGetFilterWithBadId() {
		try {
			filterFactory.getFilter("some bad id", store);
			fail("Expect EpCatalogViewRequestBindException!");
		} catch (EpCatalogViewRequestBindException e) {
			// succeed!
			assertNotNull(e);
		}
	}
	
	/**
	 * Test for getAttributeRangeFiltersWithoutPredefinedRanges() with an attribute range that 
	 * have children and one that doesn't. Should only return the one without children in the end.
	 */
	@Test
	public void testGetAttributeRangeFiltersWithChildrenAndNoParent() {
		
		final Map<String, AttributeRangeFilter> attributeRanges = new HashMap<String, AttributeRangeFilter>();
		
		//parent range filter
		AttributeRangeFilter rangeFilterParent = new AttributeRangeFilterImpl();
		String rangeId1 = "arA001";
		rangeFilterParent.setId(rangeId1);
		
		AttributeRangeFilter rangeFilterChild1 = new AttributeRangeFilterImpl();
		String rangeId2 = "arA001__2";
		rangeFilterChild1.setId(rangeId2);
		rangeFilterChild1.setParent(rangeFilterParent);
		
		AttributeRangeFilter rangeFilterChild2 = new AttributeRangeFilterImpl();
		String rangeId3 = "arA001_2_3";
		rangeFilterChild2.setId(rangeId3);
		rangeFilterChild2.setParent(rangeFilterParent);
		
		AttributeRangeFilter rangeNoParentNoChild = new AttributeRangeFilterImpl();
		String rangeIdNoParentNoChild = "arA002";
		rangeNoParentNoChild.setId(rangeIdNoParentNoChild);
		
		rangeFilterParent.addChild(rangeFilterChild1);
		rangeFilterParent.addChild(rangeFilterChild2);
		
		attributeRanges.put(rangeId1, rangeFilterParent);
		attributeRanges.put(rangeId2, rangeFilterChild1);
		attributeRanges.put(rangeId3, rangeFilterChild2);
		attributeRanges.put(rangeIdNoParentNoChild, rangeNoParentNoChild);		
		
		final FilteredNavigationConfigurationLoader fncLoader = context.mock(FilteredNavigationConfigurationLoader.class);
		final FilteredNavigationConfiguration fnc = context.mock(FilteredNavigationConfiguration.class);
		
		filterFactory.setFncLoader(fncLoader);
		
		context.checking(new Expectations() {
			{
				allowing(fncLoader).loadFilteredNavigationConfiguration(STORE_CODE); will(returnValue(fnc));
				allowing(fnc).getAllAttributeRanges(); will(returnValue(attributeRanges));
			}
		});
		
		assertEquals(1, this.filterFactory.getAttributeRangeFiltersWithoutPredefinedRanges(STORE_CODE).size());
		assertEquals(rangeNoParentNoChild, this.filterFactory.getAttributeRangeFiltersWithoutPredefinedRanges(STORE_CODE).get(0));
	}
	
	/**
	 * Test for getAttributeRangeFiltersWithoutPredefinedRanges() with empty map.
	 * Should return an empty map.
	 */
	@Test
	public void testGetAttributeRangeFiltersWithChildrenAndParent() {
		final FilteredNavigationConfigurationLoader fncLoader = context.mock(FilteredNavigationConfigurationLoader.class);
		final FilteredNavigationConfiguration fnc = context.mock(FilteredNavigationConfiguration.class);
		
		filterFactory.setFncLoader(fncLoader);
		
		context.checking(new Expectations() {
			{
				allowing(fncLoader).loadFilteredNavigationConfiguration(STORE_CODE); will(returnValue(fnc));
				allowing(fnc).getAllAttributeRanges(); will(returnValue(getTestAttributeRanges()));
			}

			//mimic a definition of one attribute range with 3 nested ranges
			private Map<String, AttributeRangeFilter> getTestAttributeRanges() {
				Map<String, AttributeRangeFilter> attributeRanges = new HashMap<String, AttributeRangeFilter>();
				
				//parent range filter
				AttributeRangeFilter rangeFilterParent = new AttributeRangeFilterImpl();
				rangeFilterParent.setId("arA001");
				
				AttributeRangeFilter rangeFilterChild1 = new AttributeRangeFilterImpl();
				rangeFilterChild1.setId("arA001__2");
				rangeFilterChild1.setParent(rangeFilterParent);
				
				AttributeRangeFilter rangeFilterChild2 = new AttributeRangeFilterImpl();
				rangeFilterChild2.setId("arA001_2_3");
				rangeFilterChild2.setParent(rangeFilterParent);
				
				rangeFilterParent.addChild(rangeFilterChild1);
				rangeFilterParent.addChild(rangeFilterChild2);
				
				attributeRanges.put("arA001", rangeFilterParent);
				attributeRanges.put("arA001__2", rangeFilterChild1);
				attributeRanges.put("arA001_2_3", rangeFilterChild2);
				
				return attributeRanges;
			}
		});
		
		assertEquals(0, this.filterFactory.getAttributeRangeFiltersWithoutPredefinedRanges(STORE_CODE).size());
	}
	
	/**
	 * Test for getAttributeRangeFiltersWithoutPredefinedRanges() with children only.
	 * Should return an empty map.
	 */
	@Test
	public void testGetAttributeRangeFiltersWithEmptyMap() {
		final FilteredNavigationConfigurationLoader fncLoader = context.mock(FilteredNavigationConfigurationLoader.class);
		final FilteredNavigationConfiguration fnc = context.mock(FilteredNavigationConfiguration.class);
		
		filterFactory.setFncLoader(fncLoader);
		
		context.checking(new Expectations() {
			{
				allowing(fncLoader).loadFilteredNavigationConfiguration(STORE_CODE); will(returnValue(fnc));
				allowing(fnc).getAllAttributeRanges(); will(returnValue(getTestAttributeRanges()));
			}

			//mimic a definition of one attribute range with 3 nested ranges
			private Map<String, AttributeRangeFilter> getTestAttributeRanges() {
				Map<String, AttributeRangeFilter> attributeRanges = new HashMap<String, AttributeRangeFilter>();				
				return attributeRanges;
			}
		});
		
		assertEquals(0, this.filterFactory.getAttributeRangeFiltersWithoutPredefinedRanges(STORE_CODE).size());
	}	
	
	/**
	 * Test testGetFilterBean().  Ensure Bean is retrieved and separatorInToken is set.
	 */
	@Test
	public void testGetFilterBean() {
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		BeanFactoryExpectationsFactory expectationsFactory = new BeanFactoryExpectationsFactory(context, beanFactory);
		try {
			final FilteredNavigationConfigurationLoader mockFncLoader = context.mock(FilteredNavigationConfigurationLoader.class);

			final String filterBeanName = "testFilter";
			final String separatorInToken = "%%%";

			filterFactory.setFncLoader(mockFncLoader);

			context.checking(new Expectations() {
				{
					allowing(beanFactory).getBean(filterBeanName); will(returnValue(new BrandFilterImpl()));
					allowing(mockFncLoader).getSeparatorInToken();  will(returnValue(separatorInToken));
				}
			});

			Filter<?> filterBean = filterFactory.getFilterBean(filterBeanName);

			assertNotNull(filterBean);
			assertEquals(separatorInToken, filterBean.getSeparatorInToken());
		} finally {
			expectationsFactory.close();
		}
	}
}
