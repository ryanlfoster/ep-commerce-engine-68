/**
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.commons.util.capabilities;

import java.io.Serializable;

import com.elasticpath.commons.util.extenum.ExtensibleEnum;

/**
 * Contains a set of ExtensibleEnums which are supported by the owner of this instance.
 */
public interface Capabilities extends Serializable {
	
	/**
	 * Determines if this instance supports all of the given ExtensibleEnums.
	 * 
	 * @param desiredCapabilities Can be null, empty, or a list of desired ExtensibleEnums.
	 * @return true if the desiredCapabilities parameter is null, empty, or all of them are supported by this instance.
	 */
	boolean supports(ExtensibleEnum... desiredCapabilities);
}
