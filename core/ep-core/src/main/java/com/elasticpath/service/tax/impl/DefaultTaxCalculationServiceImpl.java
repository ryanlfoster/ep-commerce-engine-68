package com.elasticpath.service.tax.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.tax.TaxCategory;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.domain.tax.TaxJurisdiction;
import com.elasticpath.domain.tax.TaxRegion;
import com.elasticpath.service.impl.AbstractEpServiceImpl;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.TaxCalculationResult;
import com.elasticpath.service.tax.TaxCalculationService;
import com.elasticpath.service.tax.TaxJurisdictionService;

/**
 * Provides a default implementation of <code>TaxCalculationService</code> that 
 * calculates tax rates using the <code>TaxJurisdictionService</code>.
 * All calculations are performed and returned on BigDecimals to a scale of 4 to
 * prevent rounding issues.
 * 
 */
public class DefaultTaxCalculationServiceImpl extends AbstractEpServiceImpl implements TaxCalculationService {

	/** Used to convert the percentage value into decimal value. */
	private static final BigDecimal PERCENT_CONVERT = new BigDecimal("100.0");
	
	/** The scale to be used in the calculation. Note: All the tax value to be return to the user should only be kept in two decimal places. */
	private static final int CALCULATION_SCALE = 10;

	/** All calculations in this class should use this mode when rounding. */
	private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
	
	private TaxJurisdictionService taxJurisdictionService;
	private StoreService storeService;

	@Override
	public TaxCalculationResult calculateTaxes(final String storeCode, final Address address, final Currency currency, final Money shippingCost,
			final Collection< ? extends ShoppingItem> shoppingItems, final Money preTaxDiscount) {

		// obtain a new, empty TaxCalculationResult
		TaxCalculationResult taxCalculationResult = getBean(ContextIdNames.TAX_CALCULATION_RESULT);
		
		return calculateTaxesAndAddToResult(taxCalculationResult, storeCode, address, currency, shippingCost, shoppingItems, preTaxDiscount);
	}

	/**
	 * 
	 * Calculates the applicable taxes on a list of items, depending on the address to which they are being billed or shipped,
	 * and adds them to those in the given TaxCalculationResult object.
	 *
	 *
	 * @param taxCalculationResult the tax calculation result to be used to add up the taxes to
	 * @param storeCode guid of the store that will be used to retrieve tax jurisdictions
	 * @param address the address to use for tax calculations. If null, no calculations will be performed.
	 * @param currency the currency to use for tax calculations, must be non-null
	 * @param shippingCost the cost of shipping, so that shipping taxes can be factored in, must be non-null
	 * @param shoppingItems list of items that must be taxed, must be non-null
	 * @param preTaxDiscount the total pre-tax discount to be applied on items, before taxes are calculated, must be non-null
	 * @return the result of the tax calculations
	 * @throws EpServiceException if a required parameter is null
	 */
	public TaxCalculationResult calculateTaxesAndAddToResult(final TaxCalculationResult taxCalculationResult, final String storeCode,
			final Address address, final Currency currency, final Money shippingCost, final Collection< ? extends ShoppingItem> shoppingItems, 
			final Money preTaxDiscount) {
		if (currency == null || shippingCost == null || shoppingItems == null || preTaxDiscount == null) {
			throw new EpServiceException("Required parameter is null");
		}

		initDefaultValues(taxCalculationResult, currency);
		final Store store = getStoreService().findStoreWithCode(storeCode);
		final TaxJurisdiction taxJurisdiction = findTaxJurisdiction(store, address);
		
		taxCalculationResult.setTaxInclusive(isInclusiveTaxCalculationInUse(taxJurisdiction));
		
		Set<String> activeTaxCodes = getActiveTaxCodeNames(store);
		calculateShippingCostTaxAndBeforeTaxPrice(activeTaxCodes, shippingCost, currency, taxJurisdiction, taxCalculationResult);
		calculateCartItemsTaxesAndBeforeTaxPrices(activeTaxCodes, shoppingItems, preTaxDiscount, currency, taxJurisdiction, taxCalculationResult);
		return taxCalculationResult;
	}

