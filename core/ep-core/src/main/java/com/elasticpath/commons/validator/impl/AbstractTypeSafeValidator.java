package com.elasticpath.commons.validator.impl;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Type-safe validator extending Spring {@link Validator}.
 * @param <T> the type to validate
 */
public abstract class AbstractTypeSafeValidator<T> implements Validator {

    @Override
    @SuppressWarnings("unchecked")
    public void validate(final Object target, final Errors errors) {
        if (getTargetType().isAssignableFrom(target.getClass())) {
            validateInternal((T) target, errors);
        } else {
            errors.reject("Expected object of type " + getTargetType().getName() + " but was " + target.getClass().getName());
        }
    }
    
    @Override
    public boolean supports(final Class< ? > clazz) {
        return getTargetType().equals(clazz);
    }
    
    /**
     * Get the class that this validator validates.
     * @return the {@link Class}
     */
    protected abstract Class<? extends T> getTargetType();
    
    /**
     * Validate target.
     * @see #validate(Object, Errors)
     * @param target the object to validate
     * @param errors contextual state about the validation process
     */
    protected abstract void validateInternal(T target, Errors errors);

}
