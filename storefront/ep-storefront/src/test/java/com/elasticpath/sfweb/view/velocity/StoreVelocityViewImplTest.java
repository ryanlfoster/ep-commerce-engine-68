package com.elasticpath.sfweb.view.velocity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;

/**
 * Tests that StoreVelocityViewImpl works as expected.
 */
public class StoreVelocityViewImplTest {

	private static final char SEPARATOR = File.separatorChar;
	private static final String THEME = "theme";
	private static final String TEMPLATE = "templateName.vm";
	/**
	 * Tests if the real template name returned is as expected.
	 */
	@Test
	public void testGetRealTemplateName() {
		StoreVelocityViewImpl storeVelocityViewImpl = new StoreVelocityViewImpl() {
			@Override
			boolean templateExists(final String templateName) {
				return true;
			}
		};
		
		String realTemplateName = storeVelocityViewImpl.getRealTemplateName("templateName.vm", "storeCode", THEME);
		assertEquals(THEME + SEPARATOR + "storeCode" + SEPARATOR + "templates" + SEPARATOR + "velocity"
				 + SEPARATOR + TEMPLATE, realTemplateName);

		// check the same when the template does not exist
		storeVelocityViewImpl = new StoreVelocityViewImpl() {
			@Override
			boolean templateExists(final String templateName) {
				return false;
			}
		};
			
		realTemplateName = storeVelocityViewImpl.getRealTemplateName(TEMPLATE, "storeCode", THEME);
		assertEquals(THEME + SEPARATOR + "default" 
				 + SEPARATOR + "templates" + SEPARATOR + "velocity" + SEPARATOR + TEMPLATE, realTemplateName);

	}

	/**
	 * Tests that store config returned is not null.
	 */
	@Test
	public void testGetStoreConfig() {
		StoreVelocityViewImpl storeVelocityViewImpl = new StoreVelocityViewImpl();
		assertNotNull(storeVelocityViewImpl.getStoreConfig());
	}
	
}