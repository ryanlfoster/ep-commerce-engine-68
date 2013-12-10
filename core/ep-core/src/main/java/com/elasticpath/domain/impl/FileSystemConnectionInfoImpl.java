/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.impl;

import com.elasticpath.domain.FileSystemConnectionInfo;

/**
 * This class contains File system connection info.
 * 
 *
 */
public class FileSystemConnectionInfoImpl implements FileSystemConnectionInfo {

	  private String protocol;
	
	  private String host;

	  private Integer port;

	  private String password;

	  private String userName;
	  
	  private String rootPath;
	
    /**
     * Get the protocol to use for the connection.
     * @return the protocol
     */
    public String getProtocol() {
    	return this.protocol;
    }
    
    /**
     * Set the protocol to use for the connection.
     * @param protocol the protocol to use
     */
    public void setProtocol(final String protocol) {
    	this.protocol = protocol;
    }
	    
	@Override
	public String getHost() {
		return host;
	}

	@Override
	public void setHost(final String host) {
		this.host = host;
	}

	@Override
	public Integer getPort() {
		return port;
	}

	@Override
	public void setPort(final int port) {
		this.port = port;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void setPassword(final String password) {
		this.password = password;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public void setUserName(final String username) {
		this.userName = username;
	}

	@Override
	public String getRootPath() {
		return rootPath;
	}

	@Override
	public void setRootPath(final String rootPath) {
		this.rootPath = rootPath;
	}
}
