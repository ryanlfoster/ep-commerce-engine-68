package com.elasticpath.search.index.solr.document.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.service.catalogview.IndexProduct;
import com.elasticpath.service.catalogview.StoreProductService;
import com.elasticpath.service.search.SkuSearchResultType;
import com.elasticpath.service.search.solr.IndexUtility;
import com.elasticpath.service.search.solr.SolrIndexConstants;
import com.elasticpath.service.store.StoreService;

/**
 * Creates a {@link SolrInputDocument} for an {@link ProductSku}.
 */
public class SkuSolrInputDocumentCreator extends AbstractDocumentCreatingTask<ProductSku> {

	private SolrInputDocument solrInputDocument;

	private StoreProductService storeProductService;

	private StoreService storeService;

	private FetchGroupLoadTuner productSkuLoadTuner;

	private final Map<Catalog, Collection<Locale>> cachedSupportedLocaleByCatalog = new HashMap<Catalog, Collection<Locale>>();

	private Collection<Store> allCompleteStores;

	private final Map<Set<Long>, Collection<Store>> cachedStoresByCatalogUids = new HashMap<Set<Long>, Collection<Store>>();

	private IndexUtility indexUtility;

	/**
	 * The catalog related fields required by the sku index.
	 */
	protected class SkuCatalogFields {
		private final Collection<Locale> supportedLocales;

		private final Locale defaultLocale;

		private boolean available;

		/**
		 * Constructor.
		 * 
		 * @param availability the availability
		 * @param supportedLocales the collection of supported locales
		 * @param defaultLocale the default locale
		 */
		public SkuCatalogFields(final boolean availability, final Collection<Locale> supportedLocales, final Locale defaultLocale) {
			super();
			available = availability;
			this.supportedLocales = supportedLocales;
			this.defaultLocale = defaultLocale;
		}

		/**
		 * Is the sku available in this catalog?
		 * 
		 * @return true if sku is available
		 */
		public boolean isAvailable() {
			return available;
		}

		/**
		 * Set sku availability.
		 * 
		 * @param available true if the sku is available
		 */
		public void setAvailable(final boolean available) {
			this.available = available;
		}

		/**
		 * Get the supported locales in this catalog.
		 * 
		 * @return the collection of supported locales
		 */
		public Collection<Locale> getSupportedLocales() {
			return supportedLocales;
		}

		/**
		 * Get the default locale for this catalog.
		 * 
		 * @return the default locale
		 */
		public Locale getDefaultLocale() {
			return defaultLocale;
		}
	}

	/**
	 * Creates a {@link SolrInputDocument} based on the {@link ProductSku}.
	 * 
	 * @return the {@link SolrInputDocument}.
	 */
	public SolrInputDocument createDocument() {
		if (getEntity() == null) {
			return null;
		}

		initialize();
		addSkuValues(solrInputDocument, getEntity());
		final Map<Long, SkuCatalogFields> catalogFields = addAvailableCatalogsToDocument(solrInputDocument, getEntity());
		final Locale defaultLocale = getDefaultLocale(catalogFields);
		final Brand brand = getEntity().getProduct().getBrand();
		addBrandValues(solrInputDocument, brand, defaultLocale);
		final IndexProduct product = getStoreProductService().createIndexProduct(getEntity().getProduct(), getAllCompleteStores());
		addFieldToDocument(solrInputDocument, SolrIndexConstants.PRODUCT_CODE, getAnalyzer().analyze(product.getCode()));
		addFieldToDocument(solrInputDocument, SolrIndexConstants.SKU_CONFIG_DEFAULT_LOCALE, getAnalyzer().analyze(
				getEntity().getDisplayName(defaultLocale)));

		addFieldNotMultiValuedToDocument(solrInputDocument, SolrIndexConstants.PRODUCT_NAME_NON_LC, getAnalyzer().analyze(
				product.getDisplayName(defaultLocale)));

		final Collection<Store> containingStores = findStoresWithCatalogUids(catalogFields.keySet());
		for (final Store store : containingStores) {
			addFieldToDocument(solrInputDocument, SolrIndexConstants.STORE_CODE, store.getCode());
		}

		addDisplayableFieldsToDocument(solrInputDocument, product, catalogFields, getAllCompleteStores());
		addLocaleSpecificFieldsToDocument(solrInputDocument, getEntity(), product, brand, catalogFields);
		addResultTypeToDocument(solrInputDocument, getEntity());

		return solrInputDocument;
	}

