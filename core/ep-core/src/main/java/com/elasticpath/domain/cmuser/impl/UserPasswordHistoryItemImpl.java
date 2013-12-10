package com.elasticpath.domain.cmuser.impl;

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

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.cmuser.UserPasswordHistoryItem;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * An implementation of <code>UserPasswordHistoryItem</code> for OpenJPA database persistence.
 */
@Entity
@Table(name = UserPasswordHistoryItemImpl.TABLE_NAME)
@DataCache(enabled = false)
public class UserPasswordHistoryItemImpl extends AbstractPersistableImpl implements UserPasswordHistoryItem {

	private static final long serialVersionUID = 532237389461585637L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TPASSWORDHISTORY";

	private long uidPk;

	private Date expirationDate;

	private String oldPassword;

	@Override
	@Basic
	@Column(name = "EXPIRATION_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getExpirationDate() {
		return expirationDate;
	}

	@Override
	public void setExpirationDate(final Date date) {
		this.expirationDate = date;
	}
	
	@Override
	@Basic
	@Column(name = "OLD_PASSWORD", nullable = false)
	public String getOldPassword() {
		return oldPassword;
	}

	@Override
	public void setOldPassword(final String password) {
		this.oldPassword = password;
	}
	
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof UserPasswordHistoryItemImpl)) {
			return false;
		}
		
		UserPasswordHistoryItemImpl old = (UserPasswordHistoryItemImpl) other;
		return ObjectUtils.equals(oldPassword, old.oldPassword);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(oldPassword);
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
