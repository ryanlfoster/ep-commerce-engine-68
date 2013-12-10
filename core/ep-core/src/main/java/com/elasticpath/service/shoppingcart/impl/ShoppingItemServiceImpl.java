/**
 * 
 */
package com.elasticpath.service.shoppingcart.impl;

import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.LoadTuner;
import com.elasticpath.persistence.dao.ShoppingItemDao;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.shoppingcart.ShoppingItemService;

/**
 * Provides services for persisting {@code ShoppingItem}s. Delegates
 * persistence-level operations to a DAO.
 */
public class ShoppingItemServiceImpl implements ShoppingItemService {

	private ShoppingItemDao dao;
	
	@Override
	public ShoppingItem findByGuid(final String guid, final LoadTuner loadTuner) throws EpServiceException {
		try {
			return getDao().findByGuid(guid, loadTuner);
		} catch (EpPersistenceException ex) {
			throw new EpServiceException("Unable to get the descriptor with GUID " + guid, ex);
		}
	}

	@Override
	public ShoppingItem saveOrUpdate(final ShoppingItem shoppingItem) throws EpServiceException {
		try {
			return getDao().saveOrUpdate(shoppingItem);
		} catch (EpPersistenceException ex) {
			throw new EpServiceException("Unable to save or update the given ShoppingItem.", ex);
		}
	}

	/**
	 * @param dao the dao to set
	 */
	public void setDao(final ShoppingItemDao dao) {
		this.dao = dao;
	}

	/**
	 * @return the dao
	 */
	public ShoppingItemDao getDao() {
		return dao;
	}

}
