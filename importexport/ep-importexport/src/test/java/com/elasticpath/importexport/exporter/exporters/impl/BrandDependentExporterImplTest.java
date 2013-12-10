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

import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.DigitalAsset;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.util.assets.AssetFileManager;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.importexport.util.ApplicationPropertiesHelper;
import com.elasticpath.service.catalog.BrandService;

/**
 * Test for {@link BrandDependentExporterImpl}.
 */
@RunWith(JMock.class)
@SuppressWarnings({ "PMD.NonStaticInitializer", "PMD.TooManyStaticImports" })
public class BrandDependentExporterImplTest {
	private final BrandDependentExporterImpl brandExporter = new BrandDependentExporterImpl();
	private BrandService brandService;
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
		brandService = context.mock(BrandService.class);
		brandExporter.setBrandService(brandService);

		applicationPropertiesHelper = context.mock(ApplicationPropertiesHelper.class);
		brandExporter.setApplicationPropertiesHelper(applicationPropertiesHelper);

		dependentExporterFilter = context.mock(DependentExporterFilter.class);
		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		brandExporter.initialize(exportContext, dependentExporterFilter);
	}

	/** Tests finding dependent objects when the dependent object should be filtered. */
	@Test
	public void testFindDependentObjectsFiltered() {
		Brand brand1 = context.mock(Brand.class, "brand-1");
		Brand brand2 = context.mock(Brand.class, "brand-2");
		final List<Brand> brandsList = Arrays.asList(brand1, brand2);
		context.checking(new Expectations() {
			{
				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(true));

				one(brandService).findAllBrandsFromCatalog(CATALOG_UID);
				will(returnValue(brandsList));
			}
		});

		assertEquals(brandsList, brandExporter.findDependentObjects(CATALOG_UID));
	}

	/** Tests finding dependent objects when the dependent object should be filtered. */
	@Test
	public void testFindDependentObjectsNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Arrays.<Class<?>> asList(Brand.class));
		exportContext.setDependencyRegistry(registry);

		final long brand1Uid = 9;
		final long brand2Uid = 4541;
		final long brand3Uid = 4;
		registry.addUidDependency(Brand.class, brand1Uid);
		registry.addUidDependencies(Brand.class, new HashSet<Long>(Arrays.asList(brand2Uid, brand3Uid)));

		final Brand brand1 = context.mock(Brand.class, "brand-1");
		final Brand brand2 = context.mock(Brand.class, "brand-2");
		final Brand brand3 = context.mock(Brand.class, "brand-3");
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

				allowing(brand1).getCatalog();
				will(returnValue(dependentCatalog));
				allowing(brand2).getCatalog();
				will(returnValue(otherCatalog));
				allowing(brand3).getCatalog();
				will(returnValue(dependentCatalog));

				one(brandService).get(brand1Uid);
				will(returnValue(brand1));
				one(brandService).get(brand2Uid);
				will(returnValue(brand2));
				one(brandService).get(brand3Uid);
				will(returnValue(brand3));
			}
		});

		List<Brand> result = brandExporter.findDependentObjects(CATALOG_UID);
		assertThat("Missing brand1", result, hasItem(brand1));
		assertThat("Missing brand3", result, hasItem(brand3));
		assertThat("Brand2 is not a part of this catalog", result, not(hasItem(brand2)));
		assertEquals("Other brands returned?", 2, result.size());
	}

	/**
	 * If a dependent {@link Brand} has an image, it should be marked to be exported in the registry (another exporter
	 * handles the actual export).
	 */
	@Test
	public void testFindDependentWithImageNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Arrays.<Class<?>> asList(Brand.class, DigitalAsset.class));
		exportContext.setDependencyRegistry(registry);

		registry.addUidDependency(Brand.class, 1L);

		final String imageSubdir = "some location";
		final String brandUrl = "some url";
		final Brand brand = context.mock(Brand.class);
		context.checking(new Expectations() {
			{
				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(false));

				Catalog dependentCatalog = context.mock(Catalog.class);
				allowing(dependentCatalog).getUidPk();
				will(returnValue(CATALOG_UID));

				allowing(brand).getCatalog();
				will(returnValue(dependentCatalog));
				allowing(brand).getImageUrl();
				will(returnValue(brandUrl));

				one(brandService).get(1L);
				will(returnValue(brand));

				allowing(applicationPropertiesHelper).getPropertiesWithNameStartsWith("asset");
				will(returnValue(Collections.singletonMap(AssetFileManager.PROPERTY_IMAGE_ASSET_SUBFOLDER, imageSubdir)));
			}
		});

		Matcher<String> brandImageLocationMatcher = allOf(startsWith(imageSubdir), endsWith(brandUrl));
		Matcher<Iterable<? super String>> hasItemMatcher = hasItem(brandImageLocationMatcher);
		assertThat("Brand image in registry before export?", exportContext.getDependencyRegistry().getAssetFileNames(), not(hasItemMatcher));

		brandExporter.findDependentObjects(CATALOG_UID);

		assertThat("Missing brand image in the registry", exportContext.getDependencyRegistry().getAssetFileNames(), hasItemMatcher);
	}

	/**
	 * If a dependent {@link Brand} has an image, it should be marked to be exported in the registry (another exporter
	 * handles the actual export). This handles when we are filtered.
	 */
	@Test
	public void testFindDependentWithImageFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Arrays.<Class<?>> asList(Brand.class, DigitalAsset.class));
		exportContext.setDependencyRegistry(registry);

		registry.addUidDependency(Brand.class, 1L);

		final String imageSubdir = "some location";
		final String brandUrl = "some url";
		final Brand brand = context.mock(Brand.class);
		context.checking(new Expectations() {
			{
				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(true));

				allowing(brand).getImageUrl();
				will(returnValue(brandUrl));

				one(brandService).findAllBrandsFromCatalog(CATALOG_UID);
				will(returnValue(Collections.singletonList(brand)));

				allowing(applicationPropertiesHelper).getPropertiesWithNameStartsWith("asset");
				will(returnValue(Collections.singletonMap(AssetFileManager.PROPERTY_IMAGE_ASSET_SUBFOLDER, imageSubdir)));
			}
		});

		Matcher<String> brandImageLocationMatcher = allOf(startsWith(imageSubdir), endsWith(brandUrl));
		Matcher<Iterable<? super String>> hasItemMatcher = hasItem(brandImageLocationMatcher);
		assertThat("Brand image in registry before export?", exportContext.getDependencyRegistry().getAssetFileNames(), not(hasItemMatcher));

		brandExporter.findDependentObjects(CATALOG_UID);

		assertThat("Missing brand image in the registry", exportContext.getDependencyRegistry().getAssetFileNames(), hasItemMatcher);
	}
}
