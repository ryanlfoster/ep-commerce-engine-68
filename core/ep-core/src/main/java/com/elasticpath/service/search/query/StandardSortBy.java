package com.elasticpath.service.search.query;

import java.util.Collection;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Represents the standard types of sorts within a search.
 */
public class StandardSortBy extends AbstractExtensibleEnum<SortBy> implements SortBy {

	private static final long serialVersionUID = 1L;

	/** Ordinal constant for RELEVANCE. */
	public static final int RELEVANCE_ORDINAL = 0;
	/** Ordinal constant for PRODUCT_NAME. */
	public static final int PRODUCT_NAME_ORDINAL = 1;
	/** Ordinal constant for PRODUCT_DISPLAY_SKU_CODE_EXACT. */
	public static final int PRODUCT_DISPLAY_SKU_CODE_EXACT_ORDINAL = 2;
	/** Ordinal constant for SKU_CONFIG. */
	public static final int SKU_CONFIG_ORDINAL = 3;
	/** Ordinal constant for PRICE. */
	public static final int PRICE_ORDINAL = 4;
	/** Ordinal constant for TOP_SELLER. */
	public static final int TOP_SELLER_ORDINAL = 5;
	/** Ordinal constant for FEATURED_CATEGORY. */
	public static final int FEATURED_CATEGORY_ORDINAL = 6;
	/** Ordinal constant for FEATURED_ANYWHERE. */
	public static final int FEATURED_ANYWHERE_ORDINAL = 7;
	/** Ordinal constant for PRODUCT_NAME_NON_LC. */
	public static final int PRODUCT_NAME_NON_LC_ORDINAL = 8;
	/** Ordinal constant for PRODUCT_START_DATE. */
	public static final int PRODUCT_START_DATE_ORDINAL = 9;
	/** Ordinal constant for PRODUCT_END_DATE. */
	public static final int PRODUCT_END_DATE_ORDINAL = 10;
	/** Ordinal constant for BRAND_NAME. */
	public static final int BRAND_NAME_ORDINAL = 11;
	/** Ordinal constant for PRODUCT_DEFAULT_CATEGORY_NAME. */
	public static final int PRODUCT_DEFAULT_CATEGORY_NAME_ORDINAL = 12;
	/** Ordinal constant for PRODUCT_CODE. */
	public static final int PRODUCT_CODE_ORDINAL = 13;
	/** Ordinal constant for SKU_CODE. */
	public static final int SKU_CODE_ORDINAL = 14;
	/** Ordinal constant for PRODUCT_TYPE_NAME. */
	public static final int PRODUCT_TYPE_NAME_ORDINAL = 15;
	/** Ordinal constant for PROMOTION_ENABLE_DATE. */
	public static final int PROMOTION_ENABLE_DATE_ORDINAL = 16;
	/** Ordinal constant for PROMOTION_EXPIRATION_DATE. */
	public static final int PROMOTION_EXPIRATION_DATE_ORDINAL = 17;
	/** Ordinal constant for PROMOTION_NAME. */
	public static final int PROMOTION_NAME_ORDINAL = 18;
	/** Ordinal constant for PROMOTION_STATE. */
	public static final int PROMOTION_STATE_ORDINAL = 19;
	/** Ordinal constant for PROMOTION_TYPE. */
	public static final int PROMOTION_TYPE_ORDINAL = 20;
	/** Ordinal constant for STATUS. */
	public static final int STATUS_ORDINAL = 21;
	/** Ordinal constant for EMAIL. */
	public static final int EMAIL_ORDINAL = 22;
	/** Ordinal constant for FIRST_NAME. */
	public static final int FIRST_NAME_ORDINAL = 23;
	/** Ordinal constant for LAST_NAME. */
	public static final int LAST_NAME_ORDINAL = 24;
	/** Ordinal constant for NAME. */
	public static final int NAME_ORDINAL = 25;
	/** Ordinal constant for CUSTOMER_ID. */
	public static final int CUSTOMER_ID_ORDINAL = 26;
	/** Ordinal constant for CUSTOMER_NAME. */
	public static final int CUSTOMER_NAME_ORDINAL = 27;
	/** Ordinal constant for ADDRESS. */
	public static final int ADDRESS_ORDINAL = 28;
	/** Ordinal constant for PHONE. */
	public static final int PHONE_ORDINAL = 29;
	/** Ordinal constant for DATE. */
	public static final int DATE_ORDINAL = 30;
	/** Ordinal constant for STORE_CODE. */
	public static final int STORE_CODE_ORDINAL = 31;
	/** Ordinal constant for TOTAL. */
	public static final int TOTAL_ORDINAL = 32;
	/** Ordinal constant for ORDER_NUMBER. */
	public static final int ORDER_NUMBER_ORDINAL = 33;
	/** Ordinal constant for ACTIVE. */
	public static final int ACTIVE_ORDINAL = 34;
	/** Ordinal constant for STORE_NAME. */
	public static final int STORE_NAME_ORDINAL = 35;
	/** Ordinal constant for CALCULATION_METHOD. */
	public static final int CALCULATION_METHOD_ORDINAL = 36;
	/** Ordinal constant for CARRIER. */
	public static final int CARRIER_ORDINAL = 37;
	/** Ordinal constant for REGION. */
	public static final int REGION_ORDINAL = 38;
	/** Ordinal constant for SERVICE_LEVEL_CODE. */
	public static final int SERVICE_LEVEL_CODE_ORDINAL = 39;
	/** Ordinal constant for SERVICE_LEVEL_NAME. */
	public static final int SERVICE_LEVEL_NAME_ORDINAL = 40;
	/** Ordinal constant for SKU_RESULT_TYPE. */
	public static final int SKU_RESULT_TYPE_ORDINAL = 41;
	/** Ordinal constant for DESCRIPTION. */
	public static final int DESCRIPTION_ORDINAL = 42;
	/** Ordinal constant for CREATED_DATE. */
	public static final int CREATED_DATE_ORDINAL = 43;
	/** Ordinal constant for STATE. */
	public static final int STATE_ORDINAL = 44;

