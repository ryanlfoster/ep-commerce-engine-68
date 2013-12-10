/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.sfweb.view.velocity;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;

import com.elasticpath.commons.util.InvalidatableCache;
import com.elasticpath.service.catalogview.StoreConfig;

/**
 * <p>
 * Velocity configurer that is store aware.
 * </p>
 * <p>
 * Manages velocity engines for each store. The first time a velocity engine for a particular store is requested it will be created.
 * </p>
 * <p>
 * NOTE: careful synchronization needs to be done around the velocity engine creation. Different stores can create velocity engines in parallel
 * without issues. However, if two threads accessing the same store try to create velocity engines in parallel then deadlocks can occur. To avoid
 * this, per-store engine creation lock objects are used.
 * </p>
 */
public class StoreVelocityConfigurer extends VelocityConfigurer implements InvalidatableCache {

	private static final Logger LOG = Logger.getLogger(StoreVelocityConfigurer.class);

	private Map<String, VelocityEngine> engineMap = new ConcurrentHashMap<String, VelocityEngine>();

	private final Map<String, Object> engineCreationLockMap = Collections.synchronizedMap(new HashMap<String, Object>());

	private StoreConfig storeConfig;

	/**
	 * Return a new velocity engine for each store.
	 *
	 * @return a velocity engine
	 */
	@Override
	public VelocityEngine getVelocityEngine() {
		final String storeCode = getStoreConfig().getStoreCode();
		return getInternalVelocityEngine(storeCode);
	}

	/**
	 * Gets the map of store code to Velocity engines.
	 *
	 * @return map of Velocity engines
	 */
	protected Map<String, VelocityEngine> getEngineMap() {
		return engineMap;
	}

	/**
	 * Internal implementation of mechanism to retrieve the velocity engine corresponding to a store code.
	 *
	 * @param storeCode the key to retrieving the velocity engine.
	 * @return a velocity engine specifically for the store
	 */
	protected VelocityEngine getInternalVelocityEngine(final String storeCode) {
		VelocityEngine engine = engineMap.get(storeCode);
		if (engine != null) {
			return engine;
		}

		// Set up per-store engine creation lock
		synchronized (this) {
			if (!engineCreationLockMap.containsKey(storeCode)) {
				engineCreationLockMap.put(storeCode, new Object());
			}
		}

		// Create the velocity engines - several stores could have their
		// engines created at one time.
		synchronized (engineCreationLockMap.get(storeCode)) {
			engine = engineMap.get(storeCode);
			if (engine == null) {
				engine = createVelocityEngineInternal();
				engineMap.put(storeCode, engine);
			}
		}
		return engine;
	}


	/**
	 * Recreate the velocity engines to wipe out any cached templates/macros.
	 * New engine will be created on next access.
	 * The resource manager will correctly load VM_global_library files
	 *
	 */
	public void invalidate() {
		synchronized (this) {
			engineMap = new ConcurrentHashMap<String, VelocityEngine>();
		}
	}

	/**
	 * Internal implementation of mechanism to create a new velocity engine.
	 *
	 * @return a new velocity engine
	 */
	protected VelocityEngine createVelocityEngineInternal() {
		VelocityEngine engine = null;
		try {
			engine = createVelocityEngine();
		} catch (VelocityException e) {
			LOG.error("Error creating velocity engine", e);
		} catch (IOException e) {
			LOG.error("Error creating velocity engine", e);
		}
		return engine;
	}

	/**
	 * Get the store configuration.
	 *
	 * @return the storeConfig
	 */
	public StoreConfig getStoreConfig() {
		return storeConfig;
	}

	/**
	 * Set the store configuration object.
	 *
	 * @param storeConfig the storeConfig to set
	 */
	public void setStoreConfig(final StoreConfig storeConfig) {
		this.storeConfig = storeConfig;
	}

	/**
	 * The default implementation creates a new velocity engine,
	 * but we only want to do this explicitly.
	 * Does nothing right now.
	 */
	@Override
	public void afterPropertiesSet() {
		// Does nothing right now.
	}

	/**
	 * {@inheritDoc}
	 * Does nothing right now.
	 */
	public void invalidate(final Object objectUid) {
		// Does nothing right now.
	}
}