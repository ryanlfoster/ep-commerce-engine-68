package com.elasticpath.sfweb.util;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

/**
 * Tests for FilenameUtils.
 */
public class FilenameUtilsTest {

	private static final String FOLDER1 = "folder1";
	private static final String TEST2 = "test2";
	
	/**
	 * Tests that formPath() correctly removes duplicate slashes from the path.
	 */
	@Test
	public void testFormPath() {
		assertEquals("Filename formPath should be blank", "", FilenameUtils.formPath());
		assertEquals("Filename formPath should be folder", "folder", FilenameUtils.formPath("folder"));
		assertEquals("Filename should include 'test'", "c:" + File.separator + "test", FilenameUtils.formPath("c:/test"));
		assertEquals("Filename should include 'test1", "c:" + File.separator + "test1", FilenameUtils.formPath("", "c:/test1"));
		assertEquals("Filename should include 'test2", "path" + File.separator + TEST2, FilenameUtils.formPath("path", TEST2));
		assertEquals("Filename should include 'folder2", FOLDER1 + File.separator + "folder2" + File.separator + TEST2, 
				FilenameUtils.formPath(FOLDER1 + File.separator, "folder2", File.separator + TEST2));
		assertEquals("Filename should include 'file.txt ", FOLDER1 + File.separator + "folder3" + File.separator + "file.txt", 
				FilenameUtils.formPath(FOLDER1 + File.separator, 
						File.separator + File.separator + "folder3" + File.separator + File.separator, 
						File.separator + File.separator + File.separator + "file.txt"));
	}
}