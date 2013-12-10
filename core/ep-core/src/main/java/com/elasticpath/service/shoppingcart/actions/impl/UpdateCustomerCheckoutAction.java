package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.misc.CustomerEmailPropertyHelper;
import com.elasticpath.domain.misc.EmailProperties;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.misc.EmailService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.actions.CheckoutAction;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;

/**
 * CheckoutService to update the related customer record when an order is processed.
 */
public class UpdateCustomerCheckoutAction implements CheckoutAction {
	
	private EmailService emailService;
	
	private CustomerService customerService;

	private ShopperService shopperService;

	private BeanFactory beanFactory;

	
	@Override
	public void execute(final CheckoutActionContext context) {
		if (context.isOrderExchange()) {
			return;
		}
		
		final Shopper shopper = context.getShopper();
		final Customer customer = shopper.getCustomer();

		if (customer.isPersisted()) {
			updateCustomer(shopper, customer);
		} else {
			saveNewCustomer(shopper, customer);
			if (!customer.isAnonymous()) {
				emailCustomer(customer);
			}
		}
	}

	private void updateCustomer(final Shopper shopper, final Customer customer) {
		customerService.verifyCustomer(customer);
		final Customer updatedCustomer = customerService.update(customer);
		shopper.setCustomer(updatedCustomer);
	}
	
	private void saveNewCustomer(final Shopper shopper, final Customer customer) {
		customerService.add(customer);
		shopperService.save(shopper);
	}
	
	private void emailCustomer(final Customer customer) {
		final CustomerEmailPropertyHelper customerEmailPropHelper = beanFactory.getBean(ContextIdNames.EMAIL_PROPERTY_HELPER_CUSTOMER);
		
		final EmailProperties emailProperties = customerEmailPropHelper.getNewAccountEmailProperties(customer);
		emailService.sendMail(emailProperties);
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory; 
	}

	protected CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	protected ShopperService getShopperService() {
		return shopperService;
	}

	public void setShopperService(final ShopperService shopperService) {
		this.shopperService = shopperService;
	}
	
	protected EmailService getEmailService() {
		return emailService;
	}

	public void setEmailService(final EmailService emailService) {
		this.emailService = emailService;
	}
}