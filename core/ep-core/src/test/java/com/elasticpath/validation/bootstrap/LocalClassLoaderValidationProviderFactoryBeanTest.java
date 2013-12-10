package com.elasticpath.validation.bootstrap;

import java.util.Collections;

import javax.validation.Configuration;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.TraversableResolver;
import javax.validation.ValidationProviderResolver;
import javax.validation.spi.BootstrapState;
import javax.validation.spi.ValidationProvider;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

/**
 * Tests for LocalClassLoaderValidationProviderFactoryBean including integration with javax.validation.
 */

public class LocalClassLoaderValidationProviderFactoryBeanTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	/**
	 * Ensure that it's not possible to create a LocalClassLoaderValidationProviderFactoryBean
	 * if no {@link ValidationProvider} is specified.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void ensureProviderIsRequired() {
		LocalClassLoaderValidationProviderFactoryBean factoryBean =
				new LocalClassLoaderValidationProviderFactoryBean(null, null, null);

		factoryBean.createInstance();
	}

	/**
	 * Ensure that the custom {@link ValidationProviderResolver} is actually invoked by javax.validation
	 * when it's asked to resolve the {@link ValidationProvider}.
	 */
	@Test
	public void ensureCustomResolverIsUsedInsteadOfJavaSpi() {
		final ValidationProvider<?> provider = context.mock(ValidationProvider.class);
		final ValidationProviderResolver providerResolver = context.mock(ValidationProviderResolver.class);

		context.checking(new Expectations() { {
			oneOf(providerResolver).getValidationProviders();
			will(returnValue(Collections.singletonList(provider)));

			allowing(provider);
		} });
		
		LocalClassLoaderValidationProviderFactoryBean factoryBean =
				new LocalClassLoaderValidationProviderFactoryBean(provider, null, null) {
			@Override
			ValidationProviderResolver createValidationProviderResolver(final ValidationProvider<?> provider) {
				return providerResolver;
			}
		};

		factoryBean.createInstance();
	}

	/**
	 * Non-null values for the {@link TraversableResolver} and {@link ConstraintValidatorFactory}
	 * <strong>should</strong> be set onto the {@link Configuration}.
	 */
	@Test
	public void ensureTraversableResolverAndConstraintValidatorFactoryAreSet() {
		final ValidationProvider<?> provider = context.mock(ValidationProvider.class);
		final Configuration<?> configuration = context.mock(Configuration.class);
		final TraversableResolver traversableResolver = context.mock(TraversableResolver.class);
		final ConstraintValidatorFactory constraintValidatorFactory = context.mock(ConstraintValidatorFactory.class);

		context.checking(new Expectations() { {
			oneOf(provider).createSpecializedConfiguration(with(any(BootstrapState.class)));
			will(returnValue(configuration));

			oneOf(configuration).traversableResolver(with(traversableResolver));
			oneOf(configuration).constraintValidatorFactory(with(constraintValidatorFactory));

			allowing(configuration);
		} });
		
		LocalClassLoaderValidationProviderFactoryBean factoryBean =
				new LocalClassLoaderValidationProviderFactoryBean(provider, traversableResolver, constraintValidatorFactory);

		factoryBean.createInstance();
	}

	/**
	 * Null values for the {@link TraversableResolver} and {@link ConstraintValidatorFactory}
	 * <strong>should not</strong> be set onto the {@link Configuration}.
	 */
	@Test
	public void ensureNullTraversableResolverAndConstraintValidatorFactoryAreNotSet() {
		final ValidationProvider<?> provider = context.mock(ValidationProvider.class);
		final Configuration<?> configuration = context.mock(Configuration.class);

		context.checking(new Expectations() { {
			oneOf(provider).createSpecializedConfiguration(with(any(BootstrapState.class)));
			will(returnValue(configuration));

			never(configuration).traversableResolver(with(any(TraversableResolver.class)));
			never(configuration).constraintValidatorFactory(with(any(ConstraintValidatorFactory.class)));

			allowing(configuration);
		} });
		
		LocalClassLoaderValidationProviderFactoryBean factoryBean =
				new LocalClassLoaderValidationProviderFactoryBean(provider, null, null);

		factoryBean.createInstance();
	}
}
