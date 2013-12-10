package com.elasticpath.importexport.common.adapters.catalogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.catalogs.SkuOptionValueDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>SkuOptionValue</code> and <code>SkuOptionValueDTO</code> objects.
 */
public class SkuOptionValueAdapter extends AbstractDomainAdapterImpl<SkuOptionValue, SkuOptionValueDTO> {

	@Override
	public void populateDTO(final SkuOptionValue skuOptionValue, final SkuOptionValueDTO skuOptionValueDTO) {
		skuOptionValueDTO.setCode(skuOptionValue.getOptionValueKey());
		skuOptionValueDTO.setImage(skuOptionValue.getImage());
		skuOptionValueDTO.setOrdering(skuOptionValue.getOrdering());	
		
		final List<DisplayValue> nameValues = new ArrayList<DisplayValue>();
		for (Locale locale : skuOptionValue.getSkuOption().getCatalog().getSupportedLocales()) {			
			nameValues.add(new DisplayValue(locale.toString(), skuOptionValue.getDisplayName(locale, false)));
		}		
		skuOptionValueDTO.setNameValues(nameValues);
	}

	@Override
	public void populateDomain(final SkuOptionValueDTO skuOptionValueDTO, final SkuOptionValue skuOptionValue) {
		skuOptionValue.setOptionValueKey(skuOptionValueDTO.getCode());
		skuOptionValue.setImage(skuOptionValueDTO.getImage());
		final Integer ordering = skuOptionValueDTO.getOrdering();
		if (ordering != null) {
			// I/E exporters didn't support ordering before fix of RUMBA-594. 
			// Thus, we need to assure that former (generated before fix of RUMBA-594) product.zip does not zerofy ordering value during update. 
			skuOptionValue.setOrdering(ordering);
		}
		
		for (DisplayValue displayValue : skuOptionValueDTO.getNameValues()) {
			if (!"".equals(displayValue.getValue())) {
				try {
					Locale locale = LocaleUtils.toLocale(displayValue.getLanguage());
					if (!LocaleUtils.isAvailableLocale(locale)) {
						throw new PopulationRollbackException("IE-10000", displayValue.getLanguage());
					}
					checkLocaleSupportedByCatalog(locale, skuOptionValue);
					skuOptionValue.setDisplayName(locale, displayValue.getValue());
				} catch (IllegalArgumentException exception) {
					throw new PopulationRollbackException("IE-10000", exception, displayValue.getLanguage());
				}
			}
		}
	}
	
	/**
	 * @throws PopulationRollbackException if Catalog is not null and Locale is not supported.
	 */
	private void checkLocaleSupportedByCatalog(final Locale locale, final SkuOptionValue skuOptionValue) {
		final SkuOption skuOption = skuOptionValue.getSkuOption();
		if (skuOption != null) {
			final Catalog catalog = skuOption.getCatalog();
			if (catalog != null && !catalog.getSupportedLocales().contains(locale)) {
				throw new PopulationRollbackException("IE-10000", locale.toString());
			}
		}
	}
}
