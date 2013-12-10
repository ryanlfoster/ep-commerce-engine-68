package com.elasticpath.importexport.exporter.exporters.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.importexport.common.exception.runtime.ExportRuntimeException;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.impl.SummaryImpl;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.assets.ExportAssetFileManager;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exportentry.ExportEntry;
import com.elasticpath.importexport.util.ApplicationPropertiesHelper;

/**
 * Test case for <code>AssetExporterImpl</code>.
 */
public class AssetExporterImplTest {

	private ExportContext exportContext;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ApplicationPropertiesHelper mockApplicationPropertiesHelper;

	/**
	 * Prepare exporter configuration including all required options for assets exporter.
	 */
	@Before
	public void setUp() {
		final ExportConfiguration exportConfiguration = new ExportConfiguration();
		exportContext = new ExportContext(exportConfiguration, null);
		mockApplicationPropertiesHelper = context.mock(ApplicationPropertiesHelper.class);
		context.checking(new Expectations() {
			{
				allowing(mockApplicationPropertiesHelper).getPropertiesWithNameStartsWith("asset");
				will(returnValue(null));
			}
		});
	}

	/**
	 * Tests sequence of calls to export all asset files.
	 */
	@Test(expected = ExportRuntimeException.class)
	public void testAssetsExportExecution() throws Exception {
		final AssetExporterImpl assetExporter = new AssetExporterImpl();
		assetExporter.setApplicationPropertiesHelper(mockApplicationPropertiesHelper);
		
		assertEquals(JobType.ASSETS, assetExporter.getJobType());
		assertEquals(DigitalAsset.class, assetExporter.getDependentClasses()[0]);
		
		final String firstImage = "firstImage.png";
		final String secondImage = "secondImage.png";
		
		final ExportAssetFileManager mockAssetFileManager = context.mock(ExportAssetFileManager.class);
		context.checking(new Expectations() {
			{
				oneOf(mockAssetFileManager).initialize(with((Map<String, String>) null));
				oneOf(mockAssetFileManager).exportFile("/" + firstImage);
				oneOf(mockAssetFileManager).exportFile("/" + secondImage);
			}
		});
		
		assetExporter.setAssetFileManager(mockAssetFileManager);
		
		/// Imagine that after products export execution dependency registry
		/// contains two image entries to be exported.
		List<Class< ? >> dependentClasses = new ArrayList<Class< ? >>();
		dependentClasses.add(DigitalAsset.class);
		final DependencyRegistry dependencyRegistry = new DependencyRegistry(dependentClasses);
		dependencyRegistry.addAsset("", firstImage);
		dependencyRegistry.addAsset("", secondImage);
		exportContext.setDependencyRegistry(dependencyRegistry);
		Summary summary = new SummaryImpl();
		exportContext.setSummary(summary);
		
		assetExporter.initialize(exportContext);
		ExportEntry entry;
		
		/// As soon as assets exporter should find two files to be exported
		/// in dependency registry, expects that hasNext() return true
		/// three times: two for given images and one for generated XML.
		
		/// Expects null because our loader produces null instead input stream
		assertFalse(assetExporter.isFinished());
		entry = assetExporter.executeExport();
		assertNull(entry.getInputStream());
		assertEquals("/" + firstImage, entry.getName());
		
		/// second step
		assertFalse(assetExporter.isFinished());
		entry = assetExporter.executeExport();
		assertNull(entry.getInputStream());
		assertEquals("/" + secondImage, entry.getName());
		
		
		/// last step (all assets have been exported but assets.xml remains)
		assertFalse(assetExporter.isFinished());
		entry = assetExporter.executeExport();
		assertNotNull(entry.getInputStream());
		assertEquals("assets.xml", entry.getName());
		
		/// check context summary
		assertEquals(Integer.valueOf(2), summary.getCounters().get(JobType.ASSETS));
		/// no one more entry to be exported
		assertTrue(assetExporter.isFinished());
		assetExporter.executeExport();
	}
}
