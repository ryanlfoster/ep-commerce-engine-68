package com.elasticpath.domain.pricing.impl;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import com.elasticpath.domain.pricing.PriceListStack;

/**
 * Ordered list of Price list guids managed by a list.
 */
public class PriceListStackImpl implements PriceListStack {
	
	/** Serial version id. */
	private static final long serialVersionUID = 20090909L;

	private List <String> stack = new ArrayList <String>();
	private Currency currency; 
		
	/**
	 * @param plGuid price list to add
	 */
	public void addPriceList(final String plGuid) {
		getPriceListStack().add(plGuid);
	}

	/**
	 * @param stack the stack to set
	 */
	public void setStack(final List<String> stack) {
		this.stack = stack;
	}

	/**
	 * @return the stack
	 */
	public List<String> getPriceListStack() {
		return stack;
	}

	/**
	 * @return the currency of the price lists in this stack
	 */
	public Currency getCurrency() {
		return currency;
	}
	
	
	/**
	 * @param currency the currency of the price lists in this stack
	 */
	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

}
