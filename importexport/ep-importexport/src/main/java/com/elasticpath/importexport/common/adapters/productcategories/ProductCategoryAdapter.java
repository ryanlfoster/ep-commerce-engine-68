package com.elasticpath.importexport.common.adapters.productcategories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.productcategory.CatalogCategoriesDTO;
import com.elasticpath.importexport.common.dto.productcategory.ProductCategoriesDTO;
import com.elasticpath.importexport.common.dto.productcategory.ProductCategoryDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;

/**
 * Product category adapter.
 */
public class ProductCategoryAdapter extends AbstractDomainAdapterImpl<Product, ProductCategoriesDTO> {

	private static final Logger LOG = Logger.getLogger(ProductCategoryAdapter.class);

	@Override
	public ProductCategoriesDTO createDtoObject() {
		return new ProductCategoriesDTO();
	}

	@Override
	public void populateDTO(final Product product, final ProductCategoriesDTO productCategoriesDTO) {
		productCategoriesDTO.setProductCode(product.getCode());
		productCategoriesDTO.setCatalogCategoriesDTOList(createCatalogCategoriesDTOList(product));
	}

	@Override
	public void populateDomain(final ProductCategoriesDTO productCategoriesDTO, final Product product) {
		List<CatalogCategoriesDTO> catalogCategoriesDTOList = productCategoriesDTO.getCatalogCategoriesDTOList();
		for (CatalogCategoriesDTO catalogCategoriesDTO : catalogCategoriesDTOList) {
			final String catalogCode = catalogCategoriesDTO.getCatalogCode();
			
			if (getCachingService().findCatalogByCode(catalogCode) == null) {
				for (ProductCategoryDTO categoryDTO : catalogCategoriesDTO.getProductCategoryDTOList()) {
					LOG.error(Message.createJobTypeFailure("IE-10200", JobType.PRODUCTCATEGORYASSOCIATION, categoryDTO.getCategoryCode(), 
							catalogCode));
				}
			} else {
				populateProductByCatalogCategories(product, catalogCategoriesDTO);
			}
		}
	}

	/**
	 * Creates the list of CatalogCategoriesDTO for the Product.
	 * 
	 * @param product the product
	 * @return List of CatalogCategoriesDTO
	 */
	List<CatalogCategoriesDTO> createCatalogCategoriesDTOList(final Product product) {
		final List<CatalogCategoriesDTO> catalogCategoryDTOList = new ArrayList<CatalogCategoriesDTO>();
		final Map<String, List<ProductCategoryDTO>> catalogCategoryMap = createCatelogCategoryMap(product);
		
		for (Entry<String, List<ProductCategoryDTO>> entry : catalogCategoryMap.entrySet()) {
			CatalogCategoriesDTO catalogCategoriesDTO = new CatalogCategoriesDTO();
			catalogCategoriesDTO.setCatalogCode(entry.getKey());
			catalogCategoriesDTO.setProductCategoryDTOList(entry.getValue());
			catalogCategoryDTOList.add(catalogCategoriesDTO);
		}
		return catalogCategoryDTOList;
	}

	/**
	 * Create a Map of CatalogCodes and CatelogCategoriesDTOs for the Product.
	 * 
	 * @param product the product
	 * @return HashMap instance where key is CatalogCode and value is the List of ProductCategoryDTO
	 */
	Map<String, List<ProductCategoryDTO>> createCatelogCategoryMap(final Product product) {
		Map<String, List<ProductCategoryDTO>> catalogCategoryMap = new HashMap<String, List<ProductCategoryDTO>>();
		
		for (Category category : product.getCategories()) {
			ProductCategoryDTO productCategoryDTO = new ProductCategoryDTO();
			productCategoryDTO.setCategoryCode(category.getCode());
			productCategoryDTO.setFeaturedOrder(product.getFeaturedRank(category));

			final Catalog catalog = category.getCatalog();
			
			productCategoryDTO.setDefaultCategory(category.equals(product.getDefaultCategory(catalog)));

			addToCatalogCategoryMap(catalogCategoryMap, productCategoryDTO,	catalog.getCode());
		}
		return catalogCategoryMap;
	}

	/**
	 * Adds productCategoryDTO to catalogCategoryMap using catalogCode.
	 * 
	 * @param catalogCategoryMap the Map to add to
	 * @param productCategoryDTO the DTO to add
	 * @param catalogCode the code for key
	 */
	void addToCatalogCategoryMap(final Map<String, List<ProductCategoryDTO>> catalogCategoryMap,
									     final ProductCategoryDTO productCategoryDTO, 
									     final String catalogCode) {

		List<ProductCategoryDTO> productCategoryList = catalogCategoryMap.get(catalogCode);
		if (productCategoryList == null) {
			productCategoryList = new ArrayList<ProductCategoryDTO>();
			catalogCategoryMap.put(catalogCode, productCategoryList);
		}
		
		productCategoryList.add(productCategoryDTO);
	}
	

	/**
	 * Populate product by categories which are found by categoryCode and catalogCode.
	 *  
	 * @param product the product to populate
	 * @param catalogCategoriesDTO the DTO to populate from
	 */
	void populateProductByCatalogCategories(final Product product, final CatalogCategoriesDTO catalogCategoriesDTO) {
		for (ProductCategoryDTO productCategoryDTO : catalogCategoriesDTO.getProductCategoryDTOList()) {
			final String categoryCode = productCategoryDTO.getCategoryCode();
			
			Category category = getCachingService().findCategoryByCode(categoryCode, catalogCategoriesDTO.getCatalogCode());
			
			if (category == null) {
				LOG.error(Message.createJobTypeFailure("IE-10201", JobType.PRODUCTCATEGORYASSOCIATION, categoryCode));				
			} else {
				populateProductByCategory(product, category, productCategoryDTO.isDefaultCategory());
				populateProductByFeaturedOrder(product, category, productCategoryDTO.getFeaturedOrder());
			}
		}
	}

	/**
	 * Adds category to a product or sets it to default if isDefaultCategory is true.
	 * 
	 * @param product the product to populate
	 * @param category the category to add to product
	 * @param isDefaultCategory the flag
	 */
	void populateProductByCategory(final Product product, final Category category, final boolean isDefaultCategory) {
		if (isDefaultCategory) {
			product.setCategoryAsDefault(category);
		} else {	
			product.addCategory(category);
		}
	}	
	
	/**
	 * Sets FeaturedRank to Product using featuredOrder and Category.
	 * 
	 * @param product the product to populate
	 * @param category the category used for populate product
	 * @param featuredOrder the featured order
	 */
	void populateProductByFeaturedOrder(final Product product, final Category category, final int featuredOrder) {
		if (featuredOrder < 0) {
			LOG.error(Message.createJobTypeFailure("IE-10202", JobType.PRODUCTCATEGORYASSOCIATION));
		}
		product.setFeaturedRank(category, featuredOrder);
	}
}
