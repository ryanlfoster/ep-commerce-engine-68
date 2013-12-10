package com.elasticpath.domain.changeset.impl;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.ObjectUtils;
import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.domain.changeset.ChangeSetUser;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * ChangeSetUserImpl provides a mapping. 
 */ 
@Entity
@Table(name = ChangeSetUserImpl.TABLE_NAME)
@DataCache(enabled = false)
public class ChangeSetUserImpl extends AbstractPersistableImpl implements ChangeSetUser {
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	
	/** Database Table. */
	protected static final String TABLE_NAME = "TCHANGESETUSER";
	
	private long uidPk;
	
	private String userGuid;
	
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

	@Override
	@Basic
	@Column(name = "USER_GUID", nullable = false)
	public String getUserGuid() {
		return userGuid;
	}
	
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof ChangeSetUserImpl)) {
			return false;
		}
		
		ChangeSetUserImpl user = (ChangeSetUserImpl) other;
		return ObjectUtils.equals(userGuid, user.userGuid);
	}

	@Override
	public int hashCode() {
		return ObjectUtils.hashCode(userGuid);
	}

	@Override
	public void setUserGuid(final String userGuid) {
		this.userGuid = userGuid;
	}
}
