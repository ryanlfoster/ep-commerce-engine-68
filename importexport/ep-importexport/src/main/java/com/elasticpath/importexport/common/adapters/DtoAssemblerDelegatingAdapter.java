package com.elasticpath.importexport.common.adapters;

import com.elasticpath.common.dto.Dto;
import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;

/**
 * This class is used when all the heavy lifting that an {@code DomainAdapter} normally does can be
 * passed off to a {@code AbstractDtoAssembler}.  This is can be generally the case with a properly
 * written DtoAssembler and Importer/Exporter.  The benefit of pushing the work into a DtoAssembler
 * is re-use by other parts of the system who need DTO<->DOMAIN mapping but do not need the import/export
 * subsystem.
 * <br> 
 * This adapter is to be wired up by Spring, injecting the appropriate AbstractDtoAssembler.
 * <br>
 * If your code doesn't extend AbstractDtoAssembler, it may be time to push get*Instance() methods into the DtoAssembler
 * interface and change this class.
 *<br>
 * @param <DOMAIN> A class which implements {@code EpDomain}
 * @param <DTO> A class which implements {@code Dto}
 */
public class DtoAssemblerDelegatingAdapter<DOMAIN, DTO extends Dto> extends AbstractDomainAdapterImpl<DOMAIN, DTO> {

	private AbstractDtoAssembler<DTO, DOMAIN> assembler;

	@Override
	public DTO createDtoObject() {
		return assembler.getDtoInstance();
	}

	@Override
	public DOMAIN createDomainObject() {
		return assembler.getDomainInstance();
	}

	@Override
	public void populateDomain(final DTO source, final DOMAIN target) {
		assembler.assembleDomain(source, target);
	}

	@Override
	public void populateDTO(final DOMAIN source, final DTO target) {
		assembler.assembleDto(source, target);
	}

	public void setAssembler(final AbstractDtoAssembler<DTO, DOMAIN> assembler) {
		this.assembler = assembler;
	}

	public AbstractDtoAssembler<DTO, DOMAIN> getAssembler() {
		return assembler;
	}
}
