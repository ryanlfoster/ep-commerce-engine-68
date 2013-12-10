package com.elasticpath.test.velocity;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.elasticpath.commons.util.impl.StoreResourceManagerImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.domain.impl.SettingValueImpl;
import com.elasticpath.sfweb.view.velocity.StoreVelocityConfigurer;

/**
 * Test for storefront velocity macro loading mechanism, which should be using macros on a store specific basis.
 * NOTE: this test is coupled with the presence of an assets directory. 
 */
public class StoreVelocityConfigurerTest {

	private static final String TRUE = "true";
	private VelocityEngine engine = null;
	private StoreVelocityConfigurer velocityConfigurer = null;
	private StoreResourceManagerImpl resourceManager = null;
	
	/**
	 * Constructor prepares objects and services for testing.
	 */
	@Before
	public void setUp() {
		Properties velocityProperties = new Properties();
		velocityProperties.put("resource.loader", "file");
		velocityProperties.put("file.resource.loader.path", "target/test-classes/assets, target/test-classes/assets/themes");
		velocityProperties.put("file.resource.loader.cache", "false");
		velocityProperties.put("velocimacro.library", "VM_global_library.vm");
		velocityProperties.put("velocimacro.library.autoreload", TRUE);
		velocityProperties.put("velocimacro.permissions.allow.inline", TRUE);
		velocityProperties.put("velocimacro.permissions.allow.inline.to.replace.global", TRUE);
		velocityProperties.put("velocimacro.permissions.allow.inline.local.scope", TRUE);
		velocityProperties.put("velocimacro.context.localscope", "false");
		velocityProperties.put("velocimacro.messages.on", "false");
		velocityProperties.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
		velocityProperties.put("template.encoding", "UTF-8");
		velocityProperties.put("resource.manager.class", "com.elasticpath.commons.util.impl.StoreResourceManagerProxyImpl");
		velocityProperties.put("input.encoding", "UTF-8");
		velocityProperties.put("output.encoding", "UTF-8");
		velocityProperties.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		
		
		velocityConfigurer = new StoreVelocityConfigurer();
		velocityConfigurer.setVelocityProperties(velocityProperties);
		
		resourceManager = (StoreResourceManagerImpl) StoreResourceManagerImpl.getInstance();
		
	}
	
	
	/**
	 * Test that macros come from the correct library located under their respective store directory.
	 * i.e. For a store request coming from snapitup, global macros come from 
	 * assets/themes/electronics/default/templates/velocity/VM_global_library.vm
	 * 
	 * Here we set the store and theme to be integration and Sports store respectively by assigning a mocked
	 * StoreConfig object into our store aware resource manager and the velocity configurer.
	 * 
	 */
	@Test
	public void testStoreSpecificMacroLibraryReference() {
		StoreConfig staticStoreConfig = createStoreConfig("integration", "Sports store");
		velocityConfigurer.setStoreConfig(staticStoreConfig);
		resourceManager.setStoreConfig(staticStoreConfig);
		//We need to get a new engine every time. This work is done in the storefront by the view resolver
		engine = velocityConfigurer.getVelocityEngine();

		//Test using test.vm, which has one macro call to #identityMacro. 
		String response = VelocityEngineUtils.mergeTemplateIntoString(engine, "test.vm", null);
		assertEquals(response, "SPORTS_MACRO_LIBRARY");
	}
	
	/**
	 * Test that the macro library loading falls back to one in the default folder of the theme.
	 * i.e. For a store request from Snapitup, if 
	 * assets/themes/electronics/snapitup/templates/velocity/VM_global_library.vm
	 * doesn't exist,
	 * use the macro in 
	 * assets/themes/electronics/default/templates/velocity/VM_global_library.vm  
	 */
	@Test
	public void testDefaultMacroFallback() {
		StoreConfig staticStoreConfig = createStoreConfig("integration", "Red Hot Deals");
		velocityConfigurer.setStoreConfig(staticStoreConfig);
		resourceManager.setStoreConfig(staticStoreConfig);

		//We need to get a new engine every time. This work is done in the storefront by the view resolver
		engine = velocityConfigurer.getVelocityEngine();
		
		//If the macro file doesn't exist for a particular store, the store code will be associated with the global macro
		//library located in the default store. 

		//Test using test.vm, which has one macro call to #identityMacro. 
		String response = VelocityEngineUtils.mergeTemplateIntoString(engine, "test.vm", null);
		assertEquals(response, "DEFAULT_MACRO_LIBRARY");
	}
	
	private StoreConfig createStoreConfig(final String theme, final String storeCode) {
		return new StoreConfig() {
			public SettingValue getSetting(final String path) {
				return new SettingValueImpl() {
					private static final long serialVersionUID = -2870798342957204768L;

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
