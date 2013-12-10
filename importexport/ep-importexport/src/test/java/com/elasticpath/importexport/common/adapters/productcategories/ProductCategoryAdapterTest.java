package com.elasticpath.importexport.common.adapters.productcategories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.productcategory.CatalogCategoriesDTO;
import com.elasticpath.importexport.common.dto.productcategory.ProductCategoriesDTO;
import com.elasticpath.importexport.common.dto.productcategory.ProductCategoryDTO;

/**
 * Verify that ProductCategoryAdapter populates category domain object from DTO properly and vice versa. 
 * <br>Nested adapters should be tested separately.
 */
public class ProductCategoryAdapterTest {

	private static final String PRODUCT_CODE = "productCode";

	private static final String GOOD_CATALOG = "goodCatalog";

	private static final String NULL_CATALOG = "nullCatalog";

	private static final String NULL_CATEGORY = "nullCategory";

	private static final String GOOD_CATEGORY = "goodCategory";

	private static final String CATEGORY_CODE = "categoryCode";

	private static final String CATALOG_CODE = "catalogCode";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private Product mockProduct;
	
	private Catalog mockCatalog;
	
	private Category mockCategory;

	private ElasticPath mockElasticPath;
	
	private CachingService mockCachingService;

	private ProductCategoryAdapter productCategoryAdapter;
	
	/**
	 * Setup test.
	 */
	@Before
	public void setUp() throws Exception {

		mockProduct = context.mock(Product.class);
		mockCatalog = context.mock(Catalog.class);
		mockCategory = context.mock(Category.class);
		
		mockElasticPath = context.mock(ElasticPath.class);
		mockCachingService = context.mock(CachingService.class);

		productCategoryAdapter = new ProductCategoryAdapter();
		setUpProductCategoryAdapter(productCategoryAdapter);
	}

	private void setUpProductCategoryAdapter(final ProductCategoryAdapter productCategoryAdapter) {
		productCategoryAdapter.setElasticPath(mockElasticPath);
		productCategoryAdapter.setCachingService(mockCachingService);
	}
	
	/**
	 * Tests createDtoObject.
	 */
	@Test
	public void testCreateDtoObject() {
		assertNotNull(productCategoryAdapter.createDtoObject());
	}

	/**
	 * Tests populateDTO.
	 */
	@Test
	public void testPopulateDTO() {
		context.checking(new Expectations() {
			{
				oneOf(mockProduct).getCategories();
				will(returnValue(Collections.emptySet()));
				oneOf(mockProduct).getCode();
				will(returnValue(PRODUCT_CODE));
			}
		});
		
		ProductCategoryAdapter adapter = new ProductCategoryAdapter() {
			@Override
			List<CatalogCategoriesDTO> createCatalogCategoriesDTOList(final Product product) {
				assertEquals(Collections.emptySet(), product.getCategories());
				return Collections.emptyList();
			}
		};
		setUpProductCategoryAdapter(adapter);
		
		ProductCategoriesDTO productCategoriesDTO = new ProductCategoriesDTO();
		
		adapter.populateDTO(mockProduct, productCategoriesDTO);
		
		assertEquals(PRODUCT_CODE, productCategoriesDTO.getProductCode());
		assertEquals(Collections.emptyList(), productCategoriesDTO.getCatalogCategoriesDTOList());
	}

	/**
	 * Tests populateDomain.
	 */
	@Test
	public void testPopulateDomain() {
		context.checking(new Expectations() {
			{
				oneOf(mockProduct).addCategory(mockCategory);
				oneOf(mockCachingService).findCatalogByCode(GOOD_CATALOG);
				will(returnValue(mockCatalog));
				oneOf(mockCachingService).findCatalogByCode(NULL_CATALOG);
				will(returnValue(null));
			}
		});
		
		ProductCategoriesDTO productCategoriesDTO = new ProductCategoriesDTO();
		
		final CatalogCategoriesDTO goodCatalogCategoriesDTO = new CatalogCategoriesDTO();
		goodCatalogCategoriesDTO.setCatalogCode(GOOD_CATALOG);
		
		final CatalogCategoriesDTO nullCatalogCategoriesDTO = new CatalogCategoriesDTO();
		nullCatalogCategoriesDTO.setCatalogCode(NULL_CATALOG);
		
		productCategoriesDTO.setCatalogCategoriesDTOList(Arrays.asList(goodCatalogCategoriesDTO, nullCatalogCategoriesDTO));

		ProductCategoryAdapter adapter = new ProductCategoryAdapter() {
			@Override
			void populateProductByCatalogCategories(final Product product, final CatalogCategoriesDTO catalogCategoriesDTO) {
				assertEquals(goodCatalogCategoriesDTO, catalogCategoriesDTO);
				product.addCategory(mockCategory);
			}
		};
		setUpProductCategoryAdapter(adapter);
		
		adapter.populateDomain(productCategoriesDTO , mockProduct);
	}

