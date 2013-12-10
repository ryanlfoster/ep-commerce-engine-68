package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.CategoryTypeService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * 
 * The category type locator class.
 *
 */
public class CategoryTypeLocatorImpl extends AbstractEntityLocator {
	
	private CategoryTypeService categoryTypeService;

	/**
	 * @param categoryTypeService the categoryTypeService to set
	 */
	public void setCategoryTypeService(final CategoryTypeService categoryTypeService) {
		this.categoryTypeService = categoryTypeService;
	}
	
	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return CategoryType.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz)
			throws SyncToolConfigurationException {
		return categoryTypeService.findByGuid(guid);
	}


}
