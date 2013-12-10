package com.elasticpath.service.contentspace.impl;


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.domain.contentspace.ContentWrapper;
import com.elasticpath.domain.contentspace.Parameter;
import com.elasticpath.domain.contentspace.ParameterValue;
import com.elasticpath.domain.contentspace.ContentWrapperService;
import com.elasticpath.service.contentspace.ParameterResolvingException;
import com.elasticpath.service.contentspace.ParameterValueResolver;
import com.elasticpath.service.contentspace.ScriptEngine;
import com.elasticpath.service.contentspace.ScriptEngineFactory;

/**
 * Default implementation of {@link ParameterValueResolver}.
 */
public class ParameterValueResolverImpl implements ParameterValueResolver {

	private static final Logger LOG = Logger.getLogger(ParameterValueResolverImpl.class);

	private BeanFactory beanFactory;

	private ScriptEngineFactory scriptEngineFactory;

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	public BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	/**
	 * Resolves localized values for parameters using the parameter values supplied.
	 *
	 * @param contentWrapper for get parameter definitions.
	 * @param parameterValues the parameter values list for DynamicContent
	 * @param language the language or null if none specified
	 * @param context dynamic context map
	 * @throws ParameterResolvingException  if not all the parameters could be resolved.
	 * @return a map of parameter name - parameter value pairs or empty map if nothing was resolved
	 */
	public Map<String, String> resolveValues(
			final ContentWrapper contentWrapper,
			final List<ParameterValue> parameterValues,
			final Map <String, Object> context,
			final String language) throws ParameterResolvingException	{

		List<Parameter> parameterDefinitions = getAllContentWrapperParameters(contentWrapper);

		if (CollectionUtils.isEmpty(parameterDefinitions)) {
			//Must return a map to which objects can be put
			return new HashMap<String, String>();
		}

		Map<String, String> resolvedValue = new HashMap<String, String>();

		for (Parameter parameter : parameterDefinitions) {
			resolvedValue.put(parameter.getParameterId(), findParameterValue(parameter, parameterValues, language));
		}

		if (isDynamicParameterPresent(parameterDefinitions)) {

			Map<String, String> resolvedDynamicValues = processDynamicValues(
					contentWrapper,
					parameterValues,
					language, context);

			resolvedValue.putAll(resolvedDynamicValues);
		}

		// checks parameters and throws exception if any of them is not resolved
		checkParametersResolved(parameterDefinitions, resolvedValue);

		// filter resolved value by pass to template flag.
		filterTemplateParameters(resolvedValue, parameterDefinitions);

		return resolvedValue;
	}

	/**
	 * merge input parameters with template parameters as they have
	 * the same meaning in terms of parameter resolution.
	 * @param contentWrapper the content wrapper
	 * @return list of all parameters
	 */
	List<Parameter> getAllContentWrapperParameters(
			final ContentWrapper contentWrapper) {

		List<Parameter> parameterDefinitions = new ArrayList<Parameter>();
		if (CollectionUtils.isNotEmpty(contentWrapper.getTemplateParameters())) {
			parameterDefinitions.addAll(contentWrapper.getTemplateParameters());
		}

		if (CollectionUtils.isNotEmpty(contentWrapper.getUserInputSettings())) {
			parameterDefinitions.addAll(contentWrapper.getUserInputSettings());
		}
		return parameterDefinitions;
	}

	/**
	 *
	 * Filter the resolved values by Pass To Template flag from Parameter.
	 *
	 * @param resolvedValue a map of parameter name - parameter value pairs.
	 * @param parameterDefinitions parameter definition list from content wrapper.
	 */
	void filterTemplateParameters(final Map<String, String> resolvedValue, final List<Parameter> parameterDefinitions) {

		for (Parameter parameter : parameterDefinitions) {
			if (!parameter.isPassToTemplate()) {
				resolvedValue.remove(parameter.getParameterId());
			}
		}

	}

	/**
	 * Checks parameters and throws exception if any of them is not resolved.
	 *
	 * @param parameterDefinitions the parameter definitions
	 * @param resolvedValue the resolved values
	 * @throws ParameterResolvingException if any of the parameters is not resolved
	 */
	protected void checkParametersResolved(final List<Parameter> parameterDefinitions,
			final Map<String, String> resolvedValue) throws ParameterResolvingException {
		for (Parameter parameter : parameterDefinitions) {
			if (resolvedValue.get(parameter.getParameterId()) == null && parameter.isRequired()) {
				throw new ParameterResolvingException("Parameter [" + parameter.getParameterId() + "] has null value");
			}
		}
	}

	/**
	 * Process dynamic values by loading spring beans and passing it to the groovy shell as well
	 * as the groovy expressions for retrieving the dynamic values.
	 * @param contentWrapper Content Wrapper
	 * @param parameterValues List of parameter values. Some of them can be dynamic.
	 * @param language Language
	 * @param dynamicContext dynamic context map
	 * @return map of resolved dynamic values.
	 * @throws ParameterResolvingException if can not instanciate script engine
	 */
	Map<String, String> processDynamicValues(
			final ContentWrapper contentWrapper,
			final List<ParameterValue> parameterValues,
			final String language,
			final Map<String, Object> dynamicContext) throws ParameterResolvingException {

		Map<String, String> resolvedValue = new HashMap<String, String>();

		loadServiceDefinitions(contentWrapper, dynamicContext);

		loadStaticParameterValues(parameterValues, language, dynamicContext);

		final ScriptEngine scriptEngine = getScriptEngine(contentWrapper.getScriptLanguage());

		if (scriptEngine == null) {
			throw new ParameterResolvingException(
					MessageFormat.format("Can't create script engine. Probably <script-language> section is missing in content wrapper id {0}",
							contentWrapper.getWrapperId()
							)
					);
		}

		initializeScriptEngine(
				dynamicContext,
				contentWrapper.getInitSection(),
				scriptEngine);

		resolveDynamicValues(
				contentWrapper.getTemplateParameters(),
				parameterValues,
				resolvedValue,
				scriptEngine
				);

		return resolvedValue;

	}

