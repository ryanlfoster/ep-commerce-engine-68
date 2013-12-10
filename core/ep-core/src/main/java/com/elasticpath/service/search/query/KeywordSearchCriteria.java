package com.elasticpath.service.search.query;


/**
 * Keyword search criteria.
 */
public class KeywordSearchCriteria extends AbstractProductCategorySearchCriteria {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private String keyword;
	
	private Long categoryUid;
	
	/**
	 * Gets the keyword to search for.
	 *
	 * @return the keyword to search for
	 */
	public String getKeyword() {
		return keyword;
	}
	
	/**
	 * Sets the keyword to search for.
	 *
	 * @param keyword the keyword to search for
	 */
	public void setKeyword(final String keyword) {
		this.keyword = keyword;
	}
	
	/**
	 * Gets the category UID to search for.
	 *
	 * @return the category UID to search for
	 */
	public Long getCategoryUid() {
		return categoryUid;
	}
	
	/**
	 * Sets the category UID to search for.
	 *
	 * @param categoryUid the category UID to search for
	 */
	public void setCategoryUid(final Long categoryUid) {
		this.categoryUid = categoryUid;
	}

	@Override
	public void optimizeInternal() {
		if (!isStringValid(keyword)) {
			keyword = null;
		}
	}
}
