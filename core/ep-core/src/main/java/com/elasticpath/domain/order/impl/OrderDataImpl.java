package com.elasticpath.domain.order.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.order.OrderData;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Default implementation class which holds generic key/value information associated with an {@link com.elasticpath.domain.order.Order}.
 */
@Entity
@Table(name = OrderDataImpl.TABLE_NAME)
@DataCache(enabled = false)
public class OrderDataImpl extends AbstractPersistableImpl implements OrderData {
	private static final long serialVersionUID = 5000000001L;

	/** The name of the DB table used to persist this object. */
	static final String TABLE_NAME = "TORDERDATA";

	private String key;
	private String value;
	private long uidPk;

	/**
	 * Default Constructor.
	 */
	public OrderDataImpl() {
		//  Default Constructor for OpenJPA
		this(null, null);
	}

	/**
	 * Regular Constructor.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public OrderDataImpl(final String key, final String value) {
		this.key = key;
		this.value = value;
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

	/**
	 * @return the key
	 */
	@Basic
	@Column(name = "ITEM_KEY")
	public String getKey() {
		return key;
	}

	/**
	 * Set the key.
	 * @param key the key to set
	 */
	protected void setKey(final String key) {
		this.key = key;
	}

	@Basic
	@Column(name = "ITEM_VALUE")
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("key", key)
				.append("value", value)
				.toString();
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}

		if (!(other instanceof OrderDataImpl)) {
			return false;
		}

		OrderDataImpl data = (OrderDataImpl) other;
		return ObjectUtils.equals(this.key, data.key);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this.key);
	}

}