	/**
	 * Get a set of the names of the <code>TaxCode</code>s which are active in the given <code>Store</code>.
	 */
	private Set<String> getActiveTaxCodeNames(final Store store) {
		Set <String> activeTaxCodes = new HashSet<String>();
		if (store.getTaxCodes() != null) {
			for (TaxCode taxCode : store.getTaxCodes()) {
				activeTaxCodes.add(taxCode.getCode());
			}
		}
		return activeTaxCodes;
	}

	/**
	 * Retrieves a tax jurisdiction for the specified address and store.
	 * 
	 * @param store the store to use
	 * @param address the address to use
	 * @return an instance of a {@link TaxJurisdiction} or null if none found
	 */
	protected TaxJurisdiction findTaxJurisdiction(final Store store, final Address address) {
		if (store != null && address != null) {
			return this.getTaxJurisdictionService().retrieveEnabledInStoreTaxJurisdiction(store, address);
		}
		return null;
	}

	/**
	 * Initializes the tax calculation result instance.
	 */
	private void initDefaultValues(final TaxCalculationResult taxCalculationResult, final Currency currency) {
		taxCalculationResult.setDefaultCurrency(currency);
		
		if (taxCalculationResult.getBeforeTaxShippingCost() == null) {
			taxCalculationResult.setBeforeTaxShippingCost(getMoneyZero(currency));
		}
		
		if (taxCalculationResult.getBeforeTaxSubTotal() == null) {
			taxCalculationResult.setBeforeTaxSubTotal(getMoneyZero(currency));
		}
	}

	/**
	 * Calculate the shipping cost taxes. The provided taxCalculationResult object will be updated with before-tax shipping cost, and the tax values
	 * will be added to the relevant tax categories.
	 * 
	 * 
	 * @param activeTaxCodes the active tax codes in the system
	 * @param shippingCost the cost of shipping
	 * @param currency the currency of the tax calculation
	 * @param taxJurisdiction the tax jurisdiction
	 * @param taxCalculationResult the result object holding the result value
	 * 
	 */
	protected void calculateShippingCostTaxAndBeforeTaxPrice(final Set <String> activeTaxCodes,
			final Money shippingCost, final Currency currency, final TaxJurisdiction taxJurisdiction,
			final TaxCalculationResult taxCalculationResult) {

		Money allShippingTaxes = getMoneyZero(currency);
		if (activeTaxCodes.contains(TaxCode.TAX_CODE_SHIPPING) 
				&& (BigDecimal.ZERO.compareTo(shippingCost.getAmount()) < 0) 
				&& taxJurisdiction != null) {

			for (TaxCategory taxCategory : taxJurisdiction.getTaxCategorySet()) {
				for (TaxRegion taxRegion : taxCategory.getTaxRegionSet()) {
					BigDecimal decimalTaxRate = getDecimalTaxRate(taxRegion, TaxCode.TAX_CODE_SHIPPING);
					if (BigDecimal.ZERO != decimalTaxRate) {
						Money currentTax = getShippingTax(shippingCost, decimalTaxRate, taxJurisdiction, currency);
						taxCalculationResult.addShippingTax(currentTax);
						taxCalculationResult.addTaxValue(taxCategory, currentTax);
						
						allShippingTaxes = allShippingTaxes.add(currentTax);
					}
				}
			}
		}
		taxCalculationResult.addBeforeTaxShippingCost(
				getBeforeTaxValue(shippingCost, allShippingTaxes, taxCalculationResult.isTaxInclusive()));
	}

	private Money getShippingTax(
			final Money shippingCost, final BigDecimal decimalTaxRate, final TaxJurisdiction taxJurisdiction, final Currency currency) {
		if (isInclusiveTaxCalculationInUse(taxJurisdiction)) {
			BigDecimal sumOfTaxRates = getTotalDecimalTaxRate(TaxCode.TAX_CODE_SHIPPING, taxJurisdiction);
			BigDecimal shippingTax = getTaxIncludedInPrice(sumOfTaxRates, decimalTaxRate, shippingCost.getAmount());
			return MoneyFactory.createMoney(shippingTax, currency);
		} else {
			return calculateTax(shippingCost.getAmount(), decimalTaxRate, currency);
		}
	}

