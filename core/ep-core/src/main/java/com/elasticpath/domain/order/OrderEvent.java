/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order;

import java.util.Date;

import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.event.EventOriginatorType;
import com.elasticpath.persistence.api.Persistable;

/**
 * <code>OrderEvent</code> represents a event made on an order by a CSR/System/Customer.
 */
public interface OrderEvent extends Persistable {

	/**
	 * Get the event originator type. 
	 * @return the originatorType
	 */
	EventOriginatorType getOriginatorType();

	/**
	 * Set the event originator type.
	 * @param originatorType the originatorType to set
	 */
	void setOriginatorType(final EventOriginatorType originatorType);

	/**
	 * Get the event title.
	 * @return the title
	 */
	String getTitle();

	/**
	 * Set the event title.
	 * @param title the title to set
	 */
	void setTitle(final String title);

	/**
	 * Get the date that this order was created on.
	 * 
	 * @return the created date
	 * @domainmodel.property
	 */
	Date getCreatedDate();

	/**
	 * Set the date that the order is created.
	 * 
	 * @param createdDate the start date
	 */
	void setCreatedDate(Date createdDate);

	/**
	 * Get the date that this was last modified on.
	 * 
	 * @return the last modified date
	 */
	Date getLastModifiedDate();
	
	/**
	 * Get the CM user who created this order note.
	 * 
	 * @return the CM user
	 */
	CmUser getCreatedBy();

	/**
	 * Set the CM User who created this order note.
	 * 
	 * @param createdBy the CM user
	 */
	void setCreatedBy(CmUser createdBy);

	/**
	 * Get the note recorded against the order.
	 * 
	 * @return the note
	 * @domainmodel.property
	 */
	String getNote();

	/**
	 * Set the note against the order.
	 * 
	 * @param note the note against the order
	 */
	void setNote(String note);

}
