package com.elasticpath.importexport.common.adapters;

import com.elasticpath.common.dto.Dto;

/**
 * The Interface of Generic Adapter that is responsible for transport data between domain object and data transfer object (in abbreviated form DTO).
 * 
 * @param <DOMAIN> the domain interface (any interface)
 * @param <DTO> the data transfer object interface that must extend <code>Dto</code> interface
 */
public interface DomainAdapter<DOMAIN, DTO extends Dto> {

	/**
	 * Populates domain object from DTO. Domain object may be null in case if it is immutable and adapter itself
	 * needs to create domain object. At any case populated object will be returned by the method.
	 * 
	 * @param source the source DTO object 
	 * @param target the target domain object for population	
	 */
	void populateDomain(final DTO source, final DOMAIN target);
	
	/**
	 * Builds domain object from DTO. This method may simply delegate to populateDomain() in case if target is not not. <br>
	 * Null value for target indicates that immutable object should be created and populated, i.e. for an object for 
	 * which the process of object creation and population can not be broken down in to two parts.
	 * Populated object will be returned by the method.<br>
	 * 
	 * @param source the source DTO object 
	 * @param target the target domain object for population. May be null in which case the concrete adapter must 
	 * implement the process of object instantiation and population 
	 * @return populated domain object
	 */
	DOMAIN buildDomain(DTO source, DOMAIN target);

	/**
	 * Populates DTO from domain object.
	 * 
	 * @param source the source domain object
	 * @param target the target DTO object for population
	 */
	void populateDTO(final DOMAIN source, final DTO target);

	/**
	 * Creates empty domain object.
	 * 
	 * @return the domain object
	 */
	DOMAIN createDomainObject();

	/**
	 * Creates empty DTO object.
	 * 
	 * @return the DTO object
	 */
	DTO createDtoObject();	
}
