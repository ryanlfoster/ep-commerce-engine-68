/**
 * 
 */
package com.elasticpath.commons.util.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.commons.io.FilenameUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.ElasticPath;
import com.elasticpath.settings.SettingsReader;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingValue;


/**
 * Tests for AssetRepositoryImpl.
 */
public class AssetRepositoryImplTest {
	private static final String ASSET_SERVER_BASE_URL_SETTING = "COMMERCE/STORE/ASSETS/assetServerBaseUrl";
	private static final String ASSET_SERVER_BASE_URL = "http://host:8080/assetcontext";
	private static final String STORE_CODE = "aStoreCode";
	private AssetRepositoryImpl repository;
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	private SettingsReader settingsReader;

	/**
	 * Runs before every test case.
	 */
	@Before
	public void setUp() {
		repository = new AssetRepositoryImpl();
		settingsReader = context.mock(SettingsReader.class);
		repository.setSettingsReader(settingsReader);
	}

	/**
	 * Created to understand the behavior of the getCatalogAssetPath method.
	 */
	@Test
	public void testGetCatalogAssetPathWindowsAbsolutePath() {
		repository = new AssetRepositoryImpl() {
			@Override
			boolean isAbsolute(final String path) {
				// Allow the unit test to run on different platforms
				return true;
			}

			@Override
			String getCatalogAssetPathFromSettingsService() {
				return "c:\\assets";
			}
		};
		assertEquals("c:\\assets", repository.getCatalogAssetPath());
	}

	/**
	 * Test to understand the behaviour of the getCatalogAssetPath method.
	 */
	@Test
	public void testGetCatalogAssetPathWindowsRelativePath() {
		final ElasticPath elasticPath = context.mock(ElasticPath.class);
		context.checking(new Expectations() { {
			oneOf(elasticPath).getWebInfPath(); will(returnValue("c:\\servletbasedir\\WEB-INF"));
		} });
		repository = new AssetRepositoryImpl() {
			@Override
			boolean isAbsolute(final String path) {
				// Allow the unit test to run on different platforms
				return false;
			}

			@Override
			String getCatalogAssetPathFromSettingsService() {
				return "assets";
			}
		};
		repository.setElasticPath(elasticPath);
		assertEquals("c:\\servletbasedir\\assets", FilenameUtils.separatorsToWindows(repository.getCatalogAssetPath()));
	}

	/**
	 * Test to understand the behavior of the getCatalogAssetPath method.
	 */
	@Test
	public void testGetCatalogAssetPathUnixAbsolutePath() {
		repository = new AssetRepositoryImpl() {
			@Override
			boolean isAbsolute(final String path) {
				// Allow the unit test to run on different platforms
				return true;
			}

			@Override
			String getCatalogAssetPathFromSettingsService() {
				return "/var/ep/assets";
			}
		};
		assertEquals("/var/ep/assets", FilenameUtils.separatorsToUnix(repository.getCatalogAssetPath()));
	}

	/**
	 * Test to understand the behavior of the getCatalogAssetPath method.
	 * 
	 * We should be normalizing and returning the canonical absolute path, i.e. without the '../..' bits.
	 */
	@Test
	public void testGetCatalogAssetPathUnixRelativePath() {
		final ElasticPath elasticPath = context.mock(ElasticPath.class);
		context.checking(new Expectations() { {
			oneOf(elasticPath).getWebInfPath(); will(returnValue("/var/deploy/ep/sf/WEB-INF"));
		} });
		repository = new AssetRepositoryImpl() {
			@Override
			boolean isAbsolute(final String path) {
				// Allow the unit test to run on different platforms
				return false;
			}

			@Override
			String getCatalogAssetPathFromSettingsService() {
				return "../../assets";
			}
		};
		repository.setElasticPath(elasticPath);
		assertEquals("Should normalize relative path", "/var/deploy/assets", FilenameUtils.separatorsToUnix(repository
				.getCatalogAssetPath()));
	}


	/**
	 * Test that the file path to images is correctly generated.
	 */
	@Test
	public void testGetImagePathConcatenatesCorrectly() {
		final String assetLocation = "/home/assets";
		final String imageAssetsSubfolder = "images";

		final SettingsService settingsService = context.mock(SettingsService.class);
		final SettingValue assetLocationSettingValue = context.mock(SettingValue.class, "assetLocationSettingValue");
		final SettingValue imageSubfolderSettingValue = context.mock(SettingValue.class, "imageSubfolderSettingValue");
		context.checking(new Expectations() { {
			oneOf(assetLocationSettingValue).getValue(); will(returnValue(assetLocation));
			oneOf(imageSubfolderSettingValue).getValue(); will(returnValue(imageAssetsSubfolder));
			oneOf(settingsService).getSettingValue("COMMERCE/SYSTEM/ASSETS/assetLocation"); will(returnValue(assetLocationSettingValue));
			oneOf(settingsService).getSettingValue("COMMERCE/SYSTEM/ASSETS/imageAssetsSubfolder"); will(returnValue(imageSubfolderSettingValue));
		} });

		//On windows machines, paths starting with '/' will be recognized as relative paths.
		//To ensure that this test works on windows machines, we must override.
		repository = new AssetRepositoryImpl() {
			@Override
			boolean isAbsolute(final String path) {
				return true;
			}
		};
		repository.setSettingsReader(settingsService);

		assertEquals(FilenameUtils.concat(assetLocation, imageAssetsSubfolder), repository.getCatalogImagesPath());
	}

