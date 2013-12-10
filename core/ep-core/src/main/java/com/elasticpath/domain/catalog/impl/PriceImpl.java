/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceTier;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;

/**
 * The implementation of <code>Price</code>.
 */
public  class PriceImpl extends AbstractEpDomainImpl implements Price {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private Currency currency;

	private Map<Integer, PriceTier> priceTiers;
	
	private SortedMap<Integer, PriceTier> sortedPriceTiers;
	
	private PricingScheme pricingScheme;
	
	/**
	 * Get the product's list price.
	 *
	 * @param qty the quantity of the product
	 * @return the list price as a <code>MoneyImpl</code>
	 */
	public Money getListPrice(final int qty) {
		Money money = null;
		BigDecimal listPrice = null;

		PriceTier priceTier = getPriceTierByQty(qty);
		if (priceTier != null) {
			listPrice = priceTier.getListPrice();
			if (listPrice != null) {
				money = MoneyFactory.createMoney(listPrice, getCurrency());
			}
		}

		return money;
	}

	/**
	 * Get the product's list price of the first price tier.
	 *
	 * @return the list price as a <code>MoneyImpl</code>
	 */
	public Money getListPrice() {
		return getListPrice(getFirstPriceTierMinQty());
	}

	/**
	 * Set the product's list price.
	 *
	 * @param listPrice the product's list price as a <code>MoneyImpl</code>
	 * @param minQty the minimum quantity of the <code>PriceTier</code>
	 */
	public void setListPrice(final Money listPrice, final int minQty) {
		if (listPrice == null) {
			throw new EpDomainException("Invalid list price");
		}
		checkCurrencyMatch(listPrice.getCurrency());
		setCurrency(listPrice.getCurrency());

		PriceTier priceTier = getPriceTierByExactMinQty(minQty);

		if (priceTier == null) {
			priceTier = initializePriceTier();
			priceTier.setListPrice(listPrice.getAmount());
			priceTier.setMinQty(minQty);
			addOrUpdatePriceTier(priceTier);
		} else {
			priceTier.setListPrice(listPrice.getAmount());
		}
	}

	/**
	 * Initialize the price tier.
	 *
	 * @return the initialized price tier object
	 */
	protected PriceTier initializePriceTier() {
		PriceTier priceTier = new PriceTierImpl();
		priceTier.initialize();
		return new PriceTierImpl();
	}

	/**
	 * Get the Price tier with the same minimum quantity with the inputed min Qty.
	 *
	 * @param minQty the minimum quantity to be matched
	 * @return the matched <code>PriceTier</code>
	 */
	public PriceTier getPriceTierByExactMinQty(final int minQty) {
		if (this.getPersistentPriceTiers() != null) {
			return this.getPersistentPriceTiers().get(Integer.valueOf(minQty));
		}
		return null;
	}

	@Override
	public PriceTier getPriceTierByQty(final int qty) {
		PriceTier nearestTier = null;
		if (this.getPriceTiers() != null) {
			for (PriceTier priceTier : this.getPriceTiers().values()) { // the priceTiers set should be sorted by ascending qty
				if (priceTier.getMinQty() <= qty) {
					nearestTier = priceTier;
				}
			}
		}
		return nearestTier;
	}

	/**
	 * Get the minimum quantity of first price tier.
	 *
	 * @return the minimum quantity of first price tier return 1 if no price tier has been set.
	 */
	public int getFirstPriceTierMinQty() {
		int firstMinQty = 1;
		if (this.getPriceTiers() != null) {
			for (PriceTier priceTier : this.getPriceTiers().values()) {
				firstMinQty = priceTier.getMinQty();
				break;
			}
		}
		return firstMinQty;
	}

	/**
	 * Set the product's list price for the first price tier.
	 *
	 * @param listPrice the product's list price as a <code>MoneyImpl</code>
	 */
	public void setListPrice(final Money listPrice) {
		int firstTierMinQty = getFirstPriceTierMinQty();
		setListPrice(listPrice, firstTierMinQty);
	}

