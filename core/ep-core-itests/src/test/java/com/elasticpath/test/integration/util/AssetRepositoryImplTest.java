package com.elasticpath.test.integration.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.commons.util.impl.AssetRepositoryImpl;
import com.elasticpath.settings.SettingsService;
import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Integration test for {@link AssetRepositoryImpl}. 
 */
public class AssetRepositoryImplTest extends BasicSpringContextTest {
	private static final String STORE_CODE_WITH_NO_BASE_URL_OVERRIDE = "another store code";
	private static final String SNAPITUP_STORE_CODE = "snapitup";
	private static final String ASSET_SERVER_BASE_URL_SETTING = "COMMERCE/STORE/ASSETS/assetServerBaseUrl";
	private static final String SNAPITUP_ASSET_SERVER_BASE_URL = "http://host:8080/assetcontext";

	@Autowired
	private SettingsService settingsService;

	@Autowired
	private AssetRepository assetRepository;
	
	/**
	 * Tests getting the asset server base url for a null store code returns the default value.
	 * This test relies on the fact that the default has been set to the empty string in the database.
	 */
	@DirtiesDatabase
	@Test
	public void testGetAssetServerBaseUrlWithNullStoreCode() {
		assertEquals(StringUtils.EMPTY, assetRepository.getAssetServerBaseUrl(null));
	}
	
	/**
	 * Tests getting the asset server base url, when the setting is not in the database, and expects an exception.
	 */
	@DirtiesDatabase
	@Test(expected = EpSystemException.class)
	public void testGetAssetServerBaseUrlNoSettingDefined() {
		removeSetting(ASSET_SERVER_BASE_URL_SETTING);
		assetRepository.getAssetServerBaseUrl(null);
	}

	private void removeSetting(String settingPath) {
		try {
			SettingDefinition settingDefinition = settingsService.getSettingDefinition(settingPath);
			settingsService.deleteSettingDefinition(settingDefinition);
		} catch(Exception e) {
			fail("Deletion of setting with path " + settingPath + " failed with exception: " + e.getMessage());
		}
	}
	
	/**
	 * Tests getting the asset server base url for different contexts (store codes) gives the correct value for that store code. 
	 */
	@DirtiesDatabase
	@Test
	public void testGetAssetServerBaseUrlReturnsContextValue() {
		addAssetServerBaseUrlContextValue(SNAPITUP_STORE_CODE, SNAPITUP_ASSET_SERVER_BASE_URL);
		
		assertEquals("The asset server base url should come from the context-specific override.", 
				SNAPITUP_ASSET_SERVER_BASE_URL, assetRepository.getAssetServerBaseUrl(SNAPITUP_STORE_CODE));

		String assetServerBaseUrlDefaultValue = assetRepository.getAssetServerBaseUrl(null);
		assertNotNull(assetServerBaseUrlDefaultValue);
		assertEquals("The asset server base url should fall back to the default value.", 
				assetServerBaseUrlDefaultValue, assetRepository.getAssetServerBaseUrl(STORE_CODE_WITH_NO_BASE_URL_OVERRIDE));
	}

	private void addAssetServerBaseUrlContextValue(String context, String value) {
		SettingDefinition definition = settingsService.getSettingDefinition(ASSET_SERVER_BASE_URL_SETTING);
		SettingValue settingValue = settingsService.createSettingValue(definition, context);
		settingValue.setValue(value);
		settingsService.updateSettingValue(settingValue);
	}
	
}
