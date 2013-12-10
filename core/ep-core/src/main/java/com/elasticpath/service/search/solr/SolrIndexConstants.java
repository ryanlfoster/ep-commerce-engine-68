package com.elasticpath.service.search.solr;

import java.io.File;

/**
 * Contains all constants used in Lucene index.
 */
public final class SolrIndexConstants {

	/**
	 * The Solr home system property name.
	 */
	public static final String SOLR_HOME_PROPERTY = "solr.solr.home";

	/**
	 * The Solr synonym group enable property name.
	 */
	public static final String SOLR_ENABLE_SYNONYM_GROUPS = "ep.solr.EnableSynonymGroups";

	/** Solr score field for default sorting. */
	public static final String SCORE = "score";

	/** Object UID key. */
	public static final String OBJECT_UID = "objectUid";

	/** Category UID. */
	public static final String CATEGORY_UID = "categoryUid";

	/** Parent category UIDs. */
	public static final String PARENT_CATEGORY_UIDS = "parentCategoryUids";

	/** Parent category Codes. */
	public static final String PARENT_CATEGORY_CODES = "parentCategoryCodes";

	/** Category code. */
	public static final String CATEGORY_CODE = "categoryCode";

	/** Category name. */
	public static final String CATEGORY_NAME = "categoryName";

	/** Exact category name. */
	public static final String CATEGORY_NAME_EXACT = "categoryNameExact";

	/** Category attributes. */
	public static final String CATEGORY_ATTRIBUTES = "categoryAttributes";

	/** Brand code. */
	public static final String BRAND_CODE = "brandCode";
	
	/** Brand code for dismax query search. */
	public static final String BRAND_CODE_FOR_DISMAX = "brandCodeForDismax";

	/** A non-lower cased brand code. */
	public static final String BRAND_CODE_NON_LC = "brandCodeNonLC";

	/** Brand display name. */
	public static final String BRAND_NAME = "brandName";

	/** Exact brand name. */
	public static final String BRAND_NAME_EXACT = "brandNameExact";

	/** Catalog UID. */
	public static final String CATALOG_UID = "catalogUid";

	/** Catalog Code. */
	public static final String CATALOG_CODE = "catalogCode";

	/** Category linked status. */
	public static final String CATEGORY_LINKED = "categoryLinked";

	/** Active Flag. */
	public static final String ACTIVE_FLAG = "activeFlag";

	/** First name. */
	public static final String FIRST_NAME = "firstName";

	/** First name exact. */
	public static final String FIRST_NAME_EXACT = "firstNameExact";

	/** Last name. */
	public static final String LAST_NAME = "lastName";

	/** Last name exact. */
	public static final String LAST_NAME_EXACT = "lastNameExact";

	/** Zip or Postal code. */
	public static final String ZIP_POSTAL_CODE = "zipPostal";

	/** Preferred billing address. */
	public static final String PREFERRED_BILLING_ADDRESS = "preferredBillingAddress";

	/** Preferred billing address exact. */
	public static final String PREFERRED_BILLING_ADDRESS_EXACT = "preferredBillingAddressExact";

	/** Customer number. */
	public static final String CUSTOMER_NUMBER = "customerNumber";

	/** Phone number. */
	public static final String PHONE_NUMBER = "phoneNumber";

	/** Phone number exact. */
	public static final String PHONE_NUMBER_EXACT = "phoneNumberExact";

	/** Email. */
	public static final String EMAIL = "email";

	/** Email exact. */
	public static final String EMAIL_EXACT = "emailExact";

	/** User Id. */
	public static final String USER_ID = "userId";

	/** User Id and email in one field. */
	public static final String USERID_AND_EMAIL = "userIdAndEmail";

	/** User Id and email exact in one field. */
	public static final String USERID_AND_EMAIL_EXACT = "userIdAndEmailExact";

	/** Store Code. */
	public static final String STORE_CODE = "storeCode";

	/** Order number. */
	public static final String ORDER_NUMBER = "orderNumber";

