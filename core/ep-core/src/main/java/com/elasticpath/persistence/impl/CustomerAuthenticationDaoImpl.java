package com.elasticpath.persistence.impl;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.service.customer.CustomerService;

/**
 * Default implementation of Spring Security's <code>UserDetailsService</code>, to integrate with Spring Security framework for
 * authentication and authorization.
 */
public class CustomerAuthenticationDaoImpl implements UserDetailsService {

	private static final Logger LOG = Logger.getLogger(CustomerAuthenticationDaoImpl.class);

	private PersistenceEngine persistenceEngine;

	private StoreConfig storeConfig;
	
	private CustomerService customerService;
	

	/**
	 * Locates the customer based on the given userName of the <code>Customer</code> and the current userIdMode.
	 * 
	 * @param userName The userName presented to the {@link org.springframework.security.providers.dao.DaoAuthenticationProvider}.
	 * @return A fully populated Customer.
	 * @throws UsernameNotFoundException If the user could not be found or the user has no granted Authority.
	 * @throws DataAccessException If user could not be found for a repository-specific reason.
	 */
	public UserDetails loadUserByUsername(final String userName) throws UsernameNotFoundException, DataAccessException {
		if (userName == null) {
			throw new EpPersistenceException("Cannot retrieve null user Id.");
		}
		Customer result = null;
		
		int userIdMode = getCustomerService().getUserIdMode();
		String storeCode = storeConfig.getStore().getCode();
		
		if (userIdMode == WebConstants.USE_EMAIL_AS_USER_ID_MODE) {
			result = getCustomerService().findByEmail(userName, storeCode);
		} else {
			result = getCustomerService().findByUserId(userName, storeCode);
		}
		
		if (result == null) {
			throw new UsernameNotFoundException("No records with username " + userName);
		}
		
		return result;
	}

	/**
	 * Sets the persistence engine.
	 * 
	 * @param persistenceEngine the persistence engine to set.
	 */
	public void setPersistenceEngine(final PersistenceEngine persistenceEngine) {
		this.persistenceEngine = persistenceEngine;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Persistence engine initialized ... " + persistenceEngine);
		}
	}

	/**
	 * Returns the persistence engine.
	 * 
	 * @return the persistence engine.
	 */
	public PersistenceEngine getPersistenceEngine() {
		return this.persistenceEngine;
	}

	/**
	 * Inject the StoreConfig object.
	 * 
	 * @param storeConfig the StoreConfig object.
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}

	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	protected CustomerService getCustomerService() {
		return customerService;
	}
}
