/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.service.customer.dao;

import com.elasticpath.domain.customer.Address;

/**
 * This interface is a DAO for <code>CustomerAddress</code>es.
 */
public interface CustomerAddressDao {
	
	/**
	 * @param guid The CustomerAddress GUID.
	 * @return The CustomerAddress.
	 */
	Address findByGuid(String guid);
	
	/**
	 * If the given addressUid <= 0 then an empty CustomerAddress is returned.
	 * Otherwise the CustomerAddress is returned, or null if it doesn't exist.
	 * 
	 * @param addressUid The CustomerAddress UidPk.
	 * @return As above.
	 */
	Address get(long addressUid);

	/**
	 * @param customerAddress The customerAddress to remove.
	 */
	void remove(Address customerAddress);
	
	/**
	 * @param customerAddress The CustomerAddress to save or update.
	 * @return The saved/updated CustomerAddress.
	 */
	Address saveOrUpdate(Address customerAddress);
}
