package com.elasticpath.domain.payment.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.payment.PaymentGatewayProperty;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Default implementation of <code>PaymentGatewayProperty</code>.
 */
@Entity
@Table(name = PaymentGatewayPropertyImpl.TABLE_NAME)
@DataCache(enabled = true)
public class PaymentGatewayPropertyImpl extends AbstractPersistableImpl implements PaymentGatewayProperty {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TPAYMENTGATEWAYPROPERTIES";

	private String key;

	private String value;

	private long uidPk;

	/**
	 * Gets the key of this property.
	 * 
	 * @return the key of this property
	 */
	@Column(name = "PROPKEY")
	public String getKey() {
		return key;
	}

	/**
	 * Sets the key of this property.
	 * 
	 * @param key the key of this property
	 */
	public void setKey(final String key) {
		this.key = key;
	}

	/**
	 * Gets the value of this property.
	 * 
	 * @return the value of this property
	 */
	@Column(name = "PROPVALUE")
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of this property.
	 * 
	 * @param value the value of this property
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
}
