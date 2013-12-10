package com.elasticpath.validation.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springmodules.validation.valang.ValangValidator;

import com.elasticpath.validation.domain.ValidationConstraint;
import com.elasticpath.validation.domain.ValidationError;
import com.elasticpath.validation.domain.ValidationResult;
import com.elasticpath.validation.service.ValidationEngine;

/**
 * Spring's Valang validation engine that performs validation on a declarative constraint.
 */
public class ValangValidationEngineImpl implements ValidationEngine {
	
	private static final Logger LOG = Logger.getLogger(ValangValidationEngineImpl.class);
	
	//ValangValidator class is thread-safe
	//It can become a bottleneck if this class is used extensively
	//But since currently it is used only by condition builder client - there not going to be many calls to this service
	private final ValangValidator valangValidator = new ValangValidator();
	
	/**
	 * allows setting up valang validator with custom functions. to be used for Spring configuration
	 * when validation engine bean is defined.
	 * @param customFunctionsMapping a map where key is the function name and value is the class name 
	 *                               for the class that implementa a custom valang function. 
	 */
	public void setCustomFunctions(final Map<String, String> customFunctionsMapping) {
		valangValidator.setCustomFunctions(customFunctionsMapping);
	}
	
	/**
	 * allows adding a custom function to the valang validator.
	 * @param functionName the name of the function that is to be used in the declarative validation constraint
	 * @param functionImplementationClassName the class that implements the function.
	 */
	public void addCustomFunction(final String functionName, final String functionImplementationClassName) {
		valangValidator.addCustomFunction(
				functionName, functionImplementationClassName);
	}

	@Override
	public ValidationResult validate(final Object valueToValidate,
			final ValidationConstraint constraint) {
		
		if (constraint != null && StringUtils.isNotBlank(constraint.getConstraint())) {
		
			Errors errors = new MapBindingResult(new HashMap<Object, Object>(), "Errors");
			
			valangValidator.setValang(constraint.getConstraint());
			try {
				valangValidator.afterPropertiesSet();
			
				valangValidator.validate(valueToValidate, errors);
				if (errors.hasErrors()) {
					
					logValidationConstraintErrors(constraint, valueToValidate, errors.getAllErrors());
					return newFailedValidationResult(constraint, errors.getAllErrors());
				}
			
			} catch (Exception constraintCompilationException) {
				LOG.error("Valang constraint compilation error", constraintCompilationException);
				List<Object> exceptions = new ArrayList<Object>();
				exceptions.add(constraintCompilationException);
				logValidationConstraintErrors(constraint, valueToValidate, exceptions);
				return newFailedValidationResult(constraint, exceptions);
			}
		}
		
		return ValidationResult.VALID;
	}
	
	/**
	 * A failed validation result object.
	 */
	private class FailedValidationResult implements ValidationResult {
	
		private final ValidationConstraint validationConstraint;
		private final List<?> validationErrors;
		
		private String cachedRawErrorMessage;
		private Collection<ValidationError> cachedErrors;
		
			FailedValidationResult(final ValidationConstraint constraint,
				final List<?> errors) {
			validationConstraint = constraint;
			validationErrors = errors;
		}
		
		public boolean isValid() {
			return false;
		}

		@Override
		public String getMessage() {
			if (cachedRawErrorMessage == null) { 
				cacheRawMessage();
			}
			return cachedRawErrorMessage;
		}

		private void cacheRawMessage() {
			StringBuilder out = new StringBuilder();
			for (Object error : validationErrors) {
				out.append(error);
				out.append(", ");
			}
			cachedRawErrorMessage = out.toString();
		}

		@Override
		public String getMessage(final Locale locale) {
			return validationConstraint.getLocalizedErrorMessage(locale);
		}
		
		@Override
		public String toString() {
			return getMessage();
		}

		public ValidationError[] getErrors() {
			if (cachedErrors == null) {
				
				cacheErrorsCollection();
				
			}
			
			return cachedErrors.toArray(new ValidationError[cachedErrors.size()]);
		}

		private void cacheErrorsCollection() {
			cachedErrors = new ArrayList<ValidationError>(validationErrors.size());
			for (Object error : validationErrors) {
				
				final String rawErrorMessage = error.toString();
				
				cachedErrors.add(new ValidationError() {
					
					private String rawMessage = rawErrorMessage;
					
					@Override
					public String getMessage() {
						return rawMessage;
					}
					@Override
					public String getMessage(final Locale locale) {
						return getMessage();
					}
					
				});
				
			}
		}
		
	}
	
	private ValidationResult newFailedValidationResult(final ValidationConstraint constraint,
			final List<?> errors) {
		
		return new FailedValidationResult(constraint, errors);
		
	}
	
	private void logValidationConstraintErrors(final ValidationConstraint constraint,
			final Object valueToValidate, final Collection<?> errors) {
		
		LOG.info("Validation error for value: " + valueToValidate);
		LOG.info("Constraints: " + constraint);
		for (Object error : errors) {
			LOG.info(error);
		}
		
	}

}
