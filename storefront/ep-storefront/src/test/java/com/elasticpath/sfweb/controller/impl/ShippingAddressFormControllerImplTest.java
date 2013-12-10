package com.elasticpath.sfweb.controller.impl;

import static org.junit.Assert.assertSame;


import java.util.Arrays;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerSessionImpl;
import com.elasticpath.domain.customer.impl.CustomerSessionMementoImpl;
import com.elasticpath.domain.shopper.impl.ShopperImpl;
import com.elasticpath.domain.shopper.impl.ShopperMementoImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartMementoImpl;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.sfweb.formbean.CheckoutAddressFormBean;
import com.elasticpath.sfweb.formbean.impl.CheckoutAddressFormBeanImpl;
import com.elasticpath.sfweb.test.BeanFactoryExpectationsFactory;
import com.elasticpath.sfweb.util.SfRequestHelper;

public class ShippingAddressFormControllerImplTest {
	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private ShippingAddressFormControllerImpl controller;
	private MockHttpServletRequest request;
	private ShoppingCartImpl shoppingCart;
	private SfRequestHelper requestHelper;
	private CustomerSession customerSession;
	private ShopperImpl shopper;
	private CustomerAddressImpl shippingAddress;
	private CustomerAddressImpl billingAddress;
	private BeanFactory beanFactory;
	private BeanFactoryExpectationsFactory bfef;
	private Customer customer;

	@Before
	public void setUp() throws Exception {
		requestHelper = context.mock(SfRequestHelper.class);
		beanFactory = context.mock(BeanFactory.class);

		bfef = new BeanFactoryExpectationsFactory(context, beanFactory);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.CHECKOUT_ADDRESS_FORM_BEAN, CheckoutAddressFormBeanImpl.class);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_ADDRESS, CustomerAddressImpl.class);
		bfef.allowingBeanFactoryGetBean(ContextIdNames.SHOPPING_CART_MEMENTO, ShoppingCartMementoImpl.class);

		shippingAddress = new CustomerAddressImpl();
		billingAddress = new CustomerAddressImpl();

		customer = context.mock(Customer.class);

		shoppingCart = new ShoppingCartImpl();
		shoppingCart.setShippingAddress(shippingAddress);
		shoppingCart.setBillingAddress(billingAddress);
		shoppingCart.setStore(new StoreImpl());

		shopper = new ShopperImpl();
		shopper.setShopperMemento(new ShopperMementoImpl());
		shopper.setCurrentShoppingCart(shoppingCart);
		shopper.setCustomer(customer);

		customerSession = new CustomerSessionImpl();
		customerSession.setCustomerSessionMemento(new CustomerSessionMementoImpl());
		customerSession.setShopper(shopper);

		request = new MockHttpServletRequest();

		context.checking(new Expectations() { {
			allowing(requestHelper).getCustomerSession(request); will(returnValue(customerSession));

			allowing(customer).getAddresses(); will(returnValue(Arrays.<CustomerAddress>asList(shippingAddress, billingAddress)));
			allowing(customer).getFirstName(); will(returnValue("Foo"));
			allowing(customer).getLastName(); will(returnValue("Bar"));
			allowing(customer).getPreferredShippingAddress(); will(returnValue(shippingAddress));
		} });

		controller = new ShippingAddressFormControllerImpl();
		controller.setRequestHelper(requestHelper);
		controller.setBeanFactory(beanFactory);
	}

	@After
	public void tearDown() {
		bfef.close();
	}

	@Test
	public void testFormBackingObject() throws Exception {
		CheckoutAddressFormBean command = (CheckoutAddressFormBean) controller.formBackingObject(request);
		assertSame("Sanity: controller should set shipping address", shippingAddress, command.getSelectedAddress());
	}
}
