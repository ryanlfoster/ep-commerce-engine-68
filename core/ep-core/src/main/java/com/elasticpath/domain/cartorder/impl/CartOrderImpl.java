/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */
package com.elasticpath.domain.cartorder.impl;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.impl.AbstractPaymentMethodImpl;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Implementation of CartOrder, CartOrder should not be used in versions of EP prior to 6.4.
 */
@Entity
@Table(name = CartOrderImpl.TABLE_NAME)
@DataCache(enabled = false)
public class CartOrderImpl extends AbstractEntityImpl implements CartOrder {
	
	private static final long serialVersionUID = 641L;
	
	/** Allocation size for JPA_GENERATED_KEYS id. */
	private static final int ALLOCATION_SIZE = 1000;
	
	/** The name of the table & generator to use for persistence. */
	public static final String TABLE_NAME = "TCARTORDER";
	
	private String billingAddressGuid;
	
	private String shippingAddressGuid;
	
	private String shoppingCartGuid;
		
	private String shippingServiceLevelGuid;
	
	private long uidPk;

	private String guid;

	private PaymentMethod paymentMethod;

	private String paymentMethodGuid;

	@Override
	@Basic
	@Column(name = "BILLING_GUID", nullable = true)
	public String getBillingAddressGuid() {
		return this.billingAddressGuid;
	}

	@Override
	public void setBillingAddressGuid(final String guid) {
		this.billingAddressGuid = guid;
	}
	
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(
			strategy = GenerationType.TABLE,
			generator = TABLE_NAME)
	@TableGenerator(
			name = TABLE_NAME,
			table = "JPA_GENERATED_KEYS",
			pkColumnName = "ID",
			valueColumnName = "LAST_VALUE",
			pkColumnValue = TABLE_NAME,
			allocationSize = ALLOCATION_SIZE)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
	
	/**
	 * @return The ShoppingCart GUID.
	 */
	@Basic
	@Column(name = "SHOPPINGCART_GUID", nullable = false, unique = true)
	private String getShoppingCartGuidInternal() {
		return this.shoppingCartGuid;
	}
	
	/**
	 * @param guid The ShoppingCart GUID.
	 */
	private void setShoppingCartGuidInternal(final String guid) {
		this.shoppingCartGuid = guid;
	}
	
	@Override
	@Transient
	public String getShoppingCartGuid() {
		return getShoppingCartGuidInternal();
	}
	
	@Override
	@Transient
	public void setShoppingCartGuid(final String guid) {
		if (guid == null) {
			throw new IllegalArgumentException("Parameter [guid] cannot be null.");
		}
		setShoppingCartGuidInternal(guid);
	}
	
	@Override
	@OneToOne(targetEntity = AbstractPaymentMethodImpl.class, cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
	@JoinColumn(name = "PAYMENT_METHOD_UID")
	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	@Override
	public void setPaymentMethod(final PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	@Override
	@Basic
	@Column(name = "PAYMENTMETHOD_GUID", nullable = true)
	public String getPaymentMethodGuid() {
		return paymentMethodGuid;
	}
	
	@Override
	public void setPaymentMethodGuid(final String paymentMethodGuid) {
		this.paymentMethodGuid = paymentMethodGuid;
	}
	
	
	@Override
	@Basic
	@Column(name = "GUID", nullable = false, unique = true)
	public String getGuid() {
		return guid;
	}

	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}
	
	@Override
	@Basic
	@Column(name = "SHIPPING_ADDRESS_GUID", nullable = true)
	public String getShippingAddressGuid() {
		return shippingAddressGuid;
	}
	
	@Override
	public void setShippingAddressGuid(final String shippingAddressGuid) {
		this.shippingAddressGuid = shippingAddressGuid;
	}
	
	@Override
	@Basic
	@Column(name = "SHIPPING_SERVICE_LEVEL_GUID", nullable = true)
	public String getShippingServiceLevelGuid() {
		return shippingServiceLevelGuid;
	}
	
	@Override
	public void setShippingServiceLevelGuid(final String shippingServiceLevelGuid) {
		this.shippingServiceLevelGuid = shippingServiceLevelGuid;
	}

	@Override
	@Transient
	public int hashCode() {
	    return ObjectUtils.hashCode(getGuid());
	}

	@Override
	@Transient
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof CartOrderImpl) {
		    CartOrderImpl other = (CartOrderImpl) obj;
		    return ObjectUtils.equals(other.getGuid(), this.getGuid());
		}
		return false;
	}

}
