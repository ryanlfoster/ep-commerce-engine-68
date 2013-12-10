package com.elasticpath.common.dto.assembler.pricing;

import com.elasticpath.common.dto.assembler.AbstractDtoAssembler;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.service.pricing.BaseAmountFactory;

/**
 * Assembler/disassembler for converting DTO to BaseAmount domain objects.
 */
public class BaseAmountDtoAssembler extends AbstractDtoAssembler<BaseAmountDTO, BaseAmount> {

	private BaseAmountFactory baFactory;
	/**
	 * This operation cannot be supported because BaseAmounts have immutable interface.
	 * @param source dto
	 * @param target domain object
	 * @throws UnsupportedOperationException when called.
	 */
	@Override
	public void assembleDomain(final BaseAmountDTO source, final BaseAmount target) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public void assembleDto(final BaseAmount source, final BaseAmountDTO target) {
		target.setGuid(source.getGuid());
		target.setListValue(source.getListValue());
		target.setSaleValue(source.getSaleValue());
		target.setObjectGuid(source.getObjectGuid());
		target.setObjectType(source.getObjectType());
		target.setQuantity(source.getQuantity());
		target.setPriceListDescriptorGuid(source.getPriceListDescriptorGuid());
		copyObjectGuidToAppropriateCode(target);
	}


	/**
	 * Copies the object guid to the product or sku code depending on the object type.
	 * @param baseAmount target dto
	 */
	void copyObjectGuidToAppropriateCode(final BaseAmountDTO baseAmount) {
		if ("PRODUCT".equals(baseAmount.getObjectType())) {
			baseAmount.setProductCode(baseAmount.getObjectGuid());
		} else if ("SKU".equals(baseAmount.getObjectType())) {
			baseAmount.setSkuCode(baseAmount.getObjectGuid());
		}
	}

	
	
	/**
	 * {@inheritDoc}
	 * 
	 * If source DTO doesn't have a GUID, the domain object guid should be generated.
	 */
	@Override
	public BaseAmount assembleDomain(final BaseAmountDTO source) {
		return baFactory.createBaseAmount(source.getGuid(), source.getObjectGuid(), source.getObjectType(),
				source.getQuantity(), source.getListValue(), source.getSaleValue(), 
				source.getPriceListDescriptorGuid());
	}
	
	/**
	 * @return blank BaseAmount domain object.
	 */
	@Override
	public BaseAmount getDomainInstance() {
		return baFactory.createBaseAmount();
	}

	/**
	 * @return blank BaseAmountDTO DTO.
	 */
	@Override
	public BaseAmountDTO getDtoInstance() {
		return new BaseAmountDTO();
	}

	/**
	 * @param baFactory the baFactory to set
	 */
	public void setBaseAmountFactory(final BaseAmountFactory baFactory) {
		this.baFactory = baFactory;
	}

	/**
	 * @return the baFactory
	 */
	protected BaseAmountFactory getBaseAmountFactory() {
		return baFactory;
	}

	
}
