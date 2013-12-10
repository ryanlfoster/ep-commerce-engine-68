/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.domain.customer.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.TableGenerator;

import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Abstract base class that {@link PaymentMethod}s should extend in order to be persistable using JPA. This is
 * required because OpenJPA 1 doesn't support polymorphic persistence using interfaces only.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@SuppressWarnings("PMD.AbstractClassWithoutAnyMethod")
public abstract class AbstractPaymentMethodImpl extends AbstractPersistableImpl implements PaymentMethod {
	private static final String GENERATOR_NAME = "PAYMENTMETHOD";

	private long uidPk;

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = GENERATOR_NAME)
	@TableGenerator(name = GENERATOR_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID",
			valueColumnName = "LAST_VALUE", pkColumnValue = GENERATOR_NAME)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
}
