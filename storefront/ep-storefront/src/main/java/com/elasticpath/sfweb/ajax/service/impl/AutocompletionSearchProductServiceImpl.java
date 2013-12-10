/**
 * 
 */
package com.elasticpath.sfweb.ajax.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.directwebremoting.WebContextFactory;

import com.elasticpath.common.pricing.service.PriceLookupFacade;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.util.impl.StoreThemeMessageSource;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.StoreProductLoadTuner;
import com.elasticpath.domain.catalogview.StoreSeoUrlBuilderFactory;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.MoneyFormatter;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.search.query.ProductAutocompleteSearchCriteria;
import com.elasticpath.service.search.solr.SolrIndexSearcherImpl;
import com.elasticpath.service.search.solr.query.SolrIndexSearchResult;
import com.elasticpath.sfweb.ajax.bean.AutocompletionSearchResult;
import com.elasticpath.sfweb.ajax.bean.impl.AutocompletionRequestImpl;
import com.elasticpath.sfweb.ajax.service.AutocompletionSearchProductService;
import com.elasticpath.sfweb.util.SfRequestHelper;
import com.elasticpath.sfweb.view.helpers.PerStoreSettingHelper;

/**
 * Implementation of <code>AutocompletionSearchProductService</code>.
 *
 */
public class AutocompletionSearchProductServiceImpl implements AutocompletionSearchProductService {
	
	private PerStoreSettingHelper autocompletionEnabledHelper;
	private PerStoreSettingHelper autocompletionProductNameMaxLengthHelper;
	private PerStoreSettingHelper autocompletionProductDescriptionMaxLengthHelper;
	private PerStoreSettingHelper priceEnabledHelper;
	private PerStoreSettingHelper thumbnailEnabledHelper;
	private PerStoreSettingHelper numberOfResultsHelper;
	private PerStoreSettingHelper seoEnabledHelper;
	private PriceLookupFacade priceLookupFacade;
	private SfRequestHelper requestHelper;
	private BeanFactory beanFactory;
	private StoreProductLoadTuner autocompletionProductLoadTuner;
	private StoreSeoUrlBuilderFactory storeSeoUrlBuilderFactory;
	
	private SolrIndexSearcherImpl solrIndexSearcher;
	private ProductService productService;
	private StoreThemeMessageSource messageSource;
	private MoneyFormatter moneyFormatter;
	private PriceBuilder priceBuilder;

	private String storefrontContextUrl;
	
	@Override
	public List<AutocompletionSearchResult> findProducts(final AutocompletionRequestImpl autocompletionRequest) {

		if (!autocompletionEnabledHelper.getBoolean() || autocompletionRequest.getSearchText() == null
				|| autocompletionRequest.getSearchText().trim().length() == 0) {
			return Collections.emptyList();
		}
		
		final CustomerSession customerSession = getCustomerSession();
		final Shopper shopper = customerSession.getShopper();
		final ShoppingCart shoppingCart = shopper.getCurrentShoppingCart();

		ProductAutocompleteSearchCriteria productSearchCriteria = getProductSearchCriteria(
				autocompletionRequest, shoppingCart);
		
		List<Product> productsList = performSolrSearch(
				productSearchCriteria,
				numberOfResultsHelper.getInteger());

		// check for prices
		Map<String, Price> prices = null;
		
		if (priceEnabledHelper.getBoolean()) {			
			prices = getPriceMap(shoppingCart, shopper, productsList);			
		}
		
		return AutocompletionSearchResultBuilder.build(
				productsList, 
				prices, 
				new AutocompletionSearchResultConfiguration(
						thumbnailEnabledHelper.getBoolean(), 
						seoEnabledHelper.getBoolean(), 
						autocompletionProductNameMaxLengthHelper.getInteger(),
						autocompletionProductDescriptionMaxLengthHelper.getInteger()), 
				shoppingCart.getLocale(), 
				storeSeoUrlBuilderFactory.getStoreSeoUrlBuilder(), 
				storefrontContextUrl
				, messageSource, getMoneyFormatter());
		
	}
	
	
	
	
	/**
	 * @param shoppingCart the shopping cart.
	 * @param shopper the customer session.
	 * @param productsList the products for which to generate price map.
	 * @return map of prices for given product list.
	 */
	Map<String, Price> getPriceMap(final ShoppingCart shoppingCart, final Shopper shopper, final List<Product> productsList) {
		Map<String, Price> prices;
		prices = getPriceBuilder().build(productsList, shoppingCart, shopper, priceLookupFacade);
		return prices;
	}

