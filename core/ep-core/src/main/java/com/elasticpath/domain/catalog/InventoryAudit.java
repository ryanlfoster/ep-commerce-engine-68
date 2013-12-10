/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.catalog;

import java.io.Serializable;
import java.util.Date;

import com.elasticpath.domain.order.Order;

/**
 * Represents logging information about changes to <code>Inventory</code>.
 */
public interface InventoryAudit extends Serializable {

	/** Inventory event come up from the CMUser. */
	String EVENT_ORIGINATOR_CMUSER = "CMUser";

	/** Inventory event come up from the StoreFront. */
	String EVENT_ORIGINATOR_SF = "StoreFront";

	/** Inventory event come up from the WebService. */
	String EVENT_ORIGINATOR_WS = "WebService";

	/** Inventory event come up from the quartz job. */
	String EVENT_ORIGINATOR_QUARTZ = "Quartz";

	/**
	 * @return the eventOriginator
	 */
	String getEventOriginator();

	/**
	 * @param eventOriginator the eventOriginator to set
	 */
	void setEventOriginator(final String eventOriginator);

	/**
	 * @return the comment
	 */
	String getComment();

	/**
	 * @param comment the comment to set
	 */
	void setComment(final String comment);

	/**
	 * @return the quantity
	 */
	int getQuantity();

	/**
	 * @param quantity the quantity to set
	 */
	void setQuantity(final int quantity);

	/**
	 * @return the timestamp
	 */
	Date getLogDate();

	/**
	 * @param logDate the timestamp to set
	 */
	void setLogDate(final Date logDate);

	/**
	 * Get the event type. For legacy data, return UNKNOWN.
	 * 
	 * @return the event type
	 */
	InventoryEventType getEventType();

	/**
	 * Set the event type.
	 * 
	 * @param eventType the eventType to set
	 */
	void setEventType(final InventoryEventType eventType);

	/**
	 * Get the orderUid if the inventory event is related to an order.
	 * 
	 * @return the orderUid
	 */
	Order getOrder();

	/**
	 * Set the order which the inventory event related to.
	 * 
	 * @param order the order to set
	 */
	void setOrder(final Order order);

	/**
	 * @return the adjust quantity on hand reason
	 */
	String getReason();

	/**
	 * @param reason the adjust quantity on hand reason to set
	 */
	void setReason(final String reason);

}
