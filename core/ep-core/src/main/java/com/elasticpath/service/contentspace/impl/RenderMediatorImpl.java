/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.contentspace.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.ContentWrapperRepository;
import com.elasticpath.domain.contentspace.DynamicContent;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.service.contentspace.DynamicContentResolutionException;
import com.elasticpath.service.contentspace.DynamicContentRuntimeService;
import com.elasticpath.service.contentspace.ParameterResolvingException;
import com.elasticpath.service.contentspace.ParameterValueResolver;
import com.elasticpath.service.contentspace.RenderContext;
import com.elasticpath.service.contentspace.RenderMediator;
import com.elasticpath.service.contentspace.RenderMediatorException;
import com.elasticpath.service.contentspace.Renderer;
import com.elasticpath.tags.TagSet;

/**
 * This class is a mediator between the renderer, the request and the velocity template.
 */
public class RenderMediatorImpl implements RenderMediator {

	/** string to be rendered in case of error. (Used in contentspace velocity macro). */
	static final String SIGNAL_ERROR = "<<error>>";

	/** The logger for the RenderMediatorImpl class. **/
	private static final Logger LOG = Logger.getLogger(RenderMediatorImpl.class);
	
	private ParameterValueResolver parameterValueResolver;
	
	private ContentWrapperRepository contentWrapperRepository;
	
	private DynamicContentRuntimeService dynamicContentRuntimeService;

	private Renderer renderer;
	
	/**
	 * Sets the parameter resolver instance.
	 * 
	 * @param parameterValueResolver the parameter resolver instance
	 */
	public void setParameterValueResolver(final ParameterValueResolver parameterValueResolver) {
		this.parameterValueResolver = parameterValueResolver;
	}
	
	/**
	 * Gets the parameter resolver instance.
	 * 
	 * @return parameterValueResolver the parameter resolver instance
	 */
	ParameterValueResolver getParameterValueResolver() {
		return this.parameterValueResolver;
	}

	/**
	 *
	 * @param contentSpaceName the content space name
	 * @param locale the locale to retrieve content for
	 * @return the rendered content
	 */
	@Override
	public String render(final String contentSpaceName, final Locale locale) {
	
		try {
			final TagSet tagSet = getTagSet();

			final DynamicContent resolvedContent = getDynamicContentRuntimeService().resolve(tagSet, contentSpaceName);
			
			final String wrapperId = resolvedContent.getContentWrapperId();
			final ContentWrapper contentWrapper = getContentWrapperFromRepositoryById(contentSpaceName, wrapperId);
			
			final List<ParameterValue> parameterValue = resolvedContent.getParameterValues();

			if (locale == null) {
				LOG.warn("RenderMediatorImpl.render() can not render content: locale must not be null.");
				return signalError();
			}

			final Map<String, Object> context = new HashMap<String, Object>();
			context.put(WebConstants.CUSTOMER_SESSION, getCustomerSession());
			final Map<String, String> resolvedValues = resolveValues(contentWrapper, parameterValue, locale.toString(), context);
			
			appendGlobalParameters(resolvedValues);
			
			final RenderContext renderContext = configureRendererContext(resolvedValues, contentWrapper);
			return processRendering(contentSpaceName, renderContext);
		} catch (DynamicContentResolutionException dcre) {
			LOG.warn(dcre.getMessage());
			return signalError();
		} catch (ParameterResolvingException p) {
			LOG.warn("RenderMediatorImpl.render() can not render content: " + p.getMessage());
			return signalError();
		} catch (Exception e) {
			LOG.error("RenderMediatorImpl.render() can not render content.", e);
			return signalError();
		}
	}

	/**
	 * appends global variables to parameter list which are set inside TargetedSellingFilter now.
	 * <pre>
	 * Currently:
	 * baseUrl - base url path for war files of SF used for resolving URL paths in content wrapper templates
	 * </pre>
	 *
	 * @param resolvedValues parameter values with global parameter values
	 */
	protected void appendGlobalParameters(final Map<String, String> resolvedValues) {
		final Map<String, String> globals = getRequestAttribute(WebConstants.RENDER_MEDIATOR_GLOBAL_PARAMETER_VALUES);
		
		if (MapUtils.isNotEmpty(globals)) {
			final Set<String> keys = globals.keySet();
			for (String key : keys) {
				resolvedValues.put(key, globals.get(key));
			}
		}
	}

	/**
	 * Gets the tag cloud from the request.
	 * 
	 * @return the tag cloud or null if none was set
	 */
	TagSet getTagSet() {
		return getRequestAttribute(WebConstants.TAG_SET);
	}

