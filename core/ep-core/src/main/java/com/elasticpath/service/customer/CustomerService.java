/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.customer;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.elasticpath.commons.exception.EmailExistException;
import com.elasticpath.commons.exception.UserIdExistException;
import com.elasticpath.commons.exception.UserIdNonExistException;
import com.elasticpath.commons.exception.UserStatusInactiveException;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.service.EpPersistenceService;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.auth.UserIdentityService;

/**
 * Provide customer-related business service.
 *
 * @ws.attribute service="ContextIdNames.CUSTOMER_SERVICE"
 */
@SuppressWarnings("PMD.TooManyMethods")
public interface CustomerService extends EpPersistenceService {

	/**
	 * Adds the given customer.
	 *
	 * @param customer the customer to add
	 * @return the persisted instance of customer
	 * @throws UserIdExistException - if trying to add an customer using an existing email address.
	 *
	 * @ws.property
	 * @ws.param ori-type="com.elasticpath.domain.customer.Customer"
	 * 	ori-type-bean="ContextIdNames.CUSTOMER"
	 *  ws-type="com.elasticpath.connect.domain.customer.CustomerWsImpl"
	 * @ws.return ori-type="com.elasticpath.domain.customer.Customer"
	 * 	ws-type="com.elasticpath.connect.domain.customer.CustomerWsImpl"
	 */
	Customer add(Customer customer) throws UserIdExistException;

	/**
	 * Updates the given customer.
	 *
	 * @param customer the customer to update
	 * @return the new persisted instance of customer object
	 * @throws UserIdExistException - if trying to add an customer using an existing user Id.
	 */
	Customer update(Customer customer) throws UserIdExistException;

	/**
	 * Delete the customer.
	 *
	 * @param customer the customer to remove
	 * @throws EpServiceException - in case of any errors
	 */
	void remove(Customer customer) throws EpServiceException;

	/**
	 * Check the given email exists or not.
	 *
	 * @param email the email address
	 * @param storeCode the code of the store to check
	 * @return true if the given email exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean isUserIdExists(String email, String storeCode) throws EpServiceException;

	/**
	 * List all customers stored in the database.
	 *
	 * @return a list of customers
	 * @throws EpServiceException - in case of any errors
	 */
	List <Customer> list() throws EpServiceException;

	/**
	 * Load the customer with the given UID. Throw an unrecoverable exception if there is no matching database row.
	 *
	 * @param customerUid the customer UID
	 * @return the customer if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Customer load(long customerUid) throws EpServiceException;

	/**
	 * Get the customer with the given UID. Return null if no matching record exists.
	 *
	 * @param customerUid the customer UID
	 * @return the customer if UID exists, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	Customer get(long customerUid) throws EpServiceException;
	
	/**
	 * Get the customer with the given UID. Return <code>null</code>l if no matching record
	 * exists.
	 * 
	 * @param customerUid the customer UID
	 * @param loadTuner the load tuner to tune the results, or <code>null</code> for the default
	 *            tuner
	 * @return the customer if UID exists, otherwise <code>null</code>
	 * @throws EpServiceException - in case of any errors
	 */
	Customer get(long customerUid, FetchGroupLoadTuner loadTuner) throws EpServiceException;

	/**
	 * Retrieve the customer with the given guid.
	 *
 	 * @param guid the guid of the customer
	 * @return the customer with the given guid
	 * @throws EpServiceException in case of any error
	 */
	Customer findByGuid(String guid) throws EpServiceException;

	
	/**
	 * Retrieve the customer with the given guid, with an optional load tuner.
	 * 
	 * @param guid the guid of the customer
	 * @param loadTuner the load tuner to tune the results, or <code>null</code> for the default tuner
	 * @return the customer with the given guid
	 * @throws EpServiceException in case of any error
	 */
	Customer findByGuid(String guid, FetchGroupLoadTuner loadTuner) throws EpServiceException;

	
	/**
	 * Find the customer with the given email address in the store.
	 * If it cannot find the customer in the given store, also look within the store's associated stores.
	 *
	 * @param email the customer email address
	 * @param storeCode the store to look in.
	 * @return the customer with the given email address.
	 * @throws EpServiceException - in case of any errors
	 */
	Customer findByEmail(String email, String storeCode) throws EpServiceException;

