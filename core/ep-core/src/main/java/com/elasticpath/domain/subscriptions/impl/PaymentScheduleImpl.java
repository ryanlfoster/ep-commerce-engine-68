package com.elasticpath.domain.subscriptions.impl;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.subscriptions.PaymentSchedule;

/**
 * Domain object that holds payment schedule frequency and duration.
 */
public class PaymentScheduleImpl implements PaymentSchedule {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000002L;
	
	private Quantity paymentFrequency;
	private Quantity scheduleDuration;
	private String name;
	private int ordering;

	@Override
	public Quantity getPaymentFrequency() {
		return this.paymentFrequency;
	}

	@Override
	public void setPaymentFrequency(final Quantity paymentFrequency) {
		this.paymentFrequency = paymentFrequency;
	}

	@Override
	public Quantity getScheduleDuration() {
		return this.scheduleDuration;
	}
	
	@Override
	public void setScheduleDuration(final Quantity scheduleDuration) {
		this.scheduleDuration = scheduleDuration;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}
	
	@Override
	public int getOrdering() {
		return ordering;
	}
	
	@Override
	public void setOrdering(final int ordering) {
		this.ordering = ordering;
	}

	@Override
	public int compareTo(final PaymentSchedule other) {
		int ord1 = getOrdering();
		int ord2 = other.getOrdering();
		
		if (ord1 > ord2) {
			return 1;
		} 
		if (ord1 < ord2) {
			return -1;
		}
		return getName().compareTo(other.getName());
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(paymentFrequency).append(scheduleDuration).toHashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		PaymentScheduleImpl other = (PaymentScheduleImpl) obj;
		
		return new EqualsBuilder().append(paymentFrequency, other.paymentFrequency).append(scheduleDuration, other.scheduleDuration).isEquals();
	}

	@Override
	public String toString() {
		return "PaymentScheduleImpl [name=" + name + ", ordering=" + ordering + ", paymentFrequency=" + paymentFrequency + ", scheduleDuration="
				+ scheduleDuration + "]";
	}
	
}
