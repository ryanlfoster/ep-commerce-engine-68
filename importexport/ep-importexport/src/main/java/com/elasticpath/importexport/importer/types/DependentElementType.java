package com.elasticpath.importexport.importer.types;


/**
 * Types for dependent elements.
 */
public enum DependentElementType {

	/**
	 * Type for category attributes.
	 */
	CATEGORY_ATTRIBUTES,

	/**
	 * Type for product catalog prices.
	 */
	PRODUCT_CATALOG_PRICES,

	/**
	 * Type for product price tiers.
	 */
	PRODUCT_PRICE_TIERS,

	/**
	 * Type for product price currencies.
	 */
	PRODUCT_PRICE_CURRENCIES,

	/**
	 * Type for product attributes.
	 */
	PRODUCT_ATTRIBUTES,

	/**
	 * Type for product skus.
	 */
	PRODUCT_SKUS,

	/**
	 * Type for category assignments.
	 */
	PRODUCT_CATEGORY_ASSIGNMENTS,

	/**
	 * Type for product associations.
	 */
	PRODUCT_ASSOCIATIONS,

	/**
	 * Type for product sku pricing.
	 */
	PRODUCT_SKU_PRICING,

	/**
	 * Type for sku attributes.
	 */
	SKU_ATTRIBUTES,

	/**
	 * Type for sku price tiers.
	 */
	SKU_PRICE_TIERS,

	/**
	 * Type for sku price currencies.
	 */
	SKU_PRICE_CURRENCIES,

	/**
	 * Type for sku catalog prices.
	 */
	SKU_CATALOG_PRICES,

	/**
	 * Type for base amounts.
	 */
	BASE_AMOUNTS,

	/**
	 * Type for price adjustments.
	 */
	PRICE_ADJUSTMENTS,

	/**
	 * Type for Setting Values.
	 */
	SETTING_VALUES,

	/**
	 * Type for Setting metadata.
	 */
	SETTING_METADATA,

	/**
	 * Collections in the Tax Jurisdiction importer.
	 */
	TAX_VALUES, TAX_REGIONS, TAX_CATEGORIES,

	/**
	 * For Store.
	 */
	TAX_CODES, TAX_JURISDICTIONS, WAREHOUSES, PAYMENT_GATEWAYS, CREDIT_CARD_TYPES, PAYMENT_GATEWAY_PROPERTIES,

	/**
	 * Type for coupon set.
	 */
	COUPONSET,

	/**
	 * Types for Customer.
	 */
	ADDRESSES, CUSTOMER_GROUPS, CREDIT_CARDS, CUSTOMER_SESSIONS,
	
	/**
	 * Type for {@link com.elasticpath.domain.cmuser.UserRole}.
	 */
	USER_ROLES, 
	
	/**
	 * Type for {@link com.elasticpath.domain.cmuser.UserPermission}.
	 */
	USER_PERMISSIONS, 

	/**
	 * Type for Price Lists in {@link com.elasticpath.domain.cmuser.CmUser}.
	 */
	PRICE_LISTS, 
	
	/**
	 * Type for {@link com.elasticpath.domain.cmuser.UserPasswordHistoryItem} in {@link com.elasticpath.domain.cmuser.CmUser}.
	 */
	USER_PASSWORD_HISTORY, 
	
	/**
	 * Type for {@link com.elasticpath.domain.catalog.Catalog}.
	 */
	CATALOGS, 
	
	/**
	 * Type for {@link com.elasticpath.domain.store.Store}.
	 */
	STORES,
	
	/**
	 * Type for {@link com.elasticpath.importexport.exporter.exporters.impl.StoreAssociation}.
	 */
	STORE_ASSOCIATIONS,
	
	/**
	 * Types for CM Import Jobs.
	 */
	CM_IMPORT_MAPPINGS;
}
