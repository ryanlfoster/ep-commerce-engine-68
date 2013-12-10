package com.elasticpath.importexport.common.adapters.catalogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.apache.log4j.Logger;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.importexport.common.dto.catalogs.CatalogType;
import com.elasticpath.importexport.common.exception.runtime.PopulationRollbackException;
import com.elasticpath.importexport.common.util.Message;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br> 
 * It is responsible for data transformation between <code>Catalog</code> and
 * <code>CatalogDTO</code> objects.
 */
public class CatalogAdapter extends AbstractDomainAdapterImpl<Catalog, CatalogDTO> {

	private static final String UNSUPPORTED_LOCALE_MESSAGE = "IE-10000";
	private static final Logger LOG = Logger.getLogger(CatalogAdapter.class);

	@Override
	public void populateDTO(final Catalog catalog, final CatalogDTO catalogDTO) {
		populateCatalogCommonData(catalog, catalogDTO);
		if (catalog.isMaster()) {
			populateMasterCatalogSpecificData(catalog, catalogDTO);
		}
	}

	/*
	 * Populates data common both for master and virtual catalogs.
	 */
	private void populateCatalogCommonData(final Catalog catalog, final CatalogDTO catalogDTO) {
		catalogDTO.setCode(catalog.getCode());
		catalogDTO.setName(catalog.getName());
		catalogDTO.setType(CatalogType.getCatalogType(catalog.isMaster()));
		catalogDTO.setDefaultLanguage(catalog.getDefaultLocale().toString());
	}

	/*
	 * Supported languages and currencies should be contained only in master catalogs but not in virtual catalogs.
	 */
	private void populateMasterCatalogSpecificData(final Catalog catalog, final CatalogDTO catalogDTO) {
		final List<String> languages = new ArrayList<String>();
		for (Locale locale : catalog.getSupportedLocales()) {
			languages.add(locale.toString());
		}
		catalogDTO.setLanguages(languages);
	}

	@Override
	public void populateDomain(final CatalogDTO catalogDTO, final Catalog catalog) {
		catalog.setCode(catalogDTO.getCode());
		catalog.setName(catalogDTO.getName());
		catalog.setMaster(CatalogType.isMaster(catalogDTO.getType()));
		
		setDefaultLocaleToCatalog(catalog, catalogDTO.getDefaultLanguage());
		addLocalesToCatalog(catalog, catalogDTO.getLanguages());
		
	}

	private void setDefaultLocaleToCatalog(final Catalog catalog, final String language) {
		try {
			Locale locale = LocaleUtils.toLocale(language);
			if (LocaleUtils.isAvailableLocale(locale)) {
				catalog.setDefaultLocale(locale);
			} else {
				throw new PopulationRollbackException(UNSUPPORTED_LOCALE_MESSAGE, language);
			}
		} catch (IllegalArgumentException exception) {
			throw new PopulationRollbackException(UNSUPPORTED_LOCALE_MESSAGE, exception, language);
		}
	}

	private void addLocalesToCatalog(final Catalog catalog, final List<String> languages) {
		for (String language : languages) {
			try {
				Locale locale = LocaleUtils.toLocale(language);
				if (LocaleUtils.isAvailableLocale(locale)) {
					catalog.addSupportedLocale(locale);
				} else {
					LOG.warn(new Message(UNSUPPORTED_LOCALE_MESSAGE, language));
				}
			} catch (IllegalArgumentException exception) {
				throw new PopulationRollbackException(UNSUPPORTED_LOCALE_MESSAGE, exception, language);
			} catch (UnsupportedOperationException uoe) {
				throw new PopulationRollbackException("IE-10009", uoe, catalog.getCode());
			}
		}
	}
	
	@Override
	public Catalog createDomainObject() {
		return getBean(ContextIdNames.CATALOG);
	}

	@Override
	public CatalogDTO createDtoObject() {
		return new CatalogDTO();
	}
}
