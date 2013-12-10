package com.elasticpath.importexport.importer.importers.objectdescriptorbuilder.impl;

import java.util.Set;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.persistence.api.Persistable;

/**
 * Represents methods for building business descriptors by given objects.
 */
public interface ObjectDescriptorBuilder {
	
	/**
	 * Builds set of business object descriptors based on given parameters.
	 * 
	 * @param persistable the fresh persistable object from database
	 * @param dto corresponding data transfer object
	 * @return set of business object descriptors
	 */
	Set<BusinessObjectDescriptor> buildDescriptors(final Persistable persistable, final Dto dto);

}
