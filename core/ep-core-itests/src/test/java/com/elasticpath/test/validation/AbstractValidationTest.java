package com.elasticpath.test.validation;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Assert;

import org.hamcrest.Matchers;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;

import com.elasticpath.test.integration.BasicSpringContextTest;

/**
 * Abstract test for validation test scenarios.
 */
public abstract class AbstractValidationTest extends BasicSpringContextTest {

	/** Whitespace characters. */
	protected static char[] WHITESPACE = {' ', '\t'};
	private Validator validator;


	public Validator getValidator() {
		if (validator == null) {
			validator = getBeanFactory().getBean("validator");
		}
		return validator;
	}

	/**
	 * Find a {@link ConstraintViolation} for the given {@code propertyPath}. If there is more than a single constraint
	 * violation for a given path, only the first will be returned.
	 *
	 * @param violations the violation set
	 * @param propertyPath a property path which should have a violation
	 * @return the first constraint violation for the given {@code propertyPath} or {@code null} if not found
	 */
	public <T> ConstraintViolation<T> findViolationForPath(final Set<ConstraintViolation<T>> violations, final String propertyPath) {
		for (ConstraintViolation<T> violation : violations) {
			if (violation.getPropertyPath().toString().equals(propertyPath)) {
				return violation;
			}
		}
		return null;
	}

	/**
	 * Asserts that the set of violations <em>does</em> contain a violation for a particular property path.
	 *
	 * @param message a message to display on failure
	 * @param violations the violation set
	 * @param paths property paths that should be invalid
	 */
	public <T> void assertViolationsContains(final String message, final Set<ConstraintViolation<T>> violations,
			final String... paths) {
		Set<String> violationPaths = new HashSet<String>();
		for (ConstraintViolation<?> violation : violations) {
			violationPaths.add(violation.getPropertyPath().toString());
		}

		Assert.assertThat(message, violationPaths, hasItems(paths));
	}

	/**
	 * Asserts that the set of violations <em>does</em> contain a violation for a particular property path.
	 *
	 * @param violations the violation set
	 * @param paths property paths that should be invalid
	 * @see #assertValidationViolation(String, Object, String, Object)
	 */
	public <T> void assertViolationsContains(final Set<ConstraintViolation<T>> violations, final String... paths) {
		assertViolationsContains("", violations, paths);
	}

	/**
	 * Asserts that the violations defined by propertyPath contains the expected violation message.
	 *
	 * @param violations the violation set
	 * @param propertyPath the property path
	 * @param token the token that is expected within the violation message
	 */
	public <T> void assertViolationsMessageContainsToken(final Set<ConstraintViolation<T>> violations,
			final String propertyPath,
			final String token) {
		boolean found = false;
		StringBuilder paths = new StringBuilder();
		for (ConstraintViolation<?> violation : violations) {
			String path = violation.getPropertyPath().toString();
			if (propertyPath.equals(path)) {
				found = true;
				Assert.assertThat("The violation message should be populated correctly.",
						violation.getMessage(),
						containsString(token));
				break;
			} else {
				if (paths.length() > 0) {
					paths.append(", ");
				}
				paths.append(path);
			}
		}
		if (!found) {
			Assert.fail(String.format("Could not find property path %s within constraint violations property paths [%s].", propertyPath, paths));
		}
	}

	/**
	 * Asserts that the set of violations <em>does not</em> contain a violation for a particular property path.
	 *
	 * @param message a message to display on failure
	 * @param violations the violation set
	 * @param paths property paths that should be invalid
	 */
	public <T> void assertViolationsNotContains(final String message, final Set<ConstraintViolation<T>> violations,
			final String... paths) {
		Set<String> violationPaths = new HashSet<String>();
		for (ConstraintViolation<?> violation : violations) {
			violationPaths.add(violation.getPropertyPath().toString());
		}

		Assert.assertThat(message, violationPaths, Matchers.not(hasItems(paths)));
	}