	/** Sku code. */
	public static final String SKU_CODE = "skuCode";

	/** Create time. */
	public static final String CREATE_TIME = "createTime";

	/** Order status. */
	public static final String ORDER_STATUS = "orderStatus";

	/** Shipment status. */
	public static final String SHIPMENT_STATUS = "shipmentStatus";

	/** Shipment code. */
	public static final String SHIPMENT_ZIPCODE = "shipmentZipcode";

	/** Start date. */
	public static final String START_DATE = "startDate";

	/** End date. */
	public static final String END_DATE = "endDate";

	/** Last modified date. */
	public static final String LAST_MODIFIED_DATE = "lastModifiedDate";

	/** RMA code. */
	public static final String RMA_CODE = "rmaCode";

	/** RMA codes. */
	public static final String RMA_CODES = "rmaCodes";

    /** Warehouses codes. */
    public static final String WAREHOUSES_CODE = "warehousesCode";

	/** Product/Category is displayable. */
	public static final String DISPLAYABLE = "displayable";

	/** Product display name. */
	public static final String PRODUCT_NAME = "productName";

	/** Exact product display name. */
	public static final String PRODUCT_NAME_EXACT = "productNameExact";

	/** Product display name in default locale, empty locale suffix. */
	public static final String PRODUCT_NAME_NON_LC = "productNameNonLC";

	/** Product code. */
	public static final String PRODUCT_SKU_CODE = "productSkuCode";

	/** Product code for a multi-sku product and sku code for a single-sku product product exact. */
	public static final String PRODUCT_DISPLAY_SKU_CODE_EXACT = "productDisplaySkuCodeExact";

	/** Product code. */
	public static final String PRODUCT_CODE = "productCode";

	/** Promotion name. */
	public static final String PROMOTION_NAME = "promotionName";

	/** Promotion name exact. */
	public static final String PROMOTION_NAME_EXACT = "promotionNameExact";

	/** Promotion type. */
	public static final String PROMOTION_RULESET_UID = "promotionRuleSetUid";

	/** Promotion type name. */
	public static final String PROMOTION_RULESET_NAME = "promotionRuleSetName";

	/** Promotion type name exact. */
	public static final String PROMOTION_RULESET_NAME_EXACT = "promotionRuleSetNameExact";

	/** Promotion Enabled Flag. */
	public static final String PROMOTION_STATE = "promotionState";

	/** Product price. */
	public static final String PRICE = "price";

	/** Prefix to all attributes. */
	public static final String ATTRIBUTE_PREFIX = "attribute.";

	/** Partial field ID for featured product order. */
	public static final String FEATURED_FIELD = "feature_";

	/** Field which specifies whether a product is featured or not. */
	public static final String FEATURED = "featured";

	/** Sales count. */
	public static final String SALES_COUNT = "salesCount";

	/** SOLR core name of the product index. */
	public static final String PRODUCT_SOLR_CORE = "product";

	/** SOLR core name of the customer index. */
	public static final String CUSTOMER_SOLR_CORE = "customer";

	/** SOLR core name of the category index. */
	public static final String CATEGORY_SOLR_CORE = "category";

	/** SOLR core name of the promotion index. */
	public static final String PROMOTION_SOLR_CORE = "promotion";

	/** SOLR core name of the cmuser index. */
	public static final String CMUSER_SOLR_CORE = "cmuser";

	/** SOLR core name of the shipping service level index. */
	public static final String SHIPPING_SERVICE_LEVEL_CORE = "shippingservicelevel";

	/** SOLR core name of the SKU index. */
	public static final String SKU_SOLR_CORE = "sku";

	/** The name of the SOLR spell checker request handler. */
	public static final String SPELL_CHECKER = "spellchecker";

	/**
	 * Directory for SOLR configuration and index storage under WEB-INF. Used for embedded SOLR
	 * servers.
	 */
	public static final String SOLR_HOME_DIR = File.separatorChar + "solrHome";

	/** Product category. */
	public static final String PRODUCT_CATEGORY = "productCategory";

