/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping.impl;

import java.util.Currency;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.Persistent;

import com.elasticpath.domain.shipping.ShippingCostCalculationParameter;
import com.elasticpath.domain.shipping.ShippingCostCalculationParametersEnum;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Represents a parameter of a shipping cost calculation method, such as the dollar value of the fix base shipping cost.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.NPathComplexity" })
@Entity
@Table(name = ShippingCostCalculationParameterImpl.TABLE_NAME)
@DataCache(enabled = false)
public class ShippingCostCalculationParameterImpl extends AbstractPersistableImpl implements ShippingCostCalculationParameter {

	/** Maximum string length of currency. */
	private static final int CURRENCY_LENGTH = 3;

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSHIPPINGCOSTCALCULATIONPARAM";

	private String key;

	private String value;

	private String displayText;

	private long uidPk;

	private Currency currency;

	private boolean currencyAware;

	/**
	 * Get the parameter key.
	 * 
	 * @return the parameter key
	 */
	@Basic
	@Column(name = "PARAM_KEY", nullable = false)
	public String getKey() {
		return this.key;
	}

	/**
	 * Set the parameter key.
	 * 
	 * @param key the parameter key
	 */
	public void setKey(final String key) {
		this.key = key;
		ShippingCostCalculationParametersEnum costParameter = ShippingCostCalculationParametersEnum.getCalculationCostParameter(key);
		if (costParameter != null) {
			currencyAware = costParameter.isCurrencyAware();
		}
	}

	/**
	 * Get the parameter value.
	 * 
	 * @return the parameter value
	 */
	@Basic
	@Column(name = "VALUE")
	public String getValue() {
		return this.value;
	}

	/**
	 * Set the parameter value.
	 * 
	 * @param value the parameter value
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * Get the display text for this parameter.
	 * 
	 * @return the the display text, or the parameter value if there is no display text
	 */
	@Basic
	@Column(name = "DISPLAY_TEXT")
	public String getDisplayText() {
		if (this.displayText == null) {
			this.displayText = this.value;
		}
		return this.displayText;
	}

	/**
	 * Set the text to be displayed for this parameter. For example, the display text for a sku code id long might be the actual text sku code
	 * 
	 * @param displayText the text to display
	 */
	public void setDisplayText(final String displayText) {
		this.displayText = displayText;
	}

	/**
	 * Return the hash code.
	 * 
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (getDisplayText() == null) {
			result = prime * result;
		} else {
			result = prime * result + getDisplayText().hashCode();
		}
		if (getKey() == null) {
			result = prime * result;
		} else {
			result = prime * result + getKey().hashCode();
		}
		if (getValue() == null) {
			result = prime * result;
		} else {
			result = prime * result + getValue().hashCode();
		}
		return result;
	}

	/**
	 * Return <code>true</code> if the given object is <code>ShippingCostCalculationParameterImpl</code> and is logically equal.
	 * 
	 * @param obj the object to compare
	 * @return <code>true</code> if the given object is equal
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ShippingCostCalculationParameterImpl)) {
			return false;
		}
		final ShippingCostCalculationParameterImpl other = (ShippingCostCalculationParameterImpl) obj;
		if (getDisplayText() == null) {
			if (other.getDisplayText() != null) {
				return false;
			}
		} else if (!getDisplayText().equals(other.getDisplayText())) {
			return false;
		}
		if (getKey() == null) {
			if (other.getKey() != null) {
				return false;
			}
		} else if (!getKey().equals(other.getKey())) {
			return false;
		}
		if (getValue() == null) {
			if (other.getValue() != null) {
				return false;
			}
		} else if (!getValue().equals(other.getValue())) {
			return false;
		}
		if (getCurrency() == null) {
			if (other.getCurrency() != null) {
				return false;
			}
		} else if (!getCurrency().equals(other.getCurrency())) {
			return false;
		}
		return true;
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
	 * Gets the currency for this <code>ShippingCostCalculationParameter</code>.
	 * 
	 * @return the currency for this <code>ShippingCostCalculationParameter</code>
	 */
	@Persistent(optional = true)
	@Externalizer("toString")
	@Factory("ShippingCostCalculationParameterImpl.makeCurrency")
	@Column(name = "CURRENCY", length = CURRENCY_LENGTH)
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * Proxy for Currency.getInstance(String). Sometimes JPA mistakenly calls Currency.getInstance(Locale), so this method explicitly specifies which
	 * getInstance() to call.
	 * 
	 * @param code currency code.
	 * @return Currency object.
	 */
	public static Currency makeCurrency(final String code) {
		return Currency.getInstance(code);
	}

	/**
	 * Sets the currency for this <code>ShippingCostCalculationParameter</code>.
	 * 
	 * @param currency the currency for this <code>ShippingCostCalculationParameter</code>
	 */
	public void setCurrency(final Currency currency) {
		this.currency = currency;
	}

	/**
	 * Returns true if this parameter is currency aware, false otherwise.
	 * 
	 * @return true if the key is keyed currency aware parameter.
	 */
	@Transient
	public boolean isCurrencyAware() {
		return currencyAware;
	}
}
