package com.elasticpath.importexport.exporter.exporters.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.ElasticPath;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.domain.catalog.impl.ProductImpl;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.configuration.ConfigurationOption;
import com.elasticpath.importexport.common.dto.products.ProductDTO;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.impl.SummaryImpl;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.ExporterConfiguration;
import com.elasticpath.importexport.exporter.configuration.OptionalExporterConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.catalog.ProductService;

/**
 * Test <code>ProductExporterImpl</code>.
 */
public class ProductExporterImplTest {

	private static final long PRODUCT_UID = 1234L;

	private static final String QUERY = "FIND Product WHERE CategoryCode='1000009'";
	
	private static final String SPECIFIC_QUERY = "FIND Product WHERE ProductCode='1005420'";


	private ProductExporterImpl productExporter = null;

	private ExportContext exportContext;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ElasticPath mockElasticPath;

	private ProductService mockProductService;

	private ImportExportSearcher mockImportExportSearcher;
	
	private DomainAdapter<Product, ProductDTO> mockProductAdapter;

	private ExportConfiguration exportConfiguration;
	
	private SearchConfiguration searchConfiguration;
	

	/**
	 * Prepare for tests.
	 * 
	 * @throws Exception in case of error happens
	 */
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		mockElasticPath = context.mock(ElasticPath.class);
		mockProductService = context.mock(ProductService.class);
		mockImportExportSearcher = context.mock(ImportExportSearcher.class);
		mockProductAdapter = context.mock(DomainAdapter.class);

		context.checking(new Expectations() {
			{
				allowing(mockProductService).findByUids(with(any(Collection.class)), with(aNull(ProductLoadTuner.class)));
				will(returnValue(getFoundedProductsList()));
				allowing(mockProductAdapter).populateDTO(with(any(Product.class)), with(any(ProductDTO.class)));
				allowing(mockProductAdapter).createDtoObject();
				will(returnValue(new ProductDTO()));
			}
		});

