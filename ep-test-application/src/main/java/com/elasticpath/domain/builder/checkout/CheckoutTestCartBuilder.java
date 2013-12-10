/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.domain.builder.checkout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.payment.impl.PaymentGatewayImpl;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.payment.PaymentGatewayService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

/**
 * Builder for shopping carts to facilitate testing of checkout.
 */
public class CheckoutTestCartBuilder {
	private static final String PAYMENT_GATEWAY_PLUGIN_TEST_DOUBLE = "paymentGatewayPluginTestDouble";

	private Store store;

	private Customer customer;

	private CustomerAddress address;

	private CustomerSession customerSession;

	private SimpleStoreScenario scenario;

	private List<Product> products = new ArrayList<Product>();

	private List<GiftCertificate> giftCertificates = new ArrayList<GiftCertificate>();

	@Autowired
	private TestDataPersisterFactory persisterFactory;

	@Autowired
	private CartDirector cartDirector;

	@Autowired
	private PaymentGatewayService paymentGatewayService;

	@Autowired
	private StoreService storeService;

	/**
	 * Select the scenario to use with the builder.
	 *
	 * @param scenario the scenario
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withScenario(final SimpleStoreScenario scenario) {
		this.scenario = scenario;
		store = scenario.getStore();

		address = persisterFactory.getStoreTestPersister().createCustomerAddress("Bond", "James", "1234 Pine Street", "", "Vancouver", "CA", "BC",
				"V6J5G4", "891312345007");

		return this;
	}

	public CheckoutTestCartBuilder withCustomer(final Customer customer) {
		this.customer = customer;

		customerSession = persisterFactory.getStoreTestPersister().persistCustomerSessionWithAssociatedEntities(customer);
		return this;
	}

	/**
	 * Add a physical product to the cart.
	 *
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withPhysicalProduct() {
		Product physicalProduct = persisterFactory.getCatalogTestPersister().persistDefaultShippableProducts(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse()).get(0);
		products.add(physicalProduct);
		return this;
	}

	/**
	 * Add an electronic product to the cart.
	 *
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withElectronicProduct() {
		Product electronicProduct = persisterFactory.getCatalogTestPersister().persistDefaultNonShippableProducts(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse()).get(0);
		products.add(electronicProduct);
		return this;
	}

	/**
	 * Add a free electronic product to the cart.
	 *
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withFreeElectronicProduct() {
		Product freeElectronicProduct = persisterFactory.getCatalogTestPersister().persistNonShippablePersistedProductWithSku(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse(), BigDecimal.ZERO, "Free Electronic Product", "Free Electronic Product");
		products.add(freeElectronicProduct);
		return this;
	}

	/**
	 * Add a gift certificate with the specified amount.
	 *
	 * @param amount the amount
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withGiftCertificateAmount(final BigDecimal amount) {
		GiftCertificate certificate = persisterFactory.getGiftCertificateTestPersister().persistGiftCertificate(scenario.getStore(),
				"bigGiftCertificateGuid", "bigGiftCertificateCode",
				store.getDefaultCurrency().getCurrencyCode(), amount, "recipientName", "senderName", "theme",
				customer);
		giftCertificates.add(certificate);
		return this;
	}

	/**
	 * Use invalid token payment gateway for checkout.
	 *
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withInvalidPaymentTokenGateway() {
		addStorePaymentGateway(createAndPersistInvalidPaymentGateway());
		return this;
	}

	/**
	 * Use test double token payment gateway for checkout.
	 *
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withTestDoubleGateway() {
		addStorePaymentGateway(createAndPersistTestDoubleGateway());
		return this;
	}


	/**
	 * Use gift certificate gateway for checkout.
	 *
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withGiftCertificateGateway() {
		PaymentGateway giftCertificateGateway = persisterFactory.getStoreTestPersister().persistGiftCertificatePaymentGateway();
		addStorePaymentGateway(giftCertificateGateway);
		return this;
	}

	/**
	 * Use submitted gateway for checkout.
	 *
	 * @param paymentGateway the payment gateway
	 * @return the checkout test cart builder
	 */
	public CheckoutTestCartBuilder withGateway(final PaymentGateway paymentGateway) {
		addStorePaymentGateway(paymentGateway);
		return this;
	}

	/**
	 * Builds the shopping cart.
	 *
	 * @return the shopping cart
	 */
	public ShoppingCart build() {
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingServiceLevel(), store);
		for (Product product : products) {
			ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 1);
			cartDirector.addItemToCart(shoppingCart, dto);
		}

		for (GiftCertificate giftCertificate: giftCertificates) {
			shoppingCart.applyGiftCertificate(giftCertificate);
		}

		return shoppingCart;
	}

	private void addStorePaymentGateway(final PaymentGateway gateway) {
		store.getPaymentGateways().add(gateway);
		store = storeService.saveOrUpdate(store);
	}

	private PaymentGateway createAndPersistTestDoubleGateway() {
		PaymentGateway paymentGateway = new PaymentGatewayImpl();
		paymentGateway.setType(PAYMENT_GATEWAY_PLUGIN_TEST_DOUBLE);
		paymentGateway.setName(Utils.uniqueCode(PAYMENT_GATEWAY_PLUGIN_TEST_DOUBLE));
		return paymentGatewayService.saveOrUpdate(paymentGateway);
	}

	private PaymentGateway createAndPersistInvalidPaymentGateway() {
		final PaymentGateway paymentGateway = new PaymentGatewayImpl();
		paymentGateway.setType("paymentGatewayCyberSourceToken");
		paymentGateway.setName(Utils.uniqueCode("CybersourceTokenPaymentGateway"));
		return paymentGatewayService.saveOrUpdate(paymentGateway);
	}

}
