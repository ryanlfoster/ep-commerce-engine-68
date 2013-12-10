package com.elasticpath.common.dto.assembler.pricing;

import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.pricing.PriceListDescriptor;

/**
 * Assembler/disassembler for PriceListDescriptor DTOs.
 */
public class PriceListDescriptorDtoAssembler extends AbstractDtoAssembler<PriceListDescriptorDTO, PriceListDescriptor> {
	
	private BeanFactory beanFactory;
	
	/**
	 * Assemble a PriceListDescriptor from a PriceListDescriptorDTO. 
	 * We need to copy all fields except GUID if it is null in the DTO. i.e. newly created descriptors.
	 * 
	 * @param source the source DTO to get data from
	 * @param target the domain object to copy the DTO data to
	 */
	@Override
	public void assembleDomain(final PriceListDescriptorDTO source, final PriceListDescriptor target) {
		if (source.getGuid() != null) {
			target.setGuid(source.getGuid());
		}
		target.setCurrencyCode(source.getCurrencyCode());
		target.setDescription(source.getDescription());
		target.setName(source.getName());
		target.setHidden(source.isHidden());
	}

	@Override
	public void assembleDto(final PriceListDescriptor source, final PriceListDescriptorDTO target) {
		target.setGuid(source.getGuid());
		target.setCurrencyCode(source.getCurrencyCode());
		target.setDescription(source.getDescription());
		target.setName(source.getName());
		target.setHidden(source.isHidden());
	}

	@Override
	public PriceListDescriptor getDomainInstance() {
		return beanFactory.getBean(ContextIdNames.PRICE_LIST_DESCRIPTOR);
	}

	@Override
	public PriceListDescriptorDTO getDtoInstance() {
		return new PriceListDescriptorDTO();
	}

	/**
	 * Set the spring bean factory to use.
	 * @param beanFactory instance
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
}