	/**
	 * Get the product's sale price.
	 *
	 * @param qty the quantity of the product
	 * @return the sale price as a <code>MoneyImpl</code>
	 */
	public Money getSalePrice(final int qty) {

		Money money = null;
		BigDecimal salePrice = null;

		PriceTier priceTier = getPriceTierByQty(qty);

		if (priceTier != null) {
			salePrice = priceTier.getSalePrice();
			if (salePrice != null) {
				money = MoneyFactory.createMoney(salePrice, getCurrency());
			}
		}

		return money;
	}

	/**
	 * Get the product's sale price of the first price tier.
	 *
	 * @return the sale price as a <code>MoneyImpl</code>
	 */
	public Money getSalePrice() {
		return getSalePrice(getFirstPriceTierMinQty());
	}

	/**
	 * Set the product's sale price.
	 *
	 * @param salePrice the product's sale price as a <code>MoneyImpl</code>
	 * @param minQty the minimum quantity of the <code>PriceTier</code>
	 */
	public void setSalePrice(final Money salePrice, final int minQty) {
		if (salePrice == null) {
			throw new EpDomainException("Invalid sale price");
		}
		checkCurrencyMatch(salePrice.getCurrency());
		setCurrency(salePrice.getCurrency());

		PriceTier priceTier = getPriceTierByExactMinQty(minQty);
		// if (priceTier == null) {
		// throw new EpDomainException("No price tier found for quantity : " + minQty);
		// }
		if (priceTier == null) {
			priceTier = initializePriceTier();
			priceTier.setSalePrice(salePrice.getAmount());
			priceTier.setMinQty(minQty);
			addOrUpdatePriceTier(priceTier);
		} else {
			priceTier.setSalePrice(salePrice.getAmount());
		}
	}

	/**
	 * Set the product's sale price for the first price tier.
	 *
	 * @param salePrice the product's sale price as a <code>MoneyImpl</code>
	 */
	public void setSalePrice(final Money salePrice) {
		int firstTierMinQty = getFirstPriceTierMinQty();
		setSalePrice(salePrice, firstTierMinQty);
	}

	/**
	 * Get the product's computed price (e.g. the result of executing a rule).
	 *
	 * @param qty the quantity of the product
	 * @return the computed price as a <code>MoneyImpl</code> Returns null if no computed price has been set
	 */
	public Money getComputedPrice(final int qty) {
		Money money = null;
		BigDecimal computedPrice = null;

		PriceTier priceTier = getPriceTierByQty(qty);

		if (priceTier != null) {
			computedPrice = priceTier.getComputedPrice();
			if (computedPrice != null) {
				money = MoneyFactory.createMoney(computedPrice, getCurrency());
			}
		}

		return money;
	}

	/**
	 * Get the product's computed price of the first price tier.
	 *
	 * @return the computed price as a <code>MoneyImpl</code>
	 */
	public Money getComputedPrice() {
		return getComputedPrice(getFirstPriceTierMinQty());
	}

	/**
	 * Set the product's computed price (e.g. the result of executing a rule). A computed price cannot be less than zero. If a negative price is
	 * specified, the computed price will be set to zero.
	 *
	 * @param computedPrice the product's computed price as a <code>MoneyImpl</code>
	 * @param minQty the minimum quantity of the <code>PriceTier</code>
	 */
	public void setComputedPrice(final Money computedPrice, final int minQty) {
		if (computedPrice == null) {
			throw new EpDomainException("Invalid computed price");
		}
		checkCurrencyMatch(computedPrice.getCurrency());

		setCurrency(computedPrice.getCurrency());
		PriceTier priceTier = getPriceTierByExactMinQty(minQty);
		priceTier.setComputedPrice(computedPrice.getAmount());

	}

	/**
	 * Set the computed price for the first price tier.
	 *
	 * @param computedPrice the computed price as a <code>MoneyImpl</code>
	 */
	public void setComputedPrice(final Money computedPrice) {
		int firstTierMinQty = getFirstPriceTierMinQty();
		setComputedPrice(computedPrice, firstTierMinQty);
	}

	/**
	 * Removes the computed price for all price tier.
	 */
	public void clearComputedPrice() {
		for (PriceTier priceTier : priceTiers.values()) {
			priceTier.clearComputedPrice();
		}
	}

