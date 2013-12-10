/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order.impl;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.openjpa.persistence.DataCache;

import com.elasticpath.commons.constants.GlobalConstants;
import com.elasticpath.domain.DatabaseLastModifiedDate;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.cmuser.impl.CmUserImpl;
import com.elasticpath.domain.event.EventOriginatorType;
import com.elasticpath.domain.order.OrderEvent;
import com.elasticpath.persistence.api.AbstractPersistableImpl;

/**
 * Represents order events added to an order.
 */
@Entity
@Table(name = OrderEventImpl.TABLE_NAME)
@DataCache(enabled = false)
public class OrderEventImpl extends AbstractPersistableImpl implements OrderEvent, DatabaseLastModifiedDate {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TORDERAUDIT";

	private Date createdDate;

	private CmUser createdBy;

	private String note;

	private long uidPk;

	private EventOriginatorType originatorType;

	private String title;
	
	private Date lastModifiedDate;

	/**
	 * Allocation size for JPA_GENERATED_KEYS id.
	 */
	private static final int ALLOCATION_SIZE = 1000;
	
	/**
	 * Get the event originator type.
	 * 
	 * @return the originatorType
	 */
	@Basic
	@Enumerated(EnumType.STRING)
	@Column(name = "ORIGINATOR_TYPE")
	public EventOriginatorType getOriginatorType() {
		return originatorType;
	}

	/**
	 * Set the event originator type.
	 * 
	 * @param originatorType the originatorType to set
	 */
	public void setOriginatorType(final EventOriginatorType originatorType) {
		this.originatorType = originatorType;
	}

	/**
	 * Get the event title.
	 * 
	 * @return the title
	 */
	@Basic
	@Column(name = "TITLE")
	public String getTitle() {
		return title;
	}

	/**
	 * Set the event title.
	 * 
	 * @param title the title to set
	 */
	public void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * Get the date that this order was created on.
	 * 
	 * @return the created date
	 */
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	public Date getCreatedDate() {
		return this.createdDate;
	}

	/**
	 * Set the date that the order is created.
	 * 
	 * @param createdDate the start date
	 */
	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * Get the CM user who created this order note.
	 * 
	 * @return the CM user
	 */
	@ManyToOne(targetEntity = CmUserImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "CREATED_BY")
	public CmUser getCreatedBy() {
		return this.createdBy;
	}

	/**
	 * Set the CM User who created this order note.
	 * 
	 * @param createdBy the CM user
	 */
	public void setCreatedBy(final CmUser createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * Get the note recorded against the order.
	 * 
	 * @return the note
	 */
	@Lob
	@Column(name = "DETAIL", length = GlobalConstants.LONG_TEXT_MAX_LENGTH)
	public String getNote() {
		return this.note;
	}

	/**
	 * Set the note against the order.
	 * 
	 * @param note the note against the order
	 */
	public void setNote(final String note) {
		this.note = note;
	}
	
	@Override
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", nullable = false)
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * Set the date that this was last modified on.
	 * 
	 * @param lastModifiedDate the date that this was last modified
	 */
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID",
			valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME,  allocationSize = ALLOCATION_SIZE)
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
