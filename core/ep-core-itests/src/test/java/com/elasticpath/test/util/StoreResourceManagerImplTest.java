package com.elasticpath.test.util;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineFactory;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.elasticpath.commons.util.AssetRepository;
import com.elasticpath.commons.util.impl.StoreResourceManagerImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test for resolution of velocity templates depending on store.
 */
public class StoreResourceManagerImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private StoreResourceManagerImpl resourceManager = null;
	private final VelocityEngineFactory factory = new VelocityEngineFactory();
	private final AssetRepository assetRepo = context.mock(AssetRepository.class);


	/**
	 * Constructor prepares objects and services for testing.
	 */
	@Before
	public void setUp() {
		context.checking(new Expectations() {
			{
				allowing(assetRepo).getCmAssetsSubfolder();
				will(returnValue("cmassets"));
			}
		});

		Properties velocityProperties = new Properties();
		velocityProperties.put("resource.loader", "class");
		velocityProperties.put("velocimacro.library", "assets/cmassets/templates/velocity/VM_global_library.vm");
		velocityProperties.put("resource.manager.class", "com.elasticpath.commons.util.impl.StoreResourceManagerProxyImpl");
		velocityProperties.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		factory.setVelocityProperties(velocityProperties);
		resourceManager = (StoreResourceManagerImpl) StoreResourceManagerImpl.getInstance();
		resourceManager.setAssetRepository(assetRepo);
	}
	
	
	/**
	 * Test that the resource manager can resolve the global macro library and have it loaded into the velocity engine depending
	 * on the store code. Having null store code should mean that the CM asset folder's templates should be picked up.  
	 * 
	 * @throws Exception on error
	 */
	@DirtiesDatabase
	@Test
	public void testStoreSpecificMacroLibraryReference() throws Exception {
		StoreConfig staticStoreConfig = createStoreConfig(null, null);
		resourceManager.setStoreConfig(staticStoreConfig);
		
		//We need to get a new engine every time. This work is done in the storefront by the view resolver
		VelocityEngine engine = factory.createVelocityEngine();

		//Test using test.vm, which has one macro call to #identityMacro. 
		String response = VelocityEngineUtils.mergeTemplateIntoString(engine, "assets/cmassets/templates/velocity/testMacroTemplate.vm", null);
		assertEquals(response, "TEST_CMASSETS_MACRO");
	}

	private StoreConfig createStoreConfig(final String theme, final String storeCode) {
		return new StoreConfig() {
			public SettingValue getSetting(final String path) {
				return new SettingValueImpl() {
					private static final long serialVersionUID = -3141397261427930213L;

					@Override
					public String getValue() {
						return theme;
					}
				};
			}
			public Store getStore() { throw new UnsupportedOperationException(); }
			public String getStoreCode() { return storeCode; }
		};
	}

}
