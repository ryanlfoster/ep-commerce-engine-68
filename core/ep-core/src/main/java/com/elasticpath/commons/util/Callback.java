package com.elasticpath.commons.util;

/**
 * Callback, similar to {@link Callable}, but does not throw exception.
 * @param <T> type to return
 */
public interface Callback<T> {

    /**
     * Similar to {@link Callable#call()}, but does not declare any checked exceptions.
     * @return the result
     */
    T callback();
}
