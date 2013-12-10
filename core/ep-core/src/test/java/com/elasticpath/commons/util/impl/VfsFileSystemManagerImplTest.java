/*
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.vfs.FileName;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileSystemOptions;

import com.elasticpath.domain.FileSystemConnectionInfo;

/**
 * Verify the functionality of the methods provided by <code>VfsFileSystemManagerImpl</code>.
 */
public class VfsFileSystemManagerImplTest {

	private static final String FTP = "ftp";

	private static final String FTP_PREFIX = "ftp://";

	private static final String TEST_URI = "testURI";

	private static final String TEST_FILE_NAME = "testFile";

	private static final String ROOT = "root";

	private VfsFileSystemManagerImpl fsManager;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private FileSystemConnectionInfo mockFileSystemConnectionInfo;
	
	private FileSystemManager mockFileSystemManager;
	
	private FileObject mockFileObject;
	
	private FileName mockFileName;
	
	/**
	 * Set up mock objects etc required for the test.
	 * 
	 * @throws Exception if an error occurs during setup
	 */
	@Before
	public void setUp() throws Exception {
		mockFileSystemManager = context.mock(FileSystemManager.class);
		
		// Mock out the VFS FileSystemManager class as we don't actually want to hit the filesystem
		// Have configureSftp throw an exception for exception testing
		fsManager = new VfsFileSystemManagerImpl() {
			@Override
			protected FileSystemManager getFileSystemManager()
					throws FileSystemException {
				return mockFileSystemManager;
			}

			@Override
			protected void configureSftp() throws FileSystemException {
				throw new FileSystemException("testException");
			}
			
		};
		
		mockFileSystemConnectionInfo = context.mock(FileSystemConnectionInfo.class);
		mockFileObject = context.mock(FileObject.class);
		mockFileName = context.mock(FileName.class);
	}

	/**
	 * Test the initialisation of the file system manager with no connection info fails.
	 */
	@Test
	public void testInitializeNoConnectionInfo() {
		fsManager.setConnectionInfo(null);
		boolean result = fsManager.initialize();
		assertFalse("The initialization should have failed without connection info", result);
	}
	
	/**
	 *  Test the initialisation is successful for a protocol like ftp.
	 */
	@Test
	public void testInitializeFTP() {
		context.checking(new Expectations() {
			{
				oneOf(mockFileSystemConnectionInfo).getUserName();
				will(returnValue(null));

				oneOf(mockFileSystemConnectionInfo).getPassword();
				will(returnValue(null));

				oneOf(mockFileSystemConnectionInfo).getProtocol();
				will(returnValue(FTP));
			}
		});
		
		fsManager.setConnectionInfo(mockFileSystemConnectionInfo);
		boolean result = fsManager.initialize();
		assertTrue("The initialization should work when connection info is given", result);
	}

	/**
	 *  Test the initialisation handles exceptions.
	 */
	@Test
	public void testInitializeWithException() {
		context.checking(new Expectations() {
			{
				oneOf(mockFileSystemConnectionInfo).getUserName();
				will(returnValue(null));

				oneOf(mockFileSystemConnectionInfo).getPassword();
				will(returnValue(null));

				exactly(2).of(mockFileSystemConnectionInfo).getProtocol();
				will(returnValue("sftp"));
			}
		});
		fsManager.setConnectionInfo(mockFileSystemConnectionInfo);
		boolean result = fsManager.initialize();
		assertFalse("The initialization should fail if an exception is encountered", result);
		assertNotNull("There should have been an error reported", fsManager.getError());
	}

	/**
	 * Test setting and getting the connection info.
	 */
	@Test
	public void testSetAndGetConnectionInfo() {
		FileSystemConnectionInfo connectionInfo = mockFileSystemConnectionInfo;
		fsManager.setConnectionInfo(connectionInfo);
		assertSame("get should return the info we set", connectionInfo, fsManager.getConnectionInfo());
	}

	/**
	 * Test reporting and getting an error (exception).
	 */
	@Test
	public void testReportAndGetError() {
		final String message = "test message";
		fsManager.reportError(message, null);
		assertEquals("The returned error should be the one we reported", message, fsManager.getError().getMessage());
	}

	/**
	 * Test resolving a file given the name.
	 */
	@Test
	public void testResolveFileByName() throws Exception {
		final String filename = TEST_FILE_NAME;
		context.checking(new Expectations() {
			{
				oneOf(mockFileSystemManager).resolveFile(with(filename), with(aNull(FileSystemOptions.class)));
				will(returnValue(null));
			}
		});
		try {
			fsManager.resolveFile(filename);
		} catch (FileSystemException e) {
			fail("An exception should not have occurred");
		}
	}

	/**
	 * Test resolving a file relative to the given path.
	 */
	@Test
	public void testResolveFileRelativeToPath() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(mockFileName).getURI();
				will(returnValue(TEST_URI));

				oneOf(mockFileObject).getName();
				will(returnValue(mockFileName));

