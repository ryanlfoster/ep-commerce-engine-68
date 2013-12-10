package com.elasticpath.service.misc;

import java.util.Date;

import com.elasticpath.service.EpPersistenceService;

/**
 * Provides time service. This service provides a way to retrieve a timestamp from a unique source. This is rather critical in clustered environment
 * because different time varies on different application servers.
 */
public interface TimeService extends EpPersistenceService {
	/**
	 * Returns the current time.
	 * 
	 * @return the current time.
	 */
	Date getCurrentTime();

}
