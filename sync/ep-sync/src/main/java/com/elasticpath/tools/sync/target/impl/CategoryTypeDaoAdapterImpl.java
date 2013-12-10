package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.service.catalog.CategoryTypeService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * Dao adapter for category type.
 */
public class CategoryTypeDaoAdapterImpl extends AbstractDaoAdapter<CategoryType> {
	
	private CategoryTypeService categoryTypeService;

	private BeanFactory beanFactory;

	@Override
	public void add(final CategoryType newPersistence) throws SyncToolRuntimeException {
		categoryTypeService.add(newPersistence);
	}

	@Override
	public CategoryType createBean(final CategoryType category) {
		return beanFactory.getBean(ContextIdNames.CATEGORY_TYPE);
	}

	@Override
	public CategoryType get(final String guid) {
		try {
			return (CategoryType) getEntityLocator().locatePersistence(guid, CategoryType.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		final CategoryType categoryType = get(guid);
		if (categoryType == null) {
			return false;
		}
		categoryTypeService.remove(categoryType);
		return true;
	}

	@Override
	public CategoryType update(final CategoryType mergedPersistence) throws SyncToolRuntimeException {
		return categoryTypeService.update(mergedPersistence);
	}

	/**
	 * Sets the category type service.
	 * @param categoryTypeService the service
	 */
	public void setCategoryTypeService(final CategoryTypeService categoryTypeService) {
		this.categoryTypeService = categoryTypeService;
	}

	/**
	 * Sets the bean factory.
	 * @param beanFactory the bean factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
