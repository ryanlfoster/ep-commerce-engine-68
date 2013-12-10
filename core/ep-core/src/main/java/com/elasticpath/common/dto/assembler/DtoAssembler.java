package com.elasticpath.common.dto.assembler;

import java.util.Collection;
import java.util.List;

import com.elasticpath.common.dto.Dto;

/**
 * Data transfer objects assembler.
 * 
 * @param <DTO> Data Transfer class.
 * @param <DOMAIN> Entity class.
 */
public interface DtoAssembler<DTO extends Dto, DOMAIN> {

	/**
	 * Assembles DTO object from the BO one.
	 * 
	 * @param source Business object
	 * @param target DTO object
	 */
	void assembleDto(DOMAIN source, DTO target);

	/**
	 * Assembles DTO object from the BO one.
	 * 
	 * @param source DTO object
	 * @param target Business object
	 */
	void assembleDomain(DTO source, DOMAIN target);

	/**
	 * Assembles DTO object from the BO one.
	 * 
	 * @param source Business object
	 * @return DTO object
	 */
	DTO assembleDto(DOMAIN source);

	/**
	 * Assembles DTO object list from the BO one.
	 * 
	 * @param domains List of domain objects
	 * @return List of Data transfer Objects
	 */
	List<DTO> assembleDto(Collection<DOMAIN> domains);

	/**
	 * Assembles BO object list from the DTO one.
	 * 
	 * @param dtos List of data transfer objects
	 * @return List of domain Objects
	 */
	List<DOMAIN> assembleDomain(Collection<DTO> dtos);

}