	/**
	 * Resets the customer's password for the specified email.
	 *
	 * @param userId The user Id of the customer whose password is to be reset
	 * @param storeCode of the store for this customer
	 * @throws UserIdNonExistException if the user Id isn't found
	 */
	void resetPassword(String userId, String storeCode) throws UserIdNonExistException;

	/**
	 * Resets the given customer's password.
	 *
	 * @param customer The given customer.
	 * @return the updated instance of the customer
	 * @see Customer
	 */
	Customer auditableResetPassword(Customer customer);

	/**
	 * Changes the password for the specified customer, and sends the confirmation email.
	 * 
	 * @param customer the customer whose password is to be changed
	 * @param newPassword the new password (clear text)
	 * @return the updated instance of the customer
	 * @see Customer
	 */
	Customer changePasswordAndSendEmail(Customer customer, String newPassword);
	
	/**
	 * Changes the password for the specified customer.
	 * 
	 * @param customer the customer whose password is to be changed
	 * @param newPassword the new password (clear text)
	 * @return the updated instance of the customer
	 * @see Customer
	 */
	Customer setPassword(Customer customer, String newPassword);

	/**
	 * Creates a password and sends it to customer.
	 *
	 * @param customer the customer whose password is to be sent
	 * @return the updated Customer
	 */
	Customer registerCustomerAndSendPassword(Customer customer);

	/**
	 * Retrieve the list of customers, whose specified property contain the given criteria value.
	 *
	 * @param propertyName customer property to search on.
	 * @param criteriaValue criteria value to be used for searching.
	 * @return list of customers matching the given criteria.
	 */
	List <Customer> findCustomerLike(String propertyName, String criteriaValue);

	/**
	 * Returns a list of <code>Customer</code> based on the given uids. The returned customers will be populated based on the given load tuner.
	 *
	 * @param customerUids a collection of customer uids
	 * @return a list of <code>Customer</code>s
	 */
	List<Customer> findByUids(Collection<Long> customerUids);

	/**
	 * Validate the new customer has the valid email address (not used by any existing non-anonymous customer).
	 * @param customer the nre customer.
	 * @throws EmailExistException - if the new customer's email address already exists in system.
	 */
	void validateNewCustomer(Customer customer) throws EmailExistException;

	/**
	 * Returns all customer uids as a list.
	 *
	 * @return all customer uids as a list
	 */
	List<Long> findAllUids();

	/**
	 * Removes uids which correspond to customers that are not searchable.
	 * @param uids the uids to filter
	 * @return the uids of searchable customers
	 */
	Collection<Long> filterSearchable(Collection<Long> uids);

	/**
	 * Retrieves list of <code>Customer</code> uids where the last modified date is later than the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>Customer</code> whose last modified date is later than the specified date
	 */
	List<Long> findUidsByModifiedDate(Date date);

	/**
	 * Retrieves list of customer uids where the deleted date is later than the specified date.
	 *
	 * @param date date to compare with the deleted date
	 * @return list of customer uids whose deleted date is later than the specified date
	 */
	List<Long> findUidsByDeletedDate(Date date);

	/**
	 * Adds a customer to the default customer group (ensuring that they have the default role).
	 * @param customer the customer upon which to set the default group
	 * @throws EpServiceException in case of any errors.
	 */
	void setCustomerDefaultGroup(Customer customer);

	/**
	 * Checks the given email exists or not.
	 *
	 * @param email the user Id
	 * @param storeCode the store to look in
	 * @return true if the given user Id exists
	 * @throws EpServiceException - in case of any errors
	 */
	boolean isEmailExists(String email, String storeCode) throws EpServiceException;

	/**
	 * Find the customer with the given userId address.
	 * If it cannot find the customer in the given store, also look within the store's associated stores.
	 *
	 * @param userId the customer userId address
	 * @param storeCode the store to search in
	 * @return the customers with the given userId address.
	 * @throws EpServiceException - in case of any errors
	 */
	Customer findByUserId(String userId, String storeCode) throws EpServiceException;

	/**
	 * Set the userIdentityService instance.
	 *
	 * @param userIdentityService the userIdentityService instance.
	 */
	void setUserIdentityService(UserIdentityService userIdentityService);