	/**
	 * Get script engine for this script language.
	 *
	 * @param scriptLanguage the language name
	 * @return script engine for this script name
	 */
	ScriptEngine getScriptEngine(final String scriptLanguage) {
		return scriptEngineFactory.getInstance(scriptLanguage);
	}

	/**
	 * Resolve dynamic values.
	 * @param templateParameters list of <code>Parameter</code> definitions.
	 * @param parameterValues List of parameter values.
	 * @param resolvedValue resolved values
	 * @param scriptEngine the script engine
	 */
	void resolveDynamicValues(
			final List<Parameter> templateParameters,
			final List<ParameterValue> parameterValues,
			final Map<String, String> resolvedValue,
			final ScriptEngine scriptEngine
			) {

		for (Parameter parameterDefinition : templateParameters) {

			final String expression = parameterDefinition.getScriptExpression();

			if (!StringUtils.isBlank(expression)) {
				try {
					String dynamicResolvedValue = String.valueOf(
								resolveDynamicValue(scriptEngine, expression.trim()));
					resolvedValue.put(parameterDefinition.getParameterId(), dynamicResolvedValue);
				} catch (Exception e) {
					LOG.error("Error occurred during [" + expression + "] evaluation", e);
				}
			}
		}
	}

	/**
	 * Initializes the script engine.
	 *
	 * @param dynamicContext the dynamic context to use
	 * @param scriptInitSection the script init section
	 * @param scriptEngine the script engine
	 */
	protected void initializeScriptEngine(
			final Map<String, Object> dynamicContext,
			final String scriptInitSection,
			final ScriptEngine scriptEngine
			) {

		scriptEngine.initialize(dynamicContext, scriptInitSection);
	}

	/**
	 * Resolves a dynamic value using the script engine.
	 *
	 * @param scriptEngine for evaluate dynamic expression.
	 * @param expression the expression to resolve
	 * @return the result of the evaluated expression
	 */
	protected Object resolveDynamicValue(final ScriptEngine scriptEngine,
			final String expression) {
		return scriptEngine.evaluateExpression(expression);
	}

	/**
	 * Check if at least one dynamic value is present.
	 * At least one <code>Resolver</code> not null.
	 * @param parameters List of parameters
	 * @return true if at least one dynamic value is present.
	 */
	boolean isDynamicParameterPresent(final List<Parameter> parameters) {
		for (Parameter parameter : parameters) {
			if (parameter.getScriptExpression() != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Load static or resolved values to groovy by specified name.
	 * @param parameterValues List of parameter values
	 * @param language Language
	 * @param dynamicContext the dynamic context
	 */
	void loadStaticParameterValues(
			final List<ParameterValue> parameterValues,
			final String language,
			final Map<String, Object> dynamicContext) {

		// this must be moved out of here as it should belong to the content wrapper declaration
		Locale locale = null;
		if (language == null) {
			LOG.error("Language must be not null");
		} else {
			locale = LocaleUtils.toLocale(language);
		}
		dynamicContext.put("locale", locale);
		// end

		for (ParameterValue value : parameterValues) {
			dynamicContext.put(
					value.getParameterName(),
					value.getValue(language)
					);
		}
	}


	/**
	 * Load service definition,that correspond to spring beans
	 * and set as variables to groovy by specified name.
	 * @param contentWrapper Content Wrappper.
	 * @param dynamicContext the dynamic context
	 */
	void loadServiceDefinitions(final ContentWrapper contentWrapper,
			final Map<String, Object> dynamicContext) {
		if (contentWrapper.getServiceDefinitions() != null) {
			for (ContentWrapperService service : contentWrapper.getServiceDefinitions()) {
				dynamicContext.put(
						service.getName(),
						getServiceDefinitionBean(service.getValue())
						);
			}
		}
	}

	/**
	 * Get service definition bean by bean name for loading service definitions.
	 *
	 * @param serviceBeanName the bean id
	 * @return the bean
	 */
	protected Object getServiceDefinitionBean(final String serviceBeanName) {
		return getBeanFactory().getBean(serviceBeanName);
	}

	/**
	 * Find parameter value by specified language.
	 * @param parameter Parameter
	 * @param parameterValues list of values.
	 * @param language language.
	 * @return String value if value is present for specified language, null otherwise.
	 */
	private String findParameterValue(final Parameter parameter, final List<ParameterValue> parameterValues, final String language) {
		if (CollectionUtils.isNotEmpty(parameterValues)) {
			for (ParameterValue parameterValue : parameterValues) {

				if (StringUtils.equals(parameterValue.getParameterName(), parameter.getParameterId())) {

					String value = parameterValue.getValue(language);
					//if the language string is more than a 2 letter language
					//(such as en_IE), then get the parent and try again (with just 'en')
					if (value == null && hasParentLanguage(language)) {
						value = parameterValue.getValue(getParentLanguage(language));
					}
					if (value != null) {
						return value;
					}
				}
			}
		}

		return null;
	}

	private boolean hasParentLanguage(final String language) {
		return language.length() > 2;
	}

	private String getParentLanguage(final String languageVariant) {
		return StringUtils.substring(languageVariant, 0, 2);
	}

	/**
	 * Set the Script Engine Factory.
	 * @param scriptEngineFactory the scriptEngineFactory
	 */
	public void setScriptEngineFactory(final ScriptEngineFactory scriptEngineFactory) {
		this.scriptEngineFactory = scriptEngineFactory;
	}




}
