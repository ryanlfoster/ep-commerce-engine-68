package com.elasticpath.tools.sync.target.impl;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.support.MethodReplacer;

/**
 * Replaces the Product Dao method {@code findUidsByCategoryUids}.
 */
public class ProductDaoReplacer implements MethodReplacer {

    private Method replacedMethod = null;
    private Object invokingObject = null;
    private Set<Long> affectedCategoryUids = new HashSet<Long>();


    /**
     * Captures the {@code findUidsByCategoryUids} affected uids so that we can ignore it for the moment.
     *
     * @param obj the object.
     * @param method the UpdateLastModifiedTimes method.
     * @param args the arguments to the method (a collection of longs)
     * @return nothing.
     * @throws Throwable no exceptions thrown.
     */
	public Object reimplement(final Object obj, final Method method, final Object[] args) throws Throwable {
        setReplacedMethod(method);
        setInvokingObject(obj);

		if (!(args[0] instanceof Collection)) {
			throw new IllegalArgumentException("args[0] must be a Collection<Long>");
		}

		@SuppressWarnings("unchecked")
		final Collection<Long> input = (Collection<Long>) args[0];

		getAffectedCategoryUids().addAll(input);

        return Collections.emptyList();
    }

    /**
     *
     * @return the replacedMethod
     */
    public Method getReplacedMethod() {
        return replacedMethod;
    }

    /**
     *
     * @param replacedMethod the replacedMethod to set
     */
    public void setReplacedMethod(final Method replacedMethod) {
        this.replacedMethod = replacedMethod;
    }

       /**
     *
     * @return the invokingObject
     */
    public Object getInvokingObject() {
        return invokingObject;
    }

    /**
     *
     * @param invokingObject the invokingObject to set
     */
    public void setInvokingObject(final Object invokingObject) {
        this.invokingObject = invokingObject;
    }

    /**
     *
     * @return the affectedCategoryUids
     */
    public Set<Long> getAffectedCategoryUids() {
        return affectedCategoryUids;
    }

    /**
     *
     * @param affectedCategoryUids the affectedCategoryUids to set
     */
    public void setAffectedCategoryUids(final Set<Long> affectedCategoryUids) {
        this.affectedCategoryUids = affectedCategoryUids;
    }


}
