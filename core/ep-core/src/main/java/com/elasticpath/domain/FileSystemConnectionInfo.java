/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain;


/**
 * 
 * This class contains the filesystem connection info.
 *
 */
public interface FileSystemConnectionInfo {

    /**
     * Get the protocol to use for the connection.
     * @return the protocol
     */
    String getProtocol();
    
    /**
     * Set the protocol to use for the connection.
     * @param protocol the protocol to use
     */
    void setProtocol(final String protocol);
	
	/**
	 * Get the server host.
	 * @return host
	 */
	String getHost();

	/**
	 * Set the server host.
	 * @param host the server host
	 */
	void setHost(final String host);

	/**
	 * Get the server port.
	 * @return server port
	 */
	Integer getPort();

	/**
	 * Set the server port.
	 * @param port the server port
	 */
	void setPort(final int port);

	/**
	 * Get user password for the connection.
	 * @return the password
	 */
	String getPassword();

	/**
	 * Set user password for the connection.
	 * @param password the password
	 */
	void setPassword(final String password);

	/**
	 * Get the user name for the connection.
	 * @return the user name
	 */
	String getUserName();

	/**
	 * Set the user name for the connection.
	 * @param username the user name
	 */
	void setUserName(final String username);

	/**
	 * Get the root path of the connection.
	 * @return the rootPath
	 */
	String getRootPath();

	/**
	 * Set the root path of the connection.
	 * @param rootPath the rootPath to set
	 */
	void setRootPath(String rootPath);
	

}