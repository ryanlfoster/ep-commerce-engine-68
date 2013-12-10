package com.elasticpath.sfweb.service;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.sfweb.servlet.facade.HttpServletRequestResponseFacade;
import com.elasticpath.sfweb.servlet.listeners.NewHttpSessionEventListener;


/**
 * <code>WebCustomerSessionService</code> provides services for managing <code>CustomerSession</code>s in
 * the web application session, including cookie management.
 */
public interface WebCustomerSessionService {

	/**
	 * Handle a request intercepted by a filter.
	 * Performs any required actions to update the CustomerSession, shopping cart, and cookie.
	 * @param requestResponse the request
	 * @return the customer session
	 */
	CustomerSession handleFilterRequest(final HttpServletRequestResponseFacade requestResponse);
	
	/**
	 * Updates the session and cookie when a new account is created.
	 * @param requestResponse the HTTP Request
	 * @param customer the new customer
	 */
	void handleCreateNewAccount(final HttpServletRequestResponseFacade requestResponse, final Customer customer);
	
	/**
	 * Update the session when a customer signs in.
	 * @param requestResponse the HTTP request
	 * @param customer the customer who has signed in
	 */
	void handleCustomerSignIn(final HttpServletRequestResponseFacade requestResponse, final Customer customer);

	/**
	 * Update the session when a guest signs in.
	 * @param requestResponse the HTTP request
	 * @param customer the guest customer that has signed in
	 */
	void handleGuestSignIn(final HttpServletRequestResponseFacade requestResponse, final Customer customer);
	
	/**
	 * Adds an listener to the creation of new HTTP sessions.
	 * @param newHttpSessionEventListener the listener to add
	 */
	void addNewHttpSessionEventListener(final NewHttpSessionEventListener newHttpSessionEventListener);
	
	/**
	 * Removes a listener to the creation of new HTTP sessions.
	 * @param newHttpSessionEventListener the listener to remove
	 * @return true if the collection contained the specified listener
	 */
	boolean removeNewHttpSessionEventListener(final NewHttpSessionEventListener newHttpSessionEventListener);
	
}
