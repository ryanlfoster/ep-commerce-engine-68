package com.elasticpath.validation.bootstrap;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Configuration;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.TraversableResolver;
import javax.validation.Validation;
import javax.validation.ValidationProviderResolver;
import javax.validation.Validator;
import javax.validation.bootstrap.ProviderSpecificBootstrap;
import javax.validation.spi.ValidationProvider;

import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * FactoryBean that creates a new {@link Validator} after bootstrapping javax.validation
 * with a custom {@link ValidationProviderResolver}, and configuring it further with the
 * provided TraversableResolver and ConstraintValidatorFactory.
 * 
 * This class is created specifically to support resolution of a custom
 * {@link javax.validation.ValidatorFactory} using the current classloader rather than the
 * default Java SPI behaviour. As support for this functionality is not provided by Spring's
 * {@org.springframework.validation.beanvalidation.LocalValidatorFactoryBean}
 * 
 */
public class LocalClassLoaderValidationProviderFactoryBean extends AbstractFactoryBean<Validator> {
	private final ValidationProvider<?> provider; // Class<U> provider;
	private final TraversableResolver traversableResolver;
	private final ConstraintValidatorFactory constraintValidatorFactory;

	/**
	 * Creates a new LocalClassLoaderValidationProviderFactoryBean with the specified
	 * ValidationProviderResolver, TraversableResolver, and ConstraintValidatorFactory.
	 *
	 * @param provider a specific implementation of Provider to use for validation 
	 * @param traversableResolver the TraversableResolver to provide to the {@Configuration} instance. May be null.
	 * @param constraintValidatorFactory the ConstraintValidatorFactory to provide to the {@Configuration} instance. May be null.
	 */
	public LocalClassLoaderValidationProviderFactoryBean(
			final ValidationProvider<?> provider,
			final TraversableResolver traversableResolver,
			final ConstraintValidatorFactory constraintValidatorFactory) {
		super();
		if (null == provider) {
			throw new IllegalArgumentException("ValidationProvider must be specified but is null");
		}
		this.provider = provider;
		this.traversableResolver = traversableResolver;
		this.constraintValidatorFactory = constraintValidatorFactory;
	}

	@Override
	public Class<Validator> getObjectType() {
		return Validator.class;
	}

	@Override
	protected Validator createInstance() {
		// Need to suppress because Object.getClass()'s declared return type is <?>
		@SuppressWarnings("rawtypes")
		Class clazz = provider.getClass();
		
		@SuppressWarnings("unchecked")
		ProviderSpecificBootstrap<?> providerSpecificBootstrap = Validation.byProvider(clazz);

		providerSpecificBootstrap.providerResolver(createValidationProviderResolver(provider));

		Configuration<?> configuration = providerSpecificBootstrap.configure();

		if (null != traversableResolver) {
			configuration.traversableResolver(traversableResolver);
		}
		if (null != constraintValidatorFactory) {
			configuration.constraintValidatorFactory(constraintValidatorFactory);
		}

		return configuration.buildValidatorFactory().getValidator();
	}

	/**
	 * Creates an implementation of ValidationProviderResolver that resolves to a list containing
	 * just a single ValidationProvider.
	 *
	 * @param provider the provider that should be included in the list returned by the resolver
	 * @return the new ValidationProviderResolver
	 */
	ValidationProviderResolver createValidationProviderResolver(final ValidationProvider<?> provider) {
		return new ValidationProviderResolver() {
			@Override
			public List<ValidationProvider<?>> getValidationProviders() {
				List<ValidationProvider<?>> validationProviders = new ArrayList<ValidationProvider<?>>(1);
				validationProviders.add(provider);
				return validationProviders;
			}
		};
	}
}