	/**
	 * Tests createCatalogCategoriesDTOList.
	 */
	@Test
	public void testCreateCatalogCategoriesDTOList() {
		ProductCategoryAdapter adapter = new ProductCategoryAdapter() {
			@Override
			Map<String, List<ProductCategoryDTO>> createCatelogCategoryMap(final Product product) {
				HashMap<String, List<ProductCategoryDTO>> result = new HashMap<String, List<ProductCategoryDTO>>();
				ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();
				productCategoryDTO.setCategoryCode(CATEGORY_CODE);
				productCategoryDTO.setFeaturedOrder(1);
				productCategoryDTO.setDefaultCategory(Boolean.FALSE);
				result.put(CATALOG_CODE, Arrays.asList(productCategoryDTO));
				return result;
			}
		};
		setUpProductCategoryAdapter(adapter);
		
		List<CatalogCategoriesDTO> catalogCategoriesDTOList = adapter.createCatalogCategoriesDTOList(mockProduct);
		
		CatalogCategoriesDTO catalogCategoriesDTO = catalogCategoriesDTOList.get(0);
		List<ProductCategoryDTO> productCategoryDTOList = catalogCategoriesDTO.getProductCategoryDTOList();
		ProductCategoryDTO productCategoryDTO = productCategoryDTOList.get(0);

		assertEquals(1, catalogCategoriesDTOList.size());
		assertEquals(CATALOG_CODE, catalogCategoriesDTO.getCatalogCode());
		assertEquals(1, productCategoryDTOList.size());
		assertEquals(CATEGORY_CODE , productCategoryDTO.getCategoryCode());
		assertEquals(1 , productCategoryDTO.getFeaturedOrder());
		assertEquals(Boolean.FALSE , productCategoryDTO.isDefaultCategory());
	}

	/**
	 * Tests createCatelogCategoryMap.
	 */
	@Test
	public void testCreateCatelogCategoryMap() {
		final Set<Category> categorySet = new HashSet<Category>();
		categorySet.add(mockCategory);

		context.checking(new Expectations() {
			{
				oneOf(mockProduct).getCategories();
				will(returnValue(categorySet));
				oneOf(mockProduct).getFeaturedRank(mockCategory);
				will(returnValue(1));
				oneOf(mockProduct).getDefaultCategory(mockCatalog);
				will(returnValue(mockCategory));

				oneOf(mockCategory).getCode();
				will(returnValue(CATEGORY_CODE));
				oneOf(mockCategory).getCatalog();
				will(returnValue(mockCatalog));

				oneOf(mockCatalog).getCode();
				will(returnValue(CATALOG_CODE));
			}
		});
		
		Map<String, List<ProductCategoryDTO>> catalogCategoryMap = productCategoryAdapter.createCatelogCategoryMap(mockProduct);
		
		assertEquals(1, catalogCategoryMap.size());

		List<ProductCategoryDTO> productCategoryDTOList = catalogCategoryMap.get(CATALOG_CODE);
		assertEquals(1, productCategoryDTOList.size());
		
		ProductCategoryDTO productCategoryDTO = productCategoryDTOList.get(0);
		assertEquals(CATEGORY_CODE , productCategoryDTO.getCategoryCode());
		assertEquals(1 , productCategoryDTO.getFeaturedOrder());
		assertEquals(Boolean.TRUE , productCategoryDTO.isDefaultCategory());
	}