	/**
	 * Adds to a solr search document fields from the sku's brand.
	 * 
	 * @param document the solr search document to which the fields must be added
	 * @param sku the {@link ProductSku} to get fields from.
	 */
	protected void addSkuValues(final SolrInputDocument document, final ProductSku sku) {
		addFieldToDocument(document, SolrIndexConstants.OBJECT_UID, String.valueOf(sku.getUidPk()));
		addFieldToDocument(document, SolrIndexConstants.START_DATE, getAnalyzer().analyze(sku.getEffectiveStartDate()));
		addFieldToDocument(document, SolrIndexConstants.END_DATE, getAnalyzer().analyze(sku.getEffectiveEndDate()));
		addFieldToDocument(document, SolrIndexConstants.PRODUCT_SKU_CODE, getAnalyzer().analyze(sku.getSkuCode()));
	}

	/**
	 * <p>
	 * For each of the stores containing the given product a field containing the store's code is added to the index document specifying whether the
	 * product is displayable in that store.
	 * </p>
	 * <p>
	 * This implementation calls {@link ProductIndexBuilder#isProductAvailableAndDisplayable(IndexProduct, Store, Map)}
	 * </p>
	 * 
	 * @param document the solr input document
	 * @param product the product being indexed
	 * @param catalogFields map of catalog UIDs to catalog related fields to determining whether the given product is displayable.
	 * @param allStores collection of all known stores
	 */
	protected void addDisplayableFieldsToDocument(final SolrInputDocument document, final IndexProduct product,
			final Map<Long, SkuCatalogFields> catalogFields, final Collection<Store> allStores) {
		for (final Store store : allStores) {
			addFieldToDocument(document, getIndexUtility().createDisplayableFieldName(SolrIndexConstants.DISPLAYABLE, store.getCode()), String
					.valueOf((isProductAvailableAndDisplayable(product, store, catalogFields))));
		}
	}

	/**
	 * Determines whether the given product is both Available in all of its catalogs and Displayable in the given Store.
	 * 
	 * @param product the product being indexed
	 * @param store the store in which the product is being indexed
	 * @param catalogFields map of catalog UIDs to Availability flags
	 * @return true if the product is Displayable in the given store and Available in all catalogs containing the product
	 */
	protected boolean
			isProductAvailableAndDisplayable(final IndexProduct product, final Store store, final Map<Long, SkuCatalogFields> catalogFields) {
		boolean displayable = product.isDisplayable(store.getCode());

		// not sold separately products should not show up in search results, but they can be displayed
		displayable &= !product.isNotSoldSeparately();

		if (catalogFields.get(store.getCatalog().getUidPk()) != null) {
			// we should always get here -- only display if a product is both displayable and
			// its category is displayable
			displayable &= catalogFields.get(store.getCatalog().getUidPk()).isAvailable();

		}
		return displayable;
	}

	private void initialize() {
		allCompleteStores = getStoreService().findAllCompleteStores(getProductSkuLoadTuner());
		cachedSupportedLocaleByCatalog.clear();
		cachedStoresByCatalogUids.clear();
		solrInputDocument = new SolrInputDocument();
	}

	/**
	 * Adds to a solr search document fields from the sku's brand.
	 * 
	 * @param document the solr search document to which the fields must be added
	 * @param brand the brand of the SKU being indexed
	 * @param defaultLocale the defaultLocale of the SKU being indexed
	 */
	protected void addBrandValues(final SolrInputDocument document, final Brand brand, final Locale defaultLocale) {
		if (brand != null) {
			addFieldToDocument(document, SolrIndexConstants.BRAND_CODE, getAnalyzer().analyze(brand.getCode()));
			addFieldToDocument(document, SolrIndexConstants.BRAND_NAME, getAnalyzer().analyze(brand.getDisplayName(defaultLocale, true)));
			addFieldNotMultiValuedToDocument(document, SolrIndexConstants.SORT_BRAND_NAME, getAnalyzer().analyze(
					brand.getDisplayName(defaultLocale, true)));
		}
	}

	/**
	 * Adds to a solr search document the UIDs of catalogs in which the given sku exists.
	 * 
	 * @param document the solr search document to which the fields must be added
	 * @param sku the SKU being indexed
	 * @return a map of UIDs of catalogs in which this sku exists, and the required catalog fields.
	 */
	protected Map<Long, SkuCatalogFields> addAvailableCatalogsToDocument(final SolrInputDocument document, final ProductSku sku) {
		final Product product = sku.getProduct();

		final Set<String> catalogCodes = new HashSet<String>();
		final Set<String> parentCategoryCodes = new HashSet<String>();
		final Map<Long, SkuCatalogFields> catalogFields = new HashMap<Long, SkuCatalogFields>();
		Locale defaultLocale = null;

		for (final Category category : product.getCategories()) {
			final Catalog catalog = category.getCatalog();
			final long catalogUid = catalog.getUidPk();
			final String catalogCode = catalog.getCode();
			catalogCodes.add(catalogCode);

			if (catalog.isMaster()) {
				defaultLocale = catalog.getDefaultLocale();
			}

			// 1: if this category or any of this category's parent categories are not available, it's not available
			boolean available = true;
			available &= category.isAvailable();
			available &= checkAvailableAndPopulateParents(category.getParent(), parentCategoryCodes, available);

			// 2: As long as one route to the root is available, we're available
			SkuCatalogFields skuCatalogFields = catalogFields.get(catalogUid);
			if (skuCatalogFields == null) {
				skuCatalogFields = new SkuCatalogFields(available, getCatalogSupportedLocales(catalog), defaultLocale);
			} else {
				skuCatalogFields.setAvailable(available | skuCatalogFields.isAvailable());
			}

			catalogFields.put(catalogUid, skuCatalogFields);
		}
		addFieldToDocument(document, SolrIndexConstants.CATALOG_CODE, catalogCodes);
		return catalogFields;
	}