	/**
	 * Calculates the value of an item depending on the tax mode set (inclusive or exclusive).
	 */
	private Money getBeforeTaxValue(final Money cost, final Money taxValue, final boolean taxInclusive) {
		if (taxInclusive) {
			return cost.subtract(taxValue);
		}
		return cost;
	}

	/**
	 * From an amount that includes tax retrieve the amount that does not include tax (the before-tax amount).
	 * @param amountIncludingTax the amount including tax
	 * @param decimalTaxRate the rate of the tax included in the amount (expressed as a decimal, e.g. 7% tax as 0.07)
	 * @return the amount minus the tax
	 */
	BigDecimal getPretaxAmountFromInclusiveTaxAmount(final BigDecimal amountIncludingTax, final BigDecimal decimalTaxRate) {
		return amountIncludingTax.divide(decimalTaxRate.add(BigDecimal.ONE), CALCULATION_SCALE, ROUNDING_MODE);
	}	

	@Override
	public boolean isInclusiveTaxCalculationInUse(final String storeCode, final Address address) {
		Store store = getStoreService().findStoreWithCode(storeCode);
		TaxJurisdiction taxJurisdiction = getTaxJurisdictionService().retrieveEnabledInStoreTaxJurisdiction(store, address);
		return isInclusiveTaxCalculationInUse(taxJurisdiction);
	}

	/**
	 * Return true if the "inclusive" tax calculation method is in use; otherwise false. This is based on the specified <code>TaxJurisdiction</code>,
	 * which is based on the shipping address. If the taxJurisdiction is null, this method returns false by default.
	 * 
	 * @param taxJurisdiction the <code>TaxJurisdiction</code>
	 * @return true if the "inclusive" tax calculation method is in use; otherwise false.
	 */
	protected boolean isInclusiveTaxCalculationInUse(final TaxJurisdiction taxJurisdiction) {
		if (taxJurisdiction == null) {
			return false;
		}
		return taxJurisdiction.getPriceCalculationMethod().equals(TaxJurisdiction.PRICE_CALCULATION_INCLUSIVE);
	}


	
	/**
	 * Calculate the taxes for cart items.
	 * Only applies the tax if tax code of line item is active in the store.
	 * 
	 * @param activeTaxCodes the active tax codes in the system
	 * @param shoppingItems the collection of shopping cart items
	 * @param subtotalDiscount the amount of discount on the order
	 * @param currency the currency of the tax calculation
	 * @param taxJurisdiction the tax jurisdiction
	 * @param taxCalculationResult the result object holding the result value
	 */
	protected void calculateCartItemsTaxesAndBeforeTaxPrices(final Collection <String> activeTaxCodes, 
			final Collection< ? extends ShoppingItem> shoppingItems, final Money subtotalDiscount,
			final Currency currency, final TaxJurisdiction taxJurisdiction, final TaxCalculationResult taxCalculationResult) {
		DiscountApportioningCalculator discountCalculator = new DiscountApportioningCalculator();
		Map<String, BigDecimal> itemDiscounts = discountCalculator
				.apportionDiscountToShoppingItems(subtotalDiscount, shoppingItems);
		for (ShoppingItem shoppingItem : shoppingItems) {
			//On Order exchange wizard tax calculation may be invoked 
			//on item without unit price
			if (shoppingItem.getListUnitPrice() == null) {
				continue;
			}
			
			Money shoppingItemPrice = shoppingItem.getTotal();
			
			Money shoppingItemDiscount = getMoneyZero(currency);
			BigDecimal discount = itemDiscounts.get(shoppingItem.getGuid());
			if (discount != null) {
				shoppingItemDiscount = MoneyFactory.createMoney(discount, currency);
			}
			Money discountedShoppingItemPrice = shoppingItemPrice.subtract(shoppingItemDiscount);
			
			Money shoppingItemTaxes = getMoneyZero(currency);
			final String shoppingItemTaxCode = getTaxCodeFrom(shoppingItem);
			if (activeTaxCodes.contains(shoppingItemTaxCode)) {
				shoppingItemTaxes = calculateShoppingItemTaxes(shoppingItemTaxCode, currency, taxJurisdiction, taxCalculationResult, shoppingItem,
							discountedShoppingItemPrice);
			}
			taxCalculationResult.addItemTax(shoppingItem.getGuid(), shoppingItemTaxes);
			Money beforeTaxPrice = shoppingItemPrice;
			if (taxCalculationResult.isTaxInclusive()) {
				beforeTaxPrice = shoppingItemPrice.subtract(shoppingItemTaxes);
			}
			
			taxCalculationResult.addBeforeTaxItemPrice(beforeTaxPrice);
			
			//This line is somewhat odd because the beforeTaxPrice in the inclusive case is affected by the discount.
			//In future addBeforeTaxWithoutDiscount should just be removed.
			taxCalculationResult.addBeforeTaxWithoutDiscount(beforeTaxPrice);
		}
	}

