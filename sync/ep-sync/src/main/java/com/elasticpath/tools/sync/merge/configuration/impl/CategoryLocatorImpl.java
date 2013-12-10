package com.elasticpath.tools.sync.merge.configuration.impl;

import org.apache.log4j.Logger;

import com.elasticpath.domain.catalog.Category;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 *
 * The category locator class.
 *
 */
public class CategoryLocatorImpl extends AbstractEntityLocator {

	private static final Logger LOG = Logger.getLogger(CategoryLocatorImpl.class);

	private CategoryService categoryService;
	private FetchGroupLoadTuner categorySortLoadTuner;

	/**
	 * @param categoryService the categoryService to set
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	@Override
	public boolean isResponsibleFor(final Class< ? > clazz) {
		return Category.class.isAssignableFrom(clazz);
	}

	@Override
	public Persistable locatePersistence(final String guid, final Class< ? > clazz)
			throws SyncToolConfigurationException {

		Persistable category = null;
		final String categoryGuid = guid.substring(0, guid.indexOf(GUID_SEPARATOR));
		final String catalogGuid = guid.substring(guid.indexOf(GUID_SEPARATOR) + GUID_SEPARATOR.length());

		try {
			category = categoryService.findByGuid(categoryGuid, catalogGuid);
		} catch (final EpServiceException e) {
			LOG.warn("Can't find category for GUID: [" + categoryGuid + "], Catalog GUID: [" + catalogGuid + "].", e);
		}
		return category;
	}

	@Override
    public boolean entityExists(final String guid, final Class<?> clazz) {
        return categoryService.categoryExistsWithCompoundGuid(guid);
    }

	@Override
	public Persistable locatePersistenceForSorting(final String guid, final Class<?> clazz) throws SyncToolConfigurationException {
		try {
			return categoryService.findByCompoundGuid(guid, categorySortLoadTuner);
		} catch (final EpServiceException e) {
			LOG.warn("Can't find category for compound GUID: [" + guid + "]", e);
		}
		return null;
	}

	/**
	 * Sets the category sort load tuner.
	 *
	 * @param categorySortLoadTuner the new sort load tuner
	 */
	public void setCategorySortLoadTuner(final FetchGroupLoadTuner categorySortLoadTuner) {
		this.categorySortLoadTuner = categorySortLoadTuner;
	}

    @Override
    public Persistable locatePersistentReference(final String guid, final Class<?> clazz) throws SyncToolConfigurationException {
        try {
            return categoryService.findByCompoundGuid(guid, getEmptyFetchGroupLoadTuner());
        } catch (final EpServiceException e) {
            LOG.warn("Can't find category for compound GUID: [" + guid + "]", e);
        }
        return null;
    }

}
