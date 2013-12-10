package com.elasticpath.persistence.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.persistence.dao.PropertyLoaderAware;

/**
 * Factory DAO for creating a properties backed domain object.
 * 
 * @param <T> type of {@link PropertyLoaderAware}
 */
public class PropertiesDaoLoaderFactoryImpl<T extends PropertyLoaderAware> implements FactoryBean<T>, InitializingBean, ApplicationContextAware {

	private static final Logger LOG = Logger.getLogger(PropertiesDaoLoaderFactoryImpl.class);
	private final Properties loadedProperties = new Properties();
	private ResourcePatternResolver resourceLoader;
	private Class<T> objectType;
	private boolean singleton;
	private T lastObject;
	private List<String> resourcePatterns = Collections.emptyList();

	/**
	 * Initializes the properties that are passed to {@link PropertyLoaderAware} objects created as part of this
	 * factory.
	 */
	public void initialize() {
		for (String pattern : resourcePatterns) {
			for (Resource resource : resolveResources(pattern)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Resolving properties for " + resource.getDescription());
				}

				InputStream stream = null;
				try {
					stream = resource.getInputStream();
					loadProperties(resource, stream);
				} catch (IOException e) {
					LOG.warn("Unable to read resource, ignoring: " + resource.getDescription(), e);
				} finally {
					try {
						if (stream != null) {
							stream.close();
						}
					} catch (IOException e) {
						LOG.debug("Received an error closing a stream, ignoing " + e);
					}
				}
			}
		}

		if (loadedProperties.isEmpty()) {
			LOG.warn("No properties were loaded for factory with object type " + objectType);
		}
	}

	/**
	 * Loads up properties from {@link #readAndMutateProperties(Resource, InputStream)}. This is mainly used as an
	 * extension point.
	 * 
	 * @param resource resource that properties were read from
	 * @param stream input stream of the resoustreamrce
	 * @throws IOException in case of errors
	 */
	protected void loadProperties(final Resource resource, final InputStream stream) throws IOException {
		Properties mutatedProperties = readAndMutateProperties(resource, stream);
		if (mutatedProperties != null) {
			loadedProperties.putAll(mutatedProperties);
		}
	}

	/**
	 * Returns the properties from {@link InputStream} which were originally defined in {@link Resource}. This is mainly
	 * used as an extension point in case properties need to be mutated before being loaded. If return is {@code null}
	 * then the properties are not added.
	 * 
	 * @param resource the resource the properties came from
	 * @param stream {@link InputStream} of the resource
	 * @throws IOException in case of errors
	 * @return properties
	 */
	protected Properties readAndMutateProperties(final Resource resource, final InputStream stream) throws IOException {
		Properties properties = new Properties();
		properties.load(stream);
		return properties;
	}

	/**
	 * Called when an object is created in order to perform initial setup such as the initializing properties.
	 * 
	 * @param object the object that was created
	 */
	protected void setupObject(final T object) {
		object.setInitializingProperties(loadedProperties);
	}

	/**
	 * Resolves the resources for the given {@code pattern}. The current implementation relies heavily on the
	 * {@link ApplicationContext} in which this factory is created in. That is, resource patterns must be valid the the
	 * {@link ApplicationContext} in which this factory was started in.
	 * 
	 * @param pattern pattern for resources
	 * @return list of resolved resources
	 */
	protected Resource[] resolveResources(final String pattern) {
		Resource[] resources;
		try {
			resources = resourceLoader.getResources(pattern);
		} catch (IOException e) {
			LOG.fatal("Unable to find resource under " + pattern, e);
			throw new EpPersistenceException("Unable to list files for classpath entries in " + pattern, e);
		}
		return resources;
	}

	public void setResourcePatterns(final List<String> resourcePatterns) {
		this.resourcePatterns = resourcePatterns;
	}

	@Override
	public T getObject() throws Exception {
		if (!singleton || lastObject == null) {
			T object = objectType.newInstance();
			lastObject = object;
			setupObject(object);
		}
		return lastObject;
	}

	@Override
	public Class<?> getObjectType() {
		return objectType;
	}

	public void setObjectType(final Class<T> objectType) {
		this.objectType = objectType;
	}

	public void setSingleton(final boolean singleton) {
		this.singleton = singleton;
	}

	@Override
	public boolean isSingleton() {
		return singleton;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initialize();
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		resourceLoader = applicationContext;
	}
}
