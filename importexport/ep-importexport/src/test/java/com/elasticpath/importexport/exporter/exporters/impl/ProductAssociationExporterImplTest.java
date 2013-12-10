package com.elasticpath.importexport.exporter.exporters.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.junit.Assert;

import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductAssociationLoadTuner;
import com.elasticpath.domain.catalog.impl.CatalogImpl;
import com.elasticpath.domain.catalog.impl.ProductAssociationImpl;
import com.elasticpath.importexport.common.adapters.associations.ProductAssociationAdapter;
import com.elasticpath.importexport.common.dto.productassociation.ProductAssociationDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.impl.SummaryImpl;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.service.catalog.ProductAssociationService;

/**
 * ProductAssociation exporter test.
 */
public class ProductAssociationExporterImplTest {
	
	private static final Long ASSOCIATION_UID = 1L;

	private ProductAssociationExporterImpl productAssociationExporter = null;

	private ExportContext exportContext;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ElasticPath mockElasticPath;

	private ProductAssociationService mockProductAssociationService;
	

	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of error happens
	 */
	@Before
	public void setUp() throws Exception {
		mockElasticPath = context.mock(ElasticPath.class);
		mockProductAssociationService = context.mock(ProductAssociationService.class);
		
		final ProductAssociation productAssociation = new ProductAssociationImpl();
		productAssociation.setCatalog(new CatalogImpl());
		context.checking(new Expectations() {
			{
				allowing(mockProductAssociationService).getTuned(with(any(long.class)), with(aNull(ProductAssociationLoadTuner.class)));
				will(returnValue(productAssociation));
			}
		});

		productAssociationExporter = new ProductAssociationExporterImpl();
		productAssociationExporter.setElasticPath(mockElasticPath);
		productAssociationExporter.setProductAssociationService(mockProductAssociationService);
		productAssociationExporter.setProductAssociationAdapter(new MockProductAssociationAdapter());
	}
	
	/**
	 * Check that during initialization exporter prepares the list of UidPk for product associations to be exported.
	 */
	@Test
	public void testExporterInitialization() {
		final List<Long> associationUidPkList = new ArrayList<Long>();
		associationUidPkList.add(ASSOCIATION_UID);

		ExportConfiguration exportConfiguration = new ExportConfiguration();
		SearchConfiguration searchConfiguration = new SearchConfiguration();
		
		exportContext = new ExportContext(exportConfiguration, searchConfiguration);
		exportContext.setSummary(new SummaryImpl());
		
		List<Class< ? >> dependentClasses = new ArrayList<Class< ? >>();
		dependentClasses.add(ProductAssociation.class);
		DependencyRegistry dependencyRegistry = new DependencyRegistry(dependentClasses);
		
		dependencyRegistry.addUidDependencies(ProductAssociation.class, new HashSet<Long>(associationUidPkList));
		exportContext.setDependencyRegistry(dependencyRegistry);
		
		try {
			productAssociationExporter.initialize(exportContext);
		} catch (ConfigurationException e) {
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * Check an export of product associations.
	 */
	@Test
	public void testProcessExport() {
		testExporterInitialization();
		productAssociationExporter.processExport(System.out);
		Summary summary = productAssociationExporter.getContext().getSummary();
		assertEquals(1, summary.getCounters().size());
		assertNotNull(summary.getCounters().get(JobType.PRODUCTASSOCIATION));
		assertEquals(1, summary.getCounters().get(JobType.PRODUCTASSOCIATION).intValue());
		assertEquals(0, summary.getFailures().size());
		assertNotNull(summary.getStartDate());
		assertNotNull(summary.getElapsedTime());
		assertNotNull(summary.getElapsedTime().toString());
	}
	
	/**
	 * Mock product association adapter.
	 */
	private class MockProductAssociationAdapter extends ProductAssociationAdapter {

		@Override
		public void populateDomain(final ProductAssociationDTO source, final ProductAssociation target) {
			//do nothing
		}

		@Override
		public void populateDTO(final ProductAssociation source, final ProductAssociationDTO target) {
			// do nothing
		}	
	}
}
