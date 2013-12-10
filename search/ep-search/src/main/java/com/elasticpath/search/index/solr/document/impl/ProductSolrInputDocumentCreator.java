package com.elasticpath.search.index.solr.document.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;

import com.elasticpath.common.pricing.service.PromotedPriceLookupService;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.AttributeValueWithType;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalogview.IndexProduct;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactory;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactoryBuilder;
import com.elasticpath.service.search.index.Analyzer;
import com.elasticpath.service.search.solr.IndexUtility;
import com.elasticpath.service.search.solr.SolrIndexConstants;
import com.elasticpath.service.store.StoreService;

/**
 * Creates a {@link SolrInputDocument} for an {@link IndexProduct}.
 */
@SuppressWarnings({ "PMD.TooManyMethods" })
public class ProductSolrInputDocumentCreator extends AbstractDocumentCreatingTask<IndexProduct> {

	private static final int DEFAULT_PRICE_LOOKUP_DELAY_BETWEEN_RETRIES = 654;

	private static final int MAXIMUM_PRICE_LOOKUP_RETRIES = 8;

	private int priceLookupDelayBetweenRetries = DEFAULT_PRICE_LOOKUP_DELAY_BETWEEN_RETRIES;

	private int maximumPriceLookupRetries = MAXIMUM_PRICE_LOOKUP_RETRIES;

	private static final Logger LOG = Logger.getLogger(ProductSolrInputDocumentCreator.class);

	/** A value that boost the value. */
	protected static final int FEATURED_RANK_BOOST = 1000;

	private SolrInputDocument solrInputDocument;

	private Analyzer analyzer;

	private FetchGroupLoadTuner productLoadTuner;

	private FetchGroupLoadTuner parentCategoryLoadTuner;

	private StoreService storeService;

	private PriceListAssignmentService priceListAssignmentService;

	private IndexUtility indexUtility;

	private PromotedPriceLookupService promotedPriceLookupService;

	private CategoryService categoryService;

	private BrandService brandService;

	private BeanFactory beanFactory;

	private Collection<Store> stores;

	private final Map<Store, Collection<PriceListAssignment>> priceListAssignmentsByStore = new HashMap<Store, Collection<PriceListAssignment>>();

	/**
	 * Creates a {@link SolrInputDocument} from the attached {@link IndexProduct}.
	 *
	 * @return a {@link SolrInputDocument}.
	 */
	@Override
	public SolrInputDocument createDocument() {
		initialize();
		addProductFieldsToDocument(solrInputDocument, getEntity());
		final Map<Long, Boolean> catalogUidAvailability = addAvailableCategoriesAndCatalogsToDocument(solrInputDocument, getEntity());
		addBrandCodeToDocument(solrInputDocument, getEntity());
		addSkuCodesToDocument(solrInputDocument, getEntity());
		addProductDisplayCodeToDocument(solrInputDocument, getEntity());
		addFeaturenessToDocument(solrInputDocument, getEntity());
		addDisplayableFieldsToDocument(solrInputDocument, getEntity(), catalogUidAvailability, stores);
		addPriceFieldsToDocument(solrInputDocument, getEntity(), stores);
		addStoreSpecificFieldsToDocument(solrInputDocument, getEntity(), catalogUidAvailability);
		addLocaleSpecificFieldsToDocument(solrInputDocument, getEntity());
		addDefaultLocalizedFieldsToDocument(solrInputDocument, getEntity(), getProductBrand(getEntity()));
		addSortedFieldsToDocument(solrInputDocument, getEntity());

		if (getEntity().getWrappedProduct() instanceof ProductBundle) {
			final ArrayList<ConstituentItem> count = new ArrayList<ConstituentItem>();
			addConstituentFieldsToDocument(solrInputDocument, (ProductBundle) getEntity().getWrappedProduct(), count, false);
			addFieldToDocument(solrInputDocument, SolrIndexConstants.CONSTITUENT_COUNT, getAnalyzer().analyze(count.size()));
		}

		return solrInputDocument;
	}

	private void addSortedFieldsToDocument(final SolrInputDocument solrInputDocument, final Product product) {
		Brand brand = getProductBrand(product);
		for (Locale locale : getAllLocales(product)) {
			addFieldNotMultiValuedToDocument(solrInputDocument,
					getIndexUtility().createLocaleFieldName(SolrIndexConstants.SORT_PRODUCT_NAME, locale),
					getAnalyzer().analyze(product.getDisplayName(locale)));

			if (brand != null) {
				addFieldNotMultiValuedToDocument(solrInputDocument,
						getIndexUtility().createLocaleFieldName(SolrIndexConstants.SORT_BRAND_NAME, locale),
						getAnalyzer().analyze(brand.getDisplayName(locale, true)));
			}

			addFieldNotMultiValuedToDocument(solrInputDocument,
					getIndexUtility().createLocaleFieldName(SolrIndexConstants.SORT_PRODUCT_DEFAULT_CATEGORY_NAME, locale), getAnalyzer()
							.analyze(product.getDefaultCategory(product.getMasterCatalog()).getDisplayName(locale)));
		}

		Locale defaultLocale = product.getMasterCatalog().getDefaultLocale();
		if (brand != null) {
			addFieldNotMultiValuedToDocument(solrInputDocument, SolrIndexConstants.SORT_BRAND_NAME,
					getAnalyzer().analyze(brand.getDisplayName(defaultLocale, true)));
		}
		addFieldNotMultiValuedToDocument(solrInputDocument, SolrIndexConstants.SORT_PRODUCT_DEFAULT_CATEGORY_NAME,
				getAnalyzer().analyze(product.getDefaultCategory(product.getMasterCatalog()).getDisplayName(defaultLocale)));
	}

