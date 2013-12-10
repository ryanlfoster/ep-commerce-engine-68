package com.elasticpath.service.shopper.dao.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shopper.ShopperMemento;
import com.elasticpath.domain.shopper.impl.ShopperMementoImpl;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.shopper.dao.ShopperDao;

/**
 * The wish list dao implementation class.
 */
public class ShopperDaoImpl implements ShopperDao {

	private PersistenceEngine persistenceEngine;

	@Override
	public ShopperMemento get(final long uid) throws EpServiceException {
		return getPersistenceEngine().load(ShopperMementoImpl.class, uid);
	}

	@Override
	public void remove(final ShopperMemento shopperMemento) {
		if (shopperMemento != null) {
			this.getPersistenceEngine().delete(shopperMemento);
		}
	}
	
	@Override
	public void removeIfOrphaned(final ShopperMemento shopperMemento) {
		if (shopperMemento != null) {
			persistenceEngine.executeNamedQuery("REMOVE_SHOPPERS_IF_ORPHANED", shopperMemento.getUidPk());
		}
	}

	@Override
	public ShopperMemento saveOrUpdate(final ShopperMemento shopperMemento) {
		return getPersistenceEngine().saveOrUpdate(shopperMemento);
	}
	
	@Override
	public ShopperMemento findByCustomerAndStoreCode(final Customer customer, final String storeCode) {
        if (customer == null) {
            throw new EpServiceException("customer must be supplied.");
        }
        
        if (StringUtils.isBlank(storeCode)) {
            throw new EpServiceException("storeCode must be supplied.");
        }
        
        final List<ShopperMemento> results = persistenceEngine.<ShopperMemento>retrieveByNamedQuery("FIND_SHOPPER_BY_CUSTOMER_GUID_AND_STORECODE",
				customer.getGuid(), storeCode);
        
        if (results.isEmpty()) {
        	return null;
        }
        return results.get(0);
	}

    @Override
	public int removeNonDependantShoppersByUidList(final List<Long> shopperUids) {
    	 if (shopperUids == null) {
             throw new EpServiceException("value must be supplied.");
         }
         if (shopperUids.isEmpty()) {
         	return 0;
         }
        return this.persistenceEngine.executeNamedQueryWithList("DELETE_NON_DEPENDANT_SHOPPER_BY_UID_LIST", "list" , shopperUids);
    }

    @Override
	public int removeShoppersByUidList(final List<Long> shopperUids) {
        if (shopperUids == null) {
            throw new EpServiceException("value must be supplied.");
        }
        	return this.persistenceEngine.executeNamedQuery("DELETE_SHOPPER_BY_UID_LIST", shopperUids);
    }
    

    @Override
	public List<ShopperMemento> findShoppersOrphanedFromCustomerSessions(final int maxResults) {
    	return persistenceEngine.<ShopperMemento>retrieveByNamedQuery("GET_SHOPPERS_ORPHANED_BY_CUSTOMER_SESSION", 0, maxResults);    	
    }
    
    // Getters / Setters
    // ------------------
    
	/**
	 * Get the persistence Engine.
	 * 
	 * @return the persistence engine
	 */
	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	/**
	 * Set the persistence Engine.
	 * 
	 * @param persistenceEngine the persistence engine
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
	}

	@Override
	public List<Long> findUidsByCustomer(final Customer customer) {
		return persistenceEngine.<Long>retrieveByNamedQuery("FIND_SHOPPER_BY_CUSTOMER_GUID" , customer.getGuid());
	}
}
