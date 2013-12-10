package com.elasticpath.domain.store.impl;

import java.util.Currency;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.FetchGroups;
import org.apache.openjpa.persistence.Persistent;

import com.elasticpath.domain.misc.SupportedCurrency;
import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;

/**
 * Store currency class, required for JPA persistence.
 */
@Entity
@Table(name = StoreCurrencyImpl.TABLE_NAME)
@FetchGroups({ @FetchGroup(name = FetchGroupConstants.STORE_SHARING, attributes = { @FetchAttribute(name = "currency") }) })
@DataCache(enabled = true)
public class StoreCurrencyImpl extends AbstractPersistableImpl implements SupportedCurrency {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSTORESUPPORTEDCURRENCY";

	private long uidPk;
	
	private Currency currency;

	/**
	 * Get the currency.
	 * 
	 * @return the Currency
	 */
	@Persistent
	@Column(name = "CURRENCY")
	@Externalizer("getCurrencyCode")
	@Factory("com.elasticpath.commons.util.impl.ConverterUtils.currencyFromString")
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * Set the currency.
	 * 
	 * @param currency the currency to set
	 */
	public void setCurrency(final Currency currency) {
		this.currency = currency;
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
