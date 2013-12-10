package com.elasticpath.service.catalog.impl;

import java.util.Collection;
import java.util.Collections;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.CategoryLoadTuner;
import com.elasticpath.domain.catalog.ProductCategory;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductCategoryService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;

/**
 * Service for working with {@link ProductCategory}s.
 */
public class ProductCategoryServiceImpl extends AbstractEpPersistenceServiceImpl implements ProductCategoryService {

	private CategoryService categoryService;

	private CategoryLoadTuner categoryLoadTunerMinimal;

	@Override
	public Collection<ProductCategory> findByCategoryUid(final long categoryUid) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FIND_BY_CATEGORY_UID", categoryUid);
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("FIND_BY_PRODUCT_CATEGORY_UID", uid);
	}

	@Override
	public ProductCategory saveOrUpdate(final ProductCategory productCategory) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().saveOrUpdate(productCategory);
	}

	@Override
	public void remove(final ProductCategory productCategory) throws EpServiceException {
		sanityCheck();
		getPersistenceEngine().delete(productCategory);
	}

	@Override
	public Collection<ProductCategory> findByCategoryAndCatalog(final String catalogCode, final String categoryCode) {
		sanityCheck();
		if (catalogCode == null) {
			throw new EpServiceException("Missing catalog code argument.");
		}
		if (categoryCode == null) {
			throw new EpServiceException("Missing category code argument.");
		}

		Category category = getCategoryService().findByGuid(categoryCode, catalogCode, getCategoryLoadTunerMinimal());
		if (category == null) {
			return Collections.emptyList();
		}
		long categoryUid = category.getUidPk();
		return findByCategoryUid(categoryUid);
	}

	@Override
	public ProductCategory findByCategoryAndProduct(final String catalogCode, final String categoryCode, final String productCode) {
		sanityCheck();
		if (catalogCode == null) {
			throw new EpServiceException("Missing catalog code argument.");
		}
		if (categoryCode == null) {
			throw new EpServiceException("Missing category code argument.");
		}
		if (productCode == null) {
			throw new EpServiceException("Missing product code argument.");
		}

		Category category = getCategoryService().findByGuid(categoryCode, catalogCode, getCategoryLoadTunerMinimal());
		if (category == null) {
			return null;
		}
		long categoryUid = category.getUidPk();
		Collection<ProductCategory> result = getPersistenceEngine().retrieveByNamedQuery("FIND_BY_CATEGORY_UID_AND_PRODUCT_CODE",
				categoryUid,
				productCode);

		if (result.size() == 1) {
			return result.iterator().next();
		} else if (result.size() > 1) {
			throw new EpServiceException("Inconsistent data. Found [" + result.size() + "] product categories matching catalog code: ["
					+ catalogCode + "], category code: [" + categoryCode + "], and product code: [" + productCode + "].");
		}
		return null;
	}

	/**
	 * Get the category service.
	 * 
	 * @return The category service.
	 */
	public CategoryService getCategoryService() {
		return categoryService;
	}

	/**
	 * Set the category service.
	 * 
	 * @param categoryService The category service.
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * Gets the category load tuner.
	 * 
	 * @return The load tuner
	 */
	public CategoryLoadTuner getCategoryLoadTunerMinimal() {
		return categoryLoadTunerMinimal;
	}

	/**
	 * Set the category load tuner.
	 * 
	 * @param categoryLoadTunerMinimal The load tuner
	 */
	public void setCategoryLoadTunerMinimal(final CategoryLoadTuner categoryLoadTunerMinimal) {
		this.categoryLoadTunerMinimal = categoryLoadTunerMinimal;
	}
}