	/**
	 * Get the unmodifiable collection of locales that are supported by a master catalog or all the all the currencies assigned to all master
	 * catalogs in the system for a virtual catalog.
	 * 
	 * @param catalog the catalog
	 * @return the locales that are supported by this catalog
	 */
	protected Collection<Locale> getCatalogSupportedLocales(final Catalog catalog) {
		Collection<Locale> locales = cachedSupportedLocaleByCatalog.get(catalog);
		if (locales == null) {
			locales = catalog.getSupportedLocales();
			cachedSupportedLocaleByCatalog.put(catalog, locales);
		}
		return locales;
	}

	/**
	 * Gets all the complete stores.
	 * 
	 * @return a list containing all the complete stores
	 */
	protected Collection<Store> getAllCompleteStores() {
		return allCompleteStores;
	}

	/**
	 * Finds all the {@link Store}s that are associated with the given catalog UIDs.
	 * 
	 * @param catalogUids a collection of catalog UIDs
	 * @return all the {@link Store}s that are associated with the given catalog UIDs
	 */
	protected Collection<Store> findStoresWithCatalogUids(final Set<Long> catalogUids) {
		Collection<Store> stores = cachedStoresByCatalogUids.get(catalogUids);
		if (stores == null) {
			stores = getStoreService().findStoresWithCatalogUids(catalogUids);
			cachedStoresByCatalogUids.put(catalogUids, stores);
		}
		return stores;
	}

	/**
	 * Add locale-specific sku fields from the given sku to the given solr input document.
	 * 
	 * @param document the solr input document
	 * @param sku the sku being indexed
	 * @param product the product that the sku belongs to
	 * @param brand the brand that the sku belongs to
	 * @param catalogFields the catalog related fields
	 */
	protected void addLocaleSpecificFieldsToDocument(final SolrInputDocument document, final ProductSku sku, final Product product,
			final Brand brand, final Map<Long, SkuCatalogFields> catalogFields) {
		final Set<Locale> allLocales = new HashSet<Locale>();
		for (final SkuCatalogFields catalogField : catalogFields.values()) {
			allLocales.addAll(catalogField.getSupportedLocales());
		}

		for (final Locale locale : allLocales) {
			addFieldToDocument(document, getIndexUtility().createLocaleFieldName(SolrIndexConstants.PRODUCT_NAME, locale), getAnalyzer().analyze(
					product.getDisplayName(locale)));
			addFieldNotMultiValuedToDocument(document, getIndexUtility().createLocaleFieldName(SolrIndexConstants.SORT_PRODUCT_NAME, locale),
					getAnalyzer().analyze(product.getDisplayName(locale)));

			if (brand != null) {
				addFieldToDocument(document, getIndexUtility().createLocaleFieldName(SolrIndexConstants.BRAND_NAME, locale), getAnalyzer().analyze(
						brand.getDisplayName(locale, true)));
				addFieldNotMultiValuedToDocument(document, getIndexUtility().createLocaleFieldName(SolrIndexConstants.SORT_BRAND_NAME, locale),
						getAnalyzer().analyze(brand.getDisplayName(locale, true)));
			}

			addFieldToDocument(document, getIndexUtility().createLocaleFieldName(SolrIndexConstants.SKU_CONFIGURATION, locale), getAnalyzer()
					.analyze(sku.getDisplayName(locale)));

			if (sku.getAttributeValueMap() != null && !sku.getAttributeValueMap().isEmpty()) {
				for (final AttributeValue attributeValue : sku.getAttributeValueMap().values()) {
					addAttributeToDocument(document, attributeValue, locale);
				}
			}

			for (final SkuOptionValue skuOptionValue : sku.getOptionValues()) {
				addSkuOptionValueToDocument(document, skuOptionValue, locale);
			}

		}
	}