	/**
	 * Tests getting a valid asset server base url.
	 */
	@Test
	public void testGetAssetServerBaseUrl() {
		final SettingValue baseUrlSettingValue = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				atLeast(1).of(settingsReader).getSettingValue(ASSET_SERVER_BASE_URL_SETTING, STORE_CODE);
				will(returnValue(baseUrlSettingValue));

				allowing(baseUrlSettingValue).getValue();
				will(returnValue(ASSET_SERVER_BASE_URL));
			}
		});
		assertEquals("The asset server base url should come from the correct setting.",
				ASSET_SERVER_BASE_URL, repository.getAssetServerBaseUrl(STORE_CODE));
	}

	/**
	 * Tests getting the asset server base url throws an exception when the setting is not in the database.
	 */
	@Test(expected = EpSystemException.class)
	public void testGetAssetServerBaseUrlSettingNotFound() {
		context.checking(new Expectations() {
			{
				atLeast(1).of(settingsReader).getSettingValue(ASSET_SERVER_BASE_URL_SETTING, STORE_CODE);
				will(returnValue(null));
			}
		});
		repository.getAssetServerBaseUrl(STORE_CODE);
	}

	/**
	 * Tests getting the asset server images url if the base url setting is not a valid URL.
	 */
	@Test(expected = EpServiceException.class)
	public void testGetAssetServerImagesUrlWithInvalidBaseUrl() {
		final SettingValue baseUrlSettingValue = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				oneOf(settingsReader).getSettingValue(ASSET_SERVER_BASE_URL_SETTING, STORE_CODE);
				will(returnValue(baseUrlSettingValue));

				oneOf(baseUrlSettingValue).getValue(); will(returnValue("bad-base-url"));
			}
		});
		repository.getAssetServerImagesUrl(STORE_CODE);
	}

	/**
	 * Test getting the asset images URL with a valid asset server base url setting.
	 */
	@Test
	public void testGetAssetServerImagesUrl() {
		final SettingValue baseUrlSettingValue = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				oneOf(settingsReader).getSettingValue(ASSET_SERVER_BASE_URL_SETTING, STORE_CODE);
				will(returnValue(baseUrlSettingValue));

				oneOf(baseUrlSettingValue).getValue(); will(returnValue(ASSET_SERVER_BASE_URL));
			}
		});
		URL assetImagesURL = repository.getAssetServerImagesUrl(STORE_CODE);
		assertNotNull("The URL should not be null", assetImagesURL);
		assertEquals("The URL should have a trailing slash", ASSET_SERVER_BASE_URL + "/", assetImagesURL.toString());
	}

	/**
	 * Test that a relative path is normalized without trailing or leading separators.
	 * <pre>
	 * \\to\\images//		-->	to/images
	 * </pre>
	 */
	@Test
	public void testGetSubfolderMixedSlashes() {
		setupMockSettingService("\\\\to\\\\images//");
		assertEquals("to" + File.separator + "images", repository.getCatalogImagesSubfolder());
	}

	/**
	 * Test that a relative path is normalized without trailing or leading separators.
	 * <pre>
	 * to//images//		-->	to/images
	 * </pre>
	 */
	@Test
	public void testGetSubfolderSlashes() {
		setupMockSettingService("to//images//");
		assertEquals("to" + File.separator + "images", repository.getCatalogImagesSubfolder());
	}

	private void setupMockSettingService(final String subfolder) {
		final SettingValue settingValue = setupMockSettingValue(subfolder);
		final SettingsService settingsService = context.mock(SettingsService.class);
		context.checking(new Expectations() {
			{
				allowing(settingsService).getSettingValue(with(any(String.class)));
				will(returnValue(settingValue));
			}
		});

		repository = new AssetRepositoryImpl();
		repository.setSettingsReader(settingsService);
	}

	private SettingValue setupMockSettingValue(final String subFolder) {
		final SettingValue settingValue = context.mock(SettingValue.class);
		context.checking(new Expectations() {
			{
				allowing(settingValue).getValue();
				will(returnValue(subFolder));
			}
		});
		return settingValue;
	}
}
