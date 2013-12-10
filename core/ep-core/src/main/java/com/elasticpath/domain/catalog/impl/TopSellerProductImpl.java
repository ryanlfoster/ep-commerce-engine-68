/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.catalog.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.catalog.TopSellerProduct;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Required for JPA Mapping.
 */
@Entity
@Table(name = TopSellerProductImpl.TABLE_NAME)
@DataCache(enabled = false)
public class TopSellerProductImpl extends AbstractPersistableImpl implements TopSellerProduct {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TTOPSELLERPRODUCTS";

	private long productUid;

	private int salesCount;

	private long uidPk;

	/**
	 * Get the Product Uid.
	 * @return the productUid
	 */
	@Basic
	@Column(name = "PRODUCT_UID")
	public long getProductUid() {
		return productUid;
	}

	/**
	 * Set the Product Uid.
	 * @param productUid the productUid to set
	 */
	public void setProductUid(final long productUid) {
		this.productUid = productUid;
	}

	/**
	 * Get the sales count.
	 * @return the salesCount
	 */
	@Basic
	@Column(name = "SALES_COUNT")
	@OrderBy
	public int getSalesCount() {
		return salesCount;
	}

	/**
	 * Set the sales count.
	 * @param salesCount the salesCount to set
	 */
	public void setSalesCount(final int salesCount) {
		this.salesCount = salesCount;
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
