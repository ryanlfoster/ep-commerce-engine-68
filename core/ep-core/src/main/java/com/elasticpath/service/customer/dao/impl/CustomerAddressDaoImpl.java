/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.service.customer.dao.impl;

import java.util.List;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.persistence.dao.impl.AbstractDaoImpl;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.customer.dao.CustomerAddressDao;

/**
 * Implementation of CustomerAddressDao.
 */
public class CustomerAddressDaoImpl extends AbstractDaoImpl implements CustomerAddressDao {
	
	@Override
	public Address findByGuid(final String guid) {
		List<CustomerAddress> result = getPersistenceEngine().retrieveByNamedQuery("CUSTOMER_ADDRESS_FIND_BY_GUID", guid);
		if (result.isEmpty()) {
			return null;
		} else if (result.size() == 1) {
			return result.get(0);
		}
		throw new EpServiceException("Inconsistent data -- duplicate GUIDs exist -- " + guid);
	}
	
	@Override
	public Address get(final long addressUid) throws EpServiceException {
		if (addressUid <= 0) {
			return getBean(ContextIdNames.CUSTOMER_ADDRESS);
		}
		return getPersistentBeanFinder().get(ContextIdNames.CUSTOMER_ADDRESS, addressUid);
	}

	@Override
	public void remove(final Address customerAddress) {
		getPersistenceEngine().delete(customerAddress);
	}
	
	@Override
	public Address saveOrUpdate(final Address customerAddress) {
		return getPersistenceEngine().saveOrUpdate(customerAddress);
	}
	
}
