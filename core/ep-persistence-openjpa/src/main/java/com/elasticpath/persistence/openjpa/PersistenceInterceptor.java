/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.openjpa;

/**
 * The purpose of this interface is to introduce the executeBeforePersistAction(). This needs to be revisited as simple domain objects should not
 * be aware of precondition actions that are required to persist them. This method should be moved out of the domain and into the DAOs or Services.
 */
public interface PersistenceInterceptor {
	/**
	 * Interceptor to perform tasks before persisting changes.
	 */
	void executeBeforePersistAction();
}