	/**
	 * Get the lowest of the price values specified in this <code>Price</code>.
	 *
	 * @param qty the quantity of the product
	 * @return the lowest price as a <code>MoneyImpl</code>
	 */
	public Money getLowestPrice(final int qty) {
		Money lowestPrice = this.getListPrice(qty);

		final Money salePrice = this.getSalePrice(qty);
		if (salePrice != null && salePrice.lessThan(lowestPrice)) {
			lowestPrice = salePrice;
		}

		final Money computedPrice = this.getComputedPrice(qty);
		if (computedPrice != null && computedPrice.lessThan(lowestPrice)) {
			lowestPrice = computedPrice;
		}

		return lowestPrice;
	}

	/**
	 * Get the lowest of the price values of the first price tier.
	 *
	 * @return the lowest price as a <code>MoneyImpl</code>
	 */
	public Money getLowestPrice() {
		return getLowestPrice(getFirstPriceTierMinQty());
	}

	/**
	 * Get the currency for this product price.
	 *
	 * @return the <code>Currency</code>
	 */
	public Currency getCurrency() {
		if (this.currency == null && pricingScheme != null) {
			return pricingScheme.getCurrency();
		}
		return this.currency;
	}

	/**
	 * Set the currency for the price.
	 *
	 * @param currency of the price as a <code>Currency</code>
	 */
	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	/**
	 * Checks that a new price matches any previously set currency. If it does not match, an exception is thrown because this price could be
	 * incorrectly mapped by the previous currency.
	 *
	 * @param newCurrency the new currency to check
	 */
	private void checkCurrencyMatch(final Currency newCurrency) {
		if (getCurrency() != null && !newCurrency.equals(getCurrency())) {
			throw new EpDomainException("New price does not match previously set currency");
		}
	}

	@Override
	public SortedMap<Integer, PriceTier> getPriceTiers() {	
		if (sortedPriceTiers == null) {
			updateSortedPriceTiers();
		}
		return sortedPriceTiers;
	}
	
	
	@Override
	public void updateSortedPriceTiers() {
		if (getPersistentPriceTiers() == null) {
			this.sortedPriceTiers = null;
		} else {
			this.sortedPriceTiers = new TreeMap<Integer, PriceTier>(getPersistentPriceTiers());
		}
	}

	
	/**
	 * This method should be implemented in subclass, as the member variable is defined in this class,
	 * so we have not make this method abstract.
	 * @return a map of price tiers for this product price as a <code>PriceTierImpl</code>
	 */
	public Map<Integer, PriceTier> getPersistentPriceTiers() {
		return priceTiers;
	}
	

	@Override
	public void setPersistentPriceTiers(final Map<Integer, PriceTier> priceTiers) {
		this.priceTiers = priceTiers;
		
		//Should access the variable directly rather than use get/set method. This is to avoid cyclic JPA calls.
		//this.updateSortedPriceTiers();
		if (priceTiers == null) {
			this.sortedPriceTiers = null;
		} else {
			this.sortedPriceTiers = new TreeMap<Integer, PriceTier>(priceTiers);
		}
	}


	/**
	 * Check if the product has price tiers.
	 *
	 * @return true if the product has more than one price tier, or no quantity one price tier
	 */
	public boolean hasPriceTiers() {
		// more than one price tier
		if (priceTiers != null && priceTiers.size() > 1) {
			return true;
		}
		
		// non-quantity-one price tier
		if (priceTiers != null && !priceTiers.isEmpty() && Collections.min(priceTiers.keySet()) > 1) {
			return true;
		}
		
		return false;
	}

	/**
	 * Set default values for those fields need default values.
	 */
	@Override
	public void initialize() {
		if (this.priceTiers == null) {
			this.priceTiers = new HashMap<Integer, PriceTier>();
		}
	}

	/**
	 * Check if the lowest price is less than the list price, i.e. the price has a discount.
	 *
	 * @return true if the price has a lower price than the list price.
	 */
	public boolean isLowestLessThanList() {
		final Money lowestPrice = getLowestPrice();
		if (lowestPrice == null) {
			return false;
		}
		return lowestPrice.lessThan(getListPrice());
	}