 	/**
	 * Sum all taxes applicable to the given tax code in the given tax jurisdiction.
	 * @param itemTaxCode the tax code to find the rate for
	 * @param taxJurisdiction the jurisdiction to get the taxes from
	 * @return sum of all tax rates which apply to itemTaxCode in taxJurisdiction
	 */
	protected BigDecimal getTotalDecimalTaxRate(final String itemTaxCode, final TaxJurisdiction taxJurisdiction) {
		BigDecimal totalTaxRate = BigDecimal.ZERO;
		for (TaxCategory taxCategory : taxJurisdiction.getTaxCategorySet()) {
			for (TaxRegion taxRegion : taxCategory.getTaxRegionSet()) {
				totalTaxRate = totalTaxRate.add(getDecimalTaxRate(taxRegion, itemTaxCode));
			}
		}
		return totalTaxRate;
	}
	
	
	/**
	 * Gets a line item's tax code.
	 * @param shoppingItem the line item from which to retrieve a tax code
	 * @return the line item's tax code, or null if the line item has no ProductSku
	 */
	protected String getTaxCodeFrom(final ShoppingItem shoppingItem) {
		if (shoppingItem.getProductSku() != null) {
			return shoppingItem.getProductSku().getProduct().getTaxCode().getCode();
		}
		return null;
	}
	
	/**
	 * Get the tax rate for a given tax code in the given tax region, expressed
	 * as a decimal (e.g. a 7.5% tax represented as 0.0750).
	 * @param taxRegion the region in which the tax applies
	 * @param taxCode the code representing the tax category being applied
	 * @return the tax rate expressed as a decimal (e.g. 0.075 for a 7.5% tax), 
	 * or zero if one does not exist for the given inputs.
	 */
	BigDecimal getDecimalTaxRate(final TaxRegion taxRegion, final String taxCode) {
		BigDecimal taxRatePercentage = taxRegion.getTaxRate(taxCode);
		if (taxRatePercentage == null) {
			return BigDecimal.ZERO;
		}
		return taxRatePercentage.divide(PERCENT_CONVERT, CALCULATION_SCALE, RoundingMode.HALF_UP);
	}
	
