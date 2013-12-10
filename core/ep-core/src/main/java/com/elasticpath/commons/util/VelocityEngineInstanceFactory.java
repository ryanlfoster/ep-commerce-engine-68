package com.elasticpath.commons.util;

import org.apache.velocity.app.VelocityEngine;

/**
 * Factory class for retrieving a store specific velocity engine. 
 */
public interface VelocityEngineInstanceFactory {
	
	/**
	 * @param storeCode of the store
	 * @return velocity engine with store specific resources 
	 */
	VelocityEngine getVelocityEngine(String storeCode);
	
}