	/**
	 * Check if the lowest price is less than the list price, i.e. the price has a discount.
	 *
	 * @param qty the quantity of the product
	 * @return true if the price has a lower price than the list price.
	 */
	public boolean isLowestLessThanList(final int qty) {
		final Money lowestPrice = getLowestPrice(qty);
		if (lowestPrice == null) {
			return false;
		}
		return lowestPrice.lessThan(getListPrice(qty));
	}

	/**
	 * Calculates the <code>MoneyImpl</code> savings if the price has a discount.
	 *
	 * @return the price savings as a <code>MoneyImpl</code>
	 */
	public Money getDollarSavings() {
		final Money listPrice = getListPrice();
		if (listPrice == null) {
			return null;
		}
		return listPrice.subtract(getLowestPrice());
	}

	/**
	 * Calculates the <code>MoneyImpl</code> savings if the price has a discount.
	 *
	 * @param qty the quantity of the product
	 * @return the price savings as a <code>MoneyImpl</code>
	 */
	public Money getDollarSavings(final int qty) {
		if (qty >= getFirstPriceTierMinQty()) {
			return getListPrice(qty).subtract(getLowestPrice(qty));
		}
		
		return null;
	}

	/**
	 * Get the pre-promotion price of the first price tier to which promotions are to be applied. This is currently the lower of the sale price and
	 * the list price.
	 *
	 * @return a <code>Money</code> object representing the pre-promotion price
	 */
	public Money getPrePromotionPrice() {
		Money prePromotionPrice = getSalePrice();
		if (prePromotionPrice == null) {
			prePromotionPrice = getListPrice();
		}
		return prePromotionPrice;
	}

	/**
	 * Get the pre-promotion price of the corresponding price tier, which promotions are to be applied. This is currently the lower of the sale price
	 * and the list price.
	 *
	 * @param qty the quantity of the product
	 * @return a <code>Money</code> object representing the pre-promotion price
	 */
	public Money getPrePromotionPrice(final int qty) {
		Money prePromotionPrice = getSalePrice(qty);
		if (prePromotionPrice == null) {
			prePromotionPrice = getListPrice(qty);
		}
		return prePromotionPrice;
	}
	
	/**
	 * add the price Tiers.
	 *
	 * @param priceTier the price tier to be added
	 */
    public void addOrUpdatePriceTier(final PriceTier priceTier) {
        if (this.getPersistentPriceTiers() == null) {
                this.setPersistentPriceTiers(new HashMap<Integer, PriceTier>());
        }
        this.getPersistentPriceTiers().put(Integer.valueOf(priceTier.getMinQty()), priceTier);
        this.updateSortedPriceTiers();
    }
    
	@Override
	public String toString() {
		ToStringBuilder priceBuilder = new ToStringBuilder(this, ToStringStyle.NO_FIELD_NAMES_STYLE);
		
		if (priceTiers != null) {
			for (Integer i : priceTiers.keySet()) {
				PriceTier tier = priceTiers.get(i);
				
				if (tier != null) {
					ToStringBuilder tierBuilder = new ToStringBuilder(tier, ToStringStyle.MULTI_LINE_STYLE);
					tierBuilder.append("minQty", tier.getMinQty());
					tierBuilder.append("listPrice", tier.getListPrice());
					tierBuilder.append("salePrice", tier.getSalePrice());
					tierBuilder.append("prePromotionPrice", tier.getPrePromotionPrice());
					tierBuilder.append("computedPrice", tier.getComputedPrice());
				
					priceBuilder.append(String.valueOf(i), tierBuilder.toString());
				}
			}
		}
		
		return priceBuilder.toString();
	}

	@Override
	public PricingScheme getPricingScheme() {
		return this.pricingScheme;
	}

	@Override
	public void setPricingScheme(final PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
	}

	
	@Override
	public Set<Integer> getPriceTiersMinQuantities() {
		SortedMap<Integer, PriceTier> priceTiers = getPriceTiers();
		if (priceTiers == null) {
			return Collections.emptySet();
		}
		
		return priceTiers.keySet();
	}
	
}

