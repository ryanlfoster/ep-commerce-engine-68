package com.elasticpath.importexport.exporter.configuration;

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
 * Tests validation of the export configuration.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class ExportConfigurationLoaderTest {

	private ConfigurationLoader loader;


	/**
	 * Setup test.
	 *
	 * @throws Exception on error
	 */
	@Before
	public void setUp() throws Exception {
		loader = new ConfigurationLoader();
		Map<String, String> schemaPathMap = new HashMap<String, String>();
		schemaPathMap.put(ExportConfiguration.class.getSimpleName(), JUnitUtil.getAbsolutePath() + "/target/classes/schema/schemaExportConfig.xsd");
		loader.setSchemaPathMap(schemaPathMap);
		loader.setValidationEventHandler(new DefaultValidationEventHandler());
	}

	/**
	 * Test with valid configuration.
	 */
	@Test
	public void testValidConfiguration() {
		String xml = "<?xml version=\"1.0\"?><exportconfiguration><exporter type=\"PRODUCT\" />"
				+ "<packager type=\"ZIP\" packagename=\"export.zip\" />"
				+ "<delivery><method>FILE</method><target>./exportProduction</target></delivery></exportconfiguration>";

		assertSucceeds(xml);
	}

	/**
	 * Test with unordered configuration.
	 */
	@Test
	public void testUnorderedConfiguration() {
		String xml = "<?xml version=\"1.0\"?><exportconfiguration><packager type=\"ZIP\" "
			+	"packagename=\"export.zip\" /><exporter type=\"PRODUCT\" /><delivery><method>FILE</method><target>./exportProduction</target>"
			+	"</delivery></exportconfiguration>";

		assertFails(xml);
	}

	/**
	 * Test detects missing exporters.
	 */
	@Test
	public void testNoExporters() {
		String xml = "<?xml version=\"1.0\"?><exportconfiguration>" + "<packager type=\"ZIP\" />"
				+ "<delivery><method>FILE</method><target>./exportProduction</target></delivery></exportconfiguration>";
		assertSucceeds(xml);
	}

	/**
	 * Test detects invalid exporters.
	 */
	@Test
	public void testInvalidExporters() {
		String xml = "<?xml version=\"1.0\"?><exportconfiguration><exporters><exporter type=\"INVALID\" /></exporters>"
				+ "<packager type=\"ZIP\" />"
				+ "<delivery><method>FILE</method><target>./exportProduction</target></delivery></exportconfiguration>";
		assertFails(xml);
	}

	/**
	 * Test detects no packager.
	 */
	@Test
	public void testNoPackager() {
		String xml = "<?xml version=\"1.0\"?><exportconfiguration><exporters><exporter type=\"PRODUCT\" /></exporters>" + "<commonexportproperties>"
				+ "<delivery><method>FILE</method><target>./exportProduction</target></delivery></commonexportproperties></exportconfiguration>";
		assertFails(xml);
	}

	/**
	 * Test detects invalid packager type.
	 */
	@Test
	public void testInvalidPackager() {
		String xml = "<?xml version=\"1.0\"?><exportconfiguration><exporters><exporter type=\"PRODUCT\" /></exporters>"
				+ "<commonexportproperties><packager type=\"INVALID\" packagename=\"export.zip\" />"
				+ "<delivery><method>FILE</method><target>./exportProduction</target></delivery></commonexportproperties></exportconfiguration>";
		assertFails(xml);
	}

	/**
	 * Test detects no delivery method.
	 */
	@Test
	public void testNoDeliveryMethod() {
		String xml = "<?xml version=\"1.0\"?><exportconfiguration><exporters><exporter type=\"PRODUCT\" /></exporters>"
				+ "<commonexportproperties><packager type=\"INVALID\" packagename=\"export.zip\" />"
				+ "</commonexportproperties></exportconfiguration>";
		assertFails(xml);
	}

	/**
	 * Test detects invalid delivery method.
	 */
	@Test
	public void testInValidDeliveryMethod() {
		String xml = "<?xml version=\"1.0\"?><exportconfiguration><exporters><exporter type=\"PRODUCT\" /></exporters>"
				+ "<packager type=\"ZIP\" packagename=\"export.zip\" />"
				+ "<delivery><method>INVALID</method><target>./exportProduction</target></delivery></exportconfiguration>";
		assertFails(xml);
	}

	/**
	 * Test detects delivery method without target.
	 */
	@Test
	public void testNoTargetDeliveryMethod() {
		String xml = "<?xml version=\"1.0\"?><exportconfiguration><exporters><exporter type=\"PRODUCT\" /></exporters>"
				+ "<commonexportproperties><packager type=\"ZIP\" packagename=\"export.zip\" />"
				+ "<delivery><method>FILE</method></delivery></commonexportproperties></exportconfiguration>";
		assertFails(xml);
	}

	private void assertSucceeds(final String xml) {
		try {
			loader.load(new ByteArrayInputStream(xml.getBytes()), ExportConfiguration.class);
		} catch (ConfigurationException e) {
			fail("Unexpected configuration exception " + e.getMessage());
		}
	}

	private void assertFails(final String xml) {
		try {
			loader.load(new ByteArrayInputStream(xml.getBytes()), ExportConfiguration.class);
			fail("Expected configuration exception");
		} catch (ConfigurationException e) { // NOPMD
			// expected
		}
	}

}
