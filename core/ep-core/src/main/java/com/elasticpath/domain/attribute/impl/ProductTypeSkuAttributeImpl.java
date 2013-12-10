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
@Table(name = ProductTypeSkuAttributeImpl.TABLE_NAME)
public class ProductTypeSkuAttributeImpl extends AttributeGroupAttributeImpl {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;
	
	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TPRODUCTTYPESKUATTRIBUTE";

	/**
	 * Gets the unique identifier for this domain model object.
	 *
	 * @return the unique identifier.
	 */
	@Override
	@SuppressWarnings("PMD.UselessOverridingMethod")
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return super.getUidPk();
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 *
	 * @param uidPk the new unique identifier.
	 */
	@Override
	@SuppressWarnings("PMD.UselessOverridingMethod")
	public void setUidPk(final long uidPk) {
		super.setUidPk(uidPk);
	}

}
