package com.elasticpath.common.pricing.service;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * 
 * Extended base amount filter, that hold price ranges
 * search criteria. 
 * 
 * All search fields are optional:
 * price list description guid;
 * object type;
 * object guid, can hold only part of object guid;
 * lowest and highest price range;
 * quantity. 
 *
 */
public interface BaseAmountFilterExt extends BaseAmountFilter {
	
	/**
	 * @return the lowest price search criteria.
	 */
	BigDecimal getLowestPrice();
	
	/**
	 * @return the highest price search criteria.
	 */
	BigDecimal getHighestPrice();

	/** 
	 * @param lowestPrice lowest price search criteria.
	 */
	void setLowestPrice(BigDecimal lowestPrice);
	
	/** 
	 * @param highestPrice highest price search criteria.
	 */
	void setHighestPrice(BigDecimal highestPrice);
	
	/** 
	 * @return locale.
	 */
	Locale getLocale();

	/** 
	 * @param locale locale to use for enrich base amount within locale dependant product and sku information.
	 */
	void setLocale(Locale locale);
	
	/**
	 * @return how many records to return
	 *         You will get result rows at: startIndex, startIndex + 1, ..., startIndex + limit - 1
	 */	
	int getLimit();

	/**
	 * @param limit of result collection.
	 */	
	void setLimit(int limit);

	/**
	 * @return The offset or start position of the cursor.  Zero based.
	 *         You will get result rows at: startIndex, startIndex + 1, ..., startIndex + limit - 1
	 */
	int getStartIndex();

	/**
	 * @param startIndex the offset or start position of the cursor.  Zero based.
	 */
	void setStartIndex(int startIndex);
}
