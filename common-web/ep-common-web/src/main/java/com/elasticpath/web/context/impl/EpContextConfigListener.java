/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.web.context.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.persistence.PersistenceException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.commons.util.impl.VelocityGeographyHelperImpl;
import com.elasticpath.commons.validator.impl.EpFieldChecks;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.service.ElasticPathService;

/**
 * Bootstrap listener to set up various context config after the springframework context has been loaded.
 * <p>
 * This listener should be registered after ContextLoaderListener in web.xml.
 * </p>
 * <p>
 * For Servlet 2.2 containers and Servlet 2.3 ones that do not initalize listeners before servlets, use ContextLoaderServlet. See the latter's
 * Javadoc for details.
 * </p>
 * This class may be extended to provide custom behaviour; such as in the case of the sf.
 */
public class EpContextConfigListener implements ServletContextListener {
	/** Logger. * */
	protected static final Logger LOG = Logger.getLogger(EpContextConfigListener.class);

	private BeanFactory beanFactory;

	/**
	 * Complete the context initialization for ElasticPath.
	 *
	 * @param event the servlet context event.
	 * @throws EpSystemException in case of any error happens.
	 */
	public void contextInitialized(final ServletContextEvent event) throws EpSystemException {
		try {
			final ServletContext servletContext = event.getServletContext();
			final WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);

			// config the contextBeanFactoryReference and databaseMetaData in
			// ElasticPath
			doElasticPathConfig(webApplicationContext, servletContext);

			servletContext.setAttribute(WebConstants.GEOGRAPHY_HELPER,
					new VelocityGeographyHelperImpl(this.<Geography>getBean(ContextIdNames.GEOGRAPHY)));

			// inject static initializers
			doStaticInjection((BeanFactory) webApplicationContext.getBean("coreBeanFactory"));
		} catch (final Exception e) {
			LOG.error("Caught an exception.", e);
			throw new EpSystemException("Listener initialization failed.", e);
		}
	}

	private void doStaticInjection(final BeanFactory beanFactory) {
		EpFieldChecks.setGeography((Geography) beanFactory.getBean(ContextIdNames.GEOGRAPHY));
	}

	/**
	 * Initialize the ElasticPath object by load the config.
	 *
	 * @param webApplicationContext the web context
	 * @param servletContext the servlet context
	 */
	protected void doElasticPathConfig(final WebApplicationContext webApplicationContext, final ServletContext servletContext) {
		beanFactory = (BeanFactory) webApplicationContext.getBean("coreBeanFactory");
		ElasticPath elasticPath = getBean(ContextIdNames.ELASTICPATH);
		if (elasticPath != null) {

			// set the absolute path to the WEB-INF directory
			String webInfPath = servletContext.getRealPath("") + File.separator + "WEB-INF";
			LOG.info("EPContextConfigListenter - Set WEB-INF path: " + webInfPath);
			elasticPath.setWebInfPath(webInfPath);

			// Initialize the elastic path service
			ElasticPathService elasticPathService = getBean("elasticPathService");
			elasticPathService.init();

			// set the database server name in elasticPath
			getDatabaseMetaData();

			// Load attributes for access from velocity macros.
			servletContext.setAttribute("storefrontContextUrl", elasticPathService.getStorefrontContextUrl());
			servletContext.setAttribute(WebConstants.ELASTICPATH, elasticPath);
		}

	}

	/**
	 * Gets the given bean.
	 * @param <T> generic to ease assignment
	 * @param name the name of the bean
	 * @return the given bean
	 */
	protected <T> T getBean(final String name) {
		return beanFactory.getBean(name);
	}

	@SuppressWarnings("PMD.DoNotThrowExceptionInFinally")
	private String getDatabaseMetaData() {
		DatabaseMetaData databaseMetaData = null;

		Connection connection = null;
		try {
			PersistenceEngine engine = getBean(ContextIdNames.PERSISTENCE_ENGINE);
			connection = engine.getConnection();
			databaseMetaData = connection.getMetaData();

			//Log database meta data for trouble-shooting
			LOG.info("Database name: " + databaseMetaData.getDatabaseProductName());
			LOG.info("Database product version: " + databaseMetaData.getDatabaseProductVersion());
			LOG.info("Driver name: " + databaseMetaData.getDriverName());
			LOG.info("Driver version: " + databaseMetaData.getDriverVersion());
			LOG.info("Driver major version: " + databaseMetaData.getDriverMajorVersion());
			LOG.info("Driver minor version: " + databaseMetaData.getDriverMinorVersion());

			return databaseMetaData.getDatabaseProductName();

		} catch (SQLException e) {
			throw new EpSystemException("Failed in EpContextListener.getDatabaseMetaData", e);
		} catch (PersistenceException e) {
			throw new EpSystemException("Exception in EpContextListener.getDatabaseMetaData", e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				throw new EpSystemException("Cannot close connection.", e);
			}

		}
	}

	/**
	 * Close the root web application context.
	 *
	 * @param event not used
	 */
	public final void contextDestroyed(final ServletContextEvent event) {
		// do nothing
	}

}
