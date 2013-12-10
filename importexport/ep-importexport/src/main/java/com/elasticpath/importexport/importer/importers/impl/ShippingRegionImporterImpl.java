package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.shipping.region.ShippingRegionDTO;
import com.elasticpath.service.shipping.ShippingRegionService;

/**
 * Imports shipping regions. 
 */
public class ShippingRegionImporterImpl extends AbstractImporterImpl<ShippingRegion, ShippingRegionDTO> {

	private DomainAdapter<ShippingRegion, ShippingRegionDTO> domainAdapter;
	
	private ShippingRegionService shippingRegionService;

	@Override
	public String getImportedObjectName() {
		return ShippingRegionDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final ShippingRegionDTO dto) {
		return dto.getGuid();
	}

	@Override
	protected DomainAdapter<ShippingRegion, ShippingRegionDTO> getDomainAdapter() {
		return domainAdapter;
	}

	@Override
	protected ShippingRegion findPersistentObject(final ShippingRegionDTO dto) {
		return shippingRegionService.findByName(dto.getName());
	}

	@Override
	protected void setImportStatus(final ShippingRegionDTO object) {
		getStatusHolder().setImportStatus(object.getGuid());
	}

	/**
	 * Sets domain adapter.
	 * @param domainAdapter domain adapter
	 */
	public void setDomainAdapter(final DomainAdapter<ShippingRegion, ShippingRegionDTO> domainAdapter) {
		this.domainAdapter = domainAdapter;
	}

	/**
	 * Sets shipping Region Service.
	 * @param shippingRegionService shipping Region Service
	 */
	public void setShippingRegionService(final ShippingRegionService shippingRegionService) {
		this.shippingRegionService = shippingRegionService;
	}
	
	@Override
	public Class<? extends ShippingRegionDTO> getDtoClass() {
		return ShippingRegionDTO.class;
	}	
}
