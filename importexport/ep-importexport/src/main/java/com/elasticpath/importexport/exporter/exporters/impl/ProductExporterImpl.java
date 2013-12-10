package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.products.ProductDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.exporter.configuration.OptionalExporterConfiguration;
import com.elasticpath.importexport.exporter.context.DependencyRegistry;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.importexport.exporter.search.ImportExportSearcher;
import com.elasticpath.importexport.util.ApplicationPropertiesHelper;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.service.catalog.ProductAssociationService;
import com.elasticpath.service.catalog.ProductService;

/**
 * Exporter prepares the list of ProductAdapters based on query and executes export job.
 */
public class ProductExporterImpl extends AbstractExporterImpl<Product, ProductDTO, Long> {

	private ProductService productService;

	private DomainAdapter<Product, ProductDTO> productAdapter;

	private ProductLoadTuner productLoadTuner;

	private List<Long> productUidPkList = Collections.emptyList();

	private List<Long> futureNonDependant = null;

	private ProductAssociationService productAssociationService;
	
	private ImportExportSearcher importExportSearcher;
	
	private ApplicationPropertiesHelper applicationPropertiesHelper;

	/**
	 * Key for specific option: export only one association distant products or all products in graph.
	 */
	public static final String DIRECT_ONLY = "DIRECT_ONLY";

	private static final Logger LOG = Logger.getLogger(ProductExporterImpl.class);

	/**
	 * {@inheritDoc} throws RuntimeException can be thrown if product uid list could not be initialized.
	 */
	@Override
	protected void initializeExporter(final ExportContext context) throws ConfigurationException {
		productUidPkList = importExportSearcher.searchUids(getContext().getSearchConfiguration(), EPQueryType.PRODUCT);
		LOG.info("The UidPk list for " + productUidPkList.size() + " products is retrieved from database.");

		OptionalExporterConfiguration exporterConfiguration = context.getExportConfiguration().getExporterConfiguration()
				.getOptionalExporterConfiguration(JobType.PRODUCTASSOCIATION);

		if (exporterConfiguration != null && Boolean.TRUE.toString().equals(exporterConfiguration.getOption(DIRECT_ONLY))) {
			futureNonDependant = new ArrayList<Long>();
		}
	}

	@Override
	protected List<Long> getListExportableIDs() {
		return productUidPkList;
	}

	@Override
	protected List<Product> findByIDs(final List<Long> subList) {
		return productService.findByUids(subList, productLoadTuner);
	}

	@Override
	protected DomainAdapter<Product, ProductDTO> getDomainAdapter() {
		return productAdapter;
	}

	@Override
	protected Class<? extends ProductDTO> getDtoClass() {
		return ProductDTO.class;
	}

	@Override
	public JobType getJobType() {
		return JobType.PRODUCT;
	}

	@Override
	protected void exportFailureHandler(final Product object) {
		LOG.error(new Message("IE-20800", object.getCode()));
	}

	@Override
	protected void addDependencies(final List<Product> products, final DependencyRegistry dependencyRegistry) {
		ProductDependencyHelper helper = new ProductDependencyHelper(dependencyRegistry, productUidPkList, futureNonDependant,
				productAssociationService, getApplicationPropertiesHelper());
		helper.addDependencies(products);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?>[] getDependentClasses() {
		return new Class[] {};
	}

	/**
	 * Gets the productService.
	 * 
	 * @return the productService
	 */
	public ProductService getProductService() {
		return productService;
	}

	/**
	 * Sets the productService.
	 * 
	 * @param productService the productService to set
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}

	/**
	 * Gets the productAdapter.
	 * 
	 * @return the productAdapter
	 */
	public DomainAdapter<Product, ProductDTO> getProductAdapter() {
		return productAdapter;
	}

	/**
	 * Sets the productAdapter.
	 * 
	 * @param productAdapter the productAdapter to set
	 */
	public void setProductAdapter(final DomainAdapter<Product, ProductDTO> productAdapter) {
		this.productAdapter = productAdapter;
	}

	/**
	 * Gets the productLoadTuner.
	 * 
	 * @return the productLoadTuner
	 */
	public ProductLoadTuner getProductLoadTuner() {
		return productLoadTuner;
	}

	/**
	 * Sets the productLoadTuner.
	 * 
	 * @param productLoadTuner the productLoadTuner to set
	 */
	public void setProductLoadTuner(final ProductLoadTuner productLoadTuner) {
		this.productLoadTuner = productLoadTuner;
	}

	/**
	 * Gets the productAssociationService.
	 * 
	 * @return the productAssociationService
	 */
	public ProductAssociationService getProductAssociationService() {
		return productAssociationService;
	}

	/**
	 * Sets the productAssociationService.
	 * 
	 * @param productAssociationService the productAssociationService to set
	 */
	public void setProductAssociationService(final ProductAssociationService productAssociationService) {
		this.productAssociationService = productAssociationService;
	}

	/**
	 * Gets importExportSearcher.
	 * 
	 * @return importExportSearcher
	 */
	public ImportExportSearcher getImportExportSearcher() {
		return importExportSearcher;
	}

	/**
	 * @param importExportSearcher the ImportExportSearcher
	 */
	public void setImportExportSearcher(final ImportExportSearcher importExportSearcher) {
		this.importExportSearcher = importExportSearcher;
	}

	protected ApplicationPropertiesHelper getApplicationPropertiesHelper() {
		return applicationPropertiesHelper;
	}

	public void setApplicationPropertiesHelper(final ApplicationPropertiesHelper applicationPropertiesHelper) {
		this.applicationPropertiesHelper = applicationPropertiesHelper;
	}
}