	/**
	 * Calculates taxes for a single line item as well as storing and summing them. Returns the sum of
	 * all the valid taxes on the supplied line item. The taxCalculationResult will have the calculated
	 * tax values added, as will the line item itself. 
	 * 
	 * @param itemTaxCode the tax code of the line item
	 * @param currency the currency
	 * @param taxJurisdiction the tax jurisdiction to calculate the taxes in 
	 * @param taxCalculationResult the tax calculation result to store results in
	 * @param discountedShoppingItemPrice the discounted price of a line item
	 * @param shoppingItem the line item
	 * @return the sum of all the valid taxes on the supplied line item
	 */
	protected Money calculateShoppingItemTaxes(final String itemTaxCode, final Currency currency, final TaxJurisdiction taxJurisdiction,
			final TaxCalculationResult taxCalculationResult, final ShoppingItem shoppingItem, final Money discountedShoppingItemPrice) {
		
		Money shoppingItemTaxes = getMoneyZero(currency);
		if (itemTaxCode != null && taxJurisdiction != null) {
			for (TaxCategory taxCategory : taxJurisdiction.getTaxCategorySet()) {
				for (TaxRegion taxRegion : taxCategory.getTaxRegionSet()) {
					final BigDecimal decimalTaxRate = getDecimalTaxRate(taxRegion, itemTaxCode);
					if (BigDecimal.ZERO.compareTo(decimalTaxRate) < 0) {
					
						Money currentTax;
						
						if (isInclusiveTaxCalculationInUse(taxJurisdiction)) {
							// sum all tax rates applicable to this line item
							BigDecimal sumOfTaxRates = getTotalDecimalTaxRate(itemTaxCode, taxJurisdiction);
							BigDecimal tax = getTaxIncludedInPrice(sumOfTaxRates, decimalTaxRate, discountedShoppingItemPrice.getAmount());
							currentTax = MoneyFactory.createMoney(tax, currency);
							taxCalculationResult.addToTaxInItemPrice(currentTax);
						} else {
							currentTax = calculateTax(discountedShoppingItemPrice.getAmount(), decimalTaxRate, currency);
						}
						shoppingItemTaxes = shoppingItemTaxes.add(currentTax);
						taxCalculationResult.addTaxValue(taxCategory, currentTax);
					}
				}
			}
		}

		return shoppingItemTaxes;
	}
	
	/**
	 * Calculates the line item tax included in the tax inclusive price: (tax rate 1 x price) / 1 + sum(all tax rates).
	 *  
	 * @param sumOfTaxRates the sum of all tax rates included in the price, such as GST + PST
	 * @param decimalTaxRate the tax rate to calculate the price amount of
	 * @param discountedIncl the price of the line item, with discount already subtracted
	 * @return the tax amount included in the line item tax
	 */
	BigDecimal getTaxIncludedInPrice(
			final BigDecimal sumOfTaxRates, final BigDecimal decimalTaxRate, final BigDecimal discountedIncl) {
		
		// 1 + sum(all tax rates)
		BigDecimal div = sumOfTaxRates.add(BigDecimal.ONE);
		
		// (tax rate 1 x price) / div
		BigDecimal tax = decimalTaxRate.multiply(discountedIncl).divide(div, CALCULATION_SCALE, RoundingMode.HALF_UP);
		
		return tax;
	}
	
	/**
	 * Simple tax calculation, multiplies amount by tax rate and returns result as a Money object with the specified Currency.
	 *  
	 * @param amount the amount (it is an error for this to be null)
	 * @param decimalTaxRate the decimal tax rate. i.e. 0.05 for 5% tax rate. 
	 * @param currency the currency for the returned Money object
	 * @return result of tax calculation using the specified Currency
	 */
	protected Money calculateTax(final BigDecimal amount, final BigDecimal decimalTaxRate, final Currency currency) {
		return MoneyFactory.createMoney(
				amount.multiply(decimalTaxRate).setScale(CALCULATION_SCALE, RoundingMode.HALF_UP), currency);
	}
	
	/**
	 * Retrieves the <code>TaxJurisdictionService</code>.
	 * 
	 * @return the <code>TaxJurisdictionService</code>
	 */
	protected TaxJurisdictionService getTaxJurisdictionService() {
		return taxJurisdictionService;
	}

	/**
	 * Sets the <code>TaxJurisdictionService</code>.
	 * 
	 * @param taxJurisdictionService the <code>TaxJurisdictionService</code>
	 */
	public void setTaxJurisdictionService(final TaxJurisdictionService taxJurisdictionService) {
		this.taxJurisdictionService = taxJurisdictionService;
	}

	protected StoreService getStoreService() {
		return storeService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	/**
	 * Gets a money object of the amount zero.
	 * 
	 * @param currency The currency the money object should represent.
	 * @return a money object of value zero
	 */
	protected Money getMoneyZero(final Currency currency) {
		return MoneyFactory.createMoney(BigDecimal.ZERO, currency);
	}
}
