package com.elasticpath.importexport.common.adapters.products.data;

import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.impl.JpaAdaptorOfSkuOptionValueImpl;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.products.SkuOptionDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * The implementation of <code>DomainAdapter</code> interface. It is responsible for data transformation between
 * <code>JpaAdaptorOfSkuOptionValueImpl</code> and <code>SkuOptionDTO</code> objects.
 */
public class ProductSkuOptionAdapter extends AbstractDomainAdapterImpl<JpaAdaptorOfSkuOptionValueImpl, SkuOptionDTO> {

	@Override
	public void populateDTO(final JpaAdaptorOfSkuOptionValueImpl source, final SkuOptionDTO target) {
		SkuOption skuOption = source.getSkuOption();
		if (skuOption != null) {
			target.setCode(skuOption.getOptionKey());
			target.setSkuOptionValue(source.getOptionValueKey());
		}

	}

	@Override
	public void populateDomain(final SkuOptionDTO source, final JpaAdaptorOfSkuOptionValueImpl target) {
		SkuOption skuOption = getCachingService().findSkuOptionByKey(source.getCode());

		if (skuOption == null) {
			throw new PopulationRuntimeException("IE-10314", source.getCode());
		}

		if (!skuOption.contains(source.getSkuOptionValue())) {
			throw new PopulationRuntimeException("IE-10315", skuOption.getOptionKey(), source.getSkuOptionValue());
		}

		target.setSkuOptionValue(skuOption.getOptionValue(source.getSkuOptionValue()));
		target.setOptionKey(skuOption.getOptionKey());
	}
}
