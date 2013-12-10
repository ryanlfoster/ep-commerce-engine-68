package com.elasticpath.commons.constants;

/**
 * Contains all constants used in SEO (Search Engine Optimization).
 */
public final class SeoConstants {
	/**
	 * Define the SEO(Search Engine Optimization) Url's suffix.
	 */
	public static final String SUFFIX = ".html";

	/**
	 * Define the default separator used between tokens, such as between one price filter and one brand filter.
	 */
	public static final String DEFAULT_SEPARATOR_BETWEEN_TOKENS = "-";

	/**
	 * Define the default separator used in one token, such as in one price filter.
	 */
	public static final String DEFAULT_SEPARATOR_IN_TOKEN = "_";

	/**
	 * Define the separator used between Advanced Search Filters and Filtered Nav Filters.
	 */
	public static final String SEPARATOR_BETWEEN_ADV_SEARCH_AND_FITERED_NAV_FILTERS = "|";
	
	/**
	 * Define the SEO(Search Engine Optimization) Url's prefix for products.
	 */
	public static final String PRODUCT_PREFIX = "prod";

	/**
	 * Define the SEO(Search Engine Optimization) Url's prefix for categories.
	 */
	public static final String CATEGORY_PREFIX = "c";
	
	/**
	 * Define the SEO(Search Engine Optimization) ID prefix for brand filters.
	 */
	public static final String BRAND_FILTER_PREFIX = "b";

	/**
	 * Define the SEO(Search Engine Optimization) ID prefix for price filters.
	 */
	public static final String PRICE_FILTER_PREFIX = "pr";

	/**
	 * Define the SEO(Search Engine Optimization) ID prefix for attribute filters.
	 */
	public static final String ATTRIBUTE_FILTER_PREFIX = "at";

	/**
	 * Define the SEO(Search Engine Optimization) ID prefix for attribute range filters.
	 */
	public static final String ATTRIBUTE_RANGE_FILTER_PREFIX = "ar";
	
	/**
	 * Define the SEO ID prefix for sku option value filters.
	 */
	public static final String SKU_OPTION_VALUE_PREFIX = "so";

	/**
	 * Define the SEO(Search Engine Optimization) ID prefix for page numbers.
	 */
	public static final String PAGE_NUMBER_PREFIX = "p";

	
	/**
	 * Define the SEO(Search Engine Optimization) ID prefix for sitemap.
	 */
	public static final String SITEMAP_PREFIX = "sitemap";

	/**
	 * Define the all-page token used in url text.
	 */
	public static final String ALL_PAGE_TOKEN = "0";

	/**
	 * Define the token to represent max value.
	 */
	public static final String MAX_VALUE = "max";

	/**
	 * Define the token to represent max value.
	 */
	public static final String  MIN_VALUE = "min";
	
	/**
	 * Define the western character to English map for SEO Url.
	 */
	public static final char[][] WESTERN_TO_ENGLISH_ARRAY = new char[][] {
		{'\u00a5', 'Y'}, {'\u00b5', 'u'}, {'\u00c0', 'A'}, {'\u00c1', 'A'}, {'\u00c2', 'A'}, {'\u00c3', 'A'}, {'\u00c4', 'A'}, {'\u00c5', 'A'},
	    {'\u00c6', 'A'}, {'\u00c7', 'C'}, {'\u00c8', 'E'}, {'\u00c9', 'E'}, {'\u00ca', 'E'}, {'\u00cb', 'E'}, {'\u00cc', 'I'}, {'\u00cd', 'I'},
	    {'\u00ce', 'I'}, {'\u00cf', 'I'}, {'\u00d0', 'D'}, {'\u00d1', 'N'}, {'\u00d2', 'O'}, {'\u00d3', 'O'}, {'\u00d4', 'O'}, {'\u00d5', 'O'},
	    {'\u00d6', 'O'}, {'\u00d8', 'O'}, {'\u00d9', 'U'}, {'\u00da', 'U'}, {'\u00db', 'U'}, {'\u00dc', 'U'}, {'\u00dd', 'Y'}, {'\u00df', 's'},
	    {'\u00e0', 'a'}, {'\u00e1', 'a'}, {'\u00e2', 'a'}, {'\u00e3', 'a'}, {'\u00e4', 'a'}, {'\u00e5', 'a'}, {'\u00e6', 'a'}, {'\u00e7', 'c'},
	    {'\u00e8', 'e'}, {'\u00e9', 'e'}, {'\u00ea', 'e'}, {'\u00eb', 'e'}, {'\u00ec', 'i'}, {'\u00ed', 'i'}, {'\u00ee', 'i'}, {'\u00ef', 'i'},
	    {'\u00f0', 'o'}, {'\u00f1', 'n'}, {'\u00f2', 'o'}, {'\u00f3', 'o'}, {'\u00f4', 'o'}, {'\u00f5', 'o'}, {'\u00f6', 'o'}, {'\u00f8', 'o'},
	    {'\u00f9', 'u'}, {'\u00fa', 'u'}, {'\u00fb', 'u'}, {'\u00fc', 'u'}, {'\u00fd', 'y'}, {'\u00ff', 'y'}};
}
