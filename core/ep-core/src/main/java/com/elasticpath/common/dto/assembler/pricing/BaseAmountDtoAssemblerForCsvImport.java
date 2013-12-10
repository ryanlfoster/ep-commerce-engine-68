/**
 * 
 */
package com.elasticpath.common.dto.assembler.pricing;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.BaseAmountObjectType;

/**
 * An assembler for BaseAmount for CSV import.
 *
 */
public class BaseAmountDtoAssemblerForCsvImport extends BaseAmountDtoAssembler {

	@Override
	public BaseAmount assembleDomain(final BaseAmountDTO source) {

		String objectGuid = source.getProductCode();
		String objectType = source.getObjectType();
		if (BaseAmountObjectType.SKU.getName().equals(objectType)) {
			objectGuid = source.getSkuCode();
		} else {
			objectGuid = source.getProductCode();
		}
		return getBaseAmountFactory().createBaseAmount(source.getGuid(), objectGuid, objectType,
				source.getQuantity(), source.getListValue(), source.getSaleValue(), source.getPriceListDescriptorGuid());
	}

}
