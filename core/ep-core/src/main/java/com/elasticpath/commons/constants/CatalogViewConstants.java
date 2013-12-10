package com.elasticpath.commons.constants;

/**
 * Contains search related constants.
 */
public class CatalogViewConstants {

	/**
	 * The prefix used in attribute filters in query strings.
	 */
	public static final String ATTRIBUTE_FILTER_PREFIX = "attr-";

	/**
	 * The prefix used in attribute range filters in query strings.
	 */
	public static final String ATTRIBUTE_RANGE_FILTER_PREFIX = "attrange-";

	/**
	 * The prefix used in category filters in query strings.
	 */
	public static final String CATEGORY_FILTER_PREFIX = "cat-";

	/**
	 * The suffix used in brand filters in query strings to represent a brand filter for those products without brand.
	 */
	public static final String BRAND_FILTER_OTHERS = "others";
	
	/**
	 * The prefix used in brand filters in query strings.
	 */
	public static final String BRAND_FILTER_PREFIX = "brand-";

	/**
	 * The prefix used in price filters in query strings.
	 */
	public static final String PRICE_FILTER_PREFIX = "price-";

	/**
	 * The prefix used in price sorter in query strings.
	 */
	public static final String PRICE_SORTER_PREFIX = "price-";

	/**
	 * The prefix used in product name sorter in query strings.
	 */
	public static final String PRODUCT_NAME_SORTER_PREFIX = "productname-";

	/**
	 * The prefix used in top seller sorter to query strings.
	 */
	public static final String TOP_SELLER_SORTER_PREFIX = "topseller-";

	/**
	 * The prefix used in featured product sorter to query strings.
	 */
	public static final String FEATURED_PRODUCT_SORTER_PREFIX = "featuredproduct-";

	/**
	 * The maximum length of key words in a search request.
	 */
	public static final int SEARCH_KEYWORDS_MAX_LENGTH = 255;
}
