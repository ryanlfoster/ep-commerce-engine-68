package com.elasticpath.persistence.openjpa.impl;

import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;

/**
6	 * A PersistenceUnitManager that allows overriding a same-named persistence unit in 
7	 * different <code>persistence.xml</code> files.
8	 * {@see DefaultPersistenceUnitManager#isPersistenceUnitOverrideAllowed()}
9	 */
public class OverrideAllowingPersistenceUnitManager extends DefaultPersistenceUnitManager {

	@Override
	protected boolean isPersistenceUnitOverrideAllowed() {
		return true;
	}
}
