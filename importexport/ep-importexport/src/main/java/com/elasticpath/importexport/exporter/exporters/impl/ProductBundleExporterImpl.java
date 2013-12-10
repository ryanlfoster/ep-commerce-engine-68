package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.products.bundles.ProductBundleDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.service.catalog.ProductBundleService;

/**
 * . 
 */
public class ProductBundleExporterImpl extends AbstractExporterImpl<ProductBundle, ProductBundleDTO, String> {

	private DomainAdapter<ProductBundle, ProductBundleDTO> productBundleAdapter;

	private ProductBundleService productBundleService;

	@Override
	protected void initializeExporter(final ExportContext context) {
		// do nothing
	}

	@Override
	protected List<ProductBundle> findByIDs(final List<String> subList) {
		return productBundleService.findByGuids(subList);
	}

	@Override
	protected DomainAdapter<ProductBundle, ProductBundleDTO> getDomainAdapter() {
		return productBundleAdapter;
	}

	@Override
	protected Class<? extends ProductBundleDTO> getDtoClass() {
		return ProductBundleDTO.class;
	}

	@Override
	protected List<String> getListExportableIDs() {
		return new ArrayList<String>(getContext().getDependencyRegistry().getDependentGuids(ProductBundle.class));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?>[] getDependentClasses() {
		return new Class[] { ProductBundle.class };
	}

	@Override
	public JobType getJobType() {
		return JobType.PRODUCTBUNDLE;
	}

	/**
	 * @param productBundleAdapter the productBundleAdapter to set
	 */
	public void setProductBundleAdapter(final DomainAdapter<ProductBundle, ProductBundleDTO> productBundleAdapter) {
		this.productBundleAdapter = productBundleAdapter;
	}

	/**
	 * @param productBundleService the productBundleService to set
	 */
	public void setProductBundleService(final ProductBundleService productBundleService) {
		this.productBundleService = productBundleService;
	}

}
