package com.elasticpath.web.security;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.servlet.ServletRequest;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.util.WebUtils;

/**
 * ESAPI-aware data binder. This class is aware of {@link org.owasp.esapi.filters.SecurityWrapperRequest} and is able to bypass its checks where
 * configured.
 */
public class EsapiServletRequestDataBinder extends ServletRequestDataBinder {
	private final List<ParameterConfiguration> parameterConfig = new ArrayList<ParameterConfiguration>();
	private ServletRequestParameterRetrievalStrategy retrievalStrategy = new DefaultServletRequestParameterRetrievalStrategy();

	/**
	 * Create a new {@link EsapiServletRequestDataBinder} instance.
	 *
	 * @param target target object to bind onto (or {@code null} if the binder is just used to convert a plain parameter
	 *            value)
	 * @param objectName name of the target object
	 * @param parameterConfig {@link ParameterConfiguration} for fine tuning security
	 */
	public EsapiServletRequestDataBinder(final Object target, final String objectName, final List<ParameterConfiguration> parameterConfig) {
		super(target, objectName);
		if (parameterConfig != null) {
			this.parameterConfig.addAll(parameterConfig);
		}

		// only the fields which are configured are allowed
		if (this.parameterConfig.isEmpty()) {
			// default data binder will short-circuit on a blank allowedFields array
			super.setDisallowedFields("*");
		} else {
			String[] allowedFields = new String[this.parameterConfig.size()];
			for (int i = 0; i < this.parameterConfig.size(); ++i) {
				allowedFields[i] = this.parameterConfig.get(i).getPattern();
			}
			super.setAllowedFields(allowedFields);
		}
	}

	/**
	 * Sets the {@link ServletRequestParameterRetrievalStrategy} for getting parameter values.
	 *
	 * @param retrievalStrategy strategy used for getting parameter values
	 */
	public void setRetrievalStrategy(final ServletRequestParameterRetrievalStrategy retrievalStrategy) {
		Assert.notNull(retrievalStrategy, "retrievalStrategy must not be null");
		this.retrievalStrategy = retrievalStrategy;
	}

	@Override
	public final void bind(final ServletRequest request) {
		MutablePropertyValues mpvs = new MutablePropertyValues(fetchParamters(request, null, null));
		MultipartRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartRequest.class);
		if (multipartRequest != null) {
			bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
		}
		doBind(mpvs);
	}

	private ParameterConfiguration findMostSpecificConfiguration(final String parameterName) {
		int bestMatchQuality = -1; // a match starts at 0
		ParameterConfiguration bestMatch = null;
		for (ParameterConfiguration config : parameterConfig) {
			int quality = config.parameterMatchQuality(parameterName);
			if (quality > bestMatchQuality) {
				bestMatchQuality = quality;
				bestMatch = config;
			}
		}
		return bestMatch;
	}

	/**
	 * Default implementation of {@link ServletRequestParameterRetrievalStrategy} for
	 * {@link EsapiServletRequestDataBinder}.
	 */
	private final class DefaultServletRequestParameterRetrievalStrategy implements ServletRequestParameterRetrievalStrategy {
		@Override
		public String[] getParameterValues(final ServletRequest request, final String parameterName, final ParameterConfiguration config) {
			if (config == null) {
				// standard checks for values without configuration
				return request.getParameterValues(parameterName);
			}

			String[] values = EsapiServletUtils.findUnsafeRequest(request).getParameterValues(parameterName);
			for (int i = 0; i < values.length; ++i) {
				String cleanValue;
				try {
					cleanValue = ESAPI.validator().getValidInput(parameterName, values[i], config.getValidator(), config.getMaxLength(),
							config.isAllowNulls());
				} catch (ValidationException e) {
					// error is logged, toss the exception
					cleanValue = StringUtils.EMPTY;
				}

				switch (config.getPolicy()) {
					case BLANK:
						// this is the default implementation of above
						break;
					case FILTER:
						cleanValue = filterInvalid(config, values[i]);
						break;
					case IGNORE:
						// errors logged, but we don't care, reset to original value
						cleanValue = values[i];
						break;
					default:
						// fail-safe in case values are added to the enumeration
						throw new NotImplementedException();
				}

				values[i] = cleanValue;
			}

			return values;
		}

		private String filterInvalid(final ParameterConfiguration config, final String originalValue) {
			if (originalValue == null) {
				if (!config.isAllowNulls()) {
					return StringUtils.EMPTY;
				}
				return originalValue;
			}

			// filter out what we don't want
			String value = originalValue;
			StringBuilder builder = new StringBuilder();
			Matcher matcher = ESAPI.securityConfiguration().getValidationPattern(config.getValidator()).matcher(value);
			while (matcher.find()) {
				builder.append(value.substring(matcher.start(), matcher.end()));
			}

			return builder.substring(0, Math.min(builder.length(), config.getMaxLength()));
		}
	}

	/**
	 * Return a map containing all parameters with the given prefix. Maps single values to String and multiple values to
	 * String array.
	 * <p>
	 * For example, with a prefix of "spring_", "spring_param1" and "spring_param2" result in a Map with "param1" and
	 * "param2" as keys.
	 * </p>
	 * <p>
	 * All parameters will be retrieved from the {@link ServletRequestParameterRetrievalStrategy}.
	 * </p>
	 *
	 * @param request HTTP request in which to look for parameters
	 * @param prefix the beginning of parameter names (if this is null or the empty string, all parameters will match)
	 * @return map containing request parameters <b>without the prefix</b>, containing either a String or a String array
	 *         as values
	 * @see javax.servlet.ServletRequest#getParameterNames
	 * @see javax.servlet.ServletRequest#getParameterValues
	 * @see javax.servlet.ServletRequest#getParameterMap
	 * @see WebUtils#getParametersStartingWith(ServletRequest, String)
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> fetchParamters(final ServletRequest request, final String prefix, final String prefixSeparator) {
		String parameterPrefix = prefix;
		if (prefix != null) {
			parameterPrefix = prefix + prefixSeparator;
		}
		if (parameterPrefix == null) {
			parameterPrefix = StringUtils.EMPTY;
		}

		Map<String, Object> result = new HashMap<String, Object>();
		for (Enumeration<String> paramNames = request.getParameterNames(); paramNames != null && paramNames.hasMoreElements();) {
			String name = paramNames.nextElement();
			if (!name.startsWith(parameterPrefix)) {
				continue;
			}

			String unprefixed = name.substring(parameterPrefix.length());
			String[] values = retrievalStrategy.getParameterValues(request, name, findMostSpecificConfiguration(name));

			if (values != null && values.length > 0) {
				if (values.length > 1) {
					result.put(unprefixed, values);
				} else {
					result.put(unprefixed, values[0]);
				}
			}
		}

		return result;
	}

	@Override
	public void setAllowedFields(final String... allowedFields) {
		throw new UnsupportedOperationException("Fields are only allowed to be configured during construction");
	}

	@Override
	public void setDisallowedFields(final String... disallowedFields) {
		throw new UnsupportedOperationException("Fields are only allowed to be configured during construction");
	}
}
