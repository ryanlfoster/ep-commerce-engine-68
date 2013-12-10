/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.tools.sync.client;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;

import com.elasticpath.tools.sync.beanfactory.impl.ProxyBeanFactoryImpl;
import com.elasticpath.tools.sync.client.controller.SyncToolController;
import com.elasticpath.tools.sync.client.controller.SyncToolControllerFactory;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;
import com.elasticpath.tools.sync.target.result.Summary;

/**
 * The launcher starts the sync tool process and provides the result summary.
 */
public class SyncToolLauncher {

	private static final Logger LOG = Logger.getLogger(SyncToolLauncher.class);

	/**
	 * The bean name of the sync tool configuration.
	 */
	public static final String SYNC_TOOL_CONFIGURATION = "syncToolConfiguration";
	
	/**
	 * The bean name of the sync tool controller factory.
	 */
	public static final String SYNC_TOOL_CONTROLLER_FACTORY = "syncToolControllerFactory";
	
	/**
	 * The location of the sync tool context Spring XML file. 
	 */
	private static final String SPRING_SYNC_TOOL_XML = "spring/sync-tool-context.xml";

	/**
	 * Launches the sync tool by initializing the application context (using the sync tool context XML file)
	 * and retrieving the controller factory which ot its own creates a controller, used for running the
	 * synchronization process.
	 * 
	 * @param configuration the sync tool configuration
	 * @return the result summary of the sync process
	 * @throws SyncToolConfigurationException if a sync tool configuration exception occurs
	 */
	public Summary launch(final SyncToolConfiguration configuration) throws SyncToolConfigurationException {
		BeanFactory beanFactory = initializeApplicationContext(configuration);
		final SyncToolControllerFactory factory = getSyncControllerFactory(beanFactory);
		SyncToolController controller = factory.createController();
		
		return controller.synchronize();
	}

	/**
	 * Creates an application context (bean factory) from the XML file that holds all the
	 * bean definitions for the sync tool that are only related to the sync tool itself.
	 * In order to pass the sync tool configuration object a {@link ProxyBeanFactoryImpl} is used.
	 * 
	 * @param configuration the sync tool configuration
	 * @return the bean factory
	 */
	protected BeanFactory initializeApplicationContext(final SyncToolConfiguration configuration) {
		ProxyBeanFactoryImpl parentBeanFactory = new ProxyBeanFactoryImpl();
		parentBeanFactory.addProxyBean(SYNC_TOOL_CONFIGURATION, configuration);
		
		final XmlBeanFactory beanFactory = new XmlBeanFactory(
				new ClassPathResource(SPRING_SYNC_TOOL_XML), parentBeanFactory);
		ApplicationContext appContext = new GenericApplicationContext(beanFactory);
		((GenericApplicationContext) appContext).refresh();
		
		LOG.info("Local context has been read and initialized");
		
		return appContext;
	}

	/**
	 * Gets the sync controller factory.
	 * 
	 * @param beanFactory the bean factory to use 
	 * @return a sync controller factory
	 */
	protected SyncToolControllerFactory getSyncControllerFactory(final BeanFactory beanFactory) {
		return (SyncToolControllerFactory) beanFactory.getBean(SYNC_TOOL_CONTROLLER_FACTORY);
	}


}
