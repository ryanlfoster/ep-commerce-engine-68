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
 * Shipping cost calculation method that calculates the shipping cost as fixBase + % of order total. It needs two parameters: the value of fixBase
 * and the value of percentage of the order total.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("fixedBaseAndCostPerUnitWeightMethod")
@DataCache(enabled = false)
public class FixedBaseAndCostPerUnitWeightMethodImpl extends AbstractShippingCostCalculationMethodImpl {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/** Make sure the METHOD_TYPE value match the bean id used in the spring bean factory configuration. */
	private static final String METHOD_TYPE = "fixedBaseAndCostPerUnitWeightMethod";

	private static final String METHOD_TEXT = "FixedBaseAndCostPerUnitWeightMethod_method_text";

	/** Set of keys required for this shipping cost calculation method. */
	private static final String[] PARAMETER_KEYS = new String[] { ShippingCostCalculationParametersEnum.FIXED_BASE.getKey(),
			ShippingCostCalculationParametersEnum.COST_PER_UNIT_WEIGHT.getKey() };

	/**
	 * Must be implemented by subclasses to return their type. Make sure this matches the hibernate subclass discriminator-value and the spring
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
	 * @throws SCCMCurrencyMissingException if a matching param value for the passed currency is not found or if currency is null.
	 */
	public Money calculateShippingCost(final ShoppingCart shoppingCart) throws SCCMCurrencyMissingException {
		return calculateShippingCost(shoppingCart.getAllItems(), shoppingCart.getCurrency());
	}

	@Override
	public Money calculateShippingCost(final Collection< ? extends ShoppingItem> shoppingItems, final Currency currency)
		throws SCCMCurrencyMissingException {
		Money shippingCost = MoneyFactory.createMoney(BigDecimal.ZERO, currency);
		if (!shoppingItems.isEmpty() && hasShippableItem(shoppingItems)) {
			//Get the fixed base cost
			final BigDecimal fixedBase = new BigDecimal(this.getParamValue(ShippingCostCalculationParametersEnum.FIXED_BASE.getKey(), currency));
			//Get the cost per unit weight
			final BigDecimal costPerUnitWeight = new BigDecimal(this.getParamValue(
					ShippingCostCalculationParametersEnum.COST_PER_UNIT_WEIGHT.getKey(), currency));
			shippingCost = MoneyFactory.createMoney(
					calculateShippingCost(fixedBase, costPerUnitWeight, calculateTotalWeight(shoppingItems)), currency);
		}
		return shippingCost;
	}

	/**
	 * Checks if in the list of line items there is at least one shippable product.
	 */
	private boolean hasShippableItem(final Collection< ? extends ShoppingItem> shoppingItems) {
		for (ShoppingItem item : shoppingItems) {
			if (item.getProductSku().isShippable()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Calculates a shipping cost based on a cost per unit weight and a base cost.
	 * This implementation adds the base cost to the total weight-adjusted cost.
	 * @param fixedBaseCost the fixed base cost
	 * @param costPerUnitWeight the cost per unit weight
	 * @param weight the weight
	 * @return the shipping cost
	 */
	BigDecimal calculateShippingCost(final BigDecimal fixedBaseCost, final BigDecimal costPerUnitWeight, final BigDecimal weight) {
		return fixedBaseCost.add(costPerUnitWeight.multiply(weight));
	}
}