	/** Sorts via highest match (relevance). */
	public static final SortBy RELEVANCE = new StandardSortBy(RELEVANCE_ORDINAL, "RELEVANCE", "relevance");

	/** Sorts by product name ascending (a to z). */
	public static final SortBy PRODUCT_NAME = new StandardSortBy(PRODUCT_NAME_ORDINAL, "PRODUCT_NAME", "productName");

	/** Product code for a multi-sku product and sku code for a single-sku product product exact. */
	public static final SortBy PRODUCT_DISPLAY_SKU_CODE_EXACT = new StandardSortBy(PRODUCT_DISPLAY_SKU_CODE_EXACT_ORDINAL,
			"PRODUCT_DISPLAY_SKU_CODE_EXACT",
	"productDisplaySkuCodeExact");

	/** Sorts by sku configuration. */
	public static final SortBy SKU_CONFIG = new StandardSortBy(SKU_CONFIG_ORDINAL, "SKU_CONFIG", "skuConfig");

	/** Sorts by product price ascending (lower to higher). */
	public static final SortBy PRICE = new StandardSortBy(PRICE_ORDINAL, "PRICE", "price");

	/** Sorts by the physical sales counts of products. */
	public static final SortBy TOP_SELLER = new StandardSortBy(TOP_SELLER_ORDINAL, "TOP_SELLER", "topSeller");

	/** Sorts by feature-ness within a particular category and then by featured elsewhere. */
	public static final SortBy FEATURED_CATEGORY = new StandardSortBy(FEATURED_CATEGORY_ORDINAL, "FEATURED_CATEGORY", "featuredProducts");

	/** Sorts by feature-ness regardless of the category. */
	public static final SortBy FEATURED_ANYWHERE = new StandardSortBy(FEATURED_ANYWHERE_ORDINAL, "FEATURED_ANYWHERE", "featuredAnywhere");

	/** Sorts by product name where the name is in catalog default locale, which is available for each product. */
	public static final SortBy PRODUCT_NAME_NON_LC = new StandardSortBy(PRODUCT_NAME_NON_LC_ORDINAL, "PRODUCT_NAME_NON_LC", "productNameNonLC");

	/** Sorts by product start date. */
	public static final SortBy PRODUCT_START_DATE = new StandardSortBy(PRODUCT_START_DATE_ORDINAL, "PRODUCT_START_DATE", "productStartDate");

