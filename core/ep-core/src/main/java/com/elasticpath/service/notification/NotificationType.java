package com.elasticpath.service.notification; 

import java.util.ArrayList;
import java.util.List;

import com.elasticpath.commons.util.extenum.AbstractExtensibleEnum;

/**
 * Non-final extensible notification type enum.
 */
@SuppressWarnings("PMD.UseSingleton")
public class NotificationType extends AbstractExtensibleEnum<NotificationType> {

	/** Serial version uid. */
	private static final long serialVersionUID = 5000000001L;
	
	/** Ordinal constant for NEW_ORDER. */
 	public static final int NEW_ORDER_ORDINAL = 1;

 	/** New order notification type. */
 	public static final NotificationType NEW_ORDER_NOTIFICATION_TYPE = new NotificationType(NEW_ORDER_ORDINAL, "NewOrder");

 	/** Ordinal constant for ORDER_SHIPPED. */
 	public static final int ORDER_SHIPPED_ORDINAL = 2;

  	/** Order shipped notification type. */
 	public static final NotificationType ORDER_SHIPPED_NOTIFICATION_TYPE = new NotificationType(ORDER_SHIPPED_ORDINAL, "OrderShipped");	
	  		
	/**
	 * NotificationType constructor.
	 * 
	 * @param ordinal the ordinal value corresponding to the type
	 * @param name is the name of the notification type
	 */
	protected NotificationType(final int ordinal, final String name) {
		super(ordinal, name, NotificationType.class);		
	}

	/**
	 * Get enum list.	 
	 * @return list of enum types
	 */
	public static List<NotificationType> getEnumList() {
		return new ArrayList<NotificationType>(values(NotificationType.class));
	}

	@Override
	protected Class<NotificationType> getEnumType() {
		return NotificationType.class;
	}
	
	/**
	 * Get the enum value corresponding with the given name.
	 *
	 * @param name the name
	 * @return the NotificationType
	 */
	public static NotificationType valueOf(final String name) {
		return valueOf(name, NotificationType.class);
	}
}