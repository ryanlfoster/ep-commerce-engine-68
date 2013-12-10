package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.domain.catalog.ProductAssociationLoadTuner;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.adapters.associations.ProductAssociationAdapter;
import com.elasticpath.importexport.common.dto.productassociation.ProductAssociationDTO;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.exporter.context.ExportContext;
import com.elasticpath.service.catalog.ProductAssociationService;

/**
 * Exporter retrieves the list of ProductAssociation UIDs from DependencyRegistry and executes export job.
 */
public class ProductAssociationExporterImpl extends AbstractExporterImpl<ProductAssociation, ProductAssociationDTO, Long> {

	private ProductAssociationService productAssociationService;
	
	private ProductAssociationAdapter productAssociationAdapter;
	
	private static final Logger LOG = Logger.getLogger(ProductAssociationExporterImpl.class);
	
	private ProductAssociationLoadTuner productAssociationLoadTuner;
	
	@Override
	protected void initializeExporter(final ExportContext context) {
		// do nothing
	}
	
	@Override
	protected List<ProductAssociation> findByIDs(final List<Long> subList) {
		final List<ProductAssociation> productAssociationList = new ArrayList<ProductAssociation>();
		for (Long associationUid : subList) {
			ProductAssociation association = productAssociationService.getTuned(associationUid, productAssociationLoadTuner);
			
			if (association == null) {
				LOG.error(new Message("IE-20901", associationUid.toString()));
				continue;
			}
			
			if (association.getCatalog() == null && productAssociationService.isAssociationInCatalog(association)) {
				association.setCatalog(association.getSourceProduct().getMasterCatalog());
			}
			
			productAssociationList.add(association);
		}
		return productAssociationList;
	}

	@Override
	protected DomainAdapter<ProductAssociation, ProductAssociationDTO> getDomainAdapter() {
		return productAssociationAdapter;
	}

	@Override
	protected Class<? extends ProductAssociationDTO> getDtoClass() {
		return ProductAssociationDTO.class;
	}

	@Override
	public JobType getJobType() {
		return JobType.PRODUCTASSOCIATION;
	}

	@Override
	protected List<Long> getListExportableIDs() {
		return new ArrayList<Long>(getContext().getDependencyRegistry().getDependentUids(ProductAssociation.class));
	}

	
	@Override
	protected void exportFailureHandler(final ProductAssociation object) {
		LOG.error(new Message("IE-20900", object.getGuid()));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class<?>[] getDependentClasses() {
		return new Class[] { ProductAssociation.class };
	}

	/**
	 * Gets product association service.
	 * 
	 * @return product association service
	 */
	public ProductAssociationService getProductAssociationService() {
		return productAssociationService;
	}
	
	/**
	 * Sets product association service.
	 * 
	 * @param productAssociationService product association service
	 */
	public void setProductAssociationService(final ProductAssociationService productAssociationService) {
		this.productAssociationService = productAssociationService;
	}
	
	/**
	 * Gets product association adapter.
	 * 
	 * @return product association adapter
	 */
	public ProductAssociationAdapter getProductAssociationAdapter() {
		return productAssociationAdapter;
	}
	
	/**
	 * Sets product association adapter.
	 * 
	 * @param productAssociationAdapter product association adapter
	 */
	public void setProductAssociationAdapter(final ProductAssociationAdapter productAssociationAdapter) {
		this.productAssociationAdapter = productAssociationAdapter;
	}

	/**
	 * Set the product association load tuner to use.
	 * 
	 * @param productAssociationLoadTuner the productAssociationLoadTuner to set
	 */
	public void setProductAssociationLoadTuner(final ProductAssociationLoadTuner productAssociationLoadTuner) {
		this.productAssociationLoadTuner = productAssociationLoadTuner;
	}

}
