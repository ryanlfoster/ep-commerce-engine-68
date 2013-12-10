package com.elasticpath.service.notification;

import java.util.Properties;
/**
 * Additional data for a notification.
 */
public class NotificationData extends Properties {

	private static final long serialVersionUID = 1L;

	/** orderNumber. **/
	public static final String ORDER_NUMBER = "orderNumber";
	/** shipmentNumber. **/
	public static final String SHIPMENT_NUMBER = "shipmentNumber";
	/** sendEmail. **/
	public static final String SEND_EMAIL = "sendEmail";

	/**
	 * Short hand constructor.
	 * @param orderNumber order number
	 * @param shipmentNumber shipment number
	 * @param sendEmail true|false if a email should be sent
	 */
	public NotificationData(final String orderNumber, final String shipmentNumber, final boolean sendEmail) {
		super();
		if (orderNumber != null) {
			put(ORDER_NUMBER, orderNumber);
		}
		if (shipmentNumber != null) {
			put(SHIPMENT_NUMBER, shipmentNumber);
		}
		put(SEND_EMAIL, sendEmail);
	}

}
