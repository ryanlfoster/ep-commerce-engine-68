package com.elasticpath.importexport.exporter.configuration.search;

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
 * Tests validation of the search configuration.
 */
public class SearchConfigurationLoaderTest {

	private ConfigurationLoader loader;


	/**
	 * Setup test.
	 */
	@Before
	public void setUp() throws Exception {
		loader = new ConfigurationLoader();
		Map<String, String> schemaPathMap = new HashMap<String, String>();
		schemaPathMap.put(SearchConfiguration.class.getSimpleName(), JUnitUtil.getAbsolutePath() + "/target/classes/schema/schemaSearchConfig.xsd");
		loader.setSchemaPathMap(schemaPathMap);
		loader.setValidationEventHandler(new DefaultValidationEventHandler());
	}

	/**
	 * Test with valid configuration.
	 */
	@Test
	public void testValidConfiguration() throws Exception {
		String xml = "<?xml version=\"1.0\"?><searchconfiguration><epql>Select Catalog WHERE CatalogCode='SNAPITUP'</epql></searchconfiguration>";
		assertSucceeds(xml);
	}

	/**
	 * Test detects missing product search criteria.
	 */
	@Test
	public void testWithoutSearchCriteria() throws Exception {
		String xml = "<?xml version=\"1.0\"?><searchconfiguration></searchconfiguration>";
		assertSucceeds(xml);
	}

	/**
	 * Test detects no packager.
	 */
	@Test
	public void testFailConfiguration() {
		String xml = "<?xml version=\"1.0\"?><searchconfiguration><unknowntag/><productexportcriteria><productcode>10050002"
			+	"</productcode><locale>en</locale></productexportcriteria></searchconfiguration>";
		assertFails(xml);
	}


	private void assertSucceeds(final String xml) throws Exception {
		loader.load(new ByteArrayInputStream(xml.getBytes()), SearchConfiguration.class);
	}

	private void assertFails(final String xml) {
		try {
			loader.load(new ByteArrayInputStream(xml.getBytes()), SearchConfiguration.class);
			fail("Expected configuration exception");
		} catch (ConfigurationException e) { // NOPMD
			// expected
		}
	}

}
