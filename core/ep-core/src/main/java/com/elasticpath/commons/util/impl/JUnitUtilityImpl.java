/*
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.commons.util.impl;


/**
 * Utility methods used only in JUnit tests.
 * 
 * @deprecated since 6.4 - load resources from the filesystem or the classpath. As of 6.4, core no longer has a WEB-INF directory.
 */
@Deprecated
public class JUnitUtilityImpl {

	private static final JUnitUtilityImpl INSTANCE = new JUnitUtilityImpl();

	/**
	 * Return the absolute WEB-INF directory path.
	 *
	 * @return the absolute WEB-INF directory path
	 */
	public String getWebInfPath() {
		return "target/classes";
	}

	public String getTestClassesPath() {
		return "target/test-classes";
	}
	

	/**
	 * Return the singleton <code>JUnitUtilityImpl</code>.
	 *
	 * @return the singleton <code>JUnitUtilityImpl</code>
	 */
	public static JUnitUtilityImpl getInstance() {
		return INSTANCE;
	}
}
