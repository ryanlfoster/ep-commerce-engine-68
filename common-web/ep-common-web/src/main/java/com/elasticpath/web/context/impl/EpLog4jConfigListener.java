/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.web.context.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.RootLogger;
import org.springframework.web.util.Log4jWebConfigurer;
import org.springframework.web.util.WebUtils;

import com.elasticpath.base.exception.EpSystemException;

/**
 * <code>EpLog4jConfigListener</code> is the bootstrap listener for custom Log4J initialization in a web environment. <b>WARNING: Assumes an
 * expanded WAR file</b>, both for loading the configuration file and for writing the log files.
 * <p>
 * This listener will expect and context parameter "log4jConfigLocation" for lo4j.properties file, relative the application root, i.e.
 * "WEB-INF/conf/misc/log4j.properties". For any log file location specification inside log4j.properties, please make sure it is relative the
 * application root as well.
 * <p>
 * This listener should be registered before ContextLoaderListener in web.xml, when using custom Log4J initialization.
 * <p>
 * For Servlet 2.2 containers and Servlet 2.3 ones that do not initalize listeners before servlets, use Log4jConfigServlet. See the
 * ContextLoaderServlet javadoc for details.
 */
public class EpLog4jConfigListener implements ServletContextListener {
	/** Parameter specifying the location of the Log4J config file. */
	public static final String CONFIG_LOCATION_PARAM = "log4jConfigLocation";

	/**
	 * The web application root indicator string used in log4j.properties.
	 */
	public static final String WEB_APP_ROOT_INDICATOR = "webAppRoot:";
	
	/**
	 * The jndi lookup for the logging context as defined in web.xml.
	 */
	public static final String JNDI_CONTEXT = "java:comp/env/logging-context";
	
	/**
	 * Log4j repository selector implementation. 
	 */
	public static final RepoSelector REPO_SELECT = new EpLog4jConfigListener.RepoSelector();

	/**
	 * Configure log4j.
	 *
	 * @param event the servlet context event
	 */
	@SuppressWarnings("PMD.DoNotThrowExceptionInFinally")
	public void contextInitialized(final ServletContextEvent event) {
		final ServletContext servletContext = event.getServletContext();
		String location = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
		if (location != null) {
			InputStream inputStream = null;
			try {
				// get the absolute path for log4j configuration file
				location = WebUtils.getRealPath(servletContext, location);
				// Initialize without refresh check, i.e. without Log4J's
				// watchdog thread.
				final Properties log4jProperties = new Properties();
				inputStream = new FileInputStream(location);
				log4jProperties.load(inputStream);
				// update the log file location configuration to be absolute
				// path.
				updateLogLocation(log4jProperties, servletContext);
				// Write log message to server log.
				servletContext.log("Initializing Log4J from [" + location + "]");

				// Configure the root logger repository with our logging properties
				PropertyConfigurator.configure(log4jProperties);
				
				// Guard object needs to be sharable among wars in the same ear, or kept null 
				LogManager.setRepositorySelector(REPO_SELECT, null);
				
				// Now configure the context logger repository with our logging properties
				PropertyConfigurator.configure(log4jProperties);

			} catch (final FileNotFoundException ex) {
				throw new IllegalArgumentException("Invalid 'log4jConfigLocation' parameter: " + ex.getMessage()); // NOPMD
			} catch (final IOException ioe) {
				throw new EpSystemException("Failed to load the log4j configuration file:" + ioe.getMessage()); // NOPMD
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (final IOException e) {
						throw new EpSystemException("Cannot close the input stream.", e);
					}
				}
			}
		}
	}

	/**
	 * Shutdown log4j.
	 *
	 * @param event the servlet context event
	 */
	public void contextDestroyed(final ServletContextEvent event) {
		Log4jWebConfigurer.shutdownLogging(event.getServletContext());
	}

	private Properties updateLogLocation(final Properties log4jProperties, final ServletContext servletContext) {
		final Enumeration<?> keyEnum = log4jProperties.keys();
		try {
			while (keyEnum.hasMoreElements()) {
				final String curKey = (String) keyEnum.nextElement();
				final String value = log4jProperties.getProperty(curKey);
				if (value.startsWith(WEB_APP_ROOT_INDICATOR)) {
					log4jProperties.setProperty(curKey, WebUtils.getRealPath(servletContext, value.replaceFirst(WEB_APP_ROOT_INDICATOR, "")));
				}
			}
		} catch (final FileNotFoundException ex) {
			throw new EpSystemException("Invalid log4j configuration of log file location: " + ex.getMessage()); // NOPMD
		}
		return log4jProperties;
	}
	
	
	
	
	/**
	 * Implementation of Log4j's RepositorySelector. 
	 * Returns new repository with hierarchy name equal to JNDI value set in webapp's web.xml.  
	 */
	static class RepoSelector implements RepositorySelector {
		  // key: current thread's ContextClassLoader, 
		  // value: Hierarchy instance
		  private final Hashtable<String, Hierarchy> loggerHashTable;
		  private final Hierarchy defaultHierarchy;
		  
		  /**
		   * Constructor.
		   */
		  public RepoSelector() {
			  loggerHashTable = new Hashtable<String, Hierarchy>();
			  defaultHierarchy = new Hierarchy(new RootLogger(Level.DEBUG));
		  }
		
		
		  /**
		   * The returned value is guaranteed to be non-null.
		   * 
		   * @return instance of a LoggerRepository 
		   */
		  public LoggerRepository getLoggerRepository() {
		    String loggingContextName = null;    
		
		    try {
		      Context ctx = new InitialContext();      
		      loggingContextName = (String) ctx.lookup(JNDI_CONTEXT);
		    } catch (NamingException ne) { //NOPMD      
		      // we can't log here
		    } 
		
		    //Maintain and retrieve from hashTable cache
		    if (loggingContextName != null) {
		      Hierarchy hierarchy = loggerHashTable.get(loggingContextName);
		      if (hierarchy == null) {
		        hierarchy = new Hierarchy(new RootLogger(Level.DEBUG));
		        loggerHashTable.put(loggingContextName, hierarchy);
		      }
		      return hierarchy;
		    } 
		    return defaultHierarchy;
		  }
	}

}