	/**
	 * Adds the given product's primary fields to the given solr search index document. <br>
	 * This implementation adds: Uid, StartDate, EndDate, ProductCode, SalesCount.
	 *
	 * @param solrInputDocument the solr search index document
	 * @param product the product being indexed
	 */
	protected void addProductFieldsToDocument(final SolrInputDocument solrInputDocument, final Product product) {
		addFieldToDocument(solrInputDocument, SolrIndexConstants.OBJECT_UID, String.valueOf(product.getUidPk()));
		addFieldToDocument(solrInputDocument, SolrIndexConstants.START_DATE, getAnalyzer().analyze(product.getStartDate()));
		addFieldToDocument(solrInputDocument, SolrIndexConstants.END_DATE, getAnalyzer().analyze(product.getEndDate()));
		addFieldToDocument(solrInputDocument, SolrIndexConstants.LAST_MODIFIED_DATE, getAnalyzer().analyze(product.getLastModifiedDate()));
		addFieldToDocument(solrInputDocument, SolrIndexConstants.PRODUCT_CODE, getAnalyzer().analyze(product.getCode()));
		addFieldToDocument(solrInputDocument, SolrIndexConstants.SALES_COUNT, getAnalyzer().analyze(product.getSalesCount()));
		addFieldToDocument(solrInputDocument, SolrIndexConstants.PRODUCT_TYPE_NAME, getAnalyzer().analyze(product.getProductType().getName()));

		LOG.trace("Finished adding basic fields");
	}

	/**
	 * <p>
	 * Adds the following fields to a solr search document:<br>
	 * <ul>
	 * <li>Codes of the given product's categories.</li>
	 * <li>UIDs of catalogs in which the given product exists</li>
	 * <li>UIDs of all ancestors of the given product's categories.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Note that if a given category or any of its ancestors are not "available", the category Code and its ancestor's UIDs will still be added to
	 * the document, but its containing catalog's UID will not be in the collection of available catalog UIDs.
	 * </p>
	 *
	 * @param solrInputDocument the solr search document to which the fields must be added
	 * @param product the product being indexed
	 * @return a map of UIDs of catalogs in which this product exists, and whether the product is available in the catalog represented by the UID
	 */
	protected Map<Long, Boolean> addAvailableCategoriesAndCatalogsToDocument(final SolrInputDocument solrInputDocument, final Product product) {
		// get category information
		final Set<String> parentCategoryCodes = new HashSet<String>();

		final Map<Long, Boolean> catalogUidAvailability = new HashMap<Long, Boolean>();
		final Set<String> catalogCodes = new HashSet<String>();
		// a map of catalog codes -> category codes
		final Map<String, Collection<String>> categoryCodesMap = new HashMap<String, Collection<String>>();
		final Set<String> allCategoryCodes = new HashSet<String>();
		final Map<String, Collection<String>> masterCategoryCodesMap = new HashMap<String, Collection<String>>();
		for (final Category cat : product.getCategories()) {
			final Category category = getCategoryService().load(cat.getUidPk(), getParentCategoryLoadTuner());
			final long catalogUid = category.getCatalog().getUidPk();
			final String catalogCode = category.getCatalog().getCode();
			final String categoryCode = category.getCode();

			buildCatalogToCategoriesMap(catalogCode, categoryCode, categoryCodesMap);
			allCategoryCodes.add(categoryCode);

			if (!category.isLinked()) {
				buildCatalogToCategoriesMap(catalogCode, categoryCode, masterCategoryCodesMap);
			}

			// 1: if this category or any of this category's parent categories are not available, it's not available
			boolean available = true;
			available &= category.isAvailable();
			available &= checkAvailableAndPopulateParents(category.getParent(), parentCategoryCodes, available);

			// 2: As long as one route to the root is available, we're available
			Boolean mapAvailability = catalogUidAvailability.get(catalogUid);
			if (mapAvailability == null) {
				mapAvailability = false;
			}
			catalogUidAvailability.put(catalogUid, mapAvailability | available);
			catalogCodes.add(catalogCode);
		}
		if (categoryCodesMap.isEmpty()) {
			throw new IllegalStateException("Required field missing: " + SolrIndexConstants.PRODUCT_CATEGORY);
		}
		LOG.trace("Finished calculating parent category UID information");
		addFieldToDocument(solrInputDocument, SolrIndexConstants.CATALOG_CODE, catalogCodes);

		/*
		 * The 'categoryCode' field is only used for compatibility with advanced search where there's a need to search by category code regardless
		 * the catalog. For catalog related search the dynamic field productCategory_<Store_Code> should be used. In a later version of Solr there is
		 * a way to create a query combining all dynamic fields starting with common prefix by using the following syntax <field_name>*. This is not
		 * possible using the current Solr implementation though.
		 */
		addFieldToDocument(solrInputDocument, SolrIndexConstants.CATEGORY_CODE, allCategoryCodes);

		addCategoryCodesToDocument(solrInputDocument, SolrIndexConstants.PRODUCT_CATEGORY, categoryCodesMap);
		addFieldToDocument(solrInputDocument, SolrIndexConstants.PARENT_CATEGORY_CODES, parentCategoryCodes);
		addCategoryCodesToDocument(solrInputDocument, SolrIndexConstants.MASTER_PRODUCT_CATEGORY, masterCategoryCodesMap);
		return catalogUidAvailability;
	}

