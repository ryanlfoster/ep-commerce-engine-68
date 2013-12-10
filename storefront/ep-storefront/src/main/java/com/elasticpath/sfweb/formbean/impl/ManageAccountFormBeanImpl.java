package com.elasticpath.sfweb.formbean.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.rules.CouponUsage;
import com.elasticpath.sfweb.formbean.ManageAccountFormBean;

/**
 *
 * Form bean implementation for manage account page.
 *
 */
public class ManageAccountFormBeanImpl implements ManageAccountFormBean {

	private static final long serialVersionUID = 1L;

	private List<CouponUsage> customerCoupons = new ArrayList<CouponUsage>();

	private Customer customer;

	private List<Order> orders;

	private String storeCustomerCC;

	private Map<String, String> couponNames;

	@Override
	public Map<String, String> getCouponNames() {
		return couponNames;
	}

	@Override
	public void setCouponNames(final Map<String, String> couponNames) {
		this.couponNames = couponNames;
	}

	@Override
	public List<CouponUsage> getCustomerCoupons() {
		return customerCoupons;
	}

	@Override
	public void setCustomerCoupons(final List<CouponUsage> customerCoupons) {
		this.customerCoupons = customerCoupons;
	}

	@Override
	public Customer getCustomer() {
		return customer;
	}

	@Override
	public void setCustomer(final Customer customer) {
		this.customer = customer;
	}

	@Override
	public List<Order> getOrders() {
		return orders;
	}

	@Override
	public void setOrders(final List<Order> orders) {
		this.orders = orders;
	}

	@Override
	public String getStoreCustomerCC() {
		return storeCustomerCC;
	}

	@Override
	public void setStoreCustomerCC(final String storeCustomerCC) {
		this.storeCustomerCC = storeCustomerCC;
	}

}
