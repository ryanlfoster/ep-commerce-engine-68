package com.elasticpath.sfweb.controller.impl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.FieldMethodizer;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.WebConstants;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.AttributeRangeFilter;
import com.elasticpath.domain.catalogview.AttributeValueFilter;
import com.elasticpath.domain.catalogview.BrandFilter;
import com.elasticpath.domain.catalogview.Filter;
import com.elasticpath.domain.catalogview.PriceFilter;
import com.elasticpath.domain.catalogview.search.AdvancedSearchConfigurationProvider;
import com.elasticpath.domain.catalogview.search.AdvancedSearchRequest;
import com.elasticpath.domain.catalogview.search.SearchResult;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalogview.AdvancedSearchService;
import com.elasticpath.service.catalogview.StoreConfig;
import com.elasticpath.sfweb.formbean.AdvancedSearchControllerFormBean;
import com.elasticpath.sfweb.formbean.NonPreDefinedAttributeRangeFieldFormBean;
import com.elasticpath.sfweb.formbean.impl.NonPreDefinedAttributeRangeFieldFormBeanImpl;
import com.elasticpath.sfweb.search.impl.AdvancedStoreSearchRequestFactoryImpl;
import com.elasticpath.sfweb.search.impl.CatalogViewResultBeanCreator;
import com.elasticpath.sfweb.view.helpers.ComboBoxValueMapComparator;
import com.elasticpath.sfweb.view.helpers.OptionSummaryPresenter;
import com.elasticpath.sfweb.view.helpers.ParameterMapper;
import com.elasticpath.sfweb.view.helpers.RadioButtonValueMapComparator;
import com.elasticpath.sfweb.viewbean.CatalogViewResultBean;

/**
 * Implementation Spring MVC controller class for the Advanced Search.
 */
public class AdvancedSearchControllerImpl extends AbstractEpFormController {

	private String errorView;

	private StoreProductLoadTuner storeProductLoadTuner;

	private CatalogViewResultBeanCreator catalogViewResultBeanCreator;

	private AdvancedStoreSearchRequestFactoryImpl advancedStoreSearchRequestFactoryImpl;

	private AdvancedSearchService advancedSearchService;

	private AdvancedSearchConfigurationProvider advancedSearchConfigurationProvider;

	private CategoryService categoryService;

	private ParameterMapper parameterMapper;

	/**
	 * Checks whether or not to perform the advanced search.
	 *
	 * @param request The request
	 * @return true if filter string is set in the request or if search button is present in the request
	 */
	@Override
	protected boolean isFormSubmission(final HttpServletRequest request) {
		// treat it as a search if there is a filter string in request or if search button is in request
		if (request.getParameter(parameterMapper.getFiltersParameter()) != null
				|| request.getParameter(parameterMapper.getSearchFormButtonName()) != null) {
			return true;
		}
		return false;
	}

	@Override
	public ModelAndView onSubmit(final HttpServletRequest request, final HttpServletResponse response, final Object command,
			final BindException errors) {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		return showSearchResults(request, customerSession.getShopper());
	}

	@Override
	protected Map<String, Object> referenceData(final HttpServletRequest request) throws Exception {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();
		return getInitialModelMap(shoppingCart);
	}

	@Override
	public Object formBackingObject(final HttpServletRequest request) {
		final CustomerSession customerSession = getRequestHelper().getCustomerSession(request);
		final ShoppingCart shoppingCart = customerSession.getShoppingCart();

		final AdvancedSearchControllerFormBean advancedSearchControllerFormBean = getBean(
				ContextIdNames.ADVANCED_SEARCH_CONTROLLER_FORM_BEAN);

		advancedSearchControllerFormBean.setNonPreDefinedAttributeRangeFilterMap(constructNonPreDefinedAttributeRangeFilterMap());

		advancedSearchControllerFormBean.setLocale(shoppingCart.getLocale());

		advancedSearchControllerFormBean.setStoreCode(getRequestHelper().getStoreConfig().getStoreCode());
		return advancedSearchControllerFormBean;
	}

	/**
	 * Creates a Map of the attribute keys and <code>NonPreDefinedAttributeRangeFieldFormBean</code>s.
	 *
	 * @return a map of the attribute keys (as keys) and its NonPreDefinedAttributeRangeFieldFormBean
	 */
	protected Map<String, NonPreDefinedAttributeRangeFieldFormBean> constructNonPreDefinedAttributeRangeFilterMap() {
		final Map<String, NonPreDefinedAttributeRangeFieldFormBean> resultMap = new HashMap<String, NonPreDefinedAttributeRangeFieldFormBean>();

		for (AttributeRangeFilter rangeFilter : advancedSearchConfigurationProvider
				.getAttributeRangeFiltersWithoutPredefinedRanges(getRequestHelper().getStoreConfig().getStoreCode())) {

			resultMap.put(rangeFilter.getAttributeKey(), new NonPreDefinedAttributeRangeFieldFormBeanImpl());
		}
		return resultMap;
	}

