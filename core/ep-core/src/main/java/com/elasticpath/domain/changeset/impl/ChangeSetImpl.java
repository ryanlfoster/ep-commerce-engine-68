/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.domain.changeset.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.jdbc.ElementForeignKey;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;

import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.changeset.ChangeSetMember;
import com.elasticpath.domain.changeset.ChangeSetMutator;
import com.elasticpath.domain.changeset.ChangeSetStateCode;
import com.elasticpath.domain.changeset.ChangeSetUser;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * Change Set is a holder of references to objects of the system that
 * are being changed.
 */
@Entity
@Table(name = ChangeSetImpl.TABLE_NAME)
@DataCache(enabled = false)
public class ChangeSetImpl extends AbstractEntityImpl implements ChangeSet, ChangeSetMutator {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/** Database Table. */
	protected static final String TABLE_NAME = "TCHANGESET";

	private String description;
	private String name;
	private long uidPk;
	private Date createdDate;
	private String createdByUserGuid;

	private Collection<ChangeSetMember> memberObjects = new HashSet<ChangeSetMember>(0);

	private String objectGroupId;

	private Set<ChangeSetUser> assignedUsers = new HashSet<ChangeSetUser>(0);

	private String stateCodeName;

	@Override
	@Basic
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	@Override
	@Transient
	public Collection<BusinessObjectDescriptor> getMemberObjects() {
		Collection<BusinessObjectDescriptor> descriptors = new HashSet<BusinessObjectDescriptor>();
		for (ChangeSetMember changeSetMember : memberObjects) {
			descriptors.add(changeSetMember.getBusinessObjectDescriptor());
		}
		return descriptors;
	}

	@Override
	@Basic
	@Column(name = "NAME", nullable = false)
	public String getName() {
		return name;
	}

	@Override
	@Transient
	public String getGuid() {
		return getObjectGroupId();
	}

	@Override
	public void setGuid(final String guid) {
		throw new UnsupportedOperationException("Setting the GUID is not supported");
	}

	@Override
	public void initialize() {
		// don't call super or else setGuid will be called
	}

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
	public boolean equals(final Object obj) {
		if (!(obj instanceof ChangeSetImpl)) {
			return false;
		}
		return super.equals(obj);
	}

    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public void setMemberObjects(final Collection<ChangeSetMember> memberObjects) {
		this.memberObjects = memberObjects;
	}

	@Override
	@Basic
	@Column(name = "OBJECT_GROUP_ID", nullable = false)
	public String getObjectGroupId() {
		return objectGroupId;
	}

	@Override
	public void setObjectGroupId(final String objectGroupId) {
		this.objectGroupId = objectGroupId;
	}

	@Override
	public String toString() {
		return getObjectGroupId() + "=" + getMemberObjects();
	}
	@Override
	@Transient
	public Collection<ChangeSetMember> getChangeSetMembers() {
		return Collections.unmodifiableCollection(memberObjects);
	}

	@Override
	@Basic
	@Column(name = "CREATED_BY_USER_GUID", nullable = false)
	public String getCreatedByUserGuid() {
		return createdByUserGuid;
	}

	@Override
	public void setCreatedByUserGuid(final String createdByUserGuid) {
		this.createdByUserGuid = createdByUserGuid;
	}

	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	public Date getCreatedDate() {
		return this.createdDate;
	}

	@Override
	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	@OneToMany(targetEntity = ChangeSetUserImpl.class, fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@ElementJoinColumn(name = "CHANGESET_UID", nullable = false)
	@ElementForeignKey
	@ElementDependent
	protected Set<ChangeSetUser> getAssignedUsers() {
		return assignedUsers;
	}

	protected void setAssignedUsers(final Set<ChangeSetUser> assignedUsers) {
		this.assignedUsers = assignedUsers;
	}

	@Override
	public void addAssignedUser(final String userGuid) {

		if (userGuid == null) {
			throw new IllegalArgumentException("Cannot add a null user");
		}

		final ChangeSetUser changeSetUser = new ChangeSetUserImpl();
		changeSetUser.setUserGuid(userGuid);

		getAssignedUsers().add(changeSetUser);
	}

	@Override
	public void removeAssignedUser(final String userGuid) {

		if (userGuid == null) {
			throw new IllegalArgumentException("Cannot add a null user");
		}
		for (Iterator<ChangeSetUser> iterator = this.getAssignedUsers().iterator(); iterator.hasNext();) {
			ChangeSetUser user = iterator.next();
			if (user.getUserGuid().equals(userGuid)) {
				iterator.remove();
			}
		}
	}

	@Override
	@Transient
	public Collection<String> getAssignedUserGuids() {

		final Collection<String> guids = new HashSet<String>(0);

		for (ChangeSetUser  changeSetUser : this.getAssignedUsers()) {
			guids.add(changeSetUser.getUserGuid());
		}
		return guids;
	}

	@Basic
	@Column(name = "STATE_CODE", nullable = false, length = ChangeSetStateCode.MAX_LENGTH)
	protected String getStateCodeName() {
		return stateCodeName;
	}

	protected void setStateCodeName(final String stateCodeName) {
		this.stateCodeName = stateCodeName;
	}

	@Override
	@Transient
	public ChangeSetStateCode getStateCode() {
		return ChangeSetStateCode.getState(getStateCodeName());
	}

	@Override
	public void setStateCode(final ChangeSetStateCode changeSetStateCode) {
		setStateCodeName(changeSetStateCode.getName());
	}

}
