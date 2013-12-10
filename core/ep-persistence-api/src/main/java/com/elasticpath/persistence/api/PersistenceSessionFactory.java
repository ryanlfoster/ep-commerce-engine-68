/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.persistence.api;

import java.util.Map;


/**
 * The persistence session factory interface. This can be use to obtain an application-managed persistence session.
 */
public interface PersistenceSessionFactory {

	/**
	 * Close the factory, releasing any resources that it holds.
	 */
	void close();

	/**
	 * Create a new <code>PersistenceSession</code>. This method returns a new <code>PersistenceSession</code> instance.
	 * @return a new <code>PersistenceSession</code> instance.
	 */
	PersistenceSession createPersistenceSession();

	/**
	 * Return the map of properties used for sessions created by this factory.
	 * @return the properties
	 */
	Map<String, Object> getProperties();

	/**
	 * Set the map of properties to be used for sessions created by this factory.
	 * @param properties the map of properties
	 */
	void setProperties(final Map<String, Object> properties);

	/**
	 * Indicates whether the factory is open.
	 * @return true until a call to close has been made
	 */
	boolean isOpen();
	/**
	 * Get the connection driver name.
	 * @return the connectionDriverName
	 */

	String getConnectionDriverName();

	/**
	 * Set the connection driver name.
	 * @param connectionDriverName the connectionDriverName to set
	 */
	void setConnectionDriverName(final String connectionDriverName);

	/**
	 * Get the connection factory name.
	 * @return the connectionFactoryName
	 */
	String getConnectionFactoryName();

	/**
	 * Set the connection factory name.
	 * @param connectionFactoryName the connectionFactoryName to set
	 */
	void setConnectionFactoryName(final String connectionFactoryName);

	/**
	 * Get the connection password.
	 * @return the connectionPassword
	 */
	String getConnectionPassword();

	/**
	 * Set the connection password.
	 * @param connectionPassword the connectionPassword to set
	 */
	void setConnectionPassword(final String connectionPassword);

	/**
	 * Get the connection URL.
	 * @return the connectionURL
	 */
	String getConnectionURL();

	/**
	 * Set the connection URL.
	 * @param connectionURL the connectionURL to set
	 */
	void setConnectionURL(final String connectionURL);

	/**
	 * Get the connection user name.
	 * @return the connectionUserName
	 */
	String getConnectionUserName();

	/**
	 * Set the connection user name.
	 * @param connectionUserName the connectionUserName to set
	 */
	void setConnectionUserName(final String connectionUserName);

}
