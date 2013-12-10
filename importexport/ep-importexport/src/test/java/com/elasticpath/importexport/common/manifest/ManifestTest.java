package com.elasticpath.importexport.common.manifest;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

/**
 * Tests Manifest. 
 */
public class ManifestTest {

	private static final String EXPECTED_MANIFEST_VERSION = "6.0";

	/**
	 * Tests GetVersion.
	 */
	@Test
	public void testGetVersion() {
		Manifest manifest = new Manifest();
		
		assertEquals(EXPECTED_MANIFEST_VERSION, manifest.getVersion());
	}

	/**
	 * Tests AddResouces and GetResources methods.
	 */
	@Test
	public void testAddGetResources() {
		Manifest manifest = new Manifest();
		
		manifest.addResource("RES_1");
		manifest.addResource("RES_2");
		
		final List<String> content = manifest.getResources();

		assertEquals("RES_1", content.get(0));
		assertEquals("RES_2", content.get(1));		
	}

}
