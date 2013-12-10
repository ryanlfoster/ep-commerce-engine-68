/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.util.impl;

import java.io.InputStream;

import javax.servlet.ServletContext;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.tools.view.servlet.WebappLoader;
import org.springframework.web.context.ServletContextAware;

/**
 * Extends the {@link WebappLoader} by adding the ability to get the {@link ServletContext} instance
 * from Spring.
 */
public class ContextAwareWebappResourceLoader extends ResourceLoader implements ServletContextAware {

	private final WebappLoader delegate = new WebappLoader();
	private ServletContext servletContext;
	
	
	/**
	 * Overridden in order to delegate the call.
	 * 
	 * @param runtimeServices the Velocity runtime services implementation
	 * @param configuration the properties of Velocity
	 */
	@Override
	public void commonInit(final RuntimeServices runtimeServices, final ExtendedProperties configuration) {
		super.commonInit(runtimeServices, configuration);
		delegate.commonInit(runtimeServices, configuration);
	}

	/**
	 * Sets the {@link ServletContext} instance as an application attribute.
	 * This needs to be done in order for the {@link WebappLoader} to find the servlet context.
	 * 
	 * @param configuration the velocity properties
	 */
	@Override
	public void init(final ExtendedProperties configuration) {
		rsvc.setApplicationAttribute(ServletContext.class.getName(), servletContext);
		delegate.init(configuration);
	}

	@Override
	public long getLastModified(final Resource resource) {
		return delegate.getLastModified(resource);
	}

	@Override
	public InputStream getResourceStream(final String source) throws ResourceNotFoundException {
		return delegate.getResourceStream(source);
	}

	@Override
	public boolean isSourceModified(final Resource resource) {
		return delegate.isSourceModified(resource);
	}

	/**
	 * Sets the {@link ServletContext} instance.
	 * 
	 * @param servletContext the {@link ServletContext} instance
	 */
	public void setServletContext(final ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}