	/**
	 * Adds a product's brand code to the given solr input document. <br>
	 * If the product has a null brand, the field is not populated.
	 *
	 * @param solrInputDocument the solr input document
	 * @param product the product being indexed
	 * @return the brandCode that was added to the document, or null if there was none.
	 */
	protected String addBrandCodeToDocument(final SolrInputDocument solrInputDocument, final Product product) {
		String brandCode = null;
		if (product.getBrand() != null) {
			brandCode = product.getBrand().getCode();
			addFieldToDocument(solrInputDocument, SolrIndexConstants.BRAND_CODE, getAnalyzer().analyze(brandCode));
		}
		LOG.trace("BrandCode added");
		return brandCode;
	}

	/**
	 * Adds a product's skuCodes to the given solr input document. <br>
	 * If the product's skus collection is null, the field is not populated.
	 *
	 * @param solrInputDocument the solr input document
	 * @param product the product being indexed
	 * @return the collection of sku codes that were added to the document, or an empty collection if there were none.
	 */
	protected Collection<String> addSkuCodesToDocument(final SolrInputDocument solrInputDocument, final Product product) {
		final Collection<String> productSkus = new LinkedList<String>();
		if (product.getProductSkus() != null) {
			for (final ProductSku productSku : product.getProductSkus().values()) {
				productSkus.add(productSku.getSkuCode());
			}
			addFieldToDocument(solrInputDocument, SolrIndexConstants.PRODUCT_SKU_CODE, productSkus);
		}
		LOG.trace("Product SKUs added");
		return productSkus;
	}

	/**
	 * Adds sku code in case of single sku product or product code in case of multi-sku product to the given solr index docuemnt.<br>
	 * The field type is code and is supposed to be used in product sorting by sku name.
	 *
	 * @param solrInputDocument the solr search index document
	 * @param product the product being indexed
	 */
	protected void addProductDisplayCodeToDocument(final SolrInputDocument solrInputDocument, final IndexProduct product) {
		final boolean hasMultiSku = product.hasMultipleSkus();
		final String productDisplayName;
		if (hasMultiSku) {
			productDisplayName = product.getCode();
		} else {
			productDisplayName = product.getDefaultSku().getSkuCode();
		}
		addFieldToDocument(solrInputDocument, SolrIndexConstants.PRODUCT_DISPLAY_SKU_CODE_EXACT, getAnalyzer().analyze(productDisplayName));
	}

	/**
	 * Adds a product's 'feature-ness' to the given solr input document. <br>
	 * If a product is featured in any of its categories, the "featured" index field will be "true". <br>
	 * Also, for every category containing the given product a new document field will be created where the field name contains the UID of the
	 * category and the featured rank of the product within that category (regardless of whether the product is featured in that category - it will
	 * be 0 if the product is not featured in that category)
	 *
	 * @param solrInputDocument the solr input document
	 * @param product the product being indexed
	 * @return true if the given product is featured in any categories, false if not
	 */
	protected boolean addFeaturenessToDocument(final SolrInputDocument solrInputDocument, final Product product) {
		boolean featured = false;
		for (final Category cat : product.getCategories()) {
			int featuredRank = product.getFeaturedRank(cat);
			featured |= featuredRank > 0;

			if (featuredRank > 0) {
				featuredRank = calculateFeatureBoost(featuredRank);
			}

			// add specific ordering on a per-category basis
			addFieldToDocument(solrInputDocument, getIndexUtility().createFeaturedField(cat.getUidPk()), getAnalyzer().analyze(featuredRank));
		}
		addFieldToDocument(solrInputDocument, SolrIndexConstants.FEATURED, String.valueOf(featured));
		LOG.trace("Feature-ness added");
		return featured;
	}