				oneOf(mockFileSystemManager).resolveFile(TEST_URI + FileName.SEPARATOR + TEST_FILE_NAME, null);
				will(returnValue(null));
			}
		});
		try {
			fsManager.resolveFile(mockFileObject, TEST_FILE_NAME);
		} catch (FileSystemException e) {
			fail("An exception should not have occurred");
		}
	}

	/**
	 * Test resolving a file relative to the root path.
	 */
	@Test
	public void testResolveRelativeFile() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(mockFileSystemConnectionInfo).getProtocol();
				will(returnValue(FTP));

				oneOf(mockFileSystemConnectionInfo).getHost();
				will(returnValue(null));

				oneOf(mockFileSystemConnectionInfo).getRootPath();
				will(returnValue(ROOT));
			}
		});
		fsManager.setConnectionInfo(mockFileSystemConnectionInfo);
		context.checking(new Expectations() {
			{

				oneOf(mockFileName).getURI();
				will(returnValue(TEST_URI));

				oneOf(mockFileObject).getName();
				will(returnValue(mockFileName));

				oneOf(mockFileObject).exists();
				will(returnValue(true));

				oneOf(mockFileSystemManager).resolveFile(FTP_PREFIX, null);
				will(returnValue(mockFileObject));
			}
		});
		
		final FileObject mockFileObject2 = context.mock(FileObject.class, "second file object");
		context.checking(new Expectations() {
			{
				oneOf(mockFileObject2).resolveFile(TEST_FILE_NAME);
				will(returnValue(null));

				oneOf(mockFileSystemManager).resolveFile(TEST_URI + FileName.SEPARATOR + ROOT, null);
				will(returnValue(mockFileObject2));
			}
		});
		try {
			fsManager.resolveRelativeFile(TEST_FILE_NAME);
		} catch (FileSystemException e) {
			fail("An exception should not have occurred");
		}
	}

	/**
	 * Test getting the root path.
	 */
	@Test
	public void testGetRootPath() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(mockFileSystemConnectionInfo).getProtocol();
				will(returnValue(FTP));

				oneOf(mockFileSystemConnectionInfo).getHost();
				will(returnValue(null));

				oneOf(mockFileSystemConnectionInfo).getRootPath();
				will(returnValue(ROOT));
			}
		});
		fsManager.setConnectionInfo(mockFileSystemConnectionInfo);
		context.checking(new Expectations() {
			{

				oneOf(mockFileName).getURI();
				will(returnValue(TEST_URI));

				oneOf(mockFileObject).getName();
				will(returnValue(mockFileName));

				oneOf(mockFileObject).exists();
				will(returnValue(true));

				oneOf(mockFileSystemManager).resolveFile(FTP_PREFIX, null);
				will(returnValue(mockFileObject));
			}
		});

		assertEquals("Root path should be as expected", TEST_URI + FileName.SEPARATOR + ROOT, fsManager.getRootPath());
	}
	
	/**
	 * Test getting the root path where the connection has a host and a path with trailing slash.
	 */
	@Test
	public void testGetRootPathWithHostAndSlash() throws Exception {
		final int port = 21;
		context.checking(new Expectations() {
			{
				oneOf(mockFileSystemConnectionInfo).getProtocol();
				will(returnValue(FTP));

				exactly(2).of(mockFileSystemConnectionInfo).getHost();
				will(returnValue("localhost"));

				exactly(2).of(mockFileSystemConnectionInfo).getPort();
				will(returnValue(port));

				oneOf(mockFileSystemConnectionInfo).getRootPath();
				will(returnValue(ROOT + FileName.SEPARATOR));
			}
		});
		fsManager.setConnectionInfo(mockFileSystemConnectionInfo);
		context.checking(new Expectations() {
			{

				oneOf(mockFileName).getURI();
				will(returnValue(TEST_URI));

				oneOf(mockFileObject).getName();
				will(returnValue(mockFileName));

				oneOf(mockFileObject).exists();
				will(returnValue(true));

				oneOf(mockFileSystemManager).resolveFile("ftp://localhost:21", null);
				will(returnValue(mockFileObject));
			}
		});

		assertEquals("Root path should be as expected", TEST_URI + FileName.SEPARATOR + ROOT, fsManager.getRootPath());
	}
	
	/**
	 * Test exception handling of getRootPath.
	 */
	@Test
	public void testGetRootPathException() throws Exception {
		context.checking(new Expectations() {
			{
				oneOf(mockFileSystemConnectionInfo).getProtocol();
				will(returnValue(FTP));

				oneOf(mockFileSystemConnectionInfo).getHost();
				will(returnValue(null));

				oneOf(mockFileSystemConnectionInfo).getRootPath();
				will(returnValue(ROOT));
			}
		});
		fsManager.setConnectionInfo(mockFileSystemConnectionInfo);
		context.checking(new Expectations() {
			{

				oneOf(mockFileObject).exists();
				will(throwException(new FileSystemException("testException")));

				oneOf(mockFileSystemManager).resolveFile(FTP_PREFIX, null);
				will(returnValue(mockFileObject));
			}
		});

		assertEquals("Root path should be as expected", FTP_PREFIX + FileName.SEPARATOR + ROOT, fsManager.getRootPath());
	}
	
	/**
	 * Test getting the full path of a given file that is relative to a given path.
	 */
	@Test
	public void testFullPathOfRelativeFile() {
		context.checking(new Expectations() {
			{
				oneOf(mockFileName).getURI();
				will(returnValue(TEST_URI));

				oneOf(mockFileObject).getName();
				will(returnValue(mockFileName));
			}
		});
		String fullpath = fsManager.fullPathOfRelativeFile(mockFileObject, TEST_FILE_NAME);
		assertEquals("Full path should be as expected", TEST_URI + FileName.SEPARATOR + TEST_FILE_NAME, fullpath);
	}

	/**
	 * Test appending the given filename to the given path where neither contain slashes.
	 */
	@Test
	public void testAppendFileNameToPathWithSlashes() {
		String result = fsManager.appendFileNameToPath(FileName.SEPARATOR + TEST_FILE_NAME, TEST_URI + FileName.SEPARATOR);
		assertEquals("Full path should be as expected", TEST_URI + FileName.SEPARATOR + TEST_FILE_NAME, result);
		result = fsManager.appendFileNameToPath(FileName.SEPARATOR + TEST_FILE_NAME, TEST_URI);
		assertEquals("Full path should be as expected", TEST_URI + FileName.SEPARATOR + TEST_FILE_NAME, result);
	}

}
