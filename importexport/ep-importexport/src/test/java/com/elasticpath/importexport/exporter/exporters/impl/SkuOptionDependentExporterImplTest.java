package com.elasticpath.importexport.exporter.exporters.impl;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.util.assets.AssetFileManager;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.importexport.util.ApplicationPropertiesHelper;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * Test for {@link SkuOptionDependentExporterImpl}.
 */
@RunWith(JMock.class)
@SuppressWarnings({ "PMD.NonStaticInitializer", "PMD.TooManyStaticImports" })
public class SkuOptionDependentExporterImplTest {
	private final SkuOptionDependentExporterImpl skuOptionExporter = new SkuOptionDependentExporterImpl();
	private SkuOptionService skuOptionService;
	private DependentExporterFilter dependentExporterFilter;
	private ApplicationPropertiesHelper applicationPropertiesHelper;
	private ExportContext exportContext;
	private final Mockery context = new JUnit4Mockery();
	private static final long CATALOG_UID = 14441;

	/**
	 * Test initialization.
	 * 
	 * @throws ConfigurationException in case of errors
	 */
	@Before
	public void setUp() throws ConfigurationException {
		skuOptionService = context.mock(SkuOptionService.class);
		skuOptionExporter.setSkuOptionService(skuOptionService);

		applicationPropertiesHelper = context.mock(ApplicationPropertiesHelper.class);
		skuOptionExporter.setApplicationPropertiesHelper(applicationPropertiesHelper);

		dependentExporterFilter = context.mock(DependentExporterFilter.class);
		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		skuOptionExporter.initialize(exportContext, dependentExporterFilter);
	}

	/** Tests finding dependent objects when the dependent object should be filtered. */
	@Test
	public void testFindDependentObjectsFiltered() {
		SkuOption skuOption1 = context.mock(SkuOption.class, "skuOption-1");
		SkuOption skuOption2 = context.mock(SkuOption.class, "skuOption-2");
		final List<SkuOption> skuOptionList = Arrays.asList(skuOption1, skuOption2);
		context.checking(new Expectations() {
			{
				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(true));

				one(skuOptionService).findAllSkuOptionFromCatalog(CATALOG_UID);
				will(returnValue(skuOptionList));
			}
		});

