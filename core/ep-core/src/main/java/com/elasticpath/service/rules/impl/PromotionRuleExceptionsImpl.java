/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.rules.PromotionRuleExceptions;

/**
 * This class holds exceptions to a rule, i.e. a map of sku codes and product categories that will be excluded from a particular action. This class
 * is passed to the PromotionRuleDelegate's action methods. Note that objects of this class are instantiated by Drools and therefore can't be created
 * through Spring.
 */
public class PromotionRuleExceptionsImpl extends AbstractEpServiceImpl implements PromotionRuleExceptions {

	/** Regular expression for parsing the exception string from RuleElement. */
	private static final Pattern EXCEPTION_STRING_PATTERN = Pattern.compile("CategoryCodes:(.*)ProductCodes:(.*)ProductSkuCodes:(.*)");

//	private static final Pattern UID_STRING_PATTERN = Pattern.compile("(\\d+),");

	private static final Pattern CODE_STRING_PATTERN = Pattern.compile("([^,]*),");

	private static final int CATEGORY_CODE_GROUPNUM = 1;

	private static final int PRODUCT_CODE_GROUPNUM = 2;

	private static final int SKU_CODE_GROUPNUM = 3;

	private final Map<String, Object> skuCodeMap = new HashMap<String, Object>();

	private final List<String> productCodeList = new ArrayList<String>();

	private final List<String> categoryCodeList = new ArrayList<String>();

	/**
	 * Returns true if the specified sku is a rule exception because it's sku code or category has been specified as an exception.
	 * 
	 * @param productSku the sku to check
	 * @return true if the sku has been specified as an exception
	 */
	public boolean isSkuExcluded(final ProductSku productSku) {
		boolean isExcluded = false;
		if (skuCodeMap.containsKey(productSku.getSkuCode())) {
			isExcluded = true;
		}

		if (!isExcluded) {
			// Check if this sku belongs to one of the products, which is a rule exception.
			for (final Iterator<String> productIter = productCodeList.iterator(); productIter.hasNext();) {
				if (productSku.getProduct().getCode().equals(productIter.next())) {
					isExcluded = true;
				}
			}
		}

		if (!isExcluded) {
			// Check if this sku belongs to one of the categories, which is a rule exception.
			for (final Iterator<String> categoryIdIter = categoryCodeList.iterator(); categoryIdIter.hasNext();) {
				if (productSku.getProduct().isInCategory(categoryIdIter.next())) {
					isExcluded = true;
				}
			}
		}

		return isExcluded;
	}

	/**
	 * Returns true if the specified product is a rule exception because it's product id or category id has been specified as an exception.
	 * 
	 * @param product the product to check
	 * @return true if the product has been specified as an exception
	 */
	public boolean isProductExcluded(final Product product) {
		boolean isExcluded = false;

		// Check if this product is in the list of product rule exceptions.
		for (final Iterator<String> productIter = productCodeList.iterator(); productIter.hasNext();) {
			if (product.getCode().equals(productIter.next())) {
				isExcluded = true;
			}
		}

		if (!isExcluded) {
			// Check if this product belongs to one of the categories, which is a rule exception.
			for (final Iterator<String> categoryIdIter = this.categoryCodeList.iterator(); categoryIdIter.hasNext();) {
				if (product.isInCategory(categoryIdIter.next())) {
					isExcluded = true;
				}
			}
		}
		return isExcluded;
	}

	/**
	 * Returns true if the specified category is a rule exception because it's category id has been specified as an exception.
	 * 
	 * @param category the category to check
	 * @return true if the category has been specified as an exception
	 */
	public boolean isCategoryExcluded(final Category category) {
		boolean isExcluded = false;

		// Check if this product belongs to one of the categories, which is a rule exception.
		for (final Iterator<String> categoryIdIter = this.categoryCodeList.iterator(); categoryIdIter.hasNext();) {
			if (category.getCode().equals(categoryIdIter.next())) {
				isExcluded = true;
			}
		}
		return isExcluded;
	}

	/**
	 * Return the instance of <code>PromotionRuleExceptions</code> from parsing the given string representation.
	 * 
	 * @param exceptionStr - the string representation of a list of <code>RuleException</code>s.
	 * @throws EpDomainException if an invalid exception string is passed in.
	 */
	public void populateFromExceptionStr(final String exceptionStr) throws EpDomainException {
		this.resetPromotionRuleExceptions();
		if (exceptionStr != null) {
			final Matcher strMatcher = EXCEPTION_STRING_PATTERN.matcher(exceptionStr);
			if (strMatcher.find()) {
				if (strMatcher.group(CATEGORY_CODE_GROUPNUM).length() > 0) {
					final Matcher cIdMatcher = CODE_STRING_PATTERN.matcher(strMatcher.group(CATEGORY_CODE_GROUPNUM));
					while (cIdMatcher.find()) {
						this.categoryCodeList.add(cIdMatcher.group(1));
					}
				}

				if (strMatcher.group(PRODUCT_CODE_GROUPNUM).length() > 0) {
					final Matcher cIdMatcher = CODE_STRING_PATTERN.matcher(strMatcher.group(PRODUCT_CODE_GROUPNUM));
					while (cIdMatcher.find()) {
						this.productCodeList.add(cIdMatcher.group(1));
					}
				}

				if (strMatcher.group(SKU_CODE_GROUPNUM).length() > 0) {
					final Matcher skuCodeMatcher = CODE_STRING_PATTERN.matcher(strMatcher.group(SKU_CODE_GROUPNUM));
					while (skuCodeMatcher.find()) {
						this.skuCodeMap.put(skuCodeMatcher.group(1), null);
					}
				}
			} else {
				throw new EpDomainException("Invalid promotion rule exception string.");
			}
		}
	}

	private void resetPromotionRuleExceptions() {
		this.categoryCodeList.clear();
		this.productCodeList.clear();
		this.skuCodeMap.clear();
	}
}