	/**
	 * <p>
	 * For each of the stores containing the given product a field containing the store's code is added to the index document specifying whether the
	 * product is displayable in that store.
	 * </p>
	 * <p>
	 * This implementation calls isProductAvailableAndDisplayable(IndexProduct, Store, Map)}
	 * </p>
	 *
	 * @param solrInputDocument the solr input document
	 * @param product the product being indexed
	 * @param catalogUidAvailability map of catalog UIDs to product availability to be used as partial input to determining whether the given product
	 *            is displayable.
	 * @param allStores collection of all known stores
	 */
	protected void addDisplayableFieldsToDocument(final SolrInputDocument solrInputDocument, final IndexProduct product,
			final Map<Long, Boolean> catalogUidAvailability, final Collection<Store> allStores) {
		for (final Store store : allStores) {
			addFieldToDocument(solrInputDocument, getIndexUtility().createDisplayableFieldName(SolrIndexConstants.DISPLAYABLE, store.getCode()),
					String.valueOf((isProductAvailableAndDisplayable(product, store, catalogUidAvailability))));
		}
	}

	/**
	 * <p>
	 * For each of the stores containing the given product, the product's lowest price in each of the product price's currencies is added to the
	 * index using an index field name composed partially of the store's code.
	 * </p>
	 *
	 * @param doc the solr input document
	 * @param product the product being indexed
	 * @param stores collection of all known stores
	 */
	protected void addPriceFieldsToDocument(final SolrInputDocument doc, final IndexProduct product, final Collection<Store> stores) {
		final Map<String, Price> cachedPricefieldPriceMap = new HashMap<String, Price>();
		final BaseAmountDataSourceFactory dataSourceFactory = initDataSourceFactory(product, stores);
		for (final Store store : stores) {
			final Collection<PriceListAssignment> assignments = getPriceListAssignmentByStore(store);
			cachedPricefieldPriceMap.putAll(createPricefieldPriceMap(assignments, product, store, dataSourceFactory));
		}

		for (final Entry<String, Price> priceEntry : cachedPricefieldPriceMap.entrySet()) {
			addFieldToDocument(doc, priceEntry.getKey(), getStringValueOf(priceEntry.getValue()));
		}

		LOG.trace("Added store specific fields");
	}

	/**
	 * Adds Store related fields to the document: StoreCode.
	 *
	 * @param solrInputDocument the Solr input document
	 * @param product the product being indexed
	 * @param catalogUidAvailability maps UIDs of catalogs containing product with its availability inside of them
	 */
	protected void addStoreSpecificFieldsToDocument(final SolrInputDocument solrInputDocument, final IndexProduct product,
			final Map<Long, Boolean> catalogUidAvailability) {
		final Collection<Store> containingStores = storeService.findStoresWithCatalogUids(catalogUidAvailability.keySet());
		for (final Store store : containingStores) {
			addFieldToDocument(solrInputDocument, SolrIndexConstants.STORE_CODE, store.getCode());
		}
	}

	/**
	 * Gets all the {@link Locale}s for a given {@link Product}. {@link Product}s can have different locales depending
	 * on the {@link Catalog} they belong to.
	 * 
	 * @param product {@link Product} to get {@link Locale}s for
	 * @return all {@link Locale}s for a given {@link Product}
	 */
	private static Set<Locale> getAllLocales(final Product product) {
		// LDF map may not have all locales defined, use catalog
		final Set<Locale> allLocales = new HashSet<Locale>();
		for (final Category category : product.getCategories()) {
			allLocales.addAll(category.getCatalog().getSupportedLocales());
		}
		return allLocales;
	}

	/**
	 * Add locale-specific product fields from the given product to the given solr input document.
	 *
	 * @param solrInputDocument the solr input document
	 * @param product the product being indexed
	 */
	protected void addLocaleSpecificFieldsToDocument(final SolrInputDocument solrInputDocument, final Product product) {
		final Brand brand = getProductBrand(product);
		
		for (final Locale locale : getAllLocales(product)) {
			addFieldToDocument(solrInputDocument, getIndexUtility().createLocaleFieldName(SolrIndexConstants.PRODUCT_NAME, locale), getAnalyzer()
					.analyze(product.getDisplayName(locale)));

			if (brand != null) {
				addFieldToDocument(solrInputDocument, getIndexUtility().createLocaleFieldName(SolrIndexConstants.BRAND_NAME, locale), getAnalyzer()
						.analyze(brand.getDisplayName(locale, true)));
			}

			for (final Category category : product.getCategories()) {
				addFieldToDocument(solrInputDocument, getIndexUtility().createLocaleFieldName(SolrIndexConstants.CATEGORY_NAME, locale),
						getAnalyzer().analyze(category.getDisplayName(locale)));
			}

			for (final AttributeValue attributeValue : product.getAttributeValues(locale)) {
				addAttributeToDocument(solrInputDocument, attributeValue, locale);
			}

			for (final ProductSku productSku : product.getProductSkus().values()) {
				if (productSku.getAttributeValueMap() != null) {
					for (final AttributeValue attributeValue : productSku.getAttributeValueMap().values()) {
						addAttributeToDocument(solrInputDocument, attributeValue, locale);
					}
				}
			}

			addFieldToDocument(solrInputDocument, getIndexUtility().createLocaleFieldName(SolrIndexConstants.PRODUCT_DEFAULT_CATEGORY_NAME, locale),
					getAnalyzer().analyze(product.getDefaultCategory(product.getMasterCatalog()).getDisplayName(locale)));
		}
	}

