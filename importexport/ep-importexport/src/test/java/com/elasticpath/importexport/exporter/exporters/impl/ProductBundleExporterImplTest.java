package com.elasticpath.importexport.exporter.exporters.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.products.bundles.ProductBundleDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.ExporterConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.service.catalog.ProductBundleService;

/**
 * The test for ProductBundleExporter.
 */
public class ProductBundleExporterImplTest {
	
	private ProductBundleExporterImpl productBundleExporterImpl;
	
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();
	
	@SuppressWarnings("unchecked")
	private final DomainAdapter<ProductBundle, ProductBundleDTO> productBundleAdapter = context.mock(DomainAdapter.class);
	
	private final ProductBundleService productBundleService = context.mock(ProductBundleService.class);
	
	private final ProductBundle productBundle = context.mock(ProductBundle.class);
	
	private ExportConfiguration exportConfiguration;

	private SearchConfiguration searchConfiguration;
	
	private static final String DEPENDENT_GUID_1 = "10000001";
	
	private static final String DEPENDENT_GUID_2 = "10000002";
	
	private static final String DEPENDENT_GUID_3 = "10000003";

	private static final int THREE = 3;
	
	 /** SetUps the test. */
	@Before
	public void setUp() {
		this.productBundleExporterImpl = new ProductBundleExporterImpl();
		this.productBundleExporterImpl.setProductBundleService(productBundleService);
	}

	/**
	 * Test method for {@link ProductBundleExporterImpl#findByIDs(List)}.
	 */	
	@Test
	public void testFindByIDsListOfString() {
		
		final List<String> guids = Arrays.asList("10001", "10002");
		
		context.checking(new Expectations() { {
			oneOf(productBundleService).findByGuids(guids); will(returnValue(Arrays.asList(productBundle)));
		} });
		
		List<ProductBundle> findByIDs = productBundleExporterImpl.findByIDs(guids);
		
		assertEquals(1, findByIDs.size());
		assertSame(productBundle, findByIDs.get(0));
	}

	/**
	 * Test method for {@link ProductBundleExporterImpl#getDomainAdapter()}.
	 */	
	@Test
	public void testGetDomainAdapter() {
		
		productBundleExporterImpl.setProductBundleAdapter(productBundleAdapter);
		
		assertEquals(productBundleAdapter, productBundleExporterImpl.getDomainAdapter());
	}

	/**
	 * Test method for {@link ProductBundleExporterImpl#getDtoClass()}.
	 */	
	@Test
	public void testGetDtoClass() {
		assertEquals(ProductBundleDTO.class, productBundleExporterImpl.getDtoClass());
	}

	/**
	 * Test method for {@link ProductBundleExporterImpl#getListExportableIDs()}.
	 * @throws ConfigurationException not expected exception.
	 */	
	@Test
	public void testGetListExportableIDs() throws ConfigurationException {
		exportConfiguration = new ExportConfiguration();
		searchConfiguration = new SearchConfiguration();
		
		productBundleExporterImpl.initialize(new ExportContext(exportConfiguration, searchConfiguration));
		productBundleExporterImpl.getContext().setDependencyRegistry(new DependencyRegistry(Arrays.asList(new Class< ? >[]{ProductBundle.class})));
		
		
		
		ExporterConfiguration exporterConfiguration = new ExporterConfiguration();
		exportConfiguration.setExporterConfiguration(exporterConfiguration);
		
		productBundleExporterImpl.getContext().getDependencyRegistry().addGuidDependency(ProductBundle.class, DEPENDENT_GUID_1);
		productBundleExporterImpl.getContext().getDependencyRegistry().addGuidDependency(ProductBundle.class, DEPENDENT_GUID_2);
		productBundleExporterImpl.getContext().getDependencyRegistry().addGuidDependency(ProductBundle.class, DEPENDENT_GUID_3);
		
		HashSet<String> expectedSet = new HashSet<String>();
		expectedSet.add(DEPENDENT_GUID_1);
		expectedSet.add(DEPENDENT_GUID_2);
		expectedSet.add(DEPENDENT_GUID_3);
		
		
		final List<String> listExportableIDs = productBundleExporterImpl.getListExportableIDs();
		
		assertEquals(THREE, listExportableIDs.size());
		assertTrue(listExportableIDs.contains(DEPENDENT_GUID_1));
		assertTrue(listExportableIDs.contains(DEPENDENT_GUID_2));
		assertTrue(listExportableIDs.contains(DEPENDENT_GUID_3));
	}

	/**
	 * Test method for {@link ProductBundleExporterImpl#getDependentClasses()}.
	 */	
	@Test
	public void testGetDependentClasses() {
		assertEquals(1, productBundleExporterImpl.getDependentClasses().length);
		assertEquals(ProductBundle.class, productBundleExporterImpl.getDependentClasses()[0]);
	}

	/**
	 * Test method for {@link ProductBundleExporterImpl#getJobType()}.
	 */	
	@Test
	public void testGetJobType() {
		assertEquals(JobType.PRODUCTBUNDLE, productBundleExporterImpl.getJobType());
	}

}
