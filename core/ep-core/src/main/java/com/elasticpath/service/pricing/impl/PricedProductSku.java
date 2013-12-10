package com.elasticpath.service.pricing.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.ListIterator;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.pricing.BaseAmount;
import com.elasticpath.domain.pricing.BaseAmountObjectType;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.subscriptions.PaymentSchedule;

/**
 * A {@link ProductSku} that is priced.
 */
public class PricedProductSku extends AbstractPricedEntity<ProductSku> {
	private final PaymentSchedule paymentSchedule;
	private PriceSchedule priceSchedule;
	private final Collection<BaseAmount> baseAmounts;
	private final PriceListStack plStack;

	/**
	 * Construct a priced product SKU given the SKU and price list stack and a mapping between
	 * price schedules and base amounts.
	 *
	 * @param entity the product SKU to price
	 * @param plStack the price list stack
	 * @param paymentSchedule the payment schedule associated with the sku
	 * @param baseAmounts the collection of base amounts to be used for calculation
	 */
	public PricedProductSku(final ProductSku entity, final PriceListStack plStack,
			final PaymentSchedule paymentSchedule,
			final Collection<BaseAmount> baseAmounts) {
		super(entity, null);
		this.plStack = plStack;
		this.paymentSchedule = paymentSchedule;
		this.baseAmounts = baseAmounts;
	}

	private Price findPrice(final Collection<BaseAmount> baseAmounts) {
		final List<String> plGuids = getPriceListStack().getPriceListStack();
		final Currency currency = getPriceListStack().getCurrency();
		final Price price = getPriceBean();
		price.setCurrency(currency);
		boolean foundPrice = false;
		boolean inheritFromProduct = getPaymentSchedule() == null;

		// reverse iterator the stack of price list guids as the top of the stack trumps the bottom
		ListIterator<String> stackIter = plGuids.listIterator(plGuids.size());
		while (stackIter.hasPrevious()) {
			String plGuid = stackIter.previous();
			if (inheritFromProduct) {
				foundPrice |= getPricePopulator().populatePriceFromBaseAmounts(getBaseAmountFinder().filterBaseAmounts(baseAmounts,
						plGuid, BaseAmountObjectType.PRODUCT, getEntity().getProduct().getGuid()), currency, price);
			}
			foundPrice |= getPricePopulator().populatePriceFromBaseAmounts(getBaseAmountFinder().filterBaseAmounts(baseAmounts,
					plGuid, BaseAmountObjectType.SKU, getEntity().getSkuCode()), currency, price);
		}

		if (foundPrice) {
			return price;
		}

		return null;
	}
	/**
	 * @return the payment schedule associated with the sku
	 */
	protected PriceSchedule getPriceSchedule() {
		if (priceSchedule == null) {
			priceSchedule = getPriceSchedule(getPaymentSchedule());
		}
		return priceSchedule;
	}

	/**
	 * Get the price.
	 *
	 * @return the price
	 */
	@Override
	public Price getPrice() {
		PricingScheme pricingScheme = getBeanFactory().getBean(ContextIdNames.PRICING_SCHEME);
		Price schedulePrice = findPrice(getBaseAmounts());
		if (schedulePrice == null) {
			return null;
		}
		pricingScheme.setPriceForSchedule(getPriceSchedule(), schedulePrice);
		Price price;
		if (getPaymentSchedule() == null) {
			price = schedulePrice;
		} else {
			price = getPriceBean();
			final Money zero = MoneyFactory.createMoney(BigDecimal.ZERO, schedulePrice.getCurrency());
			price.setListPrice(zero);
		}
		price.setPricingScheme(pricingScheme);
		return price;
	}

	protected PaymentSchedule getPaymentSchedule() {
		return paymentSchedule;
	}

	protected Collection<BaseAmount> getBaseAmounts() {
		return baseAmounts;
	}

	protected PriceListStack getPriceListStack() {
		return plStack;
	}




}


