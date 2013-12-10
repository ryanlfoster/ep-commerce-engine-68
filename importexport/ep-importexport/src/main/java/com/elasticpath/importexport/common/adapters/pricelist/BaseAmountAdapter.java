package com.elasticpath.importexport.common.adapters.pricelist;

import java.util.Map;

import org.springframework.validation.FieldError;

import com.elasticpath.common.dto.assembler.pricing.BaseAmountDtoAssembler;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.exceptions.BaseAmountInvalidException;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;
import com.elasticpath.service.pricing.PriceListDescriptorService;

/**
 * The implementation of <code>DomainAdapter</code> interface. It is responsible for data transformation between <code>BaseAmount</code>
 * and <code>BaseAmountDTO</code> objects.
 */
public class BaseAmountAdapter extends AbstractDomainAdapterImpl<BaseAmount, BaseAmountDTO> {

	private static final String UNKNOWN_ERROR = "IE-10809";

	private BaseAmountDtoAssembler baseAmountDtoAssembler;
	
	private PriceListDescriptorService priceListDescriptorService;
	private Map<String, String> errorMap;
	
	@Override
	public void populateDTO(final BaseAmount source, final BaseAmountDTO target) {
		baseAmountDtoAssembler.assembleDto(source, target);
	}

	/**
	 * This operation cannot be supported because BaseAmounts have immutable interface.
	 * @param source BaseAmountDTO
	 * @param target BaseAmount
	 */
	public void populateDomain(final BaseAmountDTO source, final BaseAmount target) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public BaseAmount buildDomain(final BaseAmountDTO source, final BaseAmount target) {
		if (priceListDescriptorService.findByGuid(source.getPriceListDescriptorGuid()) == null) {
			throw new PopulationRuntimeException("IE-10800", source.getPriceListDescriptorGuid());
		}
		BaseAmount baseAmount = null;
		try {
			baseAmount = baseAmountDtoAssembler.assembleDomain(source);
		} catch (BaseAmountInvalidException e) {
			FieldError firstError = (FieldError) e.getErrors().getAllErrors().get(0);
			throw new PopulationRuntimeException(getErrorCode(firstError), e, source.getObjectGuid());
		}
		return baseAmount;
	}

	private String getErrorCode(final FieldError error) {
		if (error == null) {
			return UNKNOWN_ERROR;
		}
		String errorCode = errorMap.get(error.getCode());
		if (errorCode == null) {
			return UNKNOWN_ERROR;
		}

		return errorCode;
	}
	
	/**
	 * Returns null as Base Amount is immutable. <br>
	 * buildDomain(...) will be called to build the base amount.
	 * 
	 * @return null
	 */
	@Override
	public BaseAmount createDomainObject() {
		return null;
	}
	
	@Override
	public BaseAmountDTO createDtoObject() {
		return new BaseAmountDTO();
	}

	/**
	 * @param baseAmountDtoAssembler the baseAmountDtoAssembler to set
	 */
	public void setBaseAmountDtoAssembler(final BaseAmountDtoAssembler baseAmountDtoAssembler) {
		this.baseAmountDtoAssembler = baseAmountDtoAssembler;
	}

	/**
	 * @param priceListDescriptorService the PriceListDescriptorService to set 
	 */
	public final void setPriceListDescriptorService(final PriceListDescriptorService priceListDescriptorService) {
		this.priceListDescriptorService = priceListDescriptorService;
	}

	/**
	 * @param errorMap the errorMap to set
	 */
	public void setErrorMap(final Map<String, String> errorMap) {
		this.errorMap = errorMap;
	}
}
