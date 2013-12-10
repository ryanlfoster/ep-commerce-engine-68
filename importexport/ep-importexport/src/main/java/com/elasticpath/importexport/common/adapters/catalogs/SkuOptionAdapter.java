package com.elasticpath.importexport.common.adapters.catalogs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.LocaleUtils;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.catalogs.SkuOptionDTO;
import com.elasticpath.importexport.common.dto.catalogs.SkuOptionValueDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.service.catalog.SkuOptionService;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>SkuOption</code> and <code>SkuOptionDTO</code> objects.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class SkuOptionAdapter extends AbstractDomainAdapterImpl<SkuOption, SkuOptionDTO> {

	private SkuOptionValueAdapter skuOptionValueAdapter;
	
	private SkuOptionService skuOptionService;
	
	@Override
	public void populateDTO(final SkuOption source, final SkuOptionDTO target) {
		target.setCode(source.getOptionKey());

		final List<DisplayValue> nameValues = new ArrayList<DisplayValue>();
		for (Locale locale : source.getCatalog().getSupportedLocales()) {			
			nameValues.add(new DisplayValue(locale.toString(), source.getDisplayName(locale, false)));
		}		
		target.setNameValues(nameValues);
		
		List<SkuOptionValueDTO> optionValues = new ArrayList<SkuOptionValueDTO>();
		for (SkuOptionValue skuOptionValue : source.getOptionValues()) {
			SkuOptionValueDTO skuOptionValueDTO = new SkuOptionValueDTO();
			skuOptionValueAdapter.populateDTO(skuOptionValue, skuOptionValueDTO);
			optionValues.add(skuOptionValueDTO);
		}
		target.setSkuOptionValues(optionValues);
	}

	@Override
	public void populateDomain(final SkuOptionDTO source, final SkuOption target) { //NOPMD
		target.setOptionKey(source.getCode());

		populateDomainNamesValues(source, target);

		Set<SkuOptionValue> removeOptionValues = new HashSet<SkuOptionValue>(target.getOptionValues());

		for (SkuOptionValueDTO skuOptionValueDTO : source.getSkuOptionValues()) {
			SkuOptionValue skuOptionValue = target.getOptionValue(skuOptionValueDTO.getCode());
			if (skuOptionValue == null) {
				if (skuOptionService.optionValueKeyExists(skuOptionValueDTO.getCode())) {
					throw new PopulationRollbackException("IE-10010", skuOptionValueDTO.getCode(), target.getOptionKey());
				}
				skuOptionValue = getBean(ContextIdNames.SKU_OPTION_VALUE);
				skuOptionValueAdapter.populateDomain(skuOptionValueDTO, skuOptionValue);
				// adding a SkuOptionValue to a SkuOption should only happen when the value is a new instance
				// otherwise for a large data set of option values JPA gets confused 
				// (cannot be explained and reproduced by an integration test - might be a JPA bug)
				target.addOptionValue(skuOptionValue);
			} else {
				skuOptionValueAdapter.populateDomain(skuOptionValueDTO, skuOptionValue);
			}
			removeOptionValues.remove(skuOptionValue);
		}
		
		for (SkuOptionValue skuOptionValue : removeOptionValues) {
			if (!getCachingService().isSkuOptionValueInUse(skuOptionValue.getUidPk())) {
				target.removeOptionValue(skuOptionValue.getOptionValueKey());
			}
		}
	}

	private void populateDomainNamesValues(final SkuOptionDTO source, final SkuOption target) {
		for (DisplayValue displayValue : source.getNameValues()) {
			if (!"".equals(displayValue.getValue())) {
				try {
					Locale locale = LocaleUtils.toLocale(displayValue.getLanguage());
					if (!LocaleUtils.isAvailableLocale(locale)) {
						throw new PopulationRollbackException("IE-10000", displayValue.getLanguage());
					}
					checkLocaleSupportedByCatalog(locale, target);
					target.setDisplayName(displayValue.getValue(), locale);
				} catch (IllegalArgumentException exception) {
					throw new PopulationRollbackException("IE-10000", exception, displayValue.getLanguage());
				}
			}
		}
	}
	
	/**
	 * @throws PopulationRollbackException if Catalog is not null and Locale is not supported.
	 */
	private void checkLocaleSupportedByCatalog(final Locale locale, final SkuOption skuOption) {
		Catalog catalog = skuOption.getCatalog();
		if (catalog != null && !catalog.getSupportedLocales().contains(locale)) {
			throw new PopulationRollbackException("IE-10000", locale.toString());
		}
	}

	/**
	 * Gets skuOptionValueAdapter.
	 * 
	 * @return the skuOptionValueAdapter
	 */
	public SkuOptionValueAdapter getSkuOptionValueAdapter() {
		return skuOptionValueAdapter;
	}

	/**
	 * Sets skuOptionValueAdapter.
	 *
	 * @param skuOptionValueAdapter the skuOptionValueAdapter to set
	 */
	public void setSkuOptionValueAdapter(final SkuOptionValueAdapter skuOptionValueAdapter) {
		this.skuOptionValueAdapter = skuOptionValueAdapter;
	}
	
	/**
	 * Gets skuOptionService.
	 * 
	 * @return the skuOptionService
	 */
	public SkuOptionService getSkuOptionService() {
		return skuOptionService;
	}

	/**
	 * Sets skuOptionService.
	 * 
	 * @param skuOptionService the skuOptionService to set
	 */
	public void setSkuOptionService(final SkuOptionService skuOptionService) {
		this.skuOptionService = skuOptionService;
	}

	@Override
	public SkuOption createDomainObject() {
		return getBean(ContextIdNames.SKU_OPTION);
	}

	@Override
	public SkuOptionDTO createDtoObject() {
		return new SkuOptionDTO();
	}
}