	/**
	 * @return the customer session
	 */
	CustomerSession getCustomerSession() {
		return getRequestAttribute(WebConstants.CUSTOMER_SESSION);
	}

	/**
	 * Process rendering operation with the specified renderContext over
	 * the given contentSpaceName.
	 * 
	 * @param contentSpaceName the content space name
	 * @param renderContext the render context
	 * @return a string with null value if exception occurs or the rendered contents
	 */
	String processRendering(final String contentSpaceName, final RenderContext renderContext) {
		try {
			final Renderer newRenderer = getRenderer();
			return newRenderer.doRender(contentSpaceName, renderContext);
		} catch (Exception exc) {
			LOG.error("Error occurred", exc);
		}
		return signalError();
	}

	private RenderContext configureRendererContext(final Map<String, String> params, final ContentWrapper wrapper) {
		RenderContext renderContext = new RenderContext();
		
		renderContext.setParameters(params);
		renderContext.setContentWrapper(wrapper);
		return renderContext;
	}

	/**
	 * Signal an error by returning an error code.
	 * 
	 * @return a string representing an error has occurred
	 */
	String signalError() {
		// This error string is used in Velocity macro!!!
		return SIGNAL_ERROR;
	}

	private Renderer getRenderer() {
		return renderer;
	}

	@SuppressWarnings("unchecked")
	private <T> T getRequestAttribute(final String paramName) {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null) {
			return null;
		}
		return (T) requestAttributes.getAttribute(paramName, RequestAttributes.SCOPE_REQUEST);
	}

	/**
	 * Sets the content wrapper repository for the render mediator, the repository is used to 
	 * obtain content wrappers.
	 *
	 * @param contentWrapperRepository the repository to be set
	 */
	public void setContentWrapperRepository(final ContentWrapperRepository contentWrapperRepository) {
		this.contentWrapperRepository = contentWrapperRepository;
	}
	
	/**
	 * Sets the content wrapper repository for the render mediator, the repository is used to 
	 * obtain content wrappers.
	 *
	 * @return contentWrapperRepository the repository to be set
	 */
	public ContentWrapperRepository getContentWrapperRepository() {
		return this.contentWrapperRepository;
	}


	
	/**
	 * get wrapper from repository by wrapper id and check its validity.
	 *
	 * @param contentSpaceName the content space name
	 * @param wrapperId the id of wrapper
	 * @return content wrapper
	 * @throws RenderMediatorException if content wrapper is null after retrieval
	 */
	protected ContentWrapper getContentWrapperFromRepositoryById(
			final String contentSpaceName,
			final String wrapperId) throws RenderMediatorException {
		if (wrapperId == null) {
			throw new RenderMediatorException("RenderMediatorImpl.render() wrapper with NULL id provided" 
					+ " for cs [" + contentSpaceName + "].");
		}
		ContentWrapper contentWrapper = getContentWrapperRepository().findContentWrapperById(wrapperId);
		if (contentWrapper == null) {
			throw new RenderMediatorException("RenderMediatorImpl.render() wrapper with id [" + wrapperId 
					+ "] not in repository for cs [" + contentSpaceName + "].");
		}
		return contentWrapper;
	}
	
	/**
	 * uses parameter resolver in order to resolve locale dependent and evaluatable values for 
	 * parameters.
	 *
	 * @param contentWrapper the wrapper
	 * @param parameterValues the values 
	 * @param locale the String representation of the locale
	 * @param context dynamic context map
	 * @return map of resolved parameters where key is parameter name and value is the value of that parameter
	 * @throws ParameterResolvingException in case of inconsistent values provided for parameters
	 */
	protected Map<String, String> resolveValues(
			final ContentWrapper contentWrapper,
			final List<ParameterValue> parameterValues,
			final String locale, final Map<String, Object> context)
			throws ParameterResolvingException {
		return getParameterValueResolver().resolveValues(contentWrapper, parameterValues, context, locale);
	}

	/**
	 * get runtime service for resolving dynamic content.
	 *
	 * @return runtime service for resolving dynamic content
	 */
	DynamicContentRuntimeService getDynamicContentRuntimeService() {
		return dynamicContentRuntimeService;
	}

	/**
	 * set runtime service for resolving dynamic content.
	 *
	 * @param dynamicContentRuntimeService runtime service for resolving dynamic content
	 */
	public void setDynamicContentRuntimeService(
			final DynamicContentRuntimeService dynamicContentRuntimeService) {
		this.dynamicContentRuntimeService = dynamicContentRuntimeService;
	}

	
	/**
	 * Set the renderer.
	 * @param renderer to use.
	 */
	public void setRenderer(final Renderer renderer) {
		this.renderer = renderer;
	}


}