	/**
	 * Tests addToCatalogCategoryMap.
	 */
	@Test
	public void testAddToCatalogCategoryMap() {
		Map<String, List<ProductCategoryDTO>> catalogCategoryMap = new HashMap<String, List<ProductCategoryDTO>>();
		ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();
		
		productCategoryAdapter.addToCatalogCategoryMap(catalogCategoryMap, productCategoryDTO, CATALOG_CODE);
		
		List<ProductCategoryDTO> productCategoryDTOList = catalogCategoryMap.get(CATALOG_CODE);
		
		assertEquals(1, catalogCategoryMap.size());
		assertEquals(1, productCategoryDTOList.size());
		assertEquals(productCategoryDTO, productCategoryDTOList.get(0));
	}

	/**
	 * Tests populateProductByCatalogCategories.
	 */
	@Test
	public void testPopulateProductByCatalogCategories() {
		context.checking(new Expectations() {
			{
				oneOf(mockProduct).setFeaturedRank(mockCategory, 1);
				oneOf(mockProduct).setCategoryAsDefault(mockCategory);

				oneOf(mockCachingService).findCategoryByCode(GOOD_CATEGORY, CATALOG_CODE);
				will(returnValue(mockCategory));

				oneOf(mockCachingService).findCategoryByCode(NULL_CATEGORY, CATALOG_CODE);
				will(returnValue(null));
			}
		});
		
		final List<ProductCategoryDTO> productCategoryDTOList = new ArrayList<ProductCategoryDTO>();
		productCategoryDTOList.add(createGoodProductCategoryDTO());
		productCategoryDTOList.add(createNullProductCategoryDTO());
		
		final CatalogCategoriesDTO catalogCategoriesDTO = new CatalogCategoriesDTO();
		catalogCategoriesDTO.setProductCategoryDTOList(productCategoryDTOList);
		catalogCategoriesDTO.setCatalogCode(CATALOG_CODE);
		
		ProductCategoryAdapter adapter = new ProductCategoryAdapter() {
			@Override
			void populateProductByCategory(final Product product, final Category category, final boolean isDefaultCategory) {
				product.setCategoryAsDefault(category);
			}
			@Override
			void populateProductByFeaturedOrder(final Product product, final Category category, final int featuredOrder) {
				product.setFeaturedRank(category, featuredOrder);
			}
		};
		setUpProductCategoryAdapter(adapter);
		
		adapter.populateProductByCatalogCategories(mockProduct, catalogCategoriesDTO);
	}

	/**
	 * @return
	 */
	private ProductCategoryDTO createNullProductCategoryDTO() {
		ProductCategoryDTO nullProductCategoryDTO = new ProductCategoryDTO();
		nullProductCategoryDTO.setCategoryCode(NULL_CATEGORY);
		return nullProductCategoryDTO;
	}

	/**
	 * @return
	 */
	private ProductCategoryDTO createGoodProductCategoryDTO() {
		ProductCategoryDTO goodProductCategoryDTO = new ProductCategoryDTO();
		goodProductCategoryDTO.setCategoryCode(GOOD_CATEGORY);
		goodProductCategoryDTO.setFeaturedOrder(1);
		goodProductCategoryDTO.setDefaultCategory(true);
		return goodProductCategoryDTO;
	}

	/**
	 * Tests populateProductByCategory with isDefaultCategory == true.
	 */
	@Test
	public void testPopulateProductByCategoryOnDefaultCategory() {
		context.checking(new Expectations() {
			{
				oneOf(mockProduct).setCategoryAsDefault(mockCategory);
			}
		});
		productCategoryAdapter.populateProductByCategory(mockProduct, mockCategory, true);
	}
	
	/**
	 * Tests populateProductByCategory with isDefaultCategory == false.
	 */
	@Test
	public void testPopulateProductByCategory() {
		context.checking(new Expectations() {
			{
				oneOf(mockProduct).addCategory(mockCategory);
			}
		});
		productCategoryAdapter.populateProductByCategory(mockProduct, mockCategory, false);
	}

	/**
	 * Tests populateProductByFeaturedOrder.
	 */
	@Test
	public void testPopulateProductByFeaturedOrder() {
		context.checking(new Expectations() {
			{
				oneOf(mockProduct).setFeaturedRank(mockCategory, 1);
			}
		});
		productCategoryAdapter.populateProductByFeaturedOrder(mockProduct, mockCategory, 1);
		
//		try {
//			productCategoryAdapter.populateProductByFeaturedOrder(mockProduct, mockCategory, -1);
//			fail("PopulationRuntimeException must be thrown");
//		} catch (PopulationRuntimeException expected) {
//			assertNotNull(expected);
//		}
	}
}