	/**
	 * Adds a sku attribute to the SOLR document.
	 * 
	 * @param document the document to use
	 * @param attributeValue the attribute value to add
	 * @param locale the locale
	 */
	protected void addAttributeToDocument(final SolrInputDocument document, final AttributeValue attributeValue, final Locale locale) {
		final Attribute attribute = attributeValue.getAttribute();
		final String fieldName = getIndexUtility().createAttributeFieldName(attribute, locale, false, false);
		if (attribute.isMultiValueEnabled()) {
			final Collection<String> values = ((AttributeValueWithType) attributeValue).getShortTextMultiValues();
			addFieldToDocument(document, fieldName, values);
		} else {
			String analyzedValue;
			// Date and Date & Time attribute values must be parsed using the analyzer
			final AttributeType attributeType = attribute.getAttributeType();
			if (attributeType == AttributeType.DATE || attributeType == AttributeType.DATETIME) {
				analyzedValue = getAnalyzer().analyze((Date) attributeValue.getValue());
			} else if (attributeType == AttributeType.DECIMAL) {
				analyzedValue = getAnalyzer().analyze((BigDecimal) attributeValue.getValue());
			} else {
				analyzedValue = getAnalyzer().analyze(attributeValue.getStringValue());
			}
			addFieldToDocument(document, fieldName, analyzedValue);
		}
	}

	/**
	 * Adds a sku option value to the the SOLR document.
	 * 
	 * @param document the document to use
	 * @param skuOptionValue the sku option valuye to add
	 * @param locale the locale
	 */
	protected void addSkuOptionValueToDocument(final SolrInputDocument document, final SkuOptionValue skuOptionValue, final Locale locale) {
		final SkuOption skuOption = skuOptionValue.getSkuOption();
		final String fieldName = getIndexUtility().createSkuOptionFieldName(locale, skuOption.getOptionKey());
		addFieldToDocument(document, fieldName, getAnalyzer().analyze(skuOptionValue.getDisplayName(locale, false)));
	}

	/**
	 * Adds a field that indicates the type of SKU the document represents. See {@link SkuSearchResultType}
	 * 
	 * @param document the document
	 * @param sku the sku being indexed.
	 */
	protected void addResultTypeToDocument(final SolrInputDocument document, final ProductSku sku) {
		if (sku.getProduct().hasMultipleSkus()) {
			addFieldToDocument(document, SolrIndexConstants.SKU_RESULT_TYPE, getAnalyzer().analyze(SkuSearchResultType.PRODUCT_SKU.getSortOrder()));
		} else if (sku.getProduct() instanceof ProductBundle) {
			addFieldToDocument(document, SolrIndexConstants.SKU_RESULT_TYPE, getAnalyzer()
					.analyze(SkuSearchResultType.PRODUCT_BUNDLE.getSortOrder()));
		} else {
			addFieldToDocument(document, SolrIndexConstants.SKU_RESULT_TYPE, getAnalyzer().analyze(SkuSearchResultType.PRODUCT.getSortOrder()));
		}
	}

	private boolean checkAvailableAndPopulateParents(final Category parent, final Set<String> parentCategoryCodes, final boolean currentVisiblity) {
		boolean available = currentVisiblity;
		Category curParent = parent;
		while (curParent != null) {
			parentCategoryCodes.add(curParent.getCode());
			available &= curParent.isAvailable();
			curParent = curParent.getParent();
		}
		return available;
	}

	private Locale getDefaultLocale(final Map<Long, SkuCatalogFields> catalogFields) {
		for (final SkuCatalogFields catalogField : catalogFields.values()) {
			if (catalogField.getDefaultLocale() != null) {
				return catalogField.getDefaultLocale();
			}
		}
		return null;
	}

	/**
	 * @param storeProductService the storeProductService to set
	 */
	public void setStoreProductService(final StoreProductService storeProductService) {
		this.storeProductService = storeProductService;
	}

	/**
	 * @return the storeProductService
	 */
	public StoreProductService getStoreProductService() {
		return storeProductService;
	}

	/**
	 * @param storeService the storeService to set
	 */
	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	/**
	 * @return the storeService
	 */
	public StoreService getStoreService() {
		return storeService;
	}

	/**
	 * @param productSkuLoadTuner the productSkuLoadTuner to set
	 */
	public void setProductSkuLoadTuner(final FetchGroupLoadTuner productSkuLoadTuner) {
		this.productSkuLoadTuner = productSkuLoadTuner;
	}

	/**
	 * @return the productSkuLoadTuner
	 */
	public FetchGroupLoadTuner getProductSkuLoadTuner() {
		return productSkuLoadTuner;
	}

	/**
	 * @param indexUtility the indexUtility to set
	 */
	public void setIndexUtility(final IndexUtility indexUtility) {
		this.indexUtility = indexUtility;
	}

	/**
	 * @return the indexUtility
	 */
	public IndexUtility getIndexUtility() {
		return indexUtility;
	}

}
