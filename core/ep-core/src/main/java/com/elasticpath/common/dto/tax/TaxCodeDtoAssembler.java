package com.elasticpath.common.dto.tax;

import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.tax.TaxCode;

/**
 * DTO Assembler to go between TaxCode and TaxCodeDTO.
 */
public class TaxCodeDtoAssembler extends AbstractDtoAssembler<TaxCodeDTO, TaxCode> {

	private BeanFactory beanFactory;

	@Override
	public TaxCode getDomainInstance() {
		return beanFactory.getBean(ContextIdNames.TAX_CODE);
	}

	@Override
	public TaxCodeDTO getDtoInstance() {
		return new TaxCodeDTO();
	}

	@Override
	public void assembleDto(final TaxCode source, final TaxCodeDTO target) {
		target.setCode(source.getCode());
		target.setGuid(source.getGuid());
	}

	@Override
	public void assembleDomain(final TaxCodeDTO source, final TaxCode target) {
		target.setCode(source.getCode());
		target.setGuid(source.getGuid());
	}

	/**
	 * @param beanFactory the factory used for creating beans.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
