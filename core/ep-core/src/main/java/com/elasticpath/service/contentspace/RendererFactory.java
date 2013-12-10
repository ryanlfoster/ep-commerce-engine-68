/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.contentspace;

import org.apache.log4j.Logger;

/**
 * A factory to create a new instance of the renderer
 * every time createRenderer() is invoked.
 */
public class RendererFactory {

	private static final Logger LOG = Logger.getLogger(RendererFactory.class);

	private static RendererFactory renderFactory;
	private Class< ? extends Renderer> rendererClass;

	/**
	 * Constructs a new class.
	 */
	protected RendererFactory() {
		super();
	}
	
	/**
	 * Gets the unique instance of this factory.
	 * 
	 * @return the {@link RendererFactory} instance.
	 */
	public static RendererFactory getInstance() {
		synchronized (RendererFactory.class) {
			if (renderFactory == null) {
				renderFactory = new RendererFactory();
			}
		}
		return renderFactory;
	}
	
	/**
	 * Creates a new instance of the Renderer.
	 * @return a {@link Renderer} instance
	 */
	public Renderer createRenderer() {
		try {
			return rendererClass.newInstance();
		} catch (InstantiationException exc) {
			LOG.error(exc);
		} catch (IllegalAccessException exc) {
			LOG.error(exc);
		}
		return null;
	}
	
	/**
	 * Sets the renderer class to use to instantiate new instances.
	 * 
	 * @param rendererClass the renderer class
	 */
	public void setRendererClass(final Class< ? extends Renderer> rendererClass) {
		this.rendererClass = rendererClass;
	}
}
