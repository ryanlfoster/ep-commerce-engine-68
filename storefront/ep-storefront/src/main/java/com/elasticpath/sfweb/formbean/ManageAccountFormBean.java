package com.elasticpath.sfweb.formbean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.CouponUsage;

/**
 *
 * Form bean for manage account page.
 *
 */
public interface ManageAccountFormBean extends Serializable {

	/**
	 * @return map of coupon code and his localized name.
	 */
	Map<String, String> getCouponNames();

	/**
	 * Set the map of coupon code and his localized name.
	 * @param couponNames map of coupon code and his localized name.
	 */
	void setCouponNames(Map<String, String> couponNames);

	/**
	 * Get the list of coupon usage.
	 * @return list of coupon usage.
	 */
	List<CouponUsage> getCustomerCoupons();

	/**
	 * Set list of coupon usage.
	 * @param customerCoupons list of coupon usage.
	 */
	void setCustomerCoupons(List<CouponUsage> customerCoupons);

	/**
	 * @return customer
	 */
	Customer getCustomer();

	/**
	 * Set current customer.
	 * @param customer customer to set.
	 */
	void setCustomer(Customer customer);

	/**
	 * Get historical orders.
	 * @return List of user's orders
	 */
	List<Order> getOrders();

	/**
	 *
	 * @param orders customer orders.
	 */
	void setOrders(List<Order> orders);

	/**
	 * @return "true" if need to show saved credit cards
	 */
	String getStoreCustomerCC();

	/**
	 * @param storeCustomerCC "true" if need to show saved credit cards
	 */
	void setStoreCustomerCC(String storeCustomerCC);


}
