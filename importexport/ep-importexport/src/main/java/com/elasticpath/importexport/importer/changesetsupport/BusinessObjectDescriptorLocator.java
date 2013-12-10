/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.importexport.importer.changesetsupport;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * A locator that looks up a business object descriptor.
 */
public interface BusinessObjectDescriptorLocator {

	
	/**
	 * Locates an object descriptor by the given DTO instance.
	 * 
	 * @param dto the DTO instance
	 * @return an instance of {@link BusinessObjectDescriptor} or null if none was found
	 */
	BusinessObjectDescriptor locateObjectDescriptor(Dto dto);
}