	/**
	 * @param document SolrDocument
	 * @param product the product
	 */
	protected void addConsituentFieldsToDocumentHelper(final SolrInputDocument document, final Product product) {
		// Constituent specific document fields, these fields must be multi-valued
		addBrandCodeToDocument(document, product);
		addDefaultLocalizedMultiValueFieldsToDocument(document, product, getProductBrand(product));
		addSkuCodesToDocument(document, product);
		addLocaleSpecificFieldsToDocument(document, product);
	}

	/**
	 * @param document SolrDocument
	 * @param constituent bundle constituent item
	 */
	protected void addConsituentFieldsToDocumentHelper(final SolrInputDocument document, final ConstituentItem constituent) {
		// Constituent specific document fields, these fields must be multi-valued
		final Product product = constituent.getProduct();
		if (constituent.isProduct()) {
			addConsituentFieldsToDocumentHelper(document, product);
		} else if (constituent.isProductSku()) {
			addFieldToDocument(document, SolrIndexConstants.PRODUCT_SKU_CODE, constituent.getCode());
			addBrandCodeToDocument(document, product);
			addDefaultLocalizedMultiValueFieldsToDocument(document, product, getProductBrand(product));
			addLocaleSpecificFieldsToDocument(document, product);
		}
	}

	/**
	 * @param solrInputDocument SolrDocument
	 * @param bundle Product bundle
	 * @param count the count of constituents
	 * @param bundleIsConstituent this bundle is a nested bundle
	 */
	protected void addConstituentFieldsToDocument(final SolrInputDocument solrInputDocument, final ProductBundle bundle,
			final List<ConstituentItem> count, final boolean bundleIsConstituent) {
		if (bundleIsConstituent) {
			addConsituentFieldsToDocumentHelper(solrInputDocument, bundle);
		}
		for (final BundleConstituent bundleConstituent : bundle.getConstituents()) {
			final ConstituentItem constituent = bundleConstituent.getConstituent();
			addConsituentFieldsToDocumentHelper(solrInputDocument, constituent);
			count.add(constituent);
			// recurse into nested bundle
			if (constituent.isBundle()) {
				addConstituentFieldsToDocument(solrInputDocument, (ProductBundle) constituent.getProduct(), count, true);
			}
		}
	}

	/**
	 * Add the value for the field to the solr document. If the fields already has a value, than the new value gets concatenated to the existing
	 * value, this way creating a single value field, used for sorting.
	 *
	 * @param document - the document to which to add
	 * @param fieldName - the name of the field
	 * @param value - the value to add
	 * @return whether the operation was successful (was not aborted)
	 */
	@Override
	protected boolean addFieldNotMultiValuedToDocument(final SolrInputDocument document, final String fieldName, final String value) {
		String valueToAdd = value;
		if (document.containsKey(fieldName) && value != null && value.length() > 0) {
			final Object existingFieldValue = document.getFieldValue(fieldName);
			valueToAdd = value.concat(existingFieldValue.toString());
			document.removeField(fieldName);
		}

		return addFieldToDocument(document, fieldName, valueToAdd);
	}

