package com.elasticpath.sfweb.search.impl;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.SortUtility;
import com.elasticpath.domain.catalogview.search.AdvancedSearchRequest;
import com.elasticpath.domain.store.Store;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.service.catalogview.FilterFactory;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.sfweb.formbean.AdvancedSearchControllerFormBean;
import com.elasticpath.sfweb.search.StoreSearchRequestFactory;
import com.elasticpath.sfweb.view.helpers.ParameterMapper;

/**
 * Class to build the Advanced Search request.
 */
public class AdvancedStoreSearchRequestFactoryImpl implements StoreSearchRequestFactory {

	private static final String EMPTY_STRING = "";

	private BeanFactory beanFactory;

	/** The filter factory instance that uses the advanced search configuration loader. */
	private FilterFactory filterFactory;
	
	private ParameterMapper parameterMapper;

	@Override
	public AdvancedSearchRequest build(final HttpServletRequest request, final Store store, final Locale locale, final Currency currency) {
		AdvancedSearchRequest searchRequest = getBeanFactory().getBean(ContextIdNames.ADVANCED_SEARCH_REQUEST);
		
		String sorterIdStr = ServletRequestUtils.getStringParameter(
				request, WebConstants.REQUEST_SORTER, SortUtility.constructSortTypeOrderString(StandardSortBy.RELEVANCE, SortOrder.DESCENDING));	
		String filterIdStr = ServletRequestUtils.getStringParameter(request, WebConstants.REQUEST_FILTERS, null);
		
		// non-empty means shopper already have filters defined, so don't build the filters.
		if (StringUtils.isEmpty(filterIdStr)) {
			filterIdStr = constructFilterString(request, currency);
			searchRequest.setFiltersIdStr(filterIdStr, store);
			constructBrandFiltersFromRequest(searchRequest, request);
			constructAttributeRangeFilterWithoutPredefinedRanges(searchRequest, request, store, locale);
			searchRequest.addFilter(filterFactory.createAdvancedSearchFiteredNavSeparatorFilter());
		} else {
			searchRequest.setFiltersIdStr(filterIdStr, store);		
		}
		
		searchRequest.parseSorterIdStr(sorterIdStr);
		searchRequest.setCurrency(currency);
		searchRequest.setLocale(locale);
		return searchRequest;

	}

	private String constructFilterString(final HttpServletRequest request, final Currency currency) {

		StringBuffer filterIdStr = new StringBuffer(EMPTY_STRING);
		filterIdStr.append(constructAttributeValueFilter(request));
		addFilterSeparator(filterIdStr);
		
		filterIdStr.append(constructPriceFilter(
				request.getParameter(parameterMapper.getAmountFrom()), request.getParameter(parameterMapper.getAmountTo()), currency));
		return filterIdStr.toString();
	}

	private void addFilterSeparator(final StringBuffer filterIdStr) {
		if (filterIdStr.length() > 0) {
			filterIdStr.append(' ');
		}
	}

	private AdvancedSearchControllerFormBean getAdvancedSearchControllerFormBeanFromRequest(final HttpServletRequest request) {
		String beanName = AdvancedSearchControllerFormBean.class.getSimpleName().substring(0, 1).toLowerCase() 
		   +  AdvancedSearchControllerFormBean.class.getSimpleName().substring(1);
		return (AdvancedSearchControllerFormBean) request.getAttribute(beanName);
	}
	
	private String constructAttributeValueFilter(final HttpServletRequest request) {
		
		AdvancedSearchControllerFormBean bean = getAdvancedSearchControllerFormBeanFromRequest(request);
		
		StringBuilder attributeValueFilterStringBuilder = new StringBuilder();
		
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.putAll(bean.getAttributeValuesMap());
		
		Set<Map.Entry<String, String>> entrySet = resultMap.entrySet();		
		
		for (Map.Entry<String, String> entry : entrySet) {
			String attributeValue = entry.getValue();
			
				if (!StringUtils.isEmpty(attributeValue)) {
					attributeValueFilterStringBuilder.append(attributeValue);
					attributeValueFilterStringBuilder.append(WebConstants.SPACE);
				}	
		}
		
		if (attributeValueFilterStringBuilder.length() != 0) {
			attributeValueFilterStringBuilder.deleteCharAt(attributeValueFilterStringBuilder.length() - 1);			
		}
		
		return attributeValueFilterStringBuilder.toString();
	}
	
	
	private void constructAttributeRangeFilterWithoutPredefinedRanges(final AdvancedSearchRequest searchRequest, final HttpServletRequest request, 
			final Store store, final Locale locale) {
		
		List<AttributeRangeFilter> attributeRangeFilters = filterFactory.getAttributeRangeFiltersWithoutPredefinedRanges(store.getCode());
		
		if (!CollectionUtils.isEmpty(attributeRangeFilters)) {
			for (AttributeRangeFilter attributeRangeFilter : attributeRangeFilters) {
				String attributeKey = attributeRangeFilter.getAttributeKey();
				
				String fromSuffix = request.getParameter(getParameterMapper().convertFromRangeAttributeToParameter(attributeKey)); 
				String toSuffix = request.getParameter(getParameterMapper().convertToRangeAttributeToParameter(attributeKey));
				
				if (!StringUtils.isEmpty(fromSuffix) && StringUtils.isEmpty(toSuffix) 
						|| StringUtils.isEmpty(fromSuffix) && !StringUtils.isEmpty(toSuffix)) {
					throw new EpServiceException("Attribute range for " + attributeKey + " must have both limits set");
				} else if (!StringUtils.isEmpty(fromSuffix) && !StringUtils.isEmpty(toSuffix)) {
					searchRequest.addFilter(filterFactory.createAttributeRangeFilter(attributeKey, locale, fromSuffix, toSuffix));
				}	
			}
		}
	}		
	
	
	private String constructPriceFilter(final String priceFrom, final String priceTo, final Currency currency) {
		if (StringUtils.isNumeric(priceTo) && StringUtils.isNumeric(priceFrom)) {
			BigDecimal lowerValue = new BigDecimal(priceFrom);
			BigDecimal upperValue = new BigDecimal(priceTo);
			return filterFactory.createPriceFilter(currency, lowerValue, upperValue).getId();
		}
		return EMPTY_STRING;
	}

	
	
	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return the beanFactory
	 */
	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	private void constructBrandFiltersFromRequest(final AdvancedSearchRequest searchRequest, 
			final HttpServletRequest request) {
		AdvancedSearchControllerFormBean bean = getAdvancedSearchControllerFormBeanFromRequest(request);

		if (bean.getBrands() != null && !bean.getBrands().isEmpty()) {
			String[] brands = new String[bean.getBrands().size()];
			bean.getBrands().toArray(brands);
			BrandFilter brandFilter = filterFactory.createBrandFilter(brands);
			searchRequest.addFilter(brandFilter);
		}
	}

	/**
	 * @return the filterFactory
	 */
	protected FilterFactory getFilterFactory() {
		return filterFactory;
	}

	/**
	 * @param filterFactory the filterFactory to set
	 */
	public void setFilterFactory(final FilterFactory filterFactory) {
		this.filterFactory = filterFactory;
	}

	/**
	 * @param parameterMapper the parameterMapper to set
	 */
	public void setParameterMapper(final ParameterMapper parameterMapper) {
		this.parameterMapper = parameterMapper;
	}

	/**
	 * @return the parameterMapper
	 */
	protected ParameterMapper getParameterMapper() {
		return parameterMapper;
	}

}
