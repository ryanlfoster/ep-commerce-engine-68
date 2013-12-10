package com.elasticpath.importexport.common.adapters.catalogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.catalogs.BrandDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>Brand</code> and
 * <code>BrandDTO</code> objects.
 */
public class BrandAdapter extends AbstractDomainAdapterImpl<Brand, BrandDTO> {

	@Override
	public void populateDTO(final Brand source, final BrandDTO target) {
		target.setCode(source.getCode());

		final LocalizedProperties localizedProperties = source.getLocalizedProperties();
		final List<DisplayValue> nameValues = new ArrayList<DisplayValue>();
		for (Locale locale : source.getCatalog().getSupportedLocales()) {			
			final String displayName = localizedProperties.getValueWithoutFallBack(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, locale);
			nameValues.add(new DisplayValue(locale.toString(), displayName));
		}
		
		target.setNameValues(nameValues);		
		target.setImage(source.getImageUrl());
	}

	/**
	 * {@inheritDoc}
	 * @throws PopulationRollbackException in case incorrect brand code
	 */
	public void populateDomain(final BrandDTO source, final Brand target) {
		
		if ("".equals(source.getCode())) {
			throw new PopulationRollbackException("IE-10001");
		}				

		target.setCode(source.getCode());
		
		final LocalizedProperties localizedProperties = target.getLocalizedProperties();
		for (DisplayValue displayValue : source.getNameValues()) {
			
			setLocalizedProperty(localizedProperties, displayValue, target);
		}
		target.setLocalizedProperties(localizedProperties);
		target.setImageUrl(source.getImage());
	}

	/**
	 * Sets the displayValue to LocalizedProperties.
	 * 
	 * @param localizedProperties  the LocalizedProperties. 
	 * @param displayValue the displayValue
	 * @param target target object
	 */
	void setLocalizedProperty(final LocalizedProperties localizedProperties, final DisplayValue displayValue, final Brand target) {
		if (!"".equals(displayValue.getLanguage())) {
			try {
				final Locale locale = LocaleUtils.toLocale(displayValue.getLanguage());
				if (LocaleUtils.isAvailableLocale(locale)) {
					checkLocaleSupportedByCatalog(locale, target);
					localizedProperties.setValue(Brand.LOCALIZED_PROPERTY_DISPLAY_NAME, locale, displayValue.getValue());
				} else {
					throw new PopulationRollbackException("IE-10000", displayValue.getLanguage());
				}
			} catch (IllegalArgumentException exception) {
				throw new PopulationRollbackException("IE-10000", exception, displayValue.getLanguage());
			}
		}
	}
	
	/**
	 * @throws PopulationRollbackException if Catalog is not null and Locale is not supported.
	 */
	private void checkLocaleSupportedByCatalog(final Locale locale, final Brand brand) {
		Catalog catalog = brand.getCatalog();
		if (catalog != null && !catalog.getSupportedLocales().contains(locale)) {
			throw new PopulationRollbackException("IE-10000", locale.toString());
		}
	}

	/**
	 * Creates Brand.
	 *
	 * @return Brand
	 */
	@Override
	public Brand createDomainObject() {
		return getBean(ContextIdNames.BRAND);
	}

	/**
	 * Creates BrandDTO.
	 * 
	 * @return BrandDTO.
	 */
	@Override
	public BrandDTO createDtoObject() {
		return new BrandDTO();
	}	
	
}