	/**
	 * @param productSearchCriteria criteria for product retrieval
	 * @param maxResults maximum number of results required
	 * @return list of products
	 */
	List<Product> performSolrSearch(
			final ProductAutocompleteSearchCriteria productSearchCriteria,
			final int maxResults) {
		// search result 
		SolrIndexSearchResult searchResult = new SolrIndexSearchResult();
		searchResult.setSearchCriteria(productSearchCriteria);
		// try search
		solrIndexSearcher.search(productSearchCriteria, maxResults, searchResult);
		
		List<Long> resultUids = searchResult.getResultUids();
		// get a products list by uids 
		return productService.findByUids(resultUids, autocompletionProductLoadTuner);
	}

	/**
	 * Create product search criteria.
	 * @param autocompletionRequest request data
	 * @param shoppingCart shopping cart
	 * @return {@link ProductAutocompleteSearchCriteria} 
	 */
	ProductAutocompleteSearchCriteria getProductSearchCriteria(
			final AutocompletionRequestImpl autocompletionRequest, final ShoppingCart shoppingCart) {
		
		// escape HTML chars and Solr special chars
		final String searchText = ClientUtils.escapeQueryChars(
				StringEscapeUtils.unescapeHtml(autocompletionRequest.getSearchText().trim()));	
		
		ProductAutocompleteSearchCriteria productSearchCriteria = beanFactory.getBean(ContextIdNames.PRODUCT_AUTOCOMPLETE_SEARCH_CRITERIA);
		productSearchCriteria.setSearchText(searchText);
		productSearchCriteria.setFuzzySearchDisabled(true);
		productSearchCriteria.setActiveOnly(true);
		productSearchCriteria.setLocale(shoppingCart.getLocale());
		productSearchCriteria.setStoreCode(shoppingCart.getStore().getCode());
		productSearchCriteria.setDisplayableOnly(true);

		if (autocompletionRequest.getCategoryUid() != null && autocompletionRequest.getCategoryUid().trim().length() > 0) {
			Long categoryUid = Long.parseLong(autocompletionRequest.getCategoryUid());
			productSearchCriteria.setDirectCategoryUid(categoryUid);
		}
		return productSearchCriteria;
	}

	/**
	 * Get the {@link CustomerSession} from the DWR request.
	 *
	 * @return the CustomerSession.
	 */
	CustomerSession getCustomerSession() {
		HttpServletRequest httpRequest = WebContextFactory.get().getHttpServletRequest();
		return requestHelper.getCustomerSession(httpRequest);
	}

	/**
	 * Set the price enable helper. 
	 * @param priceEnabledHelper instance to set.
	 */
	public void setPriceEnabledHelper(final PerStoreSettingHelper priceEnabledHelper) {
		this.priceEnabledHelper = priceEnabledHelper;
	}


	/**
	 * Set thumbnails enabled helper.
	 * @param thumbnailEnabledHelper instance to set
	 */
	public void setThumbnailEnabledHelper(
			final PerStoreSettingHelper thumbnailEnabledHelper) {
		this.thumbnailEnabledHelper = thumbnailEnabledHelper;
	}

	/**
	 * Set the number of results helper.
	 * @param numberOfResultsHelper to set.
	 */
	public void setNumberOfResultsHelper(
			final PerStoreSettingHelper numberOfResultsHelper) {
		this.numberOfResultsHelper = numberOfResultsHelper;
	}
	
