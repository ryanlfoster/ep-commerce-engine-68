package com.elasticpath.service.search.query;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.service.search.AbstractSearchCriteriaImpl;
import com.elasticpath.service.search.CatalogAwareSearchCriteria;
import com.elasticpath.service.search.IndexType;


/**
 * A criteria for advanced category search.
 */
public class CategorySearchCriteria extends AbstractSearchCriteriaImpl implements CatalogAwareSearchCriteria {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	private String categoryCode;

	private String categoryName;

	private boolean categoryNameExact = false;

	private boolean activeOnly;

	private boolean inActiveOnly;

	private String ancestorCode;
	
	private boolean displayableOnly = false;

	private Set<String> catalogCodes;
	
	private Set<Locale> catalogSearchableLocales = new HashSet<Locale>();
	
	private Boolean searchLinkedCategories;

	/**
	 * Gets the category code.
	 * 
	 * @return the category code
	 */
	public String getCategoryCode() {
		return categoryCode;
	}

	/**
	 * Sets the category code.
	 * 
	 * @param categoryCode the category code
	 */
	public void setCategoryCode(final String categoryCode) {
		this.categoryCode = categoryCode;
	}

	/**
	 * Gets the category name.
	 * 
	 * @return the category name
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * Sets the category name.
	 * 
	 * @param categoryName the category name
	 */
	public void setCategoryName(final String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * Gets whether the category name should be matched exactly (untokenized). Does not modify
	 * fuzzy searches.
	 * 
	 * @return whether the category name should be matched exactly (untokenized)
	 */
	public boolean isCategoryNameExact() {
		return categoryNameExact;
	}

	/**
	 * Sets whether the category name should be matched exactly (untokenized). Does not modify
	 * fuzzy searches.
	 * 
	 * @param categoryNameExact whether the category name should be matched exactly (untokenized)
	 */
	public void setCategoryNameExact(final boolean categoryNameExact) {
		this.categoryNameExact = categoryNameExact;
	}

	/**
	 * Returns <code>true</code> if searching only for active categories.
	 * 
	 * @return <code>true</code> if searching only for active categories
	 */
	public boolean isActiveOnly() {
		return this.activeOnly;
	}

	/**
	 * Sets the active-only flag to <code>true</code> if only searching for active categories.
	 * 
	 * @param activeOnlyFlag the active-only flag
	 */
	public void setActiveOnly(final boolean activeOnlyFlag) {
		this.activeOnly = activeOnlyFlag;
	}

	/**
	 * Returns <code>true</code> if searching only for inactive categories.
	 * 
	 * @return <code>true</code> if searching only for in active categories
	 */
	public boolean isInActiveOnly() {
		return this.inActiveOnly;
	}

	/**
	 * Sets the inactive-only flag to <code>true</code> if only searching for inactive
	 * categories.
	 * 
	 * @param inActiveOnlyFlag the inactive-only flag
	 */
	public void setInActiveOnly(final boolean inActiveOnlyFlag) {
		this.inActiveOnly = inActiveOnlyFlag;
	}
	
	/**
	 * Returns the ancestor Code.
	 * 
	 * @return the ancestor Code
	 */
	public String getAncestorCode() {
		return ancestorCode;
	}

	/**
	 * Sets the ancestor Code.
	 * 
	 * @param ancestorCode the ancestor Code
	 */
	public void setAncestorCode(final String ancestorCode) {
		this.ancestorCode = ancestorCode;
	}

	/**
	 * Optimizes a search criteria by removing unnecessary information.
	 */
	public void optimize() {
		if (!isStringValid(categoryCode)) {
			categoryCode = null;
		}
		if (!isStringValid(categoryName)) {
			categoryName = null;
		}
	}
	
	/**
	 * Returns the index type this criteria deals with.
	 * @return the index type this criteria deals with.
	 */
	public IndexType getIndexType() {
		return IndexType.CATEGORY;
	}
	
	
	/**
	 * Gets whether to search for displayable categories only. Displayable categories are:
	 * <ul>
	 * <li>Not hidden</li>
	 * <li>Current date falls within a product's start date and end date (if an end date is
	 * defined)</li>
	 * </ul>
	 * 
	 * @return whether to search for displayable products only
	 */
	public boolean isDisplayableOnly() {
		return displayableOnly;
	}
	
	/**
	 * Sets whether to search for displayable categories only. Displayable categories are:
	 * <ul>
	 * <li>Not hidden</li>
	 * <li>Current date falls within a product's start date and end date (if an end date is
	 * defined)</li>
	 * </ul>
	 * 
	 * @param displayableOnly whether to search for displayable products only
	 */
	public void setDisplayableOnly(final boolean displayableOnly) {
		this.displayableOnly = displayableOnly;
	}
	
	/**
	 * Gets the catalog codes to search for.
	 *
	 * @return The catalog codes to search for.
	 */
	public Set<String> getCatalogCodes() {
		return catalogCodes;
	}
	
	/**
	 * Sets the catalog codes to search for.
	 *
	 * @param catalogCodes - The catalog codes to search for.
	 */
	public void setCatalogCodes(final Set<String> catalogCodes) {
		this.catalogCodes = catalogCodes;
	}
	
	public Set<Locale> getCatalogSearchableLocales() {
		return catalogSearchableLocales;
	}

	public void setCatalogSearchableLocales(final Set<Locale> catalogSearchableLocales) {
		this.catalogSearchableLocales = catalogSearchableLocales;
	}

	/**
	 * Returns <code>true</code> if this <code>CategorySearchCriteria</code> searches only for categories that are linked; returns
	 * <code>false</code> if this <code>CategorySearchCriteria</code> searches only for categories that are not linked; returns <code>null</code>
	 * if this <code>CategorySearchCriteria</code> searches for all categories.
	 * 
	 * @return <code>true</code> if this <code>CategorySearchCriteria</code> searches only for categories that are linked; returns
	 *         <code>false</code> if this <code>CategorySearchCriteria</code> searches only for categories that are not linked; returns
	 *         <code>null</code> if this <code>CategorySearchCriteria</code> searches for all categories
	 */
	public Boolean isLinked() {
		return searchLinkedCategories;
	}
	
	/**
	 * Set to <code>true</code> if this <code>CategorySearchCriteria</code> should only search for categories that are linked; set to
	 * <code>false</code> if this <code>CategorySearchCriteria</code> should only search for categories that are not linked; set to
	 * <code>null</code> if this <code>CategorySearchCriteria</code> should search for all categories.
	 * 
	 * @param isLinked Set to <code>true</code> if this <code>CategorySearchCriteria</code> should only search for categories that are linked;
	 *            set to <code>false</code> if this <code>CategorySearchCriteria</code> should only search for categories that are not linked;
	 *            set to <code>null</code> if this <code>CategorySearchCriteria</code> should search for all categories
	 */
	public void setLinked(final Boolean isLinked) {
		this.searchLinkedCategories = isLinked;
	}

	@Override
	public String getCatalogCode() {
		if (CollectionUtils.isNotEmpty(catalogCodes)) {
			return (String) CollectionUtils.get(catalogCodes, 0);
		}
		return null;
	}
	
	@Override
	public void setCatalogCode(final String catalogCode) {
		catalogCodes = new HashSet<String>();
		catalogCodes.add(catalogCode);
	}
}