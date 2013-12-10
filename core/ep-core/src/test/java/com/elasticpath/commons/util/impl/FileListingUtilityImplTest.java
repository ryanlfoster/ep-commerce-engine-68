/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileType;


/**
 * Verify the functionality of the methods provided by <code>VfsFileSystemManagerImpl</code>.
 */
public class FileListingUtilityImplTest {

	private static final String TEST_FILE_FOLDER = "/test/folder";

	private static final String TEST_FILE_NAME = "testFile";

	private static final String TEST_TEMPLATE_FILE_NAME = "testFile.vm";

	private static final String TEST_PROPERTIES_FILE_NAME = "testFile.properties";

	private static final String TEMPLATE_MATCH_PATTERN = "(.*).vm"; //$NON-NLS-1$

	private FileListingUtilityImpl fileListingUtility;

	private VfsFileSystemManagerImpl fsManager;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private FileSystemManager mockFileSystemManager;

	/**
	 * Set up mock objects etc required for the test.
	 * 
	 * @throws Exception if an error occurs during setup
	 */
	@Before
	public void setUp() throws Exception {
		mockFileSystemManager = context.mock(FileSystemManager.class);
		fileListingUtility = new FileListingUtilityImpl();

	}

	/**
	 * Test finding folders in a given root path.
	 */
	@Test
	public void testFindFolders() {
		
		// Mock out the VFS FileSystemManager class as we don't actually want to hit the filesystem
		// Have configureSftp throw an exception for exception testing
		fsManager = new VfsFileSystemManagerImpl() {
			@Override
			protected FileSystemManager getFileSystemManager() throws FileSystemException {
				return mockFileSystemManager;
			}
			
			public FileObject resolveRelativeFile(final String fileName) throws FileSystemException {
				//Create the mock objects needed
				final FileObject mockFileObject = context.mock(FileObject.class, "file");
				final FileObject mockFolderObject = context.mock(FileObject.class, "folder");
				final FileName mockFileName = context.mock(FileName.class);
				final FileObject[] mockFolderObjects = new FileObject[] { mockFolderObject };
				
				context.checking(new Expectations() {
					{
						//Set expectations on the mock file name
						oneOf(mockFileName).getBaseName();
						will(returnValue(TEST_FILE_NAME));

						//Set expectations on the mock folder object
						oneOf(mockFolderObject).getName();
						will(returnValue(mockFileName));

						oneOf(mockFolderObject).getType();
						will(returnValue(FileType.FOLDER));

						//Set expectations on the mock file object
						oneOf(mockFileObject).exists();
						will(returnValue(true));

						oneOf(mockFileObject).getType();
						will(returnValue(FileType.FOLDER));

						//Create a mock folderObjects and set expectations on it then return the mock file object

						oneOf(mockFileObject).getChildren();
						will(returnValue(mockFolderObjects));
					}
				});
				return mockFileObject;
				
			}
			
			public boolean initialize() {
				return true;
			}
			
			@Override
			protected void configureSftp() throws FileSystemException {
				throw new FileSystemException("testException");
			}

		};
		fileListingUtility.setFileSystemManager(fsManager);
		
		List<String> folderNames = fileListingUtility.findFolderNames(TEST_FILE_FOLDER);
		assertEquals(1, folderNames.size());
		assertEquals(TEST_FILE_NAME, folderNames.get(0));
	}

	/**
	 * Test finding file names in a given root path.
	 */
	@Test
	public void testFindFileNames() {
		
		// Mock out the VFS FileSystemManager class as we don't actually want to hit the filesystem
		// Have configureSftp throw an exception for exception testing
		fsManager = new VfsFileSystemManagerImpl() {
			@Override
			protected FileSystemManager getFileSystemManager() throws FileSystemException {
				return mockFileSystemManager;
			}
			
			public FileObject resolveRelativeFile(final String fileName) throws FileSystemException {
				
				//Create mock objects
				final FileObject mockFileObject = context.mock(FileObject.class, "file");
				final FileObject mockTemplateObject = context.mock(FileObject.class, "template");
				final FileObject mockPropertiesObject = context.mock(FileObject.class, "properties");
				final FileName mockTemplateFileName = context.mock(FileName.class, "template filename");
				final FileName mockPropertiesFileName = context.mock(FileName.class, "properties filename");
				final FileObject[] mockFolderObjects = new FileObject[] { mockTemplateObject, mockPropertiesObject };
				
				context.checking(new Expectations() {
					{
						//Set expectations on the mock template object
						oneOf(mockTemplateObject).getName();
						will(returnValue(mockTemplateFileName));

						oneOf(mockTemplateObject).getType();
						will(returnValue(FileType.FILE));

						//Set expectations on the mock template file name
						oneOf(mockTemplateFileName).getBaseName();
						will(returnValue(TEST_TEMPLATE_FILE_NAME));

						//Set expectations on the mock properties object
						oneOf(mockPropertiesObject).getName();
						will(returnValue(mockPropertiesFileName));

						oneOf(mockPropertiesObject).getType();
						will(returnValue(FileType.FILE));

						oneOf(mockPropertiesFileName).getBaseName();
						will(returnValue(TEST_PROPERTIES_FILE_NAME));

						//Set expectations on the mock file object
						oneOf(mockFileObject).exists();
						will(returnValue(true));

						oneOf(mockFileObject).getType();
						will(returnValue(FileType.FOLDER));

						//Create mock folder objects and set expectations, then finally return the mock file object
						oneOf(mockFileObject).getChildren();
						will(returnValue(mockFolderObjects));
					}
				});
				return mockFileObject;
				
			}
			
			public boolean initialize() {
				return true;
			}
			
			@Override
			protected void configureSftp() throws FileSystemException {
				throw new FileSystemException("testException");
			}

		};
		fileListingUtility.setFileSystemManager(fsManager);
		
		List<String> fileNames = fileListingUtility.findFileNames(TEST_FILE_FOLDER, TEMPLATE_MATCH_PATTERN, true);
		assertEquals(1, fileNames.size());
		assertEquals(TEST_TEMPLATE_FILE_NAME, fileNames.get(0));
	}
}
