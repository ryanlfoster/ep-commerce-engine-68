package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.pricing.ProductPricesAdapter;
import com.elasticpath.importexport.common.dto.pricing.ProductPricesDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.service.catalog.ProductService;

/**
 * Exporter implementation for product and SKU pricing.
 */
public class PricingExporterImpl extends AbstractExporterImpl<Product, ProductPricesDTO, Long> {
	 	
	private ProductPricesAdapter productPricesAdapter;
	
	private ProductService productService;
	
	private ProductLoadTuner productLoadTuner;

	@Override
	protected void initializeExporter(final ExportContext context) {
		// do nothing
	}

	@Override
	protected List<Product> findByIDs(final List<Long> subList) {
		// TODO : Implement finding Products (when PriceLists will be exported here) like this :
		// return productService.findByUids(subList, productLoadTuner);
		return Collections.emptyList(); 
	}

	@Override
	protected DomainAdapter<Product, ProductPricesDTO> getDomainAdapter() {
		return productPricesAdapter;
	}

	@Override
	protected Class<? extends ProductPricesDTO> getDtoClass() {
		return ProductPricesDTO.class;
	}

	@Override
	public JobType getJobType() {
		return JobType.PRICING;
	}

	@Override
	protected List<Long> getListExportableIDs() {
		return new ArrayList<Long>(getContext().getDependencyRegistry().getDependentUids(Product.class));
	}

	@Override
	public Class< ? >[] getDependentClasses() {
		return new Class< ? >[] {Product.class};
	}
	
	@Override
	protected int getObjectsQty(final Product domain) {
		// TODO : Implement the size of BaseAmount calculation for product and its SKUS. 
		return 0;
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
	 * Sets the catalogPricesAdapter.
	 * 
	 * @param productPricesAdapter the catalogPricesAdapter to set
	 */
	public void setProductPricesAdapter(final ProductPricesAdapter productPricesAdapter) {
		this.productPricesAdapter = productPricesAdapter;
	}	
}
