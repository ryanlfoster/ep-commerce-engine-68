package com.elasticpath.sfweb.servlet.facade;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.store.Store;

/**
 * Facade class to limit the access to the HttpServletRequest when the request must be passed outside of a filter. <br>
 * Also provides method for making accessing data contained within the request easier.
 */
public interface HttpServletRequestFacade {

	/**
	 * Used to determine if this is a new session.
	 * 
	 * @return true if new session
	 */
	boolean isNewSession();

	/**
	 * Retrieves the CustomerSession.
	 * 
	 * @return the CustomerSession
	 */
	CustomerSession getCustomerSession();

	/**
	 * Sets the CustomerSession.
	 * 
	 * @param customerSession the CustomerSession
	 */
	void setCustomerSession(CustomerSession customerSession);
	
	/**
	 * Retrieves the persisted CustomerSession.
	 * 
	 * @return the persisted CustomerSession
	 */
	CustomerSession getPersistedCustomerSession();

	/**
	 * Gets the servlet path.
	 * 
	 * @return the servlet path
	 */
	String getServletPath();

	/**
	 * Gets the query string.
	 * 
	 * @return the query string
	 */
	String getQueryString();

	/**
	 * Gets the Request URL.
	 * 
	 * @return the Request URL
	 */
	StringBuffer getRequestURL();

	/**
	 * Gets the Header value.
	 * 
	 * @param name the header name
	 * @return the header value
	 */
	String getHeader(String name);

	/**
	 * Gets the Parameter or Attribute value from the request.
	 * 
	 * @param name the name of the Parameter or Attribute
	 * @param defaultValue the value to return if the Parameter or Attribute cannot be found
	 * @return either the found or default value
	 */
	String getParameterOrAttributeValue(String name, String defaultValue);

  	/**
 	 * Gets the given parameter value from the request.  Returns null if the parameter was not found.
 	 *
 	 * @param name The name of the parameter
 	 * @return The parameter's value, or null if the parameter is not on the request
 	 */
 	String getParameterValue(final String name);	
	
	/**
	 * Gets the remote address from the request.
	 * 
	 * @return the remote address
	 */
	String getRemoteAddress();

	/**
	 * Gets the Store from the RequestHelper.
	 * 
	 * @return the Store
	 */
	Store getStore();

	/**
	 * Gets the StoreCode from the RequestHelper.
	 * 
	 * @return the Store
	 */
	String getStoreCode();
}