	/**
	 * Adds a product attribute to the SOLR document.
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
	 * Looks up the price for the given product in the given price list.
	 *
	 * @param product the product
	 * @param priceListDescriptor the descriptor
	 * @param store store
	 * @param baseAmountDataSourceFactory the base amount data source factory to be used to look up the prices
	 * @return an instance of price
	 */
	protected Price lookupPrice(final Product product, final PriceListDescriptor priceListDescriptor, final Store store,
			final BaseAmountDataSourceFactory baseAmountDataSourceFactory) {
		final PriceListStack singleStack = getBeanFactory().getBean(ContextIdNames.PRICE_LIST_STACK);

		singleStack.addPriceList(priceListDescriptor.getGuid());
		singleStack.setCurrency(Currency.getInstance(priceListDescriptor.getCurrencyCode()));

		// There is an existing bug in the Drools version we are currently using (5.0.1)
		// See: https://issues.jboss.org/browse/JBRULES-2418
		// It generates ConcurrentModificationExceptions in the multithreaded Search Index building pipeline (JP-604)
		// This bug appears to have been fixed in version 5.1.0.
		// When we upgrade to version 5.1.0, the following exception handling code should no longer be necessary and should be removed.
		Price priceFromLookup = null;
		int retryCount = 0;

		Exception thrownException = null;

		while (retryCount < maximumPriceLookupRetries) {
			thrownException = null;

			try {
				priceFromLookup = getPromotedPriceLookupService().getProductPrice(product, singleStack, store, new HashSet<Long>(),
						baseAmountDataSourceFactory);
			} catch (final ConcurrentModificationException concurrentModificationException) {
				LOG.info("Working around ConcurrentModificationException likely due to JBRULES-2418 while looking up price - Retrying shortly.");
				thrownException = concurrentModificationException;
			}

			if (thrownException == null) {
				break;
			}
			sleepBetweenRetries();
			++retryCount;
		}

		if (thrownException != null) {
			LOG.error("Could not recover from repeated ConcurrentModificationExceptions due to JBRULES-2418.  Returning null for priceLookup("
					+ product + ").", thrownException);
		}
		return priceFromLookup;
	}

	private void sleepBetweenRetries() {
		try {
			Thread.sleep(priceLookupDelayBetweenRetries);
		} catch (final InterruptedException interruptedException) {
			// nothing should be interrupting document creation.
			LOG.debug("Product price lookup retry interrupted", interruptedException.getCause());
		}
	}

	/**
	 * Gets the price field name from {@link Catalog} and {@link PriceListDescriptor}.
	 *
	 * @param catalog {@link Catalog}.
	 * @param priceListDescriptor {@link PriceListDescriptor}.
	 * @return the price field name.
	 */
	protected String getPriceFieldName(final Catalog catalog, final PriceListDescriptor priceListDescriptor) {
		return getIndexUtility().createPriceFieldName(SolrIndexConstants.PRICE, catalog.getCode(), priceListDescriptor.getGuid());
	}

	/**
	 * Create price field and price from {@link Product} and {@link PriceListDescriptor}, then caches them in a map.
	 *
	 * @param pricefieldPriceMap the price field price map.
	 * @param product {@link Product}.
	 * @param priceListDescriptor {@link PriceListDescriptor}.
	 * @param dataSourceFactory the base amount data factory to be used to look up the prices
	 * @param store {@link Store}.
	 */
	protected void mapPricefieldWithPrice(final Map<String, Price> pricefieldPriceMap, final Product product,
			final PriceListDescriptor priceListDescriptor, final Store store, final BaseAmountDataSourceFactory dataSourceFactory) {
		final String fieldName = getPriceFieldName(store.getCatalog(), priceListDescriptor);
		final Price lowestProductPrice = lookupPrice(product, priceListDescriptor, store, dataSourceFactory);

		if ((lowestProductPrice != null) && (!pricefieldPriceMap.containsKey(fieldName))) {
			pricefieldPriceMap.put(fieldName, lowestProductPrice);
		}
	}

	/**
	 * Creates a map that map price field against {@link Price}.
	 *
	 * @param assignments a list of {@link PriceListAssignment}.
	 * @param product {@link Product}.
	 * @param store {@link Store}.
	 * @param dataSourceFactory the base amount data factory to be used to look up the prices
	 * @return a map with price field string as key and {@link Price} as value.
	 */
	protected Map<String, Price> createPricefieldPriceMap(final Collection<PriceListAssignment> assignments, final Product product,
			final Store store, final BaseAmountDataSourceFactory dataSourceFactory) {
		final Map<String, Price> pricefieldPriceMap = new HashMap<String, Price>();

		for (final PriceListAssignment assignment : assignments) {
			mapPricefieldWithPrice(pricefieldPriceMap, product, assignment.getPriceListDescriptor(), store, dataSourceFactory);
		}

		return pricefieldPriceMap;
	}

	/**
	 * Initializes a {@link BaseAmountDataSourceFactory} so that it can query the prices more efficiently.
	 *
	 * @param product the product for which we need the prices
	 * @param stores the stores in which we need the prices
	 * @return the data-set factory
	 */
	protected BaseAmountDataSourceFactory initDataSourceFactory(final Product product, final Collection<Store> stores) {
		final BaseAmountDataSourceFactoryBuilder builder = getBeanFactory().getBean(ContextIdNames.BASE_AMOUNT_DATA_SOURCE_FACTORY_BUILDER);
		builder.products(product);
		for (final Store store : stores) {
			builder.priceListAssignments(getPriceListAssignmentByStore(store));
		}
		return builder.build();
	}

