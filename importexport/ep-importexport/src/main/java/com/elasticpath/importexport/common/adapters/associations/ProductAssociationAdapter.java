package com.elasticpath.importexport.common.adapters.associations;

import java.util.Date;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductAssociation;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.caching.CachingService;
import com.elasticpath.importexport.common.dto.productassociation.ProductAssociationDTO;
import com.elasticpath.importexport.common.dto.productassociation.ProductAssociationType;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br> 
 * It is responsible for data transformation between <code>ProductAssociation</code> and
 * <code>ProductAssociationDTO</code> objects.
 */
public class ProductAssociationAdapter extends AbstractDomainAdapterImpl<ProductAssociation, ProductAssociationDTO> {

	private FetchGroupLoadTuner productLoadTuner;
	private FetchGroupLoadTuner catalogLoadTuner;

	@Override
	public void populateDTO(final ProductAssociation source,
			final ProductAssociationDTO target) {
		target.setProductAssociationType(ProductAssociationType.valueOf(source.getAssociationType()));
		target.setDefaultQuantity(source.getDefaultQuantity());
		target.setEndDate(source.getEndDate());
		target.setOrdering(source.getOrdering());
		target.setSourceProductDependent(source.isSourceProductDependent());
		Product sourceProduct = source.getSourceProduct();
		if (sourceProduct != null) {
			target.setSourceProductCode(sourceProduct.getCode());
		}
		Product targetProduct = source.getTargetProduct();
		target.setStartDate(source.getStartDate());
		if (targetProduct != null) {
			target.setTargetProductCode(targetProduct.getCode());
		}
		
		target.setCatalogCode(source.getCatalog().getCode());		
	}

	@Override
	public void populateDomain(final ProductAssociationDTO source,
			final ProductAssociation target) {
		
		if (source.getSourceProductCode().equals(source.getTargetProductCode())) {
			throw new PopulationRollbackException("IE-10400", source.getSourceProductCode());
		}

		if (source.getDefaultQuantity() < 1) {
			throw new PopulationRollbackException("IE-10401");			
		}
		
		target.setDefaultQuantity(source.getDefaultQuantity());
		target.setAssociationType(source.getProductAssociationType().typeId());		
		Date startDate = source.getStartDate();
		if (startDate == null) {
			throw new PopulationRollbackException("IE-10402");
		}
		target.setStartDate(startDate);
		target.setEndDate(source.getEndDate()); // it can be null
		
		CachingService cachingService = getCachingService();
		
		String sourceProductCode = source.getSourceProductCode();		
		Product sourceProduct = cachingService.findProductByCode(sourceProductCode, productLoadTuner);
		if (sourceProduct == null) {
			throw new PopulationRollbackException("IE-10403", sourceProductCode);
		}
		target.setSourceProduct(sourceProduct);
		
		String targetProductCode = source.getTargetProductCode();	
		Product targetProduct = cachingService.findProductByCode(targetProductCode, productLoadTuner);
		if (targetProduct == null) {
			throw new PopulationRollbackException("IE-10403", targetProductCode);
		}
		target.setTargetProduct(targetProduct);
		target.setSourceProductDependent(source.isSourceProductDependent());
		target.setOrdering(source.getOrdering());
		
		final Catalog catalog = cachingService.findCatalogByCode(source.getCatalogCode(), catalogLoadTuner);
		if (catalog == null) {
			throw new PopulationRollbackException("IE-10404", source.getCatalogCode());
		}
		target.setCatalog(catalog);
	}

	/**
	 * Creates ProductAssociation.
	 * 
	 * @return ProductAssociation
	 */
	@Override
	public ProductAssociation createDomainObject() {
		return getBean(ContextIdNames.PRODUCT_ASSOCIATION);
	}

	/**
	 * Creates ProductAssociationDTO.
	 *
	 * @return ProductAssociationDTO
	 */
	@Override
	public ProductAssociationDTO createDtoObject() {
		return new ProductAssociationDTO();
	}

	/**
	 * Sets the product load tuner.
	 * 
	 * @param productLoadTuner the product load tuner
	 */
	public void setProductLoadTuner(final FetchGroupLoadTuner productLoadTuner) {
		this.productLoadTuner = productLoadTuner;
	}

	/**
	 * Set the catalog load tuner.
	 * 
	 * @param catalogLoadTuner the catalog load tuner
	 */
	public void setCatalogLoadTuner(final FetchGroupLoadTuner catalogLoadTuner) {
		this.catalogLoadTuner = catalogLoadTuner;
	}
	
}
