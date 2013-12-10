/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.beanfactory.impl;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.FileSystemResource;

import com.elasticpath.tools.sync.beanfactory.ContextInitializer;
import com.elasticpath.tools.sync.beanfactory.remote.AuthenticationServiceImpl;
import com.elasticpath.tools.sync.beanfactory.remote.HttpConnectionAuthentication;
import com.elasticpath.tools.sync.configuration.ConnectionConfiguration;

/**
 * A remote context initializer which involves login to the remote system 
 * and application context initialization.
 */
public class RemoteContextInitializerImpl implements ContextInitializer {
	private static final transient Logger LOG = Logger.getLogger(RemoteContextInitializerImpl.class);
	
	private static final String BEAN_NAME_HTTP_CONNECTION_AUTHENTICATION = "httpConnectionAuthentication";
	
	private String pathToXmlFile;
	
	/**
	 * Initializes the remote context by logging into the remote system 
	 * and creating the bean factory for that context.
	 * 
	 * @param connectionConfiguration the connection configration
	 * @return the bean factory for the remote context
	 */
	public BeanFactory initializeContext(final ConnectionConfiguration connectionConfiguration) {
		// login into the remote system
		AuthenticationServiceImpl instance = AuthenticationServiceImpl.getInstance();
		Authentication currentAuth = instance.login(connectionConfiguration.getLogin(), 
				connectionConfiguration.getPwd(), 
				connectionConfiguration.getUrl());
		// create the authentication information bean
		HttpConnectionAuthentication authentication = new HttpConnectionAuthentication();
		authentication.setConnectionUrl(connectionConfiguration.getUrl());
		authentication.setAuthentication(currentAuth);

		ProxyBeanFactoryImpl parentBeanFactory = new ProxyBeanFactoryImpl();
		parentBeanFactory.addProxyBean(BEAN_NAME_HTTP_CONNECTION_AUTHENTICATION, authentication);
		
		// create the bean factory
		XmlBeanFactory beanFactory = new XmlBeanFactory(new FileSystemResource(getPathToXmlFile()), parentBeanFactory);
		GenericApplicationContext applicationContext = new GenericApplicationContext(beanFactory);
		applicationContext.refresh();
		return applicationContext;
	}

	/**
	 *
	 * @return the pathToXmlFile
	 */
	public String getPathToXmlFile() {
		return pathToXmlFile;
	}

	/**
	 *
	 * @param pathToXmlFile the path to the XML file
	 */
	public void setPathToXmlFile(final String pathToXmlFile) {
		this.pathToXmlFile = pathToXmlFile;
	}

	/**
	 * Close the spring context on destroy.
	 *
	 * @param beanFactory the bean factory
	 */
	@Override
	public void destroyContext(final BeanFactory beanFactory) {
		LOG.info("Closing remote bean application factory");
		((ConfigurableApplicationContext) beanFactory).close();
	}

}