	/** Sorts by product end date. */
	public static final SortBy PRODUCT_END_DATE = new StandardSortBy(PRODUCT_END_DATE_ORDINAL, "PRODUCT_END_DATE", "productEndDate");

	/** Sorts by brand name. */
	public static final SortBy BRAND_NAME = new StandardSortBy(BRAND_NAME_ORDINAL, "BRAND_NAME", "brandName");

	/** Sorts by default category name of a product. */
	public static final SortBy PRODUCT_DEFAULT_CATEGORY_NAME = new StandardSortBy(PRODUCT_DEFAULT_CATEGORY_NAME_ORDINAL,
			"PRODUCT_DEFAULT_CATEGORY_NAME",
	"productDefaultCategoryName");

	/** Sorts by product code. */
	public static final SortBy PRODUCT_CODE = new StandardSortBy(PRODUCT_CODE_ORDINAL, "PRODUCT_CODE", "productCode");

	/** Sorts by sku code. */
	public static final SortBy SKU_CODE = new StandardSortBy(SKU_CODE_ORDINAL, "SKU_CODE", "productSkuCode");

	/** Sorts by product type name. */
	public static final SortBy PRODUCT_TYPE_NAME = new StandardSortBy(PRODUCT_TYPE_NAME_ORDINAL, "PRODUCT_TYPE_NAME", "productTypeName");

	/** Sorts by promotion enable date. */
	public static final SortBy PROMOTION_ENABLE_DATE = new StandardSortBy(PROMOTION_ENABLE_DATE_ORDINAL,
			"PROMOTION_ENABLE_DATE",
	"promotionEnableDate");

	/** Sorts by promotion expiration date. */
	public static final SortBy PROMOTION_EXPIRATION_DATE = new StandardSortBy(PROMOTION_EXPIRATION_DATE_ORDINAL,
			"PROMOTION_EXPIRATION_DATE",
	"promotionExpirationDate");

	/** Sorts by promotion name. */
	public static final SortBy PROMOTION_NAME = new StandardSortBy(PROMOTION_NAME_ORDINAL, "PROMOTION_NAME", "promotionName");

	/** Sorts by promotion state. */
	public static final SortBy PROMOTION_STATE = new StandardSortBy(PROMOTION_STATE_ORDINAL, "PROMOTION_STATE", "promotionState");

	/** Sorts by promotion type. */
	public static final SortBy PROMOTION_TYPE = new StandardSortBy(PROMOTION_TYPE_ORDINAL, "PROMOTION_TYPE", "promotionType");

	/** Sorts by status. */
	public static final SortBy STATUS = new StandardSortBy(STATUS_ORDINAL, "STATUS", "status");

	/** Sorts by email. */
	public static final SortBy EMAIL = new StandardSortBy(EMAIL_ORDINAL, "EMAIL", "email");

	/** Sorts by first name. */
	public static final SortBy FIRST_NAME = new StandardSortBy(FIRST_NAME_ORDINAL, "FIRST_NAME", "firstName");

	/** Sorts by last name. */
	public static final SortBy LAST_NAME = new StandardSortBy(LAST_NAME_ORDINAL, "LAST_NAME", "lastName");

	/** Sorts by name. */
	public static final SortBy NAME = new StandardSortBy(NAME_ORDINAL, "NAME", "name");

	/** Sorts by customer ID. */
	public static final SortBy CUSTOMER_ID = new StandardSortBy(CUSTOMER_ID_ORDINAL, "CUSTOMER_ID", "customerId");

	/** Sorts by customer name. */
	public static final SortBy CUSTOMER_NAME = new StandardSortBy(CUSTOMER_NAME_ORDINAL, "CUSTOMER_NAME", "customerName");

	/** Sorts by address. */
	public static final SortBy ADDRESS = new StandardSortBy(ADDRESS_ORDINAL, "ADDRESS", "address");

	/** Sorts by phone. */
	public static final SortBy PHONE = new StandardSortBy(PHONE_ORDINAL, "PHONE", "phone");

	/** Sorts by date. */
	public static final SortBy DATE = new StandardSortBy(DATE_ORDINAL, "DATE", "date");

