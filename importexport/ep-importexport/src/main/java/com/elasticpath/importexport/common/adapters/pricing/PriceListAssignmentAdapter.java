package com.elasticpath.importexport.common.adapters.pricing;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.pricing.PriceListAssignmentDTO;

/**
 * Adapter for import/export of PLA.
 */
public class PriceListAssignmentAdapter extends AbstractDomainAdapterImpl<PriceListAssignment, PriceListAssignmentDTO> {

	private PriceListAssignmentAssembler assembler;

	@Override
	public void populateDTO(final PriceListAssignment source, final PriceListAssignmentDTO target) {
		assembler.assembleDto(source, target);
	}

	@Override
	public void populateDomain(final PriceListAssignmentDTO source, final PriceListAssignment target) {
		assembler.assembleDomain(source, target);
	}

	@Override
	public PriceListAssignment createDomainObject() {
		return getBean(ContextIdNames.PRICE_LIST_ASSIGNMENT);
	}

	@Override
	public PriceListAssignmentDTO createDtoObject() {
		return new PriceListAssignmentDTO();
	}

	/**
	 * @param assembler injected {@link com.elasticpath.importexport.common.adapters.pricing.PriceListAssignmentAssembler}.
	 */
	public void setPriceListAssignmentAssembler(final PriceListAssignmentAssembler assembler) {
		this.assembler = assembler;
	}
}

