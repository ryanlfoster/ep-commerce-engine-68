package com.elasticpath.domain.shoppingcart.impl;

import static com.elasticpath.domain.quantity.Quantity.AMOUNT_PRECISION;
import static com.elasticpath.domain.quantity.Quantity.AMOUNT_SCALE;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.ObjectUtils;

import com.elasticpath.domain.quantity.Quantity;
import com.elasticpath.domain.shoppingcart.ShoppingItemRecurringPrice;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * Stores a snapshot of recurring prices for a {@link com.elasticpath.domain.shoppingcart.ShoppingItem}.  
 * 
 */
@Entity
@Table(name = ShoppingItemRecurringPriceImpl.TABLE_NAME)
public class ShoppingItemRecurringPriceImpl extends AbstractEntityImpl implements ShoppingItemRecurringPrice {
	private static final long serialVersionUID = -2984781782890218888L;
	/** Denotes the table name. */
	public static final String TABLE_NAME = "TSHOPPINGITEMRECURRINGPRICE";
	private String paymentScheduleName;
	private Quantity paymentFrequency = new Quantity(null, null);
	private Quantity scheduleDuration = new Quantity(null, null);;
	private long uidPk;
	private ShoppingItemSimplePrice simplePrice = new ShoppingItemSimplePrice();
	private String guid;

	@Override
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "amount", column = @Column(name = "FREQ_AMOUNT", nullable = false, 
				scale = AMOUNT_SCALE, precision = AMOUNT_PRECISION)),
		@AttributeOverride(name = "unit", column = @Column(name = "FREQ_UNIT", nullable = false))
	})
	public Quantity getPaymentFrequency() {
		return paymentFrequency;
	}
	
	@Override
	public void setPaymentFrequency(final Quantity paymentFrequency) {
		this.paymentFrequency = paymentFrequency; 
	}
	
	@Override
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "amount", column = @Column(name = "DURATION_AMOUNT", scale = AMOUNT_SCALE, precision = AMOUNT_PRECISION)),
		@AttributeOverride(name = "unit", column = @Column(name = "DURATION_UNIT"))
	})
	public Quantity getScheduleDuration() {
		return scheduleDuration;
	}
	
	@Override
	public void setScheduleDuration(final Quantity scheduleDuration) {
		this.scheduleDuration = scheduleDuration;
	}
	
	@Override
	@Column(name = "PAYMENT_SCHEDULE_NAME")
	public String getPaymentScheduleName() {
		return paymentScheduleName;
	}
	
	@Override
	public void setPaymentScheduleName(final String paymentScheduleName) {
		this.paymentScheduleName = paymentScheduleName;
	}
	
	
	@Override
    @Id
    @Column(name = "UIDPK")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
    @TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	
	@Override
	@Embedded
	public ShoppingItemSimplePrice getSimplePrice() {
		return simplePrice;
	}

	@Override
	public void setSimplePrice(final ShoppingItemSimplePrice simplePrice) {
		this.simplePrice = simplePrice;
	}

	/**
	 * Return the guid.
	 * 
	 * @return the guid.
	 */
	@Override
	@Basic
	@Column(name = "GUID")
	public String getGuid() {
		return guid;
	}

	/**
	 * Set the guid.
	 * 
	 * @param guid the guid to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	
	
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof ShoppingItemRecurringPriceImpl)) {
			return false;
		}
		
		ShoppingItemRecurringPriceImpl entity = (ShoppingItemRecurringPriceImpl) other;
		return ObjectUtils.equals(this.getGuid(), entity.getGuid());
	}

	@Override
	@SuppressWarnings({ "PMD.UselessOverridingMethod" })
	public int hashCode() {
		return super.hashCode();
	}
	
	
}