	/** Sorts by store code. */
	public static final SortBy STORE_CODE = new StandardSortBy(STORE_CODE_ORDINAL, "STORE_CODE", "storeCode");

	/** Sorts by total. */
	public static final SortBy TOTAL = new StandardSortBy(TOTAL_ORDINAL, "TOTAL", "total");

	/** Sorts by order number. */
	public static final SortBy ORDER_NUMBER = new StandardSortBy(ORDER_NUMBER_ORDINAL, "ORDER_NUMBER", "orderNumber");

	/** Sorts by active field. */
	public static final SortBy ACTIVE = new StandardSortBy(ACTIVE_ORDINAL, "ACTIVE", "active");

	/** Sorts by store name. */
	public static final SortBy STORE_NAME = new StandardSortBy(STORE_NAME_ORDINAL, "STORE_NAME", "storeName");

	/** Sorts by calculation method. */
	public static final SortBy CALCULATION_METHOD = new StandardSortBy(CALCULATION_METHOD_ORDINAL, "CALCULATION_METHOD", "calculationMethod");

	/** Sorts by carrier. */
	public static final SortBy CARRIER = new StandardSortBy(CARRIER_ORDINAL, "CARRIER", "carrier");

	/** Sorts by region. */
	public static final SortBy REGION = new StandardSortBy(REGION_ORDINAL, "REGION", "region");

	/** Sorts by shipping service level code. */
	public static final SortBy SERVICE_LEVEL_CODE = new StandardSortBy(SERVICE_LEVEL_CODE_ORDINAL, "SERVICE_LEVEL_CODE", "serviceLevelCode");

	/** Sorts by shipping service level name. */
	public static final SortBy SERVICE_LEVEL_NAME = new StandardSortBy(SERVICE_LEVEL_NAME_ORDINAL, "SERVICE_LEVEL_NAME", "serviceLevelName");

	/** SKU result type. */
	public static final SortBy SKU_RESULT_TYPE = new StandardSortBy(SKU_RESULT_TYPE_ORDINAL, "SKU_RESULT_TYPE", "skuResultType");

	/** Description. */
	public static final SortBy DESCRIPTION = new StandardSortBy(DESCRIPTION_ORDINAL, "DESCRIPTION", "description");

	/** Created Date. */
	public static final SortBy CREATED_DATE = new StandardSortBy(CREATED_DATE_ORDINAL, "CREATED_DATE", "createdDate");

	/** State Code Name. */
	public static final SortBy STATE = new StandardSortBy(STATE_ORDINAL, "STATE", "stateCodeName");

	private final String sortString;


	/**
	 * Create a new enum value.
	 * @param ordinal the unique ordinal value
	 * @param name the enum value name
	 * @param sortString the sort string
	 */
	protected StandardSortBy(final int ordinal, final String name, final String sortString) {
		super(ordinal, name, SortBy.class);
		this.sortString = sortString;
	}

	@Override
	public String getSortString() {
		return sortString;
	}

	@Override
	public String toString() {
		return getSortString();
	}

	/**
	 * Find the enum value with the specified ordinal value.
	 * @param ordinal the ordinal value
	 * @return the enum value
	 */
	public static SortBy valueOf(final int ordinal) {
		return valueOf(ordinal, SortBy.class);
	}

	/**
	 * Find the enum value with the specified name.
	 * @param name the name
	 * @return the enum value
	 */
	public static SortBy valueOf(final String name) {
		return valueOf(name, SortBy.class);
	}

	/**
	 * Find the enum value with the specified sortString.
	 * @param sortString the sortString
	 * @return the enum value
	 */
	public static SortBy valueOfUsingSortName(final String sortString) {
		for (final SortBy sortBy : values()) {
			if (sortBy.getSortString().equals(sortString)) {
				return sortBy;
			}
		}
		throw new IllegalArgumentException("No such SortBy with sortString " + sortString);
	}

	/**
	 * Find all enum values for a particular enum type.
	 * @return the enum values
	 */
	public static Collection<SortBy> values() {
		return values(SortBy.class);
	}

	@Override
	protected Class<SortBy> getEnumType() {
		return SortBy.class;
	}

}
