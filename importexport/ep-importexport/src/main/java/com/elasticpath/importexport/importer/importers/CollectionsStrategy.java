package com.elasticpath.importexport.importer.importers;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.persistence.api.Persistable;

/**
 * Interface for collections strategy, each importer that needs to manage its collections should create own implementation of this interface.  
 * 
 * @param <DOMAIN> the domain object
 * @param <DTO> the dto object
 */
public interface CollectionsStrategy<DOMAIN extends Persistable, DTO extends Dto> {

	/**
	 * Prepares the domain object for population.
	 * 
	 * @param domainObject the object that will be changed based on appropriate collection strategies
	 * @param dto the dto
	 */
	void prepareCollections(DOMAIN domainObject, DTO dto);
	
	/**
	 * Determines if this strategy will be called for persistent objects only or for all objects.
	 * 
	 * @return true if this strategy should be called for persistent objects only or false otherwise
	 */
	boolean isForPersistentObjectsOnly();

}
