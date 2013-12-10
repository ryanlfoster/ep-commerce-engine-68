package com.elasticpath.testutility;

import java.io.File;
import java.net.URL;

/**
 * Utility for junit tests.
 */
public final class JUnitUtil {

	private static final String IMPORTEXPORT = "importexport";
	
	private JUnitUtil() {
		//empty constructor.
	}
	
	/**
	 * Gets absolute path for importexport project.
	 * 
	 * @return the absolute path
	 */
	public static String getAbsolutePath() {
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final String utilityClassName = JUnitUtil.class.getName();
		final String utilityFileName = utilityClassName.replace('.', '/') + ".class";
		final URL utilityUrl = classLoader.getResource(utilityFileName);
		String utilityUri = utilityUrl.getFile();

		String absolutePath = utilityUri.substring(0, utilityUri.lastIndexOf(IMPORTEXPORT) + IMPORTEXPORT.length());
		final File file = new File(absolutePath);
		return file.getAbsolutePath();
	}
	
}