		productExporter = new ProductExporterImpl();
		productExporter.setElasticPath(mockElasticPath);
		productExporter.setImportExportSearcher(mockImportExportSearcher);
		productExporter.setProductService(mockProductService);
		productExporter.setProductAdapter(mockProductAdapter);
	}

	/**
	 * Check that during initialization exporter prepares the list of UidPk for products to be exported. 
	 */
	@Test
	public void testExporterInitializationWithRetrieveAllProductsQuery() throws Exception {
		final long uid = PRODUCT_UID;
		final List<Long> productUidPkList = new ArrayList<Long>();
		productUidPkList.add(uid);

		exportConfiguration = new ExportConfiguration();
		
		ExporterConfiguration exporterConfiguration = new ExporterConfiguration();
		List<OptionalExporterConfiguration> optionalExporterConfigurationList = new ArrayList<OptionalExporterConfiguration>();

		OptionalExporterConfiguration optionalExporterConfiguration = new OptionalExporterConfiguration();
		optionalExporterConfiguration.setType(JobType.PRODUCTASSOCIATION);
		List<ConfigurationOption> options = new ArrayList<ConfigurationOption>();
		ConfigurationOption option = new ConfigurationOption();
		option.setKey(ProductExporterImpl.DIRECT_ONLY);
		option.setValue(Boolean.TRUE.toString());
		options.add(option);
		optionalExporterConfiguration.setOptions(options);
		optionalExporterConfigurationList.add(optionalExporterConfiguration);

		exporterConfiguration.setOptionalExporterConfigurationList(optionalExporterConfigurationList);
		exportConfiguration.setExporterConfiguration(exporterConfiguration);

		searchConfiguration = new SearchConfiguration();
		searchConfiguration.setEpQLQuery(QUERY);
		exportContext = new ExportContext(exportConfiguration, searchConfiguration);
		exportContext.setDependencyRegistry(new DependencyRegistry(new ArrayList<Class<?>>()));
		exportContext.setSummary(new SummaryImpl());


		context.checking(new Expectations() {
			{
				oneOf(mockImportExportSearcher).searchUids(searchConfiguration, EPQueryType.PRODUCT);
				will(returnValue(productUidPkList));
			}
		});

		productExporter.initialize(exportContext);
	}

	/**
	 * Check that during initialization exporter prepares the list of UidPk for products to be exported. Method
	 * searchCriteriaAdapter.populateDomain(exportSearchCriteria, searchCriteria) is expected because exportConfiguration contains
	 * productExportCriteria to be transformed into searchCriteria.
	 */
	@Test
	public void testExporterInitializationWithCriteria() throws Exception {
		final long uid = PRODUCT_UID;
		final List<Long> productUidPkList = new ArrayList<Long>();
		productUidPkList.add(uid);

		setUpExportContextWithProductExportCriteria();

		context.checking(new Expectations() {
			{
				oneOf(mockImportExportSearcher).searchUids(searchConfiguration, EPQueryType.PRODUCT);
				will(returnValue(productUidPkList));
			}
		});
		
		productExporter.initialize(exportContext);
	}

	/**
	 * Configure export query with specific product export criteria.
	 */
	private void setUpExportContextWithProductExportCriteria() {
		exportConfiguration = new ExportConfiguration();
		
		ExporterConfiguration exporterConfiguration = new ExporterConfiguration();
		List<OptionalExporterConfiguration> optionalExporterConfigurationList = new ArrayList<OptionalExporterConfiguration>();
		
		OptionalExporterConfiguration optionalExporterConfiguration = new OptionalExporterConfiguration();
		optionalExporterConfiguration.setType(JobType.PRODUCTASSOCIATION);
		List<ConfigurationOption> options = new ArrayList<ConfigurationOption>();
		ConfigurationOption option = new ConfigurationOption();
		option.setKey(ProductExporterImpl.DIRECT_ONLY);
		option.setValue(Boolean.TRUE.toString());
		options.add(option);
		optionalExporterConfiguration.setOptions(options);
		optionalExporterConfigurationList.add(optionalExporterConfiguration);

		exporterConfiguration.setOptionalExporterConfigurationList(optionalExporterConfigurationList);
		exportConfiguration.setExporterConfiguration(exporterConfiguration);
		
		searchConfiguration = new SearchConfiguration();
		searchConfiguration.setEpQLQuery(SPECIFIC_QUERY);
		exportContext = new ExportContext(exportConfiguration, searchConfiguration);
		exportContext.setDependencyRegistry(new DependencyRegistry(new ArrayList<Class<?>>()));
		exportContext.setSummary(new SummaryImpl());
	}
	
	private List<Product> getFoundedProductsList() {
		List<Product> productList = new ArrayList<Product>();
		Product product = new ProductImpl();
		long uid = PRODUCT_UID;
		product.setUidPk(uid);
		productList.add(product);
		return productList;
	}

	/**
	 * Check an export of one product without export criteria.
	 */
	@Test
	public void testProcessExportWithoutCriteria() throws Exception {
		testExporterInitializationWithRetrieveAllProductsQuery();
		productExporter.processExport(System.out);
		Summary summary = productExporter.getContext().getSummary();
		assertEquals(1, summary.getCounters().size());
		assertNotNull(summary.getCounters().get(JobType.PRODUCT));
		assertEquals(1, summary.getCounters().get(JobType.PRODUCT).intValue());
		assertEquals(0, summary.getFailures().size());
		assertNotNull(summary.getStartDate());
		assertNotNull(summary.getElapsedTime());
		assertNotNull(summary.getElapsedTime().toString());
	}

	/**
	 * Check an export of one product with export criteria.
	 */
	@Test
	public void testProcessExportWithCriteria() throws Exception {
		testExporterInitializationWithCriteria();
		productExporter.processExport(System.out);
		Summary summary = productExporter.getContext().getSummary();
		assertEquals(1, summary.getCounters().size());
		assertNotNull(summary.getCounters().get(JobType.PRODUCT));
		assertEquals(1, summary.getCounters().get(JobType.PRODUCT).intValue());
		assertEquals(0, summary.getFailures().size());
		assertNotNull(summary.getStartDate());
		assertNotNull(summary.getElapsedTime());
		assertNotNull(summary.getElapsedTime().toString());
	}
}