		assertEquals(skuOptionList, skuOptionExporter.findDependentObjects(CATALOG_UID));
	}

	/** Tests finding dependent objects when the dependent object should be filtered. */
	@Test
	public void testFindDependentObjectsNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Arrays.<Class<?>> asList(SkuOption.class));
		exportContext.setDependencyRegistry(registry);

		final long skuOption1Uid = 9;
		final long skuOption2Uid = 4541;
		final long skuOption3Uid = 4;
		registry.addUidDependency(SkuOption.class, skuOption1Uid);
		registry.addUidDependencies(SkuOption.class, new HashSet<Long>(Arrays.asList(skuOption2Uid, skuOption3Uid)));

		final SkuOption skuOption1 = context.mock(SkuOption.class, "skuOption-1");
		final SkuOption skuOption2 = context.mock(SkuOption.class, "skuOption-2");
		final SkuOption skuOption3 = context.mock(SkuOption.class, "skuOption-3");
		context.checking(new Expectations() {
			{
				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(false));

				Catalog dependentCatalog = context.mock(Catalog.class, "dependentCatalog");
				Catalog otherCatalog = context.mock(Catalog.class, "otherCatalog");
				allowing(dependentCatalog).getUidPk();
				will(returnValue(CATALOG_UID));
				allowing(otherCatalog).getUidPk();
				will(returnValue(0L));

				allowing(skuOption1).getCatalog();
				will(returnValue(dependentCatalog));
				allowing(skuOption2).getCatalog();
				will(returnValue(otherCatalog));
				allowing(skuOption3).getCatalog();
				will(returnValue(dependentCatalog));

				one(skuOptionService).get(skuOption1Uid);
				will(returnValue(skuOption1));
				one(skuOptionService).get(skuOption2Uid);
				will(returnValue(skuOption2));
				one(skuOptionService).get(skuOption3Uid);
				will(returnValue(skuOption3));
			}
		});

		List<SkuOption> result = skuOptionExporter.findDependentObjects(CATALOG_UID);
		assertThat("Missing skuOption1", result, hasItem(skuOption1));
		assertThat("Missing skuOption3", result, hasItem(skuOption3));
		assertThat("skuOption2 is not a part of this catalog", result, not(hasItem(skuOption2)));
		assertEquals("Other skuOptions returned?", 2, result.size());
	}

	/**
	 * If a dependent {@link SkuOptionValue} has an image, it should be marked to be exported in the registry (another
	 * exporter handles the actual export).
	 */
	@Test
	public void testFindDependentWithImageNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Arrays.<Class<?>> asList(SkuOption.class, DigitalAsset.class));
		exportContext.setDependencyRegistry(registry);

		registry.addUidDependency(SkuOption.class, 1L);

		final String imageSubdir = "some location";
		final String skuOptionUrl1 = "some url1";
		final String skuOptionUrl2 = "some url2";
		final SkuOption skuOption = context.mock(SkuOption.class);
		final SkuOptionValue skuOptionValue1 = context.mock(SkuOptionValue.class, "skuOptionValue-1");
		final SkuOptionValue skuOptionValue2 = context.mock(SkuOptionValue.class, "skuOptionValue-2");
		context.checking(new Expectations() {
			{
				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(false));

				Catalog dependentCatalog = context.mock(Catalog.class);
				allowing(dependentCatalog).getUidPk();
				will(returnValue(CATALOG_UID));

				allowing(skuOption).getCatalog();
				will(returnValue(dependentCatalog));
				allowing(skuOption).getOptionValues();
				will(returnValue(Arrays.asList(skuOptionValue1, skuOptionValue2)));

				allowing(skuOptionValue1).getImage();
				will(returnValue(skuOptionUrl1));
				allowing(skuOptionValue2).getImage();
				will(returnValue(skuOptionUrl2));

				one(skuOptionService).get(1L);
				will(returnValue(skuOption));

				allowing(applicationPropertiesHelper).getPropertiesWithNameStartsWith("asset");
				will(returnValue(Collections.singletonMap(AssetFileManager.PROPERTY_IMAGE_ASSET_SUBFOLDER, imageSubdir)));
			}
		});

		Matcher<String> skuOptionValueImageLocationMatcher1 = constructImageMatcher(imageSubdir, skuOptionUrl1);
		Matcher<String> skuOptionValueImageLocationMatcher2 = constructImageMatcher(imageSubdir, skuOptionUrl2);
		Matcher<Iterable<? super String>> hasItemMatcher1 = hasItem(skuOptionValueImageLocationMatcher1);
		assertThat("skuOptionValue-1 image in registry before export?", exportContext.getDependencyRegistry().getAssetFileNames(), 
				not(hasItemMatcher1));
		Matcher<Iterable<? super String>> hasItemMatcher2 = hasItem(skuOptionValueImageLocationMatcher2);
		assertThat("skuOptionValue-2 image in registry before export?", exportContext.getDependencyRegistry().getAssetFileNames(), 
				not(hasItemMatcher2));

		skuOptionExporter.findDependentObjects(CATALOG_UID);

		assertThat("skuOptionValue-1 image missing from registry", exportContext.getDependencyRegistry().getAssetFileNames(), hasItemMatcher1);
		assertThat("skuOptionValue-2 image missing from registry", exportContext.getDependencyRegistry().getAssetFileNames(), hasItemMatcher2);
	}

	private Matcher<String> constructImageMatcher(final String prefix, final String suffix) {
		return allOf(startsWith(prefix), endsWith(suffix));
	}

	/**
	 * If a dependent {@link SkuOption} has an image, it should be marked to be exported in the registry (another
	 * exporter handles the actual export). This handles when we are filtered.
	 */
	@Test
	public void testFindDependentWithImageFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Arrays.<Class<?>> asList(SkuOption.class, DigitalAsset.class));
		exportContext.setDependencyRegistry(registry);

		registry.addUidDependency(SkuOption.class, 1L);

		final String imageSubdir = "some location";
		final String skuOptionUrl1 = "some url1";
		final String skuOptionUrl2 = "some url2";
		final SkuOption skuOption = context.mock(SkuOption.class);
		final SkuOptionValue skuOptionValue1 = context.mock(SkuOptionValue.class, "skuOptionValue-1");
		final SkuOptionValue skuOptionValue2 = context.mock(SkuOptionValue.class, "skuOptionValue-2");
		context.checking(new Expectations() {
			{
				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(true));

				allowing(skuOption).getOptionValues();
				will(returnValue(Arrays.asList(skuOptionValue1, skuOptionValue2)));

				allowing(skuOptionValue1).getImage();
				will(returnValue(skuOptionUrl1));
				allowing(skuOptionValue2).getImage();
				will(returnValue(skuOptionUrl2));

				one(skuOptionService).findAllSkuOptionFromCatalog(CATALOG_UID);
				will(returnValue(Collections.singletonList(skuOption)));

				allowing(applicationPropertiesHelper).getPropertiesWithNameStartsWith("asset");
				will(returnValue(Collections.singletonMap(AssetFileManager.PROPERTY_IMAGE_ASSET_SUBFOLDER, imageSubdir)));
			}
		});

		Matcher<String> skuOptionValueImageLocationMatcher1 = constructImageMatcher(imageSubdir, skuOptionUrl1);
		Matcher<String> skuOptionValueImageLocationMatcher2 = constructImageMatcher(imageSubdir, skuOptionUrl2);
		Matcher<Iterable<? super String>> hasItemMatcher1 = hasItem(skuOptionValueImageLocationMatcher1);
		assertThat("skuOptionValue-1 image in registry before export?", exportContext.getDependencyRegistry().getAssetFileNames(),
				not(hasItemMatcher1));
		Matcher<Iterable<? super String>> hasItemMatcher2 = hasItem(skuOptionValueImageLocationMatcher2);
		assertThat("skuOptionValue-2 image in registry before export?", exportContext.getDependencyRegistry().getAssetFileNames(),
				not(hasItemMatcher2));

		skuOptionExporter.findDependentObjects(CATALOG_UID);

		assertThat("skuOptionValue-1 image missing from registry", exportContext.getDependencyRegistry().getAssetFileNames(), hasItemMatcher1);
		assertThat("skuOptionValue-2 image missing from registry", exportContext.getDependencyRegistry().getAssetFileNames(), hasItemMatcher2);
	}
}
