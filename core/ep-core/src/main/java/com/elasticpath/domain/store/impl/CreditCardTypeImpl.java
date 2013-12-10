package com.elasticpath.domain.store.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.store.CreditCardType;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Default implementation of <code>CreditCardType</code>.
 */
@Entity
@Table(name = CreditCardTypeImpl.TABLE_NAME)
@DataCache(enabled = true)
public class CreditCardTypeImpl extends AbstractPersistableImpl implements CreditCardType {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSTORECREDITCARDTYPE";

	private String creditCardType;

	private long uidPk;

	/**
	 * Gets the type of credit card.
	 * 
	 * @return the type of credit card
	 */
	@Column(name = "TYPE")
	public String getCreditCardType() {
		return creditCardType;
	}

	/**
	 * Sets the type of credit card.
	 * 
	 * @param creditCardType the type of credit card
	 */
	public void setCreditCardType(final String creditCardType) {
		this.creditCardType = creditCardType;
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

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(creditCardType);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof CreditCardTypeImpl)) {
			return false;
		}

		final CreditCardTypeImpl cardType = (CreditCardTypeImpl) obj;

		return ObjectUtils.equals(creditCardType, cardType.creditCardType);
	}

}