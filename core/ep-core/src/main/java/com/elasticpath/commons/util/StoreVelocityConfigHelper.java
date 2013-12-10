package com.elasticpath.commons.util;

import java.io.File;

/**
 * Helper class for constructing velocity template paths.
 */
public final class StoreVelocityConfigHelper {

	private static final char FILE_SEPARATOR = File.separatorChar;

	private static final String DEFAULT_STORE_DIR = "default";

	// TODO: should not hard code this directory. inject or put in settings frwk
	private static final String VELOCITY_TEMPLATES_DIR = "templates" + File.separator + "velocity";
	
	private StoreVelocityConfigHelper() {
		//Singleton
	}
	/**
	 * Helper method to construct a store specific velocity resource path containing theme and store code.
	 * TODO: move this to settings framework
	 * 
	 * @param resourceName base name of the resource
	 * @param theme the theme name
	 * @param storeCode the store code
	 * @return a path to the resource relative to the store assets directory
	 */
	public static String getStoreSpecificResourcePath(final String resourceName, final String theme, final String storeCode) {
		final StringBuffer storeSpecificResourcePath = new StringBuffer();
		storeSpecificResourcePath.append(theme).append(FILE_SEPARATOR);
		storeSpecificResourcePath.append(storeCode).append(FILE_SEPARATOR);
		storeSpecificResourcePath.append(VELOCITY_TEMPLATES_DIR).append(FILE_SEPARATOR);
		storeSpecificResourcePath.append(resourceName);
		return storeSpecificResourcePath.toString();
	}

	/**
	 * Helper method to construct a velocity resource path for within a theme and default store.
	 * TODO: move this to settings framework
	 * 
	 * @param resourceName the base name of the resource
	 * @param theme the theme name
	 * @return a path to the resource relative to the store assets directory
	 */
	public static String getDefaultResourcePath(final String resourceName, final String theme) {
		final StringBuffer defaultResourcePath = new StringBuffer();
		defaultResourcePath.append(theme).append(FILE_SEPARATOR);
		defaultResourcePath.append(DEFAULT_STORE_DIR).append(FILE_SEPARATOR);
		defaultResourcePath.append(VELOCITY_TEMPLATES_DIR).append(FILE_SEPARATOR);
		defaultResourcePath.append(resourceName);
		return defaultResourcePath.toString();
	}

	/**
	 * Get path for CM Specific resources.
	 * 
	 * @param resourceName the base name of the resource
	 * @param cmAssetsDir the cm assets directory
	 * @return This returns the cm resources path.
	 */
	public static String getCMResourcePath(final String resourceName, final String cmAssetsDir) {
		final StringBuffer defaultResourcePath = new StringBuffer();
		defaultResourcePath.append(cmAssetsDir).append(File.separator);
		defaultResourcePath.append(VELOCITY_TEMPLATES_DIR).append(File.separator);
		defaultResourcePath.append(resourceName);
		return defaultResourcePath.toString();
	}

}
