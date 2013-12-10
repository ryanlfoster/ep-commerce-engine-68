package com.elasticpath.persistence.support;

/**
 * Contains fetch group constants.
 */
public final class FetchGroupConstants {
	
	/** The default fetch group. Only items specified (or defaulted) as eager will be loaded. */
	public static final String DEFAULT = "default";
	
	/** Everything is eagerly loaded. */
	public static final String ALL = "all";
	
	/** Nothing is loaded by the UID. */
	public static final String NONE = "none";
	
	/** Fetch group for the product index. */
	public static final String PRODUCT_INDEX = "productIndex";
	
	/** Fetch group for the product index. */
	public static final String PRODUCT_SKU_INDEX = "productSkuIndex";
	
	/** Fetch group for the category index. */
	public static final String CATEGORY_INDEX = "categoryIndex";
	
	/** Fetch group for the order index. */
	public static final String ORDER_INDEX = "orderIndex";
	
	/** Fetch group for the order default. */
	public static final String ORDER_DEFAULT = "orderDefault";
	
	/** Fetch group for the notes. */
	public static final String ORDER_NOTES = "orderNotes";
	
	/** Fetch group for the store and warehouses. */
	public static final String ORDER_STORE_AND_WAREHOUSE = "orderStoreAndWarehouse";

	/** Fetch group for order searching. */
	public static final String ORDER_SEARCH = "orderSearch";
	
	/** Fetch group for order - loads only basic order information. */
	public static final String ORDER_LIST_BASIC = "orderBasic";
	
	/** Fetch group for the promotion index. */
	public static final String PROMOTION_INDEX = "promotionIndex";
	
	/** Fetch group for the order return index. */
	public static final String ORDER_RETURN_INDEX = "orderReturnIndex";
	
	/** Fetch group for the customer index. */
	public static final String CUSTOMER_INDEX = "customerIndex";
	
	/** Fetch group for populating attribute values. */
	public static final String ATTRIBUTE_VALUES = "attributeValues";
	
	/** Fetch group for populating order addresses. */
	public static final String ORDER_ADDRESSES = "orderAddresses";
	
	/** Fetch group for populating customer addresses. */
	public static final String CUSTOMER_ADDRESSES = "customerAddresses";
	
	/** Fetch group for populating a customer profile. */
	public static final String CUSTOMER = "customer";
	
	/** Fetch group for specifying an infinite child category depth. */
	public static final String INFINITE_CHILD_CATEGORY_DEPTH = "infiniteChildCategoryDepth";
	
	/** Fetch group for specifying an infinite parent category depth. */
	public static final String INIFINITE_PARENT_CATEGORY_DEPTH = "infiniteParentCategoryDepth";
		
	/** Fetch group for populating category availability. */
	public static final String CATEGORY_AVAILABILITY = "categoryAvailability";
	
	/** Fetch group for retrieving the catalog. */
	public static final String CATALOG = "catalog";
	
	/** Fetch group for retrieving the the minimum amount of category information for simple category tasks. */
	public static final String CATEGORY_BASIC = "categoryBasic";
	
	/** Loads default attributes for catalog. */
	public static final String CATALOG_DEFAULTS = "catalogDefaults";
	
	/** Fetch group for retrieving the catalog editor. */
	public static final String CATALOG_EDITOR = "catalog_editor";
	
	/** Fetch group for retrieving the level 1 child category. */
	public static final String CATEGORY_CHILD_LEVEL_1 = "childCategoryLevel1";
	
	/** Fetch group for retrieving store to edit. */
	public static final String STORE_FOR_EDIT = "storeForEdit";
	
	/** Loads the minimal amount of information required to have a category in any type of hash table. */
	public static final String CATEGORY_HASH_MINIMAL = "categoryHashMinimal";
	
	/** Loads the minimal amount of information required to have a product in any type of hash table. */
	public static final String PRODUCT_HASH_MINIMAL = "productHashMinimal";
	
	/** Loads only the required fields for linking products and categories (creating linked categories). */
	public static final String LINK_PRODUCT_CATEGORY = "linkProductCategory";

	/** Loads only the category attributes. */
	public static final String CATEGORY_ATTRIBUTES = "categoryAttributes";
	
	/** Loads only category locale dependent fields. */
	public static final String CATEGORY_LDF = "categoryLdf";
	
	/** Loads only the required fields of a product association. */
	public static final String PRODUCT_ASSOCIATION_MINIMAL = "productAssociationMinimal";
	
	/** Loads only the shared login stores. */
	public static final String STORE_SHARING = "storeSharing";
	
	/** Loads the bundle constituents a product bundle. */
	public static final String BUNDLE_CONSTITUENTS = "bundleConstituentsInfiniteDepth";
	
	/** Loads the dependent items of a shopping item. */
	public static final String SHOPPING_ITEM_CHILD_ITEMS = "dependentChildrenInfiniteDepth";
	
	/** Loads the minimal attributes of an OAuth access token.	 */
	public static final String OAUTH_BASIC = "oauthMinimalAttributes";
}
