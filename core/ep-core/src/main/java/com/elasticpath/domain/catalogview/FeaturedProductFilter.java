package com.elasticpath.domain.catalogview;

/**
 * Special filter designed to filter out only those products that are featured. Not intended for
 * displayable use.
 */
public interface FeaturedProductFilter extends Filter<FeaturedProductFilter> {

	/**
	 * Returns the category UID of the category to get featured products for (the root category).
	 * 
	 * @return the category UID of the category to get featured product for
	 */
	Long getCategoryUid();

	/**
	 * Sets the category UID of the category to get featured products for (the root category).
	 * 
	 * @param categoryUid the category UID of the category to get featured product for
	 */
	void setCategoryUid(final Long categoryUid);
}
