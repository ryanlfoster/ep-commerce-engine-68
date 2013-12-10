package com.elasticpath.domain.factory;

import java.util.UUID;

/**
 * Used to generate a GUID by the other factories (so it's only done in 1 place).
 */
public final class TestGuidUtility {
	
	private TestGuidUtility() {
		
	}

	/**
	 * Returns a new GUID.
	 *
	 * @return new GUID.
	 */
	public static String getGuid() {
		final UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
	
}
