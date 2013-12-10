/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.store.impl;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.domain.store.WarehouseAddress;
import com.elasticpath.persistence.api.AbstractEntityImpl;
import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * Default implementation of <code>Warehouse</code>.
 */
@Entity
@Table(name = WarehouseImpl.TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = { "CODE" }))
@DataCache(enabled = true)
@FetchGroups({
		@FetchGroup(name = FetchGroupConstants.ORDER_STORE_AND_WAREHOUSE, attributes = {			
				@FetchAttribute(name = "code")
		})
})
public class WarehouseImpl extends AbstractPersistableImpl implements Warehouse {
	
	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TWAREHOUSE";

	private String name;

	private int pickDelay;

	private WarehouseAddress address;

	private long uidPk;
	
	private String code;

	/**
	 * Gets the name of this <code>Warehouse</code>.
	 * 
	 * @return the name of this <code>Warehouse</code>.
	 */
	@Basic(optional = false)
	@Column(name = "NAME")
	public String getName() {
		return name;
	}

	/**
	 * sets the name of this <code>Warehouse</code>.
	 * 
	 * @param name the name of this <code>Warehouse</code>.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Gets the pick delay of this <code>Warehouse</code>. The pick delay is defined as the amount of time (in minutes) when an order is placed to
	 * when it is available for packing in the warehouse.
	 * 
	 * @return the pick delay (in minutes)
	 */
	@Column(name = "PICK_DELAY")
	public int getPickDelay() {
		return pickDelay;
	}

	/**
	 * Sets the pick delay of this <code>Warehouse</code>. The pick delay is defined as the amount of time (in minutes) when an order is placed to
	 * when it is available for packing in the warehouse.
	 * 
	 * @param pickDelay the pick delay (in minutes)
	 */
	public void setPickDelay(final int pickDelay) {
		this.pickDelay = pickDelay;
	}

	/**
	 * Gets the <code>WarehouseAddress</code> of this <code>Warehouse</code>.
	 * 
	 * @return the <code>WarehouseAddress</code> of this <code>Warehouse</code>
	 */
	@OneToOne(targetEntity = WarehouseAddressImpl.class, cascade = CascadeType.ALL, optional = false)
	@JoinColumn(name = "ADDRESS_UID")
	@ForeignKey
	public WarehouseAddress getAddress() {
		return address;
	}

	/**
	 * Sets the <code>WarehouseAddress</code> of this <code>Warehouse</code>.
	 * 
	 * @param address the <code>WarehouseAddress</code> of this <code>Warehouse</code>
	 */
	public void setAddress(final WarehouseAddress address) {
		this.address = address;
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
	
	/**
	 * Gets the unique code associated with the <code>Store</code>.
	 * 
	 * @return the unique code associated with the <code>Store</code>
	 */
	@Basic(optional = false)
	@Column(name = "CODE", length = AbstractEntityImpl.GUID_LENGTH, unique = true)
	public String getCode() {
		return code;
	}
	
	/**
	 * Sets the unique code associated with the <code>Store</code>.
	 * 
	 * @param code the unique code associated with the <code>Store</code>
	 */
	public void setCode(final String code) {
		this.code = code;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WarehouseImpl)) {
			return false;
		}
		WarehouseImpl other = (WarehouseImpl) obj;
		return StringUtils.equals(this.code, other.code);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(this.code);
	}

}
