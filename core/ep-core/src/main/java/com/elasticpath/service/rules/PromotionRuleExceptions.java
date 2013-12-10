/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.rules;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.EpService;

/**
 * This class holds exceptions to a rule, i.e. a map of sku codes and
 * product categories that will be excluded from a particular action.
 * This class is passed to the PromotionRuleDelegate's action methods.
 */
public interface PromotionRuleExceptions extends EpService {
	// Constants used to compose the exceptions string from RuleElement.
	// It will be in the format of "CategoryIds:1,2,ProductIds:3,4,ProductSkuCodes:A001,B002,".
	/** Category UIDs. */
	String CATEGORY_CODES = "CategoryCodes:";
	
	/** Product UID. */
	String PRODUCR_CODES = "ProductCodes:";
	
	/** Product SKU codes. */
	String PRODUCTSKU_CODES = "ProductSkuCodes:";
	
	/** Exception String Separator. */
	char EXCEPTION_STRING_SEPARATOR = ',';
	
	/**
	 * Returns true if the specified sku is a rule exception because it's
	 * sku code, product id or category id has been specified as an exception.
	 * @param productSku the sku to check
	 * @return true if the sku has been specified as an exception
	 */
	boolean isSkuExcluded(final ProductSku productSku);
	
	/**
	 * Returns true if the specified product is a rule exception because it's
	 * product id or category id has been specified as an exception.
	 * @param product the product to check
	 * @return true if the product has been specified as an exception
	 */
	boolean isProductExcluded(final Product product);
	
	/**
	 * Returns true if the specified category is a rule exception because it's
	 * category id has been specified as an exception.
	 * @param category the category to check
	 * @return true if the category has been specified as an exception
	 */
	boolean isCategoryExcluded(final Category category);
	
	/**
	 * Return the instance of <code>PromotionRuleExceptions</code> from parsing the given string representation.
	 * @param exceptionStr  - the string representation of a list of <code>RuleException</code>s.\
	 * @throws EpDomainException if an invalid exception string is passed in.
	 */
	void populateFromExceptionStr(final String exceptionStr) throws EpDomainException;
}
