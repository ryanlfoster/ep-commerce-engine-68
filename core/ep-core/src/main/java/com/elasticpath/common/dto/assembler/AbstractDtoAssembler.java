package com.elasticpath.common.dto.assembler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.elasticpath.common.dto.Dto;

/**
 * Data transfer objects abstract assembler.
 * 
 * @param <DTO> Data Transfer class.
 * @param <DOMAIN> Entity class.
 */
public abstract class AbstractDtoAssembler<DTO extends Dto, DOMAIN> implements DtoAssembler<DTO, DOMAIN> {

	private final AssemblingLists<DTO, DOMAIN> assemblingDomain = new AssemblingLists<DTO, DOMAIN>();

	private final AssemblingLists<DOMAIN, DTO> assemblingDto = new AssemblingLists<DOMAIN, DTO>();

	/**
	 * Gets the instance of the domain object.
	 * 
	 * @return Domain object instance
	 */
	public abstract DOMAIN getDomainInstance();

	/**
	 * Gets the instance of the DTO object.
	 * 
	 * @return DTO object instance
	 */
	public abstract DTO getDtoInstance();
	
	/**
	 * Assembles DTO object from the BO one.
	 * 
	 * @param source Business object
	 * @param target Data transfer Object
	 */
	public abstract void assembleDto(final DOMAIN source, final DTO target);

	/**
	 * Assembles BO object from the DTO one.
	 * 
	 * @param source Data transfer Object
	 * @param target Business object
	 */
	public abstract void assembleDomain(final DTO source, final DOMAIN target);

	/**
	 * Assembles DTO object from the BO one.
	 * 
	 * @param source Business object
	 * @return DTO object
	 */
	public DTO assembleDto(final DOMAIN source) {
		DTO dto = getDtoInstance();
		if (dto != null) {
			assembleDto(source, dto);
		}
		return dto;
	}

	/**
	 * Assembles BO object from the DTO one.
	 * 
	 * @param source DTO object
	 * @return BO object
	 */
	public DOMAIN assembleDomain(final DTO source) {
		DOMAIN domain = getDomainInstance();
		if (domain != null) {
			assembleDomain(source, domain);
		}
		return domain;
	}

	/**
	 * Assembles DTO object list from the BO one.
	 * 
	 * @param domains List of domain objects
	 * @return List of Data transfer Objects
	 */
	public List<DTO> assembleDto(final Collection<DOMAIN> domains) {
		return assemblingDto.assembleList(domains);
	}

	/**
	 * Assembles BO object list from the DTO one.
	 * 
	 * @param dtos List of data transfer objects
	 * @return List of domain Objects
	 */
	public List<DOMAIN> assembleDomain(final Collection<DTO> dtos) {
		return assemblingDomain.assembleList(dtos);
	}

	/**
	 * Private class for populating lists.
	 */
	private class AssemblingLists<SOURCE, TARGET> {

		protected List<TARGET> assembleList(final Collection<SOURCE> source) {
			if (source == null || source.isEmpty()) {
				return Collections.emptyList();
			}
			List<TARGET> target = new ArrayList<TARGET>(source.size());
			for (final SOURCE src : source) {
				TARGET element = getElement(src);
				if (element != null) {
					target.add(element);
				}
			}
			return target;
		}

		@SuppressWarnings("unchecked")
		private TARGET getElement(final SOURCE source) {
			final Object element;
			if (source instanceof Dto) {
				element = assembleDomain((DTO) source);
			} else {
				element = assembleDto((DOMAIN) source);
			}
			return (TARGET) element;
		}
	}
}
