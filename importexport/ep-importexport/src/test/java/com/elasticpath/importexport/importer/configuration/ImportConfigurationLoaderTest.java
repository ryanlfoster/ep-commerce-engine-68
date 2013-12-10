package com.elasticpath.importexport.importer.configuration;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.importexport.common.configuration.ConfigurationLoader;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.marshalling.DefaultValidationEventHandler;
import com.elasticpath.testutility.JUnitUtil;

/**
 * Tests validation of the import configuration.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ImportConfigurationLoaderTest {

	private ConfigurationLoader loader;

	/**
	 * Setup test.
	 */
	@Before
	public void setUp() throws Exception {
		loader = new ConfigurationLoader();
		Map<String, String> schemaPathMap = new HashMap<String, String>();
		schemaPathMap.put(ImportConfiguration.class.getSimpleName(), JUnitUtil.getAbsolutePath() + "/target/classes/schema/schemaImportConfig.xsd");
		loader.setSchemaPathMap(schemaPathMap);
		loader.setValidationEventHandler(new DefaultValidationEventHandler());
	}

	/**
	 * Test with valid configuration.
	 */
	@Test
	public void testValidConfiguration() throws Exception {
		String xml = "<?xml version=\"1.0\"?><importconfiguration><xmlvalidation>true</xmlvalidation>"
				+ "<importstrategy><importer type=\"PRODUCT\"><importstrategy>INSERT_OR_UPDATE</importstrategy></importer>"
				+ "</importstrategy><packager type=\"ZIP\" /><retrieval><method>FILE</method><source>./target/products.zip</source></retrieval>"
				+ "</importconfiguration>";

		assertSucceeds(xml);
	}

	/**
	 * Test unordered configuration.
	 */
	@Test
	public void testUnorderedConfiguration() {
		String xml = "<?xml version=\"1.0\"?><importconfiguration>"
				+ "<retrieval><method>FILE</method><source>./target/products.zip</source></retrieval>"
				+ "<importstrategy>INSERT_OR_UPDATE</importstrategy><packager type=\"ZIP\" />"
				+ "<xmlvalidation>true</xmlvalidation></importconfiguration>";

		assertFails(xml);
	}

	/**
	 * Test detects missing xml validation.
	 */
	@Test
	public void testNoXmlValidation() {
		String xml = "<?xml version=\"1.0\"?><importconfiguration>" + "<importstrategy>INSERT_OR_UPDATE</importstrategy><packager type=\"ZIP\" />"
				+ "<retrieval><method>FILE</method><source>./target/products.zip</source></retrieval>" + "</importconfiguration>";

		assertFails(xml);
	}

	/**
	 * Test detects missing import strategy.
	 */
	@Test
	public void testNoImportStrategy() throws Exception {
		String xml = "<?xml version=\"1.0\"?><importconfiguration><xmlvalidation>true</xmlvalidation>" + "<packager type=\"ZIP\" />"
				+ "<retrieval><method>FILE</method><source>./target/products.zip</source></retrieval>" + "</importconfiguration>";

		assertSucceeds(xml);
	}

	/**
	 * Test detects invalid import strategy.
	 */
	@Test
	public void testInvalidImportStrategy() {
		String xml = "<?xml version=\"1.0\"?><importconfiguration><xmlvalidation>true</xmlvalidation>"
				+ "<importstrategy>INVALID</importstrategy><packager type=\"ZIP\" />"
				+ "<retrieval><method>FILE</method><source>./target/products.zip</source></retrieval>" + "</importconfiguration>";

		assertFails(xml);
	}

	/**
	 * Test detects missing packager.
	 */
	@Test
	public void testNoPackager() {
		String xml = "<?xml version=\"1.0\"?><importconfiguration><xmlvalidation>true</xmlvalidation>"
				+ "<importstrategy>INSERT_OR_UPDATE</importstrategy>"
				+ "<retrieval><method>FILE</method><source>./target/products.zip</source></retrieval>" + "</importconfiguration>";

		assertFails(xml);
	}

	/**
	 * Test detects missing retrieval method.
	 */
	@Test
	public void testNoRetrievalMethod() {
		String xml = "<?xml version=\"1.0\"?><importconfiguration><xmlvalidation>true</xmlvalidation>"
				+ "<importstrategy>INSERT_OR_UPDATE</importstrategy><packager type=\"ZIP\" />" + "</importconfiguration>";

		assertFails(xml);
	}

	/**
	 * Test detects invalid retrieval method.
	 */
	@Test
	public void testInvalidRetrievalMethod() {
		String xml = "<?xml version=\"1.0\"?><importconfiguration><xmlvalidation>true</xmlvalidation>"
				+ "<importstrategy>INSERT_OR_UPDATE</importstrategy><packager type=\"ZIP\" />"
				+ "<retrieval><method>INVALID</method><source>./target/products.zip</source></retrieval>" + "</importconfiguration>";

		assertFails(xml);
	}

	private void assertSucceeds(final String xml) throws Exception {
		loader.load(new ByteArrayInputStream(xml.getBytes()), ImportConfiguration.class);
	}

	private void assertFails(final String xml) {
		try {
			loader.load(new ByteArrayInputStream(xml.getBytes()), ImportConfiguration.class);
			fail("Expected configuration exception");
		} catch (ConfigurationException e) { // NOPMD
			// expected
		}
	}
}
