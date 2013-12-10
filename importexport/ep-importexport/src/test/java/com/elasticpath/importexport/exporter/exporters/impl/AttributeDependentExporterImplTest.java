package com.elasticpath.importexport.exporter.exporters.impl;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.service.attribute.AttributeService;
import com.elasticpath.service.catalog.CatalogService;

/**
 * Test for {@link AttributeDependentExporterImpl}.
 */
@RunWith(JMock.class)
@SuppressWarnings({ "PMD.NonStaticInitializer", "PMD.TooManyStaticImports" })
public class AttributeDependentExporterImplTest {
	private final AttributeDependentExporterImpl attributeExporter = new AttributeDependentExporterImpl();
	private AttributeService attributeService;
	private CatalogService catalogService;
	private DependentExporterFilter dependentExporterFilter;
	private ExportContext exportContext;
	private Catalog catalog;
	private final Mockery context = new JUnit4Mockery();
	private static final long CATALOG_UID = 885141;

	/**
	 * Test initialization.
	 * 
	 * @throws ConfigurationException in case of errors
	 */
	@Before
	public void setUp() throws ConfigurationException {
		attributeService = context.mock(AttributeService.class);
		attributeExporter.setAttributeService(attributeService);

		catalogService = context.mock(CatalogService.class);
		attributeExporter.setCatalogService(catalogService);

		catalog = context.mock(Catalog.class);
		context.checking(new Expectations() {
			{
				allowing(catalogService).getCatalog(CATALOG_UID);
				will(returnValue(catalog));

				allowing(catalog).getUidPk();
				will(returnValue(CATALOG_UID));
			}
		});

		dependentExporterFilter = context.mock(DependentExporterFilter.class);
		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		attributeExporter.initialize(exportContext, dependentExporterFilter);
	}

	/** Virtual catalogs should not have their attributes exported. */
	@Test
	public void testCatalogIsNotMaster() {
		context.checking(new Expectations() {
			{
				allowing(catalog).isMaster();
				will(returnValue(false));
			}
		});

		List<Attribute> result = attributeExporter.findDependentObjects(CATALOG_UID);
		assertNotNull(result);
		assertTrue("Attributes shouldn't be exported with virtual catalogs", result.isEmpty());
	}

	/** Tests finding dependent objects when the dependent object should be filtered. */
	@Test
	public void testFindDependentObjectsFiltered() {
		final Attribute attribute1 = context.mock(Attribute.class, "attribute-1");
		final Attribute attribute2 = context.mock(Attribute.class, "attribute-2");
		final Attribute attribute3 = context.mock(Attribute.class, "attribute-3");
		context.checking(new Expectations() {
			{
				allowing(catalog).isMaster();
				will(returnValue(true));

				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(true));

				Catalog otherCatalog = context.mock(Catalog.class, "otherCatalog");
				allowing(otherCatalog).getUidPk();
				will(returnValue(0L));

				allowing(attribute1).getCatalog();
				will(returnValue(catalog));
				allowing(attribute2).getCatalog();
				will(returnValue(otherCatalog));
				allowing(attribute3).getCatalog();
				will(returnValue(catalog));

				one(attributeService).findAllCatalogOrGlobalAttributes(CATALOG_UID);
				will(returnValue(Arrays.asList(attribute1, attribute2, attribute3)));
			}
		});

		List<Attribute> result = attributeExporter.findDependentObjects(CATALOG_UID);
		assertThat("Missing attribute1", result, hasItem(attribute1));
		assertThat("Missing attribute3", result, hasItem(attribute3));
		assertThat("attribute2 is not part of this catalog", result, not(hasItem(attribute2)));
		assertEquals("Other attributes in list?", 2, result.size());
	}

	/** Tests finding dependent objects when the dependent object should be filtered. */
	@Test
	public void testFindDependentObjectsNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Arrays.<Class<?>> asList(Attribute.class));
		exportContext.setDependencyRegistry(registry);

		final long attribute1Uid = 9;
		final long attribute2Uid = 4541;
		final long attribute3Uid = 4;
		final long attribute4Uid = 5511151;
		registry.addUidDependency(Attribute.class, attribute1Uid);
		registry.addUidDependencies(Attribute.class, new HashSet<Long>(Arrays.asList(attribute2Uid, attribute3Uid)));
		registry.addUidDependency(Attribute.class, attribute4Uid);

		final Attribute attribute1 = context.mock(Attribute.class, "attribute-globalDepCatalog");
		final Attribute attribute2 = context.mock(Attribute.class, "attribute-otherCatalog");
		final Attribute attribute3 = context.mock(Attribute.class, "attribute-depCatalog");
		final Attribute attribute4 = context.mock(Attribute.class, "attribute-globalOtherCatalog");
		context.checking(new Expectations() {
			{
				allowing(catalog).isMaster();
				will(returnValue(true));

				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(false));

				Catalog dependentCatalog = context.mock(Catalog.class, "dependentCatalog");
				Catalog otherCatalog = context.mock(Catalog.class, "otherCatalog");
				allowing(dependentCatalog).getUidPk();
				will(returnValue(CATALOG_UID));
				allowing(otherCatalog).getUidPk();
				will(returnValue(0L));

				allowing(attribute1).getCatalog();
				will(returnValue(dependentCatalog));
				allowing(attribute1).isGlobal();
				will(returnValue(true));
				allowing(attribute2).getCatalog();
				will(returnValue(otherCatalog));
				allowing(attribute2).isGlobal();
				will(returnValue(false));
				allowing(attribute3).getCatalog();
				will(returnValue(dependentCatalog));
				allowing(attribute3).isGlobal();
				will(returnValue(false));
				allowing(attribute4).getCatalog();
				will(returnValue(otherCatalog));
				allowing(attribute4).isGlobal();
				will(returnValue(true));

				one(attributeService).get(attribute1Uid);
				will(returnValue(attribute1));
				one(attributeService).get(attribute2Uid);
				will(returnValue(attribute2));
				one(attributeService).get(attribute3Uid);
				will(returnValue(attribute3));
				one(attributeService).get(attribute4Uid);
				will(returnValue(attribute4));
			}
		});

		final int expectedAttributes = 3;
		List<Attribute> result = attributeExporter.findDependentObjects(CATALOG_UID);
		assertThat("Missing attribute1", result, hasItem(attribute1));
		assertThat("Missing attribute3", result, hasItem(attribute3));
		assertThat("attribute2 isn't part of this catalog", result, not(hasItem(attribute2)));
		assertThat("attribute4 isn't part of this catalog, but is global", result, hasItem(attribute4));
		assertEquals("Other attributes returned?", expectedAttributes, result.size());
	}
}
