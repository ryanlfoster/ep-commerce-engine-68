/**
 * 
 */
package com.elasticpath.test.integration.cart;

import java.math.BigDecimal;
import java.util.Currency;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.pricing.service.PriceListHelperService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShopperFactoryForTestApplication;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.tax.TaxCode;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.persister.TaxTestPersister;

/**
 * Abstract class for Cart-related tests.
 */
public abstract class AbstractCartIntegrationTestParent extends DbTestCase {

	@Autowired
	protected ShopperService shopperService;

	@Autowired
	private CustomerSessionService customerSessionService;

	protected CustomerSession createCustomerSession() {
		return createCustomerSession(null);
	}

	protected CustomerSession createCustomerSession(final Customer customer) {
		final Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
		shopper.setCustomer(customer);
		shopperService.save(shopper);

		final CustomerSession customerSession = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSessionWithContext(
				shopper);

		final PriceListHelperService priceListHelperService = getBeanFactory().getBean(ContextIdNames.PRICE_LIST_HELPER_SERVICE);
		final Currency currency = priceListHelperService.getDefaultCurrencyFor(getScenario().getCatalog());
		customerSession.setCurrency(currency);
		return customerSession;
	}

	protected ShoppingCart createShoppingCart(final CustomerSession customerSession) {
		final ShoppingCart shoppingCart = getBeanFactory().getBean(ContextIdNames.SHOPPING_CART);
		shoppingCart.setStore(getScenario().getStore());
		shoppingCart.setCustomerSession(customerSession);
		customerSession.setShoppingCart(shoppingCart);
		return shoppingCart;
	}

	protected Product persistProductWithSku() {
		final TaxCode taxCode = getPersisterFactory().getTaxTestPersister().getTaxCode(TaxTestPersister.TAX_CODE_GOODS);
		final PriceListHelperService priceListHelperService = getBeanFactory().getBean(ContextIdNames.PRICE_LIST_HELPER_SERVICE);
		final Currency currency = priceListHelperService.getDefaultCurrencyFor(getScenario().getCatalog());
		final int orderLimit = Integer.MAX_VALUE;
		return getPersisterFactory().getCatalogTestPersister().persistProductWithSku(
				getScenario().getCatalog(),
				getScenario().getCategory(),
				getScenario().getWarehouse(),
				BigDecimal.TEN,
				currency,
				"brandCode",
				"productCode",
				"productName",
				"skuCode",
				taxCode.getCode(),
				AvailabilityCriteria.ALWAYS_AVAILABLE,
				orderLimit);
	}
}