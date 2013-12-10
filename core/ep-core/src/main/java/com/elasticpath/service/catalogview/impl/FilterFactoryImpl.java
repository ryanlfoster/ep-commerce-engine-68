package com.elasticpath.service.catalogview.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.SeoConstants;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalogview.AdvancedSearchFilteredNavSeparatorFilter;
import com.elasticpath.domain.catalogview.AttributeFilter;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.CategoryFilter;
import com.elasticpath.domain.catalogview.DisplayableFilter;
import com.elasticpath.domain.catalogview.EpCatalogViewRequestBindException;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.RangeFilter;
import com.elasticpath.domain.catalogview.impl.AdvancedSearchFilteredNavSeparatorFilterImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfiguration;
import com.elasticpath.service.catalogview.filterednavigation.FilteredNavigationConfigurationLoader;
import com.elasticpath.service.impl.AbstractEpServiceImpl;

/**
 * A factory which will create catalog <code>Filter</code>s (AKA groupings, facets, ...)
 * for filtering views on a catalog.
 * This implementation of <code>FilterFactory</code> uses an injected
 * {@link FilteredNavigationConfigurationLoader} to access the FilteredNavigationConfiguration
 * of the store for which the filter is being created.
 */
public class FilterFactoryImpl extends AbstractEpServiceImpl implements FilterFactory {
	private static final Logger LOG = Logger.getLogger(FilterFactoryImpl.class);

	private FilteredNavigationConfigurationLoader fncLoader;

	/**
	 * Creates and returns a <code>Filter</code> based on the given identifier string. If the filter
	 * being sought has been defined it will be returned, otherwise a new one with the given identifier
	 * will be created.
	 *
	 * @param idStr the identifier string
	 * @param store the store for which the filter will be operational. The store's Code must be populated,
	 * and the store's Catalog must be populated if the Category filter is requested.
	 * @return a {@link Filter}
	 * @throws EpCatalogViewRequestBindException when the given identifier string is invalid
	 * @throws EpServiceException if the store's code has not been set, or the store's Catalog has not been set for a
	 * Category filter.
	 */
	@Override
	public Filter< ? > getFilter(final String idStr, final Store store) throws EpCatalogViewRequestBindException {
		if (store.getCode() == null) {
			throw new EpServiceException("The given store's code must not be null.");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("Getting filter for storeCode=" + store.getCode() + " and filterId=" + idStr);
		}

		if (idStr.startsWith(SeoConstants.CATEGORY_PREFIX)) {
			if (store.getCatalog() == null) {
				throw new EpServiceException("The given store's Catalog must not be null.");
			}
			return createCategoryFilter(store.getCatalog(), idStr);
		} else if (idStr.startsWith(SeoConstants.ATTRIBUTE_FILTER_PREFIX)) {
			return findOrCreateAttributeValueFilter(store.getCode(), idStr);
		} else if (idStr.startsWith(SeoConstants.ATTRIBUTE_RANGE_FILTER_PREFIX)) {
			return findOrCreateAttributeRangeFilter(store.getCode(), idStr);
		} else if (idStr.startsWith(SeoConstants.PRICE_FILTER_PREFIX)) {
			return findOrCreatePriceFilter(store.getCode(), idStr);
		} else if (idStr.startsWith(SeoConstants.BRAND_FILTER_PREFIX)) {
			return createBrandFilterWithIdString(idStr);
		} else if (idStr.startsWith(SeoConstants.SEPARATOR_BETWEEN_ADV_SEARCH_AND_FITERED_NAV_FILTERS)) {
			return createAdvancedSearchFiteredNavSeparatorFilter();
		} else {
			throw new EpCatalogViewRequestBindException("Invalid filter id:" + idStr);
		}
	}

