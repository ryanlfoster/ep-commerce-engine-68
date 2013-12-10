package com.elasticpath.sfweb.util;

import java.io.File;

/**
 * General file path manipulation utilities.
 * This class was created to extract a utility method that was similar to, but not exactly like,
 * the methods in the Apache Commons class of the same name.
 */
@SuppressWarnings({ "PMD.UseSingleton" })
public final class FilenameUtils {

	/**
	 * Forms a string from the path nodes provided. They should be
	 * in the order that is required for them to be concatenated.
	 *
	 * @param pathNodes the path array containing the path nodes. they may or may not be with 
	 * 		  separators at the beginning or at the end
	 * @return a normalized path constructed from the pathNodes separated by File.separator
	 */
	public static String formPath(final String... pathNodes) {
		StringBuilder fullPath = new StringBuilder();
		for (String pathNode : pathNodes) {
			if (pathNode.length() != 0) {
				fullPath.append(pathNode);
				fullPath.append(File.separator);
			}
		}
		// java.io.File normalizes the path so that it does not have something like 
		// double or triple slashes/backslashes (depending on the operating system)
		return new File(fullPath.toString()).getPath();
	}
}
