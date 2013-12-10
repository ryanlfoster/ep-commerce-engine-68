/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.importexport.importer.controller;

import java.util.Collection;
import java.util.Set;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.importexport.importer.importers.Importer;
import com.elasticpath.persistence.api.Persistable;

/**
 * A resolver for objects related to the DTO being imported.
 * An example is the relation between Product and its SKUs. 
 * Creating/deleting a product cascades the event to its SKUs 
 * and that's why they have to be handled along with their parent product.
 * 
 * @param <DOMAIN> type {@link Persistable}
 * @param <DTO> a Dto extension
 */
public interface RelatedObjectsResolver<DOMAIN extends Persistable, DTO extends Dto> {
	
	/**
	 * Resolves and returns all the related objects to the given DTO.
	 * 
	 * @param dto the DTO
	 * @param importer the importer
	 * @param multiSkuProductTypeNames The set of product type names which are multi-sku.
	 * @return a collection of business objects
	 */
	Collection<BusinessObjectDescriptor> resolveRelatedObjects(DTO dto, Importer<DOMAIN, DTO> importer, Set<String> multiSkuProductTypeNames);
}
