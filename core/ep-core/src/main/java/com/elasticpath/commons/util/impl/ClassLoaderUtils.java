/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.commons.util.impl;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;

/**
 * Utilities for manipulating the classloader.
 */
public final class ClassLoaderUtils {

	private static final Logger LOG = Logger.getLogger(ClassLoaderUtils.class);
	
	private ClassLoaderUtils() {
		super();
	}
	
	/**
	 * Add the given URL to the classpath dynamically.
	 * 
	 * @param url the URL to add
	 */
	public static void addURL(final URL url) {
		ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		if (!(systemClassLoader instanceof URLClassLoader)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Can't add url to classpath, using unrecognized class loader " + systemClassLoader);
			}
			return;
		}
		
		URLClassLoader classLoader = (URLClassLoader) systemClassLoader; 
		Class<URLClassLoader> clazz = URLClassLoader.class;

		// Use reflection
		try {
			@SuppressWarnings("rawtypes")
			final Method method = clazz.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(classLoader, url);
		} catch (Exception e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Exception trying to add url (" + url + ") to classpath: " + e.getMessage());
			}
		}
	}

}
