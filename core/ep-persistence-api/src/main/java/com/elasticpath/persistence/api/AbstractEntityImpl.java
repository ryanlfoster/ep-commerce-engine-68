/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.persistence.api;

/**
 * The default implementation of <code>Entity</code>.
 */
public abstract class AbstractEntityImpl extends AbstractPersistableImpl implements Entity {

	/** GUID field length for persistence. */
	public static final int GUID_LENGTH = 64;

	private static final long serialVersionUID = 5000000001L;

	/**
	 * Set default values for those fields need default values.
	 */
	@Override
	public void initialize() {
		initializeGuid();
	}

	/**
	 * Initializes the GUID.
	 */
	protected void initializeGuid() {
		EntityUtils.initializeGuid(this);
	}

	@Override
	public boolean equals(final Object other) {
		return EntityUtils.equals(this, other);
	}

	@Override
	public int hashCode() {
		return EntityUtils.hashCode(this);
	}

}
