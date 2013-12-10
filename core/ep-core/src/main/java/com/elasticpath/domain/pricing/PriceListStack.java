package com.elasticpath.domain.pricing;

import java.io.Serializable;
import java.util.Currency;
import java.util.List;

/**
 * An ordered stack of Price list guids, used for price lookup in the given order.
 */
public interface PriceListStack extends Serializable {
	/**
	 * @param plGuid price list to add
	 */
	void addPriceList(final String plGuid);

	/**
	 * @param stack the stack to set
	 */
	void setStack(final List<String> stack);

	/**
	 * @return the stack
	 */
	List<String> getPriceListStack();
	
	/**
	 * @return currency of the price lists in the stack
	 */
	Currency getCurrency();
	
	/**
	 * @param currency the currency of the price lists in this stack
	 */
	void setCurrency(Currency currency);
}
