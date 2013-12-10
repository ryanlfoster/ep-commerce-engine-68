package com.elasticpath.sellingchannel.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.Set;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.PriceSchedule;
import com.elasticpath.domain.catalog.PriceScheduleType;
import com.elasticpath.domain.catalog.PricingScheme;
import com.elasticpath.domain.misc.Money;
import com.elasticpath.domain.misc.impl.MoneyFactory;
import com.elasticpath.domain.shoppingcart.ShoppingItemRecurringPrice;
import com.elasticpath.domain.shoppingcart.impl.ShoppingItemSimplePrice;
import com.elasticpath.domain.subscriptions.PaymentSchedule;
import com.elasticpath.sellingchannel.ShoppingItemRecurringPriceAssembler;
import com.elasticpath.service.pricing.PaymentScheduleHelper;

/**
 * Assembles {@link ShoppingItemRecurringPrice}s from {@link Price} and vice versa.  
 */
public class ShoppingItemRecurringPriceAssemblerImpl implements ShoppingItemRecurringPriceAssembler {
	private static final long serialVersionUID = 100L;
	private BeanFactory beanFactory;
	private PaymentScheduleHelper paymentScheduleHelper;
	
	/**
	 * Set the bean factory. The factory will be used to create new instances of 
	 * {@link ShoppingItemRecurringPrice} and {@link Price}. 
	 *
	 * @param beanFactory the factory
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	
	@Override
	public void assemblePrice(final Price price, final Set<ShoppingItemRecurringPrice> recurringPrices) {
		PricingScheme pricingScheme = price.getPricingScheme();
		if (pricingScheme == null) {
			pricingScheme = beanFactory.getBean(ContextIdNames.PRICING_SCHEME);
		}
		price.setPricingScheme(pricingScheme);

		
		if (recurringPrices == null) {
			return;
		}
		
		for (ShoppingItemRecurringPrice recPrice : recurringPrices) {
			PriceSchedule schedule = beanFactory.getBean(ContextIdNames.PRICE_SCHEDULE);
			PaymentSchedule paymentSchedule = getPaymentScheduleHelper().getPaymentSchedule(recPrice);
			schedule.setPaymentSchedule(paymentSchedule);
			schedule.setType(PriceScheduleType.RECURRING);
			Price schedulePrice = beanFactory.getBean(ContextIdNames.PRICE);
			schedulePrice.setListPrice(createMoney(recPrice.getSimplePrice().getListUnitPrice(), price.getCurrency()));
			
			Money salePrice = createMoney(recPrice.getSimplePrice().getSaleUnitPrice(), price.getCurrency());
			if (salePrice != null) {
				schedulePrice.setSalePrice(salePrice);
			}
			
			Money compPrice = createMoney(recPrice.getSimplePrice().getPromotedUnitPrice(), price.getCurrency());
			if (compPrice != null) {
				schedulePrice.setComputedPrice(compPrice);
			}
			pricingScheme.setPriceForSchedule(schedule, schedulePrice);
		}
	}
	
	
	@Override
	public Set<ShoppingItemRecurringPrice> createShoppingItemRecurringPrices(final Price price, final int quantity) {
		Set<ShoppingItemRecurringPrice> recPrices = new HashSet<ShoppingItemRecurringPrice>();
		if (price != null && price.getPricingScheme() != null) {
			Collection<PriceSchedule> schedules = price.getPricingScheme().getSchedules(PriceScheduleType.RECURRING);
			for (PriceSchedule schedule : schedules) {
				ShoppingItemRecurringPrice recPrice = beanFactory.getBean(ContextIdNames.SHOPPING_ITEM_RECURRING_PRICE);
				recPrice.setPaymentFrequency(schedule.getPaymentSchedule().getPaymentFrequency());
				recPrice.setScheduleDuration(schedule.getPaymentSchedule().getScheduleDuration());
				recPrice.setPaymentScheduleName(schedule.getPaymentSchedule().getName());
				recPrice.setSimplePrice(new ShoppingItemSimplePrice(price.getPricingScheme().getSimplePriceForSchedule(schedule), quantity));
				recPrices.add(recPrice);
			}
		}
		return recPrices;
	}
	
	private Money createMoney(final BigDecimal amount, final Currency currency) {
		if (amount == null) {
			return null;
		}

		return MoneyFactory.createMoney(amount, currency);
	}

	/**
	 * Set the payment schedule helper.
	 * @param paymentScheduleHelper the instance to set
	 */
	public void setPaymentScheduleHelper(final PaymentScheduleHelper paymentScheduleHelper) {
		this.paymentScheduleHelper = paymentScheduleHelper;
	}
	
	/**
	 * @return the payments schedule helper instance
	 */
	protected PaymentScheduleHelper getPaymentScheduleHelper() {
		return paymentScheduleHelper;
	}
	
	
	
}