	/**
	 * Creates a Map of the attribute keys and a map of attribute value SEO filter string as a key and attribute name as a value.
	 *
	 * @param locale the locale
	 * @param shortText the indicator whether obtaining short text attribute values or boolean ones
	 * @return a Map of the attribute keys and a map of attribute value SEO filter string as a key and attribute name as a value
	 */
	protected Map<String, Map<String, String>> constructAttributeValueMap(final Locale locale, final boolean shortText) {

		final Map<Attribute, List<AttributeValueFilter>> attributeValueMap = advancedSearchConfigurationProvider.getAttributeValueFilterMap(
				getRequestHelper().getStoreConfig().getStoreCode(), locale);

		final Map<String, Map<String, String>> newAttributeValueMap = new HashMap<String, Map<String, String>>();

		final Set<Map.Entry<Attribute, List<AttributeValueFilter>>> entrySet = attributeValueMap.entrySet();

		for (Map.Entry<Attribute, List<AttributeValueFilter>> entry : entrySet) {

			final boolean isShortTextAttr = shortText && entry.getKey().getAttributeType() != AttributeType.BOOLEAN;
			final boolean isBooleanAttr = !shortText && entry.getKey().getAttributeType() == AttributeType.BOOLEAN;

			if (isShortTextAttr || isBooleanAttr) {
				final Map<String, String> keyValueForSelectControlMap = new HashMap<String, String>();
				final Map<String, Boolean> displayNameToBooleanValueMap = new HashMap<String, Boolean>();

				for (AttributeValueFilter filter : entry.getValue()) {
					keyValueForSelectControlMap.put(filter.getSeoId(), filter.getDisplayName(locale));

					if (isBooleanAttr) {
						displayNameToBooleanValueMap.put(filter.getDisplayName(locale), filter.getAttributeValue().getBooleanValue());
					}
				}

				Comparator<String> selectControlValueMapComparator;

				if (isShortTextAttr) {
					selectControlValueMapComparator = new ComboBoxValueMapComparator(keyValueForSelectControlMap);
				} else {
					selectControlValueMapComparator = new RadioButtonValueMapComparator(keyValueForSelectControlMap, displayNameToBooleanValueMap);
				}

				final Map<String, String> sortedKeyValueForSelectControlMap = new TreeMap<String, String>(selectControlValueMapComparator);
				sortedKeyValueForSelectControlMap.putAll(keyValueForSelectControlMap);
				newAttributeValueMap.put(entry.getKey().getKey(), sortedKeyValueForSelectControlMap);
			}
		}
		return newAttributeValueMap;
	}

	/**
	 * Gets a Map of the attribute keys and a map of attribute value SEO filter string as a key and attribute name as a value for boolean attributes.
	 *
	 * @param locale the locale
	 * @return the map.
	 */
	protected Map<String, Map<String, String>> constructBooleanAttributeValueMap(final Locale locale) {
		return constructAttributeValueMap(locale, false);
	}

	/**
	 * Gets a Map of attribute keys and a map of attribute value SEO filter string as a key and attribute name as a value for short text attributes.
	 *
	 * @param locale the locale
	 * @return the map.
	 */
	protected Map<String, Map<String, String>> constructShortTextAttributeValueMap(final Locale locale) {
		return constructAttributeValueMap(locale, true);
	}

	/**
	 * Sets the form backing bean as an attribute only if there are no errors.
	 *
	 * @param request The request
	 * @param command The command object
	 * @param errors The errors object
	 * @throws Exception if error occurred
	 */
	@Override
	protected void onBindAndValidate(final HttpServletRequest request, final Object command, final BindException errors) throws Exception {
		if (!errors.hasErrors()) {
			request.setAttribute(getCommandName(), command);
		}
	}

