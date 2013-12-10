package com.elasticpath.domain.testcontext;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.store.Store;

/**
 * This class holds Shopping specific data required to persist 
 * between fixtures for the duration of a test.
 * 
 * Do not add elements to this class for completely different test
 * flows, create a new one that is appropriate to the set of tests.
 * 
 * NOTE: The previous approach to this was for each fixture to hold its own
 * references which allowed for them to get out of sync very easily and
 * encouraged the coupling of fixtures to other fixtures.  Any changes
 * to the way certain elements were constructed caused large ripple effects
 * throughout the fixtures.
 */
public class ShoppingTestData {

	private static ShoppingTestData instance = new ShoppingTestData();
	
	private CustomerSession customerSession;
	
	/* Wish there was another way, but time is short and some of our tests need a second CustomerSession. */
	private CustomerSession secondaryCustomerSession;
	
	private Store store;
	private Order order;
	
	private ShoppingTestData() {
	}

	public static ShoppingTestData getInstance() {
		return instance;
	}

	public CustomerSession getCustomerSession() {
		return customerSession;
	}

	public void setCustomerSession(CustomerSession customerSession) {
		this.customerSession = customerSession;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public Order getOrder() {
		return order;
	}

	public static void reset() {
		instance = new ShoppingTestData();
	}

    /**
     * @return the secondaryCustomerSession
     */
    public CustomerSession getSecondaryCustomerSession() {
        return secondaryCustomerSession;
    }

    /**
     * @param secondaryCustomerSession the secondaryCustomerSession to set
     */
    public void setSecondaryCustomerSession(final CustomerSession secondaryCustomerSession) {
        this.secondaryCustomerSession = secondaryCustomerSession;
    }

}