	/** Product category non-lower-case. */
	public static final String PRODUCT_CATEGORY_NON_LC = "productCategoryNonLC";

	/** Product default category name. */
	public static final String PRODUCT_DEFAULT_CATEGORY_NAME = "productDefaultCategoryName";

	/** Product default category name exact. */
	public static final String PRODUCT_DEFAULT_CATEGORY_NAME_EXACT = "productDefaultCategoryNameExact";

	/** Product type name. */
	public static final String PRODUCT_TYPE_NAME = "productTypeName";

	/** Product type name exact. */
	public static final String PRODUCT_TYPE_NAME_EXACT = "productTypeNameExact";

	/** User name. */
	public static final String USER_NAME = "userName";

	/** Status. */
	public static final String STATUS = "status";

	/** User role. */
	public static final String USER_ROLE = "userRole";

	/** All catalogs access. */
	public static final String ALL_CATALOGS_ACCESS = "allCatalogsAccess";

	/** All stores access. */
	public static final String ALL_STORES_ACCESS = "allStoresAccess";

	/** Total money. */
	public static final String TOTAL_MONEY = "totalMoney";

	/** Currency symbol. */
	public static final String CURRENCY_SYMBOL = "currencySymbol";

	/** This constant is for the field used for price sorting. */
	public static final String PRICE_SORT = "pricesort";

	/** Shipping Service Level code. */
	public static final String SERVICE_LEVEL_CODE = "serviceLevelCode";

	/** Carrier for a shipping service level. */
	public static final String CARRIER = "carrier";

	/** Carrier for a shipping service level exact. */
	public static final String CARRIER_EXACT = "carrierExact";

	/** Shipping Service Level region name. */
	public static final String REGION = "region";

	/** Shipping Service Level region name exact. */
	public static final String REGION_EXACT = "regionExact";

	/** Shipping Service Level name. */
	public static final String SERVICE_LEVEL_NAME = "serviceLevelName";

	/** Shipping Service Level name exact. */
	public static final String SERVICE_LEVEL_NAME_EXACT = "serviceLevelNameExact";

	/** Name for Shipping Service Level Store. */
	public static final String STORE_NAME = "store";

	/** Exact name for shipping service level store. */
	public static final String STORE_NAME_EXACT = "storeExact";

	/** Constituent count key. */
	public static final String CONSTITUENT_COUNT = "constituentCount";

	/**
	 * The master category for the product.
	 */
	public static final String MASTER_PRODUCT_CATEGORY = "masterProductCategory";

	/** SKU configuration. **/
	public static final String SKU_CONFIGURATION = "skuConfiguration";

	/** Prefix to all SKU options. */
	public static final String SKU_OPTION_PREFIX = "skuoption.";

	/** SKU Configuration in default locale. */
	public static final String SKU_CONFIG_DEFAULT_LOCALE = "skuConfigDefaultLocale";

	/** Result type of SKU search. */
	public static final String SKU_RESULT_TYPE = "skuResultType";

	/** Brand display name. Field used for sorting => is a single valued field. */
	public static final String SORT_BRAND_NAME = "sortBrandName";

	/** Exact brand name. Field used for sorting => is a single valued field. */
	public static final String SORT_BRAND_NAME_EXACT = "sortBrandNameExact";

	/** Product display name. Field used for sorting => is a single valued field. */
	public static final String SORT_PRODUCT_NAME = "sortProductName";

	/** Exact product display name. Field used for sorting => is a single valued field. */
	public static final String SORT_PRODUCT_NAME_EXACT = "sortProductNameExact";

	/** Product default category name. Field used for sorting => is a single valued field.*/
	public static final String SORT_PRODUCT_DEFAULT_CATEGORY_NAME = "sortProductDefaultCategoryName";

	/** Product default category name exact. Field used for sorting => is a single valued field.*/
	public static final String SORT_PRODUCT_DEFAULT_CATEGORY_NAME_EXACT = "sortProductDefaultCategoryNameExact";

}
