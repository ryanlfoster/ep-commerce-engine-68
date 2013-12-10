/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.attribute.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * Class required for JPA persistence mapping.
 */
@Entity
@Table(name = CategoryAttributeValueImpl.TABLE_NAME)
public class CategoryAttributeValueImpl extends AbstractAttributeValueImpl {

	/** The name of the table & generator to use for persistence. */
	public static final String TABLE_NAME = "TCATEGORYATTRIBUTEVALUE";

	private static final long serialVersionUID = 5000000001L;

	private long uidPk;

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
	 * {@inheritDoc} <br>
	 * No need to define other fields as Override on equals tests class equivalence for symmetry.
	 */
	@Override
	@SuppressWarnings({ "PMD.UselessOverridingMethod" })
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * {@inheritDoc} <br>
	 * Equals is redefined in this case to handle object equivalence between siblings.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CategoryAttributeValueImpl)) {
			return false;
		}
		return super.equals(obj);
	}

}
