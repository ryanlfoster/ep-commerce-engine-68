package com.elasticpath.search.index.solr.document.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.pricing.service.PromotedPriceLookupService;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.pricing.PriceListAssignment;
import com.elasticpath.domain.pricing.PriceListDescriptor;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.search.index.solr.builders.impl.ProductIndexBuilder;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalogview.IndexProduct;
import com.elasticpath.service.pricing.PriceListAssignmentService;
import com.elasticpath.service.pricing.datasource.BaseAmountDataSourceFactory;
import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.AnalyzerImpl;
import com.elasticpath.service.search.solr.IndexUtilityImpl;
import com.elasticpath.service.search.solr.SolrIndexConstants;
import com.elasticpath.service.store.StoreService;

/**
 * Test {@link ProductSolrInputDocumentCreator}.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.ExcessiveImports", "PMD.CouplingBetweenObjects" })
public class ProductIndexBuilderTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ProductSolrInputDocumentCreator productDocumentCreator;

	private ProductIndexBuilder productIndexBuilder;

	private IndexUtilityImpl indexUtility;
	private ProductService productService;
	private IndexProduct product;

	private SolrInputDocument document;

	private AnalyzerImpl analyzer;

	/**
	 * Prepares for tests.
	 */
	@Before
	public void setUp() {
		productService = context.mock(ProductService.class);
		product = context.mock(IndexProduct.class);
		document = new SolrInputDocument();

		this.productIndexBuilder = new ProductIndexBuilder();
		this.productDocumentCreator = new ProductSolrInputDocumentCreator();
		this.analyzer = new AnalyzerImpl();
		this.productDocumentCreator.setAnalyzer(analyzer);

		productIndexBuilder.setProductService(productService);

		indexUtility = new IndexUtilityImpl() {
			@Override
			public String createFeaturedField(final long categoryUid) {
				return SolrIndexConstants.FEATURED_FIELD + categoryUid + "_" + categoryUid;
			}
		};
		productDocumentCreator.setIndexUtility(indexUtility);
	}

	/**
	 * Test that getName() returns the correct name.
	 */
	@Test
	public void testGetName() {
		assertEquals(SolrIndexConstants.PRODUCT_SOLR_CORE, productIndexBuilder.getName());
	}

	/**
	 * Test that findDeletedUids() calls {@link ProductService#findUidsByDeletedDate(Date)} with the given date.
	 */
	@Test
	public void testFindDeletedUids() {
		final Date date = new Date();
		context.checking(new Expectations() {
			{
				oneOf(productService).findUidsByDeletedDate(date);
				will(returnValue(Collections.emptyList()));
			}
		});
		assertEquals(Collections.emptyList(), this.productIndexBuilder.findDeletedUids(date));
	}

	/**
	 * Test that findAddedOrModifiedUids() calls {@link ProductService#findUidsByModifiedDate(Date)} to find directly modified/added products, and
	 * {@link ProductService#findUidsByCategoryUids(java.util.Collection)} to find products that MAY have been modified by modifying the category in
	 * which they're contained, and returns the union of the two lists.
	 */
	@Test
	public void testFindAddedOrModifiedUids() {
		final List<Long> directlyModifiedProductUids = Arrays.asList(1L, 2L);
		final List<Long> indirectlyModifiedProductUids = Arrays.asList(3L);
		final List<Long> directlyModifiedCategoryUids = Arrays.asList(4L, 5L);
		final List<Long> indirectlyModifiedCategoryUids = Arrays.asList(6L);
		final Collection<Long> totalCategoryUids = new HashSet<Long>(Arrays.asList(4L, 5L, 6L));

		final List<Long> bundleUids = Collections.emptyList();

		// setup the mock product service to return the temp lists
		context.checking(new Expectations() {
			{
				oneOf(productService).findUidsByModifiedDate(with(any(Date.class)));
				will(returnValue(directlyModifiedProductUids));

				oneOf(productService).findUidsByCategoryUids(with(totalCategoryUids));
				will(returnValue(indirectlyModifiedProductUids));

				oneOf(productService).findBundleUids(with(directlyModifiedProductUids));
				will(returnValue(bundleUids));

				CategoryService categoryService = context.mock(CategoryService.class);
				oneOf(categoryService).findUidsByModifiedDate(with(any(Date.class)));
				will(returnValue(directlyModifiedCategoryUids));
				oneOf(categoryService).findDescendantCategoryUids(with(directlyModifiedCategoryUids));
				will(returnValue(indirectlyModifiedCategoryUids));
				productIndexBuilder.setCategoryService(categoryService);
			}
		});

		// create expected return object
		List<Long> allUids = new ArrayList<Long>();
		allUids.addAll(directlyModifiedProductUids);
		allUids.addAll(indirectlyModifiedProductUids);
		// test
		assertEquals(allUids, this.productIndexBuilder.findAddedOrModifiedUids(new Date()));
	}

	/**
	 * Test that findAllUids() returns {@link ProductService#findAllUids()}.
	 */
	@Test
	public void testFindAllUids() {
		final ArrayList<Long> uidList = new ArrayList<Long>();
		context.checking(new Expectations() {
			{
				oneOf(productService).findAllUids();
				will(returnValue(uidList));
			}
		});
		assertSame(uidList, this.productIndexBuilder.findAllUids());
	}

	/**
	 * Test that addProductFieldsToDocument adds the product's UID, startDate, endDate, productCode, and salesCount.
	 */
	@Test
	public void testAddProductFieldsToDocument() {
		final long uid = 5L;
		final Date startDate = new Date();
		final Date endDate = new Date();
		final String productCode = "MyProductCode";
		final int salesCount = 5;
		final String productTypeName = "p_type";

		context.checking(new Expectations() {
			{
				allowing(product).getUidPk();
				will(returnValue(uid));
				allowing(product).getStartDate();
				will(returnValue(startDate));
				allowing(product).getEndDate();
				will(returnValue(endDate));
				allowing(product).getLastModifiedDate();
				will(returnValue(startDate));
				allowing(product).getCode();
				will(returnValue(productCode));
				allowing(product).getSalesCount();
				will(returnValue(salesCount));

				ProductType productType = context.mock(ProductType.class);
				allowing(productType).getName();
				will(returnValue(productTypeName));

				allowing(product).getProductType();
				will(returnValue(productType));
			}
		});

		this.productDocumentCreator.addProductFieldsToDocument(document, product);

		assertEquals(analyzer.analyze(uid), document.getFieldValue(SolrIndexConstants.OBJECT_UID));
		assertEquals(analyzer.analyze(startDate), document.getFieldValue(SolrIndexConstants.START_DATE));
		assertEquals(analyzer.analyze(endDate), document.getFieldValue(SolrIndexConstants.END_DATE));
		assertEquals(analyzer.analyze(productCode), document.getFieldValue(SolrIndexConstants.PRODUCT_CODE));
		assertEquals(analyzer.analyze(salesCount), document.getFieldValue(SolrIndexConstants.SALES_COUNT));
		assertEquals(analyzer.analyze(productTypeName), document.getFieldValue(SolrIndexConstants.PRODUCT_TYPE_NAME));
	}

	/**
	 * Test that the following fields are added to a product's search index document:
	 * <ul>
	 * <li>UIDs of all catalogs containing the product.</li>
	 * <li>Map of CatalogUIDs to ProductAvailabilityBooleans.</li>
	 * <li>UIDs of all the categories that are ancestors of the categories containing the product.</li>
	 * </ul>
	 */
	@Test
	public void testAddAvailableCategoriesAndCatalogsToDocument() {
		// TODO: Fortification: Create test
	}

	/**
	 * Test that addBrandCodeToDocument adds a product's brand's code to the search document.
	 */
	@Test
	public void testAddBrandCodeToDocument() {
		final String myBrandCode = "myBrandCode";

		context.checking(new Expectations() {
			{
				Brand brand = context.mock(Brand.class);
				allowing(brand).getCode();
				will(returnValue(myBrandCode));
				allowing(product).getBrand();
				will(returnValue(brand));
			}
		});

		this.productDocumentCreator.addBrandCodeToDocument(document, product);
		assertEquals(analyzer.analyze(myBrandCode), document.getFieldValue(SolrIndexConstants.BRAND_CODE));
	}

	/**
	 * Test that addSkuCodesToDocument adds all of a product's productSku's codes to the search document.
	 */
	@Test
	public void testAddSkuCodesToDocument() {
		final String skucode1 = "skucode1";
		final String skucode2 = "skuCode2";

		context.checking(new Expectations() {
			{
				ProductSku productSku1 = context.mock(ProductSku.class, skucode1);
				allowing(productSku1).getSkuCode();
				will(returnValue(skucode1));

				ProductSku productSku2 = context.mock(ProductSku.class, skucode2);
				allowing(productSku2).getSkuCode();
				will(returnValue(skucode2));

				Map<String, ProductSku> productSkusMap = new HashMap<String, ProductSku>();
				productSkusMap.put(skucode1, productSku1);
				productSkusMap.put(skucode2, productSku2);
				allowing(product).getProductSkus();
				will(returnValue(productSkusMap));

			}
		});

		this.productDocumentCreator.addSkuCodesToDocument(document, product);
		assertTrue(document.getFieldValues(SolrIndexConstants.PRODUCT_SKU_CODE).contains(skucode1));
		assertTrue(document.getFieldValues(SolrIndexConstants.PRODUCT_SKU_CODE).contains(skucode2));
	}

	/**
	 * Test that if a product is "featured" in any categories, the document's FEATURED field is true. Also test that regardless of whether a product
	 * is featured, every category in which a product exists results in a new index field keyed on the category UID and containing the product's
	 * featured rank in that category.
	 */
	@Test
	public void testAddFeaturenessToDocumentTrue() {
		final long categoryFeaturedUid = 5L;
		final int categoryFeaturedRank = 2;
		final long categoryNotFeaturedUid = 6L;

		context.checking(new Expectations() {
			{
				Category featuredCategory = context.mock(Category.class, "featuredCategory");
				allowing(featuredCategory).getUidPk();
				will(returnValue(categoryFeaturedUid));
				allowing(product).getFeaturedRank(featuredCategory);
				will(returnValue(categoryFeaturedRank));

				Category nonFeaturedCategory = context.mock(Category.class, "nonFeaturedCategory");
				allowing(nonFeaturedCategory).getUidPk();
				will(returnValue(categoryNotFeaturedUid));
				allowing(product).getFeaturedRank(nonFeaturedCategory);
				will(returnValue(0));

				allowing(product).getCategories();
				will(returnValue(new HashSet<Category>(Arrays.asList(featuredCategory, nonFeaturedCategory))));
			}
		});

		this.productDocumentCreator.addFeaturenessToDocument(document, product);

		assertEquals("Since the product is featured in one of two categories, the index should show it as featured.", String.valueOf(true), document
				.getFieldValue(SolrIndexConstants.FEATURED));
		assertEquals("The featured category should be keyed on the category UID and contain the featured rank", analyzer
				.analyze(this.productDocumentCreator.calculateFeatureBoost(categoryFeaturedRank)), document.getFieldValue(indexUtility
				.createFeaturedField(categoryFeaturedUid)));
		assertEquals("Non-featured categories should be keyed on the category UID and contain a featured rank of 0", analyzer.analyze(0), document
				.getFieldValue(indexUtility.createFeaturedField(categoryNotFeaturedUid)));
	}

	/**	 */
	@Test
	public void testCalculateFeatureBoost1() {
		int result = productDocumentCreator.calculateFeatureBoost(1);
		assertEquals(ProductSolrInputDocumentCreator.FEATURED_RANK_BOOST, result);
	}

	/**	 */
	@Test
	public void testCalculateFeatureBoost2() {
		int result = productDocumentCreator.calculateFeatureBoost(2);
		assertEquals(ProductSolrInputDocumentCreator.FEATURED_RANK_BOOST / 2, result);
	}

	/**
	 * Test that if a product is NOT "featured" in any categories, the document's FEATURED field is false.
	 */
	@Test
	public void testAddFeaturenessToDocumentFalse() {
		final long categoryUid = 5L;

		context.checking(new Expectations() {
			{
				Category category = context.mock(Category.class);
				allowing(category).getUidPk();
				will(returnValue(categoryUid));

				allowing(product).getCategories();
				will(returnValue(Collections.singleton(category)));
				allowing(product).getFeaturedRank(category);
				will(returnValue(0));
			}
		});

		this.productDocumentCreator.addFeaturenessToDocument(document, product);
		assertEquals(String.valueOf(false), document.getFieldValue(SolrIndexConstants.FEATURED));
	}

	/**
	 * Test that for every store in the system a document field is added that is keyed partially on the store's UID, stating whether the product is
	 * displayable.
	 */
	@Test
	public void testAddDisplayablFieldsToDocument() {
		final String storeDisplayableCode = "DISPLAYABLE_STORE";
		final String storeNotDisplayableCode = "NOT_DISAPLAYABLE_STORE";

		final Store displayableStore = context.mock(Store.class, storeDisplayableCode);
		final Store invisibleStore = context.mock(Store.class, storeNotDisplayableCode);
		context.checking(new Expectations() {
			{
				allowing(displayableStore).getCode();
				will(returnValue(storeDisplayableCode));

				allowing(invisibleStore).getCode();
				will(returnValue(storeNotDisplayableCode));
			}
		});
		final Collection<Store> stores = Arrays.asList(displayableStore, invisibleStore);

		// we aren't testing the overridden method, mock it out
		ProductSolrInputDocumentCreator testService = new ProductSolrInputDocumentCreator() {
			@Override
			protected boolean isProductAvailableAndDisplayable(final IndexProduct product, final Store store,
					final Map<Long, Boolean> catalogUidAvailability) {
				if (storeDisplayableCode == store.getCode()) {
					return true;
				}
				return false;
			}
		};
		Map<Long, Boolean> catalogUidAvailability = Collections.emptyMap();
		testService.setIndexUtility(indexUtility);

		testService.addDisplayableFieldsToDocument(document, product, catalogUidAvailability, stores);
		assertEquals("The store in which the product is displayable should show up as such in the index", String.valueOf(true), document.getField(
				indexUtility.createDisplayableFieldName(SolrIndexConstants.DISPLAYABLE, storeDisplayableCode)).getValue());
		assertEquals("The store in which the product is NOT displayable should show up as such in the index", String.valueOf(false), document
				.getField(indexUtility.createDisplayableFieldName(SolrIndexConstants.DISPLAYABLE, storeNotDisplayableCode)).getValue());
	}

	/**
	 * Test that isProductAvailableAndDisplayable() returns true if the product is displayable in the given store and available in all catalogs.
	 */
	@Test
	public void testIsProductAvailableAndDisplayableTrue() {
		// TODO: Fortification: Create test
	}

	/**
	 * Test that isProductAvailableAndDisplayable() returns false if the product is NOT displayable in the given store.
	 */
	@Test
	public void testIsProductAvailableAndDisplayableFalseInStore() {
		// TODO: Fortification: Create test
	}

	/**
	 * Test that isProductAvailableAndDisplayable() returns false if the product is displayable in the given store and is available in some catalogs
	 * but not in all catalogs.
	 */
	@Test
	public void testIsProductAvailableAndDisplayableFalseInCatalog() {
		// TODO: Fortification: Create test
	}

	/**
	 * Test that addPriceFieldsToDocument adds a new field to the document for every currency/store combination, and that the added price is the
	 * lowest price available in that currency in that store. Since the field's key is retrieved dynamically from
	 * {@link IndexUtilityImpl#createPriceFieldName(String, long, java.util.Currency)} then we don't need to check the field keys.
	 */
	@Test
	public void testAddPriceFieldsToDocument() {
		final BigDecimal ten = BigDecimal.TEN;
		final Map<String, Price> priceFieldMap = new HashMap<String, Price>();
		final String priceField = "price_field";
		final Store store = context.mock(Store.class);

		final ProductSolrInputDocumentCreator builder = new ProductSolrInputDocumentCreator() {
			@Override
			protected Map<String, Price> createPricefieldPriceMap(final Collection<PriceListAssignment> assignments, final Product product,
					final Store store, final BaseAmountDataSourceFactory dataSourceFactory) {
				return priceFieldMap;
			}

			@Override
			protected BaseAmountDataSourceFactory initDataSourceFactory(final Product product, final Collection<Store> stores) {
				return null;
			}
		};

		context.checking(new Expectations() {
			{
				Money money = context.mock(Money.class);
				allowing(money).getAmount();
				will(returnValue(ten));

				PricingScheme pricingScheme = context.mock(PricingScheme.class);
				allowing(pricingScheme).getLowestPrice();
				will(returnValue(money));

				Price price = context.mock(Price.class);
				allowing(price).getPricingScheme();
				will(returnValue(pricingScheme));

				priceFieldMap.put(priceField, price);

				Catalog catalog = context.mock(Catalog.class);
				allowing(store).getCatalog();
				will(returnValue(catalog));

				PriceListAssignmentService assignmentService = context.mock(PriceListAssignmentService.class);
				allowing(assignmentService).listByCatalog(catalog);
				will(returnValue(Collections.emptyList()));
				builder.setPriceListAssignmentService(assignmentService);
			}
		});

		builder.addPriceFieldsToDocument(document, null, Collections.singleton(store));
		assertEquals(ten.toPlainString(), document.getField(priceField).getValue());
	}

	/**
	 * Tests that mapPriceFiledWithPrice creates price field and gets the {@link Price} from the PriceLookupService and then maps the price field
	 * with the {@link Price}.
	 */
	@Test
	public void testMapPricefieldWithPrice() {
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		final String priceFidld = "price_field";
		final ProductSolrInputDocumentCreator builder = new ProductSolrInputDocumentCreator() {
			@Override
			protected String getPriceFieldName(final Catalog catalog, final PriceListDescriptor priceListDescriptor) {
				return priceFidld;
			}

			@Override
			public BeanFactory getBeanFactory() {
				return beanFactory;
			}
		};

		final Currency currency = Currency.getInstance(Locale.US);
		final Store store = context.mock(Store.class);
		final Price price = context.mock(Price.class);
		final PriceListDescriptor priceListDescriptor = context.mock(PriceListDescriptor.class);
		final BaseAmountDataSourceFactory baseAmountDataSourceFactory = null;
		context.checking(new Expectations() {
			{
				Catalog catalog = context.mock(Catalog.class);
				allowing(store).getCatalog();
				will(returnValue(catalog));

				allowing(price).getCurrency();
				will(returnValue(currency));

				String guid = "guid";
				allowing(priceListDescriptor).getCurrencyCode();
				will(returnValue(currency.getCurrencyCode()));
				allowing(priceListDescriptor).getGuid();
				will(returnValue(guid));

				PriceListStack priceListStack = context.mock(PriceListStack.class);
				allowing(beanFactory).getBean(ContextIdNames.PRICE_LIST_STACK);
				will(returnValue(priceListStack));
				allowing(priceListStack).addPriceList(guid);
				allowing(priceListStack).setCurrency(currency);

				PromotedPriceLookupService promotedPriceLookupService = context.mock(PromotedPriceLookupService.class);
				allowing(promotedPriceLookupService).getProductPrice(with(product), with(priceListStack), with(store),
						with(Collections.<Long>emptySet()), with(baseAmountDataSourceFactory));
				will(returnValue(price));
				builder.setPromotedPriceLookupService(promotedPriceLookupService);
			}
		});

		final Map<String, Price> actualPricefieldPriceMap = new HashMap<String, Price>();
		builder.mapPricefieldWithPrice(actualPricefieldPriceMap, product, priceListDescriptor, store, baseAmountDataSourceFactory);
		assertEquals(1, actualPricefieldPriceMap.size());
		assertEquals(price, actualPricefieldPriceMap.get(priceFidld));
	}

	/**
	 * Test that all of a product's locale-specific fields are added to the document, including the following fields:<br>
	 * Product display name, attribute values.
	 */
	@Test
	public void testAddLocaleSpecificFieldsToDocument() {
		// TODO: Fortification: Create test
	}

	/**
	 * Test that getIndexType() returns the Product index type.
	 */
	@Test
	public void testGetIndexType() {
		assertEquals(IndexType.PRODUCT, this.productIndexBuilder.getIndexType());
	}

	/**
	 * Test that the spelling updater thread is launched when the index has been updated.
	 */
	@Test
	public void testOnIndexUpdatedLaunchesSpellingUpdaterThread() {
		// TODO: Fortification: Create test
	}

	/**
	 * Test that the rules are recompiled before the index is updated.
	 */
	@Test
	public void testOnIndexUpdating() {
		// TODO: Fortification: Create test
	}

	/**
	 * Test that the findUidsByNotification() is unsupported for the DELETE_ALL and the REBUILD update types.
	 */
	@Test
	public void testFindUidsByNotificationUnsupportedTypes() {
		// TODO: Fortification: Create test
	}

	/**
	 * Test that the findUidsByNotification(), in cases of UPDATE or DELETE notifications, will call
	 * {@link ProductService#findUidsByCategoryUids(java.util.Collection)} if the affected entity type is Category, and will call
	 * {@link ProductService#findUidsByStoreUid(long)} if the affected entity type is Store.
	 */
	@Test
	public void testFindUidsByNotificationCategoryAndStore() {
		// TODO: Fortification: Create test
	}

	/**
	 * Tests adding a null value attribute of type DECIMAL. Doing so should not result to adding new fields to the document as there's no value to be
	 * added. Also check if a field is added if the attribute value is not null.
	 */
	@Test
	public void testAddDecimalAttributeValueToDocument() {
		final AttributeValue attributeValue = context.mock(AttributeValue.class);
		Locale locale = Locale.US;
		context.checking(new Expectations() {
			{
				AttributeType attributeType = AttributeType.DECIMAL;

				Attribute attribute = context.mock(Attribute.class);
				allowing(attribute).getAttributeType();
				will(returnValue(attributeType));
				allowing(attribute);

				allowing(attributeValue).getAttributeType();
				will(returnValue(attributeType));
				allowing(attributeValue).getAttribute();
				will(returnValue(attribute));
				oneOf(attributeValue).getValue();
				will(returnValue(null));
			}
		});

		indexUtility.setSolrAttributeTypeExt(Collections.<String, String> emptyMap());
		productDocumentCreator.addAttributeToDocument(document, attributeValue, locale);
		assertEquals("adding null attribute value should not lead to " + "adding new field to the document", 0, document.getFieldNames().size());

		// ADDITIONAL CHECK: attribute value != null
		final BigDecimal attributeValueValue = BigDecimal.ONE;
		context.checking(new Expectations() {
			{
				oneOf(attributeValue).getValue();
				will(returnValue(attributeValueValue));
			}
		});

		productDocumentCreator.addAttributeToDocument(document, attributeValue, locale);
		assertEquals("Expected one field in the document", 1, document.getFieldNames().size());
		String fieldName = document.getFieldNames().iterator().next();
		assertEquals("the document's value should be equal to the attribute's value", attributeValueValue.toPlainString(), document
				.getField(fieldName).getValue());
	}

	/**
	 * Test that we count the correct number of constituents.
	 */
	@Test
	public void testAddConstituentFieldsToDocumentContainsCorrectCount() {
		ProductSolrInputDocumentCreator indexBuilder = new ProductSolrInputDocumentCreator() {
			@Override
			protected void addConsituentFieldsToDocumentHelper(final SolrInputDocument document, final ConstituentItem product) {
				// mocked for tests
			}
		};

		final ProductBundle bundle = context.mock(ProductBundle.class);
		final BundleConstituent constituent = context.mock(BundleConstituent.class);

		context.checking(new Expectations() {
			{
				allowing(bundle).getConstituents();
				will(returnValue(Collections.singletonList(constituent)));

				ConstituentItem constituentItem = context.mock(ConstituentItem.class);
				allowing(constituentItem).getProduct();
				will(returnValue(product));
				allowing(constituentItem).isBundle();
				will(returnValue(false));

				allowing(constituent).getConstituent();
				will(returnValue(constituentItem));
			}
		});

		List<ConstituentItem> count = new ArrayList<ConstituentItem>();
		indexBuilder.addConstituentFieldsToDocument(null, bundle, count, false);
		assertEquals(1, count.size());
	}
	

	/**
	 * When adding a (bundled) product to the index, we should only ever tell it to sort on a single field. Adding
	 * multiple values on a field that is sorted will result in inconsistent results when values differ from each other.
	 * Currently, bundles are the only way that a product may have multiple brands (a sorted field).
	 */
	@Test
	public void testOnly1SortableBrandFieldForBundles() {
		final String brandName = "someBrandName";
		final String nestedBrandName = "nestedBrandName";

		final Brand brand = context.mock(Brand.class);
		final Category category = context.mock(Category.class);
		
		final BrandService brandService = context.mock(BrandService.class);
		
		ProductSolrInputDocumentCreator indexBuilder = setUpForInitializeInCreateDocumentMethod();
		indexBuilder.setBrandService(brandService);
		
		context.checking(new Expectations() {
			{
				ignoring(category);
				
				//set up brand
				allowing(brand).getCode();
				will(returnValue(brandName));
				allowing(brandService).findByCode(brandName);
				will(returnValue(brand));
				allowing(brand).getDisplayName(with(aNull(Locale.class)), with(any(Boolean.class)));
				will(returnValue(brandName));
				
				// setup bundles
				Brand nestedBrand = context.mock(Brand.class, nestedBrandName);
				allowing(nestedBrand).getCode();
				will(returnValue(nestedBrandName));
				allowing(brandService).findByCode(nestedBrandName);
				will(returnValue(nestedBrand));
				allowing(nestedBrand).getDisplayName(with(aNull(Locale.class)), with(any(Boolean.class)));
				will(returnValue(nestedBrandName));

				Product nestedProduct = context.mock(Product.class);
				allowing(nestedProduct).getBrand();
				will(returnValue(nestedBrand));
				allowing(nestedProduct).getMasterCatalog();
				allowing(nestedProduct).getCategories();
				will(returnValue(Collections.singleton(category)));
				allowing(nestedProduct);

				ConstituentItem constituentItem1 = context.mock(ConstituentItem.class);
				allowing(constituentItem1).isProduct();
				will(returnValue(true));
				allowing(constituentItem1).getProduct();
				will(returnValue(nestedProduct));
				allowing(constituentItem1).isBundle();
				will(returnValue(false));

				BundleConstituent constituent1 = context.mock(BundleConstituent.class);
				allowing(constituent1).getConstituent();
				will(returnValue(constituentItem1));

				ProductBundle productBundle = context.mock(ProductBundle.class);
				allowing(productBundle).getConstituents();
				will(returnValue(Collections.singletonList(constituent1)));

				allowing(product).getWrappedProduct();
				will(returnValue(productBundle));
				allowing(product).getCategories();
				will(returnValue(Collections.singleton(category)));
				allowing(product).getBrand();
				will(returnValue(brand));
				allowing(product);
			}
		});

		indexBuilder.setEntity(product);

		SolrInputDocument document = indexBuilder.createDocument();
		SolrInputField sortBrandField = document.getField(SolrIndexConstants.SORT_BRAND_NAME);
		assertNotNull("brand not added to document", sortBrandField);
		assertEquals("only 1 brand is expected", 1, sortBrandField.getValueCount());
		assertEquals("brand of bundles should be top-level bundle brand", brandName, sortBrandField.getValue());
	}
	
	/**
	 * Test that we store only product bundle display name and not constituents display names.
	 */
	@Test
	public void testOnlyBundlesProductDisplayNameInProductNameNonLCField() {
		
		final Category category = context.mock(Category.class);
		
		final String displayName = "displayName";
		final String constituentDisplayName = "constituentDisplayName";

		//note: 'product' is the IndexProduct whose wrapper is a productBundle
		final ProductBundle productBundle = context.mock(ProductBundle.class);
		final BundleConstituent constituent = context.mock(BundleConstituent.class);
		
		final ConstituentItem constituentItem = context.mock(ConstituentItem.class);
		final Product constituentProduct = context.mock(Product.class);	
		
		final Catalog masterCatalog = context.mock(Catalog.class);
		
		ProductSolrInputDocumentCreator indexBuilder = setUpForInitializeInCreateDocumentMethod();
		
		final BrandService brandService = context.mock(BrandService.class);
		indexBuilder.setBrandService(brandService);
		
		context.checking(new Expectations() {
			{
				ignoring(category);
				ignoring(brandService);
				
				//master catalog locale
				allowing(masterCatalog).getDefaultLocale();
				will(returnValue(Locale.ENGLISH));
				
				//product
				allowing(product).getMasterCatalog();
				will(returnValue(masterCatalog));
				allowing(product).getDisplayName(Locale.ENGLISH);
				will(returnValue(displayName));
				allowing(product).getWrappedProduct();
				will(returnValue(productBundle));
				allowing(product);
				
				//product Bundle
				allowing(productBundle).getConstituents();
				will(returnValue(Collections.singletonList(constituent)));
			
				//constituent
				allowing(constituent).getConstituent();
				will(returnValue(constituentItem));
				
				//constituentItem
				allowing(constituentItem).isBundle();
				will(returnValue(false));
				allowing(constituentItem).getProduct();
				will(returnValue(constituentProduct));
				allowing(constituentItem).isProduct();
				will(returnValue(true));
				
				//constituentProduct
				allowing(constituentProduct).getMasterCatalog();
				will(returnValue(masterCatalog));
				allowing(constituentProduct).getDisplayName(Locale.ENGLISH);
				will(returnValue(constituentDisplayName));
				allowing(constituentProduct);
			}
		});

		indexBuilder.setEntity(product);

		SolrInputDocument document = indexBuilder.createDocument();
		SolrInputField productNameNonLCField = document.getField(SolrIndexConstants.PRODUCT_NAME_NON_LC);
		assertEquals("product name should be only the bundle's product display name", displayName, productNameNonLCField.getValue());
	}

	
	/**
	 * method to setup expectations for calling method createDocument().
	 *
	 * @param category
	 * @param brandService
	 * @param brand
	 * @param brandName
	 * @return
	 */
	private  ProductSolrInputDocumentCreator setUpForInitializeInCreateDocumentMethod() {
		ProductSolrInputDocumentCreator indexBuilder = new ProductSolrInputDocumentCreator() {
			@Override
			protected void addProductFieldsToDocument(final SolrInputDocument document, final Product product) {
				// mocked for tests
			}
			@Override
			protected Map<Long, Boolean> addAvailableCategoriesAndCatalogsToDocument(final SolrInputDocument solrInputDocument, 
					final Product product) {
				//mocked for tests
				return null;
			}
			@Override
			protected String addBrandCodeToDocument(final SolrInputDocument solrInputDocument, final Product product) {
				//mocked for tests
				return null;
			}
			@Override
			protected Collection<String> addSkuCodesToDocument(final SolrInputDocument solrInputDocument, final Product product) {
				//mocked for tests
				return null;
			}
			@Override
			protected void addProductDisplayCodeToDocument(final SolrInputDocument solrInputDocument, final IndexProduct product) {
				//mocked for tests
			}
			@Override
			protected boolean addFeaturenessToDocument(final SolrInputDocument solrInputDocument, final Product product) {
				//mocked for tests
				return false;
			}
			@Override
			protected void addDisplayableFieldsToDocument(final SolrInputDocument solrInputDocument, final IndexProduct product,
					final Map<Long, Boolean> catalogUidAvailability, final Collection<Store> allStores) {
					//mocked for tests
			}
			@Override
			protected void addPriceFieldsToDocument(final SolrInputDocument doc, final IndexProduct product, final Collection<Store> stores) {
				//mocked for tests
			}
			@Override
			protected void addStoreSpecificFieldsToDocument(final SolrInputDocument solrInputDocument, final IndexProduct product,
					final Map<Long, Boolean> catalogUidAvailability) {
				//mocked for tests
			}
		};
		final BeanFactory beanFactory = context.mock(BeanFactory.class);
		indexBuilder.setBeanFactory(beanFactory);
		
		final FetchGroupLoadTuner loadTuner = context.mock(FetchGroupLoadTuner.class);
		indexBuilder.setProductLoadTuner(loadTuner);

		final StoreService storeService = context.mock(StoreService.class);
		indexBuilder.setStoreService(storeService);
		
		context.checking(new Expectations() {
			{
				ignoring(loadTuner);
				ignoring(storeService);
				
				allowing(beanFactory).getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
				will(returnValue(loadTuner));
			}
		});
		indexBuilder.setAnalyzer(analyzer);
		indexBuilder.setIndexUtility(indexUtility);
		return indexBuilder;
	}
}
