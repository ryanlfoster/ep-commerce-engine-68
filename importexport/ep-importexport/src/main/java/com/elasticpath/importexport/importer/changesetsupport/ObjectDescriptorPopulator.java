/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.importexport.importer.changesetsupport;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;


/**
 * Populates an object descriptor.
 */
public interface ObjectDescriptorPopulator {

	/**
	 * Populates a descriptor object using the given DTO.
	 * 
	 * @param descriptor an empty object descriptor
	 * @param dto the DTO
	 * @return the populated business object descriptor
	 */
	BusinessObjectDescriptor populate(BusinessObjectDescriptor descriptor, Dto dto);
	
}
