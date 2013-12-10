package com.elasticpath.importexport.importer.types;

/**
 * Import Strategy Type.
 */
public enum ImportStrategyType {
	
	/**
	 * Type for insert or update strategy.
	 */
	INSERT_OR_UPDATE,
	
	/**
	 * Type for insert strategy.
	 */
	INSERT,
	
	/**
	 * Type for update strategy.
	 */
	UPDATE,
	
	/**
	 * Type for immutable insert strategy.
	 */
	IMMUTABLE
}
