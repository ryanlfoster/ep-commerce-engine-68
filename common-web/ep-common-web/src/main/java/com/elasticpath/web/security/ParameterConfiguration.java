package com.elasticpath.web.security;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.PatternMatchUtils;

/**
 * Configuration that should be passed to the {@link EsapiServletRequestDataBinder} to fine tune security preferences
 * for data binding.
 */
public class ParameterConfiguration {
	// these defaults align with the defaults provided by ESAPI
	/** Default validator used for configuration. */
	public static final String DEFAULT_VALIDATOR = "HTTPParameterValue";
	/** Default length of parameter values. */
	public static final int DEFAULT_MAX_LENGTH = 2000;
	/** Whether to allow nulls by default. */
	public static final boolean DEFAULT_ALLOW_NULLS = false;
	private final String pattern;
	private final Policy policy;
	private final String validator;
	private final int maxLength;
	private final boolean allowNulls;
	private final boolean customValidator;

	/**
	 * Creates a new {@link ParameterConfiguration} instance.
	 * 
	 * @param parameterPattern pattern to match
	 */
	public ParameterConfiguration(final String parameterPattern) {
		this(parameterPattern, Policy.BLANK);
	}

	/**
	 * Creates a new {@link ParameterConfiguration} instance.
	 * 
	 * @param parameterPattern pattern to match
	 * @param validator ESAPI validator which should be used to validate the parameter (or empty to use the default)
	 * @param maxLength maximum length for the ESAPI validator
	 * @param allowNulls whether to allow null values
	 */
	public ParameterConfiguration(final String parameterPattern, final String validator, final int maxLength, final boolean allowNulls) {
		this(parameterPattern, validator, maxLength, allowNulls, Policy.BLANK);
	}

	/**
	 * Creates a new {@link ParameterConfiguration} instance.
	 * 
	 * @param parameterPattern pattern to match
	 * @param policy {@link Policy} to be used for offending values
	 */
	public ParameterConfiguration(final String parameterPattern, final Policy policy) {
		this(parameterPattern, "", DEFAULT_MAX_LENGTH, DEFAULT_ALLOW_NULLS, policy);
	}

	/**
	 * Creates a new {@link ParameterConfiguration} instance.
	 * 
	 * @param parameterPattern pattern to match
	 * @param validator ESAPI validator which should be used to validate the parameter (or empty to use the default)
	 * @param maxLength maximum length for the ESAPI validator
	 * @param allowNulls whether to allow nulls
	 * @param policy {@link Policy} to be used for offending values
	 */
	public ParameterConfiguration(final String parameterPattern, final String validator, final int maxLength, final boolean allowNulls,
			final Policy policy) {
		this.pattern = parameterPattern;
		this.allowNulls = allowNulls;
		this.policy = policy;

		/*
		 * Make sure to keep a valid validator even if the user requests none (an empty string). This ensures that if
		 * this configuration is passed to someplace which doesn't conform to our interface (i.e. doesn't use
		 * customValidator field), it will continue to function as desired.
		 */
		if (StringUtils.isEmpty(validator)) {
			this.validator = DEFAULT_VALIDATOR;
			this.customValidator = false;
		} else {
			this.validator = validator;
			this.customValidator = true;
		}

		if (maxLength <= 0) {
			this.maxLength = DEFAULT_MAX_LENGTH;
		} else {
			this.maxLength = maxLength;
		}
	}

	/**
	 * Determines whether this configuration matches the given parameter.
	 * 
	 * @param parameterName parameter name to match
	 * @return whether the configuration matches the given parameter
	 * @see PatternMatchUtils#simpleMatch(String, String)
	 */
	public boolean matchesParameter(final String parameterName) {
		return PatternMatchUtils.simpleMatch(pattern, parameterName);
	}

	public Policy getPolicy() {
		return policy;
	}

	public boolean isCustomValidator() {
		return customValidator;
	}

	public String getValidator() {
		return validator;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public boolean isAllowNulls() {
		return allowNulls;
	}

	/* package */String getPattern() {
		return pattern;
	}

	/**
	 * Represents the policy that should be followed when {@link ParameterConfiguration} does not conform to a
	 * particular pattern.
	 */
	public static enum Policy {
		/** Blank the entire field. */
		BLANK,
		/** Filter out offending values. */
		FILTER,
		/** Ignore offending values. */
		IGNORE,
	}

	/**
	 * Returns the quality of a match with a higher value signifying a better match. The quality of a match is how
	 * close a parameter matches this configuration. The quality value is unscaled and <em>should not</em> be used
	 * to compare with quality of other configuration.
	 *
	 * @param parameterName name to check quality for
	 * @return match quality (greater than or equal to 0 if it matches, -1 otherwise)
	 */
	public int parameterMatchQuality(final String parameterName) {
		if (matchesParameter(parameterName)) {
			return pattern.replaceAll("\\*", "").length();
		}
		return -1;
	}
}