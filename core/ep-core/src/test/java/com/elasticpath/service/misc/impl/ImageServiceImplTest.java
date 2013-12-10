package com.elasticpath.service.misc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Test <code>ImageServiceImpl</code>.
 */
public class ImageServiceImplTest {

	private static final String TEST_PNG = "test.png";
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	private AssetRepository assetRepository;

	private ImageServiceImpl imageService;
	
	/**
	 * Test that we get the configured 'no image' image when a requested image is not found.
	 */
	@Test
	public void testGetFileToDecodeDoesntExist() {
		final String imageNotAvailable = "image-not-available.jpg";
		
		ImageServiceImpl service = new ImageServiceImpl() {
			@Override
			boolean fileExists(final File file) {
				return false;
			}
			@Override
			String getNoImageFilePath() {
				return imageNotAvailable;
			}
		};
		assertEquals(imageNotAvailable, service.getFileToDecode("non-existent-file.jpg").getName());
	}
	
	/**
	 * Test that the various image extensions will be returned properly.
	 */
	@Test
	public void testGetImageExt() {
		ImageServiceImpl service = new ImageServiceImpl();
		assertEquals("jpeg", service.getImageExt("test.jpg"));
		assertEquals("jpeg", service.getImageExt("test.jpeg"));
		assertEquals("gif", service.getImageExt("test.gif"));
		assertEquals("bmp", service.getImageExt("test.bmp"));
		assertEquals("png", service.getImageExt(TEST_PNG));
		assertEquals("tiff", service.getImageExt("test.tif"));
		assertEquals("tiff", service.getImageExt("test.tiff"));
		try {
			service.getImageExt("somethingelse.something");
			fail("EpSystemException expected - unsupported file extension.");
		} catch (EpSystemException ex) {
			assertNotNull(ex);
		}
	}
	
	/**
	 * Test that the file path to the "no image" image is correctly generated.
	 */
	@Test
	public void testNoImageFilePath() {
		final String rootPath = "/home/images";
		final String noImageFileName = "notAvailable.jpg";
				
		ImageServiceImpl service = new ImageServiceImpl() {
			@Override
			public String getImagePath() {
				return rootPath;
			}
			@Override
			public String getNoImageFileName() {
				return noImageFileName;
			}
		};
		
		assertEquals(rootPath + File.separator + noImageFileName, service.getNoImageFilePath());
	}
	
	/**
	 * Test that the various image extensions are mapped to the appropriate mime types.
	 */
	@Test
	public void testMapExtToMime() {
		ImageServiceImpl service = new ImageServiceImpl();
		assertEquals("image/gif", service.mapExtToMime("gif"));
		assertEquals("image/jpg", service.mapExtToMime("jpg"));
		assertEquals("image/jpg", service.mapExtToMime("jpeg"));
		assertEquals("image/bmp", service.mapExtToMime("bmp"));
		assertEquals("image/png", service.mapExtToMime("png"));
		assertEquals("image/tiff", service.mapExtToMime("tiff"));
		assertEquals("application/octet-stream", service.mapExtToMime("unknown"));
	}
	
	/**
	 * Test that if an exception is thrown while retrieving the jpeg quality setting
	 * from the settings service, the default of 0.5 will be returned.
	 */
	@Test
	public void testGetJpegQualityDefaultsIfExceptionThrown() {
		final float defaultQuality = 0.5f;
		ImageServiceImpl service = new ImageServiceImpl();
		final double delta = .1;
		assertEquals(defaultQuality, service.getJPEGQuality(), delta);
	}
	
	/**
	 * Test that the jpeg quality factor from the settings service
	 * will be returned if the settings service throws no exception.
	 */
	@Test
	public void testGetJpegQualityRetrievesSetting() {
		final String quality = "1.0";
		final float qualityFloat = 1.0f;
		final SettingValue mockSettingValue = context.mock(SettingValue.class);
		final SettingsService mockSettingsService = context.mock(SettingsService.class);
		context.checking(new Expectations() {
			{
				allowing(mockSettingValue).getValue();
				will(returnValue(quality));

				oneOf(mockSettingsService).getSettingValue("COMMERCE/SYSTEM/IMAGES/dynamicImageSizingJpegQuality");
				will(returnValue(mockSettingValue));
			}
		});
		ImageServiceImpl service = new ImageServiceImpl();
		service.setSettingsReader(mockSettingsService);

		final double delta = 0.1;
		assertEquals(qualityFloat, service.getJPEGQuality(), delta);
	}
	

	@Before
	public void setUp() throws Exception {
		context.checking(new Expectations() {
			{
				allowing(assetRepository).getCatalogAssetPath();
				will(returnValue("."));
			}
		});

		this.imageService = new ImageServiceImpl();
		this.imageService.setAssetRepository(assetRepository);
	}

	/**
	 * Tests that if double dot path steps are in the filename then the double-dots and everything
	 * prior to them will be deleted.
	 */
	@Test
	public void testDoubleDotPathRemovedFromFilename() {
		assertEquals(TEST_PNG, imageService.normalizeFilename("assets/images/../../test.png"));
	}
	
	/**
	 * Tests that single dot path steps are removed from filenames.
	 */
	@Test
	public void testSingleDotPathRemovedFromFilename() {
		assertEquals("assets" + File.separator + "images" + File.separator + TEST_PNG, imageService.normalizeFilename("assets/images//./test.png"));
	}
	
	/**
	 * Tests that double backslash characters are removed from filenames.
	 */
	@Test
	public void testDoubleBackSlashRemovedFromFilename() {
		assertEquals("assets" + File.separator + "images" + File.separator + TEST_PNG, imageService.normalizeFilename("assets/images/\\/test.png"));
	}
}


