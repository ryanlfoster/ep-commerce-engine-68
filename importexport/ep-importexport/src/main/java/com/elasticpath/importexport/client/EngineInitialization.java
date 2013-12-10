package com.elasticpath.importexport.client;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.importexport.common.exception.runtime.EngineRuntimeException;

/**
 * Singleton representing the initialization of application context. It is responsible for application context initialization, elasticPath
 * initialization and persistence session factory initialization.
 */
@SuppressWarnings("PMD.NonThreadSafeSingleton")
public final class EngineInitialization {

	private static final String APPLICATION_CONTEXT_FILE = "spring/importexport-context.xml";

	private ApplicationContext appCtx;

	private ElasticPath elasticPath;
	
	private Properties applicationProperties;

	private static EngineInitialization instance = null;

	private static final Logger LOG = Logger.getLogger(EngineInitialization.class);

	/**
	 * Private Constructor that initializes appropriate properties.
	 */
	private EngineInitialization() {
		initializeBeanFactory(APPLICATION_CONTEXT_FILE);
	}

	/**
	 * Gets Instance of this class.
	 * 
	 * @return the instance
	 */
	public static EngineInitialization getInstance() {
		if (instance == null) {
			instance = new EngineInitialization();
		}
		return instance;
	}

	/**
	 * Gets elastic path object.
	 * 
	 * @return the elastic path
	 */
	public ElasticPath getElasticPath() {
		return elasticPath;
	}

	private void initializeBeanFactory(final String fileName) {
		LOG.info("Initialize bean factory. ");
		
		
		appCtx = new ClassPathXmlApplicationContext(fileName);

		if (elasticPath == null) {
			elasticPath = (ElasticPath) appCtx.getBean(ContextIdNames.ELASTICPATH);
		}

		applicationProperties = elasticPath.getBean("applicationProperties");
		applicationProperties.put("locale.date.format", "EEE MMM dd HH:mm:ss yyyy");
		applicationProperties.put("datafile.encoding", "UTF-8");
		applicationProperties.put("units.length", "cm");
		applicationProperties.put("units.weight", "kg");

		elasticPath.setWebInfPath(getProjectRootPath());
	}

	private String getProjectRootPath() {
		try {
			File dir = new File(".");
			return dir.getCanonicalPath();
		} catch (IOException e) {
			throw new EngineRuntimeException("Failed to resolve project root path." + e);
		}
	}
}
