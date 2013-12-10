/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.importexport.importer.changesetsupport.impl;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.dto.catalogs.CatalogType;
import com.elasticpath.importexport.importer.changesetsupport.ObjectDescriptorPopulator;

/**
 * An object descriptor populator for {@link CatalogDTO}s.
 */
public class CatalogObjectDescriptorPopulator implements ObjectDescriptorPopulator {

	private String objectType;

	@Override
	public BusinessObjectDescriptor populate(final BusinessObjectDescriptor descriptor, final Dto dto) {
		CatalogDTO catalogDto = (CatalogDTO) dto;
		if (!CatalogType.isMaster(catalogDto.getType())) {
			descriptor.setObjectIdentifier(catalogDto.getCode());
			descriptor.setObjectType(getObjectType());
			return descriptor;
		}
		// master catalogs are not supported by the change set framework
		return null;
	}

	/**
	 *
	 * @param objectType the objectType to set
	 */
	public void setObjectType(final String objectType) {
		this.objectType = objectType;
	}

	/**
	 *
	 * @return the objectType
	 */
	public String getObjectType() {
		return objectType;
	}

}
