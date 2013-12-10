/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.sfweb.view.velocity;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.ToolboxManager;
import org.apache.velocity.tools.view.context.ChainedContext;
import org.apache.velocity.tools.view.servlet.ServletToolboxManager;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityToolboxView;

import com.elasticpath.commons.util.StoreVelocityConfigHelper;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.sfweb.util.impl.ThreadLocalStoreConfigProxyImpl;

/**
 * Implements a store specific VelocityView that handles the 
 * problem when one template is being referenced from another using #parse(templateName).
 * <p>
 * The Setup:
 * <p>
 * Let's assume that we have breadcrumbs.vm referenced by categoryTemplate.vm 
 * using the #parse("includes/breadcrumbs.vm") macro. The templates exist outside of the web
 * app in assets/storeassets/theme/store/templates/velocity. A custom ResourceManager is used
 * in order to retrieve the actual templates from their location depending on the requested store
 * and the theme associated with it. A file resource loader is employed to get the templates loaded
 * with root pointing to assets/storeassets.
 * <p>
 * The Problem:
 * <p>
 * Velocity processes templates in two phases - init and render. In the init state the template gets
 * initialized and as part of that phase all the macros in the template get cached in a hashtable where
 * the key is the template name which acts also as a namespace. (we set the key 
 * 'velocimacro.permissions.allow.inline.local.scope' to true so that we enable the macros being available only
 * at the level where they are defined and not globally).
 * The name of the macro is entered into the hashtable as 
 * theme/store/templates/velocity/includes/breadcrumbs.vm
 * 
 * At the render phase Velocity parses the template and if it finds a reference to a certain macro it tries
 * to find it in the cache using the name it is referenced with (in our case 'includes/breadcrumbs.vm')
 * Velocity does not have that entry in its internal cache and as such does not consider that the template
 * has any macros. This leads to interpretation of a macro reference being just a plain string printed on 
 * the display.
 * 
 * <p>
 * The Solution:
 * <p>
 * With this class we override the method pushCurrentTemplateName() that is used to add the template name
 * on top of the stack as the current template is being processed. The solution will check if a template
 * exists and if it does not will try to get the default one. The fallback strategy has to be implemented
 * here as well because we are not aware if the template is in the store's folder or the default one at the 
 * time when the template has to be resolved.
 * 
 */
public class StoreVelocityViewImpl extends VelocityToolboxView {

	private static final Logger LOG = Logger.getLogger(StoreVelocityViewImpl.class);

	private StoreConfig storeConfig;

	private VelocityConfigurer velocityConfigurer;
	
	/**
	 * This implementation overrides the original one from {@link VelocityToolboxView}. It adds the ability
	 * to resolve template names with the store code and the theme name and to prepend them.
	 * 
	 * @param model the model properties
	 * @param request the http request
	 * @param response the http response
	 * @return the Context implementation
	 * @throws Exception if error occurs
	 */
	@Override
	protected Context createVelocityContext(final Map<String, Object> model, final HttpServletRequest request, final HttpServletResponse response)
			throws Exception {
		ChainedContext velocityContext = getNewVelocityContext(model, request, response);

		// Load a Velocity Tools toolbox, if necessary.
		if (getToolboxConfigLocation() != null) {
			ToolboxManager toolboxManager = ServletToolboxManager.getInstance(getServletContext(), getToolboxConfigLocation());
			@SuppressWarnings("unchecked")
			Map<String, Object> toolboxContext = toolboxManager.getToolbox(velocityContext);
			velocityContext.setToolbox(toolboxContext);
		}

		return velocityContext;
	}

	/**
	 * Creates a new VelocityContext object that registers templates according to store.
	 * @param model properties model
	 * @param request http request
	 * @param response http response
	 * @return velocity context
	 */
	protected ChainedContext getNewVelocityContext(final Map<String, Object> model,
			final HttpServletRequest request, final HttpServletResponse response) {
		return new ChainedContext(new VelocityContext(model), getVelocityEngine(), request, response, getServletContext()) {
			/**
			 * Overrides the super method in order to prepend the correct path to the template name.
			 */
			@Override
			public void pushCurrentTemplateName(final String templateName) {
				String storeSpecificTemplateName = templateName;

				final StoreConfig storeConfig = getStoreConfig();

				if (LOG.isTraceEnabled()) {
					LOG.trace("Template being requested to be put on the stack: " + templateName);
				}

				// If we don't have a store then this request is probably coming from the servlet during startup so just pass it along.
				if (storeConfig != null && !StringUtils.isBlank(storeConfig.getStoreCode())) {
					final String storeCode = storeConfig.getStoreCode();

					final String theme = storeConfig.getSetting("COMMERCE/STORE/theme").getValue();

					// If a template is included in another template, then the included template's name
					// will not start with the theme. This is the case we're trying to solve, so under
					// these conditions we should prepend the theme/store path.
					if (!templateName.startsWith(theme)) {
						storeSpecificTemplateName = getRealTemplateName(templateName, storeCode, theme);
					}
				}
				super.pushCurrentTemplateName(storeSpecificTemplateName);
			}
		};
	}

	/**
	 * Gets the real path to the template considering the store and theme associated with it.
	 * If the specified template doesn't exist for the specified store, the default template
	 * for the given theme is referenced.
	 * 
	 * @param templateName the template name
	 * @param storeCode the store code
	 * @param theme the theme name
	 * @return the real path to the template
	 */
	String getRealTemplateName(final String templateName, final String storeCode, final String theme) {
		String storeSpecificTemplateName = StoreVelocityConfigHelper.getStoreSpecificResourcePath(templateName, theme, storeCode);

		if (!templateExists(storeSpecificTemplateName)) {
			storeSpecificTemplateName = StoreVelocityConfigHelper.getDefaultResourcePath(templateName, theme);
		}
		return storeSpecificTemplateName;
	}

	/**
	 * Checks whether a template with templateName exists in Velocity which on
	 * its own will check that on the file system.
	 * 
	 * @param templateName the template name to be checked
	 * @return true if the template can be found
	 */
	boolean templateExists(final String templateName) {
		return getVelocityEngine().resourceExists(templateName);
	}


	/**
	 * Get a reference to the store configuration.
	 * 
	 * @return the StoreConfig object
	 */
	protected StoreConfig getStoreConfig() {
		if (storeConfig == null) {
			storeConfig = ThreadLocalStoreConfigProxyImpl.getInstance();
		}
		return storeConfig;
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
	 * Get the velocity engine. Use <code>VelocityConfigurer</code> if one has been injected,
	 * otherwise delegate to the parent <code>VelocityView</code>.
	 * 
	 * @return a <code>VelocityEngine</code> object.
	 */
	@Override
	protected VelocityEngine getVelocityEngine() {
		return getVelocityConfigurer().getVelocityEngine(); 
	}

	/**
	 * Do nothing during initialization as we don't want an engine until a
	 * store is accessed.
	 */
	@Override
	protected void initApplicationContext() {
		// Do nothing, overrides default init behaviour.
	}

	/**
	 * Set URL to be not required during initialization when store is not available.
	 * 
	 * @return true if a view URL is required.
	 */
	@Override
	protected boolean isUrlRequired() {
		return storeConfig.getStoreCode() != null;
	}

}