/**
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.commons.util.capabilities;

/**
 * An object implementing this interface declares that it has Capabilities.
 */
public interface CapabilityAware {
	
	/**
	 * Get the Capabilities this instance supports.
	 * 
	 * @return The Capabilities instance.
	 */
	Capabilities getCapabilities();
}
