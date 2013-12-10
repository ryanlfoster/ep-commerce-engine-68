/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.commons.exception.SCCMCurrencyMissingException;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Shipping cost calculation method that calculates the shipping cost as % of order total. It needs one parameter: the value of percentage of the
 * order total.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("orderTotalPercentageMethod")
@DataCache(enabled = false)
public class OrderTotalPercentageMethodImpl extends AbstractShippingCostCalculationMethodImpl {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/** Make sure the METHOD_TYPE value match the bean id used in the spring bean factory configuration. */
	private static final String METHOD_TYPE = "orderTotalPercentageMethod";

	private static final String METHOD_TEXT = "OrderTotalPercentageMethod_method_text";

	/** Set of keys required for this shipping cost calculation method. */
	private static final String[] PARAMETER_KEYS = new String[] { ShippingCostCalculationParametersEnum.PERCENTAGE_OF_ORDER_TOTOAL.getKey() };

	private static final BigDecimal PERCENT_CONVERT = new BigDecimal("100.0");

	/**
	 * Must be implemented by subclasses to return their type. Make sure this matches the subclass discriminator-value and the spring
	 * context bean id for this RuleAction implementation.
	 * 
	 * @return the kind of the action subclass.
	 */
	@Override
	@Transient
	protected String getMethodType() {
		return METHOD_TYPE;
	}

	/**
	 * Return an array of parameter keys required by this rule action.
	 * 
	 * @return the parameter key array
	 */
	@Transient
	public String[] getParameterKeys() {
		return PARAMETER_KEYS.clone();
	}

	/**
	 * Return the text representation of this method for display to the user.
	 * 
	 * @return the text representation
	 */
	@Transient
	public String getDisplayText() {
		return METHOD_TEXT;
	}

	/**
	 * Calculation the shipping cost for the given shoppingCart.
	 * 
	 * @param shoppingCart - the shoppingCart.
	 * @return the shippingCost for the given shoppingCart.
	 * 
	 * @throws SCCMCurrencyMissingException if a matching param value for the passed currency is not found or if currency is null.
	 */
	public Money calculateShippingCost(final ShoppingCart shoppingCart) throws SCCMCurrencyMissingException {
		return calculateShippingCost(shoppingCart.getAllItems(), shoppingCart.getCurrency());
	}

	@Override
	public Money calculateShippingCost(final Collection< ? extends ShoppingItem> shoppingItems, final Currency currency)
		throws SCCMCurrencyMissingException {
		//not currency specific ...
		final BigDecimal percentage = new BigDecimal(
				this.getParamValue(ShippingCostCalculationParametersEnum.PERCENTAGE_OF_ORDER_TOTOAL.getKey()));

		return MoneyFactory.createMoney(percentage.multiply(getShippableItemsSubtotal(shoppingItems)).
				divide(PERCENT_CONVERT, 2, BigDecimal.ROUND_HALF_UP), currency);
	}
}
