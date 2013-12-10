package com.elasticpath.importexport.exporter.exporters.impl;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeGroup;
import com.elasticpath.domain.attribute.AttributeGroupAttribute;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.service.catalog.CategoryTypeService;

/**
 * Test for {@link CategoryTypeDependentExporterImpl}.
 */
@RunWith(JMock.class)
@SuppressWarnings({ "PMD.NonStaticInitializer", "PMD.TooManyStaticImports" })
public class CategoryTypeDependentExporterImplTest {
	private final CategoryTypeDependentExporterImpl categoryTypeExporter = new CategoryTypeDependentExporterImpl();
	private CategoryTypeService categoryTypeService;
	private DependentExporterFilter dependentExporterFilter;
	private ExportContext exportContext;
	private final Mockery context = new JUnit4Mockery();
	private static int mockCounter;
	private static final long CATALOG_UID = 66478;

	/**
	 * Test initialization.
	 * 
	 * @throws ConfigurationException in case of errors
	 */
	@Before
	public void setUp() throws ConfigurationException {
		categoryTypeService = context.mock(CategoryTypeService.class);
		categoryTypeExporter.setCategoryTypeService(categoryTypeService);

		dependentExporterFilter = context.mock(DependentExporterFilter.class);
		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		categoryTypeExporter.initialize(exportContext, dependentExporterFilter);
	}

	/** Tests finding dependent objects when the dependent object should be filtered. */
	@Test
	public void testFindDependentObjectsFiltered() {
		CategoryType categoryType1 = context.mock(CategoryType.class, "categoryType-1");
		CategoryType categoryType2 = context.mock(CategoryType.class, "categoryType-2");
		final List<CategoryType> categoryTypeList = Arrays.asList(categoryType1, categoryType2);
		context.checking(new Expectations() {
			{
				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(true));

				one(categoryTypeService).findAllCategoryTypeFromCatalog(CATALOG_UID);
				will(returnValue(categoryTypeList));
			}
		});

		assertEquals(categoryTypeList, categoryTypeExporter.findDependentObjects(CATALOG_UID));
	}

	private Attribute mockAttribute(final long uid, final boolean global, final Catalog catalog) {
		final Attribute attribute = context.mock(Attribute.class, "attribute-" + ++mockCounter);

		context.checking(new Expectations() {
			{
				allowing(attribute).getUidPk();
				will(returnValue(uid));
				allowing(attribute).isGlobal();
				will(returnValue(global));
				allowing(attribute).getCatalog();
				will(returnValue(catalog));
			}
		});

		return attribute;
	}

	private void setupMocksForAttributes(final CategoryType categoryType, final Attribute... attributes) {
		context.checking(new Expectations() {
			{
				Set<AttributeGroupAttribute> attributeGroupAttributes = new HashSet<AttributeGroupAttribute>();
				if (attributes != null) {
					for (Attribute attribute : attributes) {
						AttributeGroupAttribute attributeGroupAttribute = context.mock(AttributeGroupAttribute.class,
								"attributeGroupAttribute-" + ++mockCounter);
						attributeGroupAttributes.add(attributeGroupAttribute);

						allowing(attributeGroupAttribute).getAttribute();
						will(returnValue(attribute));
					}
				}

				AttributeGroup attributeGroup = context.mock(AttributeGroup.class, "attributeGroup-" + ++mockCounter);
				allowing(attributeGroup).getAttributeGroupAttributes();
				will(returnValue(attributeGroupAttributes));

				allowing(categoryType).getAttributeGroup();
				will(returnValue(attributeGroup));
			}
		});
	}

	/** Tests finding dependent objects when the dependent object should be filtered. */
	@Test
	public void testFindDependentObjectsNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Arrays.<Class<?>> asList(CategoryType.class, Attribute.class));
		exportContext.setDependencyRegistry(registry);

		final long categoryType1Uid = 55612;
		final long categoryType2Uid = 69891;
		final long categoryType3Uid = 16;
		registry.addUidDependency(CategoryType.class, categoryType1Uid);
		registry.addUidDependencies(CategoryType.class, new HashSet<Long>(Arrays.asList(categoryType2Uid, categoryType3Uid)));

		final long commonAttributeUid = 1;
		final long dependentCatalogAttributeUid = 2;
		final long otherCatalogAttributeUid = 3;
		final long globalAttributeUid = 4;
		final CategoryType categoryType1 = context.mock(CategoryType.class, "categoryType-1");
		final CategoryType categoryType2 = context.mock(CategoryType.class, "categoryType-2");
		final CategoryType categoryType3 = context.mock(CategoryType.class, "categoryType-3");
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

				allowing(categoryType1).getCatalog();
				will(returnValue(dependentCatalog));
				allowing(categoryType2).getCatalog();
				will(returnValue(otherCatalog));
				allowing(categoryType3).getCatalog();
				will(returnValue(otherCatalog));

				Attribute commonAttribute = mockAttribute(commonAttributeUid, false, dependentCatalog);
				Attribute dependentCatalogAttribute = mockAttribute(dependentCatalogAttributeUid, false, dependentCatalog);
				Attribute otherCatalogAttribute = mockAttribute(otherCatalogAttributeUid, false, otherCatalog);
				Attribute globalAttribute = mockAttribute(globalAttributeUid, true, otherCatalog);

				setupMocksForAttributes(categoryType1, commonAttribute, dependentCatalogAttribute);
				setupMocksForAttributes(categoryType2, commonAttribute, otherCatalogAttribute);
				setupMocksForAttributes(categoryType3, commonAttribute, globalAttribute);

				one(categoryTypeService).getObject(categoryType1Uid);
				will(returnValue(categoryType1));
				one(categoryTypeService).getObject(categoryType2Uid);
				will(returnValue(categoryType2));
				one(categoryTypeService).getObject(categoryType3Uid);
				will(returnValue(categoryType3));
			}
		});

		List<CategoryType> result = categoryTypeExporter.findDependentObjects(CATALOG_UID);
		assertThat("Missing categoryType1", result, hasItem(categoryType1));
		assertThat("categoryType3 is not a part of this catalog", result, not(hasItem(categoryType3)));
		assertThat("categoryType2 is not a part of this catalog", result, not(hasItem(categoryType2)));
		assertEquals("Other CategoryTypes returned?", 1, result.size());

		final int expectedAttributeDependencies = 3;
		Set<Long> attributeDependencies = registry.getDependentUids(Attribute.class);
		assertThat("Missing commonAttributeUid", attributeDependencies, hasItem(commonAttributeUid));
		assertThat("Missing dependentCatalogAttributeUid", attributeDependencies, hasItem(dependentCatalogAttributeUid));
		assertThat("otherCatalogAttributeUid is for a CategoryType in another catalog", attributeDependencies,
				not(hasItem(otherCatalogAttributeUid)));
		assertThat("Missing globalAttributeUid even though its for another catalog", attributeDependencies, hasItem(globalAttributeUid));
		assertEquals("Other attribute dependencies?", expectedAttributeDependencies, attributeDependencies.size());
	}
}
