/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.rules.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.rules.RuleParameter;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Represents a parameter of a rule condition, such as the category that a product must belong to to qualify for a promotion.
 */
@Entity
@Table(name = RuleParameterImpl.TABLE_NAME)
@DataCache(enabled = false)
public class RuleParameterImpl extends AbstractPersistableImpl implements RuleParameter {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TRULEPARAMETER";

	private String key;

	private String value;

	private String displayText;

	private long uidPk;

	/**
	 * No argument constructor.
	 */
	public RuleParameterImpl() {
		// No implementation
	}

	/**
	 * Constructor to create an initialized Rule Parameter. Used in unit tests.
	 *
	 * @param key the parameter key
	 * @param value the parameter value
	 */
	public RuleParameterImpl(final String key, final String value) {
		this.key = key;
		this.value = value;
	}

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
	}

	/**
	 * Get the parameter value.
	 *
	 * @return the parameter value
	 */
	@Basic
	@Column(name = "PARAM_VALUE", nullable = false)
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
