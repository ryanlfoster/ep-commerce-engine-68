/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.audit.impl;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.domain.audit.ChangeTransaction;
import com.elasticpath.domain.audit.ChangeTransactionMetadata;
import com.elasticpath.domain.objectgroup.impl.BusinessObjectGroupMemberImpl;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Meta data of the Change Transaction.
 */
@Entity
@Table(name = ChangeTransactionMetadataImpl.TABLE_NAME)
@DataCache(enabled = false)
public class ChangeTransactionMetadataImpl extends AbstractPersistableImpl implements ChangeTransactionMetadata {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The entity table name.
	 */
	protected static final String TABLE_NAME = "TCHANGETRANSACTIONMETADATA"; 		

	private long uidPk;

	private String metadataKey;

	private String metadataValue;

	private ChangeTransaction changeTransaction;

	@Override
	@OneToOne(targetEntity = BusinessObjectGroupMemberImpl.class, cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "CHANGE_TRANSACTION_UID")
	@ForeignKey
	public ChangeTransaction getChangeTransaction() {
		return changeTransaction;
	}

	@Override
	@Basic
	@Column(name = "METADATA_KEY", nullable = false)
	public String getMetadataKey() {
		return metadataKey;
	}

	@Override
	@Basic
	@Column(name = "METADATA_VALUE", nullable = false)
	public String getMetadataValue() {
		return metadataValue;
	}

	@Override
	public void setChangeTransaction(final ChangeTransaction changeTransaction) {
		this.changeTransaction = changeTransaction;
	}

	@Override
	public void setMetadataKey(final String metadataKey) {
		this.metadataKey = metadataKey;
	}

	@Override
	public void setMetadataValue(final String metadataValue) {
		this.metadataValue = metadataValue;

	}

	/**
	 * Gets the UIPK of this entity.
	 * 
	 * @return a long representing the UIDPK
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	/**
	 * Sets the UIDPK.
	 * 
	 * @param uidPk the UIDPK to set
	 */
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder("change transaction metadata:").append(getMetadataKey()).append(getMetadataValue()).toString();
	}

}
