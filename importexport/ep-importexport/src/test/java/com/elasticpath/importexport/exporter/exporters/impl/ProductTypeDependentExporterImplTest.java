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
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.exporters.DependentExporterFilter;
import com.elasticpath.service.catalog.ProductTypeService;

/**
 * Test for {@link ProductTypeDependentExporterImpl}.
 */
@RunWith(JMock.class)
@SuppressWarnings({ "PMD.NonStaticInitializer", "PMD.TooManyStaticImports" })
public class ProductTypeDependentExporterImplTest {
	private final ProductTypeDependentExporterImpl productTypeExporter = new ProductTypeDependentExporterImpl();
	private ProductTypeService productTypeService;
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
		productTypeService = context.mock(ProductTypeService.class);
		productTypeExporter.setProductTypeService(productTypeService);

		dependentExporterFilter = context.mock(DependentExporterFilter.class);
		exportContext = new ExportContext(new ExportConfiguration(), new SearchConfiguration());
		productTypeExporter.initialize(exportContext, dependentExporterFilter);
	}

	/** Tests finding dependent objects when the dependent object should be filtered. */
	@Test
	public void testFindDependentObjectsFiltered() {
		ProductType productType1 = context.mock(ProductType.class, "productType-1");
		ProductType productType2 = context.mock(ProductType.class, "productType-2");
		final List<ProductType> productTypeList = Arrays.asList(productType1, productType2);
		context.checking(new Expectations() {
			{
				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(true));

				one(productTypeService).findAllProductTypeFromCatalog(CATALOG_UID);
				will(returnValue(productTypeList));
			}
		});

		assertEquals(productTypeList, productTypeExporter.findDependentObjects(CATALOG_UID));
	}

	private SkuOption mockSkuOption(final long uid) {
		final SkuOption skuOption = context.mock(SkuOption.class, "skuOption-" + ++mockCounter);

		context.checking(new Expectations() {
			{
				allowing(skuOption).getUidPk();
				will(returnValue(uid));
			}
		});

		return skuOption;
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

	private void setupMocksForProductAttributes(final ProductType productType, final Attribute... attributes) {
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

				allowing(productType).getProductAttributeGroupAttributes();
				will(returnValue(attributeGroupAttributes));
			}
		});
	}

	private void setupMocksForSkuAttributes(final ProductType productType, final Attribute... attributes) {
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
				allowing(productType).getSkuAttributeGroup();
				will(returnValue(attributeGroup));

				allowing(attributeGroup).getAttributeGroupAttributes();
				will(returnValue(attributeGroupAttributes));
			}
		});
	}

	private ProductType mockProductType(final String name, final long uid, final Catalog catalog, final SkuOption... skuOption) {
		final ProductType productType = context.mock(ProductType.class, name);

		context.checking(new Expectations() {
			{
				allowing(productType).getUidPk();
				will(returnValue(uid));
				allowing(productType).getCatalog();
				will(returnValue(catalog));
				allowing(productType).getSkuOptions();
				will(returnValue(new HashSet<SkuOption>(Arrays.asList(skuOption))));

				allowing(productTypeService).getObject(uid);
				will(returnValue(productType));
			}
		});

		return productType;
	}

	/** Tests finding dependent objects when the dependent object should be filtered. */
	@Test
	public void testFindDependentObjectsNotFiltered() {
		DependencyRegistry registry = new DependencyRegistry(Arrays.<Class<?>> asList(ProductType.class, Attribute.class, SkuOption.class));
		exportContext.setDependencyRegistry(registry);

		final long productType1Uid = 969;
		final long productType2Uid = 33211894;
		final long productType3Uid = 61387;
		registry.addUidDependency(ProductType.class, productType1Uid);
		registry.addUidDependencies(ProductType.class, new HashSet<Long>(Arrays.asList(productType2Uid, productType3Uid)));

		final long commonSkuOptionUid = 552521;
		final long skuOption1Uid = 151;
		final long skuOption2Uid = 886225;
		final long skuOption3Uid = 6688;

		final long commonAttributeUid = 1;
		final long dependentCatalogProductAttributeUid = 2;
		final long otherCatalogProductAttributeUid = 3;
		final long globalProductAttributeUid = 4;
		final long dependentCatalogSkuAttributeUid = 5;
		final long otherCatalogSkuAttributeUid = 8;
		final long globalSkuAttributeUid = 9;

		final Catalog dependentCatalog = context.mock(Catalog.class, "dependentCatalog");
		final Catalog otherCatalog = context.mock(Catalog.class, "otherCatalog");

		SkuOption commonSkuOption = mockSkuOption(commonSkuOptionUid);
		SkuOption skuOption1 = mockSkuOption(skuOption1Uid);
		SkuOption skuOption2 = mockSkuOption(skuOption2Uid);
		SkuOption skuOption3 = mockSkuOption(skuOption3Uid);

		final ProductType productType1 = mockProductType("productType-1", productType1Uid, dependentCatalog, commonSkuOption, skuOption1);
		final ProductType productType2 = mockProductType("productType-2", productType2Uid, otherCatalog, commonSkuOption, skuOption2);
		final ProductType productType3 = mockProductType("productType-3", productType3Uid, otherCatalog, commonSkuOption, skuOption3);
		context.checking(new Expectations() {
			{
				one(dependentExporterFilter).isFiltered(CATALOG_UID);
				will(returnValue(false));

				allowing(dependentCatalog).getUidPk();
				will(returnValue(CATALOG_UID));
				allowing(otherCatalog).getUidPk();
				will(returnValue(0L));

				Attribute commonAttribute = mockAttribute(commonAttributeUid, false, dependentCatalog);
				Attribute dependentCatalogProductAttribute = mockAttribute(dependentCatalogProductAttributeUid, false, dependentCatalog);
				Attribute otherCatalogProductAttribute = mockAttribute(otherCatalogProductAttributeUid, false, otherCatalog);
				Attribute globalProductAttribute = mockAttribute(globalProductAttributeUid, true, otherCatalog);
				Attribute dependentCatalogSkuAttribute = mockAttribute(dependentCatalogSkuAttributeUid, false, dependentCatalog);
				Attribute otherCatalogSkuAttribute = mockAttribute(otherCatalogSkuAttributeUid, false, otherCatalog);
				Attribute globalSkuAttribute = mockAttribute(globalSkuAttributeUid, true, otherCatalog);

				setupMocksForProductAttributes(productType1, commonAttribute, dependentCatalogProductAttribute);
				setupMocksForProductAttributes(productType2, commonAttribute, otherCatalogProductAttribute);
				setupMocksForProductAttributes(productType3, commonAttribute, globalProductAttribute);
				setupMocksForSkuAttributes(productType1, commonAttribute, dependentCatalogSkuAttribute);
				setupMocksForSkuAttributes(productType2, commonAttribute, otherCatalogSkuAttribute);
				setupMocksForSkuAttributes(productType3, commonAttribute, globalSkuAttribute);
			}
		});

		List<ProductType> result = productTypeExporter.findDependentObjects(CATALOG_UID);
		assertThat("Missing productType1", result, hasItem(productType1));
		assertThat("productType3 is not a part of this catalog", result, not(hasItem(productType3)));
		assertThat("productType2 is not a part of this catalog", result, not(hasItem(productType2)));
		assertEquals("Other ProductTypes returned?", 1, result.size());

		final int expectedAttributeDependencies = 5;
		Set<Long> attributeDependencies = registry.getDependentUids(Attribute.class);
		assertThat("Missing commonAttributeUid", attributeDependencies, hasItem(commonAttributeUid));
		assertThat("Missing dependentCatalogProductAttributeUid", attributeDependencies, hasItem(dependentCatalogProductAttributeUid));
		assertThat("otherCatalogProductAttributeUid is for a ProductType in another catalog", attributeDependencies,
				not(hasItem(otherCatalogProductAttributeUid)));
		assertThat("Missing globalProductAttributeUid even though its for another catalog", attributeDependencies,
				hasItem(globalProductAttributeUid));
		assertThat("Missing dependentCatalogSkuAttributeUid", attributeDependencies, hasItem(dependentCatalogSkuAttributeUid));
		assertThat("otherCatalogSkuAttributeUid is for a ProductType in another catalog", attributeDependencies,
				not(hasItem(otherCatalogSkuAttributeUid)));
		assertThat("Missing globalSkuAttributeUid even though its for another catalog", attributeDependencies,
				hasItem(globalSkuAttributeUid));
		assertEquals("Other attribute dependencies?", expectedAttributeDependencies, attributeDependencies.size());

		final int expectedSkuOptions = 4;
		Set<Long> skuOptionDependencies = registry.getDependentUids(SkuOption.class);
		assertThat("Missing commonSkuOptionUid", skuOptionDependencies, hasItem(commonSkuOptionUid));
		assertThat("Missing skuOption1Uid", skuOptionDependencies, hasItem(skuOption1Uid));
		assertThat("Missing skuOption2Uid", skuOptionDependencies, hasItem(skuOption2Uid));
		assertThat("Missing skuOption3Uid", skuOptionDependencies, hasItem(skuOption3Uid));
		assertEquals("Other sku option dependencies?", expectedSkuOptions, skuOptionDependencies.size());
	}
}
