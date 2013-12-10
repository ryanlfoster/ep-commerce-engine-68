package com.elasticpath.importexport.importer.importers.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.importexport.common.configuration.PackagerConfiguration;
import com.elasticpath.importexport.common.dto.assets.AssetDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.exception.runtime.ImportRuntimeException;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.configuration.RetrievalConfiguration;
import com.elasticpath.importexport.importer.context.ImportContext;

/**
 * Test for assets importer implementation.
 */
public class AssetsImporterImplTest {

	private AssetsImporterImpl assetsImporter;


	@Before
	public void setUp() throws Exception {
		assetsImporter = new AssetsImporterImpl();
		assetsImporter.setStatusHolder(new ImportStatusHolder());
	}

	/**
	 * Check initialize assets importer.
	 */
	@Test(expected = ConfigurationException.class)
	public void testInitializeWithoutVFSHelper() throws Exception {
		assetsImporter.setAssetFileManager(null);
		ImportConfiguration configuration = new ImportConfiguration();
		PackagerConfiguration packagerConfiguration = new PackagerConfiguration();
		RetrievalConfiguration retrievalConfiguration = new RetrievalConfiguration();
		configuration.setPackagerConfiguration(packagerConfiguration);
		configuration.setRetrievalConfiguration(retrievalConfiguration);
		ImportContext context = new ImportContext(configuration);
		assetsImporter.initialize(context, null);
	}

	/**
	 * Check execute import without VFS helper.
	 */
	@Test(expected = ImportRuntimeException.class)
	public void testExecuteImportWithoutVFSHelper() {
		assetsImporter.setAssetFileManager(null);
		assetsImporter.executeImport(null);
	}

	/** The dto class must be present and correct. */
	@Test
	public void testDtoClass() {
		assertEquals("Incorrect DTO class", AssetDTO.class, assetsImporter.getDtoClass());
	}

	/** The auxiliary JAXB class list must not be null (can be empty). */
	public void testAuxiliaryJaxbClasses() {
		assertNotNull(assetsImporter.getAuxiliaryJaxbClasses());
	}
}
