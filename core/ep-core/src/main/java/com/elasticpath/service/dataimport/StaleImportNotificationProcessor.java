/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.service.dataimport;

import com.elasticpath.domain.dataimport.ImportNotification;

/**
 * A processor that should handle stale import notifications.
 */
public interface StaleImportNotificationProcessor {

	/**
	 * Processes an import notification.
	 * 
	 * @param importNotification the import notification
	 */
	void process(ImportNotification importNotification);

}