	/**
	 * Determines whether the given product is both Available in all of its catalogs and Displayable in the given Store.
	 *
	 * @param product the product being indexed
	 * @param store the store in which the product is being indexed
	 * @param catalogUidAvailability map of catalog UIDs to Availability flags
	 * @return true if the product is Displayable in the given store and Available in all catalogs containing the product
	 */
	protected boolean
			isProductAvailableAndDisplayable(final IndexProduct product, final Store store, final Map<Long, Boolean> catalogUidAvailability) {
		boolean displayable = product.isDisplayable(store.getCode());

		// not sold separately products should not show up in search results, but they can be displayed
		displayable &= !product.isNotSoldSeparately();

		if (catalogUidAvailability.get(store.getCatalog().getUidPk()) != null) {
			// we should always get here -- only display if a product is both displayable and
			// its category is displayable
			displayable &= catalogUidAvailability.get(store.getCatalog().getUidPk());

		}
		return displayable;
	}

	/**
	 * This method is returning a boost value for featured products. <br>
	 * The value is calculated by looking at the rank. <br>
	 * Higher the rank has smaller the boost value. <br>
	 * Division needed to reverse the order, multiplication is for increasing the score of the document.
	 *
	 * @param featuredRank the rank
	 * @return the boost value
	 */
	protected int calculateFeatureBoost(final int featuredRank) {
		return (int) ((1f / featuredRank) * FEATURED_RANK_BOOST);
	}

	/**
	 * Adds a field and value to a <code>SolrInputDocument</code>. Aborts if the value is empty in order to optimize the document.
	 *
	 * @param solrInputDocument the document to add fields to
	 * @param fieldName the field name
	 * @param value the value to add to the field
	 * @return whether the operation was successful (was not aborted)
	 */
	@Override
	protected boolean addFieldToDocument(final SolrInputDocument solrInputDocument, final String fieldName, final String value) {
		if (value == null) {
			return false;
		} else if (value.length() == 0) {
			return false;
		}
		solrInputDocument.addField(fieldName, value);
		return true;
	}

	/**
	 * Adds a field and value to a <code>SolrInputDocument</code>. Aborts if the value is empty in order to optimize the document.
	 *
	 * @param document the document to add fields to
	 * @param fieldName the field name
	 * @param value the value to add to the field
	 * @return whether the operation was successful (was not aborted)
	 */
	@Override
	protected boolean addFieldToDocument(final SolrInputDocument document, final String fieldName, final Collection<?> value) {
		if (value == null) {
			return false;
		} else if (value.isEmpty()) {
			return false;
		}
		document.addField(fieldName, value);
		return true;
	}

	/**
	 * Gets the load tuner to be used when loading a product's Category for indexing purposes.
	 *
	 * @return the load tuner to use when loading categories for the purpose of building the search index
	 */
	protected FetchGroupLoadTuner getParentCategoryLoadTuner() {
		if (parentCategoryLoadTuner == null) {
			parentCategoryLoadTuner = getBeanFactory().getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
			parentCategoryLoadTuner.addFetchGroup(FetchGroupConstants.INIFINITE_PARENT_CATEGORY_DEPTH, // Force loading of ancestor Categories
					FetchGroupConstants.CATEGORY_AVAILABILITY, // load startdate, enddate, hidden
					FetchGroupConstants.CATALOG, // Force loading of Catalog with Category
					FetchGroupConstants.CATEGORY_HASH_MINIMAL, // Force loading of category code
					FetchGroupConstants.PROMOTION_INDEX); // force loading of CatalogCode with Catalog
		}
		return parentCategoryLoadTuner;
	}

	private void initialize() {
		solrInputDocument = new SolrInputDocument();
		productLoadTuner.addFetchGroup(FetchGroupConstants.ORDER_DEFAULT);
		populateStoreAndPriceListAssignmentCache();
	}

	private void populateStoreAndPriceListAssignmentCache() {
		stores = getStoreService().findAllCompleteStores(getProductLoadTuner());
		priceListAssignmentsByStore.clear();
		for (final Store store : stores) {
			priceListAssignmentsByStore.put(store, getPriceListAssignmentService().listByCatalog(store.getCatalog(), true));
		}
	}

