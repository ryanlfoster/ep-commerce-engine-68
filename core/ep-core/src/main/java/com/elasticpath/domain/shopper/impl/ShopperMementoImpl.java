package com.elasticpath.domain.shopper.impl;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.persistence.openjpa.PersistenceInterceptor;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.shopper.ShopperMemento;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * The default implementation of {@link ShopperMemento}.
 *
 */
@Entity
@Table(name = ShopperMementoImpl.TABLE_NAME)
@DiscriminatorColumn(name = "TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("SHOPPER")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DataCache(enabled = false)
public class ShopperMementoImpl extends AbstractEntityImpl implements ShopperMemento, PersistenceInterceptor {
	private static final long serialVersionUID = 9101723347142279556L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSHOPPER";

	private long uidPk;

	private Customer customer;

	private String storeCode;

	private String guid;
	
	/**
	 * Allocation size for JPA_GENERATED_KEYS id.
	 */
	private static final int ALLOCATION_SIZE = 1000;
	
	@Override
	@ManyToOne(targetEntity = CustomerImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "CUSTOMER_GUID", referencedColumnName = "GUID")
	@ForeignKey(name = "FK_CUSTOMER", enabled = true)
	public Customer getCustomer() {
		return this.customer;
	}

	@Override
	public void setCustomer(final Customer customer) {
		this.customer = customer;
	}

	@Override
	@Column(name = "STORECODE")
	public String getStoreCode() {
		return storeCode;
	}

	@Override
	public void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", 
			valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME,  allocationSize = ALLOCATION_SIZE)
	public long getUidPk() {
		return this.uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	@Basic
	@Column(name = "GUID")
	public String getGuid() {
		return guid;
	}

	@Override
	public void setGuid(final String guid) { // NOPMD
		this.guid = guid;
	}	
	
	@Override
	public void executeBeforePersistAction() {
		// don't persist an Anonymous {@link Customer} which is a customer with no userId
		if ((getCustomer() != null) && (StringUtils.isEmpty(getCustomer().getUserId()))) {
			setCustomer(null);
		}
	}
}
