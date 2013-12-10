package com.elasticpath.importexport.common.adapters.products.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.LocaleDependantFields;
import com.elasticpath.domain.catalog.ObjectWithLocaleDependantFields;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.products.SeoDTO;
import com.elasticpath.importexport.common.exception.runtime.PopulationRuntimeException;

/**
 * The implementation of <code>DomainAdapter</code> interface. It is responsible for data transformation between <code>Product</code> objects
 * and <code>SeoDTO</code> objects.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class SeoAdapter extends AbstractDomainAdapterImpl<ObjectWithLocaleDependantFields, SeoDTO> {

	private Collection<Locale> supportedLocales;

	/**
	 * The Abstract SEO Populate Domain Strategy.
	 */
	abstract class AbstractSeoPopulateDomainStrategy {
		/**
		 * Populates ObjectWithLocaleDependantFields with SeoDTO using strategy. 
		 * 
		 * @param ldfObject the Domain to populate to
		 * @param seoDTO the DTO to populate from
		 */
		public void populateDomain(final SeoDTO seoDTO, final ObjectWithLocaleDependantFields ldfObject) {
			for (DisplayValue displayValue : getList(seoDTO)) {
				final String language = displayValue.getLanguage();
				final String value = displayValue.getValue();
				
				try {
					final Locale locale = LocaleUtils.toLocale(language);
					if (LocaleUtils.isAvailableLocale(locale)) {
						checkLocaleSupportedByCatalog(locale, ldfObject);
						LocaleDependantFields fields = ldfObject.getLocaleDependantFieldsWithoutFallBack(locale);
						setValue(fields, value);
						ldfObject.addOrUpdateLocaleDependantFields(fields);
					} else {
						throw new PopulationRuntimeException("IE-10306", language, value);
					}
				} catch (IllegalArgumentException exception) {
					throw new PopulationRuntimeException("IE-10306", exception, language, value);
				}
			}			
		}
		
		/**
		 * @throws PopulationRuntimeException if Catalog is not null and Locale is not supported.
		 */
		private void checkLocaleSupportedByCatalog(final Locale locale, final ObjectWithLocaleDependantFields ldfObject) {
			if (ldfObject instanceof Category) {
				Catalog catalog = ((Category) ldfObject).getCatalog();
				if (catalog != null && !catalog.getSupportedLocales().contains(locale)) {
					throw new PopulationRuntimeException("IE-10000", locale.toString());
				}
			}
		}
		
		/**
		 * Gets the list of DisplayValue to populate domain.
		 * 
		 * @param seoDTO the DTO to populate from
		 * @return the list of DisplayValue that corresponds to seoDTO 
		 */
		protected abstract List<DisplayValue> getList(final SeoDTO seoDTO);
		
		/**
		 * Sets the value to LocaleDependantFields.
		 * 
		 * @param fields the LocaleDependantFields
		 * @param value the value of DisplayValue
		 */
		protected abstract void setValue(final LocaleDependantFields fields, final String value);
	}
	
	private final AbstractSeoPopulateDomainStrategy seoUrlPopulateDomainStrategy = new AbstractSeoPopulateDomainStrategy() {
		@Override
		protected void setValue(final LocaleDependantFields fields, final String value) {
			fields.setUrl(value);
		}

		@Override
		protected List<DisplayValue> getList(final SeoDTO seoDTO) {
			return seoDTO.getUrlList();
		}
	};
	
	private final AbstractSeoPopulateDomainStrategy seoKeywordsPopulateDomainStrategy = new AbstractSeoPopulateDomainStrategy() {
		@Override
		protected void setValue(final LocaleDependantFields fields, final String value) {
			fields.setKeyWords(value);
		}

		@Override
		protected List<DisplayValue> getList(final SeoDTO seoDTO) {
			return seoDTO.getKeywordsList();
		}
	};
	
	private final AbstractSeoPopulateDomainStrategy seoTitlePopulateDomainStrategy = new AbstractSeoPopulateDomainStrategy() {
		@Override
		protected void setValue(final LocaleDependantFields fields, final String value) {
			fields.setTitle(value);
		}

		@Override
		protected List<DisplayValue> getList(final SeoDTO seoDTO) {
			return seoDTO.getTitleList();
		}
	};
	
	private final AbstractSeoPopulateDomainStrategy seoDescriptionPopulateDomainStrategy = new AbstractSeoPopulateDomainStrategy() {
		@Override
		protected void setValue(final LocaleDependantFields fields, final String value) {
			fields.setDescription(value);
		}

		@Override
		protected List<DisplayValue> getList(final SeoDTO seoDTO) {
			return seoDTO.getDescriptionList();
		}
	};	
	
	@Override
	public void populateDomain(final SeoDTO seoDTO, final ObjectWithLocaleDependantFields ldfObject) {		
		seoUrlPopulateDomainStrategy.populateDomain(seoDTO, ldfObject);
		seoKeywordsPopulateDomainStrategy.populateDomain(seoDTO, ldfObject);
		seoTitlePopulateDomainStrategy.populateDomain(seoDTO, ldfObject);
		seoDescriptionPopulateDomainStrategy.populateDomain(seoDTO, ldfObject);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws PopulationRuntimeException if supported locales are not initialized 
	 */
	public void populateDTO(final ObjectWithLocaleDependantFields ldfObject, final SeoDTO seoDTO) {
		if (supportedLocales == null) {
			throw new PopulationRuntimeException("IE-10316");
		}

		List<DisplayValue> urlList = new ArrayList<DisplayValue>();
		List<DisplayValue> keywordsList = new ArrayList<DisplayValue>();
		List<DisplayValue> titleList = new ArrayList<DisplayValue>();
		List<DisplayValue> descriptionList = new ArrayList<DisplayValue>();
		for (Locale locale : supportedLocales) {
			LocaleDependantFields localeDependantFields = ldfObject.getLocaleDependantFieldsWithoutFallBack(locale);

			DisplayValue urlAdapter = new DisplayValue(locale.toString(), localeDependantFields.getUrl());
			urlList.add(urlAdapter);

			DisplayValue titleAdapter = new DisplayValue(locale.toString(), localeDependantFields.getTitle());
			titleList.add(titleAdapter);

			DisplayValue keywordsAdapter = new DisplayValue(locale.toString(), localeDependantFields.getKeyWords());
			keywordsList.add(keywordsAdapter);

			DisplayValue descriptionAdapter = new DisplayValue(locale.toString(), localeDependantFields.getDescription());
			descriptionList.add(descriptionAdapter);

		}
		seoDTO.setUrlList(urlList);
		seoDTO.setTitleList(titleList);
		seoDTO.setKeywordsList(keywordsList);
		seoDTO.setDescriptionList(descriptionList);
	}

	/**
	 * Sets the supported locales for locale dependent fields.
	 * 
	 * @param supportedLocales the supportedLocales to set
	 */
	public void setSupportedLocales(final Collection<Locale> supportedLocales) {
		this.supportedLocales = supportedLocales;
	}

}