    /**
     * Adds the given customer.
     *
     * @param customer the customer to add
     * @param isAuthenticated true if the Customer is already authenticated via external source
     * @return the persisted instance of customer
     * @throws UserIdExistException - if trying to add an customer using an existing email address.
     */
    Customer addByAuthenticate(Customer customer, boolean isAuthenticated) throws UserIdExistException;

	/**
	 * See CustomerAddressDao for details.
	 * 
	 * @param addressUid See above.
	 * @return See above.
	 * @see com.elasticpath.service.customer.dao.impl.CustomerAddressDao
	 */
	CustomerAddress getCustomerAddress(long addressUid);

	/**
	 * Verifies the customer status.
	 *
	 * @param customer the customer need to be verified.
	 * @throws UserStatusInactiveException if the customer is inactive
	 */
	void verifyCustomer(Customer customer) throws UserStatusInactiveException;
	
	/**
	 * Retrieves customer status by customer uidpk.
	 *
	 * @param customerUid long
	 * @return customer status
	 */
	int findCustomerStatusByUid(long customerUid);
	
	/**
	 * Adds or updates the given address.
	 * If the address passed in has a uidPk of 0 then it will be added to the customer's address list,
	 * otherwise the address will be retrieved from the customer's address list and the information copied over from the given address.
	 * If the address already exists then its preferred status is not changed (for example, preferred billing or preferred shipping status).
	 * 
	 * @param customer The Customer to whom the address belongs.
	 * @param address The address to add or update.
	 * @return The updated instance of the given Customer.
	 */
	Customer addOrUpdateAddress(Customer customer, CustomerAddress address);
	
	/**
	 * Adds or updates the customer's billing address. If the address passed in has a uidPk of 0 then it will be added to the customer's address
	 * list, otherwise the corresponding address will be retrieved from the customer's address list and the information copied over from the passed
	 * in address. Finally, this address will be set as the customer's preferred billing address, the changes saved, and the updated customer
	 * instance returned.
	 * 
	 * @param customer the customer to add or update the address on
	 * @param address the address being updated or added
	 * @return the updated instance of the customer
	 */
	Customer addOrUpdateCustomerBillingAddress(Customer customer, CustomerAddress address);

	/**
	 * Adds or updates the customer's shipping address. If the address passed in has a uidPk of 0 then it will be added to the customer's address
	 * list, otherwise the corresponding address will be retrieved from the customer's address list and the information copied over from the passed
	 * in address. Finally, this address will be set as the customer's preferred shipping address, the changes saved, and the updated customer
	 * instance returned.
	 * 
	 * @param customer the customer to add or update the address on
	 * @param address the address being updated or added
	 * @return the updated instance of the customer
	 */
	Customer addOrUpdateCustomerShippingAddress(Customer customer, CustomerAddress address);

	/**
	 * Find the customer with the given email address. Filtered by Store. If store is null or
	 * store is shared login, no filtering is done.
	 *
	 * @param email the customer email address
	 * @param storeCode the store to look in
	 * @param includeAnonymous if true includes in the search the anonymous users
	 * @return the customers with the given email address.
	 * @throws EpServiceException - in case of any errors
	 */
	Customer findByEmail(String email, String storeCode, boolean includeAnonymous) throws EpServiceException;

	/**
	 * Find the customer with the given userId registered with the store.
	 * If it cannot find the customer in the given store, returns oldest record from the store's associated stores.
	 *
	 * @param userId the customer userId address
	 * @param storeCode the store to search in
	 * @param includeAnonymous includes anonymous users
	 * @return the customers with the given userId address.
	 * @throws EpServiceException - in case of any errors
	 */
	Customer findByUserId(String userId, String storeCode, boolean includeAnonymous) throws EpServiceException;

	/**
	 * Return the mode to generate user Id. 1 - Use user email as user Id, this is default value. 2 - Generate unique premanent user Id, currently
	 * will append a random four digit suffix to email address, and use it as User Id. The user Id is created when the customer is created first
	 * time. Later on, when the customer change the email address, the user Id will not be changed. 3 - Independent email and user Id
	 * 
	 * @return the flag to indicate how to generate user Id
	 */
	int getUserIdMode();

	/**
	 * Gets the customer last modified date given the customer GUID.
	 *
	 * @param customerGuid the customer GUID
	 * @return the customer last modified date
	 */
	Date getCustomerLastModifiedDate(String customerGuid);
	
}
