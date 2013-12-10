package com.elasticpath.importexport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.examples.RecursiveElementNameAndTextQualifier;
import org.xml.sax.SAXException;

import com.elasticpath.importexport.client.EngineInitialization;
import com.elasticpath.importexport.common.ImportExportContextIdNames;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.controller.ExportController;
import com.elasticpath.importexport.importer.controller.ImportController;

/**
 * This class runs the importer, then the exporter, then compares the two sets of xml files.
 */
@SuppressWarnings("PMD.DoNotCallSystemExit")
public class Roundtrip {
	
	private static final Logger LOGGER = Logger.getLogger(Roundtrip.class);
	private static final int WRONG_ARGS_EXIT_CODE = 1;
	private static final int NUM_ARGS = 3;
	
	private final EngineInitialization engine;
	
	/**
	 * Steps:
	 * Have to run create-mysql manually.
	 * Then modify runtime classpath to include importexport/ dir so that importexporttool.config can be found.
	 * Then modify exportconfiguration.xml so that target path is absolute.
	 * Then modify importconfiguration.xml so that source path is absolute.
	 * 
	 * @param args Commandline args.
	 * @throws Exception If anything goes wrong.
	 */
	public static void main(final String[] args) throws Exception {
		if (args.length != NUM_ARGS) {
			LOGGER.info("Needs the following params:");
			LOGGER.info("absolute path to importconfiguration.xml");
			LOGGER.info("absolute path to exportconfiguration.xml");
			LOGGER.info("absolute path to searchconfiguration.xml");
			System.exit(WRONG_ARGS_EXIT_CODE);
		}
		
		Roundtrip rtt = new Roundtrip();
		rtt.makeItSo(new File(args[0]), new File(args[1]), new File(args[2]));
	}
	
	/**
	 * Constructor.
	 */
	public Roundtrip() {
		this.engine = EngineInitialization.getInstance();
	}
	
	/**
	 * Does the import, export, and compare of xml files.
	 * 
	 * @param importConfigurationXml The importconfiguration.xml file.
	 * @param exportConfigurationXml The exportconfiguration.xml file.
	 * @param searchConfigurationXml The searchconfiguration.xml file.
	 * @throws Exception If anything goes wrong.
	 */
	public void makeItSo(final File importConfigurationXml, final File exportConfigurationXml, final File searchConfigurationXml) throws Exception {
		doImport(importConfigurationXml);
		doExport(exportConfigurationXml, searchConfigurationXml);
		
		LOGGER.info("Verifying...");
		XMLUnit.setIgnoreWhitespace(true);
		XMLUnit.setIgnoreAttributeOrder(true);
		
		XPath xpath = XPathFactory.newInstance().newXPath();
		File dataInDir = new File(xpath.evaluate(
				"//source",
				DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(importConfigurationXml)));
		File dataOutDir = new File(xpath.evaluate(
				"//target",
				DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(exportConfigurationXml)));
		
		compareDirs(dataInDir, dataOutDir);
	}
	
	/**
	 * Import.
	 * 
	 * @param importConfigurationXml The importconfiguration.xml file.
	 * @throws ConfigurationException If an exception occurs.
	 * @throws FileNotFoundException If an exception occurs.
	 */
	public void doImport(final File importConfigurationXml) throws ConfigurationException, FileNotFoundException {
		ImportController importer = engine.getElasticPath().getBean(ImportExportContextIdNames.IMPORT_CONTROLLER);
		importer.loadConfiguration(new FileInputStream(importConfigurationXml));
		LOGGER.info("Importing...");
		importer.executeImport();
	}
	
	/**
	 * Export.
	 * 
	 * @param exportConfigurationXml The exportconfiguration.xml file.
	 * @param searchConfigurationXml The searchconfiguration.xml file.
	 * @throws ConfigurationException If an exception occurs.
	 * @throws FileNotFoundException If an exception occurs.
	 */
	public void doExport(final File exportConfigurationXml, final File searchConfigurationXml) throws ConfigurationException, FileNotFoundException {
		ExportController exporter = engine.getElasticPath().getBean(ImportExportContextIdNames.EXPORT_CONTROLLER);
		exporter.loadConfiguration(new FileInputStream(exportConfigurationXml), new FileInputStream(searchConfigurationXml));
		LOGGER.info("Exporting...");
		exporter.executeExport();
	}
	
	/**
	 * Loops over the xml files in the dataOutDir and compares them to the files in the dataInDir.
	 * 
	 * @param dataInDir The xml that is imported.
	 * @param dataOutDir The xml that is exported.
	 * @throws Exception If an exception occurs.
	 */
	public void compareDirs(final File dataInDir, final File dataOutDir) throws Exception {
		for (String xmlFilename : dataOutDir.list(new FilenameFilter() {
			public boolean accept(final File dir, final String name) {
				return name.endsWith("xml");
			} })) {
			compareFiles(new File(dataInDir, xmlFilename), new File(dataOutDir, xmlFilename));
		}
	}
	
	/**
	 * Compares two xml files.
	 * 
	 * @param expected The expected xml data.
	 * @param actual The actual xml data.
	 * @throws Exception If an exception occurs.
	 */
	public void compareFiles(final File expected, final File actual) throws Exception {
		LOGGER.info(actual.getName() + " equal sizes: " + (actual.length() == expected.length()));
		if (actual.length() != expected.length()) {
			compareXml(new FileReader(expected), new FileReader(actual));
		}
		LOGGER.info("\n");
	}
	
	/**
	 * Compares the contents of two xml strings.
	 * 
	 * @param expected The expected xml data.
	 * @param actual The actual xml data.
	 * @throws SAXException If an exception occurs.
	 * @throws IOException If an exception occurs.
	 */
	public void compareXml(final Reader expected, final Reader actual) throws SAXException, IOException {
		final Diff diff = new Diff(expected, actual);
		diff.overrideElementQualifier(new RecursiveElementNameAndTextQualifier());
		
		DetailedDiff detailedDiff = new DetailedDiff(diff);
		for (Object difference : detailedDiff.getAllDifferences()) {
			LOGGER.info(difference);
		}
//		System.out.println(xmlFilename + " similar: " + diff.similar());
//		System.out.println(xmlFilename + " identical: " + diff.identical());
	}
	
}