	/**
	 * Get the filter from the range filter cache with the given temporary filter.
	 *
	 * @param filter the temporary created filter.
	 * @param storeCode the code for the store for which the filter was created
	 * @return the cached range filter if exists, else return the temporary filter.
	 */
	@Override
	public Filter< ? > getFilter(final Filter< ? > filter, final String storeCode) {
		if (filter == null) {
			return null;
		}

		Filter< ? > returnValue = null;
		// Find from the range definition first.
		String filterId = filter.getId();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Getting filter for storeCode=" + storeCode + " and filterId=" + filterId);
		}
		if (filter instanceof PriceFilter) {
			returnValue = findPriceFilter(storeCode, filterId);
		} else if (filter instanceof AttributeRangeFilter) {
			returnValue = findAttributeRangeFilter(storeCode, filterId);
		} else if (filter instanceof AttributeFilter) {
			returnValue = findAttributeValueFilter(storeCode, filterId);
		}
		// Not found, then return the given filter.
		if (returnValue == null) {
			returnValue = filter;
		}

		return returnValue;
	}

	/**
	 * Creates and initializes a new {@link CategoryFilter} from the given category code
	 * and catalog.
	 *
	 * @param categoryCode the category Code
	 * @param catalog the catalog to create the filter for
	 * @return a {@link CategoryFilter}
	 */
	@Override
	public CategoryFilter createCategoryFilter(final String categoryCode, final Catalog catalog) {
		CategoryFilter catFilter = getFilterBean(ContextIdNames.CATEGORY_FILTER);
		catFilter.initializeWithCode(categoryCode, catalog);
		catFilter.setCatalog(catalog);
		return catFilter;
	}

	/**
	 * Creates a new {@link BrandFilter} from the given brand code.
	 *
	 * @param brandCode the brand code
	 * @return a {@link BrandFilter}
	 */
	@Override
	public BrandFilter createBrandFilter(final String brandCode) {
		if (StringUtils.isEmpty(brandCode)) {
			throw new EpServiceException("The given brand code must be specified.");
		}
		return createBrandFilter(new String[] {brandCode});
	}

	@Override
	public BrandFilter createBrandFilter(final String[] brandCodes) {
		BrandFilter brandFilter = getFilterBean(ContextIdNames.BRAND_FILTER);
		brandFilter.initializeWithCode(brandCodes);
		return brandFilter;
	}

	@Override
	public AdvancedSearchFilteredNavSeparatorFilter createAdvancedSearchFiteredNavSeparatorFilter() {
		AdvancedSearchFilteredNavSeparatorFilter filter = new AdvancedSearchFilteredNavSeparatorFilterImpl();
		filter.setSeparatorInToken(getSeparatorInToken());
		filter.initialize(WebConstants.SPACE);
		return filter;
	}


	/**
	 * Creates and returns a Brand filter.
	 * @param idStr the filter's ID string
	 * @return the brand filter
	 */
	BrandFilter createBrandFilterWithIdString(final String idStr) {
		BrandFilter filter = getFilterBean(ContextIdNames.BRAND_FILTER);
		filter.initialize(idStr);
		return filter;
	}

	/**
	 * Creates and initializes a new Category filter for the given Catalog. The new
	 * filter will have the given ID string.
	 * @param catalog the catalog in which the filter should apply
	 * @param idStr the filter's ID string
	 * @return the category filter
	 */
	CategoryFilter createCategoryFilter(final Catalog catalog, final String idStr) {
		CategoryFilter filter = getFilterBean(ContextIdNames.CATEGORY_FILTER);
		filter.setCatalog(catalog);
		filter.initialize(idStr);
		return filter;
	}


	/**
	 * Creates and returns an AttributeValueFilter. If one has been defined for the given store and
	 * having the given ID string, it will be returned, otherwise a new one will be created and initialized.
	 * Calls {@link #getFilteredNavigationConfiguration(String)} to get the FNC for the given store.
	 * @param storeCode the code for the store in which the filter can be applied
	 * @param idStr the filter's ID string
	 * @return the attribute range filter
	 */
	AttributeValueFilter findOrCreateAttributeValueFilter(final String storeCode, final String idStr) {
		AttributeValueFilter filter = findAttributeValueFilter(storeCode, idStr);
		if (filter == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("AttributeValueFilter for storeCode=" + storeCode + " with ID=" + idStr + " was not found. Creating a new one.");
			}
			filter = getFilterBean(ContextIdNames.ATTRIBUTE_FILTER);
			filter.initialize(idStr);
		}
		return filter;
	}

	/**
	 * Attempts to find the attribute value filter identified by the given string in the given store.
	 * @param storeCode the code for the store in which the filter can be applied
	 * @param idStr the string identifier for the filter
	 * @return the requested filter, or null if it cannot be found
	 */
	AttributeValueFilter findAttributeValueFilter(final String storeCode, final String idStr) {
		return getFilteredNavigationConfiguration(storeCode).getAllAttributeSimpleValues().get(idStr);
	}

	/**
	 * Creates and returns an AttributeRangeFilter. If one has been defined for the given store and
	 * having the given ID string, it will be returned, otherwise a new one will be created and initialized.
	 * Calls {@link #getFilteredNavigationConfiguration(String)} to get the FNC for the given store.
	 * @param storeCode the code for the store in which the filter can be applied
	 * @param idStr the filter's ID string
	 * @return the attribute range filter
	 */
	AttributeRangeFilter findOrCreateAttributeRangeFilter(final String storeCode, final String idStr) {
		AttributeRangeFilter filter = findAttributeRangeFilter(storeCode, idStr);
		if (filter == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("AttributeRangeFilter for storeCode=" + storeCode + " with ID=" + idStr + " was not found. Creating a new one.");
			}
			filter = getFilterBean(ContextIdNames.ATTRIBUTE_RANGE_FILTER);
			filter.initialize(idStr);
		}
		return filter;
	}

	/**
	 * Attempts to find the attribute range filter identified by the given string in the given store.
	 * @param storeCode the code for the store in which the filter is defined
	 * @param idStr the string identifier for the filter
	 * @return the requested filter, or null if it cannot be found
	 */
	AttributeRangeFilter findAttributeRangeFilter(final String storeCode, final String idStr) {
		return getFilteredNavigationConfiguration(storeCode).getAllAttributeRanges().get(idStr);
	}

	/**
	 * Creates and returns a price filter. If one exists for the given store and
	 * having the given ID string, it will be returned, otherwise a new one will be created and initialized.
	 * Calls {@link #getFilteredNavigationConfiguration(String)} to get the FNC for the given store.
	 * @param storeCode the code for the store in which the filter is defined
	 * @param idStr the filter's ID string
	 * @return the Price filter
	 */
	PriceFilter findOrCreatePriceFilter(final String storeCode, final String idStr) {
		PriceFilter filter = getFilteredNavigationConfiguration(storeCode).getAllPriceRanges().get(idStr);
		if (filter == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("PriceFilter for storeCode=" + storeCode + " with ID=" + idStr + " was not found. Creating a new one.");
			}
			filter = getFilterBean(ContextIdNames.PRICE_FILTER);
			filter.initialize(idStr);
		}
		return filter;
	}

	/**
	 * Attempts to find the price filter identified by the given string in the given store.
	 * @param storeCode the code for the store in which the filter is defined
	 * @param idStr the filter's ID string
	 * @return the requested filter, or null if it cannot be found
	 */
	PriceFilter findPriceFilter(final String storeCode, final String idStr) {
		return getFilteredNavigationConfiguration(storeCode).getAllPriceRanges().get(idStr);
	}

	/**
	 * Loads the <code>FilteredNavigationConfiguration</code> for the <code>Store</code>
	 * with the given StoreCode, using this instance's <code>FilteredNavigationConfigurationLoader</code>.
	 * @param storeCode the code the store whose filtered navigation configuration should be retrieved
	 * @return the FilteredNavigationConfiguration object for Store with the given StoreCode
	 */
	FilteredNavigationConfiguration getFilteredNavigationConfiguration(final String storeCode) {
		return getFncLoader().loadFilteredNavigationConfiguration(storeCode);
	}

	/**
	 * @return the Filtered Navigation Configuration Loader
	 */
	public FilteredNavigationConfigurationLoader getFncLoader() {
		return this.fncLoader;
	}

	/**
	 * @param fncLoader the Filtered Navigation Configuration Loader to set
	 */
	public void setFncLoader(final FilteredNavigationConfigurationLoader fncLoader) {
		this.fncLoader = fncLoader;
	}

	@Override
	public AttributeRangeFilter createAttributeRangeFilter(final String attributeKey, final Locale locale,
			final String lowerValue, final String upperValue) {
		Map<String, Object> filterProperties = new HashMap<String, Object>();
		filterProperties.put(AttributeFilter.ATTRIBUTE_KEY_PROPERTY, attributeKey);
		filterProperties.put(RangeFilter.LOWER_VALUE_PROPERTY, lowerValue);
		filterProperties.put(RangeFilter.UPPER_VALUE_PROPERTY, upperValue);
		filterProperties.put(AttributeFilter.ATTRIBUTE_VALUES_ALIAS_PROPERTY, lowerValue + getSeparatorInToken() + upperValue);

		AttributeRangeFilter filter = getFilterBean(ContextIdNames.ATTRIBUTE_RANGE_FILTER);
		filter.initialize(filterProperties);
		filter.setLocale(locale);

		return filter;
	}

	@Override
	public AttributeValueFilter createAttributeValueFilter(final String attributeKey, final Locale locale, final String valueString) {
		Map<String, Object> filterProperties = new HashMap<String, Object>();
		filterProperties.put(AttributeFilter.ATTRIBUTE_KEY_PROPERTY, attributeKey);
		filterProperties.put(AttributeValueFilter.ATTRIBUTE_VALUE_PROPERTY, valueString);

		AttributeValueFilter filter = getFilterBean(ContextIdNames.ATTRIBUTE_FILTER);
		filter.initialize(filterProperties);
		filter.setLocale(locale);
		return filter;
	}

	@Override
	public DisplayableFilter createDisplayableFilter(final String storeCode) {
		DisplayableFilter filter = getFilterBean(ContextIdNames.DISPLAYABLE_FILTER);
		filter.setStoreCode(storeCode);
		return filter;
	}

	@Override
	public PriceFilter createPriceFilter(final Currency currency, final BigDecimal lowerValue, final BigDecimal upperValue) {
		Map<String, Object> filterProperties = new HashMap<String, Object>();
		filterProperties.put(PriceFilter.CURRENCY_PROPERTY, currency);
		filterProperties.put(RangeFilter.LOWER_VALUE_PROPERTY, lowerValue);
		filterProperties.put(RangeFilter.UPPER_VALUE_PROPERTY, upperValue);

		filterProperties.put(PriceFilter.ALIAS_PROPERTY, lowerValue + getSeparatorInToken() + upperValue);

		PriceFilter filter = getFilterBean(ContextIdNames.PRICE_FILTER);
		filter.initialize(filterProperties);
		return filter;
	}

	@Override
	public List<AttributeRangeFilter> getAttributeRangeFiltersWithoutPredefinedRanges(
			final String storeCode) {
		Map<String, AttributeRangeFilter> simpleAttributeRangeMap =
			getFilteredNavigationConfiguration(storeCode).getAllAttributeRanges();

		List<AttributeRangeFilter> resultList = new ArrayList<AttributeRangeFilter>();

		for (Entry<String, AttributeRangeFilter> entry : simpleAttributeRangeMap.entrySet()) {
			//non-pre defined attribute ranges should not have parent or children
			if (entry.getValue().getParent() == null && CollectionUtils.isEmpty(entry.getValue().getChildren())) {
				resultList.add(entry.getValue());
			}
		}

		return resultList;
	}

	@Override
	public Map<String, AttributeValueFilter> getAllSimpleValuesMap(final String storeCode) {
		return getFilteredNavigationConfiguration(storeCode).getAllAttributeSimpleValues();
	}

	@Override
	public Set<String> getSimpleAttributeKeys(final String storeCode) {
		return getFilteredNavigationConfiguration(storeCode).getAllSimpleAttributeKeys();
	}

	@Override
	public Set<String> getDefinedBrandCodes(final String storeCode) {
		return getFilteredNavigationConfiguration(storeCode).getAllBrandCodes();
	}

	@Override
	public String getSeparatorInToken() {
		return fncLoader.getSeparatorInToken();
	}

	@Override
	public <T extends Filter< ? >> T getFilterBean(final String filterBeanName) {
		T filterBean = getBean(filterBeanName);
		filterBean.setSeparatorInToken(getSeparatorInToken());
		return filterBean;
	}
}
