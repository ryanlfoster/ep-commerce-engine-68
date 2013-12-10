package com.elasticpath.tools.sync.target.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * Category dao adapter.
 */
public class CategoryDaoAdapterImpl extends AbstractDaoAdapter<Category> {
	
	private CategoryService categoryService;
	
	private BeanFactory beanFactory;

	@Override
	public void add(final Category newPersistence) throws SyncToolRuntimeException {
		categoryService.add(newPersistence);
	}

	@Override
	public Category createBean(final Category category) {
		if (category.isLinked()) {
			return beanFactory.getBean(ContextIdNames.LINKED_CATEGORY);
		}
		return beanFactory.getBean(ContextIdNames.CATEGORY);
	}

	@Override
	public Category get(final String guid) {
		try {
			return (Category) getEntityLocator().locatePersistence(guid, Category.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		final Category category = get(guid);
		if (category == null) {
			// TODO: think of error collection to receive a notification about inexisting product here.
			return false;
		}

		if (category.isLinked()) {
			categoryService.removeLinkedCategoryTree(category);
		} else {
			categoryService.removeCategoryTree(category.getUidPk());
		}
		return true;
	}

	@Override
	public Category update(final Category mergedPersistence) throws SyncToolRuntimeException {
		return categoryService.update(mergedPersistence);
	}

	/**
	 * @param categoryService the categoryService to set
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