	/**
	 * Asserts that the set of violations <em>does not</em> contain a violation for a particular property path.
	 *
	 * @param violations the violation set
	 * @param values property paths that should be invalid
	 * @see #assertViolationsNotContains(String, Set, String...)
	 */
	public <T> void assertViolationsNotContains(final Set<ConstraintViolation<T>> violations, final String... values) {
		assertViolationsNotContains("", violations, values);
	}

	/**
	 * Asserts that when the {@code property} is set to the given {@code propertyValue} that a validation violation is
	 * given. This helper method assumes there is a public setter (in camel case) method on the object that it can use
	 * to set the property. For example, if the property was {@code lastName}, then it would use the public method
	 * {@code setLastName}.
	 *
	 * @param object the object to validate
	 * @param property the property that should be set
	 * @param propertyValue the value to set
	 * @see #assertValidationViolation(String, Object, String, Object)
	 */
	public <T> void assertValidationViolation(final T object, final String property, final Object propertyValue) {
		assertValidationViolation("", object, property, propertyValue);
	}

	private void setObjectValue(final Object object, final String property, final Object objectValue) {
		ReflectionUtils.doWithMethods(object.getClass(), new MethodCallback() {
			@Override
			public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
				ReflectionUtils.makeAccessible(method);
				ReflectionUtils.invokeMethod(method, object, objectValue);
			}
		}, new MethodFilter() {
			@Override
			public boolean matches(final Method method) {
				return method.getParameterTypes().length == 1
						&& method.getName().endsWith(Character.toUpperCase(property.charAt(0)) + property.substring(1));
			}
		});
	}

	/**
	 * Asserts that when the {@code property} is set to the given {@code propertyValue} that a validation violation is
	 * given. This helper method assumes there is a public setter (in camel case) method on the object that it can use
	 * to set the property. For example, if the property was {@code lastName}, then it would use the public method
	 * {@code setLastName}.
	 *
	 * @param message the message to show on errors
	 * @param object the object to validate
	 * @param property the property that should be set
	 * @param propertyValue the value to set
	 */
	public <T> void assertValidationViolation(final String message, final T object, final String property, final Object propertyValue) {
		setObjectValue(object, property, propertyValue);
		Set<ConstraintViolation<T>> violations = getValidator().validateProperty(object, property);
		assertViolationsContains(String.format("Expected violation for value <%s>: %s", propertyValue, message), violations, property);
	}

	/**
	 * Asserts that when the {@code property} is set to the given {@code propertyValue} that it passes validation. This
	 * helper method assumes there is a public setter (in camel case) method on the object that it can use to set the
	 * property. For example, if the property was {@code lastName}, then it would use the public method
	 * {@code setLastName}.
	 *
	 * @param object the object to validate
	 * @param property the property that should be set
	 * @param propertyValue the value to set
	 * @see #assertValidationViolation(String, Object, String, Object)
	 */
	public <T> void assertValidationSuccess(final T object, final String property, final Object propertyValue) {
		assertValidationSuccess("", object, property, propertyValue);
	}

	/**
	 * Asserts that when the {@code property} is set to the given {@code propertyValue} that it passes validation. This
	 * helper method assumes there is a public setter (in camel case) method on the object that it can use to set the
	 * property. For example, if the property was {@code lastName}, then it would use the public method
	 * {@code setLastName}.
	 *
	 * @param message the message to show on errors
	 * @param object the object to validate
	 * @param property the property that should be set
	 * @param propertyValue the value to set
	 */
	public <T> void assertValidationSuccess(final String message, final T object, final String property, final Object propertyValue) {
		setObjectValue(object, property, propertyValue);
		Set<ConstraintViolation<T>> violations = getValidator().validateProperty(object, property);
		assertViolationsNotContains(String.format("Expected success for value <%s>: %s", propertyValue, message), violations, property);
	}
}
