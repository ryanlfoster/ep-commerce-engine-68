package com.elasticpath.importexport.importer.importers.impl;

import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.importexport.common.adapters.DomainAdapter;
import com.elasticpath.importexport.common.dto.shipping.ShippingServiceLevelDTO;
import com.elasticpath.service.shipping.ShippingServiceLevelService;

/**
 * Imports shipping service levels. 
 */
public class ShippingServiceLevelImporterImpl extends AbstractImporterImpl<ShippingServiceLevel, ShippingServiceLevelDTO> {

	private DomainAdapter<ShippingServiceLevel, ShippingServiceLevelDTO> domainAdapter;
	
	private ShippingServiceLevelService shippingServiceLevelService;

	@Override
	public String getImportedObjectName() {
		return ShippingServiceLevelDTO.ROOT_ELEMENT;
	}

	@Override
	protected String getDtoGuid(final ShippingServiceLevelDTO dto) {
		return dto.getGuid();
	}

	@Override
	protected DomainAdapter<ShippingServiceLevel, ShippingServiceLevelDTO> getDomainAdapter() {
		return domainAdapter;
	}

	@Override
	protected ShippingServiceLevel findPersistentObject(final ShippingServiceLevelDTO dto) {
		return shippingServiceLevelService.findByCode(dto.getCode());
	}

	@Override
	protected void setImportStatus(final ShippingServiceLevelDTO object) {
		getStatusHolder().setImportStatus(object.getGuid());
	}

	/**
	 * Sets domain adapter.
	 * @param domainAdapter dom	ain adapter
	 */
	public void setDomainAdapter(final DomainAdapter<ShippingServiceLevel, ShippingServiceLevelDTO> domainAdapter) {
		this.domainAdapter = domainAdapter;
	}

	/**
	 * Sets shipping service level service.
	 * @param shippingServiceLevelService shipping service level service
	 */
	public void setShippingServiceLevelService(final ShippingServiceLevelService shippingServiceLevelService) {
		this.shippingServiceLevelService = shippingServiceLevelService;
	}

	@Override
	public Class<? extends ShippingServiceLevelDTO> getDtoClass() {
		return ShippingServiceLevelDTO.class;
	}	
}
