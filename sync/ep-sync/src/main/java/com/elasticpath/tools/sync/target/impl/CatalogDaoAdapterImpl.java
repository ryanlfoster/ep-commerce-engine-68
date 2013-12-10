package com.elasticpath.tools.sync.target.impl;

import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.exception.SyncToolRuntimeException;

/**
 * Catalog dao adapter.
 */

// ---- DOCCatalogDaoAdapterImpl
public class CatalogDaoAdapterImpl extends AbstractDaoAdapter<Catalog> {
	
	private static final Logger LOG = Logger.getLogger(CatalogDaoAdapterImpl.class);

	private CatalogService catalogService;
	
	private BeanFactory beanFactory;

	@Override
	public void add(final Catalog newPersistence) throws SyncToolRuntimeException {
		catalogService.saveOrUpdate(newPersistence);		
	}

	@Override
	public Catalog createBean(final Catalog catalog) {
		return beanFactory.getBean(ContextIdNames.CATALOG);
	}

	@Override
	public Catalog get(final String guid) {
		try {
			return (Catalog) getEntityLocator().locatePersistence(guid, Catalog.class);
		} catch (SyncToolConfigurationException e) {
			throw new SyncToolRuntimeException("Unable to locate persistence", e);
		}
	}

	@Override
	public boolean remove(final String guid) throws SyncToolRuntimeException {
		final Catalog catalog = get(guid);
		if (catalog == null) {
			// TODO: think of error collection to receive a notification about inexisting product here.
			LOG.warn("Attempt to remove unknown Catalog with code: " + guid);
			return false;
		}
		catalogService.remove(catalog);
		return true;
	}

	@Override
	public Catalog update(final Catalog mergedPersistence) throws SyncToolRuntimeException {
		return catalogService.saveOrUpdate(mergedPersistence);
	}

	/**
	 * @param catalogService the catalogService to set
	 */
	public void setCatalogService(final CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
// ---- DOCCatalogDaoAdapterImpl
