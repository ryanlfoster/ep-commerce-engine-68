/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.view.velocity;

import java.util.Locale;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityViewResolver;

import com.elasticpath.commons.util.InvalidatableCache;
import com.elasticpath.service.catalogview.StoreConfig;

/**
 * Subclass of <code>VelocityViewRsolver</code> that can take an injected bean
 * for the view class rather than a class name.
 */
public class StoreVelocityViewResolver extends VelocityViewResolver implements InvalidatableCache {
	
	private StoreConfig storeConfig;
	private VelocityConfigurer velocityConfigurer;

	/**
	 * Creates a new View instance of the specified view class and configures it
	 * including the store and the velocity configurer.
	 * 
	 * @param viewName the name of the view to build
	 * @return the view.
	 * @throws Exception if the view couldn't be resolved
	 */
	@Override
	protected AbstractUrlBasedView buildView(final String viewName) throws Exception {
		AbstractUrlBasedView view = super.buildView(viewName);
		if (StoreVelocityViewImpl.class.isAssignableFrom(getViewClass())) {
			StoreVelocityViewImpl storeView = (StoreVelocityViewImpl) view;
			storeView.setStoreConfig(storeConfig);
			storeView.setVelocityConfigurer(velocityConfigurer);
		}
		return view;
	}

	/**
	 * Get a reference to the store configuration.
	 * 
	 * @return the StoreConfig object
	 */
	protected StoreConfig getStoreConfig() {
		return this.storeConfig;
	}

	/**
	 * Set the store configuration.
	 * 
	 * @param storeConfig the store config
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}
	
	/**
	 * Get the velocity configurer.
	 * 
	 * @return the velocityConfigurer
	 */
	protected VelocityConfigurer getVelocityConfigurer() {
		return velocityConfigurer;
	}

	/**
	 * Set the velocity configurer.
	 * 
	 * @param velocityConfigurer the velocityConfigurer to set
	 */
	public void setVelocityConfigurer(final VelocityConfigurer velocityConfigurer) {
		this.velocityConfigurer = velocityConfigurer;
	}
	
	/**
	 * Templates have changed, let's remove the cache so the RuntimeInstance object can be garbage collected.
	 */
	@Override
	public void invalidate() {
		clearCache();
	
	}
	 
	/**
	 * Remove only one View from the cache.
	 * According to:
	 * A note on caching - subclasses of AbstractCachingViewResolver cache view instances they have resolved. 
	 * This greatly improves performance when using certain view technologies. 
	 * It's possible to turn off the cache, by setting the cache property to false. 
	 * Furthermore, if you have the requirement to be able to refresh a certain view at runtime 
	 * (for example when a Velocity template has been modified), 
	 * you can use the removeFromCache(String viewName, Locale loc) method
	 * http://static.springsource.org/spring/docs/2.0.x/reference/mvc.html#mvc-viewresolver
	 * @param objectUid - the viewName
	 */
	@Override
	public void invalidate(final Object objectUid) {
		for (Locale locale : storeConfig.getStore().getSupportedLocales()) {
			removeFromCache((String) objectUid, locale);
		}
		
	}

}