	/**
	 * @param priceLookupFacade {@link PriceLookupFacade}
	 */
	public void setPriceLookupFacade(final PriceLookupFacade priceLookupFacade) {
		this.priceLookupFacade = priceLookupFacade;
	}

	/**
	 * @param requestHelper the requestHelper to set
	 */
	public void setRequestHelper(final SfRequestHelper requestHelper) {
		this.requestHelper = requestHelper;
	}
	
	/**
	 * Sets the {@code BeanFactory}.
	 * @param beanFactory The {@code BeanFactory}.
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @param autocompletionProductLoadTuner load tuner to test.
	 */
	public void setAutocompletionProductLoadTuner(
			final StoreProductLoadTuner autocompletionProductLoadTuner) {
		this.autocompletionProductLoadTuner = autocompletionProductLoadTuner;
	}
	
	/**
	 * 
	 * @param seoEnabledHelper seo enabled helper to set
	 */
	public void setSeoEnabledHelper(final PerStoreSettingHelper seoEnabledHelper) {
		this.seoEnabledHelper = seoEnabledHelper;
	}

	/**
	 * 
	 * @param storeSeoUrlBuilderFactory seo builder factory to set.
	 */
	public void setStoreSeoUrlBuilderFactory(
			final StoreSeoUrlBuilderFactory storeSeoUrlBuilderFactory) {
		this.storeSeoUrlBuilderFactory = storeSeoUrlBuilderFactory;
	}

	/**
	 * @param solrIndexSearcher the solrIndexSearcher to set
	 */
	public void setSolrIndexSearcher(final SolrIndexSearcherImpl solrIndexSearcher) {
		this.solrIndexSearcher = solrIndexSearcher;
	}

	/**
	 * @param productService the productService to set
	 */
	public void setProductService(final ProductService productService) {
		this.productService = productService;
	}
	
	/**
	 * @param autocompletionEnabledHelper the autocompletionEnabledHelper to set
	 */
	public void setAutocompletionEnabledHelper(final PerStoreSettingHelper autocompletionEnabledHelper) {
		this.autocompletionEnabledHelper = autocompletionEnabledHelper;
	}
	
	/**
	 * @param autocompletionProductNameMaxLengthHelper the autocompletionProductNameMaxLengthHelper to set
	 */
	public void setAutocompletionProductNameMaxLengthHelper(final PerStoreSettingHelper autocompletionProductNameMaxLengthHelper) {
		this.autocompletionProductNameMaxLengthHelper = autocompletionProductNameMaxLengthHelper;
	}

	/**
	 * @param autocompletionProductDescriptionMaxLengthHelper the autocompletionProductDescriptionMaxLengthHelper to set
	 */
	public void setAutocompletionProductDescriptionMaxLengthHelper(final PerStoreSettingHelper autocompletionProductDescriptionMaxLengthHelper) {
		this.autocompletionProductDescriptionMaxLengthHelper = autocompletionProductDescriptionMaxLengthHelper;
	}
	

	/**
	 * 
	 * @param storefrontContextUrl The context url for the storefront.
	 */
	public void setStorefrontContextUrl(final String storefrontContextUrl) {
		this.storefrontContextUrl = storefrontContextUrl;
	}
	
	/**
	 * @param messageSource the messageSource to set
	 */
	public void setMessageSource(final StoreThemeMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	/**
	 * @return the messageSource
	 */
	public StoreThemeMessageSource getMessageSource() {
		return messageSource;
	}

	public void setPriceBuilder(final PriceBuilder priceBuilder) {
		this.priceBuilder = priceBuilder;
	}

	protected PriceBuilder getPriceBuilder() {
		return priceBuilder;
	}

	public void setMoneyFormatter(final MoneyFormatter formatter) {
		this.moneyFormatter = formatter;
	}

	protected MoneyFormatter getMoneyFormatter() {
		return moneyFormatter;
	}
}