	private Brand getProductBrand(final Product product) {
		Brand brand = null;
		if (product.getBrand() != null) {
			brand = getBrandService().findByCode(product.getBrand().getCode());
		}
		return brand;
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

	/**
	 * Adds category codes to the solr document in the following pattern.
	 * <p>
	 * productCategory_[catalog_code] -> [[category_code], [category_code], ...]
	 * </p>
	 *
	 * @param document the solr document
	 * @param categoryCodesMap a map of catalog codes and category codes belonging to them
	 */
	private void addCategoryCodesToDocument(final SolrInputDocument document, final String categoryFieldName,
			final Map<String, Collection<String>> categoryCodesMap) {

		for (final String catalogCode : categoryCodesMap.keySet()) {
			addFieldToDocument(document, getIndexUtility().createProductCategoryFieldName(categoryFieldName, catalogCode), categoryCodesMap
					.get(catalogCode));
		}
	}

	/**
	 * Ignore locale suffix to provide correct sorting of the Unicode strings for all default localized fields.
	 *
	 * @param document SolrInputDocument
	 * @param product product being indexed
	 * @param brand brand, can be null
	 */
	private void addDefaultLocalizedFieldsToDocument(final SolrInputDocument document, final Product product, final Brand brand) {
		final Locale defaultLocale = product.getMasterCatalog().getDefaultLocale();
		
		addDefaultLocalizedMultiValueFieldsToDocument(document, product, brand);

		addFieldNotMultiValuedToDocument(document, SolrIndexConstants.PRODUCT_NAME_NON_LC, getAnalyzer().analyze(
				product.getDisplayName(defaultLocale)));
	}
	/**
	 * Ignore locale suffix to provide correct sorting of the Unicode strings for multi value fields.
	 *
	 * @param document SolrInputDocument
	 * @param product product being indexed
	 * @param brand brand, can be null
	 */
	private void addDefaultLocalizedMultiValueFieldsToDocument(final SolrInputDocument document, final Product product, final Brand brand) {
		final Locale defaultLocale = product.getMasterCatalog().getDefaultLocale();
		if (brand != null) {
			addFieldToDocument(document, SolrIndexConstants.BRAND_NAME, getAnalyzer().analyze(brand.getDisplayName(defaultLocale, true)));
		}

		addFieldToDocument(document, SolrIndexConstants.PRODUCT_DEFAULT_CATEGORY_NAME, getAnalyzer().analyze(
				product.getDefaultCategory(product.getMasterCatalog()).getDisplayName(defaultLocale)));
	}
	

	private String getStringValueOf(final Price lowestProductPrice) {
		final Money lowestPrice = lowestProductPrice.getPricingScheme().getLowestPrice();
		final BigDecimal amount = lowestPrice.getAmount();
		return amount.toPlainString();
	}

	private Collection<PriceListAssignment> getPriceListAssignmentByStore(final Store store) {
		return priceListAssignmentsByStore.get(store);
	}

	/**
	 * @param catalogCode the catalog code to add to the map
	 * @param categoryCode the category code to add to the map
	 * @param categoryCodesMap a map between catalog codes to category codes
	 */
	private void buildCatalogToCategoriesMap(final String catalogCode, final String categoryCode,
			final Map<String, Collection<String>> categoryCodesMap) {
		if (categoryCodesMap.containsKey(catalogCode)) {
			categoryCodesMap.get(catalogCode).add(categoryCode);
		} else {
			categoryCodesMap.put(catalogCode, new HashSet<String>(Arrays.asList(categoryCode)));
		}
	}

	/**
	 * @param analyzer the analyzer to set
	 */
	@Override
	public void setAnalyzer(final Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	/**
	 * @return the analyzer
	 */
	@Override
	public Analyzer getAnalyzer() {
		return analyzer;
	}

	/**
	 * @param productLoadTuner the productLoadTuner to set
	 */
	public void setProductLoadTuner(final FetchGroupLoadTuner productLoadTuner) {
		this.productLoadTuner = productLoadTuner;
	}

	/**
	 * @return the productLoadTuner
	 */
	public FetchGroupLoadTuner getProductLoadTuner() {
		return productLoadTuner;
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
	 * @param priceListAssignmentService the priceListAssignmentService to set
	 */
	public void setPriceListAssignmentService(final PriceListAssignmentService priceListAssignmentService) {
		this.priceListAssignmentService = priceListAssignmentService;
	}

	/**
	 * @return the priceListAssignmentService
	 */
	public PriceListAssignmentService getPriceListAssignmentService() {
		return priceListAssignmentService;
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

	/**
	 * @param promotedPriceLookupService the promotedPriceLookupService to set
	 */
	public void setPromotedPriceLookupService(final PromotedPriceLookupService promotedPriceLookupService) {
		this.promotedPriceLookupService = promotedPriceLookupService;
	}

	/**
	 * @return the promotedPriceLookupService
	 */
	public PromotedPriceLookupService getPromotedPriceLookupService() {
		return promotedPriceLookupService;
	}

	/**
	 * @param categoryService the categoryService to set
	 */
	public void setCategoryService(final CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	/**
	 * @return the categoryService
	 */
	public CategoryService getCategoryService() {
		return categoryService;
	}

	/**
	 * @param brandService the brandService to set
	 */
	public void setBrandService(final BrandService brandService) {
		this.brandService = brandService;
	}

	/**
	 * @return the brandService
	 */
	public BrandService getBrandService() {
		return brandService;
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
	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setPriceLookupDelayBetweenRetries(final int priceLookupDelayBetweenRetries) {
		this.priceLookupDelayBetweenRetries = priceLookupDelayBetweenRetries;
	}

	public void setMaximumPriceLookupRetries(final int maximumPriceLookupRetries) {
		this.maximumPriceLookupRetries = maximumPriceLookupRetries;
	}

}