	/**
	 * Shows the search results given either the filter string or the form data.
	 *
	 * @param request The request
	 * @param shopper The shopper
	 * @return ModelAndView that has result data. For use by Spring MVC.
	 */
	private ModelAndView showSearchResults(final HttpServletRequest request, final Shopper shopper) {
		final StoreConfig storeConfig = getRequestHelper().getStoreConfig();
		final ShoppingCart shoppingCart = shopper.getCurrentShoppingCart();
		final AdvancedSearchRequest searchRequest = getAdvancedStoreSearchRequestFactory().build(request,
				storeConfig.getStore(), shoppingCart.getLocale(), shoppingCart.getCurrency());

		// form bean is bound to form objects, pre-populate the bean
		final AdvancedSearchControllerFormBean formBean = (AdvancedSearchControllerFormBean) request.getAttribute(getCommandName());
		for (Filter<?> filter : searchRequest.getAdvancedSearchFilters()) {
			if (filter instanceof AttributeValueFilter) {
				final AttributeValueFilter valueFilter = (AttributeValueFilter) filter;
				final Attribute attribute = valueFilter.getAttribute();
				formBean.getAttributeValuesMap().put(attribute.getKey(), valueFilter.getId());
			} else if (filter instanceof PriceFilter) {
				final PriceFilter priceFilter = (PriceFilter) filter;
				formBean.setAmountFrom(priceFilter.getLowerValue().toPlainString());
				formBean.setAmountTo(priceFilter.getUpperValue().toPlainString());
			} else if (filter instanceof BrandFilter) {
				final BrandFilter brandFilter = (BrandFilter) filter;
				for (Brand brand : brandFilter.getBrands()) {
					formBean.getBrands().add(brand.getCode());
				}
			} else if (filter instanceof AttributeRangeFilter) {
				final AttributeRangeFilter rangeFilter = (AttributeRangeFilter) filter;
				final Attribute attribute = rangeFilter.getAttribute();

				final NonPreDefinedAttributeRangeFieldFormBean rangeBean = new NonPreDefinedAttributeRangeFieldFormBeanImpl();
				rangeBean.setFromField(rangeFilter.getLowerValue().toString());
				rangeBean.setToField(rangeFilter.getUpperValue().toString());

				formBean.getNonPreDefinedAttributeRangeFilterMap().put(attribute.getKey(), rangeBean);
			}
		}

		final int pageNumber = ServletRequestUtils.getIntParameter(request, WebConstants.REQUEST_PAGE_NUM, 1);

		final SearchResult searchResult = getSearchResult(shoppingCart, pageNumber, searchRequest, getStoreProductLoadTuner());

		final Map<String, Object> modelMap = getModelMapWithResults(pageNumber, shopper, searchResult);

		if (searchResult.getResultsCount() == 0) {
			modelMap.put("emptyResultSet", true);
		}
		modelMap.put("advancedSearchSummaryOptionsBean", new OptionSummaryPresenter().getSummaryOptionsBean(searchRequest, shoppingCart.getLocale(),
				shoppingCart.getCurrency()));

		return new ModelAndView(getSuccessView(), modelMap);
	}

	private SearchResult getSearchResult(final ShoppingCart shoppingCart, final int pageNumber, final AdvancedSearchRequest searchRequest,
			final StoreProductLoadTuner storeProductLoadTuner) {

		return getSearchService().search(searchRequest, shoppingCart, storeProductLoadTuner, pageNumber);
	}

	/**
	 * Returns the initial model variables that are required by the view.
	 *
	 * @param shoppingCart The shopping cart
	 * @return A map of the variable names as keys and the actual objects as values.
	 */
	protected Map<String, Object> getInitialModelMap(final ShoppingCart shoppingCart) {
		final Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("currency", shoppingCart.getCurrency());
		populateShortTextDropDownAttributes(modelMap, shoppingCart);
		final List<Brand> brandList = advancedSearchConfigurationProvider.getBrandListSortedByName(shoppingCart.getLocale(), shoppingCart.getStore()
				.getCode());

		modelMap.put("brandList", brandList);
		modelMap.put("parameterMapper", getParameterMapper());
		modelMap.put("attributeType", new FieldMethodizer("com.elasticpath.domain.attribute.AttributeType"));
		modelMap.put("shortTextAttributeValueMap", constructShortTextAttributeValueMap(shoppingCart.getLocale()));
		modelMap.put("booleanAttributeValueMap", constructBooleanAttributeValueMap(shoppingCart.getLocale()));
		modelMap.put("brandCodeNameMap", constructBrandFilterDisplayNameMap(brandList, shoppingCart.getLocale()));

		return modelMap;
	}

	private Map<Brand, Map<String, String>> constructBrandFilterDisplayNameMap(final List<Brand> brandList, final Locale locale) {
		final Map<Brand, Map<String, String>> brandCodeNameMap = new HashMap<Brand, Map<String, String>>();
		for (Brand brand : brandList) {
			final Map<String, String> codeNameMap = new HashMap<String, String>();
			codeNameMap.put(brand.getCode(), brand.getDisplayName(locale));
			brandCodeNameMap.put(brand, codeNameMap);
		}
		return brandCodeNameMap;
	}

