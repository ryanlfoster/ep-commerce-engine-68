/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.beanframework.config;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * A factory bean which will resolve a relative path using the resource loader.
 */
public class ResourcePathFactoryBean implements FactoryBean<String>, ResourceLoaderAware {

	private ResourceLoader resourceLoader;
	
	private String path;
	
	/**
	 * Set the resource loader. This is normally done automatically by spring.
	 * 
	 * @param resourceLoader the <code>ResourceLoader</code>
	 */
	public void setResourceLoader(final ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	/**
	 * Determine the absolute path found by the resource loader for the given path.
	 * 
	 * @return the absolute path if the resource loader can determine it, otherwise the original path string
	 * @throws Exception in case of exception determining the path
	 */
	public String getObject() throws Exception {
		Resource resource = resourceLoader.getResource(path);
		if (resource.exists()) {
			return resource.getFile().getAbsolutePath();
		}
		return path;
	}

	/**
	 * This factory bean will always return a string.
	 * @return String class
	 */
	public Class<?> getObjectType() {
		return String.class;
	}

	/**
	 * The path will not be a singleton, this allows it to be re-determined should
	 * the resource-loading change.
	 * 
	 * @return false
	 */
	public boolean isSingleton() {
		return false;
	}

	/**
	 * Set the path that should be resolved by the resource loader.
	 * 
	 * @param path the path to set
	 */
	public void setPath(final String path) {
		this.path = path;
	}

}
