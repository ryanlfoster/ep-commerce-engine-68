/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog.impl;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.catalog.ProductDeleted;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * The default implementation of <code>Product</code>.
 */
@Entity
@Table(name = ProductDeletedImpl.TABLE_NAME)
@DataCache(enabled = true)
public class ProductDeletedImpl extends AbstractPersistableImpl implements ProductDeleted {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TPRODUCTDELETED";

	private long productUid;

	private Date deletedDate;

	private long uidPk;

	/**
	 * Default constructor.
	 */
	public ProductDeletedImpl() {
		super();
	}

	/**
	 * Returns the uid of the deleted product.
	 *
	 * @return the uid of the deleted product
	 */
	@Basic
	@Column(name = "PRODUCT_UID")
	public long getProductUid() {
		return productUid;
	}

	/**
	 * Sets the uid of the deleted product.
	 *
	 * @param productUid the uid of the deleted product.
	 */
	public void setProductUid(final long productUid) {
		this.productUid = productUid;
	}

	/**
	 * Returns the date when the product was deleted.
	 *
	 * @return the date when the product was deleted
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DELETED_DATE", nullable = false)
	public Date getDeletedDate() {
		return deletedDate;
	}

	/**
	 * Sets the date when the product was deleted.
	 *
	 * @param deletedDate the date when the product was deleted
	 */
	public void setDeletedDate(final Date deletedDate) {
		if (deletedDate == null) {
			throw new EpDomainException("name cannot be set to null.");
		}
		this.deletedDate = deletedDate;
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
