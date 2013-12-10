package com.elasticpath.commons.util.impl;

import org.apache.log4j.Logger;

/**
 * Takes the version number and build number which Spring has injected and, on startup, logs them at the INFO level.
 */
public class VersionLogger {
	private String versionNumber;

	private String buildNumber;

	private static final Logger LOG = Logger.getLogger(VersionLogger.class);

	/**
	 * Configured in spring to be called after all the properties are set.
	 */
	public void init() {

		if (getClass().getPackage() == null || getClass().getPackage().getImplementationVersion() == null) {
			LOG.info("Commerce Version: " + versionNumber);
			LOG.info("Commerce Build Number: " + buildNumber);
		} else {
			String implVersion = getClass().getPackage().getImplementationVersion();
			String implTitle = getClass().getPackage().getImplementationTitle();
			String implVendor = getClass().getPackage().getImplementationVendor();
			LOG.info("Commerce Build Information: " + implTitle + " " + implVersion + " by " + implVendor);
		}
	}

	/**
	 * @param versionNumber The version number to output.
	 */
	public void setVersionNumber(final String versionNumber) {
		this.versionNumber = versionNumber;
	}

	/**
	 * @param buildNumber The build number to output.
	 */
	public void setBuildNumber(final String buildNumber) {
		this.buildNumber = buildNumber;
	}
}