	/**
	 * Appends to the model the required parameters so that the controller can render the attribute and its associated values correctly.
	 *
	 * @param modelMap The model map to be used in generating the ModelAndView
	 */
	private void populateShortTextDropDownAttributes(final Map<String, Object> modelMap, final ShoppingCart cart) {
		final AdvancedSearchConfigurationProvider advancedSearchConfigurationProvider = getAdvancedSearchConfigurationProvider();

		modelMap.put("attributeValueFilterMap", advancedSearchConfigurationProvider.getAttributeValueFilterMap(getRequestHelper().getStoreConfig()
				.getStoreCode(), cart.getLocale()));

		modelMap.put("decimalAttributeRangeFilterList", advancedSearchConfigurationProvider
				.getAttributeRangeFiltersWithoutPredefinedRanges(getRequestHelper().getStoreConfig().getStoreCode()));
	}

	/**
	 * Gets the model map to be returned with the view which contains the results.
	 *
	 * @param pageNumber The page number to retrieve
	 * @param shopper The session
	 * @param searchRequest The search request
	 * @param searchResult The search result
	 * @return A map of of the variables that are required by the view
	 */
	private Map<String, Object> getModelMapWithResults(final int pageNumber, final Shopper shopper, final SearchResult searchResult) {

		final ShoppingCart shoppingCart = shopper.getCurrentShoppingCart();
		final CatalogViewResultBean catalogViewResultBean = getCatalogViewResultBeanCreator().createCatalogViewResultBean(pageNumber, searchResult,
				shoppingCart.getSearchResultHistory(), shopper, getRequestHelper().getStoreConfig());

		final Map<String, Object> modelMap = getInitialModelMap(shoppingCart);

		modelMap.put("catalogViewResultBean", catalogViewResultBean);

		return modelMap;
	}

	/**
	 * @param storeProductLoadTuner the storeProductLoadTuner to set
	 */
	public void setStoreProductLoadTuner(final StoreProductLoadTuner storeProductLoadTuner) {
		this.storeProductLoadTuner = storeProductLoadTuner;
	}

	/**
	 * @return the storeProductLoadTuner
	 */
	public StoreProductLoadTuner getStoreProductLoadTuner() {
		return storeProductLoadTuner;
	}

	/**
	 * @param errorView the errorView to set
	 */
	public void setErrorView(final String errorView) {
		this.errorView = errorView;
	}

	/**
	 * @return the errorView
	 */
	public String getErrorView() {
		return errorView;
	}

	/**
	 * @param catalogViewResultBeanCreator the catalogViewResultBeanCreator to set
	 */
	public void setCatalogViewResultBeanCreator(final CatalogViewResultBeanCreator catalogViewResultBeanCreator) {
		this.catalogViewResultBeanCreator = catalogViewResultBeanCreator;
	}

	/**
	 * @return the catalogViewResultBeanCreator
	 */
	protected CatalogViewResultBeanCreator getCatalogViewResultBeanCreator() {
		return catalogViewResultBeanCreator;
	}

	/**
	 * @param advancedStoreSearchRequestFactoryImpl the AdvancedStoreSearchRequestFactoryImpl to set
	 */
	public void setAdvancedStoreSearchRequestFactory(final AdvancedStoreSearchRequestFactoryImpl advancedStoreSearchRequestFactoryImpl) {
		this.advancedStoreSearchRequestFactoryImpl = advancedStoreSearchRequestFactoryImpl;
	}

	/**
	 * @return the AdvancedStoreSearchRequestFactoryImpl
	 */
	protected AdvancedStoreSearchRequestFactoryImpl getAdvancedStoreSearchRequestFactory() {
		return advancedStoreSearchRequestFactoryImpl;
	}

	/**
	 * @param advancedSearchService the searchService to set
	 */
	public void setAdvancedSearchService(final AdvancedSearchService advancedSearchService) {
		this.advancedSearchService = advancedSearchService;
	}

	/**
	 * @return the advancedSearchService
	 */
	protected AdvancedSearchService getSearchService() {
		return advancedSearchService;
	}

	/**
	 * @return the categoryService
	 */
	public CategoryService getCategoryService() {
		return categoryService;
	}

	/**
	 * @param categoryService the categoryService to set
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * @param advancedSearchConfigurationProvider the attributeFilterFactory to set
	 */
	public void setAdvancedSearchConfigurationProvider(final AdvancedSearchConfigurationProvider advancedSearchConfigurationProvider) {
		this.advancedSearchConfigurationProvider = advancedSearchConfigurationProvider;
	}

	/**
	 * @return the attributeFilterFactory
	 */
	protected AdvancedSearchConfigurationProvider getAdvancedSearchConfigurationProvider() {
		return advancedSearchConfigurationProvider;
	}

	/**
	 * @param parameterMapper the parameterMapper to set
	 */
	public void setParameterMapper(final ParameterMapper parameterMapper) {
		this.parameterMapper = parameterMapper;

	}

	private ParameterMapper getParameterMapper() {
		return parameterMapper;
	}